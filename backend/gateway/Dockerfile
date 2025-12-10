# ============================================
# Etapa 1: Compilación del servicio (build JAR)
# ============================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app


# ================================
# 1) POMs necesarios para cachear dependencias
#    (solo POMs para permitir el reactor Maven)
# ================================

# POM raíz del monorepo
COPY pom.xml .

# shared-contracts (solo POM, requerido por el reactor)
COPY shared/shared-contracts/pom.xml shared/shared-contracts/pom.xml

# frontend (referenciado en <modules>)
COPY frontend/pom.xml frontend/pom.xml

# backend (solo POMs)
COPY backend/gateway/pom.xml backend/gateway/pom.xml
COPY backend/auth-service/pom.xml backend/auth-service/pom.xml
COPY backend/academic-project-service/pom.xml backend/academic-project-service/pom.xml
COPY backend/coordinator-service/pom.xml backend/coordinator-service/pom.xml
COPY backend/department-head-service/pom.xml backend/department-head-service/pom.xml
COPY backend/notification-service/pom.xml backend/notification-service/pom.xml


# =======================================================
# Descarga anticipada de dependencias (capa cacheable)
# Se omiten tests por completo para acelerar el build
# =======================================================
RUN mvn -q -B -Dmaven.test.skip=true \
    -pl backend/gateway -am \
    dependency:go-offline


# ================================
# 2) Copia de código fuente necesario para el build
#    (solo gateway; shared no se usa aquí)
# ================================
COPY backend/gateway backend/gateway


# ================================
# 3) Compilación y empaquetado del servicio
#    (se omiten tests: no se compilan ni ejecutan)
# ================================
RUN mvn -q -B -Dmaven.test.skip=true \
    -pl backend/gateway -am \
    package



# ============================================
# Etapa 2: Imagen final (runtime minimalista)
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# curl para healthcheck
RUN apk add --no-cache curl

# Usuario no-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# JAR generado en la etapa de build
COPY --from=build /app/backend/gateway/target/*.jar app.jar

EXPOSE 8080

# Healthcheck HTTP a Actuator
HEALTHCHECK --interval=20s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# Ejecución optimizada para contenedores
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
