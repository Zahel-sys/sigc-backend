# Script para probar TODOS los escenarios del endpoint /auth/register
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  PRUEBA COMPLETA DEL ENDPOINT /auth/register" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Función para hacer la petición
function Test-Register {
    param(
        [string]$Nombre,
        [string]$Email,
        [string]$Password,
        [string]$Dni,
        [string]$Telefono,
        [string]$Rol,
        [string]$Descripcion
    )
    
    Write-Host "───────────────────────────────────────────────────" -ForegroundColor Gray
    Write-Host "📋 $Descripcion" -ForegroundColor Yellow
    Write-Host ""
    
    $body = @{
        nombre = $Nombre
        email = $Email
        password = $Password
        dni = $Dni
        telefono = $Telefono
        rol = $Rol
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri 'http://localhost:8080/auth/register' -Method POST -Headers @{"Content-Type"="application/json"} -Body $body -UseBasicParsing
        Write-Host "✅ ÉXITO - HTTP $($response.StatusCode)" -ForegroundColor Green
        $response.Content | ConvertFrom-Json | ConvertTo-Json
    } catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "⚠️  HTTP $statusCode" -ForegroundColor $(if ($statusCode -eq 409) { "Yellow" } else { "Red" })
        
        if ($_.ErrorDetails.Message) {
            try {
                $errorJson = $_.ErrorDetails.Message | ConvertFrom-Json
                $errorJson | ConvertTo-Json
            } catch {
                $_.ErrorDetails.Message
            }
        }
    }
    Write-Host ""
}

# Verificar que el backend está corriendo
Write-Host "🔍 Verificando que el backend está corriendo..." -ForegroundColor Cyan
$testConn = Test-NetConnection -ComputerName localhost -Port 8080 -InformationLevel Quiet -WarningAction SilentlyContinue

if (-not $testConn) {
    Write-Host "❌ ERROR: El backend NO está corriendo en puerto 8080" -ForegroundColor Red
    Write-Host "   Por favor inicia el backend con: .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
    exit
}

Write-Host "✅ Backend está corriendo" -ForegroundColor Green
Write-Host ""

# Test 1: Usuario nuevo (debe funcionar)
$timestamp = Get-Date -Format "HHmmss"
Test-Register -Nombre "Usuario Nuevo" -Email "nuevo_$timestamp@example.com" -Password "Test123456" -Dni "12345678" -Telefono "987654321" -Rol "PACIENTE" -Descripcion "Test 1: Usuario nuevo (debe retornar 201)"

# Test 2: Email duplicado con el usuario que acabamos de crear (debe retornar 409)
Test-Register -Nombre "Usuario Duplicado" -Email "nuevo_$timestamp@example.com" -Password "Test123456" -Dni "87654321" -Telefono "123456789" -Rol "PACIENTE" -Descripcion "Test 2: Email duplicado (debe retornar 409 Conflict)"

# Test 3: Validación - email inválido (debe retornar 400)
Test-Register -Nombre "Usuario Invalido" -Email "email-invalido" -Password "Test123456" -Dni "12345678" -Telefono "987654321" -Rol "PACIENTE" -Descripcion "Test 3: Email inválido (debe retornar 400 Bad Request)"

# Test 4: Validación - DNI con letras (debe retornar 400)
Test-Register -Nombre "Usuario DNI Invalido" -Email "dni_$timestamp@example.com" -Password "Test123456" -Dni "ABC12345" -Telefono "987654321" -Rol "PACIENTE" -Descripcion "Test 4: DNI inválido (debe retornar 400 Bad Request)"

# Test 5: Con el email del usuario real (lfloresb@gmail.com)
Test-Register -Nombre "Leonardo" -Email "lfloresb@gmail.com" -Password "Test123456" -Dni "72048204" -Telefono "960489524" -Rol "PACIENTE" -Descripcion "Test 5: Email del usuario real (puede ser 201 si no existe, o 409 si ya existe)"

Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  PRUEBAS COMPLETADAS" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
Write-Host "RESUMEN:" -ForegroundColor Yellow
Write-Host "• HTTP 201 = Usuario creado exitosamente ✅" -ForegroundColor Green
Write-Host "• HTTP 409 = Email/DNI duplicado (esperado) ⚠️" -ForegroundColor Yellow  
Write-Host "• HTTP 400 = Errores de validación ⚠️" -ForegroundColor Yellow
Write-Host "• HTTP 500 = ERROR DEL SERVIDOR (NO DEBE APARECER) ❌" -ForegroundColor Red
Write-Host ""
