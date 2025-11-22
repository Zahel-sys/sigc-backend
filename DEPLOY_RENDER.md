**Instrucciones para desplegar en Render (rápido)**

1) Crear cuenta en Render y conectar tu repositorio (GitHub/GitLab).

2) Opcional: crear una base de datos Postgres managed en Render. Anota la URL (AJUSTAR EN RENDER PANEL):
   - EJEMPLO URL: `postgres://user:password@localhost:5432/sigc_db`

3) Variables de entorno que debes añadir en el servicio `sigc-backend-api` (Render panel → Environment):
   - `POSTGRES_URL` = la url de Postgres (jdbc style si prefieres: `jdbc:postgresql://host:5432/db?user=user&password=pass`)
   - `JWT_SECRET` = una cadena secreta larga (ej: 32+ chars)
   - `SPRING_DATASOURCE_URL` = (opcional) reemplaza si no quieres usar `POSTGRES_URL` literal
   - `APP_UPLOAD_DIR` = ruta absoluta en el contenedor donde se montará el volumen de uploads (ej: `/srv/uploads/`). Si no la configuras, por defecto el backend usa `/srv/uploads/` en producción.

4) Build & Deploy
   - Si usas `render.yaml`, Render detectará los servicios y hará el build usando los Dockerfiles.
   - Si prefieres crear los servicios manualmente, en Render crea 2 servicios (Docker):
     - Backend: Dockerfile en la raíz (usa `./Dockerfile`), puerto 8080.
     - Frontend: Dockerfile en `sigc-frontend/Dockerfile`, puerto 80.

5) Ajustes en la app (producción)
   - Revisa `src/main/resources/application.properties` o crea `application-prod.properties` para apuntar a Postgres.
   - Asegúrate que `SPRING_PROFILES_ACTIVE=prod` en variables de entorno.
    - Asegura un volumen persistente montado en la ruta indicada por `APP_UPLOAD_DIR` para que los archivos subidos persistan entre deployments.

6) Probar
   - Backend estará disponible en la URL que Render asigne al servicio (ej: `https://sigc-backend-api.onrender.com`).
   - Frontend en la URL del servicio frontend.

Notas:
 - H2 en memoria no es persistente; se recomienda Postgres en producción.
 - Si necesitas que configure `application-prod.properties` para Postgres, dímelo y lo agrego.
