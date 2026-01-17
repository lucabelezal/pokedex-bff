# ==============================================================================
# Makefile - Pokedex BFF
# ==============================================================================
# Ambiente de desenvolvimento Kotlin + Spring Boot + PostgreSQL
# Segue padrões da comunidade e boas práticas de DevOps
# ==============================================================================

.DEFAULT_GOAL := help
.PHONY: help

# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_DB_ONLY := docker/docker-compose.db-only.yml
DOCKER_COMPOSE_DEV := docker/docker-compose.dev.yml
JACOCO_REPORT := build/reports/jacoco/test/html/index.html
SWAGGER_URL := http://localhost:8080/swagger-ui/index.html

# Detecção automática dos comandos Docker
DOCKER_CMD := $(shell python3 tools/database/detect_docker_commands.py docker 2>/dev/null || echo "docker")
DOCKER_COMPOSE_CMD := $(shell python3 tools/database/detect_docker_commands.py docker-compose 2>/dev/null || echo "docker compose")

# ==============================================================================
# Help - Exibe todos os comandos disponíveis
# ==============================================================================
help:
	@echo "==================================================================="
	@echo "           📦 Pokedex BFF - Makefile Commands                      "
	@echo "==================================================================="
	@echo ""
	@echo "🚀 QUICK START:"
	@echo "  make setup          - Setup completo (deps + banco + dados)"
	@echo "  make dev            - Inicia desenvolvimento (banco + BFF local)"
	@echo "  make test           - Executa testes com cobertura"
	@echo ""
	@echo "🗄️  BANCO DE DADOS:"
	@echo "  make db-up          - Sobe banco isolado (porta 5434)"
	@echo "  make db-down        - Para banco"
	@echo "  make db-restart     - Reinicia banco"
	@echo "  make db-shell       - Conecta ao banco via psql"
	@echo "  make db-clean       - Remove banco e volumes (⚠️  apaga dados)"
	@echo "  make db-info        - Mostra configurações de conexão"
	@echo ""
	@echo "🏗️  BUILD E EXECUÇÃO:"
	@echo "  make build          - Compila o projeto"
	@echo "  make run            - Executa BFF localmente"
	@echo "  make clean          - Limpa build artifacts"
	@echo ""
	@echo "📊 DADOS:"
	@echo "  make generate-data  - Gera SQL a partir dos JSONs"
	@echo "  make validate-db    - Valida estrutura do banco"
	@echo ""
	@echo "🧪 TESTES:"
	@echo "  make test           - Executa testes + JaCoCo"
	@echo "  make test-class CLASS=Nome  - Testa classe específica"
	@echo "  make coverage       - Abre relatório de cobertura"
	@echo ""
	@echo "🔍 QUALIDADE DE CÓDIGO:"
	@echo "  make lint           - Executa ktlint + detekt"
	@echo "  make lint-fix       - Corrige problemas de formatação"
	@echo ""
	@echo "📚 DOCUMENTAÇÃO:"
	@echo "  make swagger        - Abre Swagger UI"
	@echo ""
	@echo "🧹 LIMPEZA:"
	@echo "  make clean-all      - Limpa tudo (build + containers + volumes)"
	@echo "  make clean-docker   - Remove apenas containers e volumes"
	@echo ""
	@echo "🛠️  UTILITÁRIOS:"
	@echo "  make kill-port      - Mata processo na porta 8080"
	@echo "  make check-deps     - Verifica dependências do sistema"
	@echo "  make status         - Mostra status dos serviços"
	@echo "==================================================================="

# ==============================================================================
# Setup Inicial
# ==============================================================================
setup: check-deps generate-data db-up
	@echo "✅ Setup completo! Use 'make run' para iniciar o BFF."

check-deps:
	@echo "🔍 Verificando dependências..."
	@python3 tools/database/check_dependencies.py

# ==============================================================================
# Desenvolvimento
# ==============================================================================
dev: db-up
	@echo "🚀 Iniciando ambiente de desenvolvimento..."
	@$(MAKE) run

run: check-db-running
	@echo "🔄 Iniciando BFF..."
	@./gradlew bootRun --args='--spring.profiles.active=dev'

build:
	@echo "🏗️  Compilando projeto..."
	@./gradlew clean build -x test

clean:
	@echo "🧹 Limpando build artifacts..."
	@./gradlew clean

# ==============================================================================
# Banco de Dados
# ==============================================================================
db-up: generate-data
	@echo "🔄 Subindo banco de dados..."
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_DB_ONLY) up -d
	@echo "⏳ Aguardando banco inicializar..."
	@sleep 8
	@echo "✅ Banco disponível em localhost:5434"

db-down:
	@echo "🛑 Parando banco de dados..."
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_DB_ONLY) down

db-restart: db-down db-up
	@echo "✅ Banco reiniciado"

