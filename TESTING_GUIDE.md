# 🚀 Guía de Pruebas - Endpoint de Registro

## 📍 Endpoint
```
POST http://localhost:8080/auth/register
Content-Type: application/json
```

---

## ✅ CASO 1: Registro Exitoso

### Request
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Leonardo García",
    "email": "leonardo@example.com",
    "password": "password123",
    "dni": "12345678",
    "telefono": "987654321",
    "rol": "PACIENTE"
  }'
```

### Response Esperada (201 Created)
```json
{
  "idUsuario": 1,
  "nombre": "Leonardo García",
  "email": "leonardo@example.com",
  "mensaje": "Usuario registrado exitosamente"
}
```

---

## ❌ CASO 2: Email Duplicado

### Request
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Otro Usuario",
    "email": "leonardo@example.com",
    "password": "password456",
    "dni": "87654321",
    "telefono": "912345678",
    "rol": "PACIENTE"
  }'
```

### Response Esperada (409 Conflict)
```json
{
  "timestamp": "2025-10-24T04:00:00",
  "status": 409,
  "error": "Email duplicado",
  "mensaje": "El email leonardo@example.com ya está registrado"
}
```

---

## ❌ CASO 3: Datos Inválidos

### Request (DNI inválido)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "password": "pass",
    "dni": "123",
    "telefono": "98765432",
    "rol": "PACIENTE"
  }'
```

### Response Esperada (400 Bad Request)
```json
{
  "timestamp": "2025-10-24T04:00:00",
  "status": 400,
  "error": "Errores de validación",
  "errores": {
    "dni": "El DNI debe tener exactamente 8 dígitos",
    "password": "La contraseña debe tener entre 6 y 50 caracteres",
    "telefono": "El teléfono debe tener exactamente 9 dígitos"
  }
}
```

---

## ❌ CASO 4: Campos Faltantes

### Request
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María López",
    "email": "maria@example.com"
  }'
```

### Response Esperada (400 Bad Request)
```json
{
  "timestamp": "2025-10-24T04:00:00",
  "status": 400,
  "error": "Errores de validación",
  "errores": {
    "password": "La contraseña es obligatoria",
    "dni": "El DNI es obligatorio",
    "telefono": "El teléfono es obligatorio"
  }
}
```

---

## 🧪 Prueba desde Frontend (JavaScript/React)

### Ejemplo con fetch
```javascript
const registrarUsuario = async (datos) => {
  try {
    const response = await fetch('http://localhost:8080/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(datos)
    });

    if (!response.ok) {
      const error = await response.json();
      console.error('Error:', error);
      throw new Error(error.mensaje || 'Error al registrar');
    }

    const usuario = await response.json();
    console.log('Usuario registrado:', usuario);
    return usuario;
  } catch (error) {
    console.error('Error de red:', error);
    throw error;
  }
};

// Uso
const nuevoUsuario = {
  nombre: "Leonardo",
  email: "leonardo@example.com",
  password: "12345678",
  dni: "12345678",
  telefono: "987654321",
  rol: "PACIENTE"
};

registrarUsuario(nuevoUsuario);
```

### Ejemplo con axios
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080'
});

const registrarUsuario = async (datos) => {
  try {
    const response = await api.post('/auth/register', datos);
    console.log('Usuario registrado:', response.data);
    return response.data;
  } catch (error) {
    if (error.response) {
      console.error('Error del servidor:', error.response.data);
      throw error.response.data;
    } else {
      console.error('Error de red:', error.message);
      throw error;
    }
  }
};
```

---

## 🔍 Verificación en MySQL

```sql
-- Ver usuarios registrados
SELECT id_usuario, nombre, email, rol, fecha_registro, activo 
FROM usuarios;

-- Verificar que la contraseña esté encriptada (BCrypt)
SELECT id_usuario, nombre, 
       SUBSTRING(password, 1, 20) as password_hash_preview
FROM usuarios;
-- Debe verse algo como: $2a$10$N9qo8uLOickgx2Z...

-- Contar usuarios por rol
SELECT rol, COUNT(*) as total 
FROM usuarios 
GROUP BY rol;
```

---

## 📦 Prueba con Postman

1. Crear una nueva request
2. Método: `POST`
3. URL: `http://localhost:8080/auth/register`
4. Headers: `Content-Type: application/json`
5. Body → Raw → JSON:
```json
{
  "nombre": "Test Usuario",
  "email": "test@example.com",
  "password": "test12345",
  "dni": "11111111",
  "telefono": "999888777",
  "rol": "PACIENTE"
}
```
6. Send

---

## ⚠️ Notas Importantes

1. **Contraseña Encriptada**: La contraseña se guarda como hash BCrypt (no texto plano)
2. **Email Único**: No se puede registrar el mismo email dos veces
3. **Rol por Defecto**: Si no se envía `rol`, se asigna automáticamente "PACIENTE"
4. **CORS Configurado**: Acepta peticiones desde localhost:5173 y localhost:5174
5. **Endpoint sin /api**: La ruta es `/auth/register` (NO `/api/auth/register`)

---

## 🐛 Troubleshooting

### Error: "Connection refused"
- Verifica que el backend esté corriendo: `http://localhost:8080`
- Ejecuta: `mvnw.cmd spring-boot:run`

### Error: "CORS policy blocked"
- Verifica que estés llamando desde localhost:5173 o 5174
- Revisa la consola del navegador para más detalles

### Error: "Email already exists" pero la tabla está vacía
- Limpia la tabla: `TRUNCATE TABLE usuarios;`
- Reinicia el ID: `ALTER TABLE usuarios AUTO_INCREMENT = 1;`

### Password no se encripta
- Verifica que `PasswordEncoder` esté en el contexto de Spring
- Revisa los logs de Spring Boot al iniciar
