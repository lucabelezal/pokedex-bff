# 🚀 GitHub Actions Workflows

Para documentação completa dos workflows CI/CD, consulte:

📖 **[Documentação CI/CD](../../doc/ci/README.md)**

## ✅ Workflows Ativos

- **feature.yml**: Feature CI — acionado em Pull Requests; roda lint, testes, build e Sonar.
- **main.yml**: Main CI/CD — acionado em push na `main` e também disponível como `workflow_dispatch`; roda testes, Sonar e deployment.
- **lint.yml**: Lint — checagens de ktlint/detekt (roda em PRs e em pushes para branches de desenvolvimento conforme política do time).

## 🔐 Variáveis e Secrets

- **Secrets**: `SONAR_TOKEN`
- **Variables (vars)**: `SONAR_PROJECT_KEY`, `SONAR_ORGANIZATION`, `CODECOV_TOKEN`

## 📋 Links Rápidos

- **[Guia de Uso](../../doc/ci/README.md)**: Documentação completa dos workflows
- **[Script de Validação](../../doc/ci/validate-workflows.sh)**: Ferramenta de validação

## 🔧 Validação Rápida

```bash
# Executar validação dos workflows
./doc/ci/validate-workflows.sh
```

---

*Para mais detalhes, consulte a documentação completa em `doc/ci/`*