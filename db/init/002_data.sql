-- =========================================================
-- Feellog MVP 초기 더미 데이터
-- =========================================================

SET NAMES utf8mb4;

-- =========================================================
-- CATEGORY GROUP
-- =========================================================
INSERT INTO category_group (name, created_at, updated_at)
VALUES ('생활', NOW(), NOW()),
       ('소비', NOW(), NOW()),
       ('고정', NOW(), NOW()),
       ('여가', NOW(), NOW());

-- =========================================================
-- CATEGORY
-- 생활 = 1
-- 소비 = 2
-- 고정 = 3
-- 여가 = 4
-- =========================================================
INSERT INTO category (category_group_id, name, created_at, updated_at)
VALUES (1, '식비', NOW(), NOW()),
       (1, '카페', NOW(), NOW()),
       (1, '생필품', NOW(), NOW()),

       (2, '의류', NOW(), NOW()),
       (2, '교통비', NOW(), NOW()),
       (2, '의료', NOW(), NOW()),
       (2, '교육', NOW(), NOW()),
       (2, '경조사', NOW(), NOW()),

       (3, '공과금', NOW(), NOW()),
       (3, '주거', NOW(), NOW()),
       (3, '보험료', NOW(), NOW()),
       (3, '저축', NOW(), NOW()),

       (4, '취미', NOW(), NOW()),
       (4, '뷰티', NOW(), NOW()),
       (4, '문화생활', NOW(), NOW());

-- =========================================================
-- EMOTION GROUP
-- =========================================================
INSERT INTO emotion_group (name, created_at, updated_at)
VALUES ('긍정', NOW(), NOW()),
       ('부정', NOW(), NOW()),
       ('기타', NOW(), NOW());

-- =========================================================
-- EMOTION
-- 긍정 = 1
-- 부정 = 2
-- 기타 = 3
-- =========================================================
INSERT INTO emotion (emotion_group_id, name, created_at, updated_at)
VALUES (1, '기쁨', NOW(), NOW()),
       (1, '설렘', NOW(), NOW()),
       (1, '뿌듯함', NOW(), NOW()),
       (1, '고마움', NOW(), NOW()),

       (2, '짜증', NOW(), NOW()),
       (2, '화남', NOW(), NOW()),
       (2, '불안함', NOW(), NOW()),
       (2, '슬픔', NOW(), NOW()),
       (2, '스트레스', NOW(), NOW()),
       (2, '우울함', NOW(), NOW()),

       (3, '심심함', NOW(), NOW()),
       (3, '피곤함', NOW(), NOW()),
       (3, '공허함', NOW(), NOW()),
       (3, '외로움', NOW(), NOW()),
       (3, '충동', NOW(), NOW());

-- =========================================================
-- SITUATION TAG
-- =========================================================
INSERT INTO situation_tag (name, created_at, updated_at)
VALUES ('피로회복', NOW(), NOW()),
       ('기분전환', NOW(), NOW()),
       ('보상심리', NOW(), NOW()),
       ('할인', NOW(), NOW()),
       ('충동소비', NOW(), NOW()),
       ('필요', NOW(), NOW()),
       ('약속', NOW(), NOW()),
       ('지각', NOW(), NOW()),
       ('기타', NOW(), NOW());

-- =========================================================
-- REVIEW CHOICE OPTION
-- 만족도 5단계 + 내일 소비 계획 4단계
-- =========================================================
INSERT INTO review_choice_option
(question_type, option_text, option_value, score, sort_order, is_active, created_at, updated_at)
VALUES
-- 소비 만족도 5단계
('SATISFACTION', '매우 만족했어요', 'VERY_SATISFIED', 5, 1, TRUE, NOW(), NOW()),
('SATISFACTION', '만족했어요', 'SATISFIED', 4, 2, TRUE, NOW(), NOW()),
('SATISFACTION', '보통이었어요', 'NORMAL', 3, 3, TRUE, NOW(), NOW()),
('SATISFACTION', '아쉬웠어요', 'UNSATISFIED', 2, 4, TRUE, NOW(), NOW()),
('SATISFACTION', '많이 아쉬웠어요', 'VERY_UNSATISFIED', 1, 5, TRUE, NOW(), NOW()),

