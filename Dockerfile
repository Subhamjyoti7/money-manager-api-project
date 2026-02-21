# Use lightweight Java 21 runtime
FROM eclipse-temurin:21-jre

# Set working directory inside container
WORKDIR /app

# Copy only the built jar into the container
COPY target/Money_Manager_System-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar

# Expose Spring Boot port
EXPOSE 9090

# Run the application
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]