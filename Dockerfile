# --------------------------------------------------------------
# Build Stage
# --------------------------------------------------------------

# Image with Maven 3.9 and JDK 21
# JDK and Maven are needed for compiling
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copying only the pom.xml first so the dependency layer is cached separately
# If there are changes in the source code, we dont need to download the pom dependencies
COPY pom.xml .

# Downloading the dependencies in pom.xml, only re-runs if pom.xml changes
# -B - batch mode so there are no questions and no progress bars
# -q - quiet so that only errors get returned
RUN mvn -B -q dependency:go-offline

# Copying the whole source code separately
# If there are changes in the code we only rebuild from here
COPY src ./src

# Compiling and packaging the app into an executable jar file
# -DskipTests - skips the tests (they already run in the GitHub Actions pipeline), just compiles and packages into a jar file
RUN mvn -B -q package -DskipTests

# --------------------------------------------------------------
# Runtime Stage
# --------------------------------------------------------------

# New image with only JRE, nothing from the Build Stage is transferred over
# JRE is needed to run the jar files
FROM eclipse-temurin:21-jre-alpine

# Creating a Group and a non-root User
# -S - system user without a password
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copying the jar from the Build Stage renamed to app.jar
COPY --from=build /app/target/basketball-league-tracker-*.jar app.jar

# The non-root user takes over from here
USER app

# The app listens on port 8080 (doesnt open the port)
EXPOSE 8080

# Docker runs a healthcheck on the Spring actuator health endpoint
# --interval=30s - tests every 30s
# --timeout=5s - if there is no response in 5s, its counted as a failure
# --start-period=40s - gives Spring Boot 40s to start before the container counts failures
# --retries=3 - after 3 consecutive failures the container is marked as unhealthy
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# The command that runs when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]