-- 내일 소비 계획 4단계
('NEXT_ACTION', '오늘처럼 유지할래요', 'KEEP', 4, 1, TRUE, NOW(), NOW()),
('NEXT_ACTION', '조금 더 신중하게 소비할래요', 'CAREFUL', 3, 2, TRUE, NOW(), NOW()),
('NEXT_ACTION', '지출을 줄여볼래요', 'REDUCE', 2, 3, TRUE, NOW(), NOW()),
('NEXT_ACTION', '꼭 필요한 것만 살래요', 'NECESSARY_ONLY', 1, 4, TRUE, NOW(), NOW());

-- =========================================================
-- REVIEW RESULT TEMPLATE
-- 대표 조합 기반 회고 결과 템플릿
-- =========================================================
INSERT INTO review_result_template
(emotion_id, situation_tag_id, satisfaction_option_id, next_action_option_id,
 title, feedback_text, guide_text, priority, is_active, created_at, updated_at)
VALUES
-- 스트레스 + 보상심리 + 아쉬움 + 지출 줄이기
(9, 3, 4, 8,
 '보상보다 부담이 더 컸던 하루',
 '오늘은 스트레스와 보상심리가 소비로 이어졌지만, 만족감보다는 아쉬움이 남은 하루였어요.',
 '내일은 소비로 바로 풀기보다 산책, 휴식, 대화처럼 돈을 쓰지 않는 회복 방법도 함께 시도해보세요.',
 1, TRUE, NOW(), NOW()),

-- 충동 + 충동소비 + 많이 아쉬움 + 필요한 것만
(15, 5, 5, 9,
 '충동 소비를 돌아볼 필요가 있는 하루',
 '오늘은 순간적인 충동이 소비로 이어졌고, 결과적으로 아쉬움이 크게 남았어요.',
 '내일은 구매 전 10분만 기다리거나 장바구니에 먼저 담아두는 방식으로 소비를 한 번 늦춰보세요.',
 1, TRUE, NOW(), NOW()),

-- 기쁨 + 약속 + 만족 + 유지
(1, 7, 2, 6,
 '기분 좋은 소비가 있었던 하루',
 '오늘의 소비는 좋은 감정과 약속 속에서 이루어졌고, 전반적으로 만족스러운 선택이었어요.',
 '내일도 오늘처럼 예산 안에서 즐거움을 주는 소비를 유지해보세요.',
 1, TRUE, NOW(), NOW()),

-- 피곤함 + 피로회복 + 보통 + 신중하게
(12, 1, 3, 7,
 '회복을 위해 소비한 하루',
 '오늘은 피곤함을 해소하기 위한 소비가 있었지만, 만족도는 보통 수준이었어요.',
 '내일은 소비하기 전에 충분한 휴식이나 수면처럼 직접적인 회복 방법도 먼저 떠올려보세요.',
 1, TRUE, NOW(), NOW()),

-- 불안함 + 필요 + 만족 + 신중하게
(7, 6, 2, 7,
 '필요를 잘 판단한 하루',
 '오늘은 불안한 마음 속에서도 필요한 소비를 선택했고, 만족도도 괜찮았어요.',
 '내일도 필요한 소비인지 한 번 더 확인하면서 신중한 선택을 이어가보세요.',
 1, TRUE, NOW(), NOW()),

-- 심심함 + 기분전환 + 보통 + 지출 줄이기
(11, 2, 3, 8,
 '기분 전환을 고민한 하루',
 '오늘은 심심함을 달래기 위한 소비가 있었지만, 만족감은 크지 않았어요.',
 '내일은 돈을 쓰는 활동 외에도 산책, 콘텐츠 정리, 친구와 대화 같은 대안을 시도해보세요.',
 1, TRUE, NOW(), NOW());