@echo off
REM ========================================
REM Start Database Only
REM ========================================

echo.
echo ================================
echo     Iniciando Banco PostgreSQL
echo ================================
echo.

REM Check if Docker is running
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker nao esta rodando
    echo Inicie o Docker Desktop primeiro
    pause
    exit /b 1
)

REM Check if init-data.sql exists
if not exist "docker\db\init-data.sql" (
    echo [WARN] Arquivo init-data.sql nao encontrado
    echo Execute primeiro: scripts\windows\generate-data.bat
    pause
)

echo Parando containers existentes...
docker compose -f docker\docker-compose.dev.yml down

echo.
echo Iniciando banco PostgreSQL...
docker compose -f docker\docker-compose.dev.yml up -d db

echo.
echo Aguardando banco inicializar...
timeout /t 10 /nobreak >nul

echo.
echo ================================
echo       Banco PostgreSQL ativo!
echo ================================
echo.
echo Informacoes de conexao:
echo   Host: localhost
echo   Port: 5434
echo   Database: pokedex_db
echo   Username: pokedx_user
echo   Password: pokedx_password
echo.
echo Proximo passo: scripts\windows\validate-db.bat
echo.
pause