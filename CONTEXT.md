# CONTEXTO DO PROJETO POKÉDX BFF

**Última atualização:** 22 de setembro de 2025

---

## 🏗️ REORGANIZAÇÃO ESTRUTURAL (Setembro 2025)

### 📊 Estrutura Anterior vs. Atual

#### ❌ ANTES (estrutura dispersa):
- **Dados**: `src/main/resources/data/` (misturado com resources do Spring)
- **Scripts**: `scripts/` (na raiz, desorganizado)  
- **SQL**: `docker/db/` (misturado com configs Docker)

#### ✅ DEPOIS (estrutura organizada):
```
pokedex-bff/
├── data/
│   └── json/              # 📊 Dados fonte JSON numerados (01-10)
├── database/
│   ├── schema/            # 🗄️ DDL - estrutura das tabelas
│   ├── seeds/             # 🌱 DML - dados iniciais gerados  
│   └── migrations/        # 🔄 Scripts de migração futuros
├── tools/
│   └── database/          # 🔧 Scripts Python para banco
└── docker/                # 🐳 Configurações Docker limpas
```

### 📁 Movimentações Realizadas

| Tipo | Origem | Destino | Status |
|------|--------|---------|--------|
| **JSONs** | `src/main/resources/data/` | `data/json/` | ✅ |
| **Scripts Python** | `scripts/` | `tools/database/` | ✅ |
| **Schema SQL** | `docker/db/schema.sql` | `database/schema/` | ✅ |
| **Seeds SQL** | `docker/db/init-data.sql` | `database/seeds/` | ✅ |
| **Limpeza** | Arquivos legacy e diretórios vazios | - | ✅ |

---

## 🔄 Estrutura e Fluxo de Dados

### 📋 Sequência de Dados (Dependências de Chaves Estrangeiras)

| Ordem | Arquivo | Tabela | Dependências |
|-------|---------|--------|--------------|
| 1 | `01_region.json` | `regions` | - |
| 2 | `02_type.json` | `types` | - |
| 3 | `03_egg_group.json` | `egg_groups` | - |
| 4 | `04_generation.json` | `generations` | - |
| 5 | `05_ability.json` | `abilities` | - |
| 6 | `06_species.json` | `species` | regions, generations |
| 7 | `07_stats.json` | `stats` | - |
| 8 | `08_evolution_chains.json` | `evolution_chains` | - |
| 9 | `09_pokemon.json` | `pokemons` + relacionamentos | species, abilities, stats |
| 10 | `10_weaknesses.json` | `pokemon_weaknesses` | pokemons |

### 🎯 Fluxo de Desenvolvimento

1. **Desacoplamento Total**: BFF sem seeder ou carga automática
2. **Inicialização por SQL**: `database/schema/schema.sql` + `database/seeds/init-data.sql`
3. **Geração Automática**: `tools/database/generate_sql_from_json.py` converte JSONs
4. **Validação**: `tools/database/validate_database.py` verifica banco

---

## 💻 Comandos Principais

### 🔧 Comandos de Desenvolvimento

```bash
# Gerar SQL a partir dos JSONs
make generate-sql-data      # Executa: tools/database/generate_sql_from_json.py

# Gerenciamento do banco
make db-only-up            # Sobe banco isolado com dados
make db-only-restart       # Reinicia banco com dados atualizados  
make db-only-down          # Para o banco
make db-info              # Informações de conexão

# Validação
make validate-db          # Executa: tools/database/validate_database.py
```

### 📊 Status da Validação

- ✅ **Estrutura**: 13 tabelas criadas
- ✅ **Dados**: 1223 registros inseridos
- ✅ **Integridade**: 0 problemas encontrados
- ✅ **Comandos**: Todos os targets make funcionando

---

## ➕ Processo para Novos Dados

### 🔄 Fluxo Step-by-Step

1. **Editar JSONs**: Modificar arquivos em `data/json/` (manter sequência numérica)
2. **Gerar SQL**: `make generate-sql-data`  
3. **Atualizar Banco**: `make db-only-restart`
4. **Validar**: `make validate-db`

### ⚠️ Regras Importantes

- **Sequência numérica**: Manter ordem dos arquivos (`01` a `10`)
- **Dependências**: Respeitar chaves estrangeiras na ordem
- **Naming**: Nome da tabela = arquivo sem prefixo numérico (ex: `01_region.json` → `regions`)
- **Logs**: Scripts Python mostram progresso detalhado

---

## 🐳 Configurações Docker Atualizadas

### Volume Mounts
```yaml
volumes:
  - ./database/schema/:/docker-entrypoint-initdb.d/01-schema/
  - ./database/seeds/:/docker-entrypoint-initdb.d/02-seeds/
```

### Ambiente Isolado
- **Arquivo**: `docker/docker-compose.db-only.yml`
- **Porta**: `localhost:5434`
- **Logs**: Detalhados para debugging

---

## 📚 Documentação Atualizada

| Arquivo | Propósito |
|---------|-----------|
| `README.md` | Guia completo de setup de desenvolvimento |
| `data/README.md` | Documentação da estrutura de dados |
| `tools/README.md` | Documentação das ferramentas |
| `CONTEXT.md` | Este arquivo - contexto e histórico do projeto |

---

## 🎯 Observações Importantes

- **CI/CD Ready**: Estrutura otimizada para pipelines
- **Onboarding**: Processo claro para novos desenvolvedores
- **Ambientes Limpos**: Inicialização consistente
- **Separação de Responsabilidades**: Dados fonte vs. dados gerados
- **Manutenibilidade**: Estrutura lógica e documentada

---

> 💡 **Nota**: Sempre atualize este arquivo ao realizar mudanças estruturais, de build ou de fluxo de dados.

## Comandos Principais
## Comandos Principais
- `make generate-sql-data`: Gera SQL a partir dos JSONs (`tools/database/generate_sql_from_json.py`)
- `make db-only-up`: Sobe banco isolado com dados pré-carregados
- `make db-only-restart`: Reinicia banco com dados atualizados
- `make validate-db`: Valida estrutura e dados do banco (`tools/database/validate_database.py`)
- `make db-info`: Exibe informações de conexão para DBeaver/pgAdmin

## Processo para Novos Dados
1. Editar JSONs em `data/json/` (manter sequência numérica)
2. Executar `make generate-sql-data`
3. Executar `make db-only-restart`
4. Validar com `make validate-db`

## Observações Importantes
- Nome da tabela é extraído do JSON removendo prefixo numérico (ex: `01_region.json` → tabela `regions`)
- Scripts Python exibem logs detalhados de sucesso/erro
- Banco é populado automaticamente sem dependência do BFF
- Estrutura otimizada para CI/CD, onboarding e ambientes limpos
- Separação clara: dados fonte (JSONs) vs. dados gerados (SQL)

## Documentação Atualizada
- `README.md`: Guia completo de setup de desenvolvimento
- `data/README.md`: Documentação da estrutura de dados
- `tools/README.md`: Documentação das ferramentas
- `REORGANIZATION_SUMMARY.md`: Resumo detalhado da transformação

# Sempre atualize este arquivo ao realizar mudanças estruturais, de build ou de fluxo de dados.

