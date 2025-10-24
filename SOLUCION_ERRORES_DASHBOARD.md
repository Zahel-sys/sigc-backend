# 🔧 SOLUCIÓN DE ERRORES EN EL DASHBOARD

## ❌ Problema Detectado

El error en la consola del navegador:
```
GET http://localhost:5175/usuarios/1flores@gmail.com 500 (Internal Server Error)
Error al cargar datos del dashboard:
AxiosError {message: 'Request failed with status code 500', ...}
```

## 🔍 Causa Raíz

El problema ocurre porque:

1. **Token antiguo**: El token JWT guardado en `localStorage` tiene el EMAIL en el campo "sub" en lugar del ID
2. **Usuario inexistente**: El email `1flores@gmail.com` no existe en la base de datos actual
3. **Frontend desactualizado**: El código del frontend está intentando cargar el usuario usando el formato antiguo

## ✅ SOLUCIONES (3 opciones)

### **Solución 1: Hacer Login Nuevamente (MÁS FÁCIL) ⭐**

La forma más sencilla de resolver este problema:

1. **Cerrar sesión** en el frontend
2. **Limpiar localStorage**:
   ```javascript
   localStorage.clear();
   ```
3. **Iniciar sesión nuevamente** con credenciales válidas
4. El nuevo token tendrá el formato correcto (ID en "sub")

**Usuarios disponibles para login:**
- Email: `lfloresb@gmail.com` (ID: 8)
- Email: `jclementc@gmail.com` (ID: 10)
- Email: `admin@sigc.com` (ID: 11)
- Email: `testjwt@test.com` (ID: 12)

---

### **Solución 2: Actualizar el Frontend para usar /auth/me (RECOMENDADO) ⭐⭐⭐**

Modifica el código del dashboard para usar el nuevo endpoint `/auth/me`:

#### **Antes (código antiguo con error):**
```javascript
// ❌ NO HAGAS ESTO
const token = localStorage.getItem('token');
const userId = localStorage.getItem('idUsuario'); // Puede ser email antiguo
const response = await axios.get(`http://localhost:8080/usuarios/${userId}`, {
  headers: { Authorization: `Bearer ${token}` }
});
```

#### **Después (código correcto):**
```javascript
// ✅ USA ESTE CÓDIGO
const token = localStorage.getItem('token');
const response = await axios.get('http://localhost:8080/auth/me', {
  headers: { Authorization: `Bearer ${token}` }
});

const usuario = response.data;
console.log('Usuario autenticado:', usuario);

// El endpoint /auth/me retorna:
// {
//   idUsuario: 12,
//   nombre: "Test JWT",
//   email: "testjwt@test.com",
//   dni: "12345678",
//   telefono: "987654321",
//   rol: "PACIENTE",
//   activo: true,
//   fechaRegistro: "2025-01-15T10:30:00"
// }
```

#### **Código completo del componente Dashboard:**
```javascript
import { useState, useEffect } from 'react';
import axios from 'axios';

function DashboardCliente() {
  const [usuario, setUsuario] = useState({
    nombre: 'Cargando...',
    email: '',
    dni: '',
    telefono: ''
  });

  useEffect(() => {
    cargarDatosUsuario();
  }, []);

  async function cargarDatosUsuario() {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        alert('No hay sesión activa. Redirigiendo al login...');
        window.location.href = '/login';
        return;
      }

      // ✅ USAR EL NUEVO ENDPOINT /auth/me
      const response = await axios.get('http://localhost:8080/auth/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      const data = response.data;
      
      setUsuario({
        nombre: data.nombre || 'Sin nombre',
        email: data.email || 'Sin email',
        dni: data.dni || 'No disponible',
        telefono: data.telefono || 'No disponible'
      });

      // Opcional: Actualizar localStorage
      localStorage.setItem('idUsuario', data.idUsuario);
      localStorage.setItem('nombre', data.nombre);
      localStorage.setItem('email', data.email);

    } catch (error) {
      console.error('Error al cargar usuario:', error);
      
      if (error.response?.status === 401) {
        alert('Sesión expirada. Por favor, inicia sesión nuevamente.');
        localStorage.clear();
        window.location.href = '/login';
      } else {
        alert('Error al cargar datos del usuario.');
      }
    }
  }

  return (
    <div className="dashboard-container">
      <div className="user-info">
        <h2>Bienvenido, {usuario.nombre}</h2>
        <p>Email: {usuario.email}</p>
        <p>DNI: {usuario.dni}</p>
        <p>Teléfono: {usuario.telefono}</p>
      </div>
    </div>
  );
}

