-- =========================================================
-- Feellog MVP 초기 스키마
-- =========================================================

SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS review;

DROP TABLE IF EXISTS review_guide_phrase;
DROP TABLE IF EXISTS review_emotion_adjustment_phrase;
DROP TABLE IF EXISTS review_feedback_phrase;
DROP TABLE IF EXISTS review_feedback_title_phrase;
DROP TABLE IF EXISTS review_title_phrase;
DROP TABLE IF EXISTS review_phrase_group_mapping;

DROP TABLE IF EXISTS review_choice_option;

DROP TABLE IF EXISTS asset;
DROP TABLE IF EXISTS asset_category;

DROP TABLE IF EXISTS income;
DROP TABLE IF EXISTS income_category;

DROP TABLE IF EXISTS expense_situation_tag;
DROP TABLE IF EXISTS expense_emotion;
DROP TABLE IF EXISTS expense;

DROP TABLE IF EXISTS payment_method;

DROP TABLE IF EXISTS situation_tag;
DROP TABLE IF EXISTS emotion;
DROP TABLE IF EXISTS emotion_group;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS category_group;

DROP TABLE IF EXISTS device_tokens;
DROP TABLE IF EXISTS notification_settings;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- USERS (소셜 로그인 통합)
-- =========================================================
CREATE TABLE users
(
    user_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider         VARCHAR(20)  NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email            VARCHAR(255),
    nickname         VARCHAR(100) NOT NULL,
    birth_date       DATE,
    gender           VARCHAR(20),
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_login_at    DATETIME,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,
    deleted_at       DATETIME,
    CONSTRAINT uq_provider_user UNIQUE (provider, provider_user_id),
    CONSTRAINT uq_email UNIQUE (email)
);

-- =========================================================
-- REFRESH TOKENS
-- =========================================================
CREATE TABLE refresh_tokens
(
    refresh_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    token            VARCHAR(512) NOT NULL,
    expires_at       DATETIME     NOT NULL,
    created_at       DATETIME     NOT NULL,

    CONSTRAINT uq_refresh_token_user UNIQUE (user_id),
    CONSTRAINT uq_refresh_token UNIQUE (token),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);

-- =========================================================
-- NOTIFICATION SETTINGS
-- 사용자 알림 ON/OFF 설정
-- users 1 : 1 notification_settings
-- =========================================================
CREATE TABLE notification_settings (
    notification_setting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    push_enabled    BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME        NOT NULL,
    updated_at      DATETIME        NOT NULL,

    CONSTRAINT uq_notification_settings_user UNIQUE (user_id),

    CONSTRAINT fk_notification_settings_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);

-- =========================================================
-- DEVICE TOKENS
-- 사용자 디바이스별 FCM 토큰 관리
-- users 1 : N device_tokens
-- =========================================================
CREATE TABLE device_tokens (
    device_token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    token           VARCHAR(512)    NOT NULL,
    device_type     VARCHAR(20),
    last_used_at    DATETIME,
    created_at      DATETIME        NOT NULL,
    updated_at      DATETIME        NOT NULL,

    CONSTRAINT uq_device_token UNIQUE (token),

    CONSTRAINT fk_device_token_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);

-- =========================================================
-- CATEGORY GROUP
-- (category_group 1 : N category)
-- 생활 → 식비, 카페, 생필품
-- 소비 → 의류, 교통비, 의료, 교육, 경조사
-- 고정 → 공과금, 주거, 보험료, 저축
-- 여가 → 취미, 뷰티, 문화생활
-- =========================================================
CREATE TABLE category_group
(
    category_group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL UNIQUE,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL
);

-- =========================================================
-- CATEGORY
-- 식비, 카페, 생필품
-- 의류, 교통비, 의료, 교육, 경조사
-- 공과금, 주거, 보험료, 저축
-- 취미, 뷰티, 문화생활
-- =========================================================
CREATE TABLE category
(
    category_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_group_id BIGINT       NOT NULL,
    name              VARCHAR(100) NOT NULL UNIQUE,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL,

    CONSTRAINT fk_category_group
        FOREIGN KEY (category_group_id)
            REFERENCES category_group (category_group_id)
);

