# Use a multi-stage build for a minimal production image
ARG ENV=prod
FROM gradle:8.5.0-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
ARG ENV=prod
ENV ENV=${ENV}
WORKDIR /app
COPY --from=build /app/build/libs/transaction-service-1.0-SNAPSHOT-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-DENV=${ENV}","-jar", "/app/app.jar"]
