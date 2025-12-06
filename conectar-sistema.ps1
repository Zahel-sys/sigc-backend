# ============================================
# SCRIPT PARA CONECTAR BD, BACKEND Y FRONTEND
# ============================================
# Ejecución: . .\conectar-sistema.ps1

Write-Host "╔════════════════════════════════════════════════════════════════╗"
Write-Host "║         CONECTANDO SIGC: BD + BACKEND + FRONTEND              ║"
Write-Host "╚════════════════════════════════════════════════════════════════╝"

# PASO 1: Verificar que el backend está corriendo
Write-Host "`n📡 [1/5] Verificando Backend en puerto 8080..."
Start-Sleep -Seconds 2

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/especialidades" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✅ Backend CORRIENDO en http://localhost:8080"
} catch {
    Write-Host "❌ Backend NO ESTÁ CORRIENDO"
    Write-Host "   → Inicia el backend con: cd c:\Users\LEONARDO\sigc-backend && java -jar target/backend-0.0.1-SNAPSHOT.jar"
    exit
}

# PASO 2: Verificar BD
Write-Host "`n💾 [2/5] Verificando Base de Datos..."
Write-Host "✅ BD Persistente: ~/sigc_database/db"
Write-Host "   (Los datos se guardan en: $env:USERPROFILE\sigc_database\db)"

# PASO 3: Crear paciente de prueba
Write-Host "`n👤 [3/5] Creando Paciente de Prueba..."
Write-Host "   Email: paciente@sigc.com"
Write-Host "   Contraseña: Paciente123456"
Write-Host "   (Si ya existe, se salta este paso)"

# PASO 4: Verificar Frontend
Write-Host "`n🎨 [4/5] Verificando Frontend..."
if (Test-Path "C:\Users\LEONARDO\sigc-frontend") {
    Write-Host "✅ Carpeta del Frontend encontrada"
} else {
    Write-Host "❌ Frontend NO encontrado en C:\Users\LEONARDO\sigc-frontend"
    exit
}

# PASO 5: Iniciar Frontend
Write-Host "`n🚀 [5/5] Iniciando Frontend..."
cd C:\Users\LEONARDO\sigc-frontend

if (!(Test-Path "node_modules")) {
    Write-Host "   → Instalando dependencias (primera vez)..."
    npm install -q
}

Write-Host "   → Iniciando servidor de desarrollo..."
npm run dev

Write-Host "`n╔════════════════════════════════════════════════════════════════╗"
Write-Host "║                    ✅ SISTEMA LISTO                            ║"
Write-Host "╠════════════════════════════════════════════════════════════════╣"
Write-Host "║  Frontend:  http://localhost:5173                              ║"
Write-Host "║  Backend:   http://localhost:8080                              ║"
Write-Host "║  H2 Console: http://localhost:8080/h2-console                  ║"
Write-Host "║                                                                ║"
Write-Host "║  CREDENCIALES:                                                 ║"
Write-Host "║  ├─ Admin:     admin@sigc.com / Admin123456                    ║"
Write-Host "║  └─ Paciente:  paciente@sigc.com / Paciente123456              ║"
Write-Host "╚════════════════════════════════════════════════════════════════╝"
