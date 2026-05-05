-- =========================================================
-- Feellog MVP 초기 더미 데이터
-- =========================================================

SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

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
-- INCOME CATEGORY
-- =========================================================
INSERT INTO income_category (name, created_at, updated_at)
VALUES ('급여', NOW(), NOW()),
       ('용돈', NOW(), NOW()),
       ('부수입', NOW(), NOW()),
       ('상여금', NOW(), NOW()),
       ('금융수입', NOW(), NOW()),
       ('기타', NOW(), NOW());

-- =========================================================
-- ASSET CATEGORY
-- income_category와 독립적으로 관리
-- =========================================================
INSERT INTO asset_category (name, created_at, updated_at)
VALUES ('급여', NOW(), NOW()),
       ('용돈', NOW(), NOW()),
       ('부수입', NOW(), NOW()),
       ('상여금', NOW(), NOW()),
       ('금융수입', NOW(), NOW()),
       ('기타', NOW(), NOW());


-- =========================================================
-- PAYMENT METHOD
-- =========================================================
INSERT INTO payment_method (name, created_at, updated_at)
VALUES ('카드', NOW(), NOW()),
       ('현금', NOW(), NOW()),
       ('계좌', NOW(), NOW()),
       ('기타', NOW(), NOW());

-- =========================================================
-- EMOTION GROUP
-- =========================================================
INSERT INTO emotion_group (name, created_at, updated_at)
VALUES ('긍정', NOW(), NOW()),
       ('부정', NOW(), NOW()),
       ('기타', NOW(), NOW());

-- =========================================================
-- EMOTION
-- =========================================================
INSERT INTO emotion (emotion_group_id, name, created_at, updated_at)
VALUES
    (1, '기쁨', NOW(), NOW()),
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
VALUES
    ('피로회복', NOW(), NOW()),
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
-- =========================================================
INSERT INTO review_choice_option
(question_type, option_text, option_value, score, sort_order, is_active, created_at, updated_at)
VALUES
-- 만족도
('SATISFACTION', '매우 만족스럽다', 'VERY_SATISFIED', 5, 1, TRUE, NOW(), NOW()),
('SATISFACTION', '만족스럽다', 'SATISFIED', 4, 2, TRUE, NOW(), NOW()),
('SATISFACTION', '보통이다', 'NORMAL', 3, 3, TRUE, NOW(), NOW()),
('SATISFACTION', '아쉽다', 'UNSATISFIED', 2, 4, TRUE, NOW(), NOW()),
('SATISFACTION', '매우 아쉽다', 'VERY_UNSATISFIED', 1, 5, TRUE, NOW(), NOW()),

-- 내일 다짐
('NEXT_ACTION', '오늘처럼 유지할래요', 'KEEP', 4, 1, TRUE, NOW(), NOW()),
('NEXT_ACTION', '조금 더 신중하게 소비할래요', 'CAREFUL', 3, 2, TRUE, NOW(), NOW()),
('NEXT_ACTION', '지출을 줄여볼래요', 'REDUCE', 2, 3, TRUE, NOW(), NOW()),
('NEXT_ACTION', '꼭 필요한 것만 살래요', 'NECESSARY_ONLY', 1, 4, TRUE, NOW(), NOW());

-- =========================================================
-- REVIEW PHRASE GROUP MAPPING
-- =========================================================
INSERT INTO review_phrase_group_mapping
(source_type, source_id, group_code, created_at, updated_at)
VALUES
-- EMOTION
('EMOTION', 1, 'positive', NOW(), NOW()),
('EMOTION', 2, 'positive', NOW(), NOW()),
('EMOTION', 3, 'positive', NOW(), NOW()),
('EMOTION', 4, 'positive', NOW(), NOW()),
('EMOTION', 5, 'tension', NOW(), NOW()),
('EMOTION', 6, 'tension', NOW(), NOW()),
('EMOTION', 7, 'anxious', NOW(), NOW()),
('EMOTION', 8, 'low_energy', NOW(), NOW()),
('EMOTION', 9, 'tension', NOW(), NOW()),
('EMOTION', 10, 'low_energy', NOW(), NOW()),
('EMOTION', 11, 'tension', NOW(), NOW()),
('EMOTION', 12, 'low_energy', NOW(), NOW()),
('EMOTION', 13, 'anxious', NOW(), NOW()),
('EMOTION', 14, 'anxious', NOW(), NOW()),
('EMOTION', 15, 'tension', NOW(), NOW()),

