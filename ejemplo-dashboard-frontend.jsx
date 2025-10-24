// EJEMPLO ESPECÍFICO PARA TU DASHBOARD
// Este archivo contiene 3 ejemplos diferentes - elige el que más te guste

// ====================================================
// SOLUCIÓN 1: Modificar el componente actual
// ====================================================

import { useEffect, useState } from 'react';

function DashboardCliente() {
  const [usuario, setUsuario] = useState({
    nombre: '',
    email: '',
    dni: '',
    telefono: '',
    idUsuario: null
  });

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
      // USAR EL NUEVO ENDPOINT /auth/me
      const response = await fetch('http://localhost:8080/auth/me', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          // Token inválido
          alert('Sesión expirada. Por favor, inicia sesión nuevamente.');
          localStorage.clear();
          window.location.href = '/login';
          return;
        }
        throw new Error('Error al cargar datos');
      }

      const data = await response.json();
      
      // Actualizar estado con los datos recibidos
      setUsuario({
        nombre: data.nombre,
        email: data.email,
        dni: data.dni,
        telefono: data.telefono,
        idUsuario: data.idUsuario
      });

      // Opcional: Actualizar localStorage
      localStorage.setItem('idUsuario', data.idUsuario);
      localStorage.setItem('nombre', data.nombre);
      localStorage.setItem('email', data.email);
      localStorage.setItem('dni', data.dni);
      localStorage.setItem('telefono', data.telefono);

    } catch (error) {
      console.error('Error al cargar usuario:', error);
      alert('Error al cargar datos del usuario. Por favor, intenta de nuevo.');
    }
  }

  return (
    <div className="dashboard-container">
      <div className="sidebar">
        <div className="logo">
          <h2>SIGC</h2>
          <p>Paciente</p>
        </div>
        
        <nav className="menu">
          <a href="#perfil" className="menu-item">
            <i className="icon-user"></i> Mi Perfil
          </a>
          <a href="#citas" className="menu-item">
            <i className="icon-calendar"></i> Mis Citas
          </a>
          <a href="#especialidades" className="menu-item">
            <i className="icon-heart"></i> Especialidades
          </a>
        </nav>

        <button 
          className="btn-logout"
          onClick={() => {
            localStorage.clear();
            window.location.href = '/login';
          }}
        >
          Cerrar sesión
        </button>
      </div>

      <div className="main-content">
        <h1>Bienvenido al Panel del Paciente</h1>

        {/* SECCIÓN DE DATOS DEL USUARIO */}
        <div className="usuario-card">
          <div className="usuario-avatar">
            <i className="icon-user-circle"></i>
          </div>
          
          <div className="usuario-info">
            <h3>Usuario</h3>
            <p className="usuario-nombre">
              {usuario.nombre || 'Sin nombre'}
            </p>
            <p className="usuario-email">
              {usuario.email || 'Sin email'}
            </p>
            <p className="usuario-detalle">
              <strong>DNI:</strong> {usuario.dni || 'No disponible'}
            </p>
            <p className="usuario-detalle">
              <strong>Teléfono:</strong> {usuario.telefono || 'No disponible'}
            </p>
            <p className="usuario-detalle">
              <strong>ID Usuario:</strong> {usuario.idUsuario || 'N/A'}
            </p>
          </div>
        </div>

        {/* RESTO DEL CONTENIDO */}
        <div className="dashboard-sections">
          <div className="section-card">
            <i className="icon-calendar"></i>
            <h3>Mis Citas</h3>
            <p>Ver o cancelar tus citas</p>
          </div>

          <div className="section-card">
            <i className="icon-heart"></i>
            <h3>Especialidades</h3>
            <p>Reserva tu próxima cita médica</p>
          </div>

          <div className="section-card">
            <i className="icon-user"></i>
            <h3>Mi Perfil</h3>
            <p>Edita tus datos personales</p>
          </div>
        </div>

        <div className="proximas-citas">
          <h3>Próximas Citas Activas</h3>
          <p>No tienes citas activas actualmente.</p>
        </div>
      </div>
    </div>
  );
}

// export default DashboardCliente; // ← Solo un export al final del archivo


// ====================================================
// SOLUCIÓN 2: Si usas Axios
// ====================================================

import axios from 'axios';
import { useEffect, useState } from 'react';

// Crear instancia de axios
const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

