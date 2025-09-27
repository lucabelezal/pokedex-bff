# Arquitetura Clean — Pokédex BFF

**Atualização:** 27 de setembro de 2025

---

## Contexto e Decisão Arquitetural

O projeto Pokédex BFF adota a Clean Architecture de Robert C. Martin (Uncle Bob) com 4 camadas, visando isolamento, testabilidade e independência de frameworks. A estrutura anterior baseada em MVC Estruturado foi descontinuada para dar lugar a uma separação mais clara de responsabilidades e maior facilidade de manutenção.

---

## Estrutura de Pastas e Camadas

```
src/main/kotlin/
  domain/                # Núcleo do domínio: entidades, value objects, interfaces
    model/              # Entidades puras (data classes)
    repository/         # Interfaces de acesso a dados
    usecase/            # Interfaces dos casos de uso (contratos)
  application/          # Casos de uso (usecases), DTOs de resposta
    interactor/         # Implementações concretas dos casos de uso (Interactors)
    worker/             # Workers para tarefas assíncronas/background (opcional)
  adapters/
    controller/         # Controllers HTTP (Spring) — dependem só das interfaces de usecase
    gateway/            # Implementações concretas de interfaces do domínio
  infrastructure/
    config/             # Configuração de beans de aplicação
    config/web/         # Configurações de CORS, OpenAPI, etc
    persistence/        # Entidades JPA, repositórios Spring Data
```

---

## Detalhamento das Camadas

### 1. Domain Layer

- **Localização:** `/domain/model/`, `/domain/repository/`, `/domain/usecase/`
- **Função:** Representar o núcleo do negócio, sem dependências externas.
- **O que contém:**
  - **Entidades puras** (data classes): representam conceitos do domínio.
  - **Value Objects:** tipos imutáveis, sem identidade própria.
  - **Interfaces de repositório:** contratos para persistência.
  - **Interfaces de usecase:** contratos dos casos de uso (terminam com `UseCase`).
- **Exemplo:**
```kotlin
// /domain/model/Pokemon.kt
data class Pokemon(val id: Long, val name: String)

// /domain/repository/PokemonRepository.kt
interface PokemonRepository {
    fun findById(id: Long): Pokemon?
}

// /domain/usecase/GetPokemonByIdUseCase.kt
interface GetPokemonByIdUseCase {
    fun execute(id: Long): Pokemon?
}
```

---

### 2. Application Layer

- **Localização:** `/application/interactor/`, `/application/worker/`
- **Função:** Orquestrar regras de negócio para cada caso de uso.
- **O que contém:**
  - **Interactors:** implementações concretas dos casos de uso (terminam com `Interactor`).
  - **Workers:** executam tarefas assíncronas/background, chamando Interactors.
- **O que fazem:**
  - Recebem dados de input de controllers/adapters.
  - Chamam entidades e interfaces do Domain Layer.
  - Aplicam regras de negócio e lógica de aplicação.
  - Não conhecem detalhes de infraestrutura, apenas interfaces.
- **Exemplo:**
```kotlin
// /application/interactor/GetPokemonByIdInteractor.kt
class GetPokemonByIdInteractor(
    private val pokemonRepository: PokemonRepository
) : GetPokemonByIdUseCase {
    override fun execute(id: Long): Pokemon? {
        return pokemonRepository.findById(id)
    }
}

// /application/worker/EmailWorker.kt
class EmailWorker(
    private val sendEmailInteractor: SendEmailUseCase
) {
    fun executePendingEmails() {
        val emails = fetchPendingEmails()
        emails.forEach { sendEmailInteractor.execute(it) }
    }
}
```

---

### 3. Adapter Layer

- **Localização:** `/adapters/controller/`, `/adapters/gateway/`
- **Função:** Adaptar o núcleo para o mundo externo (HTTP, banco, etc).
- **O que contém:**
  - **Controllers:** expõem endpoints REST, dependem só das interfaces de usecase do domínio.
  - **Gateways:** implementações concretas das interfaces de repositório do domínio.
- **O que fazem:**
  - Recebem requisições externas e convertem para chamadas de usecase.
  - Não contêm regra de negócio, apenas adaptação.
  - Gateways convertem entidades do domínio para entidades de persistência e vice-versa.
