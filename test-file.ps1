# Test PUT con archivo File real
$url = "http://localhost:8080/especialidades/1"

# Crear archivo de prueba
$testImagePath = "c:\Users\LEONARDO\sigc-backend\test-image.jpg"
if (-not (Test-Path $testImagePath)) {
    $jpegBytes = [byte[]](0xFF, 0xD8, 0xFF, 0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0xFF, 0xDB, 0x00, 0x43, 0x00, 0x08, 0x06, 0x06, 0x07, 0x06, 0x05, 0x08, 0x07, 0x07, 0x07, 0x09, 0x09, 0x08, 0x0A, 0x0C, 0x14, 0x0D, 0x0C, 0x0B, 0x0B, 0x0C, 0x19, 0x12, 0x13, 0x0F, 0x14, 0x1D, 0x1A, 0x1F, 0x1E, 0x1D, 0x1A, 0x1C, 0x1C, 0x20, 0x24, 0x2E, 0x27, 0x20, 0x22, 0x2C, 0x23, 0x1C, 0x1C, 0x28, 0x37, 0x29, 0x2C, 0x30, 0x31, 0x34, 0x34, 0x34, 0x1F, 0x27, 0x39, 0x3D, 0x38, 0x32, 0x3C, 0x2E, 0x33, 0x34, 0x32, 0xFF, 0xC0, 0x00, 0x0B, 0x08, 0x00, 0x01, 0x00, 0x01, 0x01, 0x01, 0x11, 0x00, 0xFF, 0xC4, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0xFF, 0xC4, 0x00, 0x14, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xDA, 0x00, 0x08, 0x01, 0x01, 0x00, 0x00, 0x3F, 0x00, 0x7F, 0xFF, 0xD9)
    [System.IO.File]::WriteAllBytes($testImagePath, $jpegBytes)
    Write-Host "Archivo de prueba creado" -ForegroundColor Green
}

Write-Host "=== TEST PUT con File Real ===" -ForegroundColor Cyan

# Construir multipart/form-data
$boundary = "----WebKitFormBoundary" + [System.Guid]::NewGuid().ToString("N")
$LF = "`r`n"

$fileContent = [System.IO.File]::ReadAllBytes($testImagePath)

$bodyLines = @()
$bodyLines += "--$boundary"
$bodyLines += 'Content-Disposition: form-data; name="nombre"'
$bodyLines += ""
$bodyLines += "Cardiologia con File"
$bodyLines += "--$boundary"
$bodyLines += 'Content-Disposition: form-data; name="descripcion"'
$bodyLines += ""
$bodyLines += "Test con archivo binario"
$bodyLines += "--$boundary"
$bodyLines += 'Content-Disposition: form-data; name="imagen"; filename="test.jpg"'
$bodyLines += "Content-Type: image/jpeg"
$bodyLines += ""

$bodyText = ($bodyLines -join $LF) + $LF
$bodyTextBytes = [System.Text.Encoding]::UTF8.GetBytes($bodyText)
$bodyEndBytes = [System.Text.Encoding]::UTF8.GetBytes("$LF--$boundary--$LF")
$bodyBytes = $bodyTextBytes + $fileContent + $bodyEndBytes

Write-Host "Tamano body: $($bodyBytes.Length) bytes"
Write-Host "Tamano imagen: $($fileContent.Length) bytes"
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $url -Method PUT `
        -ContentType "multipart/form-data; boundary=$boundary" `
        -Body $bodyBytes `
        -UseBasicParsing
    
    Write-Host "Respuesta exitosa - Status: $($response.StatusCode)" -ForegroundColor Green
    $json = $response.Content | ConvertFrom-Json
    Write-Host ($json | ConvertTo-Json -Depth 5)
} catch {
    Write-Host "Error - StatusCode: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response:" -ForegroundColor Yellow
        Write-Host $responseBody
    }
}