function DashboardCliente() {
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarDatosUsuario();
  }, []);

  async function cargarDatosUsuario() {
    try {
      setLoading(true);
      const response = await api.get('/auth/me');
      setUsuario(response.data);
    } catch (error) {
      console.error('Error:', error);
      if (error.response?.status === 401) {
        localStorage.clear();
        window.location.href = '/login';
      }
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return <div className="loading">Cargando datos del usuario...</div>;
  }

  if (!usuario) {
    return <div className="error">No se pudieron cargar los datos</div>;
  }

  return (
    <div className="dashboard">
      <h1>Bienvenido {usuario.nombre}</h1>
      <div className="usuario-info">
        <p><strong>Email:</strong> {usuario.email}</p>
        <p><strong>DNI:</strong> {usuario.dni}</p>
        <p><strong>Teléfono:</strong> {usuario.telefono}</p>
        <p><strong>ID:</strong> {usuario.idUsuario}</p>
        <p><strong>Rol:</strong> {usuario.rol}</p>
      </div>
    </div>
  );
}

// export default DashboardCliente; // ← Solo un export al final


// ====================================================
// SOLUCIÓN 3: Con manejo de errores completo
// ====================================================

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
      setError(null);

      const response = await fetch('http://localhost:8080/auth/me', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        if (response.status === 401) {
          // Token inválido o expirado
          localStorage.clear();
          window.location.href = '/login';
          return;
        }
        
        if (response.status === 404) {
          throw new Error('Usuario no encontrado');
        }

        throw new Error(`Error del servidor: ${response.status}`);
      }

      const data = await response.json();
      setUsuario(data);

      // Actualizar localStorage con datos frescos
      Object.entries(data).forEach(([key, value]) => {
        if (value !== null && value !== undefined) {
          localStorage.setItem(key, value);
        }
      });

    } catch (err) {
      console.error('Error al cargar usuario:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  // Estado de carga
  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Cargando datos del usuario...</p>
      </div>
    );
  }

  // Estado de error
  if (error) {
    return (
      <div className="error-container">
        <h3>Error al cargar datos</h3>
        <p>{error}</p>
        <button onClick={cargarDatosUsuario}>Reintentar</button>
        <button onClick={() => {
          localStorage.clear();
          window.location.href = '/login';
        }}>
          Volver al login
        </button>
      </div>
    );
  }

  // Contenido principal
  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <h1>Panel del Paciente</h1>
        <button onClick={() => {
          localStorage.clear();
          window.location.href = '/login';
        }}>
          Cerrar Sesión
        </button>
      </header>

      <main className="dashboard-main">
        <section className="usuario-section">
          <h2>Mis Datos</h2>
          <div className="usuario-card">
            <div className="usuario-avatar">
              {usuario.nombre.charAt(0).toUpperCase()}
            </div>
            <div className="usuario-datos">
              <h3>{usuario.nombre}</h3>
              <div className="dato-item">
                <span className="label">Email:</span>
                <span className="value">{usuario.email}</span>
              </div>
              <div className="dato-item">
                <span className="label">DNI:</span>
                <span className="value">{usuario.dni}</span>
              </div>
              <div className="dato-item">
                <span className="label">Teléfono:</span>
                <span className="value">{usuario.telefono}</span>
              </div>
              <div className="dato-item">
                <span className="label">ID Usuario:</span>
                <span className="value">{usuario.idUsuario}</span>
              </div>
              <div className="dato-item">
                <span className="label">Rol:</span>
                <span className="value badge">{usuario.rol}</span>
              </div>
              <div className="dato-item">
                <span className="label">Estado:</span>
                <span className={`value badge ${usuario.activo ? 'activo' : 'inactivo'}`}>
                  {usuario.activo ? 'Activo' : 'Inactivo'}
                </span>
              </div>
            </div>
          </div>
        </section>

        <section className="acciones-rapidas">
          <h2>Acciones Rápidas</h2>
          <div className="cards-grid">
            <div className="action-card">
              <i className="icon-calendar"></i>
              <h3>Mis Citas</h3>
              <p>Ver o cancelar tus citas</p>
              <button>Ver Citas</button>
            </div>
            <div className="action-card">
              <i className="icon-heart"></i>
              <h3>Especialidades</h3>
              <p>Reserva tu próxima cita médica</p>
              <button>Reservar</button>
            </div>
            <div className="action-card">
              <i className="icon-user"></i>
              <h3>Mi Perfil</h3>
              <p>Edita tus datos personales</p>
              <button>Editar</button>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}

export default DashboardCliente;
