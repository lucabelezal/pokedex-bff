# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml
JACOCO_REPORT_PATH = build/reports/jacoco/test/html/index.html

# ==============================================================================
# Comandos PHONY  
# ==============================================================================
.PHONY: help dev-setup dev-setup-for-windows start-db stop-db clean-db clean-bff run-bff clean-all force-remove-db-container deep-clean-gradle \
		test test-class open-jacoco-report generate-sql-data validate-db check-deps install-db-deps db-only-up db-only-down db-only-restart db-only-clean db-only-shell db-info \
		dev-db-up dev-db-down dev-db-clean dev-db-shell prod-up prod-down prod-clean prod-shell clean-docker

# ==============================================================================
# Ajuda
# ==============================================================================
help:
	@echo "==================================================================="
	@echo "                 Comandos do Makefile para Pokedex BFF             "
	@echo "==================================================================="
	@echo "  make help                   - Exibe esta mensagem de ajuda."
	@echo ""
	@echo "🔧 CONFIGURAÇÃO INICIAL:"
	@echo "  make check-deps             - Verifica se todas as dependências estão instaladas."
	@echo "  make dev-setup              - Configura e inicia o ambiente (Linux/macOS)."
	@echo "  make dev-setup-for-windows - Configura e inicia o ambiente (Git Bash/WSL no Windows)."
	@echo ""
	@echo "🗄️  BANCO DE DADOS (Isolado):"
	@echo "  make db-only-up             - Sobe APENAS o banco com dados pré-carregados."
	@echo "  make db-only-down           - Para o banco isolado."
	@echo "  make db-only-restart        - Reinicia o banco isolado."
	@echo "  make db-only-clean          - Remove banco isolado e volumes (apaga dados!)."
	@echo "  make db-only-shell          - Abre shell psql no banco isolado."
	@echo "  make db-info                - Exibe informações de conexão para DBeaver/pgAdmin."
	@echo ""
	@echo "🚀 DESENVOLVIMENTO COMPLETO:"
	@echo "  make dev-db-up              - Sobe banco + BFF para desenvolvimento."
	@echo "  make dev-db-down            - Para ambiente de desenvolvimento."
	@echo "  make dev-db-clean           - Remove ambiente dev e volumes."
	@echo "  make dev-db-shell           - Abre shell psql no banco de dev."
	@echo ""
	@echo "🏗️  BUILD E EXECUÇÃO:"
	@echo "  make clean-bff              - Executa './gradlew clean'."
	@echo "  make run-bff                - Executa apenas o BFF (requer banco ativo)."
	@echo ""
	@echo "📊 SQL E DADOS:"
	@echo "  make generate-sql-data      - Gera init-data.sql a partir dos JSONs."
	@echo "  make install-db-deps        - Instala dependências Python para validação do banco."
	@echo "  make validate-db            - Valida estrutura e dados do banco (requer banco ativo)."
	@echo ""
	@echo "🧪 TESTES:"
	@echo "  make test                   - Roda todos os testes e gera o relatório JaCoCo."
	@echo "  make test-class CLASS=<Nome> - Roda testes de uma classe específica."
	@echo "  make open-jacoco-report     - Abre o relatório JaCoCo HTML no navegador."
	@echo ""
	@echo "🧹 LIMPEZA:"
	@echo "  make clean-all              - Para tudo, limpa DB, Gradle e contêineres."
	@echo "  make force-remove-db-container - Força a remoção do contêiner do banco."
	@echo "  make deep-clean-gradle      - Limpa caches e artefatos do Gradle."
	@echo ""
	@echo "📚 DOCUMENTAÇÃO:"
	@echo "  make open-swagger           - Abre a documentação Swagger no navegador."
	@echo ""
	@echo "💡 FLUXO RECOMENDADO:"
	@echo "  1. make db-only-up          (testa o banco isoladamente)"
	@echo "  2. make dev-db-up           (sobe ambiente completo para desenvolvimento)"
	@echo "  3. make test                (executa testes)"
	@echo "==================================================================="

# ==============================================================================
# Documentação da API (Swagger)
# ==============================================================================
SWAGGER_URL = http://localhost:8080/swagger-ui/index.html

