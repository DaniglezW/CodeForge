# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos los archivos de configuración y código fuente
COPY pom.xml .
COPY src ./src

# Hacemos el build del JAR (sin tests para acelerar, opcional)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

# Copiamos el JAR construido desde el stage build
COPY --from=build /app/target/code-forge-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]