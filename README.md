## 프로젝트 소개

Spring Boot 기반의 결제 서비스 멀티 모듈 프로젝트입니다. 공통 모듈과 결제 모듈을 분리하여 관리하며, AWS 기반 클라우드 환경을 지원합니다.

## 기술 스택

- Backend
    - Java 17 (Amazon Corretto)
    - Spring Boot 3.1.3
    - Spring Security
    - Spring Data JPA
    - Spring Data Redis

- Database
    - H2 Database
    - Redis

- Authentication
    - JWT
    - Session

- Payment
    - Toss Payments API

- Build/Deploy
    - Gradle
    - AWS CodeBuild

## 프로젝트 구조

```
프로젝트명/
├── .gradle/                
├── .idea/                  
├── config/                 
├── gradle/                 
├── logs/                   
├── module-common/          # 공통 모듈
│   ├── src/
│   └── build.gradle
├── module-pay/            # 결제 모듈
│   ├── src/
│   └── build.gradle
├── .gitignore            
├── build.gradle          
├── buildspec.yml         
├── gradlew              
├── gradlew.bat          
└── settings.gradle
```

## API 명세

API 문서는 Postman을 통해 확인하실 수 있습니다.

[![Run in Postman](https://run.pstmn.io/button.svg)](https://www.postman.com/seungkyu-6610/account/overview)

API 문서 접근 방법

1. 위 Postman 버튼을 클릭하여 워크스페이스로 이동
2. Collections에서 각 API 엔드포인트 확인
3. 요청/응답 예시 및 스키마 확인 가능
4. Environment 설정을 통해 개발/운영 환경 구분 가능

## 주요 기능

### 인증 기능

- JWT/세션 기반 이중 인증 지원
- Auth-Type 헤더를 통한 인증 방식 선택
- 비밀번호 암호화 저장
- 토큰 기반 인증 상태 관리

### 결제 기능

- Toss Payments 연동
- 결제 검증 및 확인
- 결제 상태 실시간 조회
- 트랜잭션 로깅

### 모니터링

- MDC 기반 트랜잭션 추적
- 요청/응답 시간 측정
- 상세 에러 로깅
- 프로파일별 설정 모니터링

## 모듈 구성

### module-common

- 공통 유틸리티 클래스
- 보안 설정
- 공통 예외 처리
- 기본 엔티티 클래스
- 공통 인터페이스

### module-pay

- 결제 처리 로직
- Toss Payments 연동
- 결제 상태 관리
- 결제 관련 엔티티
- 결제 이벤트 처리

## 품질 관리

### 코드 품질

- Checkstyle을 통한 코드 스타일 검사
- SonarQube를 통한 정적 코드 분석

### 모니터링

- 트랜잭션 ID 기반 로그 추적
- MDC를 통한 분산 로깅
- 결제 처리 시간 모니터링
- 에러 발생 추적

## 배포 프로세스

### AWS CodeBuild

1. GitHub 저장소 연동
2. buildspec.yml 기반 빌드 설정
3. 자동 테스트 실행
4. JAR 파일 생성
5. AWS S3 아티팩트 업로드
6. AWS CodeDeploy를 통한 EC2 배포

### 배포 환경 구성

- EC2 인스턴스 설정
- CodeDeploy 에이전트 설치
- IAM 역할 및 권한 설정
- CloudWatch 모니터링
