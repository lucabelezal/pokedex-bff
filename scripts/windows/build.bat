@echo off
REM ========================================
REM Build Application
REM ========================================

echo.
echo ================================
echo     Compilando Aplicacao
echo ================================
echo.

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo [ERROR] gradlew.bat nao encontrado
    echo Certifique-se de estar na raiz do projeto
    pause
    exit /b 1
)

echo Compilando projeto...
call gradlew.bat build -x test

if %errorlevel% eq 0 (
    echo.
    echo [SUCCESS] Build concluido com sucesso!
    echo.
    echo Arquivo JAR gerado em:
    echo   build\libs\pokedex-bff-0.0.1-SNAPSHOT.jar
    echo.
    echo Para executar: java -jar build\libs\pokedex-bff-0.0.1-SNAPSHOT.jar
) else (
    echo.
    echo [ERROR] Build falhou
    echo Verifique os logs acima para detalhes
)

echo.
pause