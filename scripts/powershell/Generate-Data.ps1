# ========================================
# Generate SQL Data from JSON files
# ========================================
## Arquivo removido
Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "   Gerando dados SQL dos JSONs" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Check if Python is available
try {
    python --version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Python not found"
    }
} catch {
    Write-Host "[ERROR] Python não encontrado" -ForegroundColor Red
    Write-Host "Execute primeiro: .\scripts\powershell\Setup.ps1" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

# Check if data directory exists
if (-not (Test-Path "data\json")) {
    Write-Host "[ERROR] Diretório data\json não encontrado" -ForegroundColor Red
    Write-Host "Certifique-se de estar na raiz do projeto" -ForegroundColor Yellow
    Read-Host "Pressione Enter para sair"
    exit 1
}

Write-Host "Convertendo arquivos JSON para SQL..." -ForegroundColor Yellow
python scripts\json_to_sql.py

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[SUCCESS] Dados SQL gerados com sucesso!" -ForegroundColor Green
    Write-Host "Arquivo criado: docker\db\init-data.sql" -ForegroundColor White
    Write-Host ""
    Write-Host "Próximo passo: .\scripts\powershell\Start-Database.ps1" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "[ERROR] Falha ao gerar dados SQL" -ForegroundColor Red
    Write-Host "Verifique os arquivos JSON em data\json\" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Pressione Enter para continuar"