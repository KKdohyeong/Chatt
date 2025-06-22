# DevView - 개발자를 위한 기술 면접 플랫폼

<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring Boot-3.4.3-green?style=for-the-badge&logo=spring-boot" alt="Spring Boot 3.4.3">
  <img src="https://img.shields.io/badge/Spring Security-6.0-blue?style=for-the-badge&logo=spring-security" alt="Spring Security">
  <img src="https://img.shields.io/badge/MySQL-8.4.0-blue?style=for-the-badge&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/OpenAI-GPT-412991?style=for-the-badge&logo=openai" alt="OpenAI GPT">
</div>

## 📋 목차

- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)

## 🎯 프로젝트 소개

DevView는 개발자를 위한 AI 기반 기술 면접 플랫폼입니다. CS 질문과 이력서 기반 면접을 제공하며, 텍스트와 음성 기반 면접을 모두 지원합니다.

### 핵심 특징

- 🤖 **AI 기반 면접**: OpenAI GPT를 활용한 지능형 면접 진행
- 📝 **이력서 분석**: 업로드된 이력서를 AI가 분석하여 맞춤형 질문 생성
- 💬 **실시간 대화**: 자연스러운 대화형 면접 진행
- 📊 **상세 평가**: 면접 결과에 대한 AI 기반 종합 평가 제공
- 🔄 **꼬리 질문**: 답변에 따른 심화 질문 자동 생성

### 주의사항 : 현재 이력서 분석 시간이 1~2분정도 소요됩니다.


## ✨ 주요 기능

### 1. CS 기술 면접
- **다양한 분야**: 알고리즘, 데이터베이스, 네트워크, 운영체제 등
- **단계별 진행**: 기본 질문 → 답변 → 꼬리 질문 → 평가
- **실시간 피드백**: 답변에 대한 즉시 평가 및 개선점 제시

### 2. 이력서 기반 면접
- **이력서 업로드**: 텍스트 형태의 이력서 내용 입력
- **AI 분석**: 이력서 내용을 분석하여 키워드 추출
- **맞춤형 질문**: 추출된 키워드 기반으로 개인화된 질문 생성
- **프로젝트 심화**: 경험한 프로젝트에 대한 심층 질문

### 3. 면접 모드
- **텍스트 기반**: 채팅 형태의 텍스트 면접
- **음성 기반**: 음성 인식을 통한 음성 면접 (준비 중)

### 4. 사용자 관리
- **회원가입/로그인**: JWT 기반 인증 시스템
- **면접 기록**: 과거 면접 결과 및 평가 저장
- **북마크**: 중요 질문 북마크 기능

## 🛠 기술 스택

### Backend
- **Java 21**: 최신 Java 버전 사용
- **Spring Boot 3.4.3**: 웹 애플리케이션 프레임워크
- **Spring Security 6**: 보안 및 인증
- **Spring Data JPA**: 데이터 접근 계층
- **Hibernate**: ORM 프레임워크
- **MySQL 8.4.0**: 메인 데이터베이스
- **H2 Database**: 개발용 인메모리 데이터베이스

### AI & External Services
- **OpenAI GPT**: AI 기반 질문 생성 및 평가
- **JWT**: 토큰 기반 인증

### Frontend
- **Thymeleaf**: 서버 사이드 템플릿 엔진
- **HTML/CSS/JavaScript**: 클라이언트 사이드

### DevOps
- **Docker**: 컨테이너화
- **Docker Compose**: 멀티 컨테이너 관리
- **Gradle**: 빌드 도구

## 📁 프로젝트 구조

```
DevView/
├── src/main/java/project/DevView/cat_service/
│   ├── ai/service/                    # AI 서비스
│   │   ├── ChatGptService.java       # GPT API 연동
│   │   └── ResumeAIService.java      # 이력서 분석 AI
│   ├── global/                       # 공통 설정
│   │   ├── config/                   # 설정 클래스
│   │   ├── controller/               # 공통 컨트롤러
│   │   ├── dto/                      # 공통 DTO
│   │   ├── entity/                   # 공통 엔티티
│   │   ├── exception/                # 예외 처리
│   │   ├── jwt/                      # JWT 관련
│   │   └── service/                  # 공통 서비스
│   ├── interview/                    # 면접 관련
│   │   ├── controller/               # 면접 컨트롤러
│   │   ├── dto/                      # 면접 DTO
│   │   ├── entity/                   # 면접 엔티티
│   │   ├── mapper/                   # 매퍼
│   │   ├── repository/               # 면접 리포지토리
│   │   └── service/                  # 면접 서비스
│   ├── question/                     # 질문 관련
│   │   ├── controller/               # 질문 컨트롤러
│   │   ├── dto/                      # 질문 DTO
│   │   ├── entity/                   # 질문 엔티티
│   │   ├── mapper/                   # 매퍼
│   │   ├── repository/               # 질문 리포지토리
│   │   └── service/                  # 질문 서비스
│   ├── resume/                       # 이력서 관련
│   │   ├── controller/               # 이력서 컨트롤러
│   │   ├── dto/                      # 이력서 DTO
│   │   ├── entity/                   # 이력서 엔티티
│   │   ├── repository/               # 이력서 리포지토리
│   │   └── service/                  # 이력서 서비스
│   └── user/                         # 사용자 관련
│       ├── controller/               # 사용자 컨트롤러
│       ├── dto/                      # 사용자 DTO
│       ├── entity/                   # 사용자 엔티티
│       ├── repository/               # 사용자 리포지토리
│       └── service/                  # 사용자 서비스
├── src/main/resources/
│   ├── templates/                    # Thymeleaf 템플릿
│   ├── static/                       # 정적 리소스
│   ├── application.properties        # 기본 설정
│   └── application.yml               # YAML 설정
├── build.gradle.kts                  # Gradle 빌드 설정
├── Dockerfile                        # Docker 이미지 설정
└── docker-compose.yml                # Docker Compose 설정
```

### 5. 접속

애플리케이션이 실행되면 다음 URL로 접속할 수 있습니다:
주의사항 : streamlit으로 배포했고 오랫동안 접속을 안할 시 sleep이 되어 처음으로 켜지는데 오랜 시간이 걸립니다.
- **메인 페이지**: https://streamlit1-o75t.onrender.com/


<div align="center">
  <p>Made with ❤️ by DevView Team</p>
</div> 
