package com.feellog.backend.domain.review.service;

import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.emotion.repository.EmotionRepository;
import com.feellog.backend.domain.review.dto.ReviewOptionsResponse;
import com.feellog.backend.domain.review.dto.request.ReviewUpsertRequest;
import com.feellog.backend.domain.review.dto.response.ReviewResponse;
import com.feellog.backend.domain.review.entity.Review;
import com.feellog.backend.domain.review.entity.ReviewChoiceOption;
import com.feellog.backend.domain.review.repository.ReviewChoiceOptionRepository;
import com.feellog.backend.domain.review.repository.ReviewRepository;
import com.feellog.backend.domain.situationtag.entity.SituationTag;
import com.feellog.backend.domain.situationtag.repository.SituationTagRepository;
import com.feellog.backend.domain.user.entity.User;
import com.feellog.backend.domain.user.entity.UserStatus;
import com.feellog.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final EmotionRepository emotionRepository;
    private final SituationTagRepository situationTagRepository;
    private final ReviewChoiceOptionRepository reviewChoiceOptionRepository;
    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final ReviewResultService reviewResultService;

    // REV-02 회고 선택지 조회하기
    public ReviewOptionsResponse getReviewOptions() {
        return new ReviewOptionsResponse(
                emotionRepository.findEmotionOptions(),
                situationTagRepository.findSituationTagOptions(),
                reviewChoiceOptionRepository.findOptionsByQuestionType("SATISFACTION"),
                reviewChoiceOptionRepository.findOptionsByQuestionType("NEXT_ACTION")
        );
    }

    // REV-03 회고 선택 내용 저장하기
    @Transactional
    public ReviewResponse upsertReview(
            Long userId,
            LocalDate reviewDate,
            ReviewUpsertRequest request
    ) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Emotion emotion = emotionRepository.findById(request.emotionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 감정입니다."));

        SituationTag situationTag = situationTagRepository.findById(request.situationTagId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소비 상황입니다."));

        ReviewChoiceOption satisfactionOption = reviewChoiceOptionRepository.findById(request.satisfactionOptionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 만족도 선택지입니다."));

        ReviewChoiceOption nextActionOption = reviewChoiceOptionRepository.findById(request.nextActionOptionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 내일 소비 계획 선택지입니다."));

        Review review = reviewRepository
                .findByUserIdAndReviewDate(userId, reviewDate)
                .map(existingReview -> {
                    existingReview.update(
                            emotion,
                            situationTag,
                            satisfactionOption,
                            nextActionOption
                    );
                    return existingReview;
                })
                .orElseGet(() -> Review.create(
                        user,
                        reviewDate,
                        emotion,
                        situationTag,
                        satisfactionOption,
                        nextActionOption
                ));

        Review savedReview = reviewRepository.save(review);

        // 리뷰 설문 -> 결과 생성은 전부 ReviewResultService로 위임
        return reviewResultService.createResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long userId, LocalDate date) {
        Review review = reviewRepository.findByUserIdAndReviewDate(userId, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 회고가 존재하지 않습니다."));

        return reviewResultService.createResponse(review);
    }
}