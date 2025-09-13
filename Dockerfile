# 빌드 단계 - Gradle로 애플리케이션 빌드
FROM eclipse-temurin:17-jdk AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 빌드 파일들 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 소스 코드 복사
COPY src src

# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew bootJar -x test

# 실행 단계 - 경량 이미지 사용
FROM eclipse-temurin:17-jre

# 메타데이터 추가
LABEL maintainer="keyword-watcher-team"
LABEL version="1.0"
LABEL description="Keyword Watcher Application"

# 애플리케이션 실행을 위한 사용자 생성 (보안상 root 사용 방지)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 파일 소유권을 appuser로 변경
RUN chown appuser:appuser app.jar

# appuser로 실행 (보안)
USER appuser

# 애플리케이션이 사용할 포트 노출
EXPOSE 8080

# 환경 변수 설정 (기본값)
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 애플리케이션 실행 (환경 변수를 통한 설정 주입)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
