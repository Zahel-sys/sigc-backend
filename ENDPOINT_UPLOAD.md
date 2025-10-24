# 🖼️ ENDPOINT DE SUBIDA DE IMÁGENES

## 📋 Información General

**Endpoint:** `POST /uploads`  
**Content-Type:** `multipart/form-data`  
**Campo requerido:** `file`  
**Respuesta:** JSON con la URL de la imagen

---

## 🎯 Funcionalidad

El endpoint permite subir imágenes para especialidades médicas y retorna la URL relativa donde se guardó el archivo.

### Características:

✅ **Validación de formato** - Solo acepta: JPG, JPEG, PNG, GIF, WEBP  
✅ **Validación de tamaño** - Máximo 5MB por archivo  
✅ **Nombres únicos** - Agrega timestamp para evitar colisiones  
✅ **Sanitización** - Limpia caracteres especiales del nombre  
✅ **Creación automática** - Crea carpetas si no existen  
✅ **Respuesta detallada** - Retorna URL, nombre, tamaño y tipo

---

## 📤 Petición

### Headers:
```
Content-Type: multipart/form-data
```

### Body:
- **Campo:** `file`
- **Tipo:** archivo de imagen
- **Formatos:** jpg, jpeg, png, gif, webp
- **Tamaño máximo:** 5MB

### Ejemplo con cURL:
```bash
curl -X POST http://localhost:8080/uploads \
  -F "file=@imagen.jpg"
```

### Ejemplo con JavaScript:
```javascript
const formData = new FormData();
formData.append('file', archivoSeleccionado);

const response = await fetch('http://localhost:8080/uploads', {
    method: 'POST',
    body: formData
});

const data = await response.json();
console.log(data.url); // /images/especialidades/1729756800_imagen.jpg
```

### Ejemplo con React:
```jsx
const handleUpload = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch('http://localhost:8080/uploads', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();
        
        if (data.success) {
            console.log('Imagen subida:', data.url);
            // Usar la URL: http://localhost:8080 + data.url
        }
    } catch (error) {
        console.error('Error:', error);
    }
};
```

---

## 📥 Respuesta

### ✅ Respuesta Exitosa (200 OK)

```json
{
    "success": true,
    "url": "/images/especialidades/1729756800_cardiologia.jpg",
    "filename": "1729756800_cardiologia.jpg",
    "size": 245678,
    "contentType": "image/jpeg"
}
```

