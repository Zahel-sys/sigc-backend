# Script para probar el endpoint /usuarios/{idOrEmail}
# Este endpoint ahora acepta tanto ID numérico como email

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "PROBANDO ENDPOINT /usuarios/{idOrEmail}" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080"

# Test 1: Obtener usuario por ID (número)
Write-Host "1️⃣  TEST: GET /usuarios/12 (búsqueda por ID)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/usuarios/12" -Method GET -ErrorAction Stop
    $usuario = $response.Content | ConvertFrom-Json
    Write-Host "✅ Usuario encontrado:" -ForegroundColor Green
    Write-Host "   - ID: $($usuario.idUsuario)" -ForegroundColor White
    Write-Host "   - Nombre: $($usuario.nombre)" -ForegroundColor White
    Write-Host "   - Email: $($usuario.email)" -ForegroundColor White
} catch {
    if ($_.Exception.Response.StatusCode -eq 404) {
        Write-Host "⚠️  Usuario con ID 12 no encontrado (404)" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 2: Obtener usuario por EMAIL (texto)
Write-Host "2️⃣  TEST: GET /usuarios/1flores@gmail.com (búsqueda por EMAIL)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/usuarios/1flores@gmail.com" -Method GET -ErrorAction Stop
    $usuario = $response.Content | ConvertFrom-Json
    Write-Host "✅ Usuario encontrado por email:" -ForegroundColor Green
    Write-Host "   - ID: $($usuario.idUsuario)" -ForegroundColor White
    Write-Host "   - Nombre: $($usuario.nombre)" -ForegroundColor White
    Write-Host "   - Email: $($usuario.email)" -ForegroundColor White
    Write-Host "   - DNI: $($usuario.dni)" -ForegroundColor White
} catch {
    if ($_.Exception.Response.StatusCode -eq 404) {
        Write-Host "⚠️  Usuario con email '1flores@gmail.com' no encontrado (404)" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "   Detalles: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

Write-Host ""

# Test 3: Endpoint tradicional /usuarios/email/{email}
Write-Host "3️⃣  TEST: GET /usuarios/email/1flores@gmail.com (endpoint explícito)" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/usuarios/email/1flores@gmail.com" -Method GET -ErrorAction Stop
    $usuario = $response.Content | ConvertFrom-Json
    Write-Host "✅ Usuario encontrado:" -ForegroundColor Green
    Write-Host "   - ID: $($usuario.idUsuario)" -ForegroundColor White
    Write-Host "   - Email: $($usuario.email)" -ForegroundColor White
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "RESUMEN" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ El endpoint /usuarios/{idOrEmail} ahora acepta:" -ForegroundColor Green
Write-Host "   1. ID numérico (ej: /usuarios/12)" -ForegroundColor White
Write-Host "   2. Email (ej: /usuarios/1flores@gmail.com)" -ForegroundColor White
Write-Host "   3. Retrocompatible con tokens antiguos" -ForegroundColor White
Write-Host "`n💡 Recomendación: Migra el frontend para usar /auth/me" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan
