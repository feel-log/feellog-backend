package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndReviewDate(Long userId, LocalDate reviewDate);

    Optional<Review> findByUserIdAndReviewDate(Long userId, LocalDate reviewDate);
}