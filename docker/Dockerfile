# Dockerfile para FNAFHS Academy Backend

# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY backend/src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear directorio para uploads
RUN mkdir -p /app/uploads && \
    addgroup -S spring && \
    adduser -S spring -G spring

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Configurar usuario no root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Variables de entorno
ENV JAVA_OPTS=""

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/test || exit 1

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]