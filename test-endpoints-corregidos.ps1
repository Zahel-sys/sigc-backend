# =====================================================
# 🧪 TEST COMPLETO - ENDPOINTS SIN /api
# =====================================================
# Prueba todos los endpoints corregidos (sin prefijo /api)

$baseUrl = "http://localhost:8080"
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "🧪 PROBANDO ENDPOINTS CORREGIDOS" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Función para hacer peticiones con manejo de errores
function Test-Endpoint {
    param(
        [string]$Nombre,
        [string]$Url,
        [string]$Metodo = "GET",
        [object]$Body = $null
    )
    
    Write-Host "📡 Probando: $Nombre" -ForegroundColor Yellow
    Write-Host "   URL: $Url" -ForegroundColor Gray
    
    try {
        $params = @{
            Uri = $Url
            Method = $Metodo
            ContentType = "application/json"
            TimeoutSec = 10
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json)
        }
        
        $response = Invoke-RestMethod @params
        
        Write-Host "   ✅ STATUS: 200 OK" -ForegroundColor Green
        
        # Mostrar cantidad de resultados si es un array
        if ($response -is [Array]) {
            Write-Host "   📊 Registros encontrados: $($response.Count)" -ForegroundColor Cyan
            if ($response.Count -gt 0) {
                Write-Host "   📋 Primer registro:" -ForegroundColor Gray
                $response[0] | ConvertTo-Json -Depth 2 | Write-Host -ForegroundColor White
            }
        } else {
            $response | ConvertTo-Json -Depth 2 | Write-Host -ForegroundColor White
        }
        
        Write-Host ""
        return $true
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "   ❌ ERROR $statusCode" -ForegroundColor Red
        Write-Host "   💬 Mensaje: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host ""
        return $false
    }
}

# Esperar a que el backend esté listo
Write-Host "⏳ Esperando a que el backend esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

$testsPassed = 0
$testsFailed = 0

# =====================================================
# 1️⃣ TEST: GET /especialidades
# =====================================================
if (Test-Endpoint -Nombre "GET /especialidades" -Url "$baseUrl/especialidades") {
    $testsPassed++
} else {
    $testsFailed++
}

# =====================================================
# 2️⃣ TEST: GET /doctores
# =====================================================
if (Test-Endpoint -Nombre "GET /doctores" -Url "$baseUrl/doctores") {
    $testsPassed++
} else {
    $testsFailed++
}

# =====================================================
# 3️⃣ TEST: GET /horarios
# =====================================================
if (Test-Endpoint -Nombre "GET /horarios" -Url "$baseUrl/horarios") {
    $testsPassed++
} else {
    $testsFailed++
}

# =====================================================
# 4️⃣ TEST: GET /citas
# =====================================================
if (Test-Endpoint -Nombre "GET /citas" -Url "$baseUrl/citas") {
    $testsPassed++
} else {
    $testsFailed++
}

# =====================================================
# 5️⃣ TEST: GET /servicios
# =====================================================
if (Test-Endpoint -Nombre "GET /servicios" -Url "$baseUrl/servicios") {
    $testsPassed++
} else {
    $testsFailed++
}

# =====================================================
# 6️⃣ TEST: GET /usuarios
# =====================================================
if (Test-Endpoint -Nombre "GET /usuarios" -Url "$baseUrl/usuarios") {
    $testsPassed++
} else {
    $testsFailed++
}

# =====================================================
# 📊 RESUMEN FINAL
# =====================================================
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "📊 RESUMEN DE PRUEBAS" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "✅ Tests exitosos: $testsPassed" -ForegroundColor Green
Write-Host "❌ Tests fallidos: $testsFailed" -ForegroundColor Red
Write-Host ""

if ($testsFailed -eq 0) {
    Write-Host "🎉 TODOS LOS TESTS PASARON - BACKEND FUNCIONA CORRECTAMENTE" -ForegroundColor Green
} else {
    Write-Host "⚠️ HAY ERRORES QUE CORREGIR" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "💡 Nota: Si hay errores 404, verifica que:" -ForegroundColor Yellow
Write-Host "   1. El backend este ejecutandose en http://localhost:8080" -ForegroundColor Gray
Write-Host "   2. Los controladores tengan las rutas sin /api" -ForegroundColor Gray
Write-Host "   3. SecurityConfig permita acceso a estas rutas" -ForegroundColor Gray
