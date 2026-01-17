# 🚀 GitHub Actions CI/CD

## 📋 Visão Geral

Documentação completa dos workflows GitHub Actions do projeto Pokédex BFF, seguindo **conventional commits**, **otimização de custos** (300 min/mês) e **reaproveitamento de código**.

# 🚀 GitHub Actions CI/CD

## 📋 Visão Geral

A pipeline está separada em:

- **CI (automático)**: lint/test/build/coverage/sonar.
- **Deploy (manual)**: cria tag de release e executa o deploy.

## 🏗️ Estrutura dos Workflows

```
.github/workflows/
├── feature.yml   # 🔧 CI em PRs e branches de desenvolvimento
├── main.yml      # ✅ CI em push na main
└── deploy.yml    # 🚀 Deploy manual + criação de tag (bump automático)
```

## 🔧 feature.yml - Feature CI

### ✅ Quando executa

- `pull_request` para `main` e `develop`
- `push` em branches de desenvolvimento (ex.: `feature/**`, `fix/**`, `release/**`, `ci/**`)

### 🔄 O que roda

- `ktlintCheck` + `detekt`
- `test` + `jacocoTestReport`
- Codecov (se `CODECOV_TOKEN` existir)
- Sonar (se `SONAR_TOKEN` existir)

## ✅ main.yml - Main CI

### ✅ Quando executa

- `push` na `main`

### 🔄 O que roda

- Mesmo fluxo de CI (lint/test/build/coverage)
- Sonar habilitado apenas para evento `push` e somente se houver token

## 🚀 deploy.yml - Deploy (manual)

### ✅ Quando executa

- Somente via `workflow_dispatch` (botão “Run workflow” no GitHub Actions)

### 🎛️ Inputs

- `environment`: ambiente de deploy (ex.: `production`)
- `version`: SemVer opcional (ex.: `1.2.3`). Se informado, cria a tag `v1.2.3`.
- `bump`: se `version` estiver vazio, faz bump automático a partir do último `vX.Y.Z` (`patch`, `minor`, `major`).
  - Se não existir nenhuma tag `vX.Y.Z` ainda, o baseline é `0.0.0` (ex.: `patch` vira `0.0.1`).

## 🔒 Secrets necessários

- `CODECOV_TOKEN`
- `SONAR_TOKEN`
- `SONAR_PROJECT_KEY`
- `SONAR_ORGANIZATION`

## 🔧 Validação rápida

```bash
./scripts/ci/validate-workflows.sh
```

---

*Documentação atualizada - Janeiro 2026*