**Campos:**
- `success` - Indica si la operación fue exitosa
- `url` - URL relativa de la imagen (usar con: http://localhost:8080 + url)
- `filename` - Nombre final del archivo guardado
- `size` - Tamaño en bytes
- `contentType` - Tipo MIME del archivo

### ❌ Respuestas de Error

#### Archivo vacío (400 Bad Request)
```json
{
    "success": false,
    "error": "El archivo está vacío"
}
```

#### Archivo demasiado grande (413 Payload Too Large)
```json
{
    "success": false,
    "error": "El archivo es demasiado grande (máximo 5MB)"
}
```

#### Formato no válido (400 Bad Request)
```json
{
    "success": false,
    "error": "Formato de imagen no válido. Permitidos: [jpg, jpeg, png, gif, webp]"
}
```

#### Error interno (500 Internal Server Error)
```json
{
    "success": false,
    "error": "Error al guardar la imagen: [mensaje de error]"
}
```

---

## 📁 Almacenamiento

### Ubicación física:
```
sigc-backend/
└── src/
    └── main/
        └── resources/
            └── static/
                └── images/
                    └── especialidades/
                        ├── 1729756800_cardiologia.jpg
                        ├── 1729756850_neurologia.png
                        └── ...
```

### URL de acceso:
```
http://localhost:8080/images/especialidades/1729756800_cardiologia.jpg
```

### Formato del nombre:
```
[timestamp]_[nombre_sanitizado].[extension]

Ejemplo: 1729756800_cardiologia.jpg
         └─────┬────┘ └─────┬─────┘
           timestamp   nombre original
```

---

## 🧪 Pruebas

### Opción 1: PowerShell Script
```powershell
.\test-upload.ps1
```

Este script:
1. Crea una imagen de prueba PNG
2. La sube al endpoint
3. Verifica que sea accesible
4. Muestra los resultados

### Opción 2: Página HTML de Prueba
1. Abre `test-upload.html` en tu navegador
2. Arrastra una imagen o haz clic para seleccionar
3. Haz clic en "Subir Imagen"
4. Verás el resultado y podrás acceder a la imagen

### Opción 3: Postman
1. Método: `POST`
2. URL: `http://localhost:8080/uploads`
3. Body: `form-data`
4. Key: `file` (type: File)
5. Value: Selecciona un archivo de imagen

---

## ⚙️ Configuración

### application.properties
```properties
# Tamaño máximo por archivo: 5MB
spring.servlet.multipart.max-file-size=5MB

# Tamaño máximo total de la petición: 10MB
spring.servlet.multipart.max-request-size=10MB

# Habilitar multipart
spring.servlet.multipart.enabled=true

# Servir archivos estáticos
spring.web.resources.static-locations=classpath:/static/
```

### SecurityConfig.java
```java
.requestMatchers("/uploads/**").permitAll()
.requestMatchers("/images/**").permitAll()
```

---

## 🔧 Personalización

### Cambiar la carpeta de destino:

En `UploadController.java`, línea 27:
```java
private static final String UPLOAD_DIR = "src/main/resources/static/images/especialidades/";
```

### Cambiar formatos permitidos:

En `UploadController.java`, línea 30:
```java
private static final List<String> ALLOWED_EXTENSIONS = 
    Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
```

### Cambiar tamaño máximo:

En `UploadController.java`, línea 33:
```java
private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
```

Y en `application.properties`:
```properties
spring.servlet.multipart.max-file-size=5MB
```

---

## 🛡️ Seguridad

### Validaciones implementadas:

✅ **Extensión de archivo** - Solo imágenes permitidas  
✅ **Tamaño de archivo** - Máximo 5MB  
✅ **Sanitización de nombres** - Elimina caracteres especiales  
✅ **Nombres únicos** - Timestamp previene sobrescritura  
✅ **CORS configurado** - Solo localhost:5173-5175

### Recomendaciones adicionales para producción:

⚠️ **Antivirus** - Escanear archivos subidos  
⚠️ **Autenticación** - Requerir usuario autenticado  
⚠️ **Cuotas** - Limitar subidas por usuario/día  
⚠️ **Almacenamiento externo** - Usar S3, Cloudinary, etc.  
⚠️ **CDN** - Para mejor rendimiento  

---

## 🐛 Troubleshooting

### Error: "El archivo está vacío"
- Verifica que estás enviando el archivo correctamente
- El campo debe llamarse `file`
- Asegúrate de usar `multipart/form-data`

### Error: "Formato de imagen no válido"
- Solo se permiten: jpg, jpeg, png, gif, webp
- Verifica la extensión del archivo

### Error: "El archivo es demasiado grande"
- Tamaño máximo: 5MB
- Comprime la imagen antes de subir

### Error: No se puede acceder a la imagen
- Verifica que el backend esté ejecutándose
- Confirma que la carpeta `static/images/especialidades/` existe
- Revisa que Spring esté sirviendo archivos estáticos

### La imagen no se muestra en el frontend
- URL completa debe ser: `http://localhost:8080` + `data.url`
- Ejemplo: `http://localhost:8080/images/especialidades/1729756800_imagen.jpg`

---

## 📊 Ejemplo Completo (React)

```jsx
import React, { useState } from 'react';

function UploadImage() {
    const [imageUrl, setImageUrl] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Validar tamaño
        if (file.size > 5 * 1024 * 1024) {
            setError('El archivo es demasiado grande (máximo 5MB)');
            return;
        }

        // Validar formato
        const validFormats = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
        if (!validFormats.includes(file.type)) {
            setError('Formato no válido. Use JPG, PNG, GIF o WEBP');
            return;
        }

        // Subir archivo
        const formData = new FormData();
        formData.append('file', file);

        setLoading(true);
        setError('');

        try {
            const response = await fetch('http://localhost:8080/uploads', {
                method: 'POST',
                body: formData
            });

            const data = await response.json();

            if (data.success) {
                // Construir URL completa
                const fullUrl = `http://localhost:8080${data.url}`;
                setImageUrl(fullUrl);
                console.log('Imagen subida:', fullUrl);
            } else {
                setError(data.error);
            }
        } catch (err) {
            setError('Error de conexión: ' + err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <input 
                type="file" 
                accept="image/jpeg,image/png,image/gif,image/webp"
                onChange={handleFileChange}
                disabled={loading}
            />

            {loading && <p>Subiendo...</p>}
            {error && <p style={{color: 'red'}}>{error}</p>}
            
            {imageUrl && (
                <div>
                    <p>¡Imagen subida exitosamente!</p>
                    <img src={imageUrl} alt="Uploaded" style={{maxWidth: '300px'}} />
                    <p>URL: {imageUrl}</p>
                </div>
            )}
        </div>
    );
}

export default UploadImage;
```

---

## ✅ Checklist de Implementación

- [x] Endpoint `/uploads` creado
- [x] Validación de formato (jpg, jpeg, png, gif, webp)
- [x] Validación de tamaño (máximo 5MB)
- [x] Sanitización de nombres de archivo
- [x] Carpeta de imágenes creada
- [x] Configuración de Spring multipart
- [x] CORS configurado
- [x] Script de prueba PowerShell
- [x] Página HTML de prueba
- [x] Documentación completa

---

## 🎉 Estado Final

✅ **Endpoint funcionando correctamente**  
✅ **Validaciones implementadas**  
✅ **Archivos de prueba creados**  
✅ **Documentación completa**

**¡El endpoint de subida de imágenes está listo para usar!** 🚀
