package com.feellog.backend.domain.review.service;

import com.feellog.backend.domain.review.dto.response.ReviewResponse;
import com.feellog.backend.domain.review.entity.Review;
import com.feellog.backend.domain.review.entity.ReviewResultTemplate;
import com.feellog.backend.domain.review.dto.response.ReviewOptionSummaryResponse;
import com.feellog.backend.domain.review.dto.response.ReviewResultResponse;
import com.feellog.backend.domain.review.dto.response.ReviewTitleResponse;
import com.feellog.backend.domain.review.repository.ReviewResultTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewResultService {

    private final ReviewResultTemplateRepository reviewResultTemplateRepository;

    public ReviewResponse createResponse(Review review) {
        ReviewResultTemplate template = findTemplate(review);

        return new ReviewResponse(
                review.getId(),
                review.getReviewDate(),
                createTitle(template),
                createOptions(review),
                createResult(template)
        );
    }

    // 리뷰 설문 조사 결과에 따른 템플릿 가져오기
    private ReviewResultTemplate findTemplate(Review review) {
        return reviewResultTemplateRepository
                .findFirstByEmotionIdAndSituationTagIdAndSatisfactionOptionIdAndNextActionOptionIdAndIsActiveTrueOrderByPriorityAsc(
                        review.getEmotion().getId(),
                        review.getSituationTag().getId(),
                        review.getSatisfactionOption().getId(),
                        review.getNextActionOption().getId()
                )
                .orElse(null);
    }

    private ReviewTitleResponse createTitle(ReviewResultTemplate template) {
        if (template == null) {
            return new ReviewTitleResponse(
                    "오늘의 소비를 ",
                    "돌아본",
                    " 하루"
            );
        }

        return new ReviewTitleResponse(
                template.getTitlePrefixText(),
                template.getTitleHighlightText(),
                template.getTitleSuffixText()
        );
    }

    private ReviewOptionSummaryResponse createOptions(Review review) {
        return new ReviewOptionSummaryResponse(
                review.getSituationTag().getName(),
                review.getSatisfactionOption().getScore(),
                review.getNextActionOption().getOptionText()
        );
    }

    private ReviewResultResponse createResult(ReviewResultTemplate template) {
        if (template == null) {
            return new ReviewResultResponse(
                    "오늘의 소비 피드백",
                    "오늘의 소비는 감정과 상황이 함께 작용한 결과일 수 있어요. 어떤 순간에 소비가 일어났는지 돌아본 것만으로도 충분히 의미 있는 기록이에요.",
                    "내일은 이렇게 해보세요",
                    List.of(
                            "소비하기 전 잠깐 멈춰보기",
                            "정말 필요한 소비인지 확인하기",
                            "하루 뒤에도 필요한지 다시 생각해보기"
                    )
            );
        }

        return new ReviewResultResponse(
                template.getFeedbackTitle(),
                template.getFeedbackText(),
                template.getGuideTitle(),
                List.of(
                        template.getGuideItem1(),
                        template.getGuideItem2(),
                        template.getGuideItem3()
                )
        );
    }
}