# Guía Rápida - PUT /usuarios/cambiar-password

## 🚀 Quick Start

### 1. Request
```bash
PUT http://localhost:8080/usuarios/cambiar-password
Authorization: Bearer {token}
Content-Type: application/json

{
  "passwordActual": "password123",
  "passwordNueva": "nuevoPassword456",
  "passwordConfirmar": "nuevoPassword456"
}
```

### 2. Success Response (200)
```json
{
  "idUsuario": 1,
  "email": "usuario@example.com",
  "mensaje": "Contraseña cambiada exitosamente",
  "timestamp": "2025-11-19T19:30:00",
  "exitoso": true
}
```

### 3. Error Response
```json
{
  "error": "Error message here",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

## 📋 Validaciones

| Regla | Código | Error |
|-------|--------|-------|
| Token required | 401 | "Token JWT no proporcionado" |
| Token valid | 401 | "Token JWT inválido o expirado" |
| User exists | 404 | "Usuario no encontrado" |
| Fields required | 400 | "Todos los campos de password son requeridos" |
| Current password correct | 400 | "Contraseña actual incorrecta" |
| New passwords match | 422 | "Las nuevas contraseñas no coinciden" |
| Different from current | 422 | "La nueva contraseña debe ser diferente a la actual" |
| Min 6 characters | 422 | "La contraseña debe tener al menos 6 caracteres" |

---

## 💻 PowerShell Test

```powershell
# 1. Login
$login = Invoke-RestMethod `
  -Uri "http://localhost:8080/auth/login" `
  -Method Post `
  -Body (@{
    email = "user@example.com"
    password = "password123"
  } | ConvertTo-Json) `
  -ContentType "application/json"

$token = $login.token

# 2. Change Password
$body = @{
  passwordActual = "password123"
  passwordNueva = "newPass456"
  passwordConfirmar = "newPass456"
} | ConvertTo-Json

$result = Invoke-RestMethod `
  -Uri "http://localhost:8080/usuarios/cambiar-password" `
  -Method Put `
  -Headers @{"Authorization" = "Bearer $token"} `
  -Body $body `
  -ContentType "application/json"

Write-Host $result | ConvertTo-Json
```

---

## 🎨 React Component

```jsx
import { useState } from 'react';

export default function CambiarPassword() {
  const [form, setForm] = useState({
    passwordActual: '',
    passwordNueva: '',
    passwordConfirmar: ''
  });
  const [msg, setMsg] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem('token');
    if (!token) {
      setMsg('❌ Not authenticated');
      return;
    }

    try {
      const res = await fetch('http://localhost:8080/usuarios/cambiar-password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(form)
      });

      const data = await res.json();

      if (res.ok) {
        setMsg(`✅ ${data.mensaje}`);
        setForm({ passwordActual: '', passwordNueva: '', passwordConfirmar: '' });
      } else {
        setMsg(`❌ ${data.error}`);
      }
    } catch (err) {
      setMsg(`❌ Error: ${err.message}`);
    }
  };

  return (
    <div>
      <h2>🔒 Change Password</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="password"
          placeholder="Current password"
          value={form.passwordActual}
          onChange={(e) => setForm({...form, passwordActual: e.target.value})}
          required
        />
        <input
          type="password"
          placeholder="New password"
          value={form.passwordNueva}
          onChange={(e) => setForm({...form, passwordNueva: e.target.value})}
          required
        />
        <input
          type="password"
          placeholder="Confirm password"
          value={form.passwordConfirmar}
          onChange={(e) => setForm({...form, passwordConfirmar: e.target.value})}
          required
        />
        <button type="submit">Change Password</button>
      </form>
      {msg && <p>{msg}</p>}
    </div>
  );
}
```

---

## 🔍 cURL Examples

### Success
```bash
curl -X PUT http://localhost:8080/usuarios/cambiar-password \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "passwordActual": "password123",
    "passwordNueva": "newPass456",
    "passwordConfirmar": "newPass456"
  }'
```

### Missing Token (401)
```bash
curl -X PUT http://localhost:8080/usuarios/cambiar-password \
  -H "Content-Type: application/json" \
  -d '{
    "passwordActual": "password123",
    "passwordNueva": "newPass456",
    "passwordConfirmar": "newPass456"
  }'
```

### Wrong Current Password (400)
```bash
curl -X PUT http://localhost:8080/usuarios/cambiar-password \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{
    "passwordActual": "wrongPassword",
    "passwordNueva": "newPass456",
    "passwordConfirmar": "newPass456"
  }'
```

### Passwords Don't Match (422)
```bash
curl -X PUT http://localhost:8080/usuarios/cambiar-password \
  -H "Authorization: Bearer eyJ..." \
  -H "Content-Type: application/json" \
  -d '{
    "passwordActual": "password123",
    "passwordNueva": "newPass456",
    "passwordConfirmar": "different"
  }'
```

---

## ⚡ Key Points

✅ **Required Headers**
- `Authorization: Bearer {token}` (mandatory)
- `Content-Type: application/json`

✅ **Required Fields**
- `passwordActual` (current password)
- `passwordNueva` (new password, min 6 chars)
- `passwordConfirmar` (must match passwordNueva)

⚠️ **Constraints**
- Password must be >= 6 characters
- New password must differ from current
- New passwords must match
- Current password must be correct
- Token must be valid and not expired

❌ **Common Errors**
- 401: Missing or invalid token
- 400: Wrong current password or empty fields
- 422: Passwords don't match or too short
- 404: User not found
- 500: Server error

---

## 📁 Related Files

- `TESTING_PUT_CAMBIAR_PASSWORD.md` - 10 detailed test cases
- `PROMPT_CAMBIAR_PASSWORD_FRONTEND.md` - Full React component
- `RESUMEN_PUT_CAMBIAR_PASSWORD.md` - Complete summary

---

## ✅ Implementation Status

| Component | Status |
|-----------|--------|
| Backend Endpoint | ✅ Complete |
| DTOs | ✅ Complete |
| Validations (8) | ✅ Complete |
| Authentication | ✅ Integrated |
| Encryption | ✅ BCryptPasswordEncoder |
| Documentation | ✅ Complete |
| Frontend React | ✅ Example Provided |
| Testing | ✅ 10 Cases Documented |

---

## 🎯 Remember

- Always get token from login first
- Send token in `Authorization` header with `Bearer ` prefix
- All password fields are required
- Response does NOT contain password
- Use HTTPS in production

🚀 Ready to use!
