# Princípios de Componentes

> *Como organizar componentes para evoluir, escalar e manter sistemas grandes?*

Os princípios de componentes, conforme Clean Architecture, estabelecem como agrupar e relacionar módulos em componentes (unidades implantáveis, como pacotes, módulos, etc). Eles complementam o SOLID, focando em coesão e acoplamento entre componentes.

---

## Grupos de Princípios
- **Coesão de Componentes:** Quais classes devem estar juntas? (ex: domínio, casos de uso, adapters)
- **Acoplamento de Componentes:** Como os componentes devem se relacionar? (ex: domínio nunca depende de adapters)

---

## Princípios de Coesão de Componentes

### 1. REP — Reuse/Release Equivalence Principle (Equivalência de Reúso/Release)
- **Definição:** A granularidade do reúso é a granularidade do release. Classes agrupadas em um componente devem ser liberadas juntas, com versionamento comum.
- **No projeto:** Cada camada (domain, application, adapters, infrastructure) é um componente coeso.

### 2. CCP — Common Closure Principle (Fechamento Comum)
- **Definição:** Agrupe classes que mudam pelas mesmas razões. Separe classes que mudam por motivos diferentes.
- **No projeto:** Mudanças de regra de negócio afetam apenas domain/application, mudanças técnicas afetam apenas adapters/infrastructure.

### 3. CRP — Common Reuse Principle (Reúso Comum)
- **Definição:** Quem usa um componente deve usar todas as classes que ele contém. Não dependa de coisas que você não precisa.
- **No projeto:** Application depende apenas de interfaces do domínio, adapters dependem de application/domain, nunca o contrário.

---

## Princípios de Acoplamento de Componentes

- **Acyclic Dependencies Principle:** Dependências entre componentes nunca formam ciclos.
- **Stable Dependencies Principle:** Componentes mais estáveis (domain) nunca dependem de componentes instáveis (adapters).
- **Stable Abstractions Principle:** Componentes mais estáveis devem ser mais abstratos (interfaces no domínio).

---

> Consulte os exemplos de estrutura e dependências no README.md e doc/OVERVIEW.md.
