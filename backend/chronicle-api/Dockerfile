FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :chronicle-api:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

# Pinpoint 에이전트 다운로드
RUN apt-get update && apt-get install -y curl && \
    curl -L -o pinpoint-agent.tar.gz https://github.com/pinpoint-apm/pinpoint/releases/download/v3.0.0/pinpoint-agent-3.0.0.tar.gz && \
    tar -xzf pinpoint-agent.tar.gz && \
    rm pinpoint-agent.tar.gz && \
    apt-get remove -y curl && apt-get clean

COPY --from=build /app/chronicle-api/build/libs/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]