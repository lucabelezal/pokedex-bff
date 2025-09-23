@echo off
REM ========================================
REM Generate SQL Data from JSON files
REM ========================================

echo.
echo ================================
echo   Gerando dados SQL dos JSONs
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

REM Check if data directory exists
if not exist "data\json" (
    echo [ERROR] Diretorio data\json nao encontrado
    echo Certifique-se de estar na raiz do projeto
    pause
    exit /b 1
)

echo Convertendo arquivos JSON para SQL...
python scripts\json_to_sql.py

if %errorlevel% eq 0 (
    echo.
    echo [SUCCESS] Dados SQL gerados com sucesso!
    echo Arquivo criado: docker\db\init-data.sql
    echo.
    echo Proximo passo: scripts\windows\start-db.bat
) else (
    echo.
    echo [ERROR] Falha ao gerar dados SQL
    echo Verifique os arquivos JSON em data\json\
)

echo.
pause