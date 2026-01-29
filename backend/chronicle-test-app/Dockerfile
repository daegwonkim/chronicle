FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :chronicle-test-app:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/chronicle-test-app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
