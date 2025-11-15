-- ===================================================================
-- Inicialización de base de datos para Keycloak (Identity Provider)
--
-- Este script crea una base de datos y un usuario dedicados
-- exclusivamente para Keycloak.
--
-- Ejecutado automáticamente por docker-entrypoint-initdb.d en el
-- primer arranque del contenedor (si el volumen está vacío).
-- ===================================================================

-- 1) Crear usuario para conexión
CREATE USER keycloak_user WITH PASSWORD 'keycloak_pass';

-- 2) Crear base de datos exclusiva
CREATE DATABASE keycloak_db OWNER keycloak_user;

-- 3) Seguridad básica (evitar acceso global)
REVOKE ALL PRIVILEGES ON DATABASE keycloak_db FROM PUBLIC;

-- 4) Permisos mínimos necesarios para operar
GRANT CONNECT ON DATABASE keycloak_db TO keycloak_user;

-- Keycloak creará automáticamente sus tablas internas al iniciar.