-- =========================================================
-- PAYMENT METHOD
-- 카드, 현금, 계좌, 기타
-- =========================================================
CREATE TABLE payment_method
(
    payment_method_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(50) NOT NULL UNIQUE,
    created_at        DATETIME    NOT NULL,
    updated_at        DATETIME    NOT NULL
);

-- =========================================================
-- INCOME CATEGORY
-- 급여, 용돈, 부수입, 상여금, 금융, 수입, 기타
-- =========================================================
CREATE TABLE income_category
(
    income_category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(100) NOT NULL UNIQUE,
    created_at         DATETIME     NOT NULL,
    updated_at         DATETIME     NOT NULL
);

-- =========================================================
-- ASSET CATEGORY
-- income_category와 독립적으로 관리
-- =========================================================
CREATE TABLE asset_category
(
    asset_category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL UNIQUE,
    created_at        DATETIME     NOT NULL,
    updated_at        DATETIME     NOT NULL
);

-- =========================================================
-- EMOTION GROUP
-- (emotion_group 1 : N emotion)
-- 긍정 → 기쁨, 설렘, 뿌듯함, 고마움
-- 부정 → 짜증, 화남, 불안함, 슬픔, 스트레스, 우울함
-- 기타 → 심심함, 피곤함, 공허함, 외로움, 충동
-- =========================================================
CREATE TABLE emotion_group
(
    emotion_group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL
);

-- =========================================================
-- EMOTION
-- 기쁨, 설렘, 뿌듯함, 고마움
-- 짜증, 화남, 불안함, 슬픔, 스트레스, 우울함
-- 심심함, 피곤함, 공허함, 외로움, 충동
-- =========================================================
CREATE TABLE emotion
(
    emotion_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    emotion_group_id BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL,

    CONSTRAINT fk_emotion_group
        FOREIGN KEY (emotion_group_id)
            REFERENCES emotion_group (emotion_group_id)
);

-- =========================================================
-- SITUATION TAG
-- 단독 마스터 테이블 (group 테이블 X)
-- 피로회복, 기분전환, 보상심리, 충동소비, 필요
-- =========================================================
CREATE TABLE situation_tag
(
    situation_tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL UNIQUE,
    created_at       DATETIME     NOT NULL,
    updated_at       DATETIME     NOT NULL
);

-- =========================================================
-- EXPENSE (핵심 테이블)
-- users 1 : N expense                  // 한 명의 유저가 여러 개의 expense(소비를 가짐)
-- category 1 : N expense               // 하나의 소비는 하나의 카테고리를 가짐
-- expense N : M emotion                // 하나의 소비는 0개 이상의 감정을 가짐
-- expense N : M situation_tag          // 하나의 소비는 0개 이상의 상황 태그를 가짐
-- =========================================================
CREATE TABLE expense
(
    expense_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT         NOT NULL,
    category_id       BIGINT         NOT NULL,
    payment_method_id BIGINT         NOT NULL,
    amount            DECIMAL(15, 2) NOT NULL,
    expense_date      DATE           NOT NULL,
    expense_time      TIME,
    merchant_name     VARCHAR(150),
    memo              TEXT,
    is_deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at        DATETIME       NOT NULL,
    updated_at        DATETIME       NOT NULL,
    deleted_at        DATETIME,

    CONSTRAINT fk_expense_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),

    CONSTRAINT fk_expense_category
        FOREIGN KEY (category_id)
            REFERENCES category (category_id),

    CONSTRAINT fk_expense_payment_method
        FOREIGN KEY (payment_method_id)
            REFERENCES payment_method (payment_method_id)
);

-- =========================================================
-- INCOME (수입 기록)
-- =========================================================
CREATE TABLE income
(
    income_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT         NOT NULL,
    income_category_id BIGINT         NOT NULL,
    amount             DECIMAL(15, 2) NOT NULL,
    income_date        DATE           NOT NULL,
    memo               TEXT,
    is_deleted         BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at         DATETIME       NOT NULL,
    updated_at         DATETIME       NOT NULL,
    deleted_at         DATETIME,

    CONSTRAINT fk_income_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),

    CONSTRAINT fk_income_category
        FOREIGN KEY (income_category_id)
            REFERENCES income_category (income_category_id)
);

