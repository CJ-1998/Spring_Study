# ---------- (1단계: 빌드 환경) ----------
# Gradle과 Java가 이미 깔려있는 이미지를 불러옵니다.
FROM gradle:7.6-jdk17-alpine AS builder

# 작업 폴더 설정
WORKDIR /build

# 소스 코드 복사 (내 컴퓨터/EC2의 소스를 도커 안으로 넣음)
COPY . .

# 빌드 실행 (여기서 JAR 파일이 만들어짐)
# -x test는 테스트 코드는 생략하고 빨리 빌드하라는 뜻
RUN gradle clean build -x test --no-daemon

# ---------- (2단계: 실행 환경) ----------
# 실제 실행할 가벼운 이미지를 불러옵니다.
FROM amazoncorretto:17

WORKDIR /app

# 1단계(builder)에서 만든 JAR 파일만 쏙 가져옵니다.
COPY --from=builder /build/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
