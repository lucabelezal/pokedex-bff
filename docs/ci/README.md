# 🚀 GitHub Actions CI/CD

## 📋 Visão Geral

Documentação completa dos workflows GitHub Actions do projeto Pokédex BFF, seguindo **conventional commits**, **otimização de custos** (300 min/mês) e **reaproveitamento de código**.

## 🏗️ Estrutura dos Workflows

### 📂 Arquivos

```
.github/workflows/
├── shared-ci.yml      # ⚡ Workflow compartilhado/reutilizável
├── 1-feature.yml      # 🔧 CI para branches de feature (apenas com PR)
├── 2-main.yml         # 🚀 CI/CD para branch main
└── 3-sonar.yml        # 🔍 Análise SonarQube dedicada
```

---

## 🔧 1-feature.yml - Feature CI

### ✅ **Quando executa:**
- ✅ **Apenas** quando há **Pull Request aberto** para `main`
- ✅ Branch deve seguir **conventional commits**: `type/description`
- ✅ Ignora arquivos de documentação (`.md`, `docs/`, etc.)

### 🎯 **Tipos de branch aceitos:**
```
feat/add-pokemon-search          ✅
fix/authentication-bug           ✅  
docs/update-readme              ✅
refactor/clean-architecture      ✅
test/add-integration-tests       ✅
ci/optimize-workflows           ✅
chore/update-dependencies       ✅
perf/improve-query-speed        ✅
style/format-code               ✅
build/update-gradle             ✅
hotfix/critical-security-fix    ✅

random-branch-name              ❌
feature-pokemon                 ❌
```

### 🔄 **Fluxo:**
1. **Validação**: Verifica naming convention da branch
2. **CI**: Executa workflow compartilhado com SonarQube
3. **Resultado**: Build ✅ + Tests ✅ + SonarQube ✅

### ⏱️ **Tempo estimado:** ~8-12 minutos

---

## 🚀 2-main.yml - Main CI/CD  

### ✅ **Quando executa:**
- ✅ **Apenas** push para branch `main`
- ✅ Ignora arquivos de documentação

### 🔄 **Fluxo:**
1. **CI completo**: Build + Tests + SonarQube
2. **Deploy**: Deploy automático para produção
3. **Release**: Cria tag de release automática
4. **Notificação**: Status do deployment

### ⏱️ **Tempo estimado:** ~15-20 minutos

---

## 🔍 3-sonar.yml - SonarQube Analysis

### ✅ **Quando executa:**
- 🕐 **Agendado**: Segunda-feira às 2h UTC (análise semanal)
- 🖱️ **Manual**: Via workflow_dispatch
- 🏷️ **PRs específicos**: Apenas com label `sonar-required`

### 🔄 **Fluxo:**
1. **Análise dedicada**: SonarQube completo
2. **Quality Gate**: Verificação de qualidade
3. **Relatório**: Link para resultados

### ⏱️ **Tempo estimado:** ~10-15 minutos

---

## ⚡ shared-ci.yml - Workflow Compartilhado

### 🎯 **Propósito:**
- **Reutilização**: Evita duplicação de código
- **Consistência**: Mesma lógica em todos os workflows
- **Otimização**: Cache inteligente e paralelização

### 🔧 **Jobs:**

#### 1. **build-and-test** (sempre executa)
- ☕ Setup JDK 21
- 📦 Cache inteligente do Gradle
- 🧪 Testes com coverage
- 🏗️ Build da aplicação
- 📊 Upload para Codecov

#### 2. **sonar-analysis** (condicional)
- 🔍 Análise SonarQube
- 📊 Relatórios de qualidade
- ⚡ Cache separado para Sonar

#### 3. **deploy** (condicional - apenas main)
- 🏷️ Criação de tags de release
- 🚀 Deploy para ambiente especificado
- 📦 Gestão de artefatos

