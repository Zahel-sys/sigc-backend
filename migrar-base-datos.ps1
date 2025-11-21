# ============================================
# Script de Migración de Base de Datos SIGC
# ============================================

param(
    [string]$dbHost = "localhost",
    [string]$dbPort = "3306",
    [string]$dbUser = "root",
    [string]$dbPassword = "",
    [string]$dbName = "sigc_db",
    [switch]$backup,
    [switch]$fresh
)

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  MIGRACIÓN BASE DE DATOS SIGC" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que MySQL esté disponible
Write-Host "🔍 Verificando conexión a MySQL..." -ForegroundColor Yellow
$testConnection = "mysql -h$dbHost -P$dbPort -u$dbUser"
if ($dbPassword) {
    $testConnection += " -p$dbPassword"
}
$testConnection += " -e 'SELECT 1' 2>&1"

try {
    $result = Invoke-Expression $testConnection
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo conectar a MySQL"
    }
    Write-Host "✅ Conexión exitosa a MySQL" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: No se pudo conectar a MySQL" -ForegroundColor Red
    Write-Host "   Verifica que MySQL esté corriendo y las credenciales sean correctas" -ForegroundColor Yellow
    exit 1
}

# Hacer backup si se solicita
if ($backup) {
    Write-Host ""
    Write-Host "📦 Creando backup de la base de datos..." -ForegroundColor Yellow
    $backupFile = "backup_sigc_db_$(Get-Date -Format 'yyyyMMdd_HHmmss').sql"
    
    $dumpCmd = "mysqldump -h$dbHost -P$dbPort -u$dbUser"
    if ($dbPassword) {
        $dumpCmd += " -p$dbPassword"
    }
    $dumpCmd += " $dbName > $backupFile 2>&1"
    
    Invoke-Expression $dumpCmd
    
    if (Test-Path $backupFile) {
        Write-Host "✅ Backup creado: $backupFile" -ForegroundColor Green
    } else {
        Write-Host "⚠️  No se pudo crear el backup" -ForegroundColor Yellow
    }
}

# Decidir qué script ejecutar
$scriptToRun = if ($fresh) { "crear_bd_completa.sql" } else { "migrar_bd.sql" }

if (-not (Test-Path $scriptToRun)) {
    Write-Host "❌ Error: No se encontró el archivo $scriptToRun" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🚀 Ejecutando: $scriptToRun" -ForegroundColor Yellow
Write-Host ""

# Ejecutar el script SQL
$mysqlCmd = "mysql -h$dbHost -P$dbPort -u$dbUser"
if ($dbPassword) {
    $mysqlCmd += " -p$dbPassword"
}

if ($fresh) {
    # Para crear_bd_completa.sql no especificamos la BD porque el script la crea
    $mysqlCmd += " < $scriptToRun 2>&1"
} else {
    # Para migrar_bd.sql sí especificamos la BD
    $mysqlCmd += " $dbName < $scriptToRun 2>&1"
}

$output = Invoke-Expression $mysqlCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Migración completada exitosamente" -ForegroundColor Green
    Write-Host ""
    
    # Mostrar resumen
    Write-Host "📊 Resumen de la base de datos:" -ForegroundColor Cyan
    $summaryCmd = "mysql -h$dbHost -P$dbPort -u$dbUser"
    if ($dbPassword) {
        $summaryCmd += " -p$dbPassword"
    }
    $summaryCmd += " $dbName -e 'SELECT COUNT(*) AS Usuarios FROM usuarios; SELECT COUNT(*) AS Doctores FROM doctores; SELECT COUNT(*) AS Especialidades FROM especialidades; SELECT COUNT(*) AS Horarios FROM horarios;'"
    
    Invoke-Expression $summaryCmd
} else {
    Write-Host "❌ Error durante la migración:" -ForegroundColor Red
    Write-Host $output
    exit 1
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  MIGRACIÓN COMPLETADA" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Credenciales del administrador:" -ForegroundColor Yellow
Write-Host "  Email: admin@sigc.com" -ForegroundColor White
Write-Host "  Password: Admin123456" -ForegroundColor White
Write-Host ""
