package com.feellog.backend.domain.review.service;

import com.feellog.backend.domain.emotion.entity.Emotion;
import com.feellog.backend.domain.review.dto.response.ReviewDetailResponse;
import com.feellog.backend.domain.review.repository.EmotionRepository;
import com.feellog.backend.domain.situationtag.entity.SituationTag;
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
    private final ReviewResultTemplateRepository reviewResultTemplateRepository;
    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

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

        String summaryText = template != null
                ? template.getSummaryText()
                : "오늘 선택한 감정과 소비 상황을 바탕으로 회고 결과를 정리했어요.";

        String feedbackTitle = template != null
                ? template.getFeedbackTitle()
                : "오늘의 소비 피드백";

        String feedbackText = template != null
                ? template.getFeedbackText()
                : "오늘의 소비는 감정과 상황이 함께 작용한 결과일 수 있어요. 어떤 순간에 소비가 일어났는지 돌아본 것만으로도 충분히 의미 있는 기록이에요.";

        String guideTitle = template != null
                ? template.getGuideTitle()
                : "내일은 이렇게 해보세요";

        String guideItem1 = template != null
                ? template.getGuideItem1()
                : "소비하기 전 잠깐 멈춰보기";

        String guideItem2 = template != null
                ? template.getGuideItem2()
                : "정말 필요한 소비인지 확인하기";

        String guideItem3 = template != null
                ? template.getGuideItem3()
                : "하루 뒤에도 필요한지 다시 생각해보기";

        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = Review.create(
                user,
                request.reviewDate(),
                emotion,
                situationTag,
                satisfactionOption,
                nextActionOption,
                template,
                title,
                summaryText,
                feedbackTitle,
                feedbackText,
                guideTitle,
                guideItem1,
                guideItem2,
                guideItem3
        );

        Review savedReview = reviewRepository.save(review);

        return new ReviewCreateResponse(
                savedReview.getId(),
                savedReview.getReviewDate(),
                savedReview.getTitle(),
                savedReview.getSummaryText(),
                savedReview.getFeedbackTitle(),
                savedReview.getFeedbackText(),
                savedReview.getGuideTitle(),
                List.of(
                        savedReview.getGuideItem1(),
                        savedReview.getGuideItem2(),
                        savedReview.getGuideItem3()
                )
        );
    }

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReview(Long userId, LocalDate date) {
        Review review = reviewRepository.findByUserIdAndReviewDate(userId, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜의 회고가 존재하지 않습니다."));

        return new ReviewDetailResponse(
                review.getId(),
                review.getReviewDate(),

                review.getEmotion().getId(),
                review.getEmotion().getName(),

                review.getSituationTag().getId(),
                review.getSituationTag().getName(),

                review.getSatisfactionOption().getId(),
                review.getSatisfactionOption().getOptionText(),

                review.getNextActionOption().getId(),
                review.getNextActionOption().getOptionText(),

                review.getTitle(),
                review.getSummaryText(),

                review.getFeedbackTitle(),
                review.getFeedbackText(),

                review.getGuideTitle(),
                List.of(
                        review.getGuideItem1(),
                        review.getGuideItem2(),
                        review.getGuideItem3()
                )
        );
    }
}