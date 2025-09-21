import os
import json

DATA_DIR = 'src/main/resources/data'
OUTPUT_SQL = 'docker/db/init-data.sql'

def json_to_sql(table, data):
    if not data:
        return ''
    keys = data[0].keys()
    columns = ', '.join(keys)
    values = []
    for row in data:
        row_values = []
        for k in keys:
            v = row[k]
            if isinstance(v, str):
                v = v.replace("'", "''")
                row_values.append(f"'{v}'")
            elif v is None:
                row_values.append('NULL')
            else:
                row_values.append(str(v))
        values.append(f"({', '.join(row_values)})")
    return f"INSERT INTO {table} ({columns}) VALUES\n" + ',\n'.join(values) + ";\n"

json_files = sorted([f for f in os.listdir(DATA_DIR) if f.endswith('.json')])

try:
    with open(OUTPUT_SQL, 'w', encoding='utf-8') as out:
        out.write('BEGIN;\n')
        for filename in json_files:
            # Remove prefixo numérico e underline do nome da tabela
            table = filename.replace('.json', '').split('_', 1)[-1]
            try:
                with open(os.path.join(DATA_DIR, filename), encoding='utf-8') as f:
                    data = json.load(f)
                    if isinstance(data, dict):
                        data = [data]
                    sql = json_to_sql(table, data)
                    out.write(f'-- Dados para {table}\n')
                    out.write(sql)
                    out.write('\n')
                print(f"[OK] {filename} convertido para INSERT em {table}")
            except Exception as e:
                print(f"[ERRO] Falha ao processar {filename}: {e}")
        out.write('COMMIT;\n')
    print(f"\n[SUCESSO] SQL gerado em {OUTPUT_SQL}")
except Exception as e:
    print(f"[FATAL] Falha ao gerar o SQL: {e}")