open-swagger:
	@echo "--- Abrindo Swagger UI no navegador: $(SWAGGER_URL) ---"
	@if command -v xdg-open > /dev/null; then \
		xdg-open $(SWAGGER_URL); \
	elif command -v open > /dev/null; then \
		open $(SWAGGER_URL); \
	elif command -v start > /dev/null; then \
		start $(SWAGGER_URL); \
	else \
		echo "Não foi possível detectar um comando para abrir URLs automaticamente."; \
		echo "Por favor, abra manualmente: $(SWAGGER_URL)"; \
	fi

# ======================================================================
# Comandos Legados (manter compatibilidade)
# ======================================================================

start-db: db-only-up
stop-db: db-only-down  
clean-db: db-only-clean
restart-db: db-only-restart

# ==============================================================================
# BFF - Spring Boot / Gradle
# ==============================================================================

clean-bff:
	@echo "--- Limpando o projeto BFF (gradle clean) ---"
	./gradlew clean

run-bff:
	@echo "🔄 Iniciando o BFF (requer banco ativo)..."
	./gradlew bootRun --args='--spring.profiles.active=dev'

# ==============================================================================
# Testes e JaCoCo
# ==============================================================================

test: clean-bff
	@echo "--- Rodando todos os testes e gerando relatório JaCoCo ---"
	./gradlew clean test jacocoTestReport
	@echo "Relatório JaCoCo gerado em: $(JACOCO_REPORT_PATH)"
	make open-jacoco-report

test-class: clean-bff
ifeq ($(CLASS),)
	@echo "ERRO: Para rodar testes de uma classe específica, use: make test-class CLASS=<NomeDaClasseTeste>"
	@exit 1
else
	@echo "--- Rodando testes da classe $(CLASS) e gerando relatório JaCoCo ---"
	./gradlew clean test --tests "*$(CLASS)*" jacocoTestReport
	@echo "Relocatório JaCoCo gerado em: $(JACOCO_REPORT_PATH)"
	make open-jacoco-report
endif

open-jacoco-report:
	@echo "--- Abrindo relatório JaCoCo HTML no navegador: $(JACOCO_REPORT_PATH) ---"
	@if [ ! -f "$(JACOCO_REPORT_PATH)" ]; then \
		echo "ERRO: Relatório JaCoCo não encontrado em $(JACOCO_REPORT_PATH). Certifique-se de ter rodado os testes primeiro."; \
		exit 1; \
	fi
	@if command -v xdg-open > /dev/null; then \
		xdg-open $(JACOCO_REPORT_PATH); \
	elif command -v open > /dev/null; then \
		open $(JACOCO_REPORT_PATH); \
	elif command -v start > /dev/null; then \
		start $(JACOCO_REPORT_PATH); \
	else \
		echo "Não foi possível detectar um comando para abrir URLs/arquivos automaticamente."; \
		echo "Por favor, abra manualmente: $(JACOCO_REPORT_PATH); \
	fi

# ==============================================================================
# Verificação de Dependências e Geração de Dados SQL
# ==============================================================================

# Verifica se todas as dependências estão instaladas
# Uso: make check-deps
check-deps:
	@echo "🔍 Verificando dependências do sistema..."
	python3 tools/database/check_dependencies.py

# Gera o arquivo docker/db/init-data.sql a partir dos JSONs
# Uso: make generate-sql-data
# Requer: Python 3
generate-sql-data:
	@echo "� Gerando init-data.sql a partir dos JSONs..."
	python3 tools/database/generate_sql_from_json.py

# Instala dependências Python para validação do banco
# Uso: make install-db-deps
install-db-deps:
	@echo "🔄 Instalando dependências Python para validação do banco..."
	pip3 install --break-system-packages psycopg2-binary
	@echo "✅ Dependências instaladas com sucesso!"

# Valida estrutura e dados do banco de dados
# Uso: make validate-db
# Requer: banco ativo (use db-only-up primeiro) e dependências (use install-db-deps primeiro)
validate-db:
	@echo "🔍 Validando estrutura e dados do banco..."
	python3 tools/database/validate_database.py
	@echo "✅ Validação concluída!"

# ==============================================================================
# Banco de Dados Isolado (apenas para testes do banco)
# ==============================================================================