-- =========================================================
-- ASSET (자산 기록)
-- =========================================================
CREATE TABLE asset
(
    asset_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT         NOT NULL,
    asset_category_id BIGINT         NOT NULL,
    amount            DECIMAL(15, 2) NOT NULL,
    asset_date        DATE           NOT NULL,
    memo              TEXT,
    is_deleted        BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at        DATETIME       NOT NULL,
    updated_at        DATETIME       NOT NULL,
    deleted_at        DATETIME,

    CONSTRAINT fk_asset_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),

    CONSTRAINT fk_asset_category
        FOREIGN KEY (asset_category_id)
            REFERENCES asset_category (asset_category_id)
);

-- =========================================================
-- EXPENSE EMOTION (N:M)
-- =========================================================
CREATE TABLE expense_emotion
(
    expense_emotion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id         BIGINT   NOT NULL,
    emotion_id         BIGINT   NOT NULL,
    created_at         DATETIME NOT NULL,

    CONSTRAINT uq_expense_emotion UNIQUE (expense_id, emotion_id),

    CONSTRAINT fk_expense_emotion_expense
        FOREIGN KEY (expense_id)
            REFERENCES expense (expense_id),

    CONSTRAINT fk_expense_emotion_emotion
        FOREIGN KEY (emotion_id)
            REFERENCES emotion (emotion_id)
);

-- =========================================================
-- EXPENSE SITUATION TAG (N:M)
-- =========================================================
CREATE TABLE expense_situation_tag
(
    expense_situation_tag_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id               BIGINT   NOT NULL,
    situation_tag_id         BIGINT   NOT NULL,
    created_at               DATETIME NOT NULL,

    CONSTRAINT uq_expense_situation UNIQUE (expense_id, situation_tag_id),

    CONSTRAINT fk_expense_situation_expense
        FOREIGN KEY (expense_id)
            REFERENCES expense (expense_id),

    CONSTRAINT fk_expense_situation_tag
        FOREIGN KEY (situation_tag_id)
            REFERENCES situation_tag (situation_tag_id)
);

-- =========================================================
-- REVIEW CHOICE OPTION
-- =========================================================
CREATE TABLE review_choice_option
(
    review_choice_option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_type           VARCHAR(50)  NOT NULL,
    option_text             VARCHAR(255) NOT NULL,
    option_value            VARCHAR(100) NOT NULL,
    score                   INT,
    sort_order              INT          NOT NULL DEFAULT 0,
    is_active               BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              DATETIME     NOT NULL,
    updated_at              DATETIME     NOT NULL
);


-- =========================================================
-- REVIEW PHRASE GROUP MAPPING
-- 원본 선택값 -> 내부 그룹값 매핑
-- =========================================================
CREATE TABLE review_phrase_group_mapping
(
    review_phrase_group_mapping_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_type                    VARCHAR(50) NOT NULL,
    source_id                      BIGINT      NOT NULL,
    group_code                     VARCHAR(50) NOT NULL,
    created_at                     DATETIME    NOT NULL,
    updated_at                     DATETIME    NOT NULL,

    CONSTRAINT uq_review_phrase_group_mapping
        UNIQUE (source_type, source_id)
);

-- =========================================================
-- REVIEW TITLE PHRASE
-- 결과 제목: 상황 그룹 + 만족도 그룹
-- =========================================================
CREATE TABLE review_title_phrase
(
    review_title_phrase_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    situation_group_code     VARCHAR(50)  NOT NULL,
    satisfaction_group_code  VARCHAR(50)  NOT NULL,

    title_prefix_text        VARCHAR(150) NOT NULL,
    title_highlight_text     VARCHAR(50)  NOT NULL,
    title_suffix_text        VARCHAR(150) NOT NULL,

    created_at               DATETIME     NOT NULL,
    updated_at               DATETIME     NOT NULL,

    CONSTRAINT uq_review_title_phrase
        UNIQUE (situation_group_code, satisfaction_group_code)
);

-- =========================================================
-- REVIEW FEEDBACK TITLE PHRASE
-- 피드백 제목: 상황 그룹
-- =========================================================
CREATE TABLE review_feedback_title_phrase
(
    review_feedback_title_phrase_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    situation_group_code            VARCHAR(50)  NOT NULL,
    feedback_title                  VARCHAR(255) NOT NULL,
    created_at                      DATETIME     NOT NULL,
    updated_at                      DATETIME     NOT NULL,

    CONSTRAINT uq_review_feedback_title_phrase
        UNIQUE (situation_group_code)
);

