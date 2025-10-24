# Script para probar login del administrador

Write-Host "══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "  PRUEBA DE LOGIN - USUARIO ADMINISTRADOR" -ForegroundColor Cyan
Write-Host "══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Verificar que el backend está corriendo
Write-Host "🔍 Verificando backend..." -ForegroundColor Yellow
$testConn = Test-NetConnection -ComputerName localhost -Port 8080 -InformationLevel Quiet -WarningAction SilentlyContinue

if (-not $testConn) {
    Write-Host "❌ ERROR: El backend NO está corriendo en puerto 8080" -ForegroundColor Red
    Write-Host "   Por favor inicia el backend con: .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
    exit
}

Write-Host "✅ Backend está corriendo" -ForegroundColor Green
Write-Host ""

# Credenciales del administrador
$credentials = @{
    email = "admin@sigc.com"
    password = "Admin123456"
} | ConvertTo-Json

Write-Host "📧 Email:    admin@sigc.com" -ForegroundColor Cyan
Write-Host "🔑 Password: Admin123456" -ForegroundColor Cyan
Write-Host ""
Write-Host "📤 Enviando petición a POST /auth/login..." -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest `
        -Uri 'http://localhost:8080/auth/login' `
        -Method POST `
        -Headers @{"Content-Type"="application/json"} `
        -Body $credentials `
        -UseBasicParsing
    
    Write-Host "✅ LOGIN EXITOSO - HTTP $($response.StatusCode)" -ForegroundColor Green
    Write-Host ""
    Write-Host "📨 Respuesta del servidor:" -ForegroundColor Green
    
    $jsonResponse = $response.Content | ConvertFrom-Json
    Write-Host ""
    Write-Host "🎫 Token JWT:" -ForegroundColor Cyan
    Write-Host $jsonResponse.token -ForegroundColor White
    Write-Host ""
    
    if ($jsonResponse.usuario) {
        Write-Host "👤 Datos del usuario:" -ForegroundColor Cyan
        Write-Host "   Nombre: $($jsonResponse.usuario.nombre)" -ForegroundColor White
        Write-Host "   Email:  $($jsonResponse.usuario.email)" -ForegroundColor White
        Write-Host "   Rol:    $($jsonResponse.usuario.rol)" -ForegroundColor White
        Write-Host ""
    }
    
    Write-Host "══════════════════════════════════════════════════" -ForegroundColor Green
    Write-Host "  ✅ AUTENTICACIÓN EXITOSA" -ForegroundColor Green
    Write-Host "══════════════════════════════════════════════════" -ForegroundColor Green
    
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    Write-Host "❌ ERROR - HTTP $statusCode" -ForegroundColor Red
    Write-Host ""
    
    if ($_.ErrorDetails.Message) {
        Write-Host "📨 Detalles del error:" -ForegroundColor Red
        try {
            $errorJson = $_.ErrorDetails.Message | ConvertFrom-Json
            $errorJson | ConvertTo-Json
        } catch {
            $_.ErrorDetails.Message
        }
    } else {
        Write-Host $_.Exception.Message -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "💡 Posibles causas:" -ForegroundColor Yellow
    Write-Host "   1. El backend no ha terminado de iniciar (espera un momento)" -ForegroundColor White
    Write-Host "   2. El usuario admin no se creó correctamente" -ForegroundColor White
    Write-Host "   3. La contraseña es incorrecta" -ForegroundColor White
    Write-Host ""
    Write-Host "   Revisa los logs del backend para más detalles" -ForegroundColor White
}

Write-Host ""
