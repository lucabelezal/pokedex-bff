# CONTEXTO DO PROJETO POKÉDEX BFF

> **IMPORTANTE:** Toda documentação Markdown (.md) criada para o projeto deve seguir estritamente os padrões, estrutura e convenções dos arquivos já existentes dentro da pasta `doc/`. **Antes de criar um novo .md, revise se já existe um arquivo ou subpasta adequada para o tema. Nunca duplique temas ou crie arquivos soltos fora do padrão.**

**Última atualização:** 27 de setembro de 2025

---
## 📌 Visão Geral
O projeto Pokédex BFF adota **Clean Architecture (Uncle Bob)** com organização de código consolidada sob o namespace único `kotlin.com.pokedex.bff`. Durante a refatoração foram removidas pastas órfãs fora deste namespace (`/adapters`, `/application`, `/domain` na raiz de `kotlin/`) que continham versões duplicadas de entidades, use cases e controllers.

---

## 🏗️ Arquitetura Final (2025)
```
src/main/kotlin/com/pokedex/bff/
  domain/
    pokemon/
      entities/         # Entidades ricas de domínio (ex: Pokemon.kt, Ability.kt)
      valueobject/      # Value Objects (ex: PokemonNumber.kt, Experience.kt)
      repository/       # Interfaces de repositório (ex: PokemonRepository.kt)
      service/          # Serviços de domínio
      event/ exception/ # Eventos e exceções de domínio
    shared/             # Tipos utilitários, exceptions e value objects genéricos
  application/
    interactor/         # Implementações concretas dos casos de uso (ex: CreatePokemonInteractor.kt)
    usecase/            # Interfaces de casos de uso (ex: CreatePokemonUseCase.kt)
    dtos/
      input/            # DTOs de entrada (ex: CreatePokemonInput.kt)
      output/           # DTOs de saída (ex: PokemonOutput.kt)
  adapters/
    input/
      web/
        controller/     # Controllers REST (ex: PokemonController.kt)
        dto/ mapper/    # DTOs e mapeadores para entrada
    output/
      persistence/
        entity/         # Entidades JPA (ex: PokemonJpaEntity.kt)
        mapper/         # Mapeadores JPA <-> domínio (ex: PokemonPersistenceMapper.kt)
        repository/     # Adapters de repositório (ex: PokemonRepositoryAdapter.kt)
      external/
        client/         # Clients HTTP externos (ex: PokeApiClient.kt)
        mapper/         # Mapeadores de resposta externa
  infrastructure/
    config/             # Beans, providers, configuração de DI (ex: UseCaseFactory.kt)
    migration/          # Scripts de migração (ex: V1__Create_pokemon_table.sql)
    security/           # Configuração de segurança (ex: SecurityConfig.kt)
```

### Fluxo de Dependências
`infrastructure → adapters → application → domain`
- Nenhuma dependência reversa.
- O **domínio** não conhece frameworks, JPA, DTOs ou detalhes técnicos.
- **Interactors** dependem apenas de interfaces do domínio.
- **Controllers** só enxergam interfaces de use cases.
- **Adapters** implementam interfaces e fazem mapeamento entre camadas.

### Exemplos de Componentes
- **Entidade de Domínio:** `domain/pokemon/entities/Pokemon.kt`
- **Value Object:** `domain/pokemon/valueobject/PokemonNumber.kt`
- **Repositório:** `domain/pokemon/repository/PokemonRepository.kt`
- **Use Case:** `application/usecase/CreatePokemonUseCase.kt`
- **Interactor:** `application/interactor/CreatePokemonInteractor.kt`
- **Controller:** `adapters/input/web/controller/PokemonController.kt`
- **Entidade JPA:** `adapters/output/persistence/entity/PokemonJpaEntity.kt`
- **Mapper:** `adapters/output/persistence/mapper/PokemonPersistenceMapper.kt`
- **Client Externo:** `adapters/output/external/client/PokeApiClient.kt`
- **Configuração:** `infrastructure/config/UseCaseFactory.kt`

