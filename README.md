# ChattService
개발자 기술 면접 대비 사이트

## 프로젝트 소개
ChattService는 개발자들이 기술 면접을 효과적으로 준비할 수 있도록 도와주는 웹 애플리케이션입니다. 다양한 개발 분야의 면접 질문과 모범 답안을 제공하며, 사용자가 직접 면접 시뮬레이션을 진행할 수 있는 기능을 제공합니다.

## 주요 기능
- 개발 분야별 기술 면접 질문 및 답변 제공
- 사용자 맞춤형 면접 시뮬레이션
- 질문 검색 및 북마크 기능
- 사용자 학습 진도 추적
- 면접 피드백 시스템

## 기술 스택
- **백엔드**: Java, Spring Boot
- **프론트엔드**: HTML, CSS, JavaScript
- **데이터베이스**: MySQL (예상)
- **배포**: Docker, AWS (예상)

## 프로젝트 구조
```
DevView/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── project/
│   │   │       └── DevView/
│   │   │           └── cat_service/
│   │   │               ├── CatServiceApplication.java (메인 애플리케이션)
│   │   │               ├── global/ (전역 설정 및 유틸리티)
│   │   │               ├── img/ (이미지 처리)
│   │   │               ├── interview/ (면접 관련 기능)
│   │   │               ├── question/ (질문 관리)
│   │   │               └── user/ (사용자 관리)
│   │   └── resources/ (정적 리소스, 설정 파일)
│   └── test/ (테스트 코드)
```

## 설치 및 실행 방법
1. 저장소 클론
   ```bash
   git clone https://github.com/your-username/ChattService.git
   cd ChattService
   ```

2. 프로젝트 빌드
   ```bash
   cd DevView
   ./gradlew build
   ```

3. 애플리케이션 실행
   ```bash
   ./gradlew bootRun
   ```
   또는
   ```bash
   java -jar build/libs/DevView-0.0.1-SNAPSHOT.jar
   ```

4. 웹 브라우저에서 접속
   ```
   http://localhost:8080
   ```

## 기여 방법
1. 이 저장소를 포크합니다.
2. 새로운 브랜치를 생성합니다: `git checkout -b feature/새기능`
3. 변경사항을 커밋합니다: `git commit -m '새로운 기능 추가'`
4. 브랜치에 푸시합니다: `git push origin feature/새기능`
5. Pull Request를 제출합니다.

## 라이센스
이 프로젝트는 MIT 라이센스 하에 배포됩니다. 자세한 내용은 LICENSE 파일을 참조하세요.

## 연락처
- 이메일: example@example.com
- 웹사이트: https://example.com