# Sobe apenas o banco com dados pré-carregados (teste isolado)
db-only-up: generate-sql-data
	@echo "🔄 Subindo banco de dados isolado..."
	docker compose -f docker/docker-compose.db-only.yml up -d
	@echo "⏳ Aguardando inicialização do banco..."
	@sleep 10
	@echo "📋 Verificando logs de inicialização:"
	docker compose -f docker/docker-compose.db-only.yml logs db
	@echo "✅ Banco isolado disponível em localhost:5434"

# Para o banco isolado
db-only-down:
	@echo "🔄 Parando banco isolado..."
	docker compose -f docker/docker-compose.db-only.yml down

# Reinicia o banco isolado
db-only-restart: db-only-down db-only-up

# Remove banco isolado e volumes
db-only-clean:
	@echo "🔄 Removendo banco isolado e volumes..."
	docker compose -f docker/docker-compose.db-only.yml down -v --remove-orphans
	@echo "✅ Banco isolado removido"

# Abre shell psql no banco isolado
db-only-shell:
	@echo "🔄 Conectando ao banco isolado..."
	PGPASSWORD=postgres psql -h localhost -U postgres -p 5434 -d pokedex_dev_db

# Exibe informações de conexão para DBeaver e outras ferramentas
db-info:
	@echo "=================================================================="
	@echo "             📊 INFORMAÇÕES DE CONEXÃO DO BANCO"
	@echo "=================================================================="
	@echo "🗄️  BANCO DE DESENVOLVIMENTO (localhost:5434)"
	@echo ""
	@echo "📋 Configurações para DBeaver/DataGrip/pgAdmin:"
	@echo "   Host:      localhost"
	@echo "   Porta:     5434"
	@echo "   Database:  pokedex_dev_db"
	@echo "   Usuário:   postgres"
	@echo "   Senha:     postgres"
	@echo ""
	@echo "🔗 URL de Conexão (JDBC):"
	@echo "   jdbc:postgresql://localhost:5434/pokedex_dev_db"
	@echo ""
	@echo "📊 Tabelas principais:"
	@echo "   • regions (10 registros)"
	@echo "   • types (18 registros)"
	@echo "   • generations (10 registros)"
	@echo "   • abilities (306 registros)"
	@echo "   • species (620 registros)"
	@echo "   • pokemons (25 registros)"
	@echo "   • pokemon_types, pokemon_abilities, pokemon_weaknesses"
	@echo ""
	@echo "💡 Comandos úteis:"
	@echo "   make db-only-up     - Sobe o banco isoladamente"
	@echo "   make db-only-shell  - Conecta via psql"
	@echo "   make validate-db    - Valida estrutura e dados"
	@echo "=================================================================="

# ==============================================================================
# Orquestração Completa (Linux/macOS)
# ==============================================================================

dev-setup:
	@echo "🔄 Iniciando setup de desenvolvimento..."
	@echo "📊 Gerando dados SQL..."
	python3 tools/database/generate_sql_from_json.py
	@echo "🔄 Subindo banco de dados..."
	docker compose -f docker/docker-compose.dev.yml up -d db
	@echo "⏳ Aguardando banco inicializar..."
	@sleep 10
	@echo "🔄 Iniciando BFF..."
	./gradlew bootRun --args='--spring.profiles.active=dev'

# ==============================================================================
# Orquestração para Windows via Git Bash ou WSL
# ==============================================================================

# Comando para checar ambiente Windows e orientar instalação do Java e Gradle via Scoop
check-windows-env:
	@echo "Verificando Java e Gradle no Windows..."
	@if ! command -v java > /dev/null 2>&1; then \
		echo "Java não encontrado! Instale com:"; \
		echo "  scoop bucket add java"; \
		echo "  scoop install openjdk21"; \
		exit 1; \
	else \
		echo "Java encontrado:"; java -version; \
	fi
	@if ! command -v gradle > /dev/null 2>&1; then \
		echo "Gradle não encontrado! Instale com:"; \
		echo "  scoop install gradle"; \
		exit 1; \
	else \
		echo "Gradle encontrado:"; gradle --version; \
	fi

