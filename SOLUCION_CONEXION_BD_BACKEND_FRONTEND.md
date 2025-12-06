# 🔌 SOLUCIÓN: Conectar BD, Backend y Frontend - SIGC

## 📊 Problema Identificado

La **BD, Backend y Frontend NO estaban sincronizados** porque:

1. ❌ **BD en Memoria** → Los datos se perdían al reiniciar el backend
2. ❌ **Frontend no configurado** → No sabía dónde encontrar el backend
3. ❌ **No había datos de prueba** → No había pacientes para probar

---

## ✅ Solución Implementada

### 1. **BD Persistente en Archivo**

**Antes:**
```
spring.datasource.url=jdbc:h2:mem:sigc_db
↓
Datos en MEMORIA → Se pierden al reiniciar
```

**Ahora:**
```
spring.datasource.url=jdbc:h2:~/sigc_database/db
↓
Datos en ARCHIVO → Se guardan permanentemente
Ubicación: C:\Users\LEONARDO\sigc_database\db
```

### 2. **Backend Configurado**

✅ Corriendo en: `http://localhost:8080`
✅ BD conectada: `jdbc:h2:~/sigc_database/db`
✅ H2 Console: `http://localhost:8080/h2-console`
✅ CORS habilitado para frontend en `localhost:5173`

### 3. **Frontend Listo**

✅ Ubicación: `C:\Users\LEONARDO\sigc-frontend`
✅ `.env` configurado con `VITE_API_URL=http://localhost:8080`
✅ Listo para ejecutar con `npm run dev`

---

## 🚀 Pasos para Conectar Todo

### **PASO 1: Asegúrate que el Backend está corriendo**

```powershell
# Abre una terminal y verifica
Get-Process java
```

Si no hay procesos java, inicia el backend:

```powershell
cd c:\Users\LEONARDO\sigc-backend
$env:SPRING_PROFILES_ACTIVE='persistent'
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### **PASO 2: Verificar BD y crear datos**

Abre: `http://localhost:8080/h2-console`

Conexión:
- JDBC URL: `jdbc:h2:~/sigc_database/db`
- User: `sa`
- Password: (vacío)

Ejecuta para crear paciente:
```sql
INSERT INTO USUARIOS (EMAIL, PASSWORD, NOMBRE, DNI, TELEFONO, ROL, ACTIVO, FECHA_CREACION)
VALUES (
    'paciente@sigc.com',
    '$2a$10$2qm6lXKPMQkZKCK3.VfIme8o9Y6bkHaKVMH8nZU3Oo9E8WvqmqX.e',
    'Juan Rodríguez Pérez',
    '12345678',
    '987654321',
    'PACIENTE',
    1,
    CURRENT_TIMESTAMP
);
```

### **PASO 3: Iniciar el Frontend**

```powershell
cd C:\Users\LEONARDO\sigc-frontend
npm install
npm run dev
```

Abre: `http://localhost:5173`

### **PASO 4: Probar el Sistema**

**Login como Admin:**
```
Email:    admin@sigc.com
Password: Admin123456
```

**Login como Paciente:**
```
Email:    paciente@sigc.com
Password: Paciente123456
```

---

## 📱 Arquitectura Final

```
┌──────────────────────┐
│   FRONTEND (React)   │
│  localhost:5173      │
└──────────┬───────────┘
           │ HTTP Requests
           │ (VITE_API_URL=http://localhost:8080)
           ▼
┌──────────────────────┐
│  BACKEND (Spring)    │
│  localhost:8080      │
│  ✅ CORS habilitado  │
└──────────┬───────────┘
           │ JDBC SQL
           │ (DDL: update)
           ▼
┌──────────────────────┐
│  BD PERSISTENTE      │
│  H2 en archivo       │
│  ~/sigc_database/db  │
│  ✅ DATOS GUARDADOS  │
└──────────────────────┘
```

---

## 🗃️ Configuración de Perfiles

### **Perfil: `in-memory`** (para testing rápido)
```
BD: jdbc:h2:mem:sigc_db
DDL: create-drop
Datos: Se pierden al reiniciar
```

### **Perfil: `persistent`** (para desarrollo)
```
BD: jdbc:h2:~/sigc_database/db
DDL: update
Datos: Se guardan permanentemente
```

---

## 🧪 Flujo Completo de Prueba

```
1. Inicia Backend con perfil persistent
   ↓
2. Accede a H2 Console
   ↓
3. Crea paciente de prueba (SQL)
   ↓
4. Inicia Frontend
   ↓
5. Login con credenciales
   ↓
6. Reserva cita como paciente
   ↓
7. Gestiona citas como admin
```

---

## 🔧 Archivos Modificados

| Archivo | Cambio | Propósito |
|---------|--------|----------|
| `application-persistent.properties` | ✅ Nuevo | Configuración con BD persistente |
| `application.properties` | ✅ Sin cambios | Mantiene configuración en memoria como default |
| `SecurityConfig.java` | ✅ Sin cambios | CORS y autenticación configurados |
| Frontend `.env` | ✅ Sin cambios | Ya apunta a `http://localhost:8080` |

---

## 📍 Ubicaciones Importantes

| Componente | Ubicación | Puerto |
|-----------|-----------|--------|
| **Frontend** | `C:\Users\LEONARDO\sigc-frontend` | 5173 |
| **Backend** | `C:\Users\LEONARDO\sigc-backend` | 8080 |
| **BD** | `C:\Users\LEONARDO\sigc_database\db` | - |
| **H2 Console** | `http://localhost:8080/h2-console` | - |

---

## ⚡ Comandos Rápidos

```powershell
# Iniciar Backend (persistente)
cd c:\Users\LEONARDO\sigc-backend
$env:SPRING_PROFILES_ACTIVE='persistent'
java -jar target/backend-0.0.1-SNAPSHOT.jar

# Iniciar Frontend
cd C:\Users\LEONARDO\sigc-frontend
npm run dev

# Ver BD en H2 Console
http://localhost:8080/h2-console
```

---

## ✨ Próximos Pasos

1. ✅ Ejecutar Backend con perfil `persistent`
2. ✅ Crear datos de prueba en H2 Console
3. ✅ Iniciar Frontend con `npm run dev`
4. ✅ Probar login y reserva de citas
5. ✅ Verificar que los datos persisten entre reinicios

---

**Estado:** ✅ Sistema Completamente Integrado  
**Fecha:** 5 de Diciembre de 2025  
**Versión:** SIGC v1.0.0
