
# Princípios de Coesão de Componentes

> *Como organizar componentes para evoluir, escalar e manter sistemas grandes?*

Componentes são as menores entidades implantáveis de um sistema — bibliotecas, módulos, pacotes ou plugins. Um componente bem projetado pode ser desenvolvido, testado e implantado de forma independente, facilitando a evolução do sistema (Martin, 2017).

---

## A Evolução dos Componentes: Da Origem ao Plug-in

Nos primórdios do software, programadores controlavam manualmente o endereço de carregamento dos programas. Bibliotecas eram incluídas como código-fonte e compiladas junto com a aplicação. Para acelerar a compilação, surgiram as bibliotecas binárias e, depois, os binários relocáveis e ligadores, permitindo dividir programas em segmentos independentes. Com o avanço do hardware, a ligação de componentes tornou-se quase instantânea, viabilizando a arquitetura de plugins. Hoje, arquivos .jar, DLLs e bibliotecas compartilhadas são rotineiramente usados como plug-ins — "os componentes do software das nossas arquiteturas" (Martin, 2017).

---

## Grupos de Princípios
- **Coesão de Componentes:** Quais classes devem estar juntas? (ex: domínio, casos de uso, adapters)
- **Acoplamento de Componentes:** Como os componentes devem se relacionar? (ex: domínio nunca depende de adapters)

---

## Coesão de Componentes – Princípios, Exemplos e Dilemas

Saber determinar a que componente uma classe pertence é uma tarefa crucial, muitas vezes ignorada ou feita ao acaso. A coesão de componentes refere-se ao grau em que partes de um sistema estão logicamente interligadas e focadas em um propósito comum. Para entender coesão, precisamos dominar três princípios:

---

### 1. REP — Reuse/Release Equivalence Principle (Equivalência de Reúso/Release)
> "A granularidade do reúso é a granularidade do release." (Martin, 2017)

**Analogia:** Pense em um kit de ferramentas. Você não compra cada chave de fenda separadamente; compra um kit com um propósito comum, que tem um nome, versão e embalagem única (o release). Se o fabricante lança uma nova versão, você sabe exatamente o que está recebendo. Seria impraticável se cada ferramenta tivesse seu próprio ciclo de lançamento.

**Exemplo de Software:** Componentes como arquivos .jar em Java, .dll em .NET ou gem em Ruby são as unidades de implantação e reúso. Ferramentas como Maven e RVM existem para gerenciar esses "kits" de software, que possuem números de versão e são lançados em conjunto.

**No projeto:** Cada camada (domain, application, adapters, infrastructure) é um componente coeso, versionável e documentado.

---

### 2. CCP — Common Closure Principle (Fechamento Comum)
> "Reúna em componentes as classes que mudam pelas mesmas razões e nos mesmos momentos. Separe em componentes diferentes as classes que mudam em momentos diferentes e por diferentes razões." (Martin, 2017)

**Analogia:** Imagine o motor de um carro. Todas as peças do motor estão agrupadas porque trabalham juntas para gerar energia. Se os engenheiros decidem melhorar a eficiência, modificam várias peças do motor, não o sistema de som ou os assentos. Manter responsabilidades separadas facilita manutenção e atualizações.

**Exemplo de Software:** Em uma aplicação financeira, regras para calcular impostos (contabilidade) e regras para relatórios de horas (RH) devem estar em componentes separados. Se compartilham código e uma mudança é feita a pedido da contabilidade, pode quebrar os relatórios do RH sem que ninguém perceba. Este é o SRP aplicado a componentes.

**No projeto:** Mudanças de regra de negócio afetam apenas domain/application; mudanças técnicas afetam adapters/infrastructure.

---

### 3. CRP — Common Reuse Principle (Reúso Comum)
> "Não force os usuários de um componente a dependerem de coisas que eles não precisam." (Martin, 2017)

**Analogia:** Pense em uma assinatura de revista. Se você só quer a revista de esportes, não deveria ser obrigado a assinar também as de culinária e notícias. Se a editora mudar a revista de culinária, você não quer ser impactado. Em software, se seu componente só precisa de uma classe de outro, ainda assim cria dependência com o componente inteiro. Mudanças em partes que você não usa podem forçá-lo a recompilar e reimplantar seu próprio trabalho.

**Exemplo de Software:** Uma classe de contêiner e seus iteradores associados são sempre usados juntos e pertencem ao mesmo componente. Por outro lado, classes não fortemente ligadas não devem estar no mesmo componente, para evitar dependências desnecessárias.

**No projeto:** Application depende apenas de interfaces do domínio; adapters dependem de application/domain, nunca o contrário.

---

## Dilemas, Armadilhas e Decisões Arquiteturais