dev-setup-for-windows: check-windows-env
	@echo "🔄 Iniciando setup para Windows..."
	@echo "📊 Gerando dados SQL..."
	python3 tools/database/generate_sql_from_json.py
	@echo "🔄 Subindo banco de dados..."
	docker compose -f docker/docker-compose.dev.yml up -d db
	@echo "⏳ Aguardando banco inicializar..."
	sleep 10
	@echo "🔄 Iniciando BFF..."
	gradlew.bat bootRun --args='--spring.profiles.active=dev'


# ==============================================================================
# Limpeza Total
# ==============================================================================

clean-all: deep-clean-gradle stop-db clean-db
	@echo "==================================================================="
	@echo " Todos os contêineres, volumes e builds limpos. "
	@echo "==================================================================="

force-remove-db-container:
	@echo "--- Forçando a parada e remoção do contêiner 'pokedex-db' ---"
	-docker stop pokedex-db || true
	-docker rm pokedex-db || true
	@echo "Contêiner 'pokedex-db' removido (se existia). Tente 'make dev-setup' novamente."

deep-clean-gradle:
	@echo "--- Realizando limpeza profunda do Gradle (incluindo caches) ---"
	./gradlew clean --refresh-dependencies --no-build-cache
	rm -rf build .gradle
	@echo "--- Limpeza profunda do Gradle concluída. ---"

# ======================================================================
# Ambiente de Desenvolvimento Local
# ======================================================================
# make dev-db-up         - Sobe apenas o banco de dev (porta 5433, volume isolado)
# make dev-db-down       - Para e remove o banco de dev
# make dev-db-clean      - Remove banco de dev e volume (apaga dados)
# make dev-db-shell      - Abre um shell psql no banco de dev

# Sobe apenas o banco de dev
# Uso: make dev-db-up
dev-db-up: generate-sql-data
	@echo "🔄 Subindo ambiente de desenvolvimento (DB + BFF)..."
	docker compose -f docker/docker-compose.dev.yml up -d
	@echo "⏳ Aguardando inicialização..."
	@sleep 10
	@echo "📋 Verificando logs do banco:"
	docker compose -f docker/docker-compose.dev.yml logs db
	@echo "✅ Ambiente de desenvolvimento disponível - DB: localhost:5434, BFF: localhost:8081"

# Para e remove o banco de dev
# Uso: make dev-db-down
dev-db-down:
	@echo "🔄 Parando ambiente de desenvolvimento..."
	docker compose -f docker/docker-compose.dev.yml down

# Remove banco de dev e volume (apaga dados)
# Uso: make dev-db-clean
dev-db-clean:
	@echo "🔄 Removendo ambiente de desenvolvimento e volumes..."
	docker compose -f docker/docker-compose.dev.yml down -v --remove-orphans
	@echo "✅ Ambiente de desenvolvimento removido"

# Abre um shell psql no banco de dev
# Uso: make dev-db-shell
dev-db-shell:
	@echo "🔄 Conectando ao banco de desenvolvimento..."
	PGPASSWORD=postgres psql -h localhost -U postgres -p 5434 -d pokedex_dev_db

# ======================================================================
# Ambiente de Produção/Deploy (usando docker-compose.yml principal)
# ======================================================================

prod-up: generate-sql-data
	@echo "🔄 Subindo ambiente de produção..."
	./gradlew clean build
	docker compose up --build -d
	@echo "✅ Ambiente de produção disponível - DB: localhost:5432, BFF: localhost:8080"

prod-down:
	@echo "🔄 Parando ambiente de produção..."
	docker compose down

prod-clean:
	@echo "🔄 Removendo ambiente de produção e volumes..."
	docker compose down -v --remove-orphans
	@echo "✅ Ambiente de produção removido"

prod-shell:
	@echo "🔄 Conectando ao banco de produção..."
	PGPASSWORD=pokedex psql -h localhost -U pokedex -p 5432 -d pokedex

clean-docker:
	@echo "🔄 Removendo containers e volumes..."
	@docker compose -f docker/docker-compose.dev.yml down -v --remove-orphans
	@docker compose -f docker/docker-compose.db-only.yml down -v --remove-orphans
	@docker volume prune -f
	@echo "✅ Containers e volumes removidos com sucesso!"
