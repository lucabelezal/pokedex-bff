# Makefile na raiz do seu projeto

# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
# Caminho para o arquivo Docker Compose de desenvolvimento
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml

# ==============================================================================
# Comandos PHONY (Garante que os targets são comandos, não arquivos)
# ==============================================================================
.PHONY: help setup-dev start-db stop-db clean-db load-data clean-bff run-bff clean-all force-remove-db-container deep-clean-gradle

# ==============================================================================
# Comandos de Ajuda
# ==============================================================================
help:
	@echo "==================================================================="
	@echo "                 Comandos do Makefile para Pokedex BFF             "
	@echo "==================================================================="
	@echo "  make help                   - Exibe esta mensagem de ajuda."
	@echo ""
	@echo "  make setup-dev              - Configura e inicia o ambiente de desenvolvimento completo:"
	@echo "                                  1. Inicia o PostgreSQL via Docker Compose."
	@echo "                                  2. Aguarda o DB estar acessível."
	@echo "                                  3. Inicia o BFF, que popula o DB com os JSONs." # <-- Corrigido
	@echo ""
	@echo "  make start-db               - Apenas inicia o contêiner do banco de dados PostgreSQL."
	@echo "  make stop-db                - Para o contêiner do banco de dados."
	@echo "  make clean-db               - Remove o contêiner do DB e seus volumes de dados (CUIDADO: APAGA OS DADOS!)."
	@echo ""
	@echo "  make load-data              - Inicia o BFF para carregar os dados JSON no DB." # <-- Corrigido
	@echo "                                  (Requer que o DB já esteja rodando e acessível)."
	@echo "  make clean-bff              - Limpa o projeto BFF (executa './gradlew clean')."
	@echo "  make run-bff                - Inicia o BFF (sem carregar dados automaticamente, a menos que o perfil 'dev' esteja ativo)."
	@echo ""
	@echo "  make clean-all              - Limpa o ambiente de desenvolvimento completo:"
	@echo "                                  1. Para o DB. 2. Remove o DB e seus volumes. 3. Limpa o BFF (incluindo caches do Gradle)."
	@echo "  make force-remove-db-container - Força a remoção de um contêiner 'pokedex-db' órfão ou travado."
	@echo "  make deep-clean-gradle      - Realiza uma limpeza profunda dos caches e artefatos do Gradle."
	@echo "==================================================================="

# ==============================================================================
# Comandos do Banco de Dados (Docker Compose)
# ==============================================================================

start-db:
	@echo "--- Iniciando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f $(DOCKER_COMPOSE_FILE) up -d db
	@echo "Aguardando alguns segundos para o banco de dados inicializar..."
	@sleep 5 # Pequeno atraso para dar tempo ao DB de iniciar antes que o BFF tente conectar
	@echo "Banco de dados iniciado. Verifique os logs do contêiner 'pokedex-db' para status detalhado."

stop-db:
	@echo "--- Parando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f $(DOCKER_COMPOSE_FILE) stop db

clean-db:
	@echo "--- Removendo o contêiner do DB e volumes de dados (APAGANDO DADOS) ---"
	docker compose -f $(DOCKER_COMPOSE_FILE) down -v --remove-orphans # Adicionado --remove-orphans para limpeza completa

# ==============================================================================
# Comandos do BFF (Spring Boot/Kotlin)
# ==============================================================================

clean-bff:
	@echo "--- Limpando o projeto BFF (gradle clean) ---"
	./gradlew clean

load-data: start-db
	@echo "--- Iniciando o BFF e carregando dados JSON no DB ---" # <-- Corrigido
	./gradlew bootRun --args='--spring.profiles.active=dev'

run-bff:
	@echo "--- Iniciando o BFF ---"
	./gradlew bootRun

# ==============================================================================
# Comandos de Orquestração Completa
# ==============================================================================

setup-dev:
	@echo "--- Iniciando o contêiner do banco de dados PostgreSQL ---"
	docker compose -f docker/docker-compose.dev.yml up -d db
	@echo "Aguardando alguns segundos para o banco de dados inicializar..."
	sleep 5 # Ajuste conforme necessário
	@echo "Banco de dados iniciado. Verifique os logs do contêiner 'pokedex-db' para status detalhado."
	@echo "--- Iniciando o BFF e carregando dados JSON no DB ---" # <-- Corrigido
	./gradlew bootRun --args='--spring.profiles.active=dev'

clean-all: deep-clean-gradle stop-db clean-db
	@echo "==================================================================="
	@echo " Todos os contêineres, volumes e builds limpos. "
	@echo "==================================================================="

force-remove-db-container:
	@echo "--- Forçando a parada e remoção do contêiner 'pokedex-db' ---"
	-docker stop pokedex-db || true
	-docker rm pokedex-db || true
	@echo "Contêiner 'pokedex-db' removido (se existia). Tente 'make setup-dev' novamente."

deep-clean-gradle:
	@echo "--- Realizando limpeza profunda do Gradle (incluindo caches) ---"
	./gradlew clean --refresh-dependencies --no-build-cache
	rm -rf build .gradle
	@echo "--- Limpeza profunda do Gradle concluída. ---"