# 게시판 키워드 알림 시스템

특정 키워드가 포함된 게시글이 올라오면 자동으로 알림을 보내주는 시스템입니다.

## 🛠️ 기술 스택

### Backend

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring Security**
- **Spring Mail**

### Database

- **H2 Database** (개발 환경) 추후 **MySQL**로 변경 예정

### Scheduling

- **Spring Scheduler** (주기적인 작업 처리)

### Web Crawling

- **JSoup** - 웹 크롤링

### DevOps

- **Docker** - 컨테이너화
- **Jenkins** - CI/CD
- **JaCoCo** - 코드 커버리지

### Frontend

- **Thymeleaf** - 서버 사이드 템플릿

## 📋 기능 목록

### Phase 1 - 기본 기능

- [X] 사용자 회원가입 및 로그인
- [ ] 사용자 아이디 및 비밀번호 찾기
- [X] 키워드 등록 및 관리
- [X] 알림 받을 웹사이트 URL 등록
- [ ] 웹사이트별 키워드 매칭 설정

### Phase 2 - 크롤링 및 알림

- [X] 웹사이트 크롤링 기능
- [ ] URL별 키워드 추출 가능 여부 확인 및 수동 설정
    - 자동: 웹사이트에서 키워드를 자동으로 추출할 수 있는 경우
    - 수동: 크롤링이 어려운 사이트의 경우 사용자가 직접 키워드 입력
- [ ] 키워드 매칭 알고리즘
- [ ] 이메일 알림 발송

### Phase 3 - 고급 기능

- [ ] 알림 히스토리 관리
- [ ] 사용자별 알림 설정 (빈도, 시간대 등)
- [ ] 키워드 우선순위 설정
- [ ] 대시보드 통계

### Phase 4 - 시스템 최적화

- [ ] 메시지 큐를 통한 비동기 처리
- [ ] 크롤링 성능 최적화
- [ ] 알림 중복 방지

## 🚀 실행 방법

### 개발 환경

```bash
./gradlew bootRun
```

### Docker 실행 (현재 미구현)

```bash
docker build -t keyword-watcher .
docker run -p 8080:8080 keyword-watcher
```

## 📊 테스트 및 커버리지

```bash
# 테스트 실행
./gradlew test

# 커버리지 리포트 생성
./gradlew jacocoTestReport
```

## 🔮 추후 고려사항

1. **이중화가 가능한 구조로 변경**
    - 로드 밸런서를 통한 다중 인스턴스 운영
    - 데이터베이스 클러스터링

2. **서킷브레이커를 통한 크롤링 불가 URL 체크**
    - 접근 불가능한 사이트에 대한 자동 처리
    - 장애 전파 방지

## 📝 API 문서

개발 완료 후 Swagger UI를 통해 API 문서를 제공할 예정입니다.
