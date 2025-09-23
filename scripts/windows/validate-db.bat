@echo off
REM ========================================
REM Validate Database and Data
REM ========================================

echo.
echo ================================
echo    Validando Banco e Dados
echo ================================
echo.

REM Check if Python is available
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Python nao encontrado
    echo Execute primeiro: scripts\windows\setup.bat
    pause
    exit /b 1
)

REM Check if validation script exists
if not exist "scripts\check_db.py" (
    echo [ERROR] Script de validacao nao encontrado
    echo Arquivo esperado: scripts\check_db.py
    pause
    exit /b 1
)

echo Verificando conexao com banco...
python scripts\check_db.py

if %errorlevel% eq 0 (
    echo.
    echo ================================
    echo     Validacao bem-sucedida!
    echo ================================
    echo.
    echo O banco esta funcionando corretamente
    echo Todos os dados foram carregados
    echo.
    echo Proximo passo: scripts\windows\start-app.bat
) else (
    echo.
    echo ================================
    echo       Validacao falhou!
    echo ================================
    echo.
    echo Possiveis problemas:
    echo   1. Banco nao esta rodando
    echo   2. Dados nao foram carregados
    echo   3. Erro de conexao
    echo.
    echo Tente: scripts\windows\start-db.bat
)

echo.
pause