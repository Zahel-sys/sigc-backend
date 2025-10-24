# 🎯 SOLUCIÓN DEFINITIVA - Cargar Datos del Usuario en el Dashboard

## ✅ Nuevo Endpoint: `/auth/me`

He creado un endpoint que **automáticamente** obtiene los datos del usuario autenticado desde el token JWT, sin importar si es un token antiguo o nuevo.

---

## 🚀 Uso en el Frontend

### **Código para tu Dashboard (DashboardCliente.jsx o similar)**

Reemplaza tu código actual que intenta cargar los datos del usuario con esto:

```javascript
import { useEffect, useState } from 'react';

function DashboardCliente() {
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    cargarDatosUsuario();
  }, []);

  async function cargarDatosUsuario() {
    const token = localStorage.getItem('token');

    if (!token) {
      window.location.href = '/login';
      return;
    }

    try {
      setLoading(true);
      
      // NUEVO ENDPOINT - Obtiene usuario automáticamente del token
      const response = await fetch('http://localhost:8080/auth/me', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          // Token inválido o expirado
          localStorage.clear();
          window.location.href = '/login';
          return;
        }
        throw new Error('Error al cargar datos del usuario');
      }

      const data = await response.json();
      
      // Guardar datos en el estado
      setUsuario(data);
      
      // Opcional: Actualizar localStorage con datos frescos
      localStorage.setItem('idUsuario', data.idUsuario);
      localStorage.setItem('nombre', data.nombre);
      localStorage.setItem('email', data.email);
      localStorage.setItem('dni', data.dni);
      localStorage.setItem('telefono', data.telefono);
      localStorage.setItem('rol', data.rol);
      
      setError(null);
    } catch (err) {
      console.error('Error al cargar usuario:', err);
      setError('No se pudieron cargar los datos del usuario');
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return <div>Cargando...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="dashboard">
      <h1>Bienvenido al Panel del Paciente</h1>
      
      <div className="usuario-info">
        <h3>Usuario</h3>
        <p><strong>Nombre:</strong> {usuario?.nombre || 'Sin nombre'}</p>
        <p><strong>Email:</strong> {usuario?.email || 'Sin email'}</p>
        <p><strong>DNI:</strong> {usuario?.dni || 'No disponible'}</p>
        <p><strong>Teléfono:</strong> {usuario?.telefono || 'No disponible'}</p>
        <p><strong>ID Usuario:</strong> {usuario?.idUsuario}</p>
        <p><strong>Rol:</strong> {usuario?.rol}</p>
      </div>
    </div>
  );
}

export default DashboardCliente;
```

---

## 📊 Alternativa: Usar React Query (Recomendado)

Si usas React Query (TanStack Query):

```javascript
import { useQuery } from '@tanstack/react-query';

function DashboardCliente() {
  const { data: usuario, isLoading, error } = useQuery({
    queryKey: ['usuario', 'me'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      
      const response = await fetch('http://localhost:8080/auth/me', {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.clear();
          window.location.href = '/login';
        }
        throw new Error('Error al cargar usuario');
      }

      return response.json();
    },
    staleTime: 5 * 60 * 1000, // 5 minutos
    retry: 1
  });

  if (isLoading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div className="dashboard">
      <h1>Bienvenido {usuario.nombre}</h1>
      <p>Email: {usuario.email}</p>
      <p>DNI: {usuario.dni}</p>
      <p>Teléfono: {usuario.telefono}</p>
    </div>
  );
}
```

---

## 🔧 Alternativa: Crear un Custom Hook

```javascript
// hooks/useUsuario.js
import { useState, useEffect } from 'react';

export function useUsuario() {
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    cargarUsuario();
  }, []);

  async function cargarUsuario() {
    const token = localStorage.getItem('token');

    if (!token) {
      window.location.href = '/login';
      return;
    }

    try {
      setLoading(true);
      const response = await fetch('http://localhost:8080/auth/me', {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) throw new Error('Error al cargar usuario');

      const data = await response.json();
      setUsuario(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return { usuario, loading, error, recargar: cargarUsuario };
}

// Uso en cualquier componente:
function DashboardCliente() {
  const { usuario, loading, error } = useUsuario();

  if (loading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>Bienvenido {usuario.nombre}</h1>
      <p>DNI: {usuario.dni}</p>
      <p>Email: {usuario.email}</p>
    </div>
  );
}
```

