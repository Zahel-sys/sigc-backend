# Test JWT Fix - Verificar que el token contiene ID en "sub"
Write-Host "`n=== TEST JWT CON ID EN SUB ===" -ForegroundColor Cyan
Write-Host "Probando que el token JWT ahora usa ID numérico en 'sub'`n" -ForegroundColor Yellow

# 1. Login
Write-Host "1. POST /auth/login" -ForegroundColor Magenta
$loginBody = @{
    email = "testjwt@test.com"
    password = "12345678"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody

    Write-Host "✅ Login exitoso" -ForegroundColor Green
    Write-Host "`nRespuesta completa:" -ForegroundColor Yellow
    $response | ConvertTo-Json -Depth 3 | Write-Host
    
    $token = $response.token
    
    # 2. Decodificar payload del token (parte entre los dos puntos)
    Write-Host "`n2. Decodificando token JWT..." -ForegroundColor Magenta
    $parts = $token.Split('.')
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
        
        Write-Host "✅ Payload del token decodificado:" -ForegroundColor Green
        $claims | ConvertTo-Json -Depth 3 | Write-Host
        
        # 3. Verificar campos esperados
        Write-Host "`n3. Verificación de campos:" -ForegroundColor Magenta
        
        if ($claims.sub -match '^\d+$') {
            Write-Host "✅ sub contiene ID numérico: $($claims.sub)" -ForegroundColor Green
        } else {
            Write-Host "❌ ERROR: sub NO contiene ID numérico: $($claims.sub)" -ForegroundColor Red
        }
        
        if ($claims.email) {
            Write-Host "✅ email presente: $($claims.email)" -ForegroundColor Green
        } else {
            Write-Host "❌ ERROR: email NO presente en el token" -ForegroundColor Red
        }
        
        if ($claims.rol) {
            Write-Host "✅ rol presente: $($claims.rol)" -ForegroundColor Green
        } else {
            Write-Host "❌ ERROR: rol NO presente en el token" -ForegroundColor Red
        }
        
        Write-Host "`n=== ESTRUCTURA ESPERADA ===" -ForegroundColor Cyan
        Write-Host @"
{
  "sub": "1",              ← ID numérico del usuario
  "email": "1flores@gmail.com",
  "rol": "PACIENTE",
  "iat": $($claims.iat),
  "exp": $($claims.exp)
}
"@ -ForegroundColor Yellow
        
    } else {
        Write-Host "❌ ERROR: Token mal formado" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ ERROR en login: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.Exception.Response | ConvertTo-Json
}

Write-Host "`n=== FIN DEL TEST ===" -ForegroundColor Cyan
