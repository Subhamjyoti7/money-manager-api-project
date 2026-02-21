# Use Java 17 runtime (matches build)
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the executable jar
COPY target/Money_Manager_System-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]