# Prompt para Obtener y Visualizar Horarios de Doctores en el Frontend

## 🎯 Objetivo Principal
Cuando un usuario hace clic en el perfil de un doctor (por ejemplo, "Richard" de Cardiología), debe aparecer una tabla con todos sus horarios de atención disponibles.

Actualmente no se visualizan los horarios. Necesito que se conecte correctamente con el backend y se muestren los datos.

## 📋 Endpoint Backend Disponible
```
GET http://localhost:8080/horarios/doctor/{idDoctor}
```

**Ejemplo de uso:** 
- Doctor ID 1 (Richard): `http://localhost:8080/horarios/doctor/1`
- Doctor ID 2: `http://localhost:8080/horarios/doctor/2`

**Respuesta que devuelve (JSON):**
```json
[
  {
    "idHorario": 1,
    "fecha": "2025-11-20",
    "turno": "Mañana",
    "horaInicio": "08:00:00",
    "horaFin": "12:00:00",
    "disponible": true,
    "doctor": {
      "idDoctor": 1,
      "nombre": "Richard",
      "especialidad": "Cardiología"
    }
  },
  {
    "idHorario": 2,
    "fecha": "2025-11-20",
    "turno": "Tarde",
    "horaInicio": "14:00:00",
    "horaFin": "18:00:00",
    "disponible": true,
    "doctor": {
      "idDoctor": 1,
      "nombre": "Richard",
      "especialidad": "Cardiología"
    }
  }
]
```

## 💻 Código React para el Frontend

### 1. En el componente que muestra el perfil del doctor:

```jsx
import React, { useState, useEffect } from 'react';

export default function DoctorProfile({ doctorId }) {
  const [horarios, setHorarios] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Obtener horarios cuando el componente se monta o cuando cambia el doctorId
  useEffect(() => {
    if (doctorId) {
      fetchHorarios(doctorId);
    }
  }, [doctorId]);

  const fetchHorarios = async (id) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8080/horarios/doctor/${id}`);
      
      if (!response.ok) {
        throw new Error('Error al obtener horarios');
      }
      
      const data = await response.json();
      setHorarios(data);
    } catch (err) {
      setError(err.message);
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="doctor-profile">
      <h2>Horarios de Atención</h2>
      
      {loading && <p>Cargando horarios...</p>}
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      
      {horarios.length === 0 && !loading && (
        <p>No hay horarios disponibles para este doctor</p>
      )}
      
      {horarios.length > 0 && (
        <table className="horarios-table">
          <thead>
            <tr>
              <th>Fecha</th>
              <th>Turno</th>
              <th>Hora Inicio</th>
              <th>Hora Fin</th>
              <th>Estado</th>
              <th>Acción</th>
            </tr>
          </thead>
          <tbody>
            {horarios.map((horario) => (
              <tr key={horario.idHorario}>
                <td>{horario.fecha}</td>
                <td>{horario.turno}</td>
                <td>{horario.horaInicio}</td>
                <td>{horario.horaFin}</td>
                <td>
                  {horario.disponible ? (
                    <span style={{ color: 'green' }}>✓ Disponible</span>
                  ) : (
                    <span style={{ color: 'red' }}>✗ Ocupado</span>
                  )}
                </td>
                <td>
                  <button 
                    disabled={!horario.disponible}
                    onClick={() => handleReserva(horario.idHorario)}
                  >
                    {horario.disponible ? 'Reservar' : 'Ocupado'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );

  function handleReserva(horarioId) {
    console.log('Reservando horario:', horarioId);
    // Aquí puedes llamar a un endpoint para reservar
  }
}
```

### 2. Estilos CSS (opcional):

```css
.doctor-profile {
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
}

.horarios-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  background: white;
}

.horarios-table thead {
  background: #007bff;
  color: white;
}

.horarios-table th,
.horarios-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.horarios-table tbody tr:hover {
  background: #f9f9f9;
}

.horarios-table button {
  padding: 8px 16px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.horarios-table button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.horarios-table button:hover:not(:disabled) {
  background: #218838;
}
```

## 🔗 Integración en tu componente actual

Si ya tienes el componente que muestra el doctor (como en la imagen), modifícalo así:

```jsx
// En tu componente que muestra "Richard" con "Cardiología"

const [selectedDoctorId, setSelectedDoctorId] = useState(null);

// Cuando hagas clic en el perfil del doctor
const handleDoctorClick = (doctorId) => {
  setSelectedDoctorId(doctorId);
  // Mostrar modal o cambiar vista a DoctorProfile
};

// En el JSX:
{selectedDoctorId && <DoctorProfile doctorId={selectedDoctorId} />}
```

## ✅ Resumen

- El **backend** ya tiene el endpoint `/horarios/doctor/{id}`
- El endpoint devuelve una **lista de horarios disponibles** para ese doctor
- El **frontend** debe hacer un `fetch` a ese endpoint cuando se abre el perfil
- Los horarios se muestran en una **tabla con estado** (Disponible/Ocupado)
- El usuario puede **reservar** desde esa vista

¡Listo para implementar!