-- SITUATION
('SITUATION', 1, 'reward', NOW(), NOW()),
('SITUATION', 2, 'reward', NOW(), NOW()),
('SITUATION', 3, 'reward', NOW(), NOW()),
('SITUATION', 4, 'impulse', NOW(), NOW()),
('SITUATION', 5, 'impulse', NOW(), NOW()),
('SITUATION', 6, 'need', NOW(), NOW()),
('SITUATION', 7, 'social', NOW(), NOW()),
('SITUATION', 8, 'social', NOW(), NOW()),
('SITUATION', 9, 'etc', NOW(), NOW()),

-- SATISFACTION
('SATISFACTION', 1, 'positive', NOW(), NOW()),
('SATISFACTION', 2, 'positive', NOW(), NOW()),
('SATISFACTION', 3, 'neutral', NOW(), NOW()),
('SATISFACTION', 4, 'negative', NOW(), NOW()),
('SATISFACTION', 5, 'negative', NOW(), NOW()),

-- NEXT_ACTION
('NEXT_ACTION', 6, 'maintain', NOW(), NOW()),
('NEXT_ACTION', 7, 'careful', NOW(), NOW()),
('NEXT_ACTION', 8, 'reduce', NOW(), NOW()),
('NEXT_ACTION', 9, 'essential', NOW(), NOW());

-- =========================================================
-- REVIEW TITLE PHRASE
-- =========================================================
INSERT INTO review_title_phrase
(situation_group_code, satisfaction_group_code,
 title_prefix_text, title_highlight_text, title_suffix_text,
 created_at, updated_at)
VALUES

-- reward
('reward','positive','나를 위한 소비가 ','기분 좋게',' 남은 오늘',NOW(),NOW()),
('reward','neutral','나를 위한 소비를 ','다시 생각해본',' 오늘',NOW(),NOW()),
('reward','negative','나를 위한 보상보다 ','아쉬움','이 더 컸던 오늘',NOW(),NOW()),

-- impulse
('impulse','positive','순간의 선택이 ','나쁘지 않았던',' 오늘',NOW(),NOW()),
('impulse','neutral','즉흥적인 소비를 ','한 번 돌아본',' 오늘',NOW(),NOW()),
('impulse','negative','순간의 선택에 ','아쉬움','이 남은 오늘',NOW(),NOW()),

-- social
('social','positive','함께한 시간에 ','만족','이 남은 오늘',NOW(),NOW()),
('social','neutral','관계 속 소비를 ','가볍게 돌아본',' 오늘',NOW(),NOW()),
('social','negative','분위기보다 지출이 ','더 크게',' 느껴진 오늘',NOW(),NOW()),

-- need
('need','positive','필요한 소비를 ','잘 선택한',' 오늘',NOW(),NOW()),
('need','neutral','필요에 따라 ','소비한',' 오늘',NOW(),NOW()),
('need','negative','필요했지만 ','조금 아쉬움','이 남은 오늘',NOW(),NOW()),

-- etc
('etc','positive','오늘의 소비가 ','괜찮게',' 느껴진 하루',NOW(),NOW()),
('etc','neutral','오늘의 소비를 ','차분히 돌아본',' 하루',NOW(),NOW()),
('etc','negative','오늘의 소비에 ','아쉬움','이 남은 하루',NOW(),NOW());
-- =========================================================
-- REVIEW FEEDBACK TITLE PHRASE
-- =========================================================
INSERT INTO review_feedback_title_phrase
(situation_group_code, feedback_title, created_at, updated_at)
VALUES
    ('reward','나를 위한 소비를 어떻게 남길까요?',NOW(),NOW()),
    ('impulse','즉흥적인 소비를 한 번 점검해볼까요?',NOW(),NOW()),
    ('social','관계 속 소비를 가볍게 돌아볼까요?',NOW(),NOW()),
    ('need','필요한 소비였는지 확인해볼까요?',NOW(),NOW()),
    ('etc','오늘의 소비를 차분히 정리해볼까요?',NOW(),NOW());

-- =========================================================
-- REVIEW FEEDBACK PHRASE
-- =========================================================
INSERT INTO review_feedback_phrase
(situation_group_code, satisfaction_group_code, feedback_text, created_at, updated_at)
VALUES

-- reward
('reward','positive','나를 위한 소비가 좋은 회복이 되었어요. 이런 소비는 줄이기보다 기준을 기억해두면 좋아요.',NOW(),NOW()),
('reward','neutral','나를 위한 소비였지만 만족감이 크진 않았어요. 정말 필요한 보상이었는지 생각해보세요.',NOW(),NOW()),
('reward','negative','보상 소비는 자연스럽지만 아쉬움이 남았다면 예산을 정해두는 것이 좋아요.',NOW(),NOW()),

