# Probar endpoint POST /auth/register

$body = @{
    nombre = "Leonardo"
    email = "lfloresb@gmail.com"
    password = "Test123456"
    dni = "72048204"
    telefono = "960489524"
    rol = "PACIENTE"
} | ConvertTo-Json

Write-Host "Probando POST /auth/register..." -ForegroundColor Cyan
Write-Host "Datos: $body" -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/auth/register' -Method POST -Headers @{"Content-Type"="application/json"} -Body $body -UseBasicParsing
    Write-Host "SUCCESS - HTTP $($response.StatusCode)" -ForegroundColor Green
    $response.Content
} catch {
    Write-Host "ERROR - HTTP $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        $_.ErrorDetails.Message
    } else {
        $_.Exception.Message
    }
}
