# 📝 PROMPT para Implementar Reserva de Citas en Frontend

## 🎯 Objetivo
Implementar la funcionalidad de reserva de citas médicas en el frontend React que consume el endpoint POST `/citas` del backend.

## 📋 Requisitos

### 1. El usuario debe poder:
- Ver los horarios disponibles de un doctor
- Seleccionar un horario específico
- Hacer clic en un botón "Reservar" para crear la cita
- Ver confirmación exitosa o mensaje de error

### 2. Validaciones en el Frontend:
- Verificar que el usuario esté autenticado (token disponible)
- Mostrar loading mientras se procesa la reserva
- Mostrar mensajes de error claros

### 3. Integración con Backend:
- Endpoint: `POST http://localhost:8080/citas`
- Header: `Authorization: Bearer {token}`
- Body: `{ usuario: { idUsuario: 1 }, horario: { idHorario: 5 } }`

---

## 💻 Componente React Completo

Crea un archivo `ReservarCita.jsx` con este código:

```jsx
import React, { useState } from 'react';
import './ReservarCita.css'; // Importar estilos (abajo)

function ReservarCita({ horarioId, horario, onCitaCreada }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [citaCreada, setCitaCreada] = useState(null);

  // Obtener el ID del usuario autenticado del localStorage
  const getIdPaciente = () => {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    return usuario.idUsuario || null;
  };

  // Obtener el token JWT
  const getToken = () => {
    return localStorage.getItem('token') || null;
  };

  const handleReservar = async () => {
    setLoading(true);
    setError(null);
    setCitaCreada(null);

    try {
      // Validar token
      const token = getToken();
      if (!token) {
        throw new Error('No hay sesión activa. Por favor, inicia sesión.');
      }

      // Validar que se tiene el ID del paciente
      const idPaciente = getIdPaciente();
      if (!idPaciente) {
        throw new Error('No se encontró información del usuario. Por favor, inicia sesión nuevamente.');
      }

      // Validar que se proporcionó horarioId
      if (!horarioId) {
        throw new Error('Horario no válido');
      }

      // Realizar la petición al backend
      const response = await fetch('http://localhost:8080/citas', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          usuario: {
            idUsuario: idPaciente
          },
          horario: {
            idHorario: horarioId
          }
        })
      });

      // Procesar respuesta
      const data = await response.json();

      // Manejar errores según el código de estado
      if (response.status === 401) {
        throw new Error('Sesión expirada. Por favor, inicia sesión de nuevo.');
      } else if (response.status === 400) {
        throw new Error(data.error || 'Datos inválidos');
      } else if (response.status === 404) {
        throw new Error(data.error || 'Paciente u horario no encontrado');
      } else if (response.status === 409) {
        throw new Error(data.error || 'Este horario ya no está disponible');
      } else if (response.status === 422) {
        throw new Error(data.error || 'No se puede reservar en un horario pasado');
      } else if (!response.ok) {
        throw new Error(data.error || 'Error al crear la cita');
      }

      // Éxito
      setCitaCreada(data);
      console.log('✅ Cita creada exitosamente:', data);

      // Ejecutar callback si existe
      if (onCitaCreada) {
        onCitaCreada(data);
      }

      // Limpiar estado después de 3 segundos
      setTimeout(() => {
        setCitaCreada(null);
        setError(null);
      }, 3000);

    } catch (err) {
      console.error('❌ Error:', err.message);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reservar-cita-container">
      {/* Información del horario */}
      {horario && (
        <div className="horario-info">
          <p><strong>Fecha:</strong> {horario.fecha}</p>
          <p><strong>Turno:</strong> {horario.turno}</p>
          <p><strong>Hora:</strong> {horario.horaInicio} - {horario.horaFin}</p>
          <p className="disponibilidad">
            {horario.disponible ? (
              <span className="disponible">✓ Disponible</span>
            ) : (
              <span className="ocupado">✗ Ocupado</span>
            )}
          </p>
        </div>
      )}

      {/* Botón de reservar */}
      <button 
        onClick={handleReservar} 
        disabled={loading || !horario?.disponible}
        className="btn-reservar"
      >
        {loading ? '⏳ Reservando...' : '📅 Reservar Cita'}
      </button>

      {/* Mensaje de error */}
      {error && (
        <div className="error-message">
          <span>❌ {error}</span>
        </div>
      )}

      {/* Confirmación de éxito */}
      {citaCreada && (
        <div className="success-message">
          <p>✅ ¡Cita reservada exitosamente!</p>
          <p><strong>ID de Cita:</strong> {citaCreada.idCita}</p>
          <p><strong>Estado:</strong> {citaCreada.estado}</p>
          <p><strong>Doctor:</strong> {citaCreada.doctor.nombre}</p>
          <p><strong>Fecha:</strong> {citaCreada.fechaCita}</p>
          <p><strong>Hora:</strong> {citaCreada.horaCita}</p>
        </div>
      )}
    </div>
  );
}

export default ReservarCita;
```