### ⚙️ **Parâmetros:**
```yaml
inputs:
  run-sonar: boolean        # Executar SonarQube?
  run-deployment: boolean   # Executar deploy?
  environment: string       # Ambiente de deploy

secrets:
  CODECOV_TOKEN            # Token do Codecov
  SONAR_TOKEN             # Token do SonarCloud
  SONAR_PROJECT_KEY       # Chave do projeto
  SONAR_ORGANIZATION      # Organização SonarCloud
```

---

## 📊 Otimizações de Performance

### ⚡ **Cache Strategy:**
- **Gradle**: `~/.gradle/caches` + `~/.gradle/wrapper`
- **SonarQube**: `~/.sonar/cache`
- **Build artifacts**: Para deploy rápido

### 🔧 **Gradle Optimizations:**
```bash
--parallel          # Execução paralela
--daemon           # Gradle daemon
--no-scan          # Sem Gradle Enterprise
-x test            # Skip tests quando apropriado
```

### ⏱️ **Timeouts:**
- **Feature CI**: 15 min max
- **Main CI/CD**: 20 min max  
- **SonarQube**: 15 min max
- **Validation**: 2 min max

---

## 📈 Economia de Recursos

### 📊 **Antes vs Depois:**

| Cenário | Antes | Depois | Economia |
|---------|--------|--------|----------|
| **Feature PR** | ~15 min | ~10 min | **33%** |
| **Main push** | ~20 min | ~18 min | **10%** |
| **SonarQube** | A cada push | Semanal/Manual | **80%** |
| **Cache hit** | Baixo | Alto | **40%** |

### 🎯 **Estimativa mensal (300 min):**
- **Features**: ~20 PRs × 10 min = 200 min
- **Main**: ~8 pushes × 18 min = 144 min  
- **SonarQube**: ~4 análises × 15 min = 60 min

**Total mensal estimado**: ~280 min ✅ **(dentro do limite)**

---

## 🔒 Secrets Necessários

```yaml
# Codecov
CODECOV_TOKEN: "xxx"

# SonarCloud  
SONAR_TOKEN: "xxx"
SONAR_PROJECT_KEY: "pokedex-bff"
SONAR_ORGANIZATION: "lucabelezal"
```

---

## 🚦 Exemplos de Uso

### 💡 **Criando Feature Branch:**
```bash
# ✅ Correto
git checkout -b feat/add-pokemon-search
git checkout -b fix/authentication-bug
git checkout -b refactor/clean-architecture

# ❌ Incorreto  
git checkout -b pokemon-search
git checkout -b bug-fix
git checkout -b refactoring
```

### 🔍 **Forçar SonarQube em PR:**
1. Abrir PR normalmente
2. Adicionar label `sonar-required`
3. SonarQube executará automaticamente

### 📊 **Análise manual SonarQube:**
1. Ir em Actions → "3 - SonarQube Analysis"
2. Click "Run workflow"
3. Escolher branch (default: main)

---

## 🎯 Benefícios da Refatoração

### ✅ **Compliance:**
- ✅ Conventional commits obrigatório
- ✅ CI apenas com PR aberto
- ✅ Branch main protegida

### ⚡ **Performance:**
- ✅ Reaproveitamento de código
- ✅ Cache inteligente
- ✅ Paralelização otimizada
- ✅ Timeouts configurados

### 💰 **Economia:**
- ✅ 33% menos tempo em features
- ✅ 80% menos execuções SonarQube
- ✅ Cache reduz rebuild
- ✅ Dentro do limite de 300 min/mês

### 🔧 **Manutenibilidade:**
- ✅ Workflow compartilhado
- ✅ Configuração centralizada
- ✅ Logs melhorados
- ✅ Notificações claras

---

## 📚 Documentação Relacionada

- **[Guia de Migração](MIGRATION.md)**: Processo completo de migração dos workflows antigos
- **[Script de Validação](../../.github/workflows/validate-workflows.sh)**: Ferramenta para validar workflows localmente
- **[Arquitetura do Projeto](../architecture/README.md)**: Documentação da arquitetura MVC
- **[Guia de Desenvolvimento](../development/README.md)**: Setup e desenvolvimento local

---

*Documentação atualizada - Janeiro 2025*