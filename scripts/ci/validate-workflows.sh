#!/bin/bash

# 🔧 Script de Validação dos Workflows GitHub Actions
# Uso: ./scripts/ci/validate-workflows.sh

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
    error "Execute este script na raiz do projeto: ./scripts/ci/validate-workflows.sh"
    exit 1
fi

log "Verificando estrutura dos workflows..."

# Lista de arquivos esperados
EXPECTED_FILES=(
    ".github/workflows/feature.yml"
    ".github/workflows/main.yml"
    ".github/workflows/deploy.yml"
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

# feature.yml - deve ter pull_request (e normalmente push para branches de desenvolvimento)
if grep -q "pull_request:" .github/workflows/feature.yml; then
    success "feature.yml: Trigger pull_request configurado"
else
    error "feature.yml: Trigger pull_request não encontrado"
    exit 1
fi

if grep -q "push:" .github/workflows/feature.yml; then
    success "feature.yml: Trigger push configurado"
else
    warning "feature.yml: Trigger push não encontrado (ok se for intencional)"
fi

# main.yml - deve ter trigger apenas para push na main
if grep -q "branches: \[main\]" .github/workflows/main.yml; then
    success "main.yml: Trigger para branch main configurado"
else
    error "main.yml: Trigger para branch main não encontrado"
    exit 1
fi

# deploy.yml - deve ser workflow_dispatch
if grep -q "workflow_dispatch:" .github/workflows/deploy.yml; then
    success "deploy.yml: Trigger workflow_dispatch configurado"
else
    error "deploy.yml: Trigger workflow_dispatch não encontrado"
    exit 1
fi

# Verifica otimizações de cache
log "Verificando otimizações de performance..."

for workflow in .github/workflows/feature.yml .github/workflows/main.yml .github/workflows/deploy.yml; do
    if grep -q "gradle/actions/setup-gradle@v3" "$workflow"; then
        success "$(basename "$workflow"): setup-gradle com cache configurado"
    else
        warning "$(basename "$workflow"): setup-gradle não encontrado"
    fi

    if grep -q "timeout-minutes:" "$workflow"; then
        success "$(basename "$workflow"): timeouts configurados"
    else
        warning "$(basename "$workflow"): timeout-minutes não encontrado"
    fi

    if grep -q "\-\-parallel" "$workflow"; then
        success "$(basename "$workflow"): Gradle paralelização habilitada"
    else
        warning "$(basename "$workflow"): flag --parallel não encontrada"
    fi
done

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

echo ""
success "✅ Todos os workflows validados com sucesso!"
echo ""
log "Para testar localmente:"
echo "  1. Crie uma branch: git checkout -b feat/test-workflows"
echo "  2. Faça um commit e abra um PR para testar o feature.yml"
echo ""
log "Para deploy manual:"
echo "  1. Vá em Actions → 'Deploy'"
echo "  2. Clique em 'Run workflow' e escolha environment/version/bump"
