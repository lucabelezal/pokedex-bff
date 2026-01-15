# Guia de Lint e Análise Estática Kotlin

Este projeto utiliza as ferramentas **Ktlint** e **Detekt** para garantir a qualidade, padronização e segurança do código Kotlin, seguindo o padrão de mercado adotado por empresas como JetBrains, Pinterest, Netflix e Mercado Livre.

---

## O que é Ktlint?
- Ferramenta de formatação e lint para Kotlin.
- Aplica automaticamente o padrão oficial de código Kotlin.
- Corrige problemas de formatação e detecta más práticas simples.
- [Repositório oficial](https://github.com/pinterest/ktlint)

## O que é Detekt?
- Ferramenta de análise estática para Kotlin.
- Detecta code smells, problemas de design, complexidade, duplicidade, más práticas e violações de padrões.
- Altamente configurável e extensível.
- [Repositório oficial](https://github.com/detekt/detekt)

---

## Como funciona no projeto

- **Antes de cada commit:**
  - Um hook de pré-commit executa `ktlintCheck` e `detekt`. Commits com problemas são bloqueados.
- **No CI (GitHub Actions):**
  - Todo push ou pull request executa lint e análise estática automaticamente.
- **No build:**
  - Os comandos `./gradlew ktlintCheck detekt` podem ser executados manualmente a qualquer momento.
- **No Makefile:**
  - Use `make lint` para rodar lint/análise e `make lint-fix` para corrigir formatação automaticamente.

---

## Como rodar manualmente

```sh
./gradlew ktlintCheck detekt
```

---

## Como corrigir problemas automaticamente

```sh
./gradlew ktlintFormat
```

Ou simplesmente:

```sh
make lint-fix
```

---

lint:
	./gradlew ktlintCheck detekt
lint-fix:
	./gradlew ktlintFormat


---

## Configuração Detekt

O arquivo de configuração Detekt está em `config/detekt/detekt.yml` e segue as regras mais comuns do mercado, podendo ser customizado conforme a necessidade do time.

---

## Referências
- [Ktlint (Pinterest/JetBrains) - GitHub](https://github.com/pinterest/ktlint)
- [Detekt (Netflix/Mercado Livre) - GitHub](https://github.com/detekt/detekt)
- [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

---

**Dica:** Mantenha seu código sempre limpo e padronizado. Isso facilita code review, reduz bugs e acelera a evolução do projeto!

