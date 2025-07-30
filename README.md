# SafeView Backend

CCTV 복호화 키 관리 시스템의 백엔드 애플리케이션입니다.

## 🚀 주요 기능

- CCTV 복호화 키 발급 및 관리
- 블록체인 기반 키 무결성 검증
- JWT 기반 사용자 인증
- 30일간 재사용 가능한 키 시스템

## 📋 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수들을 설정하세요:

```bash
# ===== 데이터베이스 설정 =====
DB_USERNAME=safeview_user
DB_PASSWORD=safeview_password123

# ===== JWT 설정 =====
JWT_SECRET=your_super_secret_jwt_key_for_safeview_application_2024

# ===== 블록체인 설정 =====
# Infura 프로젝트 ID (실제 운영 시 변경 필요)
BLOCKCHAIN_RPC_URL=https://mainnet.infura.io/v3/YOUR_ACTUAL_PROJECT_ID
# 스마트 컨트랙트 주소 (실제 배포 후 변경 필요)
BLOCKCHAIN_CONTRACT_ADDRESS=0x1234567890123456789012345678901234567890
# 블록체인 개인키 (실제 운영 시 변경 필요)
BLOCKCHAIN_PRIVATE_KEY=0x0000000000000000000000000000000000000000000000000000000000000001

# ===== 애플리케이션 설정 =====
# 서버 포트
SERVER_PORT=8080
# JWT 만료 시간 (밀리초)
JWT_EXPIRATION=36000000

# ===== CCTV 복호화 설정 =====
# 키 만료 일수
CCTV_KEY_EXPIRATION_DAYS=30
# 기본 사용 횟수
CCTV_DEFAULT_USES=90
# 블록체인 활성화 여부 (true/false)
BLOCKCHAIN_ENABLED=true
```

## 🛠️ 실행 방법

### 1. 데이터베이스 설정
```sql
CREATE DATABASE safeview;
CREATE USER 'safeview_user'@'localhost' IDENTIFIED BY 'safeview_password123';
GRANT ALL PRIVILEGES ON safeview.* TO 'safeview_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

## 📡 API 엔드포인트

### 키 발급
```
POST /api/decryption/keys
Authorization: Bearer {JWT_TOKEN}
```

### 키 검증
```
POST /api/decryption/keys/verify
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
    "accessToken": "발급받은_접근_토큰"
}
```

### 키 목록 조회
```
GET /api/decryption/keys?userId={userId}&page=0&size=10
Authorization: Bearer {JWT_TOKEN}
```

## 🔧 기술 스택

- **Spring Boot 3.5.3**
- **Spring Security**
- **Spring Data JPA**
- **MySQL**
- **Web3j** (블록체인 연동)
- **Gradle**

## 📝 주요 특징

- **30일간 재사용 가능한 키**: 같은 사용자가 30일 이내에 재요청 시 기존 키 반환
- **블록체인 무결성**: 키 해시를 블록체인에 등록하여 무결성 보장
- **간소화된 인증**: 토큰 기반의 간단한 인증 시스템
- **환경 변수 관리**: 다양한 환경에서 유연한 설정 가능
