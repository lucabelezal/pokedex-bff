import json
import os

# Caminho para os arquivos JSON e o arquivo SQL de saída
json_dir = "src/main/resources/data"
sql_output = "docker/db/schema.dev.sql"

def json_to_sql():
    with open(sql_output, "w") as sql_file:
        for filename in sorted(os.listdir(json_dir)):
            if filename.endswith(".json"):
                table_name = filename.split(".")[0]
                file_path = os.path.join(json_dir, filename)

                with open(file_path, "r") as json_file:
                    data = json.load(json_file)

                    for record in data:
                        columns = ", ".join(record.keys())
                        values = ", ".join(f"'{v}'" if isinstance(v, str) else str(v) for v in record.values())
                        sql = f"INSERT INTO {table_name} ({columns}) VALUES ({values});\n"
                        sql_file.write(sql)

if __name__ == "__main__":
    os.makedirs(os.path.dirname(sql_output), exist_ok=True)
    json_to_sql()
    print(f"Arquivo SQL gerado em: {sql_output}")
