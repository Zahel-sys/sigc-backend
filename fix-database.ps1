# ============================================
# Script de correccion automatica de base de datos
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  CORRECCION DE BASE DE DATOS SIGC_DB" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuracion
$dbHost = "127.0.0.1"
$dbUser = "root"
# Nota: Estas variables se usan en la linea 86 del script (comando mysql)

Write-Host "Opciones disponibles:" -ForegroundColor Yellow
Write-Host "1. Correccion INCREMENTAL (mantiene datos existentes)" -ForegroundColor Green
Write-Host "2. Recrear base de datos COMPLETA (elimina todo y recrea)" -ForegroundColor Red
Write-Host ""

$opcion = Read-Host "Selecciona una opcion (1 o 2)"

if ($opcion -eq "1") {
    Write-Host ""
    Write-Host "=== OPCION 1: Correccion Incremental ===" -ForegroundColor Green
    Write-Host "Esta opcion:" -ForegroundColor Yellow
    Write-Host "  - Mantiene todos los datos existentes" -ForegroundColor White
    Write-Host "  - Corrige las foreign keys problematicas" -ForegroundColor White
    Write-Host "  - Agrega indices faltantes" -ForegroundColor White
    Write-Host ""
    
    $scriptFile = "database\fix_foreign_keys.sql"
    
} elseif ($opcion -eq "2") {
    Write-Host ""
    Write-Host "=== OPCION 2: Recrear Base de Datos ===" -ForegroundColor Red
    Write-Host "ADVERTENCIA: Esta opcion:" -ForegroundColor Red
    Write-Host "  - ELIMINARA todos los datos existentes" -ForegroundColor White
    Write-Host "  - Creara la estructura desde cero" -ForegroundColor White
    Write-Host "  - Insertara datos de prueba" -ForegroundColor White
    Write-Host ""
    
    $confirmar = Read-Host "Estas seguro? Esto eliminara TODOS los datos (SI/NO)"
    
    if ($confirmar -ne "SI") {
        Write-Host "Operacion cancelada." -ForegroundColor Yellow
        exit
    }
    
    $scriptFile = "database\sigc_db_fixed.sql"
    
} else {
    Write-Host "Opcion invalida. Saliendo..." -ForegroundColor Red
    exit
}

# Verificar que el script SQL existe
if (-not (Test-Path $scriptFile)) {
    Write-Host "Error: No se encuentra el archivo $scriptFile" -ForegroundColor Red
    exit
}

Write-Host ""
Write-Host "Intentando ejecutar script SQL..." -ForegroundColor Cyan

# Metodo 1: Intentar con mysql.exe (si esta en PATH o en XAMPP)
$mysqlPaths = @(
    "C:\xampp\mysql\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "mysql.exe"  # Si esta en PATH
)

$mysqlExe = $null
foreach ($path in $mysqlPaths) {
    if (Test-Path $path -ErrorAction SilentlyContinue) {
        $mysqlExe = $path
        break
    }
}

if ($mysqlExe) {
    Write-Host "MySQL encontrado en: $mysqlExe" -ForegroundColor Green
    Write-Host "Conectando a: $dbHost como usuario: $dbUser" -ForegroundColor Gray
    Write-Host "Ejecutando script..." -ForegroundColor Yellow
    
    try {
        # Ejecutar sin contrasena (configuracion XAMPP por defecto)
        & $mysqlExe -h $dbHost -u $dbUser -e "source $scriptFile"
        
        Write-Host ""
        Write-Host "Script ejecutado exitosamente!" -ForegroundColor Green
        
    } catch {
        Write-Host ""
        Write-Host "Error al ejecutar el script:" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        Write-Host ""
        Write-Host "Ejecuta manualmente el script en phpMyAdmin:" -ForegroundColor Yellow
        Write-Host "  1. Abre http://localhost/phpmyadmin" -ForegroundColor White
        Write-Host "  2. Selecciona la base de datos 'sigc_db'" -ForegroundColor White
        Write-Host "  3. Ve a la pestana 'SQL'" -ForegroundColor White
        Write-Host "  4. Copia y pega el contenido de: $scriptFile" -ForegroundColor White
        Write-Host "  5. Haz clic en 'Continuar'" -ForegroundColor White
        exit
    }
    
} else {
    Write-Host "MySQL CLI no encontrado en las rutas comunes." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Por favor, ejecuta el script manualmente:" -ForegroundColor Yellow
    Write-Host "  1. Abre http://localhost/phpmyadmin" -ForegroundColor White
    Write-Host "  2. Selecciona la base de datos 'sigc_db'" -ForegroundColor White
    Write-Host "  3. Ve a la pestana 'SQL'" -ForegroundColor White
    Write-Host "  4. Copia y pega el contenido de: $scriptFile" -ForegroundColor White
    Write-Host "  5. Haz clic en 'Continuar'" -ForegroundColor White
    Write-Host ""
    
    $abrirArchivo = Read-Host "Abrir el archivo SQL ahora? (S/N)"
    if ($abrirArchivo -eq "S" -or $abrirArchivo -eq "s") {
        Start-Process notepad.exe $scriptFile
    }
    
    exit
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SIGUIENTE PASO" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Ahora reinicia el backend con:" -ForegroundColor Yellow
Write-Host "  .\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "O ejecuta:" -ForegroundColor Yellow
Write-Host "  .\fix-database-and-run.ps1" -ForegroundColor White
Write-Host "  (para corregir y arrancar automaticamente)" -ForegroundColor Gray
Write-Host ""
