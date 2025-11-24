# ====== Etapa 1: Compilación del JAR ======
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# --- 1) POMs para cachear dependencias y satisfacer el reactor de Maven ---

# POM padre del monorepo
COPY pom.xml .

# --- shared ---
COPY shared/shared-contracts/pom.xml shared/shared-contracts/pom.xml

# --- frontend (aunque no se compile aquí, está listado en <modules>) ---
COPY frontend/pom.xml frontend/pom.xml

# --- backend (solo POMs) ---
COPY backend/gateway/pom.xml backend/gateway/pom.xml
COPY backend/auth-service/pom.xml backend/auth-service/pom.xml
COPY backend/academic-project-service/pom.xml backend/academic-project-service/pom.xml
COPY backend/coordinator-service/pom.xml backend/coordinator-service/pom.xml
COPY backend/department-head-service/pom.xml backend/department-head-service/pom.xml
COPY backend/notification-service/pom.xml backend/notification-service/pom.xml


# Descarga anticipada de dependencias (capa cacheable)

# ✔ Línea correcta (cuando los tests funcionen)
# RUN mvn -q -B -DskipTests -pl backend/department-head-service -am dependency:go-offline

# ❗ Línea temporal mientras los tests están rotos
RUN mvn -q -B -Dmaven.test.skip=true -pl backend/department-head-service -am dependency:go-offline


# --- 2) Código fuente necesario ---

# Módulo compartido
COPY shared/shared-contracts shared/shared-contracts

# Microservicio department-head-service
COPY backend/department-head-service backend/department-head-service


# ====== Compilación del microservicio ======

# ✔ Línea correcta (cuando los tests estén bien)
# RUN mvn -q -B -DskipTests -pl backend/department-head-service -am package

# ❗ Línea temporal mientras los tests están rotos
RUN mvn -q -B -Dmaven.test.skip=true -pl backend/department-head-service -am package



# ====== Etapa 2: Imagen final (runtime) ======
FROM eclipse-temurin:21-jre-alpine

# curl para healthchecks
RUN apk add --no-cache curl

# Usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/backend/department-head-service/target/*.jar app.jar

# Puerto expuesto del microservicio
EXPOSE 8084

# Healthcheck (Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=5s --start-period=50s --retries=3 \
  CMD curl -fsS http://localhost:8084/actuator/health || exit 1

# Entrada JVM optimizada
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
