-- Importação ordenada por dependências
\copy pokemon FROM '/csv/pokemon.csv' DELIMITER ',' CSV HEADER;

-- Atualizar sequences após importação
SELECT setval('pokemon_id_seq', (SELECT MAX(id) FROM pokemon));