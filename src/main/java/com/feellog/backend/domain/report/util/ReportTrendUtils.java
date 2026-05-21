package com.feellog.backend.domain.report.util;

import com.feellog.backend.domain.report.dto.projection.MonthlyTagRankProjection;
import com.feellog.backend.domain.report.dto.response.ConsecutiveTrendDto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportTrendUtils {

    private ReportTrendUtils() {}

    public static ConsecutiveTrendDto buildConsecutiveTrend(
            List<MonthlyTagRankProjection> rawData,
            YearMonth baseMonth,
            String messageTemplate,
            int trendMonths
    ) {
        // 월별 tagId → score 맵 (최신월 index 0)
        List<Map<Long, Long>> monthlyScores = new ArrayList<>();
        List<Map<Long, String>> monthlyNames = new ArrayList<>();

        // 루프 돌기 전에 미리 연월별로 그룹화 (O(N))
        Map<YearMonth, List<MonthlyTagRankProjection>> groupedByMonth = rawData.stream()
                .collect(Collectors.groupingBy(d -> YearMonth.of(d.getYear(), d.getMonth())));

        for (int i = 0; i < trendMonths; i++) {
            YearMonth ym = baseMonth.minusMonths(i);
            Map<Long, Long> scoreMap = new LinkedHashMap<>();
            Map<Long, String> nameMap = new LinkedHashMap<>();

            // 전체 리스트가 아니라 해당 월의 데이터만 꺼내서 처리
            List<MonthlyTagRankProjection> monthData =
                    groupedByMonth.getOrDefault(ym, Collections.emptyList());
            for (MonthlyTagRankProjection d : monthData) {
                scoreMap.merge(d.getTagId(), d.getScore().longValue(), Long::sum);
                nameMap.putIfAbsent(d.getTagId(), d.getTagName());
            }

            monthlyScores.add(scoreMap);
            monthlyNames.add(nameMap);
        }

        // 당월 데이터 없으면 미노출
        if (monthlyScores.getFirst().isEmpty()) return emptyTrend();

        // 당월 1위 tagId 집합
        Set<Long> consecutiveTopIds = getTopIds(monthlyScores.getFirst());
        int consecutiveMonths = 1;

        for (int i = 1; i < trendMonths; i++) {
            if (monthlyScores.get(i).isEmpty()) break;

            Set<Long> prevTopIds = getTopIds(monthlyScores.get(i));
            consecutiveTopIds.retainAll(prevTopIds); // 교집합: 연속 공동 1위만 유지

            if (consecutiveTopIds.isEmpty()) break;
            consecutiveMonths++;
        }

        // 1개월만 1위 → 미노출
        if (consecutiveMonths <= 1) return emptyTrend();

        // 태그명 수집 (당월 nameMap 기준)
        Map<Long, String> currentNameMap = monthlyNames.getFirst();
        List<String> topNames = consecutiveTopIds.stream()
                .sorted()
                .map(currentNameMap::get)
                .filter(name -> name != null && !name.isBlank())
                .toList();

        String message = resolveConsecutiveMessage(messageTemplate, consecutiveMonths, topNames);

        return ConsecutiveTrendDto.builder()
                .months(consecutiveMonths)
                .names(topNames)
                .message(message)
                .build();
    }

    // scoreMap에서 최고점 동률 tagId Set 반환
    private static Set<Long> getTopIds(Map<Long, Long> scoreMap) {
        long maxScore = scoreMap.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);

        return scoreMap.entrySet().stream()
                .filter(e -> e.getValue() == maxScore)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    // 연속 문구 포맷 처리 (공동 1위 3개 이상 시 "외 N개" 처리)
    private static String resolveConsecutiveMessage(String template, int months, List<String> names) {
        String namesStr;
        if (names.size() >= 3) {
            namesStr = names.get(0) + ", " + names.get(1) + " 외 " + (names.size() - 2) + "개";
        } else {
            namesStr = String.join(", ", names);
        }

        return template
                .replace("{N}", String.valueOf(months))
                .replace("{names}", namesStr);
    }

    private static ConsecutiveTrendDto emptyTrend() {
        return ConsecutiveTrendDto.builder().months(0).names(List.of()).message(null).build();
    }
}