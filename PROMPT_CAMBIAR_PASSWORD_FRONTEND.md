# Frontend Integration - PUT /usuarios/cambiar-password

## React Component Implementation

### Complete Component with Validation

```jsx
import React, { useState } from 'react';
import './CambiarPasswordForm.css';

/**
 * Component for changing user password
 * Includes all 7 validations + token authentication
 */
export default function CambiarPasswordForm() {
  const [formData, setFormData] = useState({
    passwordActual: '',
    passwordNueva: '',
    passwordConfirmar: '',
  });

  const [loading, setLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [showPassword, setShowPassword] = useState({
    actual: false,
    nueva: false,
    confirmar: false,
  });

  // Get token from localStorage (same place where login stores it)
  const getAuthToken = () => {
    const token = localStorage.getItem('token');
    if (!token) {
      setErrorMessage('No autenticado. Por favor inicia sesión');
      return null;
    }
    return token;
  };

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error when user starts typing
    if (errorMessage) setErrorMessage('');
  };

  // Toggle password visibility
  const togglePasswordVisibility = (field) => {
    setShowPassword(prev => ({
      ...prev,
      [field]: !prev[field]
    }));
  };

  // Client-side validation before sending
  const validarFormulario = () => {
    // Validación 1: Campos no vacíos
    if (!formData.passwordActual.trim()) {
      setErrorMessage('La contraseña actual es requerida');
      return false;
    }

    if (!formData.passwordNueva.trim()) {
      setErrorMessage('La nueva contraseña es requerida');
      return false;
    }

    if (!formData.passwordConfirmar.trim()) {
      setErrorMessage('Confirmar contraseña es requerido');
      return false;
    }

    // Validación 2: Las nuevas contraseñas coinciden
    if (formData.passwordNueva !== formData.passwordConfirmar) {
      setErrorMessage('Las nuevas contraseñas no coinciden');
      return false;
    }

    // Validación 3: Longitud mínima
    if (formData.passwordNueva.length < 6) {
      setErrorMessage('La contraseña debe tener al menos 6 caracteres');
      return false;
    }

    // Validación 4: Nueva contraseña diferente de actual
    if (formData.passwordNueva === formData.passwordActual) {
      setErrorMessage('La nueva contraseña debe ser diferente a la actual');
      return false;
    }

    return true;
  };

  // Submit form to backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Clear previous messages
    setSuccessMessage('');
    setErrorMessage('');

    // Client-side validation
    if (!validarFormulario()) {
      return;
    }

    const token = getAuthToken();
    if (!token) {
      return;
    }

    setLoading(true);

    try {
      const response = await fetch('http://localhost:8080/usuarios/cambiar-password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          passwordActual: formData.passwordActual,
          passwordNueva: formData.passwordNueva,
          passwordConfirmar: formData.passwordConfirmar
        })
      });

      const data = await response.json();

      if (response.ok) {
        // Success: 200
        setSuccessMessage(`✅ ${data.mensaje}`);
        // Clear form
        setFormData({
          passwordActual: '',
          passwordNueva: '',
          passwordConfirmar: ''
        });
        // Redirect after 2 seconds
        setTimeout(() => {
          window.location.href = '/perfil';
        }, 2000);
      } else {
        // Error: 400, 401, 404, 422, 500
        handleErrorResponse(response.status, data);
      }
    } catch (error) {
      setErrorMessage('Error de conexión con el servidor');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  // Handle different HTTP error codes
  const handleErrorResponse = (status, data) => {
    switch (status) {
      case 400:
        setErrorMessage(data.error || 'Solicitud inválida');
        break;
      case 401:
        setErrorMessage('No autorizado. Por favor vuelve a iniciar sesión');
        setTimeout(() => localStorage.removeItem('token'), 2000);
        break;
      case 404:
        setErrorMessage(data.error || 'Usuario no encontrado');
        break;
      case 422:
        setErrorMessage(data.error || 'Datos inválidos');
        break;
      case 500:
        setErrorMessage(data.error || 'Error del servidor');
        break;
      default:
        setErrorMessage(data.error || 'Error desconocido');
    }
  };

  return (
    <div className="cambiar-password-container">
      <div className="cambiar-password-card">
        <h2>🔒 Cambiar Contraseña</h2>
        
        <form onSubmit={handleSubmit}>
          {/* Contraseña Actual */}
          <div className="form-group">
            <label htmlFor="passwordActual">Contraseña Actual *</label>
            <div className="password-input-group">
              <input
                id="passwordActual"
                type={showPassword.actual ? 'text' : 'password'}
                name="passwordActual"
                value={formData.passwordActual}
                onChange={handleInputChange}
                disabled={loading}
                placeholder="Ingresa tu contraseña actual"
                autoComplete="current-password"
              />
              <button
                type="button"
                className="toggle-visibility"
                onClick={() => togglePasswordVisibility('actual')}
                disabled={loading}
              >
                {showPassword.actual ? '🙈' : '👁️'}
              </button>
            </div>
          </div>

          {/* Nueva Contraseña */}
          <div className="form-group">
            <label htmlFor="passwordNueva">Nueva Contraseña *</label>
            <div className="password-input-group">
              <input
                id="passwordNueva"
                type={showPassword.nueva ? 'text' : 'password'}
                name="passwordNueva"
                value={formData.passwordNueva}
                onChange={handleInputChange}
                disabled={loading}
                placeholder="Ingresa tu nueva contraseña"
                autoComplete="new-password"
              />
              <button
                type="button"
                className="toggle-visibility"
                onClick={() => togglePasswordVisibility('nueva')}
                disabled={loading}
              >
                {showPassword.nueva ? '🙈' : '👁️'}
              </button>
            </div>
            <small className="help-text">
              Mínimo 6 caracteres. Debe ser diferente a la actual.
            </small>
          </div>

          {/* Confirmar Contraseña */}
          <div className="form-group">
            <label htmlFor="passwordConfirmar">Confirmar Contraseña *</label>
            <div className="password-input-group">
              <input
                id="passwordConfirmar"
                type={showPassword.confirmar ? 'text' : 'password'}
                name="passwordConfirmar"
                value={formData.passwordConfirmar}
                onChange={handleInputChange}
                disabled={loading}
                placeholder="Confirma tu nueva contraseña"
                autoComplete="new-password"
              />
              <button
                type="button"
                className="toggle-visibility"
                onClick={() => togglePasswordVisibility('confirmar')}
                disabled={loading}
              >
                {showPassword.confirmar ? '🙈' : '👁️'}
              </button>
            </div>
            {formData.passwordNueva && formData.passwordConfirmar && (
              <small className={`help-text ${formData.passwordNueva === formData.passwordConfirmar ? 'match' : 'mismatch'}`}>
                {formData.passwordNueva === formData.passwordConfirmar 
                  ? '✅ Las contraseñas coinciden'
                  : '❌ Las contraseñas no coinciden'
                }
              </small>
            )}
          </div>

          {/* Success Message */}
          {successMessage && (
            <div className="alert alert-success">
              {successMessage}
            </div>
          )}

          {/* Error Message */}
          {errorMessage && (
            <div className="alert alert-error">
              {errorMessage}
            </div>
          )}

          {/* Buttons */}
          <div className="form-buttons">
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? '⏳ Procesando...' : '🔄 Cambiar Contraseña'}
            </button>
            <button 
              type="button" 
              className="btn btn-secondary"
              onClick={() => window.history.back()}
              disabled={loading}
            >
              ← Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
```

