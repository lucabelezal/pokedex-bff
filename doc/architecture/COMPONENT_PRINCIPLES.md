
Representação visual (triângulo de tensão):

```
			             [REP] 
               Reuse/Release Equivalence
				  /               \
				 /                 \
			    /                   \
             [CCP]-----------------[CRP]
         Common Closure             Common Reuse
```

Inclusivo: REP <-> CCP
Exclusivo: CRP -> REP, CRP -> CCP
Tensão: REP <-> CRP, CCP <-> CRP

Tabela visual dos Princípios de Coesão e suas Relações:

| Sigla | Nome Completo                                 | Relações principais                |
|-------|-----------------------------------------------|------------------------------------|
| REP   | Reuse/Release Equivalence Principle           | Inclusivo com CCP, Tensão com CRP  |
| CCP   | Common Closure Principle                      | Inclusivo com REP, Tensão com CRP  |
| CRP   | Common Reuse Principle                        | Exclusivo com REP e CCP            |

Legenda das Relações:
- Inclusivo: Compartilham motivos para mudar juntos.
- Exclusivo: Separação para evitar dependências desnecessárias.
- Tensão: Dificuldade de satisfazer ambos ao mesmo tempo.


## Diagrama de Tensão dos Princípios de Coesão

Os três princípios de coesão (REP, CCP, CRP) formam um triângulo de tensão. O equilíbrio entre eles é dinâmico e depende do contexto do projeto. As bordas do triângulo representam o custo de abandonar o princípio do vértice oposto.



Legenda dos Princípios de Coesão:

- **REP**: Reuse/Release Equivalence Principle (Princípio da Equivalência de Reuso/Lançamento)
- **CCP**: Common Closure Principle (Princípio do Fechamento Comum)
- **CRP**: Common Reuse Principle (Princípio do Reuso Comum)

Relações:
- Inclusivo: REP <-> CCP
- Exclusivo: CRP -> REP, CRP -> CCP
- Tensão: REP <-> CRP, CCP <-> CRP

> O arquiteto precisa encontrar uma posição no triângulo de tensão para satisfazer as demandas atuais, sabendo que elas mudarão com o tempo (Martin, 2017).


## Relação Visual entre Componentes (Clean Architecture)

```
Infraestrutura --> Adaptadores --> Aplicação --> Domínio
		^             ^             ^
		|             |             |
		+-------------+-------------+

As dependências sempre apontam para dentro: camadas externas dependem das internas, nunca o contrário.
```

# Princípios dos Componentes: O conceito por trás do código

> *Como organizar componentes para evoluir, escalar e manter sistemas grandes?*

Segundo Robert C. Martin (Clean Architecture, 2017), componentes são as menores entidades implantáveis de um sistema — podem ser bibliotecas, módulos, pacotes ou plugins. Um componente bem projetado pode ser desenvolvido, testado e implantado de forma independente, facilitando a evolução do sistema.

## Breve História dos Componentes

Na década de 1980, componentes eram bibliotecas carregadas por ligadores. Com a evolução dos discos e compiladores, tornou-se viável ligar bibliotecas dinamicamente, surgindo a arquitetura de plugins e componentes. Isso permitiu que sistemas fossem divididos em partes menores, cada uma com seu ciclo de release e evolução (MARTIN, 2017).

---

## Grupos de Princípios
- **Coesão de Componentes:** Quais classes devem estar juntas? (ex: domínio, casos de uso, adapters)
- **Acoplamento de Componentes:** Como os componentes devem se relacionar? (ex: domínio nunca depende de adapters)

---


## Coesão de Componentes – Uma abordagem da arquitetura limpa

Saber determinar a que componente uma certa classe pertence é uma tarefa de grande importância, que requer bons conhecimentos sobre os princípios da engenharia de software (Martin, 2017). Muitas vezes, isso é ignorado ou feito ao acaso durante o desenvolvimento, com base apenas no contexto imediato.

A coesão de componentes refere-se ao grau em que partes ou módulos de um sistema estão logicamente interligados e focados em uma única função ou propósito. Para compreender a coesão de componentes, é fundamental entender os três princípios que a regem:

- **REP:** Princípio da Equivalência Reúso/Release (Reuse/Release Equivalence Principle)
- **CCP:** Princípio do Fechamento Comum (Common Closure Principle)
- **CRP:** Princípio do Reúso Comum (Common Reuse Principle)

### 1. REP — Reuse/Release Equivalence Principle (Equivalência de Reúso/Release)
> "A granularidade do reúso é a granularidade do release." (MARTIN, 2017)