-- impulse
('impulse','positive','즉흥적인 소비였지만 만족감이 남았어요. 다만 반복되지 않도록 관리가 필요해요.',NOW(),NOW()),
('impulse','neutral','순간적으로 선택한 소비였어요. 다음에는 잠깐 멈춰보는 것도 좋아요.',NOW(),NOW()),
('impulse','negative','충동 소비는 이후 아쉬움이 더 크게 남을 수 있어요. 하루 뒤 다시 보는 방법을 추천해요.',NOW(),NOW()),

-- social
('social','positive','관계 속 소비가 만족스럽게 남았어요. 감당 가능한 범위에서 유지해보세요.',NOW(),NOW()),
('social','neutral','자연스럽게 발생한 소비였어요. 다음에는 금액을 미리 정해보세요.',NOW(),NOW()),
('social','negative','분위기 때문에 지출이 커졌을 수 있어요. 결제 전 한 번 더 확인해보세요.',NOW(),NOW()),

-- need
('need','positive','필요한 소비를 잘 선택했어요. 생활을 위한 좋은 소비예요.',NOW(),NOW()),
('need','neutral','필요한 소비였지만 만족감이 크지 않았어요. 대안이 있는지 확인해보세요.',NOW(),NOW()),
('need','negative','필요했지만 아쉬움이 남았어요. 어떤 점이 문제였는지 돌아보세요.',NOW(),NOW()),

-- etc
('etc','positive','오늘 소비가 전반적으로 괜찮았어요. 좋은 기준을 기억해보세요.',NOW(),NOW()),
('etc','neutral','특별히 좋지도 나쁘지도 않은 소비였어요. 기록만 해도 충분해요.',NOW(),NOW()),
('etc','negative','아쉬움이 남은 소비였어요. 상황을 돌아보면 다음 선택이 좋아져요.',NOW(),NOW());

-- =========================================================
-- REVIEW EMOTION ADJUSTMENT PHRASE
-- =========================================================
INSERT INTO review_emotion_adjustment_phrase
(emotion_group_code, adjustment_text, created_at, updated_at)
VALUES
    ('positive','소비 후 긍정적인 감정도 함께 기억해보세요.',NOW(),NOW()),
    ('anxious','소비 후 후회가 남았다면 다음 소비 전에 잠깐 멈춰보세요.',NOW(),NOW()),
    ('low_energy','에너지가 낮을 때는 소비 판단이 흔들릴 수 있어요.',NOW(),NOW()),
    ('tension','감정이 강할 때 소비가 빠르게 일어날 수 있어요.',NOW(),NOW());

-- =========================================================
-- REVIEW GUIDE PHRASE
-- =========================================================
-- =========================================================
-- REVIEW GUIDE PHRASE
-- 가이드: 내일 다짐 그룹
-- =========================================================
INSERT INTO review_guide_phrase
(next_action_group_code, guide_title, guide_item_1, guide_item_2, guide_item_3, created_at, updated_at)
VALUES
    ('maintain',
     '좋은 소비 습관을 유지하고 싶을 때 이렇게 해보세요',
     '만족도가 높았던 소비 기준 기억하기',
     '나에게 도움이 된 소비는 기록해두기',
     '비슷한 소비가 반복될 때 예산 안에서 유지하기',
     NOW(), NOW()),

    ('careful',
     '조금 더 신중하게 소비하고 싶을 때 이렇게 해보세요',
     '결제 전 필요한 이유를 한 문장으로 적어보기',
     '비슷한 물건을 이미 갖고 있는지 확인하기',
     '오늘 꼭 사야 하는 소비인지 생각해보기',
     NOW(), NOW()),

    ('reduce',
     '지출을 줄이고 싶을 때 이렇게 해보세요',
     '이번 주 사용할 예산 먼저 정하기',
     '바로 사지 않고 하루 뒤에 다시 생각해보기',
     '진짜 원하는 건지 스스로 되물어보기',
     NOW(), NOW()),

    ('essential',
     '꼭 필요한 소비만 하고 싶을 때 이렇게 해보세요',
     '구매 전 필수 소비와 선택 소비로 나눠보기',
     '장바구니에 담고 바로 결제하지 않기',
     '이번 주 안에 꼭 필요한 소비인지 확인하기',
     NOW(), NOW());