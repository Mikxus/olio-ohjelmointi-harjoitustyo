
FROM gradle:8.11.1-jdk17 AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY backend ./backend
COPY common ./common
COPY desktop-app ./desktop-app

RUN chmod +x ./gradlew && ./gradlew :backend:quarkusBuild -Dquarkus.package.jar.type=fast-jar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/backend/build/quarkus-app/ ./

EXPOSE 8080
CMD ["java", "-jar", "quarkus-run.jar"]
