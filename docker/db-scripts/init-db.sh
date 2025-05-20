#!/bin/bash
set -e

# Executar scripts em ordem
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    \i /docker-entrypoint-initdb.d/create-tables.sql
    \i /docker-entrypoint-initdb.d/import-data.sql
EOSQL

# Aplicar migrações se existirem
if [ -d "/docker-entrypoint-initdb.d/migrations" ]; then
    for migration in /docker-entrypoint-initdb.d/migrations/*.sql; do
        psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f "$migration"
    done
fi