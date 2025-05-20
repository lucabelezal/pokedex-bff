run-dev:
	docker compose -f docker/docker-compose.dev.yml up -d

stop-dev:
	docker compose -f docker/docker-compose.dev.yml down

logs-db:
	docker logs pokedex-db

build:
	./gradlew build
