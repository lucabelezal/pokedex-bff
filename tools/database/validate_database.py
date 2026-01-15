#!/usr/bin/env python3
"""
Script para validar a estrutura e dados do banco de dados da Pokédex.
Este script conecta ao banco PostgreSQL e verifica:
- Se todas as tabelas foram criadas
- Se cada tabela possui dados
- Quantidade de registros por tabela
- Ordem de dependências entre tabelas
- Problemas potenciais
"""

import psycopg2
import sys
from typing import Dict, List
from dataclasses import dataclass

@dataclass
class TableInfo:
    name: str
    exists: bool = False
    row_count: int = 0
    columns: List[str] = None
    foreign_keys: List[str] = None
    has_data: bool = False

class DatabaseValidator:
    def __init__(self, host='localhost', port=5434, database='pokedex_dev_db', 
                 user='postgres', password='postgres'):
        self.connection_params = {
            'host': host,
            'port': port,
            'database': database,
            'user': user,
            'password': password
        }
        self.conn = None
        
        # Ordem esperada das tabelas baseada nas dependências
        self.expected_tables_order = [
            'regions',           # Base - sem dependências
            'types',             # Base - sem dependências  
            'egg_groups',        # Base - sem dependências
            'generations',       # Depende de: regions
            'abilities',         # Base - sem dependências
            'species',           # Depende de: generations
            'stats',             # Base - sem dependências (dados de pokémons)
            'evolution_chains',  # Base - dados JSON independentes
            'pokemons',          # Depende de: stats, generations, species, regions, evolution_chains
            'pokemon_types',     # Depende de: pokemons, types
            'pokemon_abilities', # Depende de: pokemons, abilities
            'pokemon_egg_groups', # Depende de: pokemons, egg_groups
            'pokemon_weaknesses'  # Depende de: pokemons, types
        ]
        
        # Contadores esperados mínimos (baseado nos JSONs)
        self.expected_min_counts = {
            'regions': 10,
            'types': 18,
            'egg_groups': 15,
            'generations': 10,
            'abilities': 300,
            'species': 600,
            'stats': 25,
            'evolution_chains': 7,
            'pokemons': 25,
            'pokemon_types': 25,        # Pokémons tem pelo menos 1 tipo
            'pokemon_abilities': 25,     # Pokémons tem pelo menos 1 habilidade
            'pokemon_egg_groups': 25,    # Pokémons tem pelo menos 1 grupo de ovo
            'pokemon_weaknesses': 20,    # Dados de fraquezas dos tipos
        }

    def connect(self) -> bool:
        """Conecta ao banco de dados."""
        try:
            print("🔌 Conectando ao banco de dados...")
            print(f"   Host: {self.connection_params['host']}:{self.connection_params['port']}")
            print(f"   Database: {self.connection_params['database']}")
            print(f"   User: {self.connection_params['user']}")
            
            self.conn = psycopg2.connect(**self.connection_params)
            print("✅ Conexão estabelecida com sucesso!")
            return True
        except psycopg2.Error as e:
            print(f"❌ ERRO: Falha ao conectar ao banco: {e}")
            return False
        except Exception as e:
            print(f"❌ ERRO CRÍTICO: {e}")
            return False

    def disconnect(self):
        """Desconecta do banco de dados."""
        if self.conn:
            self.conn.close()
            print("🔌 Conexão fechada.")

    def get_all_tables(self) -> List[str]:
        """Retorna lista de todas as tabelas no banco."""
        try:
            with self.conn.cursor() as cursor:
                cursor.execute("""
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_schema = 'public' 
                    AND table_type = 'BASE TABLE'
                    ORDER BY table_name;
                """)
                return [row[0] for row in cursor.fetchall()]
        except psycopg2.Error as e:
            print(f"❌ ERRO: Falha ao obter lista de tabelas: {e}")
            return []

    def get_table_info(self, table_name: str) -> TableInfo:
        """Retorna informações detalhadas sobre uma tabela."""
        info = TableInfo(name=table_name)
        
        try:
            with self.conn.cursor() as cursor:
                # Verifica se a tabela existe
                cursor.execute("""
                    SELECT COUNT(*) 
                    FROM information_schema.tables 
                    WHERE table_schema = 'public' 
                    AND table_name = %s;
                """, (table_name,))
                
                info.exists = cursor.fetchone()[0] > 0
                
                if not info.exists:
                    return info
                
                # Conta registros na tabela
                cursor.execute(f"SELECT COUNT(*) FROM {table_name};")
                info.row_count = cursor.fetchone()[0]
                info.has_data = info.row_count > 0
                
                # Obtém colunas da tabela
                cursor.execute("""
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_schema = 'public' 
                    AND table_name = %s
                    ORDER BY ordinal_position;
                """, (table_name,))
                info.columns = [row[0] for row in cursor.fetchall()]
                
                # Obtém chaves estrangeiras
                cursor.execute("""
                    SELECT DISTINCT
                        tc.constraint_name,
                        kcu.column_name,
                        ccu.table_name AS foreign_table_name,
                        ccu.column_name AS foreign_column_name
                    FROM information_schema.table_constraints AS tc
                    JOIN information_schema.key_column_usage AS kcu
                        ON tc.constraint_name = kcu.constraint_name
                        AND tc.table_schema = kcu.table_schema
                    JOIN information_schema.constraint_column_usage AS ccu
                        ON ccu.constraint_name = tc.constraint_name
                        AND ccu.table_schema = tc.table_schema
                    WHERE tc.constraint_type = 'FOREIGN KEY'
                        AND tc.table_name = %s;
                """, (table_name,))
                
                fks = cursor.fetchall()
                info.foreign_keys = [f"{row[1]} -> {row[2]}.{row[3]}" for row in fks]
                
        except psycopg2.Error as e:
            print(f"❌ ERRO: Falha ao obter informações da tabela {table_name}: {e}")
            
        return info

    def validate_table_dependencies(self, tables_info: Dict[str, TableInfo]) -> List[str]:
        """Valida se as dependências entre tabelas estão corretas."""
        issues = []
        
        # Verifica se tabelas base têm dados antes das dependentes
        dependencies = {
            'generations': ['regions'],
            'species': ['generations'],  
            'pokemons': ['stats', 'generations', 'species', 'regions', 'evolution_chains'],
            'pokemon_types': ['pokemons', 'types'],
            'pokemon_abilities': ['pokemons', 'abilities'],
            'pokemon_egg_groups': ['pokemons', 'egg_groups'],
            'pokemon_weaknesses': ['pokemons']
        }
        
        for table, deps in dependencies.items():
            if table in tables_info and tables_info[table].exists:
                for dep in deps:
                    if dep not in tables_info or not tables_info[dep].exists:
                        issues.append(f"❌ {table} existe mas dependência {dep} não foi encontrada")
                    elif not tables_info[dep].has_data and tables_info[table].has_data:
                        issues.append(f"⚠️  {table} tem dados mas dependência {dep} está vazia")
        
        return issues

    def analyze_data_issues(self, tables_info: Dict[str, TableInfo]) -> List[str]:
        """Analisa possíveis problemas nos dados."""
        issues = []
        
        for table_name, info in tables_info.items():
            if not info.exists:
                issues.append(f"❌ CRÍTICO: Tabela {table_name} não existe")
                continue
                
            if not info.has_data:
                issues.append(f"⚠️  AVISO: Tabela {table_name} existe mas está vazia")
                continue
                
            # Verifica se tem pelo menos o mínimo esperado
            if table_name in self.expected_min_counts:
                min_expected = self.expected_min_counts[table_name]
                if info.row_count < min_expected:
                    issues.append(f"⚠️  AVISO: Tabela {table_name} tem apenas {info.row_count} registros (esperado pelo menos {min_expected})")
        
        return issues

    def run_validation(self) -> bool:
        """Executa a validação completa do banco."""
        print("🚀 Iniciando validação do banco de dados...")
        print("=" * 60)
        
        if not self.connect():
            return False
        
        try:
            # Obtém todas as tabelas do banco
            existing_tables = self.get_all_tables()
            print(f"📊 Tabelas encontradas no banco: {len(existing_tables)}")
            
            if existing_tables:
                print("   " + ", ".join(existing_tables))
            else:
                print("❌ CRÍTICO: Nenhuma tabela encontrada no banco!")
                return False
            
            print("\n" + "=" * 60)
            
            # Coleta informações de todas as tabelas esperadas
            tables_info = {}
            
            print("📋 Analisando tabelas esperadas:")
            print("-" * 40)
            
            for table_name in self.expected_tables_order:
                info = self.get_table_info(table_name)
                tables_info[table_name] = info
                
                status = "✅" if info.exists and info.has_data else "❌" if not info.exists else "⚠️ "
                data_status = f"{info.row_count} registros" if info.exists else "N/A"
                
                print(f"{status} {table_name:<20} | {data_status:<15} | Existe: {info.exists}")
            
            # Verifica tabelas extras (não esperadas)
            extra_tables = set(existing_tables) - set(self.expected_tables_order)
            if extra_tables:
                print(f"\n📋 Tabelas extras encontradas (não esperadas): {', '.join(extra_tables)}")
            
            print("\n" + "=" * 60)
            print("🔍 DIAGNÓSTICO DE PROBLEMAS:")
            print("-" * 40)
            
            # Análise de problemas
            data_issues = self.analyze_data_issues(tables_info)
            dependency_issues = self.validate_table_dependencies(tables_info)
            
            all_issues = data_issues + dependency_issues
            
            if not all_issues:
                print("✅ PERFEITO: Nenhum problema encontrado!")
                print("   ✓ Todas as tabelas existem")
                print("   ✓ Todas as tabelas possuem dados")
                print("   ✓ Dependências estão corretas")
            else:
                print(f"⚠️  {len(all_issues)} problema(s) encontrado(s):")
                for issue in all_issues:
                    print(f"   {issue}")
            
            print("\n" + "=" * 60)
            print("📊 RESUMO FINAL:")
            print("-" * 40)
            
            total_tables = len(self.expected_tables_order)
            existing_count = sum(1 for info in tables_info.values() if info.exists)
            with_data_count = sum(1 for info in tables_info.values() if info.has_data)
            total_records = sum(info.row_count for info in tables_info.values() if info.exists)
            
            print(f"📋 Tabelas esperadas: {total_tables}")
            print(f"✅ Tabelas existentes: {existing_count}")
            print(f"📊 Tabelas com dados: {with_data_count}")
            print(f"📈 Total de registros: {total_records}")
            print(f"⚠️  Problemas encontrados: {len(all_issues)}")
            
            success_rate = (with_data_count / total_tables) * 100
            print(f"🎯 Taxa de sucesso: {success_rate:.1f}%")
            
            return len(all_issues) == 0
            
        finally:
            self.disconnect()

def main():
    """Função principal."""
    print("🗃️  VALIDADOR DE BANCO DE DADOS - POKÉDEX BFF")
    print("=" * 60)
    
    validator = DatabaseValidator()
    success = validator.run_validation()
    
    print("\n" + "=" * 60)
    if success:
        print("🎉 VALIDAÇÃO CONCLUÍDA COM SUCESSO!")
        print("   O banco está funcionando corretamente.")
    else:
        print("❌ VALIDAÇÃO FALHOU!")
        print("   Verifique os problemas listados acima.")
    
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()