# Visão Geral

Este repositório contém o código-fonte para o **Pokedex BFF (Backend For Frontend)**. Este serviço atua como uma camada intermediária otimizada entre as fontes de dados externas sobre Pokémon e as aplicações frontend que consomem essas informações (web, mobile, etc.).

## 🎯 Principais Objetivos

O Pokedex BFF é projetado para:
* **Simplificar o Consumo de Dados:** Centralizar a lógica de agregação e transformação de dados de diversas fontes, fornecendo uma API unificada e fácil de usar para o frontend.
* **Otimizar Desempenho:** Reduzir o número de requisições que o frontend precisa fazer, combinando dados de múltiplas fontes em uma única resposta.
* **Adaptar Formato:** Oferecer dados no formato exato que o frontend necessita, minimizando a lógica de processamento e adaptação na interface de usuário.
* **Cache e Desempenho:** Implementar estratégias de cache para dados frequentemente acessados, melhorando a velocidade de resposta.

## 🏗️ Arquitetura Implementada (Setembro 2025)

### **Clean Architecture + Hexagonal Architecture**

O projeto foi **completamente refatorado** seguindo rigorosamente os princípios do **Clean Architecture com Ports & Adapters**, garantindo:

#### ✅ **Separação Total de Responsabilidades**
- **Domain**: Núcleo puro de negócio com Value Objects ricos
- **Application**: Use Cases específicos com responsabilidade única  
- **Infrastructure**: Adaptadores que implementam portas de entrada/saída
- **Interfaces**: Controllers que dependem apenas de abstrações

#### ✅ **Benefícios Arquiteturais**
- **Alta Testabilidade**: Use Cases testáveis unitariamente sem infraestrutura
- **Baixo Acoplamento**: Comunicação entre camadas apenas via interfaces
- **Domínio Rico**: Value Objects com validações e comportamentos de negócio
- **Inversão Total**: Controllers usam interfaces, não implementações
- **Evolutibilidade**: Fácil adição de novos Use Cases e substituição de implementações

### **Implementações Concretas**

#### **Value Objects Ricos**
```kotlin
@JvmInline
value class PokemonId(val value: Long) {
    fun isGeneration1(): Boolean = value in 1L..151L
    fun getGeneration(): Int = when(value) { /* regras de negócio */ }
}
```

#### **Use Cases Específicos**
```kotlin
@Component  
class GetPaginatedPokemonsUseCase(
    private val pokemonRepository: PokemonRepository // Interface do domínio
) {
    fun execute(page: Int, size: Int): PokedexListResponse {
        // Lógica de negócio pura com validações
    }
}
```

#### **Ports & Adapters**
```kotlin
// Porta de entrada
interface PokedexUseCases {
    fun getPaginatedPokemons(page: Int, size: Int): PokedexListResponse
}

// Adaptador que implementa a porta
@Service
class PokedexUseCasesAdapter(
    private val getPaginatedPokemonsUseCase: GetPaginatedPokemonsUseCase
) : PokedexUseCases
```

## 🧪 Testabilidade Implementada

### **Testes Unitários de Value Objects**
```kotlin
@Test
fun `should format pokemon number correctly`() {
    val pokemonNumber = PokemonNumber("25")
    assertThat(pokemonNumber.formatForDisplay()).isEqualTo("025")
}
```

### **Testes Unitários de Use Cases**
```kotlin
@Test  
fun `should return paginated pokemon list when valid parameters`() {
    // Given
    every { pokemonRepository.findAll(any()) } returns mockPage
    
    // When
    val result = useCase.execute(0, 10)
    
    // Then
    assertThat(result.pokemons).hasSize(1)
    verify(exactly = 1) { pokemonRepository.findAll(any()) }
}
```

## 📊 Métricas de Melhoria

| Aspecto | Antes | Depois |
|---------|-------|--------|
| **Acoplamento** | Alto (interface+impl juntos) | Baixo (separação total) |
| **Testabilidade** | Difícil (depende de Spring) | Fácil (mocks simples) |
| **Domínio** | Anêmico | Rico (Value Objects) |
| **Responsabilidades** | Misturadas | Single Responsibility |
| **Inversão de Dependência** | Parcial | Total |

## 🚀 Status da Implementação

### ✅ **Concluído**
- Separação total entre interface e implementação
- Value Objects ricos com validações de negócio
- Use Cases específicos com responsabilidade única
- Ports & Adapters implementados
- Testes unitários funcionais
- Compilação successful após refatoração

### 🔄 **Próximos Passos**
- Extensão para outros contextos (Species, Evolution)
- Mais Value Objects (`PokemonType`, `PokemonStats`)
- Domain Services para lógicas complexas
- Testes de integração e arquitetura

---

## 📚 Documentação Relacionada

- [ARCHITECTURE.md](./ARCHITECTURE.md) - Documentação completa da arquitetura implementada
- [GETTING_STARTED.md](./GETTING_STARTED.md) - Guia de setup atualizado
- [REFACTORING_PROPOSAL.md](../REFACTORING_PROPOSAL.md) - Proposta completa da refatoração
- [REFACTORING_SUMMARY.md](../REFACTORING_SUMMARY.md) - Resumo executivo das melhorias
- [CONTEXT.md](../CONTEXT.md) - Contexto completo do projeto atualizado

---

*Documento atualizado após refatoração Clean Architecture avançada - 23/09/2025*