---

## 🎨 Archivo de Estilos (ReservarCita.css)

```css
.reservar-cita-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  margin-top: 15px;
  border-left: 4px solid #007bff;
}

.horario-info {
  background: white;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 15px;
  border: 1px solid #dee2e6;
}

.horario-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
}

.horario-info strong {
  color: #007bff;
  min-width: 80px;
  display: inline-block;
}

.disponibilidad {
  padding: 8px 12px;
  border-radius: 4px;
  background: #f0f0f0;
  margin-top: 10px !important;
}

.disponible {
  color: #28a745;
  font-weight: bold;
}

.ocupado {
  color: #dc3545;
  font-weight: bold;
}

.btn-reservar {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-reservar:hover:not(:disabled) {
  background: linear-gradient(135deg, #0056b3, #003d82);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.btn-reservar:disabled {
  background: #ccc;
  cursor: not-allowed;
  opacity: 0.6;
}

.error-message {
  background: #f8d7da;
  color: #721c24;
  padding: 12px;
  border-radius: 6px;
  margin-top: 15px;
  border: 1px solid #f5c6cb;
  display: flex;
  align-items: center;
  gap: 10px;
}

.error-message span {
  font-size: 14px;
}

.success-message {
  background: #d4edda;
  color: #155724;
  padding: 15px;
  border-radius: 6px;
  margin-top: 15px;
  border: 1px solid #c3e6cb;
}

.success-message p {
  margin: 8px 0;
  font-size: 14px;
}

.success-message strong {
  color: #004085;
}

.success-message p:first-child {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}
```

---

## 🔌 Integración en el Componente del Perfil del Doctor

En el componente que muestra el perfil del doctor, usa el componente `ReservarCita`:

```jsx
import React, { useState, useEffect } from 'react';
import ReservarCita from './ReservarCita';

function DoctorProfile({ doctorId }) {
  const [horarios, setHorarios] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedHorario, setSelectedHorario] = useState(null);

  useEffect(() => {
    fetchHorarios();
  }, [doctorId]);

  const fetchHorarios = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/horarios/doctor/${doctorId}`);
      const data = await response.json();
      setHorarios(data);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCitaCreada = (cita) => {
    console.log('Cita creada:', cita);
    // Actualizar lista de horarios
    fetchHorarios();
    setSelectedHorario(null);
  };

  return (
    <div className="doctor-profile">
      <h2>Horarios de Atención</h2>
      
      {loading && <p>Cargando...</p>}

      {horarios.map((horario) => (
        <div key={horario.idHorario} className="horario-card">
          <div className="horario-details">
            <p><strong>Fecha:</strong> {horario.fecha}</p>
            <p><strong>Turno:</strong> {horario.turno}</p>
            <p><strong>Hora:</strong> {horario.horaInicio} - {horario.horaFin}</p>
            <p>{horario.disponible ? '✓ Disponible' : '✗ Ocupado'}</p>
          </div>

          {selectedHorario?.idHorario === horario.idHorario && (
            <ReservarCita 
              horarioId={horario.idHorario}
              horario={horario}
              onCitaCreada={handleCitaCreada}
            />
          )}

          {selectedHorario?.idHorario !== horario.idHorario && (
            <button 
              onClick={() => setSelectedHorario(horario)}
              disabled={!horario.disponible}
              className="btn-select"
            >
              {horario.disponible ? 'Seleccionar' : 'No Disponible'}
            </button>
          )}
        </div>
      ))}
    </div>
  );
}

export default DoctorProfile;
```

---

## 🔑 Variables de Entorno Necesarias

En tu `.env` o configuración:

```
REACT_APP_API_URL=http://localhost:8080
```

---

## ✅ Checklist de Implementación

- [ ] Crear archivo `ReservarCita.jsx`
- [ ] Crear archivo `ReservarCita.css`
- [ ] Obtener token del localStorage después del login
- [ ] Obtener datos del usuario del localStorage
- [ ] Llamar a `POST /citas` con headers de autenticación
- [ ] Mostrar estados: loading, error, éxito
- [ ] Refrescar lista de horarios después de reservar
- [ ] Validar que usuario esté autenticado antes de permitir reserva

---

## 🧪 Prueba Rápida (cURL desde Terminal)

```bash
# 1. Obtener token (login)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sigc.com","password":"Admin123456"}'

# 2. Copiar el token de la respuesta y usarlo en:
curl -X POST http://localhost:8080/citas \
  -H "Authorization: Bearer TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"idUsuario": 1},
    "horario": {"idHorario": 1}
  }'
```

---

## 📝 Notas Importantes

1. El **token JWT** se obtiene después del login
2. El **ID del paciente** se obtiene del usuario autenticado
3. El componente valida automáticamente todos los errores
4. Se muestra un mensaje de éxito durante 3 segundos
5. Después de reservar, se debe refrescar la lista de horarios

¡Listo para implementar! 🚀
