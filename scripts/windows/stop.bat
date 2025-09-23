@echo off
REM ========================================
REM Stop All Services
REM ========================================

echo.
echo ================================
echo     Parando Todos os Servicos
echo ================================
echo.

echo Parando containers...
docker compose -f docker\docker-compose.dev.yml down

if %errorlevel% eq 0 (
    echo.
    echo [SUCCESS] Todos os servicos foram parados
) else (
    echo.
    echo [ERROR] Erro ao parar servicos
)

echo.
pause