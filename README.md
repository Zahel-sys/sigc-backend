# 🏥 SIGC - Sistema Integral de Gestión de Citas (Backend)

Este repositorio contiene el **backend** del Sistema Integral de Gestión de Citas (SIGC), desarrollado con **Spring Boot**, **Java 17** y **MySQL (XAMPP)**.  
El proyecto forma parte del curso de **Herramientas en Desarrollo de Software**.

---

## 🚀 Descripción General

El **SIGC** permite gestionar citas médicas de forma eficiente entre **pacientes** y **administradores**.  
Actualmente, el backend implementa controladores CRUD completos, validaciones, autenticación básica y soporte para carga de imágenes locales.

---

## 🧩 Tecnologías Utilizadas

| Componente | Tecnología |
|-------------|-------------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.6 |
| ORM | Spring Data JPA (Hibernate) |
| Base de Datos | MySQL (XAMPP) |
| Dependencias clave | Lombok, Spring Web, Spring Security, Validation API |
| Servidor Local | Tomcat embebido |

---

## 🧱 Estructura del Proyecto

src/
├─ main/
│ ├─ java/com/sigc/backend/
│ │ ├─ controller/ → Controladores REST (Usuarios, Doctores, Citas, etc.)
│ │ ├─ model/ → Entidades JPA
│ │ ├─ repository/ → Interfaces de persistencia
│ │ └─ service/ → Lógica de negocio
│ └─ resources/
│ ├─ application.properties → Configuración de BD
│ ├─ uploads/ → Imágenes subidas
│ └─ static/ → Recursos estáticos (opcional)
└─ test/ → Pruebas unitarias


---

## ⚙️ Configuración del entorno

1️⃣ Clona el repositorio:
```bash
git clone https://github.com/Zahel-sys/sigc-backend.git


2️⃣ Abre el proyecto en Spring Tool Suite (STS) o IntelliJ IDEA.

3️⃣ Crea una base de datos en MySQL llamada sigc_db:

CREATE DATABASE sigc_db;


4️⃣ Configura las credenciales en src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/sigc_db
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB


5️⃣ Ejecuta el proyecto:

mvn spring-boot:run


El servidor se iniciará en:

http://localhost:8080

📚 Endpoints Principales (API REST)
Entidad	Método	Endpoint	Descripción
Usuarios	POST	/api/usuarios	Crear usuario (registro)
	GET	/api/usuarios	Listar usuarios
Doctores	POST	/api/doctores	Registrar doctor con imagen
	GET	/api/doctores	Listar doctores
Especialidades	POST	/api/especialidades	Crear especialidad médica
	GET	/api/especialidades	Listar especialidades
Horarios	POST	/api/horarios	Registrar horario
	GET	/api/horarios	Listar horarios disponibles
Citas	POST	/api/citas	Crear cita médica
	DELETE	/api/citas/{id}	Cancelar cita
🧠 Validaciones Implementadas

DNI: 8 dígitos obligatorios

Celular: 9 dígitos válidos

Horarios: no pueden ser en fechas pasadas

Citas: se pueden cancelar hasta 2 días antes de la fecha programada

🖼️ Carga de Imágenes

Las imágenes de doctores y especialidades se guardan en la carpeta:

src/main/resources/uploads/


Se puede acceder mediante:

http://localhost:8080/uploads/{nombre_archivo}

🧑‍💻 Roles del Sistema
Rol	Descripción
Administrador	Gestiona doctores, especialidades y horarios
Paciente	Agenda, consulta y cancela sus citas médicas