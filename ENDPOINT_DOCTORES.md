# 👨‍⚕️ ENDPOINT DE DOCTORES - MULTIPART/FORM-DATA

## 📋 RESUMEN

El endpoint `/doctores` ha sido actualizado para recibir datos mediante **FormData (multipart/form-data)** desde el frontend React.

---

## 🔧 ENDPOINTS DISPONIBLES

### 1️⃣ **GET /doctores** - Listar todos los doctores

**Request:**
```http
GET http://localhost:8080/doctores
```

**Response:**
```json
[
  {
    "idDoctor": 1,
    "nombre": "Dr. Juan Pérez",
    "especialidad": "Cardiología",
    "cupoPacientes": 10,
    "imagen": "/images/doctores/1729765432000_doctor1.jpg"
  }
]
```

---

### 2️⃣ **POST /doctores** - Crear nuevo doctor (MULTIPART)

**Content-Type:** `multipart/form-data`

**FormData Fields:**

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `nombre` | String | ✅ Sí | Nombre completo del doctor |
| `especialidad` | String | ✅ Sí | Nombre de la especialidad (NO el ID) |
| `cupoPacientes` | Integer | ✅ Sí | Cupo máximo de pacientes |
| `imagen` | File | ❌ No | Imagen del doctor (jpg, png, etc.) |

**Ejemplo de Request (JavaScript/React):**
```javascript
const formData = new FormData();
formData.append('nombre', 'Dr. Juan Pérez');
formData.append('especialidad', 'Cardiología');
formData.append('cupoPacientes', '10');
formData.append('imagen', imageFile); // File object o null

const response = await fetch('http://localhost:8080/doctores', {
  method: 'POST',
  body: formData
  // NO enviar Content-Type header, se establece automáticamente
});
```

**Response Success (201 Created):**
```json
{
  "idDoctor": 5,
  "nombre": "Dr. Juan Pérez",
  "especialidad": "Cardiología",
  "cupoPacientes": 10,
  "imagen": "/images/doctores/1729765432000_doctor.jpg"
}
```

**Response Error (500):**
```json
"Error al crear el doctor: [mensaje de error]"
```

---

### 3️⃣ **PUT /doctores/{id}** - Actualizar doctor (MULTIPART)

**Content-Type:** `multipart/form-data`

**FormData Fields:** (mismos que POST)

**Ejemplo de Request:**
```javascript
const formData = new FormData();
formData.append('nombre', 'Dr. Juan Pérez Actualizado');
formData.append('especialidad', 'Neurología');
formData.append('cupoPacientes', '15');
// imagen es opcional, solo si quieres actualizar la imagen
formData.append('imagen', newImageFile);

const response = await fetch('http://localhost:8080/doctores/5', {
  method: 'PUT',
  body: formData
});
```

**Response Success (200 OK):**
```json
{
  "idDoctor": 5,
  "nombre": "Dr. Juan Pérez Actualizado",
  "especialidad": "Neurología",
  "cupoPacientes": 15,
  "imagen": "/images/doctores/1729765999000_new_image.jpg"
}
```

---

### 4️⃣ **DELETE /doctores/{id}** - Eliminar doctor

**Request:**
```http
DELETE http://localhost:8080/doctores/5
```

**Response Success (200 OK):**
```
(empty body)
```

---

## 📂 ALMACENAMIENTO DE IMÁGENES

### Directorio:
```
src/main/resources/static/images/doctores/
```

### Formato de nombre:
```
{timestamp}_{sanitized_filename}
Ejemplo: 1729765432000_doctor.jpg
```

### URL de acceso:
```
/images/doctores/1729765432000_doctor.jpg
```

---

## ✨ CARACTERÍSTICAS IMPLEMENTADAS

### ✅ Imagen OPCIONAL
- El campo `imagen` es **opcional** (`required = false`)
- No genera error 500 si no se envía imagen
- Se puede crear/actualizar doctor sin imagen

### ✅ Validación de Archivo
- Sanitización del nombre del archivo
- Eliminación de caracteres especiales
- Timestamp único para evitar colisiones

