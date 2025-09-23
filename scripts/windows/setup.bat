@echo off
REM ========================================
REM Pokedex BFF - Windows Setup Script
REM ========================================

echo.
echo ================================
echo    Pokedex BFF - Windows Setup
echo ================================
echo.

REM Check if Docker is running
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker nao encontrado ou nao esta rodando
    echo Por favor instale Docker Desktop: https://desktop.docker.com/win/main/amd64/Docker%%20Desktop%%20Installer.exe
    pause
    exit /b 1
)

REM Check if Docker Compose is available
docker compose version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose nao encontrado
    echo Por favor atualize o Docker Desktop para versao mais recente
    pause
    exit /b 1
)

REM Check if Python is available
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Python nao encontrado
    echo Por favor instale Python: https://www.python.org/downloads/
    pause
    exit /b 1
)

echo [OK] Docker encontrado
echo [OK] Docker Compose encontrado
echo [OK] Python encontrado
echo.

echo Verificando dependencias Python...
python -c "import psycopg2" >nul 2>&1
if %errorlevel% neq 0 (
    echo [WARN] psycopg2 nao encontrado. Instalando...
    pip install psycopg2-binary
    if %errorlevel% neq 0 (
        echo [ERROR] Falha ao instalar psycopg2
        pause
        exit /b 1
    )
    echo [OK] psycopg2 instalado com sucesso
) else (
    echo [OK] psycopg2 encontrado
)

echo.
echo ================================
echo   Todas as dependencias OK!
echo ================================
echo.
echo Proximos passos:
echo   1. scripts\windows\generate-data.bat
echo   2. scripts\windows\start-db.bat
echo   3. scripts\windows\validate-db.bat
echo.
pause