-- Exemplo de migration inicial para tabela de Pokémon
CREATE TABLE IF NOT EXISTS pokemon (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    level INT NOT NULL
);