### ✅ Logging Completo
```
INFO: Registrando doctor: Dr. Juan Pérez - Especialidad: Cardiología
INFO: Imagen del doctor guardada: /images/doctores/1729765432000_doctor.jpg
INFO: Doctor registrado exitosamente con ID: 5
```

### ✅ Manejo de Errores
- Try-catch en todos los métodos
- Mensajes de error descriptivos
- Logging de excepciones

---

## 🧪 PRUEBAS CON cURL

### Crear doctor CON imagen:
```bash
curl -X POST http://localhost:8080/doctores \
  -F "nombre=Dr. Juan Pérez" \
  -F "especialidad=Cardiología" \
  -F "cupoPacientes=10" \
  -F "imagen=@/ruta/a/imagen.jpg"
```

### Crear doctor SIN imagen:
```bash
curl -X POST http://localhost:8080/doctores \
  -F "nombre=Dr. María García" \
  -F "especialidad=Pediatría" \
  -F "cupoPacientes=15"
```

### Actualizar doctor:
```bash
curl -X PUT http://localhost:8080/doctores/1 \
  -F "nombre=Dr. Juan Pérez Actualizado" \
  -F "especialidad=Neurología" \
  -F "cupoPacientes=12" \
  -F "imagen=@/ruta/a/nueva_imagen.jpg"
```

---

## 🔍 PRUEBAS CON POSTMAN

### Configuración:
1. **Method:** POST o PUT
2. **URL:** `http://localhost:8080/doctores` (o `/doctores/{id}` para PUT)
3. **Body:** Seleccionar **form-data** (NO raw JSON)
4. **Agregar campos:**
   - `nombre` (Text)
   - `especialidad` (Text)
   - `cupoPacientes` (Text)
   - `imagen` (File) - Seleccionar archivo desde tu PC

---

## ⚠️ NOTAS IMPORTANTES

### ❌ **NO Enviar:**
```javascript
// ❌ INCORRECTO - NO usar application/json
fetch('http://localhost:8080/doctores', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ nombre: '...', ... })
});
```

### ✅ **SÍ Enviar:**
```javascript
// ✅ CORRECTO - Usar FormData
const formData = new FormData();
formData.append('nombre', '...');
formData.append('especialidad', '...');
formData.append('cupoPacientes', '...');
formData.append('imagen', file);

fetch('http://localhost:8080/doctores', {
  method: 'POST',
  body: formData
  // NO establecer Content-Type header
});
```

---

## 📊 DIFERENCIAS CON VERSIÓN ANTERIOR

### ANTES (JSON):
```java
@PostMapping
public ResponseEntity<?> crear(@RequestBody Doctor doctor)
```

### AHORA (MULTIPART):
```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> crear(
    @RequestParam("nombre") String nombre,
    @RequestParam("especialidad") String especialidad,
    @RequestParam("cupoPacientes") Integer cupoPacientes,
    @RequestParam(value = "imagen", required = false) MultipartFile imagen)
```

---

## 🔐 CORS CONFIGURADO

```java
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://localhost:5175"
})
```

---

## 📝 LOGS ESPERADOS

### Creación exitosa:
```
INFO: Registrando doctor: Dr. Juan Pérez - Especialidad: Cardiología
INFO: Directorio creado: src/main/resources/static/images/doctores/
INFO: Imagen guardada en: src/main/resources/static/images/doctores/1729765432000_doctor.jpg
INFO: Imagen del doctor guardada: /images/doctores/1729765432000_doctor.jpg
INFO: Doctor registrado exitosamente con ID: 5
```

### Creación sin imagen:
```
INFO: Registrando doctor: Dr. María García - Especialidad: Pediatría
INFO: No se proporcionó imagen para el doctor
INFO: Doctor registrado exitosamente con ID: 6
```

### Error:
```
ERROR: Error al registrar doctor: [stack trace completo]
```

---

**Última actualización:** 24 de Octubre de 2025  
**Versión:** 2.0.0 (Multipart/Form-Data)  
**Estado:** ✅ FUNCIONAL
