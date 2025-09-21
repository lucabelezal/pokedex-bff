# ==============================================================================
# Variáveis de Configuração
# ==============================================================================
DOCKER_COMPOSE_FILE = docker/docker-compose.dev.yml
JACOCO_REPORT_PATH = build/reports/jacoco/test/html/index.html

# ==============================================================================
# Comandos PHONY
# ==============================================================================
.PHONY: help dev-setup dev-setup-for-windows start-db stop-db clean-db load-data clean-bff run-bff clean-all force-remove-db-container deep-clean-gradle \
		test test-class open-jacoco-report generate-sql-data db-bootstrap dev-db-up dev-db-down dev-db-clean dev-db-shell prod-up prod-down prod-clean prod-shell

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
	@echo ""
	@echo "  make generate-sql-data        - Gera o SQL de dados a partir dos JSONs (src/main/resources/data)"
	@echo "  make db-bootstrap             - Gera o SQL e sobe o ambiente completo (DB + BFF)"
	@echo ""
	@echo "  O comando 'db-bootstrap' executa tudo: gera o SQL, inicializa o banco e sobe o BFF já pronto para uso."
	@echo "  Útil para ambientes limpos, CI/CD ou onboarding rápido."
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
		echo "Por favor, abra manualmente: $(JACOCO_REPORT_PATH); \
	fi

# ==============================================================================
# Novo fluxo: Geração e carga de dados SQL a partir dos JSONs
# ==============================================================================

# Gera o arquivo docker/db/init-data.sql a partir dos JSONs
# Uso: make generate-sql-data
# Requer: Python 3
#
generate-sql-data:
	@echo "[INFO] Gerando docker/db/init-data.sql a partir dos JSONs..."
	python3 scripts/json2sql.py

# Sobe o ambiente completo (gera SQL, sobe DB e BFF)
# Uso: make db-bootstrap
#
db-bootstrap: generate-sql-data
	@echo "[INFO] Gerando JAR do BFF com Gradle..."
	./gradlew clean build
	@echo "[INFO] Subindo ambiente completo (DB + BFF) com Docker Compose..."
	docker-compose up --build

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

# ======================================================================
# Ambiente de Desenvolvimento Local
# ======================================================================
# make dev-db-up         - Sobe apenas o banco de dev (porta 5433, volume isolado)
# make dev-db-down       - Para e remove o banco de dev
# make dev-db-clean      - Remove banco de dev e volume (apaga dados)
# make dev-db-shell      - Abre um shell psql no banco de dev

# Sobe apenas o banco de dev
# Uso: make dev-db-up
#
dev-db-up:
	docker compose -f docker/docker-compose.dev.yml up -d
	@echo "[INFO] Gerando arquivo SQL a partir dos JSONs..."
	python3 scripts/json_to_sql.py
	@echo "[INFO] Arquivo SQL gerado. Subindo logs do banco de dados para verificar importação..."
	docker compose -f docker/docker-compose.dev.yml logs -f db

# Para e remove o banco de dev
# Uso: make dev-db-down
#
dev-db-down:
	docker compose -f docker/docker-compose.dev.yml down

# Remove banco de dev e volume (apaga dados)
# Uso: make dev-db-clean
#
dev-db-clean:
	docker compose -f docker/docker-compose.dev.yml down -v --remove-orphans

# Abre um shell psql no banco de dev
# Uso: make dev-db-shell
#
dev-db-shell:
	PGPASSWORD=postgres psql -h localhost -U postgres -p 5433 -d pokedex_dev_db

# ======================================================================
# Ambiente de Produção/Deploy
# ======================================================================
# make prod-up           - Sobe todo o ambiente de produção (DB + BFF)
# make prod-down         - Para e remove containers de prod
# make prod-clean        - Remove containers e volumes de prod (apaga dados)
# make prod-shell        - Abre um shell psql no banco de prod

prod-up: db-bootstrap

prod-down:
	docker-compose down

prod-clean:
	docker-compose down -v --remove-orphans

prod-shell:
	PGPASSWORD=pokedex psql -h localhost -U pokedex -p 5432 -d pokedex

clean-docker:
	@docker-compose -f docker/docker-compose.dev.yml down -v --remove-orphans
	@docker volume prune -f
	@echo "Volumes e containers removidos com sucesso!"
