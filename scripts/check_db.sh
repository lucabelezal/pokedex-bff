#!/bin/bash

# Nome do container do banco de dados
db_container="pokedex-bff-db-dev"

# Variáveis de conexão ao banco de dados
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_NAME="pokedex_dev_db"
DB_PORT=5434

# Detecta o comando timeout correto para o sistema operacional
if [[ "$(uname)" == "Darwin" ]]; then
  TIMEOUT_CMD="gtimeout"
else
  TIMEOUT_CMD="timeout"
fi

# Função para aguardar o container do banco de dados estar pronto
wait_for_db() {
  echo "Aguardando o banco de dados ficar pronto..."
  # Atualiza o comando para usar a porta correta
  $TIMEOUT_CMD 60 bash -c 'until pg_isready -h localhost -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" > /dev/null 2>&1; do
    echo "Banco ainda não está pronto, aguardando..."
    sleep 2
  done'

  if [ $? -eq 0 ]; then
    echo "Banco de dados está pronto."
  else
    echo "Erro: Timeout ao aguardar o banco de dados."
    exit 1
  fi
}

# Função para verificar as tabelas no banco de dados
check_tables() {
  echo "Verificando tabelas no banco de dados..."
  docker exec -e PGPASSWORD="$DB_PASSWORD" "$db_container" \
    psql -U "$DB_USER" -d "$DB_NAME" -c "\\dt" | grep -E "(region|type|egg_group|generation|ability|species|stats|evolution_chains|weaknesses|pokemon)"

  if [ $? -eq 0 ]; then
    echo "Sucesso: Todas as tabelas foram criadas corretamente."
  else
    echo "Erro: Algumas tabelas estão faltando ou não foram criadas."
  fi
}

# Função para capturar erros nos logs do container
check_logs() {
  echo "Capturando erros nos logs do container..."
  docker logs "$db_container" 2>&1 | grep -i "error"

  if [ $? -eq 0 ]; then
    echo "Erro(s) encontrado(s) nos logs do container."
  else
    echo "Nenhum erro encontrado nos logs do container."
  fi
}

# Executa as funções
wait_for_db
check_tables
check_logs

echo "Sequência de verificação concluída."
