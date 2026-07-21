# ─────────────────────────────────────────────────────────────────────
#  Stage 1: Build
#  Uses Maven + JDK 17 to compile and package the application.
# ─────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first — exploits Docker layer cache
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached when pom.xml unchanged)
RUN ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ─────────────────────────────────────────────────────────────────────
#  Stage 2: Runtime
#  Minimal JRE image — no build tools, smaller attack surface.
# ─────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the fat jar from build stage
COPY --from=builder /app/target/*.jar app.jar

# File upload temp directory (Cloudinary uploads)
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Health check — uses Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Active profile = prod; JVM tuned for containers
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=prod", \
  "-jar", "app.jar"]
