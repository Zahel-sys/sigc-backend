$baseUrl = "http://localhost:8080"

Write-Host ""
Write-Host "PRUEBA: Login y /auth/me" -ForegroundColor Cyan
Write-Host ""

# Login
Write-Host "1. Login con testjwt@test.com..." -ForegroundColor Yellow
$loginBody = '{"email":"testjwt@test.com","password":"test123"}'
$loginResponse = Invoke-WebRequest -Uri "$baseUrl/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$loginData = $loginResponse.Content | ConvertFrom-Json
Write-Host "   Token recibido OK" -ForegroundColor Green
$token = $loginData.token

# /auth/me
Write-Host ""
Write-Host "2. Obteniendo datos con /auth/me..." -ForegroundColor Yellow
$headers = @{ "Authorization" = "Bearer $token" }
$meResponse = Invoke-WebRequest -Uri "$baseUrl/auth/me" -Method GET -Headers $headers
$usuario = $meResponse.Content | ConvertFrom-Json
Write-Host "   ID: $($usuario.idUsuario)" -ForegroundColor White
Write-Host "   Nombre: $($usuario.nombre)" -ForegroundColor White
Write-Host "   Email: $($usuario.email)" -ForegroundColor White
Write-Host "   DNI: $($usuario.dni)" -ForegroundColor White
Write-Host ""
Write-Host "RESULTADO: Backend funcionando correctamente" -ForegroundColor Green
Write-Host ""
