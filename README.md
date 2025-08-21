# SafeView Backend

CCTV 영상 관리 및 복호화 시스템의 백엔드 애플리케이션입니다.

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [주요 기능](#주요-기능)
- [API 문서](#api-문서)
- [설치 및 실행](#설치-및-실행)
- [환경 설정](#환경-설정)
- [데이터베이스](#데이터베이스)
- [보안](#보안)
- [개발 가이드](#개발-가이드)

## 🎯 프로젝트 개요

SafeView는 CCTV 영상을 안전하게 관리하고 복호화하는 시스템입니다. 블록체인 기술을 활용하여 키의 무결성을 보장하고, JWT 기반 인증으로 보안을 강화했습니다.

### 주요 특징

- 🔐 **JWT 기반 인증**: Access Token과 Refresh Token을 통한 안전한 인증
- 🏗️ **블록체인 연동**: 복호화 키의 무결성과 추적성 보장
- 📹 **CCTV 영상 관리**: 영상 녹화, 저장, 다운로드 기능
- 👥 **역할 기반 접근 제어**: USER, MODERATOR, ADMIN 권한 관리
- 🔑 **복호화 키 관리**: 키 발급, 검증, 취소 기능

## 🛠️ 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database** (개발용)
- **Gradle**

### Security & Authentication
- **JWT (JSON Web Token)**
- **BCrypt** (비밀번호 암호화)
- **Spring Security**

### Blockchain Integration
- **Web3j** (Ethereum 클라이언트)
- **Solidity** (스마트 컨트랙트)
- **Sepolia Testnet**

### External Services
- **AI Server** (영상 처리)
- **S3 Storage** (영상 저장)

## 📁 프로젝트 구조

```
src/main/java/com/safeview/
├── domain/                          # 도메인별 패키지
│   ├── administrator/               # 관리자 기능
│   │   ├── controller/             # 관리자 API 컨트롤러
│   │   ├── dto/                    # 관리자 관련 DTO
│   │   ├── entity/                 # 관리자 요청 엔티티
│   │   ├── mapper/                 # DTO-Entity 변환
│   │   ├── repository/             # 데이터 접근 계층
│   │   └── service/                # 비즈니스 로직
│   ├── auth/                       # 인증 관련
│   │   ├── controller/             # 인증 API 컨트롤러
│   │   ├── dto/                    # 인증 관련 DTO
│   │   ├── mapper/                 # 인증 매퍼
│   │   └── service/                # 인증 서비스
│   ├── decryption/                 # 복호화 기능
│   │   ├── config/                 # 복호화 설정
│   │   ├── controller/             # 복호화 API 컨트롤러
│   │   ├── dto/                    # 복호화 관련 DTO
│   │   ├── entity/                 # 복호화 키 엔티티
│   │   ├── mapper/                 # 복호화 매퍼
│   │   ├── repository/             # 복호화 데이터 접근
│   │   └── service/                # 복호화 서비스
│   ├── user/                       # 사용자 관리
│   │   ├── controller/             # 사용자 API 컨트롤러
│   │   ├── dto/                    # 사용자 관련 DTO
│   │   ├── entity/                 # 사용자 엔티티
│   │   ├── mapper/                 # 사용자 매퍼
│   │   ├── repository/             # 사용자 데이터 접근
│   │   └── service/                # 사용자 서비스
│   └── video/                      # 영상 관리
│       ├── controller/             # 영상 API 컨트롤러
│       ├── dto/                    # 영상 관련 DTO
│       ├── entity/                 # 영상 엔티티
│       ├── mapper/                 # 영상 매퍼
│       ├── repository/             # 영상 데이터 접근
│       └── service/                # 영상 서비스
└── global/                         # 전역 설정 및 공통 기능
    ├── config/                     # 설정 클래스들
    ├── entity/                     # 기본 엔티티
    ├── exception/                  # 예외 처리
    ├── response/                   # 응답 형식
    └── security/                   # 보안 관련
```

## 🚀 주요 기능

### 1. 사용자 관리
- **회원가입**: 이메일, 비밀번호, 개인정보를 통한 회원가입
- **로그인/로그아웃**: JWT 토큰 기반 인증
- **이메일 중복 확인**: 회원가입 시 이메일 중복 검증
- **역할 관리**: USER, MODERATOR, ADMIN 권한 체계

### 2. 인증 및 보안
- **JWT 토큰 관리**: Access Token (1시간), Refresh Token (7일)
- **HttpOnly 쿠키**: XSS 공격 방지를 위한 안전한 토큰 저장
- **비밀번호 암호화**: BCrypt를 통한 안전한 비밀번호 저장
- **권한별 접근 제어**: API별 권한 검증

### 3. 관리자 기능
- **권한 요청 관리**: 사용자의 관리자 권한 요청 처리
- **전체 사용자 관리**: 모든 사용자 정보 조회 및 관리
- **요청 승인/거절**: 관리자 권한 요청에 대한 처리

### 4. 복호화 키 관리
- **키 발급**: CCTV 영상 복호화를 위한 키 생성
- **키 검증**: 발급된 키의 유효성 검증
- **키 취소**: 만료되거나 보안상 취소가 필요한 키 관리
- **블록체인 연동**: 키 정보의 무결성 보장

### 5. 영상 관리
- **영상 녹화**: AI 서버와 연동한 영상 녹화 시작/중지
- **영상 저장**: S3 스토리지를 통한 안전한 영상 저장
- **영상 목록**: 사용자별 영상 목록 조회
- **영상 다운로드**: 저장된 영상 다운로드 기능

## 📚 API 문서

### 인증 API
- `POST /api/auth/login` - 로그인
- `POST /api/auth/logout` - 로그아웃
- `GET /api/auth/me` - 현재 사용자 정보 조회

### 사용자 API
- `POST /api/users/signup` - 회원가입
- `GET /api/users/check-email` - 이메일 중복 확인

### 관리자 API
- `POST /api/admin/requests` - 관리자 권한 요청 생성
- `GET /api/admin/requests` - 관리자 권한 요청 목록 조회
- `PUT /api/admin/requests/{id}` - 관리자 권한 요청 처리

### 복호화 API
- `POST /api/decryption/keys/issue` - 복호화 키 발급
- `POST /api/decryption/keys/verify` - 복호화 키 검증
- `DELETE /api/decryption/keys/revoke` - 복호화 키 취소
- `GET /api/decryption/keys` - 복호화 키 목록 조회

### 영상 API
- `POST /api/videos/start` - 영상 녹화 시작
- `POST /api/videos/stop` - 영상 녹화 중지
- `GET /api/videos/all` - 사용자별 영상 목록 조회
- `GET /api/videos/download/{filename}` - 영상 다운로드

### 블록체인 API
- `GET /api/blockchain/status` - 블록체인 연결 상태 확인
- `GET /api/blockchain/balance` - 계정 잔액 조회
- `POST /api/blockchain/keys/register` - 키 블록체인 등록

## 🛠️ 설치 및 실행

### 필수 요구사항
- Java 17 이상
- Gradle 7.x 이상
- H2 Database (개발용)

### 1. 프로젝트 클론
```bash
git clone [repository-url]
cd Backend
```

### 2. 의존성 설치
```bash
./gradlew build
```

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 4. 개발 서버 접속
- **애플리케이션**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console

## ⚙️ 환경 설정

### application.yml 설정

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
  h2:
    console:
      enabled: true

jwt:
  secret: your-jwt-secret-key-here
  expiration: 3600000  # 1시간 (ms)
  refresh-expiration: 604800000  # 7일 (ms)

cctv:
  decryption:
    key-type: CCTV_AES256
    max-uses: 10
    expiration-hours: 24

blockchain:
  network-url: https://sepolia.infura.io/v3/your-project-id
  contract-address: your-contract-address
  private-key: your-private-key

ai:
  server:
    url: http://localhost:5000

api:
  internal:
    ai-server-key: your-ai-server-api-key
```

## 🗄️ 데이터베이스

### 주요 테이블

#### users
- 사용자 정보 저장
- 이메일, 비밀번호, 개인정보, 역할 포함

#### admin_requests
- 관리자 권한 요청 정보
- 요청자, 제목, 설명, 상태, 처리 정보 포함

#### decryption_keys
- 복호화 키 정보
- 키 해시, 사용자 ID, 만료 시간, 사용 횟수 포함

#### blockchain_transactions
- 블록체인 트랜잭션 정보
- 트랜잭션 해시, 키 해시, 상태 정보 포함

#### videos
- 영상 정보
- 파일명, S3 URL, 사용자 ID 포함

## 🔒 보안

### 인증 및 권한
- **JWT 토큰**: Access Token과 Refresh Token 분리
- **HttpOnly 쿠키**: XSS 공격 방지
- **BCrypt 암호화**: 비밀번호 안전한 저장
- **역할 기반 접근 제어**: API별 권한 검증

### 데이터 보안
- **블록체인 검증**: 키 무결성 보장
- **API 키 검증**: AI 서버 통신 보안
- **입력값 검증**: 모든 사용자 입력 검증

### 네트워크 보안
- **CORS 설정**: 프론트엔드와의 안전한 통신
- **HTTPS**: 프로덕션 환경에서 HTTPS 사용 권장

## 👨‍💻 개발 가이드

### 코드 컨벤션
- **패키지 구조**: 도메인별 패키지 분리
- **주석**: 모든 클래스와 메서드에 주석 작성
- **예외 처리**: 일관된 예외 처리 및 응답 형식

### 개발 환경 설정
1. **IDE 설정**: IntelliJ IDEA 또는 Eclipse 권장
2. **코드 포맷팅**: Google Java Style Guide 준수

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


---

**SafeView Backend** - 안전한 CCTV 영상 관리 시스템
