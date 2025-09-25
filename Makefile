# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml
JACOCO_REPORT_PATH = build/reports/jacoco/test/html/index.html

# Detecção automática dos comandos Docker
DOCKER_CMD := $(shell python3 tools/database/detect_docker_commands.py docker 2>/dev/null || echo "docker")
DOCKER_COMPOSE_CMD := $(shell python3 tools/database/detect_docker_commands.py docker-compose 2>/dev/null || echo "docker compose")

# ==============================================================================
# Funções Helper para Verificação do Banco
# ==============================================================================


# Verifica especificamente se o banco isolado (porta 5434) está rodando
check-isolated-database:
	@if ! $(DOCKER_CMD) ps | grep -q "pokedx-bff-db-standalone"; then \
		echo "⚠️  BANCO ISOLADO NÃO DETECTADO (porta 5434)"; \
		echo "   Este comando requer o banco isolado."; \
		echo ""; \
		echo "🚀 Deseja subir o banco isolado agora? (Y/n)"; \
		read confirm; \
		case "$$confirm" in \
			[nN]|[nN][oO]) \
				echo "❌ Operação cancelada. Para subir o banco isolado:"; \
				echo "   make db-only-up"; \
				exit 1; \
				;; \
			*) \
				echo "📦 Subindo banco isolado..."; \
				$(MAKE) db-only-up; \
				;; \
		esac \
	else \
		echo "✅ Banco isolado detectado - prosseguindo..."; \
	fi

# Verifica se o ambiente de desenvolvimento completo está rodando
check-dev-database:
	@if ! $(DOCKER_CMD) ps | grep -q "pokedex-bff-db-dev"; then \
		echo "⚠️  BANCO DE DESENVOLVIMENTO NÃO DETECTADO"; \
		echo "   Este comando requer o ambiente de desenvolvimento."; \
		echo ""; \
		echo "🚀 Deseja subir o ambiente de desenvolvimento agora? (Y/n)"; \
		read confirm; \
		case "$$confirm" in \
			[nN]|[nN][oO]) \
				echo "❌ Operação cancelada. Para subir o ambiente de dev:"; \
				echo "   make dev-db-up"; \
				exit 1; \
				;; \
			*) \
				echo "📦 Subindo ambiente de desenvolvimento..."; \
				$(MAKE) dev-db-up; \
				;; \
		esac \
	else \
		echo "✅ Ambiente de desenvolvimento detectado - prosseguindo..."; \
	fi


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
	@echo "  make run-bff                - Executa o BFF (verifica/sobe banco automaticamente)."
	@echo ""
	@echo "📊 SQL E DADOS:"
	@echo "  make generate-sql-data      - Gera init-data.sql a partir dos JSONs."
	@echo "  make install-db-deps        - Instala dependências Python para validação do banco."
	@echo "  make validate-db            - Valida estrutura e dados (verifica/sobe banco automaticamente)."
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
	@echo "  make open-swagger           - Abre Swagger (verifica/sobe banco e BFF automaticamente)."
	@echo ""
	@echo "⚡ COMANDOS OTIMIZADOS:"
	@echo "  make dev-up                 - Inicia ambiente completo (banco + BFF)."
	@echo "  make dev-down               - Para ambiente de desenvolvimento."
	@echo "  make db-refresh             - Atualiza dados do banco (recria com dados frescos)."
	@echo "  make bff-only               - Executa apenas BFF (verifica/sobe banco automaticamente)."
	@echo "  make dev-status             - Mostra status dos serviços."
	@echo "  make dev-logs               - Exibe logs em tempo real."
	@echo ""
	@echo "💡 FLUXO RECOMENDADO:"
	@echo "  1. make check-deps          (verifica dependências)"
	@echo "  2. make dev-up              (sobe ambiente completo)"
	@echo "  3. make test                (executa testes)"
	@echo "  4. make dev-status          (verifica se tudo está ok)"
	@echo "==================================================================="

# ============================================================================== 
# Variáveis de Configuração
# ==============================================================================
	@echo "  4. make dev-status          (verifica se tudo está ok)"
	@echo "==================================================================="

# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
	@echo "  4. make dev-status          (verifica se tudo está ok)"
	@echo "==================================================================="

# ==============================================================================
# Documentação da API (Swagger)
# ==============================================================================
SWAGGER_URL = http://localhost:8080/swagger-ui/index.html

open-swagger: check-database
	@echo "🚀 Verificando se o BFF está rodando..."
	@if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then \
		echo "⚠️  BFF não está rodando. Iniciando..."; \
		$(MAKE) run-bff & \
		echo "⏳ Aguardando BFF inicializar..."; \
		sleep 15; \
	fi
	@echo "📖 Abrindo Swagger UI no navegador: $(SWAGGER_URL)"
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

