# Test simple de endpoints
$baseUrl = "http://localhost:8080"
$passed = 0
$failed = 0

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TEST DE ENDPOINTS - SIGC" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

function Test-Endpoint {
    param([string]$Url, [string]$Description)
    
    Write-Host "$Description" -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri $Url -Method GET -ErrorAction Stop
        if ($response -is [array]) {
            Write-Host "  ✅ SUCCESS - $($response.Count) registros" -ForegroundColor Green
        } else {
            Write-Host "  ✅ SUCCESS" -ForegroundColor Green
        }
        $script:passed++
    } catch {
        Write-Host "  ❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
        $script:failed++
    }
    Write-Host ""
}

# Probar endpoints
Test-Endpoint "$baseUrl/especialidades" "GET /especialidades"
Test-Endpoint "$baseUrl/doctores" "GET /doctores"
Test-Endpoint "$baseUrl/horarios" "GET /horarios"
Test-Endpoint "$baseUrl/citas" "GET /citas"
Test-Endpoint "$baseUrl/servicios" "GET /servicios"
Test-Endpoint "$baseUrl/usuarios" "GET /usuarios"

# Test de login
Write-Host "POST /auth/login (admin)" -ForegroundColor Yellow
try {
    $loginData = @{
        email = "admin@sigc.com"
        password = "Admin123456"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json" -ErrorAction Stop
    Write-Host "  ✅ SUCCESS - Token recibido" -ForegroundColor Green
    Write-Host "  Rol: $($response.rol)" -ForegroundColor Cyan
    $script:passed++
} catch {
    Write-Host "  ❌ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    $script:failed++
}

Write-Host "`n========================================" -ForegroundColor White
Write-Host "RESUMEN" -ForegroundColor White
Write-Host "========================================" -ForegroundColor White
Write-Host "✅ Pasadas:  $passed" -ForegroundColor Green
Write-Host "❌ Fallidas: $failed" -ForegroundColor $(if ($failed -eq 0) { "Green" } else { "Red" })
Write-Host "========================================`n" -ForegroundColor White

if ($failed -eq 0) {
    Write-Host "✅ TODOS LOS TESTS PASARON" -ForegroundColor Green
} else {
    Write-Host "⚠️  ALGUNOS TESTS FALLARON" -ForegroundColor Yellow
}
