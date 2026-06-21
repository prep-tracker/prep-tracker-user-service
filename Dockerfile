# Stage 1 - Build
FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /build

# 1. Copy and install common libraries first
COPY prep-tracker-common-lib ./prep-tracker-common-lib
RUN mvn clean install -f prep-tracker-common-lib -DskipTests

# 2. Copy user-service POM and go offline
COPY prep-tracker-user-service/pom.xml ./prep-tracker-user-service/
WORKDIR /build/prep-tracker-user-service
RUN mvn dependency:go-offline

# 3. Copy user-service source and build
WORKDIR /build
COPY prep-tracker-user-service/src ./prep-tracker-user-service/src
WORKDIR /build/prep-tracker-user-service
RUN mvn clean package -DskipTests

# Stage 2 - Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /build/prep-tracker-user-service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
