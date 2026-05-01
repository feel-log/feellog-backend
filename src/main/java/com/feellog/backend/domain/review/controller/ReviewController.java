package com.feellog.backend.domain.review.controller;

import com.feellog.backend.domain.review.dto.ReviewOptionsResponse;
import com.feellog.backend.domain.review.dto.request.ReviewUpsertRequest;
import com.feellog.backend.domain.review.dto.response.ReviewResponse;
import com.feellog.backend.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ReviewResponse getReview(
            @AuthenticationPrincipal Long userId,
            @RequestParam LocalDate date
    ) {
        return reviewService.getReview(userId, date);
    }
}