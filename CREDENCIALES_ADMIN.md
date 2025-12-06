# Credenciales del Sistema SIGC

## Usuario Administrador

El sistema crea automáticamente un usuario administrador al iniciar por primera vez.

### 🔐 Credenciales de Administrador

```
Email:    admin@sigc.com
Password: Admin123456
```

### ⚙️ Características

- **Creación automática**: El usuario admin se crea automáticamente al iniciar la aplicación si no existe
- **Rol**: ADMIN
- **Estado**: Activo
- **DNI**: 00000000 (placeholder)
- **Teléfono**: 999999999 (placeholder)

### ⚠️ IMPORTANTE - Seguridad

1. **Cambia la contraseña** después del primer inicio de sesión
2. **No compartas** estas credenciales en producción
3. **Actualiza el DNI y teléfono** del administrador con datos reales

### 📝 Cómo usar

#### Endpoint de Login

```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "admin@sigc.com",
  "password": "Admin123456"
}
```

#### Con PowerShell

```powershell
$body = @{
    email = "admin@sigc.com
    password = "Admin123456"
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8080/auth/login' -Method POST -Headers @{"Content-Type"="application/json"} -Body $body
```

#### Con cURL

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sigc.com","password":"Admin123456"}'
```

### 🔄 Reinicialización

Si necesitas recrear el usuario administrador:

1. Elimina el usuario de la base de datos:
   ```sql
   DELETE FROM usuarios WHERE email = 'admin@sigc.com';
   ```

2. Reinicia la aplicación - el usuario se creará automáticamente

### 👥 Otros Roles

El sistema soporta tres roles:

- **ADMIN**: Acceso completo al sistema
- **DOCTOR**: Gestión de cit"as y pacientes
- **PACIENTE**: Gestión de sus propias citas

---

**Fecha de creación**: 24 de octubre de 2025  
**Generado automáticamente por**: DataInitializer.java