-- =========================================================
-- REVIEW FEEDBACK PHRASE
-- 피드백 내용: 상황 그룹 + 만족도 그룹
-- =========================================================
CREATE TABLE review_feedback_phrase
(
    review_feedback_phrase_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    situation_group_code      VARCHAR(50) NOT NULL,
    satisfaction_group_code   VARCHAR(50) NOT NULL,
    feedback_text             TEXT        NOT NULL,
    created_at                DATETIME    NOT NULL,
    updated_at                DATETIME    NOT NULL,

    CONSTRAINT uq_review_feedback_phrase
        UNIQUE (situation_group_code, satisfaction_group_code)
);

-- =========================================================
-- REVIEW EMOTION ADJUSTMENT PHRASE
-- 감정 보정 문장: 감정 그룹
-- =========================================================
CREATE TABLE review_emotion_adjustment_phrase
(
    review_emotion_adjustment_phrase_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emotion_group_code                  VARCHAR(50) NOT NULL,
    adjustment_text                     TEXT        NOT NULL,
    created_at                          DATETIME    NOT NULL,
    updated_at                          DATETIME    NOT NULL,

    CONSTRAINT uq_review_emotion_adjustment_phrase
        UNIQUE (emotion_group_code)
);

-- =========================================================
-- REVIEW GUIDE PHRASE
-- 가이드: 내일 다짐 그룹
-- =========================================================
CREATE TABLE review_guide_phrase
(
    review_guide_phrase_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    next_action_group_code VARCHAR(50)  NOT NULL,
    guide_title            VARCHAR(255) NOT NULL,
    guide_item_1           VARCHAR(255) NOT NULL,
    guide_item_2           VARCHAR(255) NOT NULL,
    guide_item_3           VARCHAR(255) NOT NULL,
    created_at             DATETIME     NOT NULL,
    updated_at             DATETIME     NOT NULL,

    CONSTRAINT uq_review_guide_phrase
        UNIQUE (next_action_group_code)
);

-- =========================================================
-- REVIEW
-- 특정 날짜의 설문 기반 회고
-- expense, income, asset과 직접 연결하지 않음
-- 설문에 대한 결과가 아닌 설문 값만 저장 후 조회할 때 해당 정보 통해서 조회
-- =========================================================
CREATE TABLE review
(
    review_id              BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id                BIGINT NOT NULL,
    review_date            DATE   NOT NULL,

    emotion_id             BIGINT NOT NULL,
    situation_tag_id       BIGINT NOT NULL,
    satisfaction_option_id BIGINT NOT NULL,
    next_action_option_id  BIGINT NOT NULL,

    created_at             DATETIME NOT NULL,
    updated_at             DATETIME NOT NULL,

    -- 사용자 + 날짜 기준 1개만 허용 (덮어쓰기 가능)
    CONSTRAINT uq_review_user_date
        UNIQUE (user_id, review_date),

    -- FK
    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id),

    CONSTRAINT fk_review_emotion
        FOREIGN KEY (emotion_id)
            REFERENCES emotion (emotion_id),

    CONSTRAINT fk_review_situation
        FOREIGN KEY (situation_tag_id)
            REFERENCES situation_tag (situation_tag_id),

    CONSTRAINT fk_review_satisfaction
        FOREIGN KEY (satisfaction_option_id)
            REFERENCES review_choice_option (review_choice_option_id),

    CONSTRAINT fk_review_next_action
        FOREIGN KEY (next_action_option_id)
            REFERENCES review_choice_option (review_choice_option_id)
);

-- =========================================================
-- NOTIFICATIONS
-- 사용자에게 도착한 인앱 알림 (FCM 발송과 별개로 알림함 저장)
-- users 1 : N notifications
-- =========================================================
CREATE TABLE IF NOT EXISTS notifications
(
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    type            VARCHAR(30)  NOT NULL,
    body            VARCHAR(255) NOT NULL,
    is_read         BOOLEAN      NOT NULL DEFAULT FALSE,
    read_at         DATETIME,
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NOT NULL,
    deleted_at      DATETIME,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
);