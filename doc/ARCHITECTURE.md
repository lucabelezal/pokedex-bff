# Arquitetura do Sistema — Pokédex BFF

## 1. Visão Geral

O Pokédex BFF é um backend que oferece dados estruturados de Pokémon através de APIs REST.  
A arquitetura é baseada em uma aplicação Spring Boot modular, seguindo boas práticas de separação de camadas para facilitar manutenção, testes e evolução.

---

## 2. Objetivos Arquiteturais

- Manter separação clara entre apresentação, domínio e infraestrutura.  
- Facilitar testes unitários e integração.  
- Facilitar evolução, manutenibilidade e escalabilidade.  
- Permitir substituição futura de tecnologias sem impacto na lógica de negócio.  
- Suportar pré-carga de dados via JSON para banco relacional.

---

## 3. Visão Geral da Arquitetura

### Camadas

| Camada       | Responsabilidade                                                        | Pacote Base                    |
|--------------|------------------------------------------------------------------------|-------------------------------|
| Controller   | Interface HTTP REST, recebimento e retorno de dados JSON via API       | `controller/`                  |
| DTO          | Objetos para comunicação via API, desacoplados da camada persistência  | `controller/dtos/`             |
| Service      | Orquestra lógica de aplicação, regras de negócio simples e fluxos      | `service/`                    |
| Domain Model | Modelos de negócio puros, regras e validações de negócio (opcional)    | `domain/model/`                |
| Infra Entity | Entidades JPA para mapeamento relacional e persistência                | `infra/entity/`                |
| Infra Repo   | Repositórios Spring Data JPA para acesso ao banco                      | `infra/repository/`            |
| Seeder       | Classes para carregamento inicial de dados (JSON → banco)              | `infra/seeder/`                |
| Utils        | Utilitários e helpers genéricos                                        | `utils/`                      |

---

## 4. Fluxo Principal

1. **Cliente** faz requisição HTTP REST para o Controller.  
2. Controller recebe parâmetros e delega para Service.  
3. Service executa lógica de negócio e acessa Repositórios para CRUD.  
4. Repositórios acessam banco via JPA.  
5. Dados persistidos ou consultados são convertidos entre Entidades, Modelos e DTOs via mappers.  
6. Service retorna DTOs para Controller, que retorna resposta HTTP.

---

## 5. Tecnologias Utilizadas

- **Spring Boot:** Framework principal para API REST e injeção de dependência.  
- **Spring Data JPA:** Repositórios para acesso a banco relacional.  
- **PostgreSQL:** Banco de dados relacional.  
- **Jackson:** Serialização/deserialização JSON.  
- **SLF4J + Logback:** Log de sistema.  
- **Kotlin:** Linguagem principal.

---

## 6. Padrões e Boas Práticas

- **Separação de responsabilidades:** Cada camada tem função clara.  
- **DTOs separados das entidades:** Evita exposição direta da estrutura interna.  
- **Transações declarativas:** Controladas na camada de serviço com `@Transactional`.  
- **Log detalhado:** Para rastreamento da importação e operações críticas.  
- **Carregamento inicial:** Seeder para popular banco a partir de arquivos JSON.  
- **Mapeamento manual via funções e extension methods:** Controle explícito de transformação.

---

## 7. Detalhes Técnicos

### 7.1 Controller

- Localizado em `controller/`.  
- Recebe parâmetros de paginação, filtros.  
- Retorna DTOs via ResponseEntity.

### 7.2 Service

- Localizado em `service/`.  
- Implementa lógica de paginação, orquestra repositórios.  
- Transações são gerenciadas aqui.

### 7.3 Domain Model

- Classes Kotlin simples, sem dependências Spring.  
- Regras e validações podem ser implementadas aqui.

### 7.4 Infra Entity

- Entidades JPA com anotações `@Entity`, `@Table`.  
- Mapear colunas, relações e constraints do banco.

### 7.5 Infra Repository

- Interface estende `JpaRepository`.  
- Métodos customizados e JPQL via `@Query`.

### 7.6 Seeder

- Executado em startup via `CommandLineRunner`.  
- Carrega dados JSON e popula banco.  
- Tratamento de erros e logs detalhados.

---

## 8. Exemplo de Estrutura de Pastas

```plaintext
com.pokedex.bff
├── controller
│   └── PokemonController.kt
├── controller/dtos
│   └── PokemonListResponse.kt
├── service
│   └── PokemonService.kt
├── domain/model
│   └── Pokemon.kt
├── infra/entity
│   └── PokemonEntity.kt
├── infra/repository
│   └── PokemonRepository.kt
├── infra/seeder
│   └── DatabaseSeeder.kt
├── utils
│   └── JsonFile.kt
```

### Diagrama de Componentes

```mermaid
graph TD
  Client -->|HTTP REST| Controller
  Controller --> Service
  Service --> Repository
  Repository --> Database[(PostgreSQL)]

  Controller -.->|Uses DTOs| DTO[DTOs]
  Service -.->|Uses Domain Models| DomainModel[Domain Models]
  Repository -.->|Uses Entities| Entity[Entities JPA]

  Seeder --> Repository
  Seeder -->|Reads| JSONFiles[JSON Files]
  ```

### Diagrama de Sequência

```mermaid
sequenceDiagram
  participant Client
  participant Controller
  participant Service
  participant Repository
  participant Database

  Client->>Controller: GET /api/v1/pokemons?page=0&size=10
  Controller->>Service: getPokemons(page=0, size=10)
  Service->>Repository: findAll(PageRequest(0,10))
  Repository->>Database: Query paginated Pokemons
  Database-->>Repository: Page<Pokemon>
  Repository-->>Service: Page<Pokemon>
  Service-->>Controller: PokemonListResponse with pagination metadata
  Controller-->>Client: HTTP 200 + JSON payload
  ```
