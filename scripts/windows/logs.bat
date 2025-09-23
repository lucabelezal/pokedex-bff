@echo off
REM ========================================
REM Show Application Logs
REM ========================================

echo.
echo ================================
echo        Logs da Aplicacao
echo ================================
echo.
echo Pressione Ctrl+C para sair dos logs
echo.

docker compose -f docker\docker-compose.dev.yml logs -f