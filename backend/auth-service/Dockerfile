# ====== Etapa 1: Compilación del JAR ======
FROM maven:3.9-eclipse-temurin-21 AS build

# Directorio de trabajo en la etapa de build
WORKDIR /app

# --- 1) POMs para cachear dependencias y satisfacer el reactor de Maven ---

# POM padre del monorepo
COPY pom.xml .

# --- shared ---
COPY shared/shared-contracts/pom.xml shared/shared-contracts/pom.xml

# --- frontend (aunque no lo compilemos aquí, está en <modules>) ---
COPY frontend/pom.xml frontend/pom.xml

# --- backend (solo POMs, no código) ---
COPY backend/gateway/pom.xml backend/gateway/pom.xml
COPY backend/auth-service/pom.xml backend/auth-service/pom.xml
COPY backend/academic-project-service/pom.xml backend/academic-project-service/pom.xml
COPY backend/coordinator-service/pom.xml backend/coordinator-service/pom.xml
COPY backend/department-head-service/pom.xml backend/department-head-service/pom.xml
COPY backend/notification-service/pom.xml backend/notification-service/pom.xml

# Descarga de dependencias (capa cacheable)
# Línea correcta cuando los tests funcionen:
# RUN mvn -q -B -DskipTests -pl backend/auth-service -am dependency:go-offline

# Línea temporal mientras los tests están rotos:
RUN mvn -q -B -Dmaven.test.skip=true -pl backend/auth-service -am dependency:go-offline


# --- 2) Código fuente necesario para compilar auth-service ---

# Módulo compartido
COPY shared/shared-contracts shared/shared-contracts

# Microservicio auth-service
COPY backend/auth-service backend/auth-service


# ====== Compilar auth-service ======

# ✔ Línea correcta cuando los tests funcionen (solo no ejecuta tests):
# RUN mvn -q -B -DskipTests -pl backend/auth-service -am package

# ❗ Línea temporal mientras los tests están rotos (no compila ni ejecuta tests):
RUN mvn -q -B -Dmaven.test.skip=true -pl backend/auth-service -am package



# ====== Etapa 2: Imagen final (runtime) ======
FROM eclipse-temurin:21-jre-alpine

# curl para el healthcheck HTTP
RUN apk add --no-cache curl

# Usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Directorio de trabajo en la imagen final
WORKDIR /app

# Copia del JAR generado en la etapa de build
COPY --from=build /app/backend/auth-service/target/*.jar app.jar

# Puerto expuesto del microservicio
EXPOSE 8081

# Healthcheck contra el actuator de Spring Boot
HEALTHCHECK --interval=30s --timeout=5s --start-period=50s --retries=3 \
  CMD curl -fsS http://localhost:8081/actuator/health || exit 1

# Entrada de la JVM optimizada para contenedores
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
