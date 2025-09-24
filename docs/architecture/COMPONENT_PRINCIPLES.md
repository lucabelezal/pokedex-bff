# Princípios de Componentes

> *Como organizar componentes para evoluir, escalar e manter sistemas grandes?*

Os princípios de componentes, abordados na Parte IV do livro Arquitetura Limpa, estabelecem como agrupar e relacionar módulos em componentes (unidades implantáveis, como JARs, DLLs, pacotes, etc). Eles complementam o SOLID, focando em coesão e acoplamento entre componentes.

---

## Grupos de Princípios
- **Coesão de Componentes:** Quais classes devem estar juntas?
- **Acoplamento de Componentes:** Como os componentes devem se relacionar?

---

## Princípios de Coesão de Componentes

### 1. REP — Reuse/Release Equivalence Principle (Equivalência de Reúso/Release)
- **Definição:** A granularidade do reúso é a granularidade do release. Classes agrupadas em um componente devem ser liberadas juntas, com versionamento e rastreamento comum.
- **Visual:** Imagine um pacote versionado: tudo que está dentro dele é liberado junto, facilitando controle e reúso.

```
┌───────────────┐
│ Componente A  │
│ ───────────── │
│ Classe1       │
│ Classe2       │
│ Classe3       │
└───────────────┘
	↑ Release v1.2 (tudo junto)
```

### 2. CCP — Common Closure Principle (Fechamento Comum)
- **Definição:** Agrupe classes que mudam pelas mesmas razões e nos mesmos momentos. Separe classes que mudam por motivos diferentes.
- **Relação:** É o SRP aplicado ao nível de componentes.
- **Visual:** Componentes bem fechados mudam pouco e juntos, reduzindo impacto de mudanças.

```
┌───────────────┐      ┌───────────────┐
│ Componente X  │      │ Componente Y  │
│ ───────────── │      │ ───────────── │
│ ClasseA       │      │ ClasseB       │
│ ClasseB       │      │ ClasseC       │
└───────────────┘      └───────────────┘
	↑ Mudam juntos         ↑ Mudam juntos
```

### 3. CRP — Common Reuse Principle (Reúso Comum)
- **Definição:** Quem usa um componente deve usar todas as classes que ele contém. Não dependa de coisas que você não precisa.
- **Slogan:** "Não dependa de coisas que você não precisa."
- **Visual:** Evite dependências desnecessárias entre pacotes.

```
┌───────────────┐
│ Componente Z  │
│ ───────────── │
│ ClasseX       │
│ ClasseY       │
└───────────────┘
↑
Usuário depende de tudo que está no componente
```

> **Nota:** REP e CCP tendem a aumentar o número de componentes (inclusivos), enquanto CRP tende a diminuir (excludente). O equilíbrio entre eles é visualizado como um "diagrama de tensão".

---

## Princípios de Acoplamento de Componentes

### 1. SAP — Stable Abstractions Principle (Abstrações Estáveis)
- **Definição:** Um componente deve ser tão abstrato quanto estável. Componentes estáveis (difíceis de mudar) devem ser altamente abstratos, permitindo extensão sem modificação.
- **Visual:** Componentes "no centro" do sistema são estáveis e abstratos, servindo de base para outros.

```
	  (UI)
	    │
	    ▼
┌───────────────┐
│  Aplicação    │
└───────────────┘
	│
	▼
┌───────────────┐
│ Domínio (estável e abstrato)
└───────────────┘
```

### 2. SDP — Stable Dependencies Principle (Dependência Estável)
- **Definição:** As dependências entre componentes devem apontar para componentes mais estáveis.
- **Visual:** As setas de dependência sempre apontam para componentes mais estáveis.

```
┌───────────────┐    ┌───────────────┐
│  UI           │    │  Infra        │
└───────────────┘    └───────────────┘
		│                 │
		▼                 ▼
	┌───────────────┐
	│  Domínio      │
	└───────────────┘
// Setas apontam para o mais estável
```

### 3. ADP — Acyclic Dependencies Principle (Dependências Acíclicas)
- **Definição:** O grafo de dependências entre componentes deve ser acíclico. Ciclos dificultam manutenção e evolução.
- **Visual:** O grafo de dependências deve ser "limpo", sem loops.

```
// Correto (acíclico):
Componente A → Componente B → Componente C

// Incorreto (cíclico):
Componente A → Componente B → Componente C
	↑                        ↓
	└───────────────┘
```

---

## Visualização Geral


```
┌───────────────┐    ┌───────────────┐
│ Componente 1  │───▶│ Componente 2  │
└───────────────┘    └───────────────┘
		│                  │
		▼                  ▼
	┌───────────────┐
	│ Componente 3  │
	└───────────────┘
```

Imagine os componentes como "ilhas" conectadas por setas (dependências). As setas devem sempre apontar para componentes mais estáveis e abstratos, evitando ciclos. O equilíbrio entre coesão e acoplamento é o segredo para sistemas grandes e flexíveis.

---

## Próximos Passos

Em breve, este guia será aprofundado com exemplos visuais, diagramas e estudos de caso práticos!

[Voltar para o SOLID](./SOLID.md)
