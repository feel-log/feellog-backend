package com.feellog.backend.domain.report.controller;

import com.feellog.backend.domain.report.dto.response.MonthlyReportResponse;
import com.feellog.backend.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}