# 🌊 FeelLog — 감정 소비 기록 앱 백엔드

> 소비할 때 어떤 감정이었는지 기록하고, 회고를 통해 소비 습관을 돌아보는 서비스

![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-6DB33F?style=flat-square&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase_FCM-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)

---

## 📌 프로젝트 소개

**FeelLog**는 소비 기록에 *감정*과 *상황 태그*를 함께 남기고, 날짜별 회고를 작성하며 나의 소비 패턴을 감정적으로 돌아볼 수 있는 앱입니다.

단순한 가계부를 넘어, "왜 이 돈을 썼는지"를 기록하는 것에 초점을 맞췄습니다.

---

## ✨ 주요 기능

| 기능 | 설명 |
|------|------|
| 소셜 로그인 | 카카오 / 구글 OAuth2 로그인, 게스트 모드 지원 |
| 소비 기록 | 카테고리·결제수단·감정·상황 태그를 포함한 소비 CRUD |
| 수입 기록 | 수입 카테고리별 월간/일별 수입 관리 |
| 자산 관리 | 자산 카테고리별 등록·조회·수정·삭제 |
| 날짜별 회고 | 선택지 기반 일일 회고 작성 및 결과 메시지 생성 |
| 리포트 | 일별·주별·월별 소비·감정 통계 리포트 |
| 푸시 알림 | Firebase FCM 기반 지출 유도(19시) / 회고 유도(21시) 자동 발송 |
| 마스터 데이터 | 감정·카테고리·결제수단·상황 태그 전체 조회 |

---

## 🛠 기술 스택

**Language & Framework**
- Java 21, Spring Boot 4.0.5
- Spring Security + JWT (Access / Refresh Token)
- Spring Data JPA + MySQL 8.0

**인증**
- Kakao / Google 소셜 로그인 연동
- 프론트엔드에서 전달받은 소셜 Access Token을 Kakao·Google API로 검증 후 자체 JWT 발급
- JWT (`jjwt 0.12.6`)

**알림**
- Firebase Admin SDK (FCM 푸시)
- Spring Scheduler (cron 기반 자동 발송)

**문서 & 도구**
- Springdoc OpenAPI (Swagger UI)
- Docker / Docker Compose
- Gradle

---

## 📁 패키지 구조

```
src/main/java/com/feellog/backend/
├── BackendApplication.java
├── global/
│   ├── config/          # Security, CORS, Firebase, Swagger 설정
│   ├── jwt/             # JwtProvider
│   ├── security/        # JwtAuthenticationFilter
│   ├── oauth2/          # Google / Kakao 클라이언트
│   ├── scheduler/       # 푸시 알림 스케줄러
│   ├── exception/       # BusinessException, ErrorCode, GlobalExceptionHandler
│   └── common/          # BaseTimeEntity
└── domain/
    ├── user/            # 인증, 회원 관리
    ├── expense/         # 소비 기록
    ├── income/          # 수입 기록
    ├── asset/           # 자산 관리
    ├── review/          # 일일 회고
    ├── report/          # 리포트 (일별/주별/월별)
    ├── notification/    # FCM 디바이스 토큰, 푸시 알림
    ├── master/          # 마스터 데이터 통합 조회
    ├── emotion/         # 감정 / 감정 그룹
    ├── category/        # 소비 카테고리 / 그룹
    ├── situationtag/    # 상황 태그
    └── paymentmethod/   # 결제 수단
```

---

## 🔗 API 엔드포인트 요약

모든 API는 `/api/v1` prefix를 사용합니다. 인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더가 필요합니다.

<details>
<summary><b>Auth — /api/v1/auth</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/kakao` | 카카오 소셜 로그인 |
| POST | `/google` | 구글 소셜 로그인 |
| POST | `/guest` | 게스트 로그인 |
| POST | `/refresh` | Access Token 재발급 |
| POST | `/logout` | 로그아웃 (Refresh Token 삭제) |

</details>

<details>
<summary><b>User — /api/v1/users</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/me` | 내 정보 조회 |
| PUT | `/me` | 내 정보 수정 |
| DELETE | `/me` | 회원 탈퇴 |

</details>

<details>
<summary><b>Expense — /api/v1/expenses</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/` | 소비 기록 생성 |
| GET | `/{expenseId}` | 소비 기록 단건 조회 |
| GET | `/daily` | 일별 소비 목록 |
| GET | `/monthly` | 월별 소비 목록 |
| GET | `/category/{categoryId}` | 카테고리별 소비 목록 |
| GET | `/category_group/{groupId}` | 카테고리 그룹별 소비 목록 |
| GET | `/emotion/{emotionId}` | 감정별 소비 목록 |
| GET | `/emotion_group/{groupId}` | 감정 그룹별 소비 목록 |
| GET | `/situation_tag/{situationTagId}` | 상황 태그별 소비 목록 |
| PUT | `/{expenseId}` | 소비 기록 수정 |
| DELETE | `/{expenseId}` | 소비 기록 삭제 |

</details>

<details>
<summary><b>Income — /api/v1/incomes</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/` | 수입 기록 생성 |
| GET | `/{incomeId}` | 수입 기록 단건 조회 |
| GET | `/daily` | 일별 수입 목록 |
| GET | `/monthly` | 월별 수입 목록 |
| GET | `/category/{categoryId}` | 카테고리별 수입 목록 |
| PUT | `/{incomeId}` | 수입 기록 수정 |
| DELETE | `/{incomeId}` | 수입 기록 삭제 |

