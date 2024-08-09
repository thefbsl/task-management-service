FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install

FROM amazoncorretto:17
WORKDIR /app
COPY target/task-management-api-0.0.1-SNAPSHOT.jar /app/task-manager.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "task-manager.jar"]