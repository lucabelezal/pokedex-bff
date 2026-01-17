# 🚀 GitHub Actions Workflows

Para documentação completa dos workflows CI/CD, consulte:

📖 **[Documentação CI/CD](../../doc/ci/README.md)**

## ✅ Workflows Ativos

- **feature.yml**: Feature CI — acionado em Pull Requests e pushes em branches de desenvolvimento; roda lint, testes, build e Sonar.
- **main.yml**: Main CI — acionado em push na `main`; roda lint, testes, build, Codecov e Sonar.
- **deploy.yml**: Deploy — acionado manualmente (`workflow_dispatch`); cria tag `vX.Y.Z` (bump automático ou versão informada) e executa o deploy.

## 🔐 Variáveis e Secrets

- **Secrets**: `SONAR_TOKEN`, `SONAR_PROJECT_KEY`, `SONAR_ORGANIZATION`, `CODECOV_TOKEN`

## 📋 Links Rápidos

- **[Guia de Uso](../../doc/ci/README.md)**: Documentação completa dos workflows
- **[Script de Validação](../../scripts/ci/validate-workflows.sh)**: Ferramenta de validação

## 🔧 Validação Rápida

```bash
# Executar validação dos workflows
./scripts/ci/validate-workflows.sh
```

---

*Para mais detalhes, consulte a documentação completa em `doc/ci/`*