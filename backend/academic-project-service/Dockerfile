# ====== Etapa 1: Compilación del JAR ======
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# --- 1) POMs para cachear dependencias y satisfacer el reactor de Maven ---

# POM padre del monorepo
COPY pom.xml .

# --- shared ---
COPY shared/shared-contracts/pom.xml shared/shared-contracts/pom.xml

# --- frontend ---
COPY frontend/pom.xml frontend/pom.xml

# --- backend (solo POMs) ---
COPY backend/gateway/pom.xml backend/gateway/pom.xml
COPY backend/auth-service/pom.xml backend/auth-service/pom.xml
COPY backend/academic-project-service/pom.xml backend/academic-project-service/pom.xml
COPY backend/coordinator-service/pom.xml backend/coordinator-service/pom.xml
COPY backend/department-head-service/pom.xml backend/department-head-service/pom.xml
COPY backend/notification-service/pom.xml backend/notification-service/pom.xml


# Descarga anticipada de dependencias (capa cacheable)

# ✔ Línea correcta cuando los tests estén bien:
# RUN mvn -q -B -DskipTests -pl backend/academic-project-service -am dependency:go-offline

# ❗ Línea temporal mientras los tests estén rotos:
RUN mvn -q -B -Dmaven.test.skip=true -pl backend/academic-project-service -am dependency:go-offline


# --- 2) Código fuente necesario ---

# shared-contracts
COPY shared/shared-contracts shared/shared-contracts

# academic project service
COPY backend/academic-project-service backend/academic-project-service


# ====== Compilación ======

# ✔ Línea correcta cuando los tests estén bien:
# RUN mvn -q -B -DskipTests -pl backend/academic-project-service -am package

# ❗ Línea temporal mientras los tests estén rotos:
RUN mvn -q -B -Dmaven.test.skip=true -pl backend/academic-project-service -am package



# ====== Etapa 2: Imagen final (runtime) ======
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copia del JAR generado en la etapa de build
COPY --from=build /app/backend/academic-project-service/target/*.jar app.jar

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --start-period=50s --retries=3 \
  CMD curl -fsS http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
