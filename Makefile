# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml
JACOCO_REPORT_PATH = build/reports/jacoco/test/html/index.html

# ==============================================================================
# Comandos PHONY
# ==============================================================================
.PHONY: help dev-setup dev-setup-for-windows start-db stop-db clean-db load-data clean-bff run-bff clean-all force-remove-db-container deep-clean-gradle \
		test test-class open-jacoco-report

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
	@echo "  make test                   - Roda todos os testes e gera o relatório JaCoCo."
	@echo "  make test-class CLASS=<NomeDaClasseTeste> - Roda testes de uma classe específica e gera o relatório JaCoCo."
	@echo "                                        Ex: make test-class CLASS=PokemonServiceTest"
	@echo "  make open-jacoco-report     - Abre o relatório JaCoCo HTML no navegador."
	@echo ""
	@echo "  make clean-all              - Para tudo, limpa DB, Gradle e contêineres."
	@echo "  make force-remove-db-container - Força a remoção do contêiner 'pokedex-db'."
	@echo "  make deep-clean-gradle      - Limpa caches e artefatos do Gradle."
	@echo ""
	@echo "  make open-swagger           - Abre a documentação Swagger no navegador."
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
		echo "Por favor, abra manualmente: $(JACOCO_REPORT_PATH)"; \
	fi

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