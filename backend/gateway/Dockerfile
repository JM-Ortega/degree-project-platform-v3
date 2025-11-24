##############################
# Etapa 1 — Build con Maven
##############################
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# --- 1) POMs para cachear dependencias y satisfacer el reactor de Maven ---

# POM padre del monorepo
COPY pom.xml .

# shared
COPY shared/shared-contracts/pom.xml shared/shared-contracts/pom.xml

# frontend (aunque no se compile aquí, está en <modules>)
COPY frontend/pom.xml frontend/pom.xml

# backend (solo POMs)
COPY backend/gateway/pom.xml backend/gateway/pom.xml
COPY backend/auth-service/pom.xml backend/auth-service/pom.xml
COPY backend/academic-project-service/pom.xml backend/academic-project-service/pom.xml
COPY backend/coordinator-service/pom.xml backend/coordinator-service/pom.xml
COPY backend/department-head-service/pom.xml backend/department-head-service/pom.xml
COPY backend/notification-service/pom.xml backend/notification-service/pom.xml


# Descargamos dependencias (capa cacheable)

# ✔ Línea correcta cuando los tests del gateway estén bien:
RUN mvn -q -B -DskipTests -pl backend/gateway -am dependency:go-offline

# ❗ Línea temporal mientras los tests estén rotos:
# RUN mvn -q -B -Dmaven.test.skip=true -pl backend/gateway -am dependency:go-offline


# --- 2) Código fuente necesario para el gateway ---

# shared-contracts (si el gateway lo usa para DTOs / contratos)
COPY shared/shared-contracts shared/shared-contracts

# Código del gateway
COPY backend/gateway backend/gateway


# Compilamos el gateway (y módulos relacionados si existieran)

# ✔ Línea correcta cuando los tests estén bien:
RUN mvn -q -B -DskipTests -pl backend/gateway -am package

# ❗ Línea temporal mientras los tests estén rotos:
# RUN mvn -q -B -Dmaven.test.skip=true -pl backend/gateway -am package



##############################
# Etapa 2 — Imagen final (runtime)
##############################
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# curl para el healthcheck
RUN apk add --no-cache curl

# Crear usuario no root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR generado
COPY --from=build /app/backend/gateway/target/*.jar app.jar

# Exponer puerto del gateway
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=20s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# Ejecutar con Java optimizado para contenedores
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
