# 🚀 Guía Final de Deployment en Render

## ✅ Pre-requisitos completados

- [x] Código en GitHub: `feature/solid-refactor` branch actualizada
- [x] Dockerfiles creados (backend + frontend)
- [x] `render.yaml` blueprint configurado con:
  - Base de datos Postgres managed
  - Disco persistente para uploads (1GB)
  - Variables de entorno automáticas
  - Health check endpoint

---

## 📋 Pasos para Deploy (3 minutos)

### Opción A: Import Blueprint (Recomendado - 1 clic)

1. **Ve a Render Dashboard**: https://dashboard.render.com
2. **New → Blueprint**
3. **Conecta tu repositorio**: `Zahel-sys/sigc-backend`
4. **Selecciona branch**: `feature/solid-refactor`
5. **Click "Apply"**

Render automáticamente:
- Creará base de datos Postgres (`sigc-postgres`)
- Desplegará backend (`sigc-backend-api`) con disco persistente
- Desplegará frontend (`sigc-frontend`)
- Configurará todas las env vars y conexiones

### Opción B: Manual (si Blueprint falla)

#### 1. Crear Base de Datos
```
Dashboard → New → PostgreSQL
- Name: sigc-postgres
- Database: sigc_db
- User: sigc_user
- Plan: Starter (gratis)
```
**Anota la Internal Connection String**

#### 2. Crear Backend Service
```
Dashboard → New → Web Service
- Connect repo: Zahel-sys/sigc-backend
- Branch: feature/solid-refactor
- Environment: Docker
- Dockerfile Path: ./Dockerfile
- Plan: Starter

Health Check Path: /actuator/health

Environment Variables:
  SPRING_PROFILES_ACTIVE=prod
  SPRING_DATASOURCE_URL=[pega la connection string de Postgres]
  JWT_SECRET=[genera una cadena aleatoria de 32+ chars]
  APP_UPLOAD_DIR=/srv/uploads/

Disk (Add Disk):
  Name: sigc-uploads
  Mount Path: /srv/uploads
  Size: 1 GB
```

#### 3. Crear Frontend Service
```
Dashboard → New → Web Service
- Connect repo: Zahel-sys/sigc-backend
- Branch: feature/solid-refactor
- Environment: Docker
- Dockerfile Path: ./sigc-frontend/Dockerfile
- Plan: Starter

Environment Variables:
  VITE_API_URL=https://[tu-backend-url].onrender.com
```

---

## 🔍 Verificación Post-Deploy (Checklist)

### Backend Health Check
```bash
curl https://sigc-backend-api.onrender.com/actuator/health
# Esperado: {"status":"UP"}
```

### Login Test
```powershell
$uri = "https://sigc-backend-api.onrender.com/auth/login"
$body = @{ email="admin@sigc.com"; password="Admin123456" } | ConvertTo-Json
$headers = @{ "Content-Type"="application/json" }
$response = Invoke-RestMethod -Uri $uri -Method Post -Body $body -Headers $headers
Write-Host "Token recibido:" $response.token
```

### WebSocket STOMP Test
```javascript
// Desde browser console en tu frontend desplegado
const socket = new SockJS('https://sigc-backend-api.onrender.com/ws');
const stompClient = Stomp.over(socket);
stompClient.connect(
  { Authorization: 'Bearer YOUR_JWT_TOKEN' },
  frame => console.log('✅ STOMP CONNECTED:', frame),
  error => console.error('❌ STOMP ERROR:', error)
);
```

### Endpoints Test
```bash
# GET especialidades
curl https://sigc-backend-api.onrender.com/especialidades

# GET doctores
curl https://sigc-backend-api.onrender.com/doctores

# GET horarios (con fecha)
curl "https://sigc-backend-api.onrender.com/horarios?fecha=2025-11-22"
```

---

## 🐛 Troubleshooting

### ❌ Build falla en backend
**Síntoma**: "BUILD FAILED" en Render logs  
**Solución**: 
- Verifica que `Dockerfile` esté en la raíz del repo
- Check logs: `mvn package` debe completar sin errores
- Si hay error de dependencias: limpia cache en Render (Settings → Clear build cache)

### ❌ Backend arranca pero retorna 500
**Síntoma**: Health check falla, logs muestran errores de DB  
**Solución**:
- Verifica `SPRING_DATASOURCE_URL` tenga formato correcto:
  ```
  jdbc:postgresql://[host]/[db]?user=[user]&password=[pass]
  ```