### CSS Styling

```css
/* CambiarPasswordForm.css */

.cambiar-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.cambiar-password-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 40px;
  width: 100%;
  max-width: 420px;
}

.cambiar-password-card h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 600;
  font-size: 14px;
}

.password-input-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.password-input-group input {
  flex: 1;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.password-input-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.password-input-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.toggle-visibility {
  padding: 10px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  transition: opacity 0.2s;
}

.toggle-visibility:hover {
  opacity: 0.7;
}

.toggle-visibility:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.help-text {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #666;
}

.help-text.match {
  color: #4caf50;
}

.help-text.mismatch {
  color: #f44336;
}

/* Alerts */
.alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 14px;
}

.alert-success {
  background-color: #e8f5e9;
  color: #2e7d32;
  border-left: 4px solid #4caf50;
}

.alert-error {
  background-color: #ffebee;
  color: #c62828;
  border-left: 4px solid #f44336;
}

/* Buttons */
.form-buttons {
  display: flex;
  gap: 12px;
  margin-top: 30px;
}

.btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #e0e0e0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #d0d0d0;
}

.btn-secondary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Responsive */
@media (max-width: 600px) {
  .cambiar-password-card {
    padding: 30px 20px;
  }

  .cambiar-password-card h2 {
    font-size: 20px;
    margin-bottom: 20px;
  }

  .form-buttons {
    flex-direction: column;
  }

  .btn {
    flex: auto;
  }
}
```

---

## Usage in Your App

### Add Route in React Router

```jsx
import CambiarPasswordForm from './components/CambiarPasswordForm';

<Routes>
  {/* ... other routes ... */}
  <Route path="/cambiar-password" element={<CambiarPasswordForm />} />
</Routes>
```

