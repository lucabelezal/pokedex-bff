# ========================================
# Pokedex BFF - PowerShell Setup Script
# ========================================

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "   Pokedex BFF - PowerShell Setup" -ForegroundColor Cyan  
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
try {
    $dockerVersion = docker --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Docker encontrado: $dockerVersion" -ForegroundColor Green
    } else {
        throw "Docker not found"
    }
} catch {
    Write-Host "[ERROR] Docker não encontrado ou não está rodando" -ForegroundColor Red
    Write-Host "Por favor instale Docker Desktop: https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Check if Docker Compose is available
try {
    $composeVersion = docker compose version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Docker Compose encontrado: $composeVersion" -ForegroundColor Green
    } else {
        throw "Docker Compose not found"
    }
} catch {
    Write-Host "[ERROR] Docker Compose não encontrado" -ForegroundColor Red
    Write-Host "Por favor atualize o Docker Desktop para versão mais recente" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Check if Python is available
try {
    $pythonVersion = python --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Python encontrado: $pythonVersion" -ForegroundColor Green
    } else {
        throw "Python not found"
    }
} catch {
    Write-Host "[ERROR] Python não encontrado" -ForegroundColor Red
    Write-Host "Por favor instale Python: https://www.python.org/downloads/" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host ""
Write-Host "Verificando dependências Python..." -ForegroundColor Yellow

# Check if psycopg2 is available
try {
    python -c "import psycopg2" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] psycopg2 encontrado" -ForegroundColor Green
    } else {
        throw "psycopg2 not found"
    }
} catch {
    Write-Host "[WARN] psycopg2 não encontrado. Instalando..." -ForegroundColor Yellow
    pip install psycopg2-binary
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] psycopg2 instalado com sucesso" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Falha ao instalar psycopg2" -ForegroundColor Red
        Read-Host "Pressione Enter para sair"
        exit 1
    }
}

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "   Todas as dependências OK!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Cyan
Write-Host "  1. .\scripts\powershell\Generate-Data.ps1" -ForegroundColor White
Write-Host "  2. .\scripts\powershell\Start-Database.ps1" -ForegroundColor White  
Write-Host "  3. .\scripts\powershell\Validate-Database.ps1" -ForegroundColor White
Write-Host ""
Read-Host "Pressione Enter para continuar"