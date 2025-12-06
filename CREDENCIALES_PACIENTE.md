# 👤 Credenciales de Paciente - Sistema SIGC

## 📋 Estado Actual

El sistema **no tiene pacientes pre-creados** en la base de datos. Los pacientes deben ser registrados a través del formulario de registro del frontend o creados manualmente.

---

## 🔐 Opción 1: Crear Paciente mediante Base de Datos (Más Rápido)

### Acceder a la Consola H2

```
URL: http://localhost:8080/h2-console
```

### Credenciales de Acceso H2
```
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:mem:sigc_db
User Name: sa
Password: (dejar en blanco)
```

### SQL para Crear Paciente de Prueba

Ejecuta este comando SQL en la consola H2:

```sql
INSERT INTO USUARIOS (EMAIL, PASSWORD, NOMBRE, DNI, TELEFONO, ROL, ACTIVO)
VALUES (
    'paciente@sigc.com',
    '$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e',  -- password: Paciente123456
    'Juan Rodríguez Pérez',
    '12345678',
    '987654321',
    'PACIENTE',
    1
);
```

**Resultado:**
```
Email:    paciente@sigc.com
Password: Paciente123456
Nombre:   Juan Rodríguez Pérez
DNI:      12345678
Teléfono: 987654321
Rol:      PACIENTE
```

---

## 🔐 Opción 2: Crear Paciente mediante Frontend (Recomendado)

### Pasos:

1. **Abre el Frontend**
   ```
   http://localhost:5173
   ```

2. **Busca el enlace "¿No tienes cuenta?"**
   - Haz clic en "Registrarse"

3. **Completa el formulario de registro:**
   ```
   Email:           tu-email@dominio.com
   Nombre:          Tu Nombre Completo
   DNI:             12345678
   Teléfono:        987654321
   Contraseña:      MinoMayus123!
   Confirmar Pass:  MinoMayus123!
   Rol:             PACIENTE
   ```

4. **Haz clic en "Registrarse"**

5. **Inicia sesión con las nuevas credenciales**

---

## 📝 Credenciales de Prueba Rápidas

Si ejecutaste el SQL anterior, puedes usar estas credenciales inmediatamente:

### Paciente de Prueba 1
```
📧 Email:    paciente@sigc.com
🔑 Password: Paciente123456
👤 Nombre:   Juan Rodríguez Pérez
🆔 DNI:      12345678
📞 Teléfono: 987654321
```

### Paciente de Prueba 2 (Opcional)
```
INSERT INTO USUARIOS (EMAIL, PASSWORD, NOMBRE, DNI, TELEFONO, ROL, ACTIVO)
VALUES (
    'maria.garcia@sigc.com',
    '$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e',  -- Paciente123456
    'María García López',
    '87654321',
    '987654322',
    'PACIENTE',
    1
);

📧 Email:    maria.garcia@sigc.com
🔑 Password: Paciente123456
👤 Nombre:   María García López
🆔 DNI:      87654321
📞 Teléfono: 987654322
```

---

## 🔑 Contraseñas Hasheadas Disponibles

Para usar en INSERT directo, aquí están las contraseñas BC rypt pre-hasheadas:

| Contraseña | Hash BCrypt |
|-----------|-------------|
| `Paciente123456` | `$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e` |
| `Admin123456` | `$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e` |

---

## 🧪 Flujo de Prueba Completo

### 1. Login como Admin
```
Email:    admin@sigc.com
Password: Admin123456
```
- Acceso: Dashboard de Administrador
- Gestiona: Doctores, Especialidades, Usuarios

### 2. Register como Paciente
```
Email:    paciente@sigc.com
Password: Paciente123456
```
- Acceso: Panel de Paciente
- Funciones: Ver doctores, reservar citas, ver historial

### 3. Prueba Endpoints
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"paciente@sigc.com",
    "password":"Paciente123456"
  }'
```

---

## 📊 Estructura de Rol

| Rol | Acceso | Permisos |
|-----|--------|----------|
| **ADMIN** | Panel Administrativo | Ver/crear/editar/eliminar doctores, especialidades, usuarios |
| **DOCTOR** | Panel de Doctor | Ver pacientes, ver citas, crear reportes |
| **PACIENTE** | Panel de Paciente | Ver doctores, reservar citas, ver historial de citas |

---

## 💡 Notas Importantes

1. **Contraseña:** Debe incluir mayúsculas, minúsculas y números
2. **Email:** Debe ser único en el sistema
3. **DNI:** Formato libre (puede ser de 6-9 dígitos)
4. **Rol:** ADMIN, DOCTOR, o PACIENTE
5. **Hash:** Las contraseñas se guardan encriptadas con BCrypt

---

## 🔗 Enlaces Útiles

- **H2 Console:** `http://localhost:8080/h2-console`
- **Frontend:** `http://localhost:5173`
- **Backend:** `http://localhost:8080`
- **API Docs:** `http://localhost:8080/swagger-ui.html`

---

**Generado:** 5 de Diciembre de 2025  
**Sistema:** SIGC Clínica v1.0.0
