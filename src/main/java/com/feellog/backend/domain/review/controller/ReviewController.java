package com.feellog.backend.domain.review.controller;

import com.feellog.backend.domain.review.dto.ReviewOptionsResponse;
import com.feellog.backend.domain.review.dto.request.ReviewCreateRequest;
import com.feellog.backend.domain.review.dto.response.ReviewCreateResponse;
import com.feellog.backend.domain.review.dto.response.ReviewDetailResponse;
import com.feellog.backend.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewCreateResponse createReview(
            @AuthenticationPrincipal Long userId,
            @RequestBody ReviewCreateRequest request
    ) {
        return reviewService.createReview(userId, request);
    }

    @GetMapping
    public ReviewDetailResponse getReview(
            @AuthenticationPrincipal Long userId,
            @RequestParam LocalDate date
    ) {
        return reviewService.getReview(userId, date);
    }
}