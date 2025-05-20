#!/bin/bash
set -e

# Aguardar PostgreSQL estar pronto
until PGPASSWORD=$POSTGRES_PASSWORD psql -h "postgres-db" -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c '\q'; do
  >&2 echo "PostgreSQL não está disponível - aguardando..."
  sleep 1
done

# Executar operações baseadas nas variáveis
case "$DB_OPERATION" in
  create)
    if [ "$DB_RESET" = "true" ]; then
      psql -h postgres-db -U $POSTGRES_USER -d $POSTGRES_DB -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
    fi
    psql -h postgres-db -U $POSTGRES_USER -d $POSTGRES_DB -f /scripts/01-create-tables.sql
    psql -h postgres-db -U $POSTGRES_USER -d $POSTGRES_DB -f /scripts/02-import-data.sql
    ;;

  update)
    # Executar migrações
    for migration in /scripts/migrations/*.sql; do
      psql -h postgres-db -U $POSTGRES_USER -d $POSTGRES_DB -f "$migration"
    done
    ;;

  delete)
    read -p "Tem certeza que deseja apagar todos os dados? (s/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Ss]$ ]]; then
      psql -h postgres-db -U $POSTGRES_USER -d $POSTGRES_DB -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"
    fi
    ;;
esac