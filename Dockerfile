
FROM eclipse-temurin:21-jdk


WORKDIR /app


COPY . .


RUN chmod +x mvnw || true


RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests


EXPOSE 8080


CMD ["java", "-jar", "target/bolsa-empleo-1.0.0.jar"]