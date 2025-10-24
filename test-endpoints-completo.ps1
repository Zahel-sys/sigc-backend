# ========================================
# 🧪 TEST COMPLETO DE ENDPOINTS - SIGC
# ========================================
# Este script prueba TODOS los endpoints del backend
# Verifica: Especialidades, Doctores, Horarios, Citas, Servicios, Usuarios

$ErrorActionPreference = "SilentlyContinue"
$baseUrl = "http://localhost:8080"
$passed = 0
$failed = 0

Write-Host "`n🔷 ========================================" -ForegroundColor Cyan
Write-Host "🔷 TEST COMPLETO DE ENDPOINTS - SIGC" -ForegroundColor Cyan
Write-Host "🔷 ========================================`n" -ForegroundColor Cyan

# Función auxiliar para hacer requests
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Description,
        [object]$Body = $null
    )
    
    Write-Host "📌 $Description" -ForegroundColor Yellow
    Write-Host "   $Method $Url" -ForegroundColor Gray
    
    try {
        $headers = @{
            "Content-Type" = "application/json"
            "Accept" = "application/json"
        }
        
        if ($Body) {
            $jsonBody = $Body | ConvertTo-Json -Depth 10
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -Body $jsonBody -ErrorAction Stop
        } else {
            $response = Invoke-RestMethod -Uri $Url -Method $Method -Headers $headers -ErrorAction Stop
        }
        
        Write-Host "   ✅ SUCCESS" -ForegroundColor Green
        
        # Mostrar información de la respuesta
        if ($response -is [array]) {
            Write-Host "   📊 Registros encontrados: $($response.Count)" -ForegroundColor Cyan
        } elseif ($response -is [PSCustomObject]) {
            Write-Host "   📄 Respuesta recibida correctamente" -ForegroundColor Cyan
        }
        
        $script:passed++
        return $response
    }
    catch {
        Write-Host "   ❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $script:failed++
        return $null
    }
    
    Write-Host ""
}

# ========================================
# 1️⃣ ESPECIALIDADES
# ========================================
Write-Host "`n🏥 ESPECIALIDADES" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/especialidades" -Description "GET /especialidades - Listar todas"

# ========================================
# 2️⃣ DOCTORES
# ========================================
Write-Host "`n👨‍⚕️ DOCTORES" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/doctores" -Description "GET /doctores - Listar todos"

# ========================================
# 3️⃣ HORARIOS
# ========================================
Write-Host "`n🕐 HORARIOS" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/horarios" -Description "GET /horarios - Listar todos"

# ========================================
# 4️⃣ CITAS
# ========================================
Write-Host "`n📅 CITAS" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/citas" -Description "GET /citas - Listar todas"

# ========================================
# 5️⃣ SERVICIOS
# ========================================
Write-Host "`n🏥 SERVICIOS" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/servicios" -Description "GET /servicios - Listar todos"

# ========================================
# 6️⃣ USUARIOS
# ========================================
Write-Host "`n👥 USUARIOS" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta
Test-Endpoint -Method "GET" -Url "$baseUrl/usuarios" -Description "GET /usuarios - Listar todos"

# ========================================
# 7️⃣ AUTENTICACIÓN
# ========================================
Write-Host "`n🔐 AUTENTICACIÓN" -ForegroundColor Magenta
Write-Host "─────────────────" -ForegroundColor Magenta

$loginData = @{
    email = "admin@sigc.com"
    password = "Admin123456"
}

$loginResponse = Test-Endpoint -Method "POST" -Url "$baseUrl/auth/login" -Description "POST /auth/login - Login de admin" -Body $loginData

if ($loginResponse) {
    Write-Host "   🔑 Token recibido: $($loginResponse.token.Substring(0, 20))..." -ForegroundColor Green
    Write-Host "   👤 Rol: $($loginResponse.rol)" -ForegroundColor Green
}

# ========================================
# 📊 RESUMEN FINAL
# ========================================
Write-Host "`n" 
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor White
Write-Host "║        📊 RESUMEN DE PRUEBAS          ║" -ForegroundColor White
Write-Host "╠════════════════════════════════════════╣" -ForegroundColor White
Write-Host "║  ✅ Pasadas:  $passed                        ║" -ForegroundColor Green
Write-Host "║  ❌ Fallidas: $failed                        ║" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor White

if ($failed -eq 0) {
    Write-Host "`n✅ TODOS LOS TESTS PASARON EXITOSAMENTE" -ForegroundColor Green
    Write-Host "🎉 El backend está funcionando correctamente" -ForegroundColor Green
} else {
    Write-Host "`n⚠️  ALGUNOS TESTS FALLARON" -ForegroundColor Yellow
    Write-Host "Por favor revisa los errores arriba" -ForegroundColor Yellow
}

Write-Host "`n"
