package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserIdAndReviewDate(Long userId, LocalDate reviewDate);

    Optional<Review> findByUserIdAndReviewDate(Long userId, LocalDate reviewDate);

    @Query("""
            SELECT r.reviewDate
            FROM Review r
            WHERE r.user.id = :userId
              AND r.reviewDate BETWEEN :startDate AND :endDate
            ORDER BY r.reviewDate ASC
            """)
    List<LocalDate> findReviewDatesByUserIdAndReviewDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}