package com.feellog.backend.domain.report.service;

import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseEmotion;
import com.feellog.backend.domain.expense.entity.ExpenseSituationTag;
import com.feellog.backend.domain.income.entity.Income;
import com.feellog.backend.domain.report.dto.response.*;
import com.feellog.backend.domain.report.repository.IncomeReportRepository;
import com.feellog.backend.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final IncomeReportRepository incomeReportRepository;

    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(Long userId, int year, int month) {

        // 기간 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 전월 기간 계산
        YearMonth prevYearMonth = yearMonth.minusMonths(1);
        LocalDate prevStartDate = prevYearMonth.atDay(1);
        LocalDate prevEndDate = prevYearMonth.atEndOfMonth();

        // 당월/전월 지출 조회
        List<Expense> currentExpenses = reportRepository.findExpensesByUserAndPeriod(userId, startDate, endDate);
        List<Expense> prevExpenses = reportRepository.findExpensesByUserAndPeriod(userId, prevStartDate, prevEndDate);

        // 당월 수입 조회
        List<Income> currentIncomes = incomeReportRepository.findIncomesByUserAndPeriod(userId, startDate, endDate);

        // 수입 합산
        long totalIncome = currentIncomes.stream()
                .mapToLong(i -> i.getAmount().longValue())
                .sum();

        // 지출 합산
        long totalExpense = currentExpenses.stream()
                .mapToLong(e -> e.getAmount().longValue())
                .sum();

        long prevTotalExpense = prevExpenses.stream()
                .mapToLong(e -> e.getAmount().longValue())
                .sum();

        // 전월 대비 증감
        Long diffAmount = null;
        Double diffRate = null;
        if (!prevExpenses.isEmpty() && prevTotalExpense > 0) {
            diffAmount = totalExpense - prevTotalExpense;
            diffRate = BigDecimal.valueOf((double) diffAmount / prevTotalExpense * 100)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // 카테고리별 집계
        List<CategoryStatDto> categoryList = buildCategoryStats(currentExpenses, totalExpense);

        // 감정별 집계
        List<EmotionStatDto> emotionList = buildEmotionStats(currentExpenses);

        // 상황별 집계
        List<SituationStatDto> situationList = buildSituationStats(currentExpenses);

        // 문구 생성
        CommentsDto comments = buildComments(categoryList, emotionList, situationList, prevExpenses);

        return MonthlyReportResponse.builder()
                .period(MonthlyReportResponse.PeriodDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build())
                .summary(MonthlyReportResponse.SummaryDto.builder()
                        .totalIncome(totalIncome)
                        .totalExpense(totalExpense)
                        .diffAmount(diffAmount)
                        .diffRate(diffRate)
                        .build())
                .comments(comments)
                .categories(MonthlyReportResponse.CategoriesDto.builder()
                        .list(categoryList)
                        .build())
                .emotions(MonthlyReportResponse.EmotionsDto.builder()
                        .list(emotionList)
                        .build())
                .situations(MonthlyReportResponse.SituationsDto.builder()
                        .list(situationList)
                        .build())
                .build();
    }

    private List<CategoryStatDto> buildCategoryStats(List<Expense> expenses, long totalExpense) {
        if (totalExpense == 0) return Collections.emptyList();

        // 카테고리별 합산
        Map<Long, Long> categoryAmountMap = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getId(),
                        Collectors.summingLong(e -> e.getAmount().longValue())
                ));

        List<Long> categoryIds = new ArrayList<>(categoryAmountMap.keySet());

        // 비율 계산
        List<double[]> rawRates = categoryIds.stream()
                .map(id -> {
                    double rate = (double) categoryAmountMap.get(id) / totalExpense * 100;
                    return new double[]{rate, Math.floor(rate)};
                })
                .collect(Collectors.toList());

        // 합계 100% 보정
        int totalDisplay = rawRates.stream().mapToInt(r -> (int) r[1]).sum();
        int remainder = 100 - totalDisplay;

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < rawRates.size(); i++) indices.add(i);
        indices.sort((a, b) -> Double.compare(
                rawRates.get(b)[0] - rawRates.get(b)[1],
                rawRates.get(a)[0] - rawRates.get(a)[1]
        ));
        for (int i = 0; i < remainder; i++) {
            rawRates.get(indices.get(i))[1]++;
        }

        // 정렬 및 순위 부여
        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(categoryAmountMap.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<CategoryStatDto> result = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && sorted.get(i).getValue() < sorted.get(i - 1).getValue()) rank = i + 1;
            Long categoryId = sorted.get(i).getKey();
            int idx = categoryIds.indexOf(categoryId);

            Expense sample = expenses.stream()
                    .filter(e -> e.getCategory().getId().equals(categoryId))
                    .findFirst().orElseThrow();

            result.add(CategoryStatDto.builder()
                    .categoryId(categoryId)
                    .categoryName(sample.getCategory().getName())
                    .categoryGroupName(sample.getCategory().getCategoryGroup().getName())
                    .totalAmount(sorted.get(i).getValue())
                    .shareRate(BigDecimal.valueOf(rawRates.get(idx)[0])
                            .setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .shareRateDisplay((int) rawRates.get(idx)[1])
                    .rank(rank)
                    .build());
        }
        return result.size() > 5 ? result.subList(0, 5) : result;
    }

    private List<EmotionStatDto> buildEmotionStats(List<Expense> expenses) {
        Map<Long, Long> emotionAmountMap = new HashMap<>();
        Map<Long, Integer> emotionCountMap = new HashMap<>();  // 건수 추가
        Map<Long, String[]> emotionMetaMap = new HashMap<>();

        for (Expense expense : expenses) {
            long amount = expense.getAmount().longValue();
            for (ExpenseEmotion ee : expense.getExpenseEmotions()) {
                Long emotionId = ee.getEmotion().getId();
                emotionAmountMap.merge(emotionId, amount, Long::sum);
                emotionCountMap.merge(emotionId, 1, Integer::sum);
                emotionMetaMap.putIfAbsent(emotionId, new String[]{
                        ee.getEmotion().getName(),
                        ee.getEmotion().getEmotionGroup().getName()
                });
            }
        }

        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(emotionAmountMap.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<EmotionStatDto> result = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && sorted.get(i).getValue() < sorted.get(i - 1).getValue()) rank = i + 1;
            Long emotionId = sorted.get(i).getKey();
            String[] meta = emotionMetaMap.get(emotionId);

            result.add(EmotionStatDto.builder()
                    .emotionId(emotionId)
                    .emotionName(meta[0])
                    .emotionGroupName(meta[1])
                    .linkedAmount(sorted.get(i).getValue())
                    .emotionCount(emotionCountMap.get(emotionId))
                    .rank(rank)
                    .build());
        }
        return result.size() > 5 ? result.subList(0, 5) : result;
    }

    private List<SituationStatDto> buildSituationStats(List<Expense> expenses) {
        Map<Long, Integer> situationCountMap = new HashMap<>();
        Map<Long, String> situationNameMap = new HashMap<>();

        for (Expense expense : expenses) {
            for (ExpenseSituationTag est : expense.getExpenseSituationTags()) {
                Long tagId = est.getSituationTag().getId();
                situationCountMap.merge(tagId, 1, Integer::sum);
                situationNameMap.putIfAbsent(tagId, est.getSituationTag().getName());
            }
        }

        List<Map.Entry<Long, Integer>> sorted = new ArrayList<>(situationCountMap.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        List<SituationStatDto> result = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && sorted.get(i).getValue() < sorted.get(i - 1).getValue()) rank = i + 1;
            Long tagId = sorted.get(i).getKey();

            result.add(SituationStatDto.builder()
                    .situationTagId(tagId)
                    .situationName(situationNameMap.get(tagId))
                    .occurrenceCount(sorted.get(i).getValue())
                    .rank(rank)
                    .build());
        }
        return result.size() > 5 ? result.subList(0, 5) : result;
    }

    private CommentsDto buildComments(
            List<CategoryStatDto> categoryList,
            List<EmotionStatDto> emotionList,
            List<SituationStatDto> situationList,
            List<Expense> prevExpenses
    ) {
        return CommentsDto.builder()
                .categoryChange(buildCategoryChangeComment(categoryList, prevExpenses))
                .emotionTrend(buildEmotionTrendComment(emotionList))
                .situationTrend(buildSituationTrendComment(situationList))
                .build();
    }

    private CommentDto buildCategoryChangeComment(
            List<CategoryStatDto> categoryList,
            List<Expense> prevExpenses
    ) {
        if (prevExpenses.isEmpty() || categoryList.isEmpty()) {
            return CommentDto.builder()
                    .type("NO_PREV_DATA")
                    .targetName(null)
                    .message("다음 달부터 지출 변화 추이를 보여드릴게요")
                    .build();
        }

        Map<Long, Long> prevCategoryMap = prevExpenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getId(),
                        Collectors.summingLong(e -> e.getAmount().longValue())
                ));

        CategoryStatDto maxChanged = null;
        long maxDiff = -1;

        for (CategoryStatDto cat : categoryList) {
            long prev = prevCategoryMap.getOrDefault(cat.categoryId(), 0L);
            long diff = Math.abs(cat.totalAmount() - prev);
            if (diff > maxDiff) {
                maxDiff = diff;
                maxChanged = cat;
            }
        }

        long finalMaxDiff = maxDiff;
        long tieCount = categoryList.stream()
                .filter(cat -> {
                    long prev = prevCategoryMap.getOrDefault(cat.categoryId(), 0L);
                    return Math.abs(cat.totalAmount() - prev) == finalMaxDiff;
                }).count();

        if (tieCount > 1) {
            return CommentDto.builder()
                    .type("TIE")
                    .targetName(null)
                    .message("이번 달 소비 변화는 한 곳보다 여러 카테고리에서 두드러졌어요")
                    .build();
        }

        return CommentDto.builder()
                .type("NORMAL")
                .targetName(maxChanged.categoryName())
                .message("이번 달 가장 크게 변한 지출은 " + maxChanged.categoryName() + "예요")
                .build();
    }

    private CommentDto buildEmotionTrendComment(List<EmotionStatDto> emotionList) {
        if (emotionList.isEmpty()) {
            return CommentDto.builder()
                    .type("NO_DATA")
                    .targetName(null)
                    .message("감정을 기록하면 이번 달 소비와 연결된 마음을 알려드릴게요")
                    .build();
        }

        // 건수 기준으로 최대값 찾기
        int maxCount = emotionList.stream()
                .mapToInt(EmotionStatDto::emotionCount)
                .max()
                .orElse(0);

        // 모두 1건씩이거나 동률인 경우
        long tieCount = emotionList.stream()
                .filter(e -> e.emotionCount() == maxCount)
                .count();

        if (tieCount > 1 || maxCount == 1) {
            return CommentDto.builder()
                    .type("EQUAL_OR_SINGLE")
                    .targetName(null)
                    .message("이번 달엔 여러 마음이 번갈아 나타났어요")
                    .build();
        }

        // 건수 기준 1위 감정
        EmotionStatDto topEmotion = emotionList.stream()
                .filter(e -> e.emotionCount() == maxCount)
                .findFirst()
                .orElseThrow();

        String emotionName = topEmotion.emotionName();
        return CommentDto.builder()
                .type("NORMAL")
                .targetName(emotionName)
                .message(emotionName + "이 이번 달 소비에 자주 연결됐어요")
                .build();
    }

    private CommentDto buildSituationTrendComment(List<SituationStatDto> situationList) {
        if (situationList.isEmpty()) {
            return CommentDto.builder()
                    .type("NO_DATA")
                    .targetName(null)
                    .message("상황을 기록하면 어떤 이유의 소비가 많았는지 보여드릴게요")
                    .build();
        }

        int maxCount = situationList.get(0).occurrenceCount();
        long tieCount = situationList.stream()
                .filter(s -> s.occurrenceCount() == maxCount)
                .count();

        if (tieCount > 1) {
            return CommentDto.builder()
                    .type("TIE")
                    .targetName(null)
                    .message("이번 달 지출에는 여러 상황이 존재했어요")
                    .build();
        }

        String situationName = situationList.get(0).situationName();
        return CommentDto.builder()
                .type("NORMAL")
                .targetName(situationName)
                .message(situationName + " 관련 소비가 가장 많았어요")
                .build();
    }
}
