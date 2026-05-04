package com.feellog.backend.domain.report.service;

import com.feellog.backend.domain.category.entity.Category;
import com.feellog.backend.domain.category.repository.CategoryRepository;
import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.emotion.repository.EmotionRepository;
import com.feellog.backend.domain.expense.entity.Expense;
import com.feellog.backend.domain.expense.entity.ExpenseEmotion;
import com.feellog.backend.domain.expense.entity.ExpenseSituationTag;
import com.feellog.backend.domain.income.entity.Income;
import com.feellog.backend.domain.report.dto.CategoryAmountDto;

import com.feellog.backend.domain.report.dto.projection.CategoryExpenseSummary;
import com.feellog.backend.domain.report.dto.projection.EmotionSummary;
import com.feellog.backend.domain.report.dto.response.CategoryDetailResponse;
import com.feellog.backend.domain.report.dto.response.CategoryStatDto;
import com.feellog.backend.domain.report.dto.response.CommentDto;
import com.feellog.backend.domain.report.dto.response.CommentsDto;
import com.feellog.backend.domain.report.dto.response.DailyReportResponse;
import com.feellog.backend.domain.report.dto.response.EmotionDetailResponse;
import com.feellog.backend.domain.report.dto.response.EmotionStatDto;
import com.feellog.backend.domain.report.dto.response.MonthlyExpenseDetailResponse;
import com.feellog.backend.domain.report.dto.response.MonthlyReportResponse;
import com.feellog.backend.domain.report.dto.response.SituationStatDto;
import com.feellog.backend.domain.report.dto.response.WeeklyReportResponse;
import com.feellog.backend.domain.report.repository.IncomeReportRepository;
import com.feellog.backend.domain.report.repository.ReportRepository;
import com.feellog.backend.global.exception.BusinessException;
import com.feellog.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final IncomeReportRepository incomeReportRepository;
    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;

    public WeeklyReportResponse getWeeklyReport(Long userId) {
        // 기간 계산 (일요일 ~ 토요일)
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        // 데이터 조회
        List<Expense> expenses = reportRepository.findExpensesOnlyByUserAndPeriod(userId, weekStart, weekEnd);

        // 날짜별 지출 합산
        Map<LocalDate, Long> dailyAmountMap = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getExpenseDate,
                        Collectors.summingLong(e -> e.getAmount().longValue())
                ));

        // 전체 날짜 배열 생성 (지출 없는 날 0원 처리)
        List<WeeklyReportResponse.DailyAmountDto> dailyAmounts = weekStart.datesUntil(weekEnd.plusDays(1))
                .map(date -> WeeklyReportResponse.DailyAmountDto.builder()
                        .date(date)
                        .expense(dailyAmountMap.getOrDefault(date, 0L))
                        .build())
                .toList();

        // 전체 합계 계산
        long totalExpense = dailyAmounts.stream()
                .mapToLong(WeeklyReportResponse.DailyAmountDto::expense)
                .sum();

        return WeeklyReportResponse.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .totalExpense(totalExpense)
                .dailyAmounts(dailyAmounts)
                .build();
    }

    public MonthlyReportResponse getMonthlyReport(Long userId, int year, int month) {

        // 기간 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 전월 기간 계산
        YearMonth prevYearMonth = yearMonth.minusMonths(1);
        LocalDate prevStartDate = prevYearMonth.atDay(1);
        LocalDate prevEndDate = prevYearMonth.atEndOfMonth();

        // 당월 지출 / 전월 카테고리 지출
        List<Expense> currentExpenses = reportRepository.findExpensesByUserAndPeriod(userId, startDate, endDate);
        List<CategoryAmountDto> prevCategoryAmounts = reportRepository.findCategoryAmountByUserAndPeriod(userId, prevStartDate, prevEndDate);

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

        // 카테고리별 집계
        List<CategoryStatDto> categoryList = buildCategoryStats(currentExpenses, totalExpense);

        // 감정별 집계
        List<EmotionStatDto> emotionList = buildEmotionStats(currentExpenses);

        // 상황별 집계
        List<SituationStatDto> situationList = buildSituationStats(currentExpenses);

        // 문구 생성
        CommentsDto comments = buildComments(categoryList, emotionList, situationList, prevCategoryAmounts);

        return MonthlyReportResponse.builder()
                .period(MonthlyReportResponse.PeriodDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build())
                .summary(MonthlyReportResponse.SummaryDto.builder()
                        .totalIncome(totalIncome)
                        .totalExpense(totalExpense)
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

        Map<Long, Expense> categorySampleMap = new HashMap<>();
        Map<Long, Long> categoryAmountMap = new HashMap<>();

        for (Expense e : expenses) {
            Long categoryId = e.getCategory().getId();
            categorySampleMap.putIfAbsent(categoryId, e);
            categoryAmountMap.merge(categoryId, e.getAmount().longValue(), Long::sum);
        }

        // 비율 계산
        Map<Long, double[]> rawRateMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry : categoryAmountMap.entrySet()) {
            double rate = (double) entry.getValue() / totalExpense * 100;
            rawRateMap.put(entry.getKey(), new double[]{rate, Math.floor(rate)});
        }

        // 합계 100% 보정
        int totalDisplay = rawRateMap.values().stream().mapToInt(r -> (int) r[1]).sum();
        int remainder = 100 - totalDisplay;

        List<Long> sortedByRemainder = rawRateMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(
                        b.getValue()[0] - b.getValue()[1],
                        a.getValue()[0] - a.getValue()[1]
                ))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (int i = 0; i < remainder; i++) {
            rawRateMap.get(sortedByRemainder.get(i))[1]++;
        }

        // 정렬 및 순위 부여
        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(categoryAmountMap.entrySet());
        sorted.sort((a, b) -> {
            int cmp = Long.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return Long.compare(a.getKey(), b.getKey()); // 동순위 시 categoryId 오름차순
        });

        List<CategoryStatDto> result = new ArrayList<>();
        int rank = 1;
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0 && sorted.get(i).getValue() < sorted.get(i - 1).getValue()) rank = i + 1;
            Long categoryId = sorted.get(i).getKey();
            double[] rates = rawRateMap.get(categoryId);
            Expense sample = categorySampleMap.get(categoryId);

            result.add(CategoryStatDto.builder()
                    .categoryId(categoryId)
                    .categoryName(sample.getCategory().getName())
                    .totalAmount(sorted.get(i).getValue())
                    .shareRate(BigDecimal.valueOf(rates[0])
                            .setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .shareRateDisplay((int) rates[1])
                    .rank(rank)
                    .build());
        }
        return result.size() > 5 ? result.subList(0, 5) : result;
    }

    private List<EmotionStatDto> buildEmotionStats(List<Expense> expenses) {
        Map<Long, Long> emotionAmountMap = new HashMap<>();
        Map<Long, Integer> emotionCountMap = new HashMap<>();
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
        sorted.sort((a, b) -> {
            int cmp = Long.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            return Long.compare(a.getKey(), b.getKey()); // 동순위 시 emotionId 오름차순
        });

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
        Map<Long, Long> situationAmountMap = new HashMap<>();  // 금액 집계용

        for (Expense expense : expenses) {
            long amount = expense.getAmount().longValue();
            for (ExpenseSituationTag est : expense.getExpenseSituationTags()) {
                Long tagId = est.getSituationTag().getId();
                situationCountMap.merge(tagId, 1, Integer::sum);
                situationNameMap.putIfAbsent(tagId, est.getSituationTag().getName());
                situationAmountMap.merge(tagId, amount, Long::sum);  // 금액 합산
            }
        }

        // 건수 내림차순 → 동순위면 금액 내림차순 → 그 다음 situationTagId 오름차순
        List<Map.Entry<Long, Integer>> sorted = new ArrayList<>(situationCountMap.entrySet());
        sorted.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp != 0) return cmp;
            int amountCmp = Long.compare(
                    situationAmountMap.get(b.getKey()),
                    situationAmountMap.get(a.getKey())
            );
            if (amountCmp != 0) return amountCmp;
            return Long.compare(a.getKey(), b.getKey()); // 동순위 시 situationTagId 오름차순
        });

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
            List<CategoryAmountDto> prevCategoryAmounts
    ) {
        return CommentsDto.builder()
                .categoryChange(buildCategoryChangeComment(categoryList, prevCategoryAmounts))
                .emotionTrend(buildEmotionTrendComment(emotionList))
                .situationTrend(buildSituationTrendComment(situationList))
                .build();
    }

    private CommentDto buildCategoryChangeComment(
            List<CategoryStatDto> categoryList,
            List<CategoryAmountDto> prevCategoryAmounts
    ) {
        if (prevCategoryAmounts.isEmpty() || categoryList.isEmpty()) {
            return CommentDto.builder()
                    .type("NO_PREV_DATA")
                    .targetName(null)
                    .message("다음 달부터 지출 변화 추이를 보여드릴게요")
                    .build();
        }

        Map<Long, Long> prevCategoryMap = prevCategoryAmounts.stream()
                .collect(Collectors.toMap(
                        CategoryAmountDto::categoryId,
                        dto -> dto.amount().longValue()
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

        // 증감률 계산
        long prev = prevCategoryMap.getOrDefault(maxChanged.categoryId(), 0L);
        long diffAmount = maxChanged.totalAmount() - prev;
        double diffRate = prev > 0
                ? BigDecimal.valueOf((double) diffAmount / prev * 100)
                .setScale(0, RoundingMode.HALF_UP)
                .doubleValue()
                : 100.0;

        String direction = diffAmount >= 0 ? "늘었어요" : "줄었어요";
        double absDiffRate = Math.abs(diffRate);

        return CommentDto.builder()
                .type("NORMAL")
                .targetName(maxChanged.categoryName())
                .message("지난달보다 " + maxChanged.categoryName() + " 지출이 "
                        + (int) absDiffRate + "% " + direction)
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

    public MonthlyExpenseDetailResponse getMonthlyExpenseDetail(Long userId, int year, int month, int page, int size, String sort) {

        Sort sortOption = switch (sort) {
            case "OLDEST" -> Sort.by(
                    Sort.Order.asc("expenseDate"),
                    Sort.Order.asc("expenseTime").nullsLast(),
                    Sort.Order.asc("createdAt")
            );
            case "AMOUNT_HIGH" -> Sort.by(
                    Sort.Order.desc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
            case "AMOUNT_LOW" -> Sort.by(
                    Sort.Order.asc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
            default -> Sort.by(
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
        };

        int validatedPage = Math.max(1, page);
        Pageable pageable = PageRequest.of(validatedPage - 1, size, sortOption);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Page<Expense> expensePage = reportRepository.findExpensesPageByUserAndPeriod(userId, startDate, endDate, pageable);

        long totalAmount = reportRepository.findTotalAmountByUserAndPeriod(userId, startDate, endDate).longValue();

        List<MonthlyExpenseDetailResponse.DailyLogDto> dailyLogs = null;
        List<MonthlyExpenseDetailResponse.ExpenseDto> expenses = null;

        if (sort.equals("AMOUNT_HIGH") || sort.equals("AMOUNT_LOW")) {
            expenses = expensePage.getContent().stream()
                    .map(this::mapToMonthlyExpenseDto)
                    .toList();
        } else {
            Map<LocalDate, List<MonthlyExpenseDetailResponse.ExpenseDto>> groupedByDate = expensePage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            Expense::getExpenseDate,
                            LinkedHashMap::new,
                            Collectors.mapping(this::mapToMonthlyExpenseDto, Collectors.toList())
                    ));

            dailyLogs = groupedByDate.entrySet().stream()
                    .map(entry -> new MonthlyExpenseDetailResponse.DailyLogDto(entry.getKey(), entry.getValue()))
                    .toList();
        }

        return MonthlyExpenseDetailResponse.builder()
                .period(new MonthlyExpenseDetailResponse.PeriodDto(startDate, endDate))
                .totalAmount(totalAmount)
                .totalElements((int) expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .currentPage(validatedPage)
                .dailyLogs(dailyLogs)
                .expenses(expenses)
                .build();
    }

    private MonthlyExpenseDetailResponse.ExpenseDto mapToMonthlyExpenseDto(Expense e) {
        return MonthlyExpenseDetailResponse.ExpenseDto.builder()
                .expenseId(e.getId())
                .date(e.getExpenseDate())
                .categoryName(e.getCategory().getName())
                .memo(e.getMemo())
                .amount(e.getAmount().longValue())
                .paymentMethod(e.getPaymentMethod().getName())
                .emotions(e.getExpenseEmotions().stream()
                        .sorted(Comparator.comparing(ee -> ee.getEmotion().getId()))
                        .map(ee -> new MonthlyExpenseDetailResponse.EmotionDto(
                                ee.getEmotion().getId(),
                                ee.getEmotion().getName()))
                        .toList())
                .situationTags(e.getExpenseSituationTags().stream()
                        .sorted(Comparator.comparing(est -> est.getSituationTag().getId()))
                        .map(est -> new MonthlyExpenseDetailResponse.SituationTagDto(
                                est.getSituationTag().getId(),
                                est.getSituationTag().getName()))
                        .toList())
                .build();
    }

    public CategoryDetailResponse getCategoryDetail(Long userId, Long categoryId, int year, int month, int page, int size, String sort) {

        // 카테고리 Id 검증
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Sort sortOption = switch (sort) {
            case "OLDEST" -> Sort.by(
                    Sort.Order.asc("expenseDate"),
                    Sort.Order.asc("expenseTime").nullsLast(),
                    Sort.Order.asc("createdAt")
            );
            case "AMOUNT_HIGH" -> Sort.by(
                    Sort.Order.desc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
            case "AMOUNT_LOW" -> Sort.by(
                    Sort.Order.asc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
            default -> Sort.by(
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
        };

        // page가 1보다 작으면 1로 고정
        int validatedPage = Math.max(1, page);
        Pageable pageable = PageRequest.of(validatedPage - 1, size, sortOption);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 지출 내역 조회
        Page<Expense> expensePage = reportRepository.findExpensesByCategoryAndPeriod(userId, categoryId, startDate, endDate, pageable);

        // 총 지출 합산
        long totalAmount = reportRepository.findTotalAmountByCategoryAndPeriod(userId, categoryId, startDate, endDate).longValue();

        // 데이터가 없을 경우
        List<CategoryDetailResponse.DailyLogDto> dailyLogs = null;
        List<CategoryDetailResponse.ExpenseDto> expenses = null;

        if (sort.equals("AMOUNT_HIGH") || sort.equals("AMOUNT_LOW")) {
            expenses = expensePage.getContent().stream()
                    .map(this::mapToExpenseDto)
                    .toList();
        } else {
            Map<LocalDate, List<CategoryDetailResponse.ExpenseDto>> groupedByDate = expensePage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            Expense::getExpenseDate,
                            LinkedHashMap::new,
                            Collectors.mapping(this::mapToExpenseDto, Collectors.toList())
                    ));

            dailyLogs = groupedByDate.entrySet().stream()
                    .map(entry -> new CategoryDetailResponse.DailyLogDto(entry.getKey(), entry.getValue()))
                    .toList();
        }

        return CategoryDetailResponse.builder()
                .category(CategoryDetailResponse.CategoryInfo.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build())
                .period(new CategoryDetailResponse.PeriodDto(startDate, endDate))
                .totalAmount(totalAmount)
                .totalElements((int) expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .currentPage(validatedPage)
                .dailyLogs(dailyLogs) // 내역이 없으면 빈 리스트 [] 전달
                .expenses(expenses)
                .build();
        }

    private CategoryDetailResponse.ExpenseDto mapToExpenseDto(Expense e) {
        return CategoryDetailResponse.ExpenseDto.builder()
                .expenseId(e.getId())
                .date(e.getExpenseDate())
                .memo(e.getMemo())
                .amount(e.getAmount().longValue())
                .paymentMethod(e.getPaymentMethod().getName())
                .emotions(e.getExpenseEmotions().stream()
                        .sorted(Comparator.comparing(ee -> ee.getEmotion().getId()))
                        .map(ee -> new CategoryDetailResponse.EmotionDto(
                                ee.getEmotion().getId(),
                                ee.getEmotion().getName()))
                        .toList())
                .situationTags(e.getExpenseSituationTags().stream()
                        .sorted(Comparator.comparing(est -> est.getSituationTag().getId()))
                        .map(est -> new CategoryDetailResponse.SituationTagDto(
                                est.getSituationTag().getId(),
                                est.getSituationTag().getName()))
                        .toList())
                .build();
    }

    public EmotionDetailResponse getEmotionDetail(Long userId, Long emotionId, int year, int month, int page, int size, String sort) {

        Emotion emotion = emotionRepository.findById(emotionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMOTION_NOT_FOUND));

        Sort sortOption = switch (sort) {
            case "OLDEST" -> Sort.by(
                    Sort.Order.asc("expenseDate"),
                    Sort.Order.asc("expenseTime").nullsLast(),
                    Sort.Order.asc("createdAt")
            );
            case "AMOUNT_HIGH" -> Sort.by(
                    Sort.Order.desc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
            case "AMOUNT_LOW" -> Sort.by(
                    Sort.Order.asc("amount"),
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
            default -> Sort.by(
                    Sort.Order.desc("expenseDate"),
                    Sort.Order.desc("expenseTime").nullsLast(),
                    Sort.Order.desc("createdAt")
            );
        };

        int validatedPage = Math.max(1, page);
        Pageable pageable = PageRequest.of(validatedPage - 1, size, sortOption);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Page<Expense> expensePage = reportRepository.findExpensesByEmotionAndPeriod(userId, emotionId, startDate, endDate, pageable);

        long totalAmount = reportRepository.findTotalAmountByEmotionAndPeriod(userId, emotionId, startDate, endDate).longValue();

        List<EmotionDetailResponse.DailyLogDto> dailyLogs = null;
        List<EmotionDetailResponse.ExpenseDto> expenses = null;

        if (sort.equals("AMOUNT_HIGH") || sort.equals("AMOUNT_LOW")) {
            expenses = expensePage.getContent().stream()
                    .map(this::mapToEmotionExpenseDto)
                    .toList();
        } else {
            Map<LocalDate, List<EmotionDetailResponse.ExpenseDto>> groupedByDate = expensePage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            Expense::getExpenseDate,
                            LinkedHashMap::new,
                            Collectors.mapping(this::mapToEmotionExpenseDto, Collectors.toList())
                    ));

            dailyLogs = groupedByDate.entrySet().stream()
                    .map(entry -> new EmotionDetailResponse.DailyLogDto(entry.getKey(), entry.getValue()))
                    .toList();
        }

        return EmotionDetailResponse.builder()
                .emotion(EmotionDetailResponse.EmotionInfo.builder()
                        .emotionId(emotion.getId())
                        .emotionName(emotion.getName())
                        .build())
                .period(new EmotionDetailResponse.PeriodDto(startDate, endDate))
                .totalAmount(totalAmount)
                .totalElements((int) expensePage.getTotalElements())
                .totalPages(expensePage.getTotalPages())
                .currentPage(validatedPage)
                .dailyLogs(dailyLogs)
                .expenses(expenses)
                .build();
    }

    private EmotionDetailResponse.ExpenseDto mapToEmotionExpenseDto(Expense e) {
        return EmotionDetailResponse.ExpenseDto.builder()
                .expenseId(e.getId())
                .date(e.getExpenseDate())
                .categoryName(e.getCategory().getName())
                .memo(e.getMemo())
                .amount(e.getAmount().longValue())
                .paymentMethod(e.getPaymentMethod().getName())
                .situationTags(e.getExpenseSituationTags().stream()
                        .sorted(Comparator.comparing(est -> est.getSituationTag().getId()))
                        .map(est -> new EmotionDetailResponse.SituationTagDto(
                                est.getSituationTag().getId(),
                                est.getSituationTag().getName()))
                        .toList())
                .build();
    }

    public DailyReportResponse getDailyReport(Long userId) {
        LocalDate today = LocalDate.now();

        BigDecimal totalAmount = reportRepository.findTotalExpenseByDate(userId, today);
        List<CategoryExpenseSummary> categories = reportRepository.findCategoryExpenseSummaryByDate(userId, today);
        List<EmotionSummary> emotions = reportRepository.findEmotionSummaryByDate(userId, today);

        String date = today.toString();
        String dayOfWeek = today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);
        long total = totalAmount.longValue();

        return DailyReportResponse.builder()
                .period(DailyReportResponse.Period.builder()
                        .date(date)
                        .dayOfWeek(dayOfWeek)
                        .build())
                .summary(DailyReportResponse.Summary.builder()
                        .totalExpenseAmount(total)
                        .build())
                .expenseGraph(buildExpenseGraph(categories, total))
                .emotions(buildEmotions(emotions))
                .build();
    }

    private DailyReportResponse.ExpenseGraph buildExpenseGraph(
            List<CategoryExpenseSummary> categories,
            long total
    ) {
        if (categories.isEmpty() || total == 0) return null;

        BigDecimal maxAmount = categories.get(0).getTotal();

        List<CategoryExpenseSummary> topCategories = categories.stream()
                .filter(c -> c.getTotal().compareTo(maxAmount) == 0)
                .toList();

        boolean allEqual = topCategories.size() == categories.size();
        String displayType = resolveDisplayType(topCategories.size(), allEqual);
        String mainMessage = resolveMainMessage(displayType, topCategories);

        BigDecimal topSum = topCategories.stream()
                .map(CategoryExpenseSummary::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int topRatio = topSum
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 0, RoundingMode.HALF_UP)
                .intValue();

        String subMessage = resolveSubMessage(displayType, topCategories, topRatio);
        int extraCount = topCategories.size() >= 3 ? topCategories.size() - 2 : 0;
        List<DailyReportResponse.ExpenseGraph.TopCategory> topCategoryList = topCategories.stream()
                .limit(2)
                .map(c -> DailyReportResponse.ExpenseGraph.TopCategory.builder()
                        .label(c.getName())
                        .amount(c.getTotal().longValue())
                        .build())
                .toList();

        Set<Long> topIds = topCategories.stream()
                .map(CategoryExpenseSummary::getCategoryId)
                .collect(Collectors.toSet());

        DailyReportResponse.ExpenseGraph.SecondCategory secondCategory = categories.stream()
                .filter(c -> !topIds.contains(c.getCategoryId()))
                .findFirst()
                .map(c -> DailyReportResponse.ExpenseGraph.SecondCategory.builder()
                        .label(c.getName())
                        .amount(c.getTotal().longValue())
                        .build())
                .orElse(null);

        return DailyReportResponse.ExpenseGraph.builder()
                .displayType(displayType)
                .mainMessage(mainMessage)
                .subMessage(subMessage)
                .topRatio(topRatio)
                .topCategories(topCategoryList)
                .secondCategory(secondCategory)
                .extraCount(extraCount)
                .build();
    }

    private String resolveDisplayType(int topCount, boolean allEqual) {
        if (allEqual) return "ALL_EQUAL";
        return switch (topCount) {
            case 1 -> "SINGLE";
            case 2 -> "DUAL";
            default -> "MULTIPLE";
        };
    }

    private String resolveMainMessage(String displayType, List<CategoryExpenseSummary> topCategories) {
        return switch (displayType) {
            case "SINGLE" -> "오늘은 %s에 가장 많이 지출했어요"
                    .formatted(topCategories.get(0).getName());
            case "DUAL" -> "오늘은 %s와 %s에 가장 많이 지출했어요"
                    .formatted(topCategories.get(0).getName(), topCategories.get(1).getName());
            case "MULTIPLE" -> "%s, %s 외 %d개 항목에 동일하게 지출했어요"
                    .formatted(
                            topCategories.get(0).getName(),
                            topCategories.get(1).getName(),
                            topCategories.size() - 2
                    );
            case "ALL_EQUAL" -> "오늘은 여러 항목에 동일한 금액을 지출했어요";
            default -> "";
        };
    }

    private String resolveSubMessage(String displayType, List<CategoryExpenseSummary> topCategories, int topRatio) {
        if (displayType.equals("ALL_EQUAL") || topRatio == 100) {
            return "소비가 여러 항목에 고르게 나뉘어 있어요";
        }
        return switch (displayType) {
            case "SINGLE" -> "%s 지출이 전체의 %d%%를 차지해요"
                    .formatted(topCategories.get(0).getName(), topRatio);
            case "DUAL" -> "두 항목이 전체의 %d%%를 차지해요".formatted(topRatio);
            case "MULTIPLE" -> "해당 항목들이 전체의 %d%%를 차지해요".formatted(topRatio);
            default -> "";
        };
    }

    private DailyReportResponse.Emotions buildEmotions(List<EmotionSummary> emotions) {
        if (emotions.isEmpty()) {
            return DailyReportResponse.Emotions.builder()
                    .list(List.of())
                    .isEmpty(true)
                    .build();
        }

        List<DailyReportResponse.Emotions.EmotionItem> list = new ArrayList<>();
        for (int i = 0; i < emotions.size(); i++) {
            EmotionSummary e = emotions.get(i);
            list.add(DailyReportResponse.Emotions.EmotionItem.builder()
                    .emotionId(e.getEmotionId())
                    .emotionName(e.getName())
                    .emotionGroupName(e.getEmotionGroupName())
                    .emotionCount(e.getEmotionCount())
                    .rank(i + 1)
                    .build());
        }

        return DailyReportResponse.Emotions.builder()
                .list(list)
                .isEmpty(false)
                .build();
    }
}
