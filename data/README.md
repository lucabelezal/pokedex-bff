# 📊 Data Directory

Este diretório contém os dados fonte do projeto Pokédex BFF.

## 📁 Estrutura

```
data/
└── json/           # Arquivos JSON com dados dos Pokémons
    ├── 01_region.json          # Regiões (Kanto, Johto, etc.)
    ├── 02_type.json            # Tipos (Fogo, Água, Grama, etc.)
    ├── 03_egg_group.json       # Grupos de ovos
    ├── 04_generation.json      # Gerações dos jogos
    ├── 05_ability.json         # Habilidades dos Pokémons
    ├── 06_species.json         # Espécies dos Pokémons
    ├── 07_stats.json           # Estatísticas base
    ├── 08_evolution_chains.json # Cadeias evolutivas
    ├── 09_pokemon.json         # Dados principais dos Pokémons
    └── 10_weaknesses.json      # Fraquezas por tipo
```

## 🔢 Ordem de Processamento

Os arquivos são numerados para garantir a ordem correta de inserção no banco de dados, respeitando as dependências de chaves estrangeiras:

1. **Tabelas Base**: regions, types, egg_groups, generations
2. **Tabelas Intermediárias**: abilities, species, stats, evolution_chains  
3. **Tabela Principal**: pokemons (depende de todas as anteriores)
4. **Tabelas de Relacionamento**: pokemon_types, pokemon_abilities, pokemon_weaknesses

## 🛠️ Como Usar

Para gerar o SQL a partir destes dados:

```bash
# Gerar arquivo SQL
make generate-sql-data

# Ou executar diretamente
python3 tools/database/generate_sql_from_json.py
```

## 📝 Formato dos Arquivos

Todos os arquivos seguem o formato JSON padrão:

```json
[
  {
    "id": 1,
    "name": "Example",
    "other_fields": "..."
  }
]
```

## ⚠️ Importante

- **Não altere** a numeração dos arquivos
- **Mantenha** a estrutura JSON consistente
