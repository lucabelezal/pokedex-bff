@echo off
REM ========================================
REM Run Tests
REM ========================================

echo.
echo ================================
echo       Executando Testes
echo ================================
echo.

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo [ERROR] gradlew.bat nao encontrado
    echo Certifique-se de estar na raiz do projeto
    pause
    exit /b 1
)

echo Executando testes unitarios...
call gradlew.bat test

if %errorlevel% eq 0 (
    echo.
    echo [SUCCESS] Todos os testes passaram!
    echo.
    echo Relatorio de testes disponivel em:
    echo   build\reports\tests\test\index.html
) else (
    echo.
    echo [ERROR] Alguns testes falharam
    echo Verifique os logs acima para detalhes
)

echo.
pause