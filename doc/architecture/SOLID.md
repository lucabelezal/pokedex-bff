
# Guia Prático: SOLID em Kotlin

> *Por que dominar SOLID é essencial para microserviços modernos?*

Os princípios SOLID são a espinha dorsal de sistemas robustos, evolutivos e fáceis de manter. Eles não são só "boas práticas": são fundamentos para desacoplamento, testabilidade e crescimento sustentável do seu backend. Aqui você encontra explicações aprofundadas, exemplos reais (com comentários em português) e padrões de projeto que ajudam a aplicar cada princípio no seu microserviço Kotlin.

**Nota:** Este projeto utiliza uma **arquitetura em camadas pragmática**, não uma Clean Architecture pura. O domínio contém anotações JPA para simplificar o desenvolvimento, mas ainda assim seguimos os princípios SOLID para manter o código desacoplado e testável.

---

## Sumário
- [SRP — Responsabilidade Única](#srp)
- [OCP — Aberto/Fechado](#ocp)
- [LSP — Substituição de Liskov](#lsp)
- [ISP — Segregação de Interface](#isp)
- [DIP — Inversão de Dependência](#dip)

---


## <a name="srp"></a>SRP — Single Responsibility Principle (Responsabilidade Única)

**Definição:** Uma classe deve ter apenas um motivo para mudar, ou seja, ser responsável por apenas um ator ou contexto.

### Por que é importante?
Misturar responsabilidades diferentes na mesma classe gera acoplamento acidental, bugs difíceis de rastrear e dificulta a manutenção. O SRP é a base para código coeso e fácil de evoluir.

### Violação (Exemplo realista):
```kotlin
// Violação: classe faz persistência, serialização e integração
class PokemonService {
    fun save(pokemon: Pokemon) { /* salva no banco */ }
    fun toJson(pokemon: Pokemon): String { /* serializa para JSON */ }
    fun sendToQueue(pokemon: Pokemon) { /* envia para fila */ }
}
```
// Problema: cada método atende um "ator" diferente (DB, API, mensageria)

### Como corrigir (Aplicando SRP):
```kotlin
// Cada classe tem uma responsabilidade clara
class PokemonRepository {
    fun save(pokemon: Pokemon) { /* ... */ }
}

class PokemonSerializer {
    fun toJson(pokemon: Pokemon): String { /* ... */ }
}

class PokemonQueueSender {
    fun send(pokemon: Pokemon) { /* ... */ }
}
```
// Agora, mudanças em persistência não afetam serialização ou mensageria.

**Definição:** Uma classe, módulo ou função deve ter apenas um motivo para mudar, ou seja, uma única responsabilidade — normalmente associada a um único ator (grupo de interesse).

### SRP e a Lei de Conway

O SRP é um corolário ativo da Lei de Conway: a estrutura do software tende a refletir a estrutura social da organização. Ou seja, cada módulo deve ser responsável por um único ator ou grupo de interesse, facilitando a comunicação e a manutenção.

### Coesão e Organização de Pacotes

Coesão é a força que mantém o código responsável por um único ator unido. O SRP orienta como organizar classes em pacotes e módulos, promovendo alta coesão e baixo acoplamento.

### Sintomas de Violação do SRP

A melhor forma de entender o SRP é observar os sintomas de sua violação:
- **Duplicação acidental:** Quando diferentes atores solicitam mudanças em funcionalidades distintas de uma mesma classe, pode ocorrer duplicação de lógica ou conflitos.
- **Fusões e conflitos:** Múltiplos desenvolvedores alterando a mesma classe por motivos diferentes aumentam o risco de conflitos e bugs.

#### Exemplo clássico: Employee

```kotlin
// Violação do SRP: múltiplos motivos para mudar (CFO, COO, CTO)
class Employee {
    fun calculatePay() { /* lógica de pagamento (CFO) */ }
    fun reportHours() { /* lógica de RH (COO) */ }
    fun save() { /* lógica de persistência (CTO) */ }
}
```

Se o departamento de contabilidade (CFO) pedir uma mudança em `calculatePay`, pode afetar acidentalmente o RH ou a persistência. Fusões de código e bugs são comuns.

#### Como corrigir

Separe as responsabilidades em classes distintas, cada uma coesa e voltada para um único ator:

```kotlin
class EmployeeData(val nome: String, val salario: Double, val horas: Int)

class Payroll {
    fun calculatePay(data: EmployeeData) { /* ... */ }
}

class HR {
    fun reportHours(data: EmployeeData) { /* ... */ }
}

class EmployeeRepository {
    fun save(data: EmployeeData) { /* ... */ }
}
```

Se necessário, use um padrão Facade para orquestrar as operações:

```kotlin
class EmployeeFacade(
    private val payroll: Payroll,
    private val hr: HR,
    private val repository: EmployeeRepository
) {
    fun processEmployee(data: EmployeeData) {
        payroll.calculatePay(data)
        hr.reportHours(data)
        repository.save(data)
    }
}
```

### Resumo

O SRP facilita manutenção, testes e evolução do sistema, pois mudanças em uma responsabilidade não afetam outras. Ele também reduz conflitos de equipe e aumenta a clareza do código.

**Padrões que ajudam:**
- [Facade](https://refactoring.guru/pt-br/design-patterns/facade), [Adapter](https://refactoring.guru/pt-br/design-patterns/adapter), [Template Method](https://refactoring.guru/pt-br/design-patterns/template-method)
**Padrão que ajuda:** *Facade* (para orquestrar várias responsabilidades sem misturá-las)

---


## <a name="ocp"></a>OCP — Open/Closed Principle (Aberto/Fechado)


**Definição:** Uma entidade de software (classe, módulo, função, etc.) deve estar aberta para extensão, mas fechada para modificação.

### O que significa "aberto para extensão, fechado para modificação"?

O OCP é frequentemente mal compreendido porque parece paradoxal: como posso permitir mudanças sem modificar o código? A resposta está nas abstrações: você projeta seu sistema para que novos comportamentos possam ser adicionados (extensão) sem alterar o código já existente (modificação).

#### Abstrações como pontos de articulação
As abstrações (interfaces, classes abstratas, funções de ordem superior) são os "pontos de articulação" do projeto — locais onde o sistema pode ser "dobrado" ou estendido sem quebrar o que já existe. Coisas concretas mudam muito, mas as abstrações mudam com muito menos frequência.

### Consequências de violar o OCP

Quando o OCP não é seguido:
- Cada nova regra de negócio exige modificar código já existente, aumentando o risco de bugs e regressões.
- O código vira um grande bloco de condicionais (if/else, when), difícil de entender e manter.
- Refatorações se tornam arriscadas, pois qualquer mudança pode quebrar funcionalidades antigas.
- O sistema escala mal: pequenas mudanças exigem grandes revisões.

#### Exemplo prático de violação
```kotlin
// Violação: precisa alterar a classe para cada novo formato
class PokemonFormatter {
    fun format(pokemon: Pokemon, type: String): String = when(type) {
        "json" -> toJson(pokemon)
        "xml" -> toXml(pokemon)
        else -> throw IllegalArgumentException()
    }
    // ...implementações
}
```
// Problema: para cada novo formato, altera a classe e arrisca bugs.

#### Como corrigir (OCP + Strategy)
Use abstrações para permitir extensão sem modificar a classe existente:
```kotlin
interface Formatter {
    fun format(pokemon: Pokemon): String
}

class JsonFormatter : Formatter {
    override fun format(pokemon: Pokemon) = "{...}"
}

class XmlFormatter : Formatter {
    override fun format(pokemon: Pokemon) = "<pokemon>...</pokemon>"
}

class PokemonFormatter(private val formatter: Formatter) {
    fun format(pokemon: Pokemon) = formatter.format(pokemon)
}
```
// Para novo formato, só cria uma nova implementação de Formatter.

### OCP e evolução do negócio

O OCP é fundamental para sistemas que precisam evoluir com o tempo. Se o código não for "aberto para extensão", cada nova demanda do negócio exigirá mexer em código antigo, aumentando o risco de bugs e tornando a evolução lenta e custosa.

**Resumo:**
- O OCP facilita a evolução do sistema, pois novas funcionalidades podem ser adicionadas sem alterar código já existente, reduzindo riscos de bugs e regressões.
- Abstrações bem definidas são o segredo para aplicar o OCP.
- O uso de padrões de projeto como Strategy, Template Method e Decorator ajuda a criar pontos de extensão seguros.

**Padrões que ajudam:**
- [Strategy](https://refactoring.guru/pt-br/design-patterns/strategy), [Template Method](https://refactoring.guru/pt-br/design-patterns/template-method), [Decorator](https://refactoring.guru/pt-br/design-patterns/decorator)

---


## <a name="lsp"></a>LSP — Liskov Substitution Principle (Substituição de Liskov)

**Definição:**
> "Se S é um subtipo de T, então objetos do tipo T podem ser substituídos por objetos do tipo S sem alterar as propriedades desejáveis do programa." — Barbara Liskov

O LSP vai muito além de evitar exceções em subclasses. Ele trata da coesão, semântica e contratos do seu modelo. O verdadeiro desafio é garantir que a hierarquia de tipos faça sentido não só sintaticamente, mas também conceitualmente.

### Profundidade do LSP: Semântica e Modelagem

O exemplo clássico do quadrado/retângulo mostra que, mesmo que `Quadrado` seja um tipo especial de `Retângulo` matematicamente, na modelagem OO isso pode ser um erro:

```kotlin
open class Retangulo(var largura: Int, var altura: Int) {
    open fun setLargura(l: Int) { largura = l }
    open fun setAltura(a: Int) { altura = a }
    fun area() = largura * altura
}

class Quadrado(tamanho: Int) : Retangulo(tamanho, tamanho) {
    override fun setLargura(l: Int) {
        largura = l
        altura = l
    }
    override fun setAltura(a: Int) {
        largura = a
        altura = a
    }
}

fun main() {
    val r: Retangulo = Quadrado(5)
    r.setLargura(10)
    r.setAltura(2)
    println(r.area()) // Esperado: 20, mas retorna 4!
}
```

O problema: o contrato de `Retangulo` permite alterar largura e altura independentemente, mas `Quadrado` quebra essa expectativa. Isso gera bugs sutis e obriga o código cliente a tratar casos especiais, violando o LSP.

### LSP no domínio Pokémon: Magikarp deve herdar de Pokémon?

No seu domínio, pense: Magikarp deveria herdar de Pokémon se ele não compartilha o mesmo contrato comportamental? Se Magikarp não pode atacar, mas a interface de Pokémon exige esse método, forçar a herança pode gerar violações do LSP:

```kotlin
open class Pokemon {
    open fun atacar() {
        println("Ataque padrão!")
    }
}

class Magikarp : Pokemon() {
    override fun atacar() {
        throw UnsupportedOperationException("Magikarp não ataca!")
    }
}

fun atacarPokemon(pokemon: Pokemon) {
    pokemon.atacar() // Pode lançar exceção!
}
```

**Como evitar?**
- Repense a modelagem: talvez nem todo Pokémon deva implementar `atacar()`, ou talvez o comportamento "atacante" deva ser uma interface separada (`Atacante`).
- Prefira composição a herança quando o comportamento não é compartilhado por todos os tipos.

```kotlin
interface Atacante {
    fun atacar()
}

class Magikarp
class Pikachu : Atacante {
    override fun atacar() {
        println("Pikachu usou Choque do Trovão!")
    }
}

fun atacarPokemon(atacante: Atacante) {
    atacante.atacar()
}

fun main() {
    atacarPokemon(Pikachu()) // OK
    // atacarPokemon(Magikarp()) // Não compila, pois Magikarp não é Atacante
}
```

### LSP e Arquitetura

O LSP não vale só para herança, mas para qualquer contrato (interfaces, APIs, serviços). Se você quebra o contrato esperado, força o cliente a tratar casos especiais, polui a arquitetura e cria pontos de acoplamento e bugs difíceis de rastrear.

**Resumo:**
- O LSP exige que subclasses (ou implementações) realmente possam ser usadas no lugar das superclasses (ou contratos) sem alterar o comportamento esperado do sistema.
- Isso envolve respeitar contratos, invariantes, semântica do domínio e garantir coesão.
- Modelagem errada pode gerar violações profundas e difíceis de detectar.

**Padrões que ajudam a respeitar o LSP:**
- [Strategy](https://refactoring.guru/pt-br/design-patterns/strategy), [State](https://refactoring.guru/pt-br/design-patterns/state), [Composition over Inheritance](https://refactoring.guru/pt-br/design-patterns/composite)

**Leitura complementar:**
- [Refactoring Guru — LSP](https://refactoring.guru/pt-br/design-patterns/principles/liskov-substitution-principle)
- [Refactoring Guru — Todos os Design Patterns](https://refactoring.guru/pt-br/design-patterns/)

### Por que é importante?
Permite polimorfismo seguro. Se um subtipo "quebra" o contrato, o sistema fica imprevisível.

### Violação (Exemplo clássico):
```kotlin
open class Pokemon {
    open fun attack(): String = "Ataque padrão"
}

class Magikarp : Pokemon() {
    override fun attack(): String {
        throw UnsupportedOperationException("Magikarp não ataca!")
    }
}

fun battle(pokemon: Pokemon) {
    println(pokemon.attack())
}

battle(Magikarp()) // Vai lançar exceção!
```
// Problema: Magikarp não pode ser usado como Pokemon sem quebrar o contrato.

### Como corrigir (LSP):
```kotlin
open class Pokemon {
    open fun attack(): String = "Ataque padrão"
}

class Magikarp : Pokemon() {
    override fun attack(): String = "Splash!" // Implementação válida
}

battle(Magikarp()) // OK, imprime "Splash!"
```

**Padrão que ajuda:** *Template Method* (define contrato e permite especialização segura)

---


## <a name="isp"></a>ISP — Interface Segregation Principle (Segregação de Interface)


**Definição:** Não force clientes a dependerem de métodos que não usam. O ISP trata de criar interfaces pequenas, coesas e específicas para cada contexto de uso.

### O desafio de criar boas interfaces

O maior erro ao projetar interfaces é tentar prever todos os usos futuros, criando interfaces "inchadas" (com muitos métodos) para "facilitar" a vida dos clientes. Isso gera dependências desnecessárias, força implementações inúteis e aumenta o acoplamento.

#### Prejuízo da dependência
Depender de módulos (ou interfaces) que contêm mais elementos do que você realmente precisa é prejudicial. Isso vale tanto para código-fonte (recompilação/redeploy desnecessário) quanto para arquitetura (serviços, APIs, contratos).

### Violação (Exemplo realista):
```kotlin
// Interface genérica demais
interface PokemonOperations {
    fun attack()
    fun swim()
    fun fly()
}

class Pidgey : PokemonOperations {
    override fun attack() { /* ... */ }
    override fun swim() { /* ... */ }
    override fun fly() { /* ... */ }
}

class Magikarp : PokemonOperations {
    override fun attack() { /* ... */ }
    override fun swim() { /* ... */ }
    override fun fly() { /* Magikarp não voa! */ }
}
```
// Problema: Magikarp é forçado a implementar métodos que não fazem sentido.

### Como corrigir (ISP)
Crie interfaces pequenas e específicas:
```kotlin
interface Attacker { fun attack() }
interface Swimmer { fun swim() }
interface Flyer { fun fly() }

class Pidgey : Attacker, Flyer {
    override fun attack() { /* ... */ }
    override fun fly() { /* ... */ }
}

class Magikarp : Attacker, Swimmer {
    override fun attack() { /* ... */ }
    override fun swim() { /* ... */ }
}
```
// Agora cada classe implementa só o que faz sentido.

### Dicas práticas para evitar over engineering e boilerplate
- Comece com interfaces pequenas, só com o que é realmente necessário.
- Não tente prever todos os usos futuros: evolua as interfaces conforme a necessidade real.
- Prefira compor comportamentos (várias interfaces pequenas) a herdar de uma interface "Deus".
- Use Adapter para integrar com interfaces legadas sem forçar métodos inúteis.

### Impacto arquitetural
O ISP vale para código, APIs, contratos de microserviços e até integrações externas. Interfaces inchadas geram acoplamento desnecessário, dificultam evolução e aumentam o risco de breaking changes.

**Padrão que ajuda:**
- [Adapter](https://refactoring.guru/pt-br/design-patterns/adapter) (para adaptar interfaces legadas sem forçar métodos inúteis)

---


## <a name="dip"></a>DIP — Dependency Inversion Principle (Inversão de Dependência)


**Definição:** Módulos de alto nível (regras de negócio, domínio) não devem depender de módulos de baixo nível (detalhes, frameworks, UI, banco, etc). Ambos devem depender de abstrações (interfaces).

### DIP e a Arquitetura Limpa

O DIP é o coração da Clean Architecture: todas as dependências devem apontar para o domínio (o centro). O domínio não sabe nada sobre UI, frameworks, banco, mensageria, etc. Isso garante que as regras de negócio sejam estáveis, testáveis e independentes de detalhes voláteis.

#### Inversão de Fluxo
Em uma arquitetura que aplica DIP, o fluxo de controle (quem chama quem) é o oposto das dependências de código-fonte. O domínio define interfaces; implementações concretas (detalhes) são "plugadas" de fora para dentro.

#### Abstrações vs. Concretos
Tudo que é concreto (frameworks, drivers, APIs externas) é volátil e muda com frequência. Abstrações (interfaces) são estáveis e mudam pouco. O DIP protege seu sistema de mudanças externas.

### Exemplo real de violação do DIP
Imagine um serviço de domínio que instancia diretamente um repositório concreto:
```kotlin
// Violação: domínio depende de detalhe concreto
class PokemonService {
    private val repository = PokemonRepository() // depende de implementação concreta
    fun get(id: Long) = repository.findById(id)
}
```
 
#### Visualização da Inversão de Dependência

Antes (Violação do DIP):
```
[Alto Nível] --> [Baixo Nível]
```
Se precisar trocar a persistência (de banco para API, por exemplo), é preciso alterar o domínio, quebrando testes e regras de negócio.

#### Como corrigir (aplicando DIP)
Dependa de uma abstração:
```kotlin
interface PokemonRepository {
    fun findById(id: Long): Pokemon?
}

class PokemonService(private val repository: PokemonRepository) {
    fun get(id: Long) = repository.findById(id)
}

// Em produção, injete a implementação concreta via construtor ou DI
val service = PokemonService(JpaPokemonRepository())
```
 
Depois (Aplicando DIP):
```
[Alto Nível] --> [Abstração]
                     ^
                     |
             [Baixo Nível]
```

**Como interpretar o diagrama:**
- **Antes:** Módulos de alto nível dependem diretamente de implementações concretas (baixo nível)
- **Depois:** Ambos os módulos dependem de uma abstração (interface), invertendo a direção da dependência
- A seta `-->` indica "depende de"
- O módulo de baixo nível agora implementa a abstração definida pelo alto nível
Agora, o domínio não sabe nada sobre detalhes de persistência. Você pode trocar a implementação sem tocar nas regras de negócio.

### Consequências de violar o DIP
- O domínio fica acoplado a frameworks, bancos, APIs, dificultando testes e evolução.
- Mudanças em detalhes obrigam a alterar regras de negócio.
- O sistema se torna rígido, difícil de manter e pouco testável.
- Refatorações e migrações tecnológicas se tornam traumáticas.

### DIP e Arquitetura de Plug-in
O DIP permite criar arquiteturas de plug-in: o domínio é o "core" estável, e detalhes (UI, banco, mensageria, etc) são "plugins" facilmente substituíveis. Isso permite evoluir o sistema sem medo de quebrar o core.

### Gerenciamento de Instâncias
Criar instâncias de classes concretas dentro do domínio viola o DIP. Use injeção de dependência (DI), fábricas (Factory/Abstract Factory) ou frameworks de DI para garantir que o domínio só conheça interfaces.

**Resumo:**
- O DIP é o pilar da Clean Architecture: dependências sempre apontam para o domínio.
- Facilita testes, manutenção, evolução e migração tecnológica.
- Protege o core do sistema de mudanças externas.

**Padrões que ajudam:**
- [Factory](https://refactoring.guru/pt-br/design-patterns/factory-method), [Abstract Factory](https://refactoring.guru/pt-br/design-patterns/abstract-factory), [Dependency Injection](https://refactoring.guru/pt-br/design-patterns/dependency-injection)

---


---


## Princípios de Componentes (PLUS)

> *Como organizar componentes para evoluir, escalar e manter sistemas grandes?*

Os princípios de componentes, abordados na Parte IV do livro Arquitetura Limpa, estabelecem como agrupar e relacionar módulos em componentes (unidades implantáveis, como JARs, DLLs, pacotes, etc). Eles complementam o SOLID, focando em coesão e acoplamento entre componentes.

### Grupos de Princípios
- **Coesão de Componentes:** Quais classes devem estar juntas?
- **Acoplamento de Componentes:** Como os componentes devem se relacionar?

### Princípios de Coesão de Componentes
1. **REP — Reuse/Release Equivalence Principle (Equivalência de Reúso/Release):**
    - A granularidade do reúso é a granularidade do release. Classes agrupadas em um componente devem ser liberadas juntas, com versionamento e rastreamento comum.
2. **CCP — Common Closure Principle (Fechamento Comum):**
    - Agrupe classes que mudam pelas mesmas razões e nos mesmos momentos. Separe classes que mudam por motivos diferentes.
    - É o SRP aplicado ao nível de componentes.
3. **CRP — Common Reuse Principle (Reúso Comum):**
    - Quem usa um componente deve usar todas as classes que ele contém. Não dependa de coisas que você não precisa.

> **Nota:** REP e CCP tendem a aumentar o número de componentes (inclusivos), enquanto CRP tende a diminuir (excludente). O equilíbrio entre eles é visualizado como um "diagrama de tensão".

### Princípios de Acoplamento de Componentes
1. **SAP — Stable Abstractions Principle (Abstrações Estáveis):**
    - Um componente deve ser tão abstrato quanto estável. Componentes estáveis (difíceis de mudar) devem ser altamente abstratos, permitindo extensão sem modificação.
2. **SDP — Stable Dependencies Principle (Dependência Estável):**
    - As dependências entre componentes devem apontar para componentes mais estáveis.
3. **ADP — Acyclic Dependencies Principle (Dependências Acíclicas):**
    - O grafo de dependências entre componentes deve ser acíclico. Ciclos dificultam manutenção e evolução.

### Visualização
Imagine os componentes como "ilhas" conectadas por setas (dependências). As setas devem sempre apontar para componentes mais estáveis e abstratos, evitando ciclos. O equilíbrio entre coesão e acoplamento é o segredo para sistemas grandes e flexíveis.

> **Leia mais:** [Princípios Componentes](./COMPONENT_PRINCIPLES.md)

---

## Conclusão

SOLID não é só teoria: é o caminho para sistemas evolutivos, testáveis e robustos. Use os exemplos acima como referência para refatorar seu código, aplicar os padrões certos e construir microserviços Kotlin realmente profissionais!
