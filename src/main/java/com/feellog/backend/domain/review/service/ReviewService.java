package com.feellog.backend.domain.review.service;

import com.feellog.backend.domain.catalog.emotion.entity.Emotion;
import com.feellog.backend.domain.review.repository.EmotionRepository;
import com.feellog.backend.domain.catalog.situationtag.entity.SituationTag;
import com.feellog.backend.domain.review.repository.SituationTagRepository;
import com.feellog.backend.domain.review.dto.ReviewOptionsResponse;
import com.feellog.backend.domain.review.dto.request.ReviewCreateRequest;
import com.feellog.backend.domain.review.dto.response.ReviewCreateResponse;
import com.feellog.backend.domain.review.entity.Review;
import com.feellog.backend.domain.review.entity.ReviewChoiceOption;
import com.feellog.backend.domain.review.entity.ReviewResultTemplate;
import com.feellog.backend.domain.review.repository.ReviewChoiceOptionRepository;
import com.feellog.backend.domain.review.repository.ReviewRepository;
import com.feellog.backend.domain.review.repository.ReviewResultTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final EmotionRepository emotionRepository;
    private final SituationTagRepository situationTagRepository;
    private final ReviewChoiceOptionRepository reviewChoiceOptionRepository;
    private final ReviewResultTemplateRepository reviewResultTemplateRepository;
    private final ReviewRepository reviewRepository;

    public ReviewOptionsResponse getReviewOptions() {
        return new ReviewOptionsResponse(
                emotionRepository.findEmotionOptions(),
                situationTagRepository.findSituationTagOptions(),
                reviewChoiceOptionRepository.findOptionsByQuestionType("SATISFACTION"),
                reviewChoiceOptionRepository.findOptionsByQuestionType("NEXT_ACTION")
        );
    }

    @Transactional
    public ReviewCreateResponse createReview(Long userId, ReviewCreateRequest request) {

        if (reviewRepository.existsByUserIdAndReviewDate(userId, request.reviewDate())) {
            throw new IllegalArgumentException("이미 해당 날짜의 회고가 작성되었습니다.");
        }

        Emotion emotion = emotionRepository.findById(request.emotionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 감정입니다."));

        SituationTag situationTag = situationTagRepository.findById(request.situationTagId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소비 상황입니다."));

        ReviewChoiceOption satisfactionOption = reviewChoiceOptionRepository.findById(request.satisfactionOptionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 만족도 선택지입니다."));

        ReviewChoiceOption nextActionOption = reviewChoiceOptionRepository.findById(request.nextActionOptionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 내일 소비 계획 선택지입니다."));

        ReviewResultTemplate template = reviewResultTemplateRepository
                .findFirstByEmotionIdAndSituationTagIdAndSatisfactionOptionIdAndNextActionOptionIdAndIsActiveTrueOrderByPriorityAsc(
                        request.emotionId(),
                        request.situationTagId(),
                        request.satisfactionOptionId(),
                        request.nextActionOptionId()
                )
                .orElse(null);

        String title = template != null
                ? template.getTitle()
                : "오늘의 소비를 돌아봤어요";

        String feedbackText = template != null
                ? template.getFeedbackText()
                : "오늘의 소비는 감정과 상황이 함께 작용한 결과일 수 있어요. 어떤 순간에 소비가 일어났는지 돌아본 것만으로도 충분히 의미 있는 기록이에요.";

        String guideText = template != null
                ? template.getGuideText()
                : "내일은 소비하기 전 잠깐 멈춰서 지금 필요한 소비인지 한 번만 확인해보세요.";

        Review review = Review.create(
                userId,
                request.reviewDate(),
                emotion,
                situationTag,
                satisfactionOption,
                nextActionOption,
                template,
                title,
                feedbackText,
                guideText
        );

        Review savedReview = reviewRepository.save(review);

        return new ReviewCreateResponse(
                savedReview.getId(),
                savedReview.getReviewDate(),
                savedReview.getTitle(),
                savedReview.getFeedbackText(),
                savedReview.getGuideText()
        );
    }
}