db-clean:
	@echo "⚠️  Removendo banco e volumes (isso apagará os dados)..."
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_DB_ONLY) down -v --remove-orphans
	@echo "✅ Banco removido"

db-shell: check-db-running
	@echo "🔄 Conectando ao banco..."
	@PGPASSWORD=postgres psql -h localhost -U postgres -p 5434 -d pokedex_dev_db

db-info:
	@echo "==================================================================="
	@echo "           📊 Informações de Conexão - PostgreSQL"
	@echo "==================================================================="
	@echo "Host:      localhost"
	@echo "Porta:     5434"
	@echo "Database:  pokedex_dev_db"
	@echo "Usuário:   postgres"
	@echo "Senha:     postgres"
	@echo ""
	@echo "🔗 JDBC URL:"
	@echo "jdbc:postgresql://localhost:5434/pokedex_dev_db"
	@echo ""
	@echo "📊 Comandos úteis:"
	@echo "  make db-shell    - Conecta via psql"
	@echo "  make validate-db - Valida estrutura"
	@echo "==================================================================="

# ==============================================================================
# Dados
# ==============================================================================
generate-data:
	@echo "📊 Gerando SQL a partir dos JSONs..."
	@python3 tools/database/generate_sql_from_json.py

validate-db: check-db-running
	@echo "🔍 Validando estrutura do banco..."
	@python3 tools/database/validate_database.py

# ==============================================================================
# Testes
# ==============================================================================
test:
	@echo "🧪 Executando testes..."
	@./gradlew test jacocoTestReport
	@echo "✅ Testes concluídos! Use 'make coverage' para ver o relatório."

test-class:
ifndef CLASS
	@echo "❌ Erro: especifique CLASS=NomeDaClasse"
	@exit 1
endif
	@echo "🧪 Testando classe $(CLASS)..."
	@./gradlew test --tests $(CLASS)

coverage:
	@if [ -f $(JACOCO_REPORT) ]; then \
		echo "📊 Abrindo relatório de cobertura..."; \
		if command -v open > /dev/null; then \
			open $(JACOCO_REPORT); \
		elif command -v xdg-open > /dev/null; then \
			xdg-open $(JACOCO_REPORT); \
		else \
			echo "❌ Não foi possível abrir automaticamente."; \
			echo "Abra manualmente: $(JACOCO_REPORT)"; \
		fi; \
	else \
		echo "❌ Relatório não encontrado. Execute 'make test' primeiro."; \
	fi

# ==============================================================================
# Lint
# ==============================================================================
lint:
	@echo "🔍 Executando lint..."
	@./gradlew ktlintCheck detekt

lint-fix:
	@echo "🔧 Corrigindo formatação..."
	@./gradlew ktlintFormat

# ==============================================================================
# Documentação
# ==============================================================================
swagger: check-db-running
	@echo "📖 Verificando se BFF está rodando..."
	@if ! curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then \
		echo "⚠️  BFF não está rodando. Execute 'make run' primeiro."; \
		exit 1; \
	fi
	@echo "📖 Abrindo Swagger UI..."
	@if command -v open > /dev/null; then \
		open $(SWAGGER_URL); \
	elif command -v xdg-open > /dev/null; then \
		xdg-open $(SWAGGER_URL); \
	else \
		echo "Abra manualmente: $(SWAGGER_URL)"; \
	fi

# ==============================================================================
# Limpeza
# ==============================================================================
clean-all: clean db-clean clean-docker
	@echo "✅ Limpeza completa realizada"

clean-docker:
	@echo "🧹 Removendo containers Docker..."
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_DB_ONLY) down -v --remove-orphans 2>/dev/null || true
	@$(DOCKER_COMPOSE_CMD) -f $(DOCKER_COMPOSE_DEV) down -v --remove-orphans 2>/dev/null || true
	@docker volume prune -f
	@echo "✅ Containers removidos"

# ==============================================================================
# Utilitários
# ==============================================================================
kill-port:
	@echo "🔎 Verificando porta 8080..."
	@if lsof -i :8080 | grep LISTEN; then \
		PID=$$(lsof -ti :8080); \
		echo "⚠️  Processo encontrado: PID=$$PID"; \
		kill -9 $$PID; \
		echo "✅ Processo finalizado."; \
	else \
		echo "✅ Porta 8080 livre."; \
	fi

status:
	@echo "📊 Status dos Serviços:"
	@echo "━━━━━━━━━━━━━━━━━━━━━━"
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
	@echo "━━━━━━━━━━━━━━━━━━━━━━"

# ==============================================================================
# Funções Helper (privadas)
# ==============================================================================
check-db-running:
	@if ! $(DOCKER_CMD) ps | grep -q "pokedex.*db"; then \
		echo "❌ Banco não está rodando!"; \
		echo "💡 Execute 'make db-up' primeiro."; \
		exit 1; \
	fi

