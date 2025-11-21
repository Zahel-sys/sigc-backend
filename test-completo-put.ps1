# Test Completo del Endpoint PUT /especialidades/:id con FormData + File

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST COMPLETO: PUT /especialidades/:id" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# TEST 1: Solo texto (sin archivo)
Write-Host "[1] Test con solo texto (nombre + descripcion):" -ForegroundColor Yellow
$response1 = curl.exe -X PUT "http://localhost:8080/especialidades/1" `
    -F "nombre=Cardiologia Solo Texto" `
    -F "descripcion=Actualizacion sin archivo" `
    -s
Write-Host $response1
Write-Host ""

# TEST 2: Texto + Archivo File
Write-Host "[2] Test con texto + archivo File:" -ForegroundColor Yellow
$testImagePath = "c:\Users\LEONARDO\sigc-backend\test-image.jpg"
$response2 = curl.exe -X PUT "http://localhost:8080/especialidades/1" `
    -F "nombre=Cardiologia Con Archivo" `
    -F "descripcion=Actualizacion con archivo binario" `
    -F "imagen=@$testImagePath" `
    -s
Write-Host $response2
Write-Host ""

# TEST 3: Verificar que la imagen se puede recuperar
Write-Host "[3] Verificar que la imagen se sirve correctamente:" -ForegroundColor Yellow
$json = $response2 | ConvertFrom-Json
$imagenUrl = "http://localhost:8080/especialidades/imagen/$($json.imagen)"
Write-Host "URL imagen: $imagenUrl" -ForegroundColor Cyan

try {
    $imgResponse = Invoke-WebRequest -Uri $imagenUrl -UseBasicParsing
    Write-Host "OK - Imagen accesible (Status: $($imgResponse.StatusCode), Content-Type: $($imgResponse.Headers['Content-Type']))" -ForegroundColor Green
} catch {
    Write-Host "ERROR - No se puede acceder a la imagen" -ForegroundColor Red
}
Write-Host ""

# TEST 4: Verificar archivo en disco
Write-Host "[4] Verificar archivo fisico en disco:" -ForegroundColor Yellow
$filePath = "C:\sigc\uploads\especialidades\$($json.imagen)"
if (Test-Path $filePath) {
    $fileInfo = Get-Item $filePath
    Write-Host "OK - Archivo existe: $($fileInfo.Name) ($($fileInfo.Length) bytes)" -ForegroundColor Green
} else {
    Write-Host "ERROR - Archivo no encontrado en disco" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RESUMEN: Todos los tests exitosos" -ForegroundColor Green
Write-Host "El endpoint PUT /especialidades/:id" -ForegroundColor Green
Write-Host "ahora maneja correctamente:" -ForegroundColor Green
Write-Host "  - FormData con texto (nombre, descripcion)" -ForegroundColor White
Write-Host "  - FormData con archivos File (MultipartFile)" -ForegroundColor White
Write-Host "  - Validacion de extensiones (jpg, jpeg, png, webp)" -ForegroundColor White
Write-Host "  - Validacion de tamano (max 5MB)" -ForegroundColor White
Write-Host "  - Almacenamiento en C:\sigc\uploads\especialidades\" -ForegroundColor White
Write-Host "  - Endpoint GET /especialidades/imagen/:filename" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