</details>

<details>
<summary><b>Asset — /api/v1/assets</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/categories` | 자산 카테고리 목록 |
| POST | `/` | 자산 생성 |
| GET | `/summary` | 자산 전체 요약 |
| GET | `/` | 자산 목록 |
| GET | `/{assetId}` | 자산 단건 조회 |
| PATCH | `/{assetId}` | 자산 수정 |
| DELETE | `/{assetId}` | 자산 삭제 |

</details>

<details>
<summary><b>Review — /api/v1/reviews</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/options` | 회고 선택지 목록 조회 |
| PUT | `/{reviewDate}` | 회고 작성 / 수정 (upsert) |
| GET | `/{reviewDate}` | 날짜별 회고 조회 |

</details>

<details>
<summary><b>Report — /api/v1/reports</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/daily` | 일별 리포트 |
| GET | `/weekly` | 주별 리포트 |
| GET | `/monthly` | 월별 리포트 |
| GET | `/monthly/expenses` | 월별 지출 상세 |
| GET | `/categories/{categoryId}/expenses` | 카테고리별 지출 상세 |
| GET | `/emotions/{emotionId}/expenses` | 감정별 지출 상세 |

</details>

<details>
<summary><b>Device Token — /api/v1/device-tokens</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/` | FCM 디바이스 토큰 등록 |
| DELETE | `/` | FCM 디바이스 토큰 삭제 |

</details>

<details>
<summary><b>Notification — /api/v1/notifications</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/test` | 테스트 푸시 발송 |

</details>

<details>
<summary><b>Master Data — /api/v1/master-data</b></summary>

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/` | 감정·카테고리·결제수단·상황 태그 전체 조회 |

</details>

---

## ⚙️ 로컬 실행 방법

### 1. 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성합니다.

```env
# MySQL
MYSQL_ROOT_PASSWORD=root_password
MYSQL_DATABASE=feellog
MYSQL_USER=feellog_user
MYSQL_PASSWORD=your_password
MYSQL_PORT=3306
MYSQL_TZ=Asia/Seoul

# JWT
JWT_SECRET=your_jwt_secret_key

# OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
KAKAO_APP_ID=your_kakao_app_id

# Firebase
# 로컬: 프로젝트 루트 기준 상대경로 또는 절대경로
# 예) firebase-service-account.json / /home/ubuntu/feellog/firebase-service-account.json
FIREBASE_CREDENTIAL_PATH=firebase-service-account.json
```

> Firebase 서비스 계정 키(`firebase-service-account.json`)는 Firebase 콘솔에서 발급합니다.
> 로컬에서는 프로젝트 루트에 파일을 두고 상대경로를 사용하면 동작하지만, 배포 환경에서는 절대경로를 권장합니다.

> ⚠️ **보안 주의사항**
> - `.env` 파일과 `firebase-service-account.json`은 절대 Git에 커밋하지 않습니다 (`.gitignore`에 추가 필수)
> - 배포 환경에서는 환경변수 또는 서버 내부 파일 경로로 관리합니다
> - `JWT_SECRET`은 충분히 긴 랜덤 문자열을 사용합니다

### 2. DB 실행 (Docker)

```bash
docker compose up -d
```

초기화가 필요한 경우 (데이터 전체 삭제):

```bash
docker compose down -v
docker compose up -d
```

### 3. Spring Boot 실행

```bash
./gradlew bootRun
```

### 4. Swagger UI 접속

- Local: `http://localhost:8080/swagger-ui/index.html`
- Production: `https://api.feellog.xyz/swagger-ui/index.html`

---

## 🗄 DB 테이블 구조

| 영역 | 테이블 |
|------|--------|
| 사용자 / 인증 | `users`, `refresh_tokens`, `notification_settings`, `device_tokens` |
| 소비 | `category_group`, `category`, `payment_method`, `expense`, `expense_emotion`, `expense_situation_tag` |
| 수입 | `income_category`, `income` |
| 자산 | `asset_category`, `asset` |
| 감정 / 상황 | `emotion_group`, `emotion`, `situation_tag` |
| 회고 | `review_choice_option`, `review_phrase_group_mapping`, `review_title_phrase`, `review_feedback_title_phrase`, `review_feedback_phrase`, `review_emotion_adjustment_phrase`, `review_guide_phrase`, `review` |

초기 스키마 및 데이터는 `db/init/` 하위 SQL 파일에서 관리합니다.

---

## 🔔 푸시 알림 스케줄

Spring Scheduler로 매일 자동 발송됩니다.

| 시각 | 내용 |
|------|------|
| 오후 7시 (19:00 KST) | 지출 기록 유도 알림 |
| 오후 9시 (21:00 KST) | 일일 회고 유도 알림 |
