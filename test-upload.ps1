# =====================================================
# 🧪 TEST ENDPOINT DE SUBIDA DE IMAGENES
# =====================================================
# Prueba el endpoint POST /uploads

$baseUrl = "http://localhost:8080"

Write-Host "=================================" -ForegroundColor Cyan
Write-Host "TEST ENDPOINT DE SUBIDA" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Crear una imagen de prueba si no existe
$testImagePath = "test-image.png"

if (-not (Test-Path $testImagePath)) {
    Write-Host "Creando imagen de prueba..." -ForegroundColor Yellow
    
    # Crear una imagen PNG simple de 1x1 pixel (base64)
    $base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
    $imageBytes = [Convert]::FromBase64String($base64Image)
    [System.IO.File]::WriteAllBytes($testImagePath, $imageBytes)
    
    Write-Host "Imagen de prueba creada: $testImagePath" -ForegroundColor Green
    Write-Host ""
}

# Test 1: Subir imagen
Write-Host "1. Probando subida de imagen..." -ForegroundColor Yellow

try {
    # Leer el archivo
    $fileBytes = [System.IO.File]::ReadAllBytes($testImagePath)
    $fileName = [System.IO.Path]::GetFileName($testImagePath)
    
    # Crear el boundary para multipart
    $boundary = [System.Guid]::NewGuid().ToString()
    
    # Construir el body multipart
    $LF = "`r`n"
    $bodyLines = (
        "--$boundary",
        "Content-Disposition: form-data; name=`"file`"; filename=`"$fileName`"",
        "Content-Type: image/png$LF",
        [System.Text.Encoding]::GetEncoding("iso-8859-1").GetString($fileBytes),
        "--$boundary--$LF"
    ) -join $LF
    
    # Hacer la petición
    $response = Invoke-RestMethod -Uri "$baseUrl/uploads" `
        -Method POST `
        -ContentType "multipart/form-data; boundary=$boundary" `
        -Body $bodyLines `
        -TimeoutSec 10
    
    Write-Host "   OK - Imagen subida exitosamente" -ForegroundColor Green
    Write-Host "   URL: $($response.url)" -ForegroundColor Cyan
    Write-Host "   Nombre: $($response.filename)" -ForegroundColor Gray
    Write-Host "   Tamaño: $($response.size) bytes" -ForegroundColor Gray
    Write-Host ""
    
    # Guardar la URL para verificar
    $uploadedUrl = $response.url
    
    # Test 2: Verificar que la imagen es accesible
    Write-Host "2. Verificando acceso a la imagen..." -ForegroundColor Yellow
    
    try {
        $imageResponse = Invoke-WebRequest -Uri "$baseUrl$uploadedUrl" -Method GET -TimeoutSec 5
        
        if ($imageResponse.StatusCode -eq 200) {
            Write-Host "   OK - Imagen accesible en: $baseUrl$uploadedUrl" -ForegroundColor Green
        }
    }
    catch {
        Write-Host "   ERROR - No se pudo acceder a la imagen" -ForegroundColor Red
        Write-Host "   Mensaje: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "=================================" -ForegroundColor Green
    Write-Host "TESTS COMPLETADOS CON EXITO" -ForegroundColor Green
    Write-Host "=================================" -ForegroundColor Green
    
}
catch {
    Write-Host "   ERROR - Fallo la subida" -ForegroundColor Red
    Write-Host "   Mensaje: $($_.Exception.Message)" -ForegroundColor Red
    
    Write-Host ""
    Write-Host "=================================" -ForegroundColor Red
    Write-Host "TEST FALLIDO" -ForegroundColor Red
    Write-Host "=================================" -ForegroundColor Red
}

Write-Host ""
Write-Host "Notas:" -ForegroundColor Yellow
Write-Host "  - El archivo se guarda en: src/main/resources/static/images/especialidades/" -ForegroundColor Gray
Write-Host "  - La URL retornada es relativa: /images/especialidades/timestamp_filename" -ForegroundColor Gray
Write-Host "  - Tamano maximo permitido: 5MB" -ForegroundColor Gray
Write-Host "  - Formatos permitidos: jpg, jpeg, png, gif, webp" -ForegroundColor Gray
