#!/usr/bin/env python3
"""
Script para gerar comandos INSERT SQL a partir dos arquivos JSON de dados da Pokédex.
Este script lê os arquivos JSON numerados de 01 a 10 e gera comandos SQL correspondentes.
"""

import json
import os
import sys
from typing import Dict, Any, List, Optional
from pathlib import Path

# Mapeamento de arquivos JSON para nomes de tabelas
FILE_TO_TABLE_MAPPING = {
    "01_region.json": "regions",
    "02_type.json": "types",
    "03_egg_group.json": "egg_groups",
    "04_generation.json": "generations",
    "05_ability.json": "abilities",
    "06_species.json": "species",
    "07_stats.json": "stats",
    "08_evolution_chains.json": "evolution_chains",
    "09_pokemon.json": "pokemons",
    "10_weaknesses.json": "pokemon_weaknesses",
}

# Mapeamento de campos válidos por tabela (para filtrar campos extras dos JSONs)
TABLE_VALID_FIELDS = {
    "regions": ["id", "name"],
    "types": ["id", "name", "color"],
    "egg_groups": ["id", "name"],
    "generations": ["id", "name", "region_id"],
    "abilities": ["id", "name", "description", "effect"],
    "species": ["id", "name", "description", "color", "generation_id"],
    "stats": ["id", "total", "hp", "attack", "defense", "sp_atk", "sp_def", "speed"],
    "evolution_chains": ["id", "chain_data"],
    "pokemon_weaknesses": ["pokemon_id", "type_id"],
    "pokemons": ["id", "number", "name", "height", "weight", "description", "sprites", 
                 "gender_male", "gender_female", "gender_rate_value", "egg_cycles", 
                 "stats_id", "generation_id", "species_id", "region_id", "evolution_chain_id"]
}

def escape_sql_value(value: Any) -> str:
    """Escapa valores para inserção segura em SQL."""
    if value is None:
        return 'NULL'
    elif isinstance(value, str):
        # Escapa aspas simples e quebras de linha
        escaped = value.replace("'", "''").replace("\n", "\\n")
        return f"'{escaped}'"
    elif isinstance(value, bool):
        return 'TRUE' if value else 'FALSE'
    elif isinstance(value, (int, float)):
        return str(value)
    elif isinstance(value, dict):
        # Para campos JSON, converte o dict para string JSON escapada
        json_str = json.dumps(value, ensure_ascii=False)
        escaped = json_str.replace("'", "''").replace("\n", "\\n")
        return f"'{escaped}'"
    elif isinstance(value, list):
        # Para arrays, converte para string JSON escapada
        json_str = json.dumps(value, ensure_ascii=False)
        escaped = json_str.replace("'", "''").replace("\n", "\\n")
        return f"'{escaped}'"
    else:
        escaped = str(value).replace("'", "''")
        return f"'{escaped}'"

def filter_valid_fields(record: Dict[str, Any], table_name: str) -> Dict[str, Any]:
    """Filtra apenas os campos válidos para a tabela especificada."""
    if table_name not in TABLE_VALID_FIELDS:
        return record  # Se não há mapeamento, retorna todos os campos
    
    valid_fields = TABLE_VALID_FIELDS[table_name]
    filtered_record = {}
    
    for field in valid_fields:
        if field in record:
            filtered_record[field] = record[field]
    
    return filtered_record

def flatten_object(obj: Dict[str, Any], parent_key: str = '', separator: str = '_') -> Dict[str, Any]:
    """
    Achata objetos aninhados para compatibilidade com estrutura SQL.
    Ex: {"sprites": {"front_default": "url"}} -> {"sprites_front_default": "url"}
    """
    items = []
    for key, value in obj.items():
        new_key = f"{parent_key}{separator}{key}" if parent_key else key
        
        if isinstance(value, dict) and not is_json_field(new_key):
            # Achata objetos aninhados, exceto campos que devem permanecer como JSON
            items.extend(flatten_object(value, new_key, separator).items())
        else:
            items.append((new_key, value))
    
    return dict(items)

def is_json_field(field_name: str) -> bool:
    """Determina se um campo deve permanecer como JSON no banco."""
    json_fields = [
        'sprites', 'other_sprites', 'stats_data', 'abilities_data', 
        'types_data', 'weaknesses_data', 'evolution_data'
    ]
    return any(json_field in field_name.lower() for json_field in json_fields)

