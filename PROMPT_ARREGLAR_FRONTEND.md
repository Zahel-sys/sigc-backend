# 🔧 GUÍA COMPLETA PARA ARREGLAR EL FRONTEND DEL DASHBOARD

## ❌ PROBLEMA ACTUAL

El dashboard muestra estos errores en la consola del navegador:
```
GET http://localhost:5175/usuarios/1flores@gmail.com 500 (Internal Server Error)
Error al cargar datos del dashboard
AxiosError {message: 'Request failed with status code 500', ...}
```

Y en pantalla aparece:
- "Sin email"
- "DNI: No disponible"
- Datos del usuario no se cargan

---

## 🔍 CAUSA DEL PROBLEMA

1. El frontend está usando un **token JWT antiguo** que tiene el email en el campo "sub" en lugar del ID numérico
2. El código intenta hacer `GET /usuarios/{email}` que no funciona correctamente
3. El usuario `1flores@gmail.com` no existe en la base de datos actual

---

## ✅ SOLUCIÓN COMPLETA

### PASO 1: Ubicar el archivo del Dashboard

Busca el archivo del componente Dashboard, probablemente:
- `DashboardCliente.jsx`
- `Dashboard.jsx`
- `DashboardPaciente.jsx`
- `pages/Dashboard.jsx`

### PASO 2: Reemplazar el código completo

