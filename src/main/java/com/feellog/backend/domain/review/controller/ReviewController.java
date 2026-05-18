package com.feellog.backend.domain.review.controller;

import com.feellog.backend.domain.review.dto.ReviewOptionsResponse;
import com.feellog.backend.domain.review.dto.request.ReviewUpsertRequest;
import com.feellog.backend.domain.review.dto.response.MonthlyReviewResponse;
import com.feellog.backend.domain.review.dto.response.ReviewResponse;
import com.feellog.backend.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/options")
    public ReviewOptionsResponse getReviewOptions() {
        return reviewService.getReviewOptions();
    }

    @PutMapping("/{reviewDate}")
    public ReviewResponse upsertReview(
            @AuthenticationPrincipal Long userId,
            @PathVariable LocalDate reviewDate,
            @RequestBody ReviewUpsertRequest request
    ) {
        return reviewService.upsertReview(userId, reviewDate, request);
    }

    @GetMapping("/{reviewDate}")
    public ReviewResponse getReview(
            @AuthenticationPrincipal Long userId,
            @PathVariable LocalDate reviewDate
    ) {
        return reviewService.getReview(userId, reviewDate);
    }

    @GetMapping("/monthly")
    public MonthlyReviewResponse getMonthlyReviews(
            @AuthenticationPrincipal Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reviewService.getMonthlyReviews(userId, year, month);
    }
}