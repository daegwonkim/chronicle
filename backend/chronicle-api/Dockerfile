FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .. .
RUN ./gradlew :chronicle-api:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/chronicle-api/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
