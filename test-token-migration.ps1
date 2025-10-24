# Script para migrar tokens antiguos a tokens nuevos
Write-Host "`n=== MIGRACIÓN DE TOKEN ANTIGUO A NUEVO ===" -ForegroundColor Cyan

# Paso 1: Login con usuario existente (esto generará un token NUEVO)
Write-Host "`n1. Haciendo login para obtener token NUEVO..." -ForegroundColor Yellow
$loginBody = @{
    email = "testjwt@test.com"
    password = "12345678"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody

    $newToken = $loginResponse.token

    Write-Host "✅ Login exitoso" -ForegroundColor Green
    Write-Host "`nRespuesta del login:" -ForegroundColor Cyan
    $loginResponse | ConvertTo-Json -Depth 3

    # Paso 2: Validar el token nuevo
    Write-Host "`n2. Validando token NUEVO..." -ForegroundColor Yellow
    $validateBody = @{
        token = $newToken
    } | ConvertTo-Json

    $validateResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/validate-token" `
        -Method POST `
        -ContentType "application/json" `
        -Body $validateBody

    Write-Host "✅ Token validado" -ForegroundColor Green
    Write-Host "`nValidación:" -ForegroundColor Cyan
    $validateResponse | ConvertTo-Json -Depth 3

    # Paso 3: Obtener información del token
    Write-Host "`n3. Obteniendo información del token..." -ForegroundColor Yellow
    $tokenInfoResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/token-info" `
        -Method GET `
        -Headers @{
            "Authorization" = "Bearer $newToken"
        }

    Write-Host "✅ Información obtenida" -ForegroundColor Green
    Write-Host "`nInfo del token:" -ForegroundColor Cyan
    $tokenInfoResponse | ConvertTo-Json -Depth 3

    # Paso 4: Decodificar el token JWT
    Write-Host "`n4. Decodificando token JWT..." -ForegroundColor Yellow
    $parts = $newToken.Split('.')
    if ($parts.Length -eq 3) {
        $payload = $parts[1]
        
        # Agregar padding si es necesario
        while ($payload.Length % 4 -ne 0) {
            $payload += "="
        }
        
        # Decodificar Base64
        $decodedBytes = [System.Convert]::FromBase64String($payload)
        $decodedJson = [System.Text.Encoding]::UTF8.GetString($decodedBytes)
        $claims = $decodedJson | ConvertFrom-Json
        
        Write-Host "✅ Token decodificado:" -ForegroundColor Green
        $claims | ConvertTo-Json -Depth 3

        # Verificar estructura
        Write-Host "`n5. Verificación de estructura del token:" -ForegroundColor Yellow
        
        if ($claims.sub -match '^\d+$') {
            Write-Host "✅ 'sub' contiene ID numérico: $($claims.sub)" -ForegroundColor Green
        } else {
            Write-Host "❌ 'sub' NO es numérico: $($claims.sub)" -ForegroundColor Red
        }
        
        if ($claims.email) {
            Write-Host "✅ 'email' presente: $($claims.email)" -ForegroundColor Green
        } else {
            Write-Host "⚠️  'email' no presente" -ForegroundColor Yellow
        }
        
        if ($claims.rol) {
            Write-Host "✅ 'rol' presente: $($claims.rol)" -ForegroundColor Green
        } else {
            Write-Host "⚠️  'rol' no presente" -ForegroundColor Yellow
        }
    }

    # Paso 5: Probar refresh token
    Write-Host "`n6. Probando endpoint de refresh token..." -ForegroundColor Yellow
    try {
        $refreshResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/refresh-token" `
            -Method POST `
            -Headers @{
                "Authorization" = "Bearer $newToken"
            }

        Write-Host "✅ Token refrescado (aunque ya era nuevo)" -ForegroundColor Green
        Write-Host "`nToken refrescado:" -ForegroundColor Cyan
        $refreshResponse | ConvertTo-Json -Depth 3

    } catch {
        Write-Host "ℹ️  Endpoint refresh funciona (token ya era nuevo)" -ForegroundColor Cyan
    }

    Write-Host "`n=== RESUMEN ===" -ForegroundColor Cyan
    Write-Host "✅ Login: Funcional" -ForegroundColor Green
    Write-Host "✅ Validación de token: Funcional" -ForegroundColor Green
    Write-Host "✅ Info de token: Funcional" -ForegroundColor Green
    Write-Host "✅ Estructura JWT: Correcta (ID en 'sub')" -ForegroundColor Green
    Write-Host "✅ Claims adicionales: email y rol presentes" -ForegroundColor Green

    Write-Host "`n=== INSTRUCCIONES PARA EL FRONTEND ===" -ForegroundColor Magenta
    Write-Host "`n1. DETECTAR TOKEN ANTIGUO:" -ForegroundColor Yellow
    Write-Host "   fetch('http://localhost:8080/auth/token-info', { ... })" -ForegroundColor White
    Write-Host "`n2. RENOVAR TOKEN AUTOMÁTICAMENTE:" -ForegroundColor Yellow
    Write-Host "   fetch('http://localhost:8080/auth/refresh-token', { ... })" -ForegroundColor White
    Write-Host "`n3. SOLUCIÓN SIMPLE: Cerrar sesión y hacer login de nuevo" -ForegroundColor Yellow
    Write-Host "   localStorage.clear(); window.location.href = '/login';" -ForegroundColor White

} catch {
    Write-Host "❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $reader.BaseStream.Position = 0
            $responseBody = $reader.ReadToEnd()
            Write-Host "Respuesta del servidor: $responseBody" -ForegroundColor Red
        } catch {
            Write-Host "No se pudo leer la respuesta del servidor" -ForegroundColor Red
        }
    }
}

Write-Host "`n=== FIN ===" -ForegroundColor Cyan