# ==============================================================================
# Comandos de Desenvolvimento Otimizados
# ==============================================================================

dev-up:
	@echo "🚀 INICIANDO AMBIENTE DE DESENVOLVIMENTO COMPLETO..."
	@echo "   📦 Subindo banco + BFF com recarga automática"
	@$(MAKE) check-deps
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_FILE) up -d db
	@echo "⏳ Aguardando banco ficar disponível..."
	@sleep 5
	@$(MAKE) run-bff

dev-down:
	@echo "🛑 PARANDO AMBIENTE DE DESENVOLVIMENTO..."
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_FILE) down
	@echo "✅ Ambiente de desenvolvimento parado."

db-refresh:
	@echo "🔄 ATUALIZANDO DADOS DO BANCO..."
	@echo "   ⚠️  Isso irá recriar o banco com dados frescos!"
	@read -p "Tem certeza? (y/N): " confirm; \
	if [ "$$confirm" = "y" ] || [ "$$confirm" = "Y" ]; then \
		$(MAKE) db-only-clean; \
		$(MAKE) generate-sql-data; \
		$(MAKE) db-only-up; \
		echo "✅ Banco atualizado com dados frescos!"; \
	else \
		echo "❌ Operação cancelada."; \
	fi

bff-only:
	@echo "🏗️  EXECUTANDO APENAS O BFF..."
	@$(MAKE) check-database
	@$(MAKE) run-bff

dev-status:
	@echo "📊 STATUS DO AMBIENTE DE DESENVOLVIMENTO:"
	@echo "----------------------------------------"
	@if $(DOCKER_CMD) ps | grep -q "pokedex.*db"; then \
		echo "✅ Banco: RODANDO"; \
	else \
		echo "❌ Banco: PARADO"; \
	fi
	@if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then \
		echo "✅ BFF: RODANDO (http://localhost:8080)"; \
	else \
		echo "❌ BFF: PARADO"; \
	fi
	@echo "----------------------------------------"

dev-logs:
	@echo "📋 LOGS DO AMBIENTE DE DESENVOLVIMENTO:"
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_FILE) logs -f

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

run-bff: check-database
	@echo "🔄 Iniciando o BFF..."
	./gradlew bootRun --args='--spring.profiles.active=dev'




# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml
JACOCO_REPORT_PATH = build/reports/jacoco/test/html/index.html

# Detecção automática dos comandos Docker
DOCKER_CMD := $(shell python3 tools/database/detect_docker_commands.py docker 2>/dev/null || echo "docker")
DOCKER_COMPOSE_CMD := $(shell python3 tools/database/detect_docker_commands.py docker-compose 2>/dev/null || echo "docker compose")

# ==============================================================================
# Funções Helper para Verificação do Banco
# ==============================================================================

# Verifica se o banco está rodando e oferece para subir se não estiver
check-database:
	@if ! $(DOCKER_CMD) ps | grep -q "pokedex.*db"; then \

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
validate-db: check-database
	@echo "🔍 Validando estrutura e dados do banco..."
	python3 tools/database/validate_database.py
	@echo "✅ Validação concluída!"

# ==============================================================================
# Banco de Dados Isolado (apenas para testes do banco)
# ==============================================================================

# Sobe apenas o banco com dados pré-carregados (teste isolado)
db-only-up: generate-sql-data
	@echo "🔄 Subindo banco de dados isolado..."
	$(DOCKER_COMPOSE_CMD) -f docker/docker-compose.db-only.yml up -d
	@echo "⏳ Aguardando inicialização do banco..."
	@sleep 10
	@echo "📋 Verificando logs de inicialização:"
	$(DOCKER_COMPOSE_CMD) -f docker/docker-compose.db-only.yml logs db
	@echo "✅ Banco isolado disponível em localhost:5434"

# Para o banco isolado
db-only-down:
	@echo "🔄 Parando banco isolado..."
	$(DOCKER_COMPOSE_CMD) -f docker/docker-compose.db-only.yml down

# Reinicia o banco isolado
db-only-restart: db-only-down db-only-up

# Remove banco isolado e volumes
db-only-clean:
	@echo "🔄 Removendo banco isolado e volumes..."
	$(DOCKER_COMPOSE_CMD) -f docker/docker-compose.db-only.yml down -v --remove-orphans
	@echo "✅ Banco isolado removido"

# Abre shell psql no banco isolado
db-only-shell: check-isolated-database
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
	@$(MAKE) check-deps
	@echo "📊 Gerando dados SQL..."
	python3 tools/database/generate_sql_from_json.py
	@echo "🔄 Subindo banco de dados..."
	$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_FILE) up -d db
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
dev-db-shell: check-dev-database
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