---

## 🎯 Crear un Contexto Global del Usuario

```javascript
// context/UsuarioContext.jsx
import { createContext, useContext, useState, useEffect } from 'react';

const UsuarioContext = createContext();

export function UsuarioProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarUsuario();
  }, []);

  async function cargarUsuario() {
    const token = localStorage.getItem('token');
    
    if (!token) {
      setLoading(false);
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/auth/me', {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        const data = await response.json();
        setUsuario(data);
      } else {
        localStorage.clear();
      }
    } catch (err) {
      console.error('Error al cargar usuario:', err);
    } finally {
      setLoading(false);
    }
  }

  function cerrarSesion() {
    localStorage.clear();
    setUsuario(null);
    window.location.href = '/login';
  }

  return (
    <UsuarioContext.Provider value={{ usuario, loading, cerrarSesion }}>
      {children}
    </UsuarioContext.Provider>
  );
}

export function useUsuario() {
  return useContext(UsuarioContext);
}

// En tu App.jsx:
import { UsuarioProvider } from './context/UsuarioContext';

function App() {
  return (
    <UsuarioProvider>
      {/* tus rutas */}
    </UsuarioProvider>
  );
}

// En cualquier componente:
import { useUsuario } from './context/UsuarioContext';

function DashboardCliente() {
  const { usuario, loading, cerrarSesion } = useUsuario();

  if (loading) return <div>Cargando...</div>;
  if (!usuario) return <div>No autenticado</div>;

  return (
    <div>
      <h1>Bienvenido {usuario.nombre}</h1>
      <p>ID: {usuario.idUsuario}</p>
      <p>DNI: {usuario.dni}</p>
      <p>Email: {usuario.email}</p>
      <button onClick={cerrarSesion}>Cerrar Sesión</button>
    </div>
  );
}
```

---

## 🧪 Verificación

### Backend:
```powershell
# Hacer login
$login = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"testjwt@test.com","password":"12345678"}'

# Obtener datos del usuario
$token = $login.token
Invoke-RestMethod -Uri "http://localhost:8080/auth/me" -Headers @{"Authorization"="Bearer $token"}
```

### Frontend (consola del navegador):
```javascript
// Probar endpoint
const token = localStorage.getItem('token');
const response = await fetch('http://localhost:8080/auth/me', {
  headers: { 'Authorization': `Bearer ${token}` }
});
const usuario = await response.json();
console.log(usuario);
// Deberías ver: {idUsuario: 12, nombre: "...", email: "...", dni: "...", etc}
```

---

## 📋 Endpoints Disponibles

| Método | Endpoint | Descripción | Requiere Token |
|--------|----------|-------------|----------------|
| GET | `/auth/me` | Obtiene usuario autenticado | ✅ Sí |
| GET | `/auth/profile` | Alias de `/auth/me` | ✅ Sí |
| GET | `/usuarios/{id}` | Obtiene usuario por ID | ❌ No |
| GET | `/usuarios/email/{email}` | Obtiene usuario por email | ❌ No |

---

## ✅ Ventajas del Endpoint `/auth/me`

1. ✅ **No necesitas saber el ID del usuario** - Lo extrae automáticamente del token
2. ✅ **Compatible con tokens antiguos** - Maneja tanto tokens nuevos (ID en sub) como antiguos (email en sub)
3. ✅ **Más seguro** - El usuario solo puede ver sus propios datos
4. ✅ **Estándar REST** - Sigue el patrón `/me` usado por OAuth2 y APIs modernas
5. ✅ **Menos errores** - No hay riesgo de pasar un ID incorrecto

---

## 🎯 Resumen de Cambios en el Frontend

**ANTES (código que causaba error):**
```javascript
const email = parseJwt(token).sub;  // Podría ser email o ID
const response = await fetch(`http://localhost:8080/usuarios/${email}`);
```

**AHORA (código correcto):**
```javascript
const response = await fetch('http://localhost:8080/auth/me', {
  headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
});
```

---

**Fecha:** 24 de octubre de 2025  
**Endpoint:** `/auth/me`  
**Estado:** ✅ FUNCIONAL  
**Probado:** ✅ SÍ
