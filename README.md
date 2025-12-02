# 🎓 Degree Project Platform v3

Plataforma para la gestión integral de trabajos de grado, diseñada para manejar el flujo completo desde el **Formato A** hasta la **asignación de evaluadores**.  
Este README resume el propósito, requisitos funcionales y arquitectura

🔗 **Repositorio:** https://github.com/JM-Ortega/degree-project-platform-v3

---

## 🚀 Estado y objetivo
- 🎯 **Objetivo:** Gestionar el ciclo de proyectos de grado: registro de docentes, envío y versiones del Formato A, evaluación por coordinador, subida del anteproyecto y asignación de evaluadores.  
- 🌿 **Rama principal:** `main`  
- 🧩 **Arquitectura:** Microservicios  

---

## 🛠️ Tecnologías principales

### ☕ Backend (Java & Spring)
- ☕ **Java 21**
- 🍃 **Spring Boot 3.5.x**
- ☁️ **Spring Cloud** (Gateway + BOM)
- 🌐 **Spring Web (WebMVC)**
- ⚡ **Spring WebFlux**
- 🔐 **Spring Security** (OAuth2 Resource Server)
- 🛡️ **JWT**
- 🗝️ **Keycloak / Keycloak Admin Client**
- ✔️ **Spring Validation (Jakarta)**
- 🗄️ **Spring Data JPA (Hibernate)**
- 📊 **Actuator**

### 🧰 Librerías y utilidades
- 🔄 **MapStruct**
- 📝 **Lombok**
- 📘 **springdoc-openapi (Swagger UI)**
- 🧂 **Argon2 (argon2-jvm)**
- 🧱 **Jackson** (databind + jsr310)
- 🪵 **Logback**

### 🗃️ Bases de datos
- 🐘 **PostgreSQL**
- 🧪 **H2** (tests)

### 📨 Mensajería y comunicación
- 🐇 **RabbitMQ**
- 📬 **Spring AMQP**
- 🔗 **Retrofit 2**
- 🌐 **OkHttp 4**

### 🐳 Contenedores y despliegue
- 🐳 **Docker / Docker Compose**

### 🧪 Testing
- 📦 **Testcontainers**
- 🧪 **JUnit 5**
- 🤖 **Mockito**

### 🖥️ Interfaz gráfica
- 🎨 **JavaFX**

---

## 📌 Requisitos funcionales (alcance del proyecto)

Alta prioridad — el sistema debe soportar los siguientes flujos:

### 1. 👨‍🏫 Registro de docentes
- Datos: nombres, apellidos, celular (opcional), programa (Sistemas, Electrónica y Telecomunicaciones, Automática Industrial, Tecnología en Telemática), email institucional y contraseña con políticas de seguridad.

### 2. 📄 Envío del Formato A por el docente
- Campos: título, modalidad (investigación / práctica profesional), fecha, director, codirector, objetivos, PDF adjunto.  
- Para modalidad **Práctica Profesional**, se debe incluir carta de aceptación.  
- Notificación asíncrona al coordinador (logger).

### 3. 🧑‍💼 Evaluación del Formato A por el coordinador
- Visualización de proyectos con sus estados.  
- Acciones: aprobar, rechazar, agregar observaciones.  
- Notificación asíncrona a docentes y estudiantes (logger).

### 4. 🔁 Nuevas versiones del Formato A
- Se controla el número de intentos (2, 3...).  
- Al tercer intento fallido, el proyecto queda **rechazado definitivamente**.  
- Tras cada subida: notificación al coordinador.

### 5. 👩‍🎓 Visualización del estado del proyecto por estudiantes
- Estados: primera evaluación, segunda, tercera, aceptado, rechazado.

### 6. 📤 Subida del anteproyecto por el docente
- Solo si el Formato A fue aprobado.  
- Registrar fecha de subida.  
- Notificación al jefe de departamento.

### 7. 🏛️ Gestión del jefe de departamento
- Visualización de anteproyectos.  
- Delegación y asignación de **dos evaluadores**.  
- Notificación simulada por logger.

---

## 🧱 Arquitectura
- 🎯 **Controladores REST**  
- ⚙️ **Servicios** (lógica de negocio)  
- 🗂️ **Persistencia** con Spring Data JPA  
- 🔄 **DTOs + MapStruct**  
- 🔐 **Seguridad** con Keycloak y JWT  
- 🔔 **Notificaciones asíncronas**  
- 🧪 **Pruebas** unitarias e integración  
- 🧩 **Microservicios**  
