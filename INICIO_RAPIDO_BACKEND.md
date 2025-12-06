# 🚀 INICIO RÁPIDO - SIGC Backend

## ⚡ 3 Minutos para empezar

### 1️⃣ Iniciar el Backend (Windows PowerShell)

```powershell
cd C:\Users\LEONARDO\sigc-backend

# Opción A: Ejecutar directamente
java -jar target\backend-0.0.1-SNAPSHOT.jar

# Opción B: En background (Job)
$job = Start-Job -ScriptBlock { 
  Set-Location "C:\Users\LEONARDO\sigc-backend"
  java -jar "target\backend-0.0.1-SNAPSHOT.jar" 
}
```

⏱️ Espera 30-45 segundos a que inicie

### 2️⃣ Verificar que está Operativo

```bash
# En otra terminal PowerShell
curl.exe -X GET http://localhost:8080/especialidades
```

✅ Si ves un array JSON con 8 especialidades, ¡está funcionando!

### 3️⃣ Hacer Login

**Como ADMINISTRADOR:**
```bash
curl.exe -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin2@sigc.com\",\"password\":\"Admin123456\"}"
```

**Como PACIENTE:**
```bash
curl.exe -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"paciente@sigc.com\",\"password\":\"Paciente123456\"}"
```

---

## 👥 Usuarios Disponibles

| Email | Contraseña | Rol | Estado |
|-------|-----------|-----|--------|
| `admin2@sigc.com` | `Admin123456` | ADMIN | ✅ Funciona |
| `paciente@sigc.com` | `Paciente123456` | PACIENTE | ✅ Funciona |

---

## 🎯 Endpoints Principales

### Autenticación
- `POST /auth/register` - Registrar nuevo usuario
- `POST /auth/login` - Iniciar sesión
- `GET /auth/me` - Datos del usuario (requiere token)

### Datos
- `GET /especialidades` - Listar especialidades (8)
- `GET /doctores` - Listar doctores
- `GET /citas` - Listar citas
- `GET /horarios` - Listar horarios

### Gestión
- `POST /citas` - Crear cita
- `PUT /citas/{id}` - Modificar cita
- `DELETE /citas/{id}` - Eliminar cita

---

## 📝 Crear Nuevo Usuario

```bash
curl.exe -X POST http://localhost:8080/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"nombre\":\"Maria Lopez\",\"email\":\"maria@sigc.com\",\"password\":\"Maria123456\",\"dni\":\"87654321\",\"telefono\":\"666000200\",\"rol\":\"PACIENTE\"}"
```

**Importante:**
- Email debe ser único
- Teléfono: exactamente 9 dígitos (sin guiones)
- DNI: exactamente 8 dígitos
- Contraseña: 6-50 caracteres
- Rol: PACIENTE, DOCTOR o ADMIN

---

## 🔐 Usar Token JWT

Después del login, recibes un token. Úsalo en requests protegidos:

```bash
curl.exe -X GET http://localhost:8080/auth/me ^
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 🗄️ Base de Datos H2

### Acceder a H2 Console
```
http://localhost:8080/h2-console

Configuración:
- URL: jdbc:h2:~/sigc_database/db
- Usuario: SA
- Contraseña: (dejada vacía)
```

### Ver datos SQL
```sql
SELECT * FROM USUARIOS;
SELECT * FROM CITAS;
SELECT * FROM ESPECIALIDADES;
```

---

## ❌ Troubleshooting

| Problema | Solución |
|----------|----------|
| **"Failed to connect"** | Backend no está corriendo. Ver sección 1️⃣ |
| **"401 Unauthorized"** | Email o contraseña incorrectos |
| **"409 Conflict"** | Email ya está registrado. Usar otro email |
| **"400 Bad Request"** | Teléfono debe ser 9 dígitos, DNI 8 dígitos |
| **Backend muy lento** | Dejar que termine de iniciar (hasta 45 segundos) |

---

## 📊 Información del Sistema

```
Framework: Spring Boot 3.5.8
Java: 21.0.7
Base de datos: H2
Autenticación: JWT HS256
Puerto: 8080
Endpoints: 51+
```

---

## ✨ Estado

```
✅ Backend: Operacional
✅ BD: Conectada y persistente
✅ Autenticación: JWT funcionando
✅ Usuarios: Admin y Paciente listos
✅ Listo para frontend
```

---

## 🚀 Siguiente Paso

Conectar tu frontend React en `http://localhost:5173`

El backend está configurado con CORS para aceptar requests desde:
- `http://localhost:5173`
- `http://localhost:5174`
- `http://localhost:5175`

---

**¡Listo! El backend está completamente funcional.** 🎉

Ver archivo `CREDENCIALES_FUNCIONANDO.md` para más detalles.