def process_special_tables(table_name: str, records: List[Dict[str, Any]]) -> List[str]:
    """Processa tabelas com relacionamentos especiais."""
    sql_statements = []
    
    if table_name == "evolution_chains":
        # Para evolution_chains, mapeia 'chain' para 'chain_data'
        for record in records:
            chain_data = record.get('chain', {})
            evolution_record = {
                'id': record.get('id'),
                'chain_data': chain_data if chain_data else None
            }
            # Filtra apenas campos válidos para a tabela
            filtered_record = filter_valid_fields(evolution_record, table_name)
            
            # Para evolution_chains, não achatar o chain_data
            columns = list(filtered_record.keys())
            values = [escape_sql_value(filtered_record[col]) for col in columns]
            
            # Gera o comando SQL
            columns_str = ", ".join(columns)
            values_str = ", ".join(values)
            sql = f"INSERT INTO {table_name} ({columns_str}) VALUES ({values_str});"
            sql_statements.append(sql)
    elif table_name == "pokemon_weaknesses":
        # Para pokemon_weaknesses, processa array de fraquezas em relacionamentos
        # Primeiro, precisa mapear nomes de tipos para IDs
        type_name_to_id = {
            'Normal': 1, 'Fogo': 2, 'Água': 3, 'Elétrico': 4, 'Grama': 5, 
            'Gelo': 6, 'Lutador': 7, 'Venenoso': 8, 'Terrestre': 9, 'Voador': 10,
            'Psíquico': 11, 'Inseto': 12, 'Pedra': 13, 'Fantasma': 14, 'Dragão': 15,
            'Escuridão': 16, 'Metálico': 17, 'Fada': 18
        }
        
        for record in records:
            pokemon_id = record.get('pokemon_id')
            weaknesses = record.get('weaknesses', [])
            
            for weakness_name in weaknesses:
                type_id = type_name_to_id.get(weakness_name)
                if type_id:
                    sql = f"INSERT INTO {table_name} (pokemon_id, type_id) VALUES ({pokemon_id}, {type_id});"
                    sql_statements.append(sql)
    elif table_name == "pokemons":
        # Para pokémons, também gera as tabelas de relacionamento
        for record in records:
            # Filtra campos válidos e remove relacionamentos many-to-many
            filtered_record = filter_valid_fields(record, table_name)
            main_record = {k: v for k, v in filtered_record.items() 
                          if k not in ['type_ids', 'abilities', 'egg_group_ids', 'weaknesses']}
            
            sql_statements.append(generate_insert_sql(table_name, [main_record])[0])
            
            pokemon_id = record.get('id')
            
            # Relacionamentos many-to-many
            if 'type_ids' in record and record['type_ids']:
                for type_id in record['type_ids']:
                    sql_statements.append(
                        f"INSERT INTO pokemon_types (pokemon_id, type_id) VALUES ({pokemon_id}, {type_id});"
                    )
            
            if 'abilities' in record and record['abilities']:
                for ability_data in record['abilities']:
                    if isinstance(ability_data, dict):
                        ability_id = ability_data.get('ability_id')
                        is_hidden = ability_data.get('is_hidden', False)
                        sql_statements.append(
                            f"INSERT INTO pokemon_abilities (pokemon_id, ability_id, is_hidden) VALUES ({pokemon_id}, {ability_id}, {is_hidden});"
                        )
            
            if 'egg_group_ids' in record and record['egg_group_ids']:
                for egg_group_id in record['egg_group_ids']:
                    sql_statements.append(
                        f"INSERT INTO pokemon_egg_groups (pokemon_id, egg_group_id) VALUES ({pokemon_id}, {egg_group_id});"
                    )
            
            if 'weaknesses' in record and record['weaknesses']:
                for weakness_data in record['weaknesses']:
                    if isinstance(weakness_data, dict):
                        type_id = weakness_data.get('type_id')
                        multiplier = weakness_data.get('multiplier', 1.0)
                        sql_statements.append(
                            f"INSERT INTO pokemon_weaknesses (pokemon_id, type_id, multiplier) VALUES ({pokemon_id}, {type_id}, {multiplier});"
                        )
    else:
        # Para outras tabelas, processamento normal
        sql_statements.extend(generate_insert_sql(table_name, records))
    
    return sql_statements

def generate_insert_sql(table_name: str, records: List[Dict[str, Any]]) -> List[str]:
    """Gera comandos INSERT SQL para uma lista de registros."""
    if not records:
        return []
    
    sql_statements = []
    
    for record in records:
        # Filtra apenas campos válidos para a tabela
        filtered_record = filter_valid_fields(record, table_name)
        
        # Achata objetos aninhados se necessário
        flattened_record = flatten_object(filtered_record)
        
        # Extrai colunas e valores
        columns = list(flattened_record.keys())
        values = [escape_sql_value(flattened_record[col]) for col in columns]
        
        # Gera o comando SQL
        columns_str = ", ".join(columns)
        values_str = ", ".join(values)
        sql = f"INSERT INTO {table_name} ({columns_str}) VALUES ({values_str});"
        
        sql_statements.append(sql)
    
    return sql_statements

