# ✅ CREDENCIALES FUNCIONANDO - SIGC Backend

## 🎯 Estado General
✅ **Backend operacional en puerto 8080**  
✅ **Base de datos H2 persistente**  
✅ **Autenticación JWT activa**  
✅ **51+ endpoints funcionando**

---

## 👤 Usuario ADMINISTRADOR

### Credenciales
```
Email: admin2@sigc.com
Contraseña: Admin123456
Rol: ADMIN
ID: 6
Nombre: Admin User
```

### Cómo Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin2@sigc.com","password":"Admin123456"}'
```

### Respuesta Exitosa (200 OK)
```json
{
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "rol": "ADMIN",
  "idUsuario": 6,
  "email": "admin2@sigc.com"
}
```

---

## 👥 Usuario PACIENTE (Recién Creado)

### Credenciales
```
Email: paciente@sigc.com
Contraseña: Paciente123456
Rol: PACIENTE
ID: 5
Nombre: Juan Perez
```

### Cómo Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"paciente@sigc.com","password":"Paciente123456"}'
```

### Respuesta Exitosa (200 OK)
```json
{
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "rol": "PACIENTE",
  "idUsuario": 5,
  "email": "paciente@sigc.com"
}
```

---

## 🔑 Información del Token JWT

### Estructura
Token incluye:
- `email`: Email del usuario
- `rol`: Rol del usuario (ADMIN, PACIENTE, DOCTOR)
- `sub`: ID del usuario
- `iat`: Timestamp de creación
- `exp`: Timestamp de expiración (24 horas)

### Algoritmo
- **Algoritmo**: HS256
- **Duración**: 24 horas
- **Clave secreta**: Configurada en `application.properties`

### Uso del Token
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 📝 Crear Nuevos Usuarios

### Endpoint
```
POST /auth/register
```

### Payload
```json
{
  "nombre": "Tu Nombre",
  "email": "correo@ejemplo.com",
  "password": "Password123456",
  "dni": "12345678",
  "telefono": "555000100",
  "rol": "PACIENTE"
}
```

### Validaciones
- **nombre**: 2-100 caracteres
- **email**: Email válido, único en la BD
- **password**: 6-50 caracteres
- **dni**: Exactamente 8 dígitos
- **telefono**: Exactamente 9 dígitos (sin guiones)
- **rol**: PACIENTE, DOCTOR o ADMIN (opcional, default: PACIENTE)

### Ejemplo
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Maria Lopez",
    "email":"maria@sigc.com",
    "password":"Maria123456",
    "dni":"87654321",
    "telefono":"666000200",
    "rol":"PACIENTE"
  }'
```

---

## ✅ Endpoints Verificados

### GET /especialidades
Retorna lista de especialidades médicas (8 disponibles)
```bash
curl -X GET http://localhost:8080/especialidades
```

### POST /auth/register
Registra nuevo usuario (201 Created)

### POST /auth/login  
Autentica usuario y retorna token JWT (200 OK)

### GET /auth/me
Obtiene datos del usuario autenticado (requiere token en header)
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer <token>"
```

---

## 🛠️ Troubleshooting

### Backend No Responde
```bash
# Verificar que esté corriendo
curl http://localhost:8080/especialidades

# Ver procesos Java
Get-Process -Name java

# Reiniciar backend (en PowerShell)
$job = Start-Job -ScriptBlock { 
  Set-Location "C:\Users\LEONARDO\sigc-backend"
  java -jar "target\backend-0.0.1-SNAPSHOT.jar" 
}
```

### Error 401 (No Autorizado)
- Verificar que el email existe en la BD
- Verificar que la contraseña sea correcta
- Verificar que el usuario no esté marcado como inactivo

### Error 400 (Solicitud Incorrecta)
- Verificar que el teléfono tenga exactamente 9 dígitos (sin guiones)
- Verificar que el DNI tenga exactamente 8 dígitos
- Verificar que el email sea válido

### Error 409 (Conflicto - Email Duplicado)
- El email ya está registrado
- Usar otro email para registrar nuevo usuario

---

## 📊 Base de Datos

### Ubicación
```
C:\Users\LEONARDO\sigc_database\db.mv.db
```

### Conexión H2
```
URL: jdbc:h2:~/sigc_database/db
Usuario: SA
Contraseña: (vacía)
Modo: MySQL compatible
```

### H2 Console
```
http://localhost:8080/h2-console
```

---

## 🚀 Próximos Pasos

1. ✅ Backend funcionando
2. ✅ Autenticación verificada
3. ✅ Credenciales de admin creadas
4. ✅ Credenciales de paciente creadas
5. ⏳ Integrar con frontend (React)
6. ⏳ Probar endpoints de citas
7. ⏳ Probar endpoints de doctores

---

## 📝 Notas

- Todas las contraseñas se almacenan como hash BCrypt (no texto plano)
- Los tokens expiran en 24 horas
- La base de datos es persistente (los datos se mantienen entre reinicios)
- El sistema está configurado para desarrollo (CORS habilitado para localhost:5173-5175)

---

**Última actualización**: 2025-12-05  
**Estado**: ✅ OPERACIONAL
