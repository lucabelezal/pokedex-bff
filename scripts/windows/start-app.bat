@echo off
REM ========================================
REM Start Full Application
REM ========================================

echo.
echo ================================
echo   Iniciando Aplicacao Completa
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

echo Parando containers existentes...
docker compose -f docker\docker-compose.dev.yml down

echo.
echo Iniciando banco + aplicacao...
docker compose -f docker\docker-compose.dev.yml up -d

echo.
echo Aguardando servicos inicializarem...
timeout /t 15 /nobreak >nul

echo.
echo ================================
echo      Aplicacao iniciada!
echo ================================
echo.
echo Servicos disponiveis:
echo   API: http://localhost:8080
echo   Swagger: http://localhost:8080/swagger-ui.html
echo   Database: localhost:5434
echo.
echo Para parar: scripts\windows\stop.bat
echo Para logs: scripts\windows\logs.bat
echo.
pause