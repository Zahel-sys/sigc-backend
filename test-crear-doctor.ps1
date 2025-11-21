# Script de prueba para crear un doctor
Write-Host "=== Probando creación de doctor ===" -ForegroundColor Cyan

# Prueba 1: Con datos mínimos
Write-Host "`n1. Creando doctor con datos básicos..." -ForegroundColor Yellow

$boundary = [System.Guid]::NewGuid().ToString()
$LF = "`r`n"

$bodyLines = (
    "--$boundary",
    "Content-Disposition: form-data; name=`"nombre`"$LF",
    "Dr. Juan Perez",
    "--$boundary",
    "Content-Disposition: form-data; name=`"especialidad`"$LF",
    "Cardiologia",
    "--$boundary",
    "Content-Disposition: form-data; name=`"cupoPacientes`"$LF",
    "10",
    "--$boundary--$LF"
) -join $LF

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/doctores" `
        -Method POST `
        -ContentType "multipart/form-data; boundary=$boundary" `
        -Body $bodyLines
    
    Write-Host "✅ Doctor creado exitosamente:" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Error al crear doctor:" -ForegroundColor Red
    Write-Host $_.Exception.Message
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorBody = $reader.ReadToEnd()
        Write-Host "Detalles del error:" -ForegroundColor Red
        Write-Host $errorBody
    }
}

# Verificar doctores existentes
Write-Host "`n2. Listando doctores existentes..." -ForegroundColor Yellow
try {
    $doctores = Invoke-RestMethod -Uri "http://localhost:8080/doctores" -Method GET
    Write-Host "Doctores en la BD:" -ForegroundColor Green
    $doctores | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Error al listar doctores" -ForegroundColor Red
}
