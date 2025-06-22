# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml

# ==============================================================================
# Comandos PHONY
# ==============================================================================
.PHONY: help dev-setup dev-setup-for-windows start-db stop-db clean-db load-data clean-bff run-bff clean-all force-remove-db-container deep-clean-gradle

# ==============================================================================
# Ajuda
# ==============================================================================
help:
	@echo "==================================================================="
	@echo "                 Comandos do Makefile para Pokedex BFF             "
	@echo "==================================================================="
	@echo "  make help                   - Exibe esta mensagem de ajuda."
	@echo ""
	@echo "  make dev-setup              - Configura e inicia o ambiente (Linux/macOS)."
	@echo "  make dev-setup-for-windows - Configura e inicia o ambiente (Git Bash/WSL no Windows)."
	@echo ""
	@echo "  make start-db               - Inicia o banco PostgreSQL com Docker Compose."
	@echo "  make stop-db                - Para o contêiner do banco."
	@echo "  make clean-db               - Remove o banco e os volumes (apaga os dados!)."
	@echo "  make load-data              - Executa o BFF e carrega os dados JSON com o profile DEV.."
	@echo "  make run-bff                - Executa o BFF sem importar dados com o profile DEV.."
	@echo "  make clean-bff              - Executa './gradlew clean'."
	@echo ""
	@echo "  make clean-all              - Para tudo, limpa DB, Gradle e contêineres."
	@echo "  make force-remove-db-container - Força a remoção do contêiner 'pokedex-db'."
	@echo "  make deep-clean-gradle      - Limpa caches e artefatos do Gradle."
	@echo "==================================================================="

# ==============================================================================
# Banco de Dados
# ==============================================================================

start-db:
	@echo "--- Iniciando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f $(DOCKER_COMPOSE_FILE) up -d db
	@echo "Aguardando alguns segundos para o banco de dados inicializar..."
	@sleep 5
	@echo "Banco de dados iniciado. Verifique os logs do contêiner 'pokedex-db'."

stop-db:
	@echo "--- Parando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f $(DOCKER_COMPOSE_FILE) stop db

clean-db:
	@echo "--- Removendo o contêiner do DB e volumes de dados (APAGANDO DADOS) ---"
	docker compose -f $(DOCKER_COMPOSE_FILE) down -v --remove-orphans

# ==============================================================================
# BFF - Spring Boot / Gradle
# ==============================================================================

clean-bff:
	@echo "--- Limpando o projeto BFF (gradle clean) ---"
	./gradlew clean

run-bff:
	@echo "--- Iniciando o BFF no profile DEV ---"
	./gradlew bootRun --args='--spring.profiles.active=dev'

load-data: start-db
	@echo "--- Iniciando o BFF (profile DEV) e carregando dados JSON no DB ---"
	./gradlew bootRun --args='--spring.profiles.active=dev'

# ==============================================================================
# Orquestração Completa (Linux/macOS)
# ==============================================================================

dev-setup:
	@echo "--- Iniciando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f docker/docker-compose.dev.yml up -d db
	@echo "Aguardando alguns segundos para o banco de dados inicializar..."
	sleep 5
	@echo "Banco de dados iniciado. Verifique os logs do contêiner 'pokedex-db'."
	@echo "--- Iniciando o BFF (profile DEV) e carregando dados JSON no DB ---"
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
	@echo "--- Iniciando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f docker\docker-compose.dev.yml up -d db
	@echo "Aguardando alguns segundos para o banco de dados inicializar..."
	sleep 5
	@echo "Banco de dados iniciado. Verifique os logs do contêiner 'pokedex-db'."
	@echo "--- Iniciando o BFF (profile DEV) e carregando dados JSON no DB ---"
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
