# feel-log-backend
FeelLog 백엔드 API 서버 (Spring Boot)

## 1. 프로젝트 개요

FeelLog는 사용자의 소비 기록과 감정, 상황 태그를 함께 저장하고,
날짜별 회고를 통해 소비 패턴을 돌아볼 수 있도록 돕는 서비스입니다.

현재 MVP 기준 주요 기능은 다음과 같습니다.

- 소셜 로그인 기반 사용자 관리
- 소비 기록 관리
- 수입 기록 관리
- 자산 기록 관리
- 감정/상황 태그 관리
- 날짜별 회고 작성 및 조회
- 푸시 알림 설정 및 디바이스 토큰 관리

## 2. 기술 스택

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL 8.0
- Docker / Docker Compose
- Gradle

## 3. DB 구조 요약

### 사용자 / 인증

- `users`
- `refresh_tokens`
- `notification_settings`
- `device_tokens`

### 소비

- `category_group`
- `category`
- `payment_method`
- `expense`
- `expense_emotion`
- `expense_situation_tag`

### 수입

- `income_category`
- `income`

### 자산

- `asset_category`
- `asset`

### 감정 / 상황

- `emotion_group`
- `emotion`
- `situation_tag`

### 회고

- `review_choice_option`
- `review_result_template`
- `review`

## 4. 로컬 실행 방법
### 1) Docker DB 실행

    docker compose up -d
### 2) DB 초기화 (필요한 경우)

    docker compose down -v

    docker compose up -d

⚠️ down -v는 MySQL 볼륨을 삭제하므로 기존 데이터가 모두 사라집니다.

### 3) MySQL 접속
    docker exec -it feellog-mysql mysql -u root -p --default-character-set=utf8mb4

비밀번호는 .env에 설정한 MYSQL_ROOT_PASSWORD를 사용합니다.

### 4) DB 선택
    USE feellog;

## 6. SQL 적용

초기 세팅 시 아래 순서로 실행합니다.
1. schema.sql
2. data.sql

## 7. Spring Boot 실행
    ./gradlew bootRun
