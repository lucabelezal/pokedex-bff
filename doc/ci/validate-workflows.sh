#!/bin/bash

# 🔧 Script de Validação dos Workflows GitHub Actions
# Uso: ./doc/ci/validate-workflows.sh

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
    error "Execute este script na raiz do projeto: ./doc/ci/validate-workflows.sh"
    exit 1
fi

log "Verificando estrutura dos workflows..."

# Lista de arquivos esperados
#!/bin/bash

# Compatibilidade: este script foi movido para scripts/ci/validate-workflows.sh

set -euo pipefail

REPO_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || true)
if [ -z "$REPO_ROOT" ]; then
  echo "[❌] Execute este script dentro de um repositório git." >&2
  exit 1
fi

cd "$REPO_ROOT"

NEW_SCRIPT="scripts/ci/validate-workflows.sh"
if [ ! -f "$NEW_SCRIPT" ]; then
  echo "[❌] Script não encontrado: $NEW_SCRIPT" >&2
  exit 1
fi

echo "[INFO] Redirecionando para ./$NEW_SCRIPT"
exec bash "$NEW_SCRIPT"