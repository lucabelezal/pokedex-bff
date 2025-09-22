# 🛠️ Tools Directory

Ferramentas de desenvolvimento para o projeto Pokédex BFF.

## 📁 Estrutura

```
tools/
└── database/                       # Ferramentas relacionadas ao banco de dados
    ├── check_dependencies.py       # Verifica dependências do sistema
    ├── generate_sql_from_json.py   # Gera SQL a partir dos JSONs
    └── validate_database.py        # Valida estrutura e dados do banco
```

## 🔧 Ferramentas Disponíveis

### 🔍 Verificador de Dependências (`check_dependencies.py`)

Verifica se todas as dependências necessárias estão instaladas no sistema.

**Uso:**
```bash
# Via Makefile (recomendado)
make check-deps

# Ou diretamente
python3 tools/database/check_dependencies.py
```

**Recursos:**
- Verifica Python 3.7+, Docker, Docker Compose, Make, psycopg2
- Compatível com Linux, macOS e Windows
- Fornece instruções de instalação específicas por SO
- Testa conectividade com Docker daemon

### 📊 Gerador de SQL (`generate_sql_from_json.py`)

Converte os arquivos JSON em comandos SQL INSERT.

**Uso:**
```bash
# Via Makefile (recomendado)
make generate-sql-data

# Ou diretamente
python3 tools/database/generate_sql_from_json.py

# Com diretório customizado
python3 tools/database/generate_sql_from_json.py /path/to/json/files
```

**Saída:** `database/seeds/init-data.sql`

### ✅ Validador de Banco (`validate_database.py`)

Verifica se o banco foi criado e populado corretamente.

**Uso:**
```bash
# Via Makefile (recomendado)
make validate-db

# Ou diretamente
python3 tools/database/validate_database.py
```

**Recursos:**
- Verifica existência de todas as tabelas
- Conta registros em cada tabela
- Valida integridade de chaves estrangeiras
- Detecta duplicatas e inconsistências

## 📦 Dependências

```bash
# Instalar dependências Python
pip install psycopg2-binary pathlib
```

## 🔗 Integração com Makefile

Estes scripts são integrados ao Makefile principal:

- `make generate-sql-data` - Executa geração de SQL
- `make validate-db` - Executa validação do banco
- `make db-only-up` - Sobe banco e gera SQL automaticamente