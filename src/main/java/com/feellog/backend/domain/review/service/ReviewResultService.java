package com.feellog.backend.domain.review.service;

import com.feellog.backend.domain.review.dto.response.ReviewOptionSummaryResponse;
import com.feellog.backend.domain.review.dto.response.ReviewResponse;
import com.feellog.backend.domain.review.dto.response.ReviewResultResponse;
import com.feellog.backend.domain.review.dto.response.ReviewTitleResponse;
import com.feellog.backend.domain.review.entity.*;
import com.feellog.backend.domain.review.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewResultService {

    private final ReviewPhraseGroupMappingRepository reviewPhraseGroupMappingRepository;
    private final ReviewTitlePhraseRepository reviewTitlePhraseRepository;
    private final ReviewFeedbackTitlePhraseRepository reviewFeedbackTitlePhraseRepository;
    private final ReviewFeedbackPhraseRepository reviewFeedbackPhraseRepository;
    private final ReviewEmotionAdjustmentPhraseRepository reviewEmotionAdjustmentPhraseRepository;
    private final ReviewGuidePhraseRepository reviewGuidePhraseRepository;

    public ReviewResponse createResponse(Review review) {
        String emotionGroupCode = getGroupCode("EMOTION", review.getEmotion().getId());
        String situationGroupCode = getGroupCode("SITUATION", review.getSituationTag().getId());
        String satisfactionGroupCode = getGroupCode("SATISFACTION", review.getSatisfactionOption().getId());
        String nextActionGroupCode = getGroupCode("NEXT_ACTION", review.getNextActionOption().getId());

        ReviewTitlePhrase titlePhrase = reviewTitlePhraseRepository
                .findBySituationGroupCodeAndSatisfactionGroupCode(
                        situationGroupCode,
                        satisfactionGroupCode
                )
                .orElse(null);

        ReviewFeedbackTitlePhrase feedbackTitlePhrase = reviewFeedbackTitlePhraseRepository
                .findBySituationGroupCode(situationGroupCode)
                .orElse(null);

        ReviewFeedbackPhrase feedbackPhrase = reviewFeedbackPhraseRepository
                .findBySituationGroupCodeAndSatisfactionGroupCode(
                        situationGroupCode,
                        satisfactionGroupCode
                )
                .orElse(null);

        ReviewEmotionAdjustmentPhrase emotionAdjustmentPhrase = reviewEmotionAdjustmentPhraseRepository
                .findByEmotionGroupCode(emotionGroupCode)
                .orElse(null);

        ReviewGuidePhrase guidePhrase = reviewGuidePhraseRepository
                .findByNextActionGroupCode(nextActionGroupCode)
                .orElse(null);

        return new ReviewResponse(
                review.getId(),
                review.getReviewDate(),
                createTitle(titlePhrase),
                createOptions(review),
                createResult(
                        feedbackTitlePhrase,
                        feedbackPhrase,
                        emotionAdjustmentPhrase,
                        guidePhrase
                )
        );
    }

    private String getGroupCode(String sourceType, Long sourceId) {
        return reviewPhraseGroupMappingRepository
                .findBySourceTypeAndSourceId(sourceType, sourceId)
                .map(ReviewPhraseGroupMapping::getGroupCode)
                .orElse("etc");
    }

    private ReviewTitleResponse createTitle(ReviewTitlePhrase titlePhrase) {
        if (titlePhrase == null) {
            return new ReviewTitleResponse(
                    "오늘의 소비를 ",
                    "돌아본",
                    " 하루"
            );
        }

        return new ReviewTitleResponse(
                titlePhrase.getTitlePrefixText(),
                titlePhrase.getTitleHighlightText(),
                titlePhrase.getTitleSuffixText()
        );
    }

    private ReviewOptionSummaryResponse createOptions(Review review) {
        return new ReviewOptionSummaryResponse(
                review.getSituationTag().getName(),
                review.getSatisfactionOption().getScore(),
                review.getNextActionOption().getOptionText()
        );
    }

    private ReviewResultResponse createResult(
            ReviewFeedbackTitlePhrase feedbackTitlePhrase,
            ReviewFeedbackPhrase feedbackPhrase,
            ReviewEmotionAdjustmentPhrase emotionAdjustmentPhrase,
            ReviewGuidePhrase guidePhrase
    ) {
        String feedbackTitle = feedbackTitlePhrase != null
                ? feedbackTitlePhrase.getFeedbackTitle()
                : "오늘의 소비 피드백";

        String feedbackText = feedbackPhrase != null
                ? feedbackPhrase.getFeedbackText()
                : "오늘의 소비는 감정과 상황이 함께 작용한 결과일 수 있어요. 어떤 순간에 소비가 일어났는지 돌아본 것만으로도 충분히 의미 있는 기록이에요.";

        if (emotionAdjustmentPhrase != null) {
            feedbackText += " " + emotionAdjustmentPhrase.getAdjustmentText();
        }

        String guideTitle = guidePhrase != null
                ? guidePhrase.getGuideTitle()
                : "내일은 이렇게 해보세요";

        List<String> guideItems = guidePhrase != null
                ? List.of(
                guidePhrase.getGuideItem1(),
                guidePhrase.getGuideItem2(),
                guidePhrase.getGuideItem3()
        )
                : List.of(
                "소비하기 전 잠깐 멈춰보기",
                "정말 필요한 소비인지 확인하기",
                "하루 뒤에도 필요한지 다시 생각해보기"
        );

        return new ReviewResultResponse(
                feedbackTitle,
                feedbackText,
                guideTitle,
                guideItems
        );
    }
}