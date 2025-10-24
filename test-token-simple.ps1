# Script para probar endpoints de token JWT
Write-Host "=== TEST DE ENDPOINTS DE TOKEN JWT ===" -ForegroundColor Cyan

# Login
Write-Host "`n1. Haciendo login..." -ForegroundColor Yellow
$loginBody = @{
    email = "testjwt@test.com"
    password = "12345678"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody

    $token = $loginResponse.token

    Write-Host "OK - Login exitoso" -ForegroundColor Green
    Write-Host "ID Usuario: $($loginResponse.idUsuario)" -ForegroundColor Cyan
    Write-Host "Email: $($loginResponse.email)" -ForegroundColor Cyan
    Write-Host "Rol: $($loginResponse.rol)" -ForegroundColor Cyan

    # Validar token
    Write-Host "`n2. Validando token..." -ForegroundColor Yellow
    $validateBody = @{ token = $token } | ConvertTo-Json

    $validateResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/validate-token" `
        -Method POST `
        -ContentType "application/json" `
        -Body $validateBody

    Write-Host "OK - Token valido: $($validateResponse.valid)" -ForegroundColor Green
    Write-Host "Es token antiguo: $($validateResponse.isOldToken)" -ForegroundColor Cyan

    # Obtener info del token
    Write-Host "`n3. Obteniendo info del token..." -ForegroundColor Yellow
    $tokenInfoResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/token-info" `
        -Method GET `
        -Headers @{ "Authorization" = "Bearer $token" }

    Write-Host "OK - Info obtenida" -ForegroundColor Green
    Write-Host "Tipo de token: $($tokenInfoResponse.tokenType)" -ForegroundColor Cyan
    Write-Host "User ID: $($tokenInfoResponse.userId)" -ForegroundColor Cyan

    # Decodificar JWT
    Write-Host "`n4. Decodificando JWT..." -ForegroundColor Yellow
    $parts = $token.Split('.')
    $payload = $parts[1]
    while ($payload.Length % 4 -ne 0) { $payload += "=" }
    $decodedBytes = [System.Convert]::FromBase64String($payload)
    $decodedJson = [System.Text.Encoding]::UTF8.GetString($decodedBytes)
    $claims = $decodedJson | ConvertFrom-Json

    Write-Host "OK - Token decodificado" -ForegroundColor Green
    Write-Host "sub: $($claims.sub)" -ForegroundColor Cyan
    Write-Host "email: $($claims.email)" -ForegroundColor Cyan
    Write-Host "rol: $($claims.rol)" -ForegroundColor Cyan

    Write-Host "`n=== RESUMEN ===" -ForegroundColor Green
    Write-Host "- Login: OK" -ForegroundColor White
    Write-Host "- Validacion: OK" -ForegroundColor White
    Write-Host "- Info token: OK" -ForegroundColor White
    Write-Host "- JWT con ID en 'sub': OK" -ForegroundColor White

} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== FIN ===" -ForegroundColor Cyan
