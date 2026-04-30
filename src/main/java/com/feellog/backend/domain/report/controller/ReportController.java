package com.feellog.backend.domain.report.controller;

import com.feellog.backend.domain.report.dto.response.MonthlyReportResponse;
import com.feellog.backend.domain.report.dto.response.WeeklyReportResponse;
import com.feellog.backend.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @AuthenticationPrincipal Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(reportService.getMonthlyReport(userId, year, month));
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyReportResponse> getWeeklyReport(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(reportService.getWeeklyReport(userId));
    }
}
