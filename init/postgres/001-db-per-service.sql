-- ===================================================================
-- Inicialización de PostgreSQL para arquitectura "DB-per-service"
-- Cada microservicio tendrá su propia base y su propio usuario.
-- Ejecutado automáticamente por docker-entrypoint-initdb.d en el
-- primer arranque del contenedor (si el volumen está vacío).
-- ===================================================================

-- 1) Crear usuarios (roles de conexión)
CREATE USER auth_user          WITH PASSWORD 'auth_pass';
CREATE USER project_user       WITH PASSWORD 'project_pass';
CREATE USER coord_user         WITH PASSWORD 'coord_pass';
CREATE USER dept_user          WITH PASSWORD 'dept_pass';
CREATE USER notif_user         WITH PASSWORD 'notif_pass';

-- 2) Crear bases de datos (cada una propiedad de su usuario)
CREATE DATABASE auth_db               OWNER auth_user;
CREATE DATABASE academic_project_db   OWNER project_user;
CREATE DATABASE coordinator_db        OWNER coord_user;
CREATE DATABASE department_head_db    OWNER dept_user;
CREATE DATABASE notification_db       OWNER notif_user;

-- 3) Quitar permisos públicos innecesarios (seguridad básica)
REVOKE ALL PRIVILEGES ON DATABASE auth_db              FROM PUBLIC;
REVOKE ALL PRIVILEGES ON DATABASE academic_project_db  FROM PUBLIC;
REVOKE ALL PRIVILEGES ON DATABASE coordinator_db       FROM PUBLIC;
REVOKE ALL PRIVILEGES ON DATABASE department_head_db   FROM PUBLIC;
REVOKE ALL PRIVILEGES ON DATABASE notification_db      FROM PUBLIC;

-- 4) Otorgar permisos mínimos al dueño (conectarse y operar en su DB)
GRANT CONNECT ON DATABASE auth_db              TO auth_user;
GRANT CONNECT ON DATABASE academic_project_db  TO project_user;
GRANT CONNECT ON DATABASE coordinator_db       TO coord_user;
GRANT CONNECT ON DATABASE department_head_db   TO dept_user;
GRANT CONNECT ON DATABASE notification_db      TO notif_user;

-- Nota:
-- Las tablas y esquemas serán creados automáticamente por Hibernate
-- ejecutándose con los usuarios anteriores, garantizando correcto ownership.
