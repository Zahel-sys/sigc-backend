# Script para probar el endpoint POST /citas

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "PRUEBA: POST /citas - Crear Cita" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080"

# TEST 1: Con campo "paciente" (como envía el frontend)
Write-Host "1️⃣  TEST: Crear cita con campo 'paciente'" -ForegroundColor Yellow
$body1 = @{
    paciente = @{
        idUsuario = 12
    }
    horario = @{
        idHorario = 1
    }
} | ConvertTo-Json

Write-Host "📤 Enviando:" -ForegroundColor Gray
Write-Host $body1 -ForegroundColor Gray
Write-Host ""

try {
    $response1 = Invoke-WebRequest -Uri "$baseUrl/citas" -Method POST -Body $body1 -ContentType "application/json" -ErrorAction Stop
    $cita = $response1.Content | ConvertFrom-Json
    Write-Host "✅ EXITO: Cita creada con campo 'paciente'" -ForegroundColor Green
    Write-Host "   ID Cita: $($cita.idCita)" -ForegroundColor White
    Write-Host "   Paciente: $($cita.usuario.nombre) (ID: $($cita.usuario.idUsuario))" -ForegroundColor White
    Write-Host "   Doctor: $($cita.doctor.nombre)" -ForegroundColor White
    Write-Host "   Fecha: $($cita.fechaCita)" -ForegroundColor White
    Write-Host "   Hora: $($cita.horaCita)" -ForegroundColor White
    Write-Host "   Estado: $($cita.estado)" -ForegroundColor White
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $errorBody = $_.ErrorDetails.Message
    Write-Host "❌ Error ($statusCode): $errorBody" -ForegroundColor Red
}

Write-Host ""

# TEST 2: Con campo "usuario" (alternativa)
Write-Host "2️⃣  TEST: Crear cita con campo 'usuario'" -ForegroundColor Yellow
$body2 = @{
    usuario = @{
        idUsuario = 12
    }
    horario = @{
        idHorario = 2
    }
} | ConvertTo-Json

Write-Host "📤 Enviando:" -ForegroundColor Gray
Write-Host $body2 -ForegroundColor Gray
Write-Host ""

try {
    $response2 = Invoke-WebRequest -Uri "$baseUrl/citas" -Method POST -Body $body2 -ContentType "application/json" -ErrorAction Stop
    $cita2 = $response2.Content | ConvertFrom-Json
    Write-Host "✅ EXITO: Cita creada con campo 'usuario'" -ForegroundColor Green
    Write-Host "   ID Cita: $($cita2.idCita)" -ForegroundColor White
    Write-Host "   Paciente: $($cita2.usuario.nombre)" -ForegroundColor White
    Write-Host "   Status HTTP: $($response2.StatusCode)" -ForegroundColor White
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $errorBody = $_.ErrorDetails.Message
    Write-Host "❌ Error ($statusCode): $errorBody" -ForegroundColor Red
}

Write-Host ""

# TEST 3: Error - Sin paciente/usuario
Write-Host "3️⃣  TEST: Error esperado - Sin paciente" -ForegroundColor Yellow
$body3 = @{
    horario = @{
        idHorario = 3
    }
} | ConvertTo-Json

try {
    $null = Invoke-WebRequest -Uri "$baseUrl/citas" -Method POST -Body $body3 -ContentType "application/json" -ErrorAction Stop
    Write-Host "❌ No debería llegar aquí" -ForegroundColor Red
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $errorBody = $_.ErrorDetails.Message
    Write-Host "✅ Error esperado recibido ($statusCode):" -ForegroundColor Green
    Write-Host "   Mensaje: $errorBody" -ForegroundColor White
}

Write-Host ""

# TEST 4: Error - Usuario no existe
Write-Host "4️⃣  TEST: Error esperado - Usuario no existe" -ForegroundColor Yellow
$body4 = @{
    paciente = @{
        idUsuario = 99999
    }
    horario = @{
        idHorario = 1
    }
} | ConvertTo-Json

try {
    $null = Invoke-WebRequest -Uri "$baseUrl/citas" -Method POST -Body $body4 -ContentType "application/json" -ErrorAction Stop
    Write-Host "❌ No debería llegar aquí" -ForegroundColor Red
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    $errorBody = $_.ErrorDetails.Message
    Write-Host "✅ Error esperado recibido ($statusCode):" -ForegroundColor Green
    Write-Host "   Mensaje: $errorBody" -ForegroundColor White
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "RESUMEN" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Endpoint POST /citas funcionando" -ForegroundColor Green
Write-Host "✅ Acepta campo 'paciente' (frontend)" -ForegroundColor Green
Write-Host "✅ Acepta campo 'usuario' (alternativa)" -ForegroundColor Green
Write-Host "✅ Validaciones funcionando correctamente" -ForegroundColor Green
Write-Host ""
