# Test PUT /especialidades/1 con FormData
$boundary = "----WebKitFormBoundary" + [System.Guid]::NewGuid().ToString("N")
$url = "http://localhost:8080/especialidades/1"

# Construir el cuerpo multipart/form-data manualmente
$bodyLines = @(
    "--$boundary",
    'Content-Disposition: form-data; name="nombre"',
    '',
    'Cardiologia Actualizada',
    "--$boundary",
    'Content-Disposition: form-data; name="descripcion"',
    '',
    'Especialidad de corazon y sistema cardiovascular - ACTUALIZADO',
    "--$boundary",
    'Content-Disposition: form-data; name="imagen"',
    '',
    'cardiologia-nueva.jpg',
    "--$boundary--"
)

$body = $bodyLines -join "`r`n"

Write-Host "=== TEST PUT /especialidades/1 con FormData ===" -ForegroundColor Cyan
Write-Host "URL: $url"
Write-Host "Content-Type: multipart/form-data; boundary=$boundary"
Write-Host ""
Write-Host "Body enviado:" -ForegroundColor Yellow
Write-Host $body
Write-Host ""

try {
    $response = Invoke-WebRequest -Uri $url -Method PUT `
        -ContentType "multipart/form-data; boundary=$boundary" `
        -Body $body `
        -UseBasicParsing
    
    Write-Host "✅ Respuesta exitosa!" -ForegroundColor Green
    Write-Host "Status: $($response.StatusCode)"
    Write-Host "Body:" -ForegroundColor Yellow
    Write-Host ($response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 5)
} catch {
    Write-Host "❌ Error en la peticion:" -ForegroundColor Red
    Write-Host "StatusCode: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "StatusDescription: $($_.Exception.Response.StatusDescription)"
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body:" -ForegroundColor Yellow
        Write-Host $responseBody
    }
}
