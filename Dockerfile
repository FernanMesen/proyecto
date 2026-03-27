# Usar Java 21
FROM eclipse-temurin:21-jdk

# Carpeta de trabajo
WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Dar permisos (por si acaso)
RUN chmod +x mvnw || true

# Instalar Maven y compilar
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Exponer el puerto
EXPOSE 8080

# Comando de inicio
CMD ["java", "-jar", "target/bolsa-empleo-1.0.0.jar"]