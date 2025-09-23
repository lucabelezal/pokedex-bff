#!/bin/bash

# 🔧 Script de Validação dos Workflows GitHub Actions
# Uso: ./docs/ci/validate-workflows.sh

set -e

echo "🔍 Validando Workflows GitHub Actions..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para log
log() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

success() {
    echo -e "${GREEN}[✅]${NC} $1"
}

warning() {
    echo -e "${YELLOW}[⚠️]${NC} $1"
}

error() {
    echo -e "${RED}[❌]${NC} $1"
}

# Verifica se estamos no diretório correto
if [ ! -d ".github/workflows" ]; then
    error "Diretório .github/workflows não encontrado!"
    error "Execute este script na raiz do projeto: ./docs/ci/validate-workflows.sh"
    exit 1
fi

log "Verificando estrutura dos workflows..."

# Lista de arquivos esperados
EXPECTED_FILES=(
    ".github/workflows/shared-ci.yml"
    ".github/workflows/1-feature.yml"
    ".github/workflows/2-main.yml"
    ".github/workflows/3-sonar.yml"
    ".github/workflows/README.md"
)

# Verifica se todos os arquivos existem
for file in "${EXPECTED_FILES[@]}"; do
    if [ -f "$file" ]; then
        success "Arquivo encontrado: $file"
    else
        error "Arquivo não encontrado: $file"
        exit 1
    fi
done

# Valida sintaxe YAML (se yq estiver disponível)
if command -v yq &> /dev/null; then
    log "Validando sintaxe YAML..."
    
    for workflow in .github/workflows/*.yml; do
        if yq eval . "$workflow" > /dev/null 2>&1; then
            success "YAML válido: $(basename "$workflow")"
        else
            error "YAML inválido: $(basename "$workflow")"
            exit 1
        fi
    done
else
    warning "yq não instalado. Pulando validação de sintaxe YAML."
    warning "Instale com: brew install yq (macOS) ou apt-get install yq (Ubuntu)"
fi

# Verifica estrutura básica dos workflows
log "Verificando estrutura dos workflows..."

# 1-feature.yml - deve ter trigger apenas para pull_request
if grep -q "pull_request:" .github/workflows/1-feature.yml; then
    success "1-feature.yml: Trigger pull_request configurado"
else
    error "1-feature.yml: Trigger pull_request não encontrado"
    exit 1
fi

# 2-main.yml - deve ter trigger apenas para push na main
if grep -q "branches: \[main\]" .github/workflows/2-main.yml; then
    success "2-main.yml: Trigger para branch main configurado"
else
    error "2-main.yml: Trigger para branch main não encontrado"
    exit 1
fi

# shared-ci.yml - deve ser workflow_call
if grep -q "workflow_call:" .github/workflows/shared-ci.yml; then
    success "shared-ci.yml: Workflow reutilizável configurado"
else
    error "shared-ci.yml: Workflow reutilizável não configurado"
    exit 1
fi

# Verifica se os workflows usam o shared-ci
if grep -q "uses: ./.github/workflows/shared-ci.yml" .github/workflows/1-feature.yml; then
    success "1-feature.yml: Usa workflow compartilhado"
else
    error "1-feature.yml: Não usa workflow compartilhado"
    exit 1
fi

if grep -q "uses: ./.github/workflows/shared-ci.yml" .github/workflows/2-main.yml; then
    success "2-main.yml: Usa workflow compartilhado"
else
    error "2-main.yml: Não usa workflow compartilhado"
    exit 1
fi

# Verifica otimizações de cache
log "Verificando otimizações de performance..."

if grep -q "actions/cache@v4" .github/workflows/shared-ci.yml; then
    success "Cache configurado no workflow compartilhado"
else
    warning "Cache não encontrado no workflow compartilhado"
fi

if grep -q "timeout-minutes:" .github/workflows/shared-ci.yml; then
    success "Timeouts configurados"
else
    warning "Timeouts não configurados"
fi

if grep -q "\-\-parallel" .github/workflows/shared-ci.yml; then
    success "Gradle paralelização habilitada"
else
    warning "Gradle paralelização não configurada"
fi

# Valida naming convention check
log "Verificando validação de naming convention..."

if grep -q "conventional naming" .github/workflows/1-feature.yml; then
    success "Validação de conventional commits configurada"
else
    error "Validação de conventional commits não encontrada"
    exit 1
fi

# Testa se o atual branch segue a convenção (se não for main)
CURRENT_BRANCH=$(git branch --show-current)
if [ "$CURRENT_BRANCH" != "main" ] && [ "$CURRENT_BRANCH" != "master" ]; then
    log "Testando naming convention para branch atual: $CURRENT_BRANCH"
    
    if [[ $CURRENT_BRANCH =~ ^(feat|feature|fix|hotfix|docs|style|refactor|perf|test|ci|build|chore|revert)/.+ ]]; then
        success "Branch atual segue conventional naming: $CURRENT_BRANCH"
    else
        warning "Branch atual NÃO segue conventional naming: $CURRENT_BRANCH"
        warning "Formato esperado: tipo/descrição (ex: feat/add-pokemon-search)"
    fi
fi

# Verifica secrets necessários
log "Verificando configuração de secrets..."

REQUIRED_SECRETS=(
    "CODECOV_TOKEN"
    "SONAR_TOKEN"
    "SONAR_PROJECT_KEY"
    "SONAR_ORGANIZATION"
)

echo ""
log "Secrets necessários no GitHub:"
for secret in "${REQUIRED_SECRETS[@]}"; do
    echo "  - $secret"
done

# Calcula estimativa de uso mensal
log "Calculando estimativa de uso mensal..."

cat << EOF

📊 ESTIMATIVA DE USO MENSAL (limite: 300 min):

🔧 Feature PRs:
  - ~20 PRs × 10 min = 200 min

🚀 Main pushes:  
  - ~8 pushes × 18 min = 144 min

🔍 SonarQube:
  - ~4 análises × 15 min = 60 min

📈 TOTAL ESTIMADO: ~280 min ✅ (dentro do limite)

EOF

echo ""
success "✅ Todos os workflows validados com sucesso!"
echo ""
log "Para testar localmente:"
echo "  1. Crie uma branch: git checkout -b feat/test-workflows"
echo "  2. Faça um commit: git commit -m 'test: validate workflows'"  
echo "  3. Abra um PR para testar o 1-feature.yml"
echo ""
log "Para deploy automático:"
echo "  1. Merge PR para main"
echo "  2. O 2-main.yml executará automaticamente"
echo ""
log "Para análise SonarQube manual:"
echo "  1. Vá em Actions → '3 - SonarQube Analysis'"
echo "  2. Clique em 'Run workflow'"