- Revisa logs de Postgres: debe estar "available"
- Checa que `spring.jpa.hibernate.ddl-auto=update` en `application-prod.properties`

### ❌ JWT tokens no validan
**Síntoma**: POST /auth/login OK pero GET /me retorna 401  
**Solución**:
- Verifica que `JWT_SECRET` esté configurado en env vars
- Debe ser la MISMA cadena entre builds (Render la genera una vez)
- Long mínimo: 32 caracteres

### ❌ WebSocket retorna 403 en /ws/info
**Síntoma**: Frontend no puede conectar a WebSocket  
**Solución**:
- Verifica que `SecurityConfig` permita `/ws/**`
- Checa CORS: añade tu dominio de frontend en `@CrossOrigin`
- Revisa que uses `wss://` (no `ws://`) en producción

### ❌ Uploads fallan / imágenes no se guardan
**Síntoma**: POST /doctores o /especialidades con imagen retorna 500  
**Solución**:
- Verifica que el disco `sigc-uploads` esté montado en `/srv/uploads`
- Checa permisos del directorio en Render (debe ser writable)
- Confirma `APP_UPLOAD_DIR=/srv/uploads/` en env vars
- Revisa logs: debe aparecer "Imagen guardada en: /srv/uploads/..."

### ❌ Frontend carga pero no conecta al backend
**Síntoma**: Frontend muestra UI pero no hay datos  
**Solución**:
- Verifica `VITE_API_URL` apunte a tu backend en Render
- Abre DevTools → Network: debe hacer requests a backend URL
- Checa CORS: backend debe permitir el origen del frontend
- Si usas variable de entorno, verifica que el build de Vite la inyecte correctamente

---

## 📊 Monitoreo

### Ver Logs en Tiempo Real
```
Render Dashboard → sigc-backend-api → Logs (tab)
Render Dashboard → sigc-frontend → Logs (tab)
```

### Métricas
```
Dashboard → Service → Metrics
- CPU usage
- Memory usage
- Request rate
- Response time
```

### Alertas (Opcional)
```
Dashboard → Service → Settings → Notifications
- Configurar alertas por email/Slack para:
  - Build failures
  - Deploy failures
  - High error rate
```

---

## 🔄 Re-Deploy / Actualización

### Deploy automático (configurado en render.yaml)
```bash
# Haz cambios localmente
git add .
git commit -m "feat: nueva funcionalidad"
git push origin feature/solid-refactor

# Render detecta el push y hace auto-deploy
```

### Deploy manual (si auto-deploy está off)
```
Dashboard → Service → Manual Deploy → Deploy latest commit
```

### Rollback a versión anterior
```
Dashboard → Service → Events → Find previous deploy → Rollback
```

---

## 📝 Datos de Acceso Iniciales

**Admin User** (creado por `DataInitializer.java`):
- Email: `admin@sigc.com`
- Password: `Admin123456`

**Datos de prueba** (creados por `SampleDataInitializer.java`):
- 4 Especialidades
- 3 Doctores
- 20 Horarios (5 días × 4 turnos)

---

## 🎯 URLs Finales

Una vez desplegado, tendrás:

- **Backend API**: `https://sigc-backend-api.onrender.com`
- **Frontend**: `https://sigc-frontend.onrender.com`
- **PostgreSQL**: `[internal connection string en Render]`

**Importante**: Render tarda ~5 min en el primer deploy (compila backend + instala deps frontend).  
Los servicios en plan "Starter" se suspenden tras 15 min de inactividad (se reactivan en <30s al recibir request).

---

## ✨ Siguientes Pasos (Post-Deploy)

1. **Prueba todos los endpoints** con el checklist de arriba
2. **Configura dominio custom** (opcional): Settings → Custom Domain
3. **Habilita HTTPS** (automático en Render)
4. **Configura CI/CD** si quieres tests automáticos antes de deploy
5. **Monitorea logs** las primeras 24h para detectar errores

---

**¿Problemas?** Revisa:
- Logs del servicio en Render
- Variables de entorno configuradas
- Estado de la base de datos (debe estar "available")
- Disco montado correctamente

**Tip**: Render tiene un plan gratis limitado. Si el servicio se duerme, el primer request tarda ~30s en despertar.