- **Exemplo:**
```kotlin
// /adapters/controller/PokemonController.kt
@RestController
class PokemonController(
    private val getPokemonByIdUseCase: GetPokemonByIdUseCase
) {
    @GetMapping("/pokemon/{id}")
    fun getById(@PathVariable id: Long) = getPokemonByIdUseCase.execute(id)
}

// /adapters/gateway/PokemonRepositoryGateway.kt
class PokemonRepositoryGateway(
    private val springDataRepo: SpringDataPokemonRepository
) : PokemonRepository {
    override fun findById(id: Long): Pokemon? {
        val entity = springDataRepo.findById(id).orElse(null)
        return entity?.toDomain()
    }
}
```

---

### 4. Infrastructure Layer

- **Localização:** `/infrastructure/persistence/`, `/infrastructure/config/`, `/infrastructure/config/web/`
- **Função:** Detalhes de frameworks, banco de dados, configurações.
- **O que contém:**
  - **Entidades JPA:** mapeamento para o banco.
  - **Repositórios Spring Data:** interfaces para persistência.
  - **Configurações Spring Boot:** beans, CORS, OpenAPI, etc.
- **O que fazem:**
  - Não contêm regra de negócio.
  - São usados apenas por Gateways e pelo framework.
- **Exemplo:**
```kotlin
// /infrastructure/persistence/entities/PokemonEntity.kt
@Entity
data class PokemonEntity(...)

// /infrastructure/persistence/repositories/SpringDataPokemonRepository.kt
interface SpringDataPokemonRepository : JpaRepository<PokemonEntity, Long>

// /infrastructure/config/BeansConfig.kt
@Configuration
class BeansConfig { ... }
```

---

## Resumo das Nomenclaturas

| Conceito/Nome         | Localização                  | Sufixo/Padrão           |
|---------------------- |-----------------------------|-------------------------|
| Entidade              | /domain/model/              | (data class)            |
| Value Object          | /domain/model/              | (data class)            |
| Interface de Repo     | /domain/repository/         | Repository              |
| Interface de UseCase  | /domain/usecase/            | UseCase                 |
| Interactor            | /application/interactor/    | Interactor              |
| Worker                | /application/worker/        | Worker                  |
| Controller            | /adapters/controller/       | Controller              |
| Gateway               | /adapters/gateway/          | Gateway                 |
| JPA Entity            | /infrastructure/persistence/| Entity                  |
| Spring Data Repo      | /infrastructure/persistence/| Repository              |
| Configuração          | /infrastructure/config/     | Config                  |

---

## Diagrama ASCII

```
[Controller] ──▶ [UseCase interface (Domain)] ◀── [Interactor (Application)]
                                         │
                                         ▼
                        [Repository interface (Domain)] ◀── [Gateway (Adapter)] ──▶ [DB/Infra]
```

- As setas de dependência sempre apontam de fora para dentro.
- O núcleo (Domain + Application) não conhece Spring Boot, banco de dados ou detalhes de implementação.

---

## Isolamento de Spring Boot e Banco de Dados
- Controllers e Interactors não conhecem detalhes de banco/framework.
- Gateways e Infrastructure fazem a ponte com o mundo externo.
- Testes de unidade podem ser feitos no núcleo sem dependências externas.

---

## Exemplo de Fluxo
- Controller recebe requisição HTTP e chama o método de uma interface de usecase (Domain).
- O Interactor (Application) implementa essa interface, executa a lógica e usa interfaces de repositório (Domain).
- O Gateway (Adapter) implementa a interface de repositório, acessando o banco via Infrastructure.

---

## Referências e Fundamentação

- **Livro:** Robert C. Martin, "Clean Architecture: A Craftsman's Guide to Software Structure and Design", Prentice Hall, 2017.
- [The Clean Architecture (artigo original)](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Clean Architecture no site do autor](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [DDD e Clean Architecture](https://herbertograca.com/2017/09/14/structuring-applications-with-clean-architecture/)
- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)

> “A principal regra da Clean Architecture é que as dependências de código devem apontar para dentro, para o núcleo do sistema.” — Robert C. Martin, Clean Architecture

---

Esta documentação reflete a arquitetura vigente do projeto. Para histórico e comparativos, consulte os demais arquivos em /doc/architecture/.
