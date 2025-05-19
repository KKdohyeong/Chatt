# ---------- 1) 빌드 단계 ----------
FROM gradle:8.6-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean bootJar -x test

# ---------- 2) 런타임 단계 ----------
FROM eclipse-temurin:21-jre
LABEL maintainer="dohyeong254@naver.com"
COPY --from=builder /app/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
