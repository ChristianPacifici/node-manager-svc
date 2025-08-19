FROM gradle:8.13.0-jdk21-ubi-minimal AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts gradle.properties ./
COPY --chown=gradle:gradle src ./src
RUN gradle build --no-daemon

FROM eclipse-temurin:21-ubi9-minimal
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "app.jar"]