Os princípios de coesão vivem em tensão:

![Diagrama de tensão para coesão de componentes](assets/diagrams/component-cohesion-tension-diagram.png)

- **REP e CCP** são forças inclusivas: incentivam o agrupamento de mais classes, fazendo os componentes crescerem.
- **CRP** é força exclusiva: incentiva a quebra dos componentes para evitar dependências desnecessárias.

**Trade-off entre Manutenibilidade e Reúso:**
- No início do projeto, manutenibilidade (CCP) costuma ser mais importante: agrupe o que muda junto.
- Com o amadurecimento, reúso (REP) e evitar dependências desnecessárias (CRP) ganham peso.
- A estrutura dos componentes evolui com o tempo, adaptando-se às novas demandas.

**Armadilha do Excesso de Confiança:**
> "Podemos limpar tudo depois, mas primeiro temos que lançar no mercado." Essa mentalidade leva a um código bagunçado e lento de modificar. A única maneira de ir rápido é ir bem.

---

## Exemplos de Falhas Arquiteturais por Ignorar os Princípios

- **Violação do CCP:** Uma empresa fictícia "P" adotou prematuramente uma arquitetura de três camadas distribuídas. Para adicionar um campo, era preciso modificar classes em três camadas, lidar com serialização, múltiplos protocolos e handlers. A arquitetura tornou o desenvolvimento caro e complexo, pois decisões de baixo nível contaminaram a lógica de negócio.

- **Violação do CRP:** Um sistema S depende de um framework F, que depende de um banco de dados D. Se D contém recursos não usados por F (e, portanto, por S), qualquer mudança nesses recursos pode forçar a reimplantação de F e S. Pior, uma falha nesses recursos pode causar falhas em cascata.

- **Síndrome da Manhã Seguinte:** Vários desenvolvedores modificam os mesmos arquivos. Você vai para casa com o sistema funcionando, mas no dia seguinte ele quebra porque alguém alterou algo de que você dependia. A solução é particionar o ambiente em componentes com releases e evitar ciclos de dependência (ADP).

---

## Relação Visual entre Componentes (Clean Architecture)

```
Infraestrutura --> Adaptadores --> Aplicação --> Domínio
		^             ^             ^
		|             |             |
		+-------------+-------------+

As dependências sempre apontam para dentro: camadas externas dependem das internas, nunca o contrário.
```

---

## Princípios de Acoplamento de Componentes

### 1. ADP — Acyclic Dependencies Principle (Dependências Acíclicas)
> "A estrutura de dependências entre componentes deve ser um grafo acíclico." (Martin, 2017)

Evite ciclos de dependência entre componentes. Se um ciclo surgir, quebre-o usando interfaces ou extraindo dependências comuns para um novo componente.

### 2. SDP — Stable Dependencies Principle (Dependências Estáveis)
> "Componentes estáveis não devem depender de componentes instáveis." (Martin, 2017)

Componentes centrais (ex: domínio) devem ser estáveis e raramente mudam. Componentes periféricos (adapters, infrastructure) podem ser mais voláteis. A estabilidade é medida pelo número de dependentes: quanto mais dependentes, mais estável.

### 3. SAP — Stable Abstractions Principle (Abstrações Estáveis)
> "Componentes estáveis devem ser mais abstratos, permitindo extensão sem modificação." (Martin, 2017)

Componentes estáveis devem expor interfaces e abstrações, facilitando extensão e desacoplamento. Componentes instáveis podem ser mais concretos.

---

## Conclusão

O equilíbrio entre coesão e acoplamento de componentes é dinâmico: o que é ideal hoje pode não ser amanhã. Os princípios de Martin guiam a criação de sistemas modulares, testáveis e evolutivos, reduzindo o custo de manutenção e facilitando o trabalho em equipe.

> "Ao criar ou utilizar componentes, não se deve apenas pensar em solucionar o problema técnico, e sim como sua solução vai se comportar junto com os componentes já existentes no sistema." (Martin, 2017)

---

## Referências

- MARTIN, Robert C. Clean Architecture: A Craftsman’s Guide to Software Structure and Design. 1st. ed. USA: Prentice Hall Press, 2017. ISBN 0134494164.

---

## Outras Citações Utilizadas

### Livros
* Lemos, O. (2022). *Arquitetura Limpa na Prática*. Publicação independente.
* Martin, R. C. (2019). *Arquitetura Limpa: O Guia do Artesão para Estrutura e Design de Software*. Starlin Alta Editora e Consultoria Eireli.

### Blog
* Martin, R. C. (2012–2023). *The Clean Code Blog*. Blog.

---

> Consulte exemplos de estrutura e dependências no README.md e doc/OVERVIEW.md.