### Add Navigation Link

```jsx
// In your header/menu component
<Link to="/cambiar-password" className="menu-link">
  🔒 Cambiar Contraseña
</Link>
```

### Or in a Profile Page

```jsx
export default function PerfilPage() {
  return (
    <div className="perfil-container">
      {/* User info */}
      <button 
        onClick={() => navigate('/cambiar-password')}
        className="btn-cambiar-password"
      >
        Cambiar Contraseña
      </button>
    </div>
  );
}
```

---

## API Integration Details

### Request Headers Required
```javascript
{
  'Content-Type': 'application/json',
  'Authorization': 'Bearer {token}'  // ← REQUIRED
}
```

### Request Body
```javascript
{
  "passwordActual": "tu_password_actual",
  "passwordNueva": "tu_nuevo_password",
  "passwordConfirmar": "tu_nuevo_password"
}
```

### Successful Response (200 OK)
```javascript
{
  "idUsuario": 1,
  "email": "usuario@example.com",
  "mensaje": "Contraseña cambiada exitosamente",
  "timestamp": "2025-11-19T19:30:00",
  "exitoso": true
}
```

### Error Responses

#### 401 - Unauthorized (No Token / Invalid Token)
```javascript
{
  "error": "Token JWT no proporcionado",
  "timestamp": "2025-11-19T19:30:00"
}
```

#### 400 - Bad Request (Validation Failed)
```javascript
{
  "error": "Contraseña actual incorrecta",
  "timestamp": "2025-11-19T19:30:00"
}
```

#### 422 - Unprocessable Entity (Business Logic Failed)
```javascript
{
  "error": "Las nuevas contraseñas no coinciden",
  "timestamp": "2025-11-19T19:30:00"
}
```

#### 404 - Not Found (User Not Found)
```javascript
{
  "error": "Usuario no encontrado",
  "timestamp": "2025-11-19T19:30:00"
}
```

#### 500 - Server Error
```javascript
{
  "error": "Error al cambiar la contraseña",
  "timestamp": "2025-11-19T19:30:00"
}
```

---

## State Management with Redux (Optional)

If using Redux:

```javascript
// slices/passwordSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';

export const cambiarPassword = createAsyncThunk(
  'password/cambiar',
  async (passwordData, { rejectWithValue }) => {
    const token = localStorage.getItem('token');
    
    if (!token) {
      return rejectWithValue('No autenticado');
    }

    try {
      const response = await fetch('http://localhost:8080/usuarios/cambiar-password', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(passwordData)
      });

      if (!response.ok) {
        const error = await response.json();
        return rejectWithValue(error.error);
      }

      return await response.json();
    } catch (error) {
      return rejectWithValue('Error de conexión');
    }
  }
);

const passwordSlice = createSlice({
  name: 'password',
  initialState: {
    loading: false,
    success: false,
    error: null,
    message: ''
  },
  extraReducers: (builder) => {
    builder
      .addCase(cambiarPassword.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(cambiarPassword.fulfilled, (state, action) => {
        state.loading = false;
        state.success = true;
        state.message = action.payload.mensaje;
      })
      .addCase(cambiarPassword.rejected, (state, action) => {
        state.loading = false;
        state.success = false;
        state.error = action.payload;
      });
  }
});

export default passwordSlice.reducer;
```

---

## Security Best Practices

✅ **Implemented in Component:**
- Client-side validation before sending
- Password visibility toggle
- Token validation
- Error handling for all HTTP codes
- No password in localStorage
- HTTPS recommended in production

✅ **Backend Provides:**
- Token JWT validation (401)
- Password current validation (400)
- Encryption with BCryptPasswordEncoder
- No password in response
- Proper HTTP status codes
- Timestamp for audit trail

⚠️ **Additional Recommendations:**
- Implement rate limiting (max 5 attempts per 15 minutes)
- Log password change attempts
- Send email confirmation
- Require re-authentication for security-sensitive operations
- Implement CSRF token protection
- Use Content Security Policy headers

---

## Testing the Integration

### Test Successful Change
1. Navigate to `/cambiar-password`
2. Enter current password: `password123`
3. Enter new password: `newPassword456`
4. Confirm new password: `newPassword456`
5. Click "Cambiar Contraseña"
6. Should redirect to `/perfil` after 2 seconds

### Test Error Scenarios
- Leave fields empty → See validation errors
- Enter wrong current password → Error 400
- Enter non-matching new passwords → Error 422
- Let token expire → Error 401
- Use deleted user account → Error 404