Com a popularização de ferramentas como Maven, NuGet e Composer, o REP se tornou ainda mais relevante. Para reutilizar componentes, estes devem ser rastreados por um processo de release/versionamento. Sem isso, não há como garantir compatibilidade entre componentes.

Do ponto de vista de design, as classes e módulos de um componente devem pertencer a um grupo coeso, com um tema ou propósito comum. Por exemplo, em um sistema de biblioteca, o componente de gerenciamento de livros deve conter apenas classes relacionadas a livros (adicionar, buscar, atualizar), e não classes de usuários ou multas.

**No projeto:** Cada camada (domain, application, adapters, infrastructure) é um componente coeso, versionável e documentado.

### 2. CCP — Common Closure Principle (Fechamento Comum)
> "Reúna em componentes as classes que mudam pelas mesmas razões e nos mesmos momentos. Separe em componentes diferentes as classes que mudam em momentos diferentes e por diferentes razões." (MARTIN, 2017)

O CCP é uma extensão do SRP (Single Responsibility Principle) para componentes. Mudanças devem afetar o mínimo de componentes possível. Classes que mudam juntas devem estar no mesmo componente, reduzindo retrabalho e facilitando releases.

O CCP também se relaciona ao OCP (Open/Closed Principle): componentes devem ser fechados para modificações, mas abertos para extensão. Ao reunir classes fechadas para os mesmos tipos de mudanças, limitamos o impacto de modificações futuras.

**No projeto:** Mudanças de regra de negócio afetam apenas domain/application; mudanças técnicas afetam adapters/infrastructure.

### 3. CRP — Common Reuse Principle (Reúso Comum)
> "Não force os usuários de um componente a dependerem de coisas que eles não precisam." (MARTIN, 2017)

O CRP nos ajuda a decidir quais classes e módulos devem ser colocados juntos. Classes que tendem a ser reutilizadas juntas devem pertencer ao mesmo componente. Isso evita dependências desnecessárias e recompilações/redeploys em cascata.

O CRP também indica quais classes não devem ser reunidas: se um componente depende de outro, deve depender de todas as suas classes. Caso contrário, mudanças desnecessárias podem ser propagadas.

**No projeto:** Application depende apenas de interfaces do domínio; adapters dependem de application/domain, nunca o contrário.

#### Diagrama de Tensão para Coesão
Os princípios REP e CCP tendem a aumentar a quantidade de componentes, enquanto o CRP tende a diminuí-los. O equilíbrio entre eles é dinâmico e depende do contexto do projeto. Cabe ao arquiteto pesar o uso de cada princípio ao longo do tempo.

---

---

## Princípios de Acoplamento de Componentes

### 1. ADP — Acyclic Dependencies Principle (Dependências Acíclicas)
> "A estrutura de dependências entre componentes deve ser um grafo acíclico." (MARTIN, 2017)

Evite ciclos de dependência entre componentes. Se um ciclo surgir, quebre-o usando interfaces ou extraindo dependências comuns para um novo componente.

### 2. SDP — Stable Dependencies Principle (Dependências Estáveis)
> "Componentes estáveis não devem depender de componentes instáveis." (MARTIN, 2017)

Componentes centrais (ex: domínio) devem ser estáveis e raramente mudam. Componentes periféricos (adapters, infrastructure) podem ser mais voláteis. A estabilidade é medida pelo número de dependentes: quanto mais dependentes, mais estável.

### 3. SAP — Stable Abstractions Principle (Abstrações Estáveis)
> "Componentes estáveis devem ser mais abstratos, permitindo extensão sem modificação." (MARTIN, 2017)

Componentes estáveis devem expor interfaces e abstrações, facilitando extensão e desacoplamento. Componentes instáveis podem ser mais concretos.

---

## Conclusão

O equilíbrio entre coesão e acoplamento de componentes é dinâmico: o que é ideal hoje pode não ser amanhã. Os princípios de Martin guiam a criação de sistemas modulares, testáveis e evolutivos, reduzindo o custo de manutenção e facilitando o trabalho em equipe.

> "Ao criar ou utilizar componentes, não se deve apenas pensar em solucionar o problema técnico, e sim como sua solução vai se comportar junto com os componentes já existentes no sistema." (MARTIN, 2017)

---

## Referências

- MARTIN, Robert C. Clean Architecture: A Craftsman’s Guide to Software Structure and Design. 1st. ed. USA: Prentice Hall Press, 2017. ISBN 0134494164.

> Consulte exemplos de estrutura e dependências no README.md e doc/OVERVIEW.md.
