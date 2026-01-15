-- Exemplo de migration inicial para tabela de Pokémon
-- NOTE (Copilot AI): SQL syntax error was reported previously: trailing comma
-- after the last column definition (e.g. 'type VARCHAR(255) NOT NULL,').
-- The trailing comma was removed to fix the syntax error.
CREATE TABLE IF NOT EXISTS pokemon (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL
);