def load_json_file(file_path: Path) -> List[Dict[str, Any]]:
    """Carrega e valida um arquivo JSON."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            data = json.load(f)
            
        if not isinstance(data, list):
            print(f"⚠️  AVISO: {file_path.name} não contém um array. Envolvendo em array.")
            data = [data]
            
        return data
    except json.JSONDecodeError as e:
        print(f"❌ ERRO: Falha ao parsear JSON em {file_path.name}: {e}")
        return []
    except Exception as e:
        print(f"❌ ERRO: Falha ao ler arquivo {file_path.name}: {e}")
        return []

def generate_init_data_sql(data_dir: Path, output_file: Path) -> bool:
    """Gera o arquivo init-data.sql completo."""
    print("🚀 Iniciando geração do arquivo init-data.sql...")
    print(f"📁 Diretório de dados: {data_dir}")
    print(f"📄 Arquivo de saída: {output_file}")
    print()
    
    all_sql_statements = []
    success_count = 0
    error_count = 0
    
    # Header do arquivo SQL
    all_sql_statements.append("-- init-data.sql")
    all_sql_statements.append("-- Arquivo gerado automaticamente a partir dos JSONs de dados")
    all_sql_statements.append(f"-- Gerado em: {os.popen('date').read().strip()}")
    all_sql_statements.append("")
    all_sql_statements.append("-- Início da carga de dados")
    all_sql_statements.append("")
    
    # Processa cada arquivo na ordem correta
    for file_name in sorted(FILE_TO_TABLE_MAPPING.keys()):
        file_path = data_dir / file_name
        table_name = FILE_TO_TABLE_MAPPING[file_name]
        
        print(f"📊 Processando {file_name} -> tabela '{table_name}'...")
        
        if not file_path.exists():
            print(f"❌ ERRO: Arquivo {file_name} não encontrado!")
            error_count += 1
            continue
        
        # Carrega dados do JSON
        records = load_json_file(file_path)
        
        if not records:
            print(f"❌ ERRO: Nenhum registro válido encontrado em {file_name}")
            error_count += 1
            continue
        
        # Adiciona comentário de seção no SQL
        all_sql_statements.append(f"-- Dados da tabela: {table_name} (origem: {file_name})")
        
        try:
            # Gera comandos SQL
            if table_name in ["evolution_chains", "pokemon_weaknesses", "pokemons"]:
                sql_statements = process_special_tables(table_name, records)
            else:
                sql_statements = generate_insert_sql(table_name, records)
            
            if sql_statements:
                all_sql_statements.extend(sql_statements)
                all_sql_statements.append("")  # Linha em branco entre seções
                print(f"✅ SUCESSO: {len(records)} registros processados, {len(sql_statements)} comandos SQL gerados")
                success_count += 1
            else:
                print(f"❌ ERRO: Nenhum comando SQL gerado para {file_name}")
                error_count += 1
                
        except Exception as e:
            print(f"❌ ERRO: Falha ao processar {file_name}: {e}")
            error_count += 1
    
    # Footer do arquivo SQL
    all_sql_statements.append("-- Fim da carga de dados")
    all_sql_statements.append(f"-- Resumo: {success_count} arquivos processados com sucesso, {error_count} com erro")
    
    # Escreve o arquivo final
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(all_sql_statements))
        
        print()
        print("📁 Arquivo init-data.sql gerado com sucesso!")
        print(f"📊 Resumo final:")
        print(f"   ✅ Arquivos processados com sucesso: {success_count}")
        print(f"   ❌ Arquivos com erro: {error_count}")
        print(f"   📄 Total de linhas SQL geradas: {len(all_sql_statements)}")
        print(f"   💾 Arquivo salvo em: {output_file}")
        
        return error_count == 0
        
    except Exception as e:
        print(f"❌ ERRO CRÍTICO: Falha ao escrever arquivo SQL: {e}")
        return False

def main():
    """Função principal."""
    if len(sys.argv) > 1:
        # Permite especificar diretório como argumento
        data_dir = Path(sys.argv[1])
    else:
        # Usa diretório padrão do projeto
        script_dir = Path(__file__).parent.parent.parent  # Sobe para raiz do projeto
        data_dir = script_dir / "data" / "json"
    
    # Arquivo de saída
    output_file = Path(__file__).parent.parent.parent / "database" / "seeds" / "init-data.sql"
    
    # Valida diretório de entrada
    if not data_dir.exists():
        print(f"❌ ERRO: Diretório de dados não encontrado: {data_dir}")
        sys.exit(1)
    
    # Cria diretório de saída se não existir
    output_file.parent.mkdir(parents=True, exist_ok=True)
    
    # Executa geração
    success = generate_init_data_sql(data_dir, output_file)
    
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()