export default DashboardCliente;
```

---

### **Solución 3: Crear el Usuario Faltante (TEMPORAL)**

Si necesitas mantener el email `1flores@gmail.com`, crea el usuario en la BD:

```sql
INSERT INTO usuarios (nombre, email, password, dni, telefono, rol, activo, fecha_registro)
VALUES (
  'Leonardo Flores',
  '1flores@gmail.com',
  '$2a$10$...',  -- Password encriptado con BCrypt
  '12345678',
  '987654321',
  'PACIENTE',
  1,
  NOW()
);
```

**Nota**: Esta solución es temporal. Lo mejor es usar la Solución 2.

---

## 🎯 Endpoint Actualizado: /usuarios/{idOrEmail}

El backend ahora acepta **AMBOS** formatos:

### ✅ Por ID (recomendado):
```
GET http://localhost:8080/usuarios/12
```

### ✅ Por Email (retrocompatibilidad):
```
GET http://localhost:8080/usuarios/testjwt@test.com
```

### ✅ Endpoint dedicado:
```
GET http://localhost:8080/usuarios/email/testjwt@test.com
```

---

## 📊 Estado Actual del Backend

### ✅ Endpoints Funcionando:
- `POST /auth/login` - Login con token nuevo (ID en "sub")
- `POST /auth/register` - Registro de usuarios
- `GET /auth/me` - **NUEVO**: Obtener usuario autenticado desde token
- `GET /auth/profile` - Alias de /auth/me
- `POST /auth/validate-token` - Validar token
- `POST /auth/refresh-token` - Renovar token antiguo
- `GET /auth/token-info` - Info del token
- `GET /usuarios/{idOrEmail}` - **ACTUALIZADO**: Acepta ID o Email
- `GET /usuarios/email/{email}` - Búsqueda por email

### 📋 Usuarios Disponibles:
| ID  | Email                      | Nombre                      |
|-----|----------------------------|-----------------------------|
| 8   | lfloresb@gmail.com         | Leonardo                    |
| 10  | jclementc@gmail.com        | Juan                        |
| 11  | admin@sigc.com             | Administrador del Sistema   |
| 12  | testjwt@test.com           | Test JWT                    |

---

## 🚀 Próximos Pasos

1. ✅ **Backend**: Ya corregido y funcionando
2. ⏳ **Frontend**: Actualizar para usar `/auth/me`
3. ⏳ **Testing**: Probar el flujo completo de login → dashboard
4. ⏳ **Documentación**: Actualizar README del proyecto

---

## 💡 Recomendación Final

**USA LA SOLUCIÓN 2**: Actualizar el frontend para usar el endpoint `/auth/me`

**Ventajas:**
- ✅ No necesitas saber el ID del usuario
- ✅ Funciona con tokens nuevos y antiguos
- ✅ El backend extrae automáticamente el usuario del token
- ✅ Más seguro (el usuario solo puede ver sus propios datos)
- ✅ Código más limpio en el frontend

---

## 🆘 Si Sigues Teniendo Errores

1. Abre la consola del navegador (F12)
2. Ve a la pestaña **Application** → **Local Storage**
3. Elimina todos los items de localStorage
4. Haz login nuevamente
5. Verifica que el token nuevo tenga el formato correcto

**Verificar token:**
```javascript
const token = localStorage.getItem('token');
const payload = JSON.parse(atob(token.split('.')[1]));
console.log('Token payload:', payload);
// Debe mostrar: { sub: "12", email: "...", rol: "..." }
```