---
## 🔄 Decisões Importantes
| Tema | Decisão | Justificativa |
|------|---------|---------------|
| Estrutura | Pastas plurais (`entities`, `repositories`, `usecases`, `interactors`, `controllers`, `gateways`) | Consistência semântica e previsibilidade |
| Duplicação antiga | Remoção / neutralização de `src/main/kotlin/domain/model/*` | Evitar colisão entre modelos simplificados e entidades ricas |
| Mapper | `PokemonMapper` centraliza conversões JPA ↔ Domínio | Isola detalhes de persistência e reduz lógica duplicada em gateways |
| Use Cases | Interfaces no domínio + Interactors na aplicação | Inversão de dependência clara |
| DTOs | Mantidos fora do domínio (`application/dto/response`) | Evitar acoplamento de transporte à modelagem de negócio |
| Persistência | Mapeamento parcial Domain -> Entity (save) com TODOs | Evoluir quando operações de escrita ampliarem |

---
## 🧩 Componentes Chave
- **Entidades Ricas**: `Pokemon`, `Ability`, `Type`, `Stats`, `Species`, `Region`, `EvolutionChain`, etc.
- **Value Objects (a evoluir)**: candidatos: `PokemonNumber`, `GenderRatio`, `LocalizedName`.
- **Casos de Uso (interfaces)**: `FetchPokemonUseCase`, `GetPaginatedPokemonsUseCase`.
- **Interactors**: `FetchPokemonInteractor`, `GetPaginatedPokemonsInteractor`.
- **Gateways**: `JpaPokemonRepository` (usa `SpringDataPokemonRepository` + `PokemonMapper`).
- **Mapper**: `PokemonMapper` (inclui TODOs para mapeamentos de escrita completos).

---
## 🧪 Testabilidade
- **Domínio** testável isoladamente sem contexto Spring.
- **Interactors** podem ser testados mockando apenas `PokemonRepository`.
- **Controllers** podem ser cobertos via WebMvcTest ou testes de contrato.
- **Mapper** merece casos de teste focados em campos complexos (sprites, abilities, stats, fraquezas).

Sugestão de primeiros testes:
```
FetchPokemonInteractorTest
GetPaginatedPokemonsInteractorTest
PokemonMapperTest
```

---
## 🛠️ TODOs Técnicos (Backlog Interno)
| Categoria | Item | Prioridade |
|-----------|------|------------|
| Mapper | Completar `toEntityPartial` para todos os relacionamentos | Média |
| Domain | Criar Value Objects (`PokemonNumber`, `StatTotal`) | Média |
| Testes | Adicionar testes unitários para mappers e interactors | Alta |
| Infra | Avaliar carga de coleções EAGER (types/abilities) -> LAZY + DTO flatten | Média |
| Docs | Atualizar README principal com comandos pós-refatoração | Baixa |
| CI | Incluir etapa de verificação de pacotes proibidos (ex: infra importando application) | Baixa |

---
## 🛡️ Regras de Proteção Arquitetural
1. **Controllers não podem importar nada de `infrastructure.*`**.
2. **Interactors não podem conhecer classes concretas de gateway** (apenas interfaces de repositório).
3. **Domain não referencia DTO, entidades JPA ou classes Spring**.
4. **Gateways não expõem tipos de infraestrutura para fora**.
5. **Mapper** é o único local onde `PokemonEntity` ↔ `Pokemon` ocorre.

Sugestão de script de verificação (futuro):
```bash
# Fail se domínio importar algo externo
grep -R "springframework" src/main/kotlin/com/pokedex/bff/domain && echo "VIOLAÇÃO" || echo OK
```

---
## 🧭 Próximos Passos Recomendados
1. Remover fisicamente diretórios legados já neutralizados (`/adapters`, `/application`, `/domain` fora de `com/pokedex/bff`).
2. Implementar testes mínimos (interactors + mapper).
3. Evoluir mapeamento reverso no `PokemonMapper` conforme necessidade de escrita.
4. Introduzir Value Objects para refinar invariantes do domínio.
5. Adicionar validação de camadas em pipeline (ex: `ArchUnit` ou `ClassGraph`).

---
## 📚 Referências
- Robert C. Martin — *Clean Architecture* (2017)
- Artigo original: https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html
- Hexagonal / Ports & Adapters (Alistair Cockburn)
- DDD Pragmático (foco em linguajar onipresente e isolamento de invariantes)

---
**Estado Atual:** Estrutura consolidada e alinhada à Clean Architecture. Restam apenas otimizações e testes.

> Manter a disciplina arquitetural agora evita regressões futuras e facilita evolução do domínio.