**COPIA Y PEGA ESTE CÓDIGO COMPLETO en tu componente Dashboard:**

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
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    cargarDatosUsuario();
  }, []);

  async function cargarDatosUsuario() {
    try {
      setLoading(true);
      setError(null);
      
      const token = localStorage.getItem('token');
      
      if (!token) {
        alert('No hay sesión activa. Redirigiendo al login...');
        localStorage.clear();
        window.location.href = '/login';
        return;
      }

      // ✅ CAMBIO PRINCIPAL: Usar /auth/me en lugar de /usuarios/{id}
      const response = await axios.get('http://localhost:8080/auth/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      const data = response.data;
      
      setUsuario({
        idUsuario: data.idUsuario,
        nombre: data.nombre || 'Sin nombre',
        email: data.email || 'Sin email',
        dni: data.dni || 'No disponible',
        telefono: data.telefono || 'No disponible',
        rol: data.rol,
        activo: data.activo
      });

      // Actualizar localStorage con datos frescos
      localStorage.setItem('idUsuario', data.idUsuario);
      localStorage.setItem('nombre', data.nombre);
      localStorage.setItem('email', data.email);
      
      setLoading(false);

    } catch (error) {
      console.error('Error al cargar usuario:', error);
      setLoading(false);
      
      if (error.response?.status === 401) {
        alert('Sesión expirada. Por favor, inicia sesión nuevamente.');
        localStorage.clear();
        window.location.href = '/login';
      } else if (error.response?.status === 404) {
        setError('Usuario no encontrado. Token antiguo detectado.');
        alert('Por favor, cierra sesión y vuelve a iniciar sesión.');
      } else {
        setError('Error al cargar datos del usuario.');
      }
    }
  }

  if (loading) {
    return (
      <div className="dashboard-container">
        <div style={{ padding: '20px', textAlign: 'center' }}>
          <p>Cargando datos del usuario...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <div className="error-message" style={{ padding: '20px', color: 'red' }}>
          <p>{error}</p>
          <button onClick={() => {
            localStorage.clear();
            window.location.href = '/login';
          }}>
            Volver al login
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="sidebar">
        <div className="logo">
          <h2>SIGC</h2>
          <p>{usuario.rol || 'Usuario'}</p>
        </div>
        <nav className="menu">
          <ul>
            <li><a href="/dashboard">Inicio</a></li>
            <li><a href="/citas">Mis Citas</a></li>
            <li><a href="/perfil">Mi Perfil</a></li>
          </ul>
        </nav>
      </div>
      
      <div className="main-content">
        <header className="header">
          <h1>Dashboard de Paciente</h1>
          <div className="user-info">
            <p><strong>Bienvenido:</strong> {usuario.nombre}</p>
            <p><strong>Email:</strong> {usuario.email}</p>
            <p><strong>DNI:</strong> {usuario.dni}</p>
            <p><strong>Teléfono:</strong> {usuario.telefono}</p>
          </div>
        </header>
        
        <div className="content">
          {/* Aquí va el resto del contenido de tu dashboard */}
          <div className="cards">
            <div className="card">
              <h3>Próximas Citas</h3>
              <p>Ver mis citas programadas</p>
            </div>
            <div className="card">
              <h3>Historial Médico</h3>
              <p>Ver mi historial</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DashboardCliente;
```

---

## 🔄 PASO 3: Limpiar el localStorage (MUY IMPORTANTE)

Después de actualizar el código, abre la consola del navegador (F12) y ejecuta:

```javascript
localStorage.clear();
location.reload();
```

---

## 🔐 PASO 4: Hacer login nuevamente

Usa cualquiera de estos usuarios para hacer login:

| Email | ID |
|-------|------|
| lfloresb@gmail.com | 8 |
| jclementc@gmail.com | 10 |
| admin@sigc.com | 11 |
| testjwt@test.com (password: test123) | 12 |

---

## 📋 CAMBIOS CLAVE EN EL CÓDIGO

### ❌ ANTES (código con error):
```javascript
const userId = localStorage.getItem('idUsuario'); // Puede ser email
const response = await axios.get(`http://localhost:8080/usuarios/${userId}`, {
  headers: { Authorization: `Bearer ${token}` }
});
```

### ✅ DESPUÉS (código correcto):
```javascript
const response = await axios.get('http://localhost:8080/auth/me', {
  headers: { Authorization: `Bearer ${token}` }
});
```

---

## 🎯 VENTAJAS DEL NUEVO ENDPOINT /auth/me

1. ✅ No necesitas saber el ID del usuario
2. ✅ El token contiene toda la información
3. ✅ Funciona con tokens nuevos y antiguos
4. ✅ Más seguro (el usuario solo ve sus propios datos)
5. ✅ Maneja errores automáticamente

---

## ✅ VERIFICACIÓN DE QUE FUNCIONA

Después de los cambios, verifica:

1. **Consola del navegador (F12 → Console):**
   - ✅ NO debe haber errores 500
   - ✅ Debe aparecer: `GET http://localhost:8080/auth/me 200 OK`

2. **Pestaña Network (F12 → Network):**
   - Busca la petición `/auth/me`
   - Estado: `200 OK`
   - Response debe mostrar tus datos en JSON

3. **En pantalla:**
   - ✅ Debe mostrar tu nombre correcto
   - ✅ Debe mostrar tu email correcto
   - ✅ Debe mostrar tu DNI
   - ✅ Ya NO debe decir "Sin email" ni "No disponible"

---

## 🆘 SI AÚN HAY ERRORES

### Error: "Token inválido" o 401
**Solución:**
```javascript
// En la consola del navegador
localStorage.clear();
// Luego haz login nuevamente
```

### Error: "Usuario no encontrado" o 404
**Solución:** El token es muy antiguo. Haz lo siguiente:
1. Cierra sesión
2. Ejecuta: `localStorage.clear()`
3. Vuelve a hacer login

### Error: "Network Error"
**Verificar:**
1. El backend está corriendo en `http://localhost:8080`
2. Ejecuta en terminal: `netstat -ano | findstr :8080`
3. Debe aparecer: `TCP 0.0.0.0:8080 ... LISTENING`

---

## 📊 ENDPOINTS DISPONIBLES EN EL BACKEND

| Endpoint | Método | Descripción | ¿Requiere Token? |
|----------|--------|-------------|------------------|
| `/auth/login` | POST | Iniciar sesión | ❌ No |
| `/auth/me` | GET | **✅ USA ESTE** - Obtener usuario actual | ✅ Sí |
| `/auth/profile` | GET | Alias de /auth/me | ✅ Sí |
| `/usuarios/{id}` | GET | Obtener por ID | ❌ No |
| `/usuarios/email/{email}` | GET | Obtener por email | ❌ No |

---

## 💡 EJEMPLO DE RESPUESTA DEL ENDPOINT /auth/me

Cuando llamas a `GET /auth/me`, el servidor responde:

```json
{
  "idUsuario": 12,
  "nombre": "Leonardo Flores",
  "email": "lfloresb@gmail.com",
  "dni": "12345678",
  "telefono": "987654321",
  "rol": "PACIENTE",
  "activo": true,
  "fechaRegistro": "2025-01-15T10:30:00"
}
```

---

## 🚀 RESUMEN RÁPIDO (TL;DR)

1. **Cambiar** la URL de `'/usuarios/' + userId` a `'/auth/me'`
2. **Ejecutar** `localStorage.clear()` en la consola
3. **Hacer login** nuevamente
4. **Verificar** que el dashboard muestra los datos correctamente

---

## ✅ CHECKLIST FINAL

- [ ] Actualicé el código del Dashboard con el nuevo endpoint `/auth/me`
- [ ] Limpié el localStorage con `localStorage.clear()`
- [ ] Hice login nuevamente
- [ ] Verificué en la consola que no hay errores 500
- [ ] El dashboard muestra mi nombre correctamente
- [ ] El dashboard muestra mi email correctamente
- [ ] El dashboard muestra mi DNI correctamente

---

**¡Listo! El dashboard ahora debe funcionar correctamente sin errores. 🎉**

Si necesitas más ayuda, revisa el archivo `SOLUCION_ERRORES_DASHBOARD.md` para información adicional.
