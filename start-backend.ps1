# Script para ejecutar el backend SIGC
Set-Location "c:\Users\LEONARDO\sigc-backend"

Write-Host "🚀 Iniciando Backend SIGC..." -ForegroundColor Green
Write-Host "Ejecutando: java -jar target\backend-0.0.1-SNAPSHOT.jar" -ForegroundColor Yellow

java -jar target\backend-0.0.1-SNAPSHOT.jar
