#!/usr/bin/env python3
"""
Script auxiliar para detectar comandos Docker corretos para uso no Makefile.
"""

import subprocess
import sys
from typing import Optional

def run_command(command: str) -> bool:
    """Executa um comando e retorna True se bem sucedido."""
    try:
        result = subprocess.run(
            command.split(),
            capture_output=True,
            text=True,
            timeout=10
        )
        return result.returncode == 0
    except Exception:
        return False

def find_docker_command() -> Optional[str]:
    """Encontra o comando Docker correto."""
    docker_paths = [
        "docker",  # No PATH
        "/usr/local/bin/docker",  # Instalação padrão
        "/Applications/Docker.app/Contents/Resources/bin/docker",  # Docker Desktop no macOS
        "/usr/bin/docker"  # Linux
    ]
    
    for path in docker_paths:
        if run_command(f"{path} --version"):
            return path
    
    return None

def find_docker_compose_command() -> Optional[str]:
    """Encontra o comando Docker Compose correto."""
    # Primeiro tenta docker compose (versão nova)
    docker_cmd = find_docker_command()
    if docker_cmd:
        if run_command(f"{docker_cmd} compose version"):
            return f"{docker_cmd} compose"
    
    # Fallback para docker-compose (versão legacy)
    docker_compose_paths = [
        "docker-compose",  # No PATH
        "/usr/local/bin/docker-compose",  # Instalação padrão
        "/Applications/Docker.app/Contents/Resources/bin/docker-compose",  # Docker Desktop no macOS
        "/usr/bin/docker-compose"  # Linux
    ]
    
    for path in docker_compose_paths:
        if run_command(f"{path} --version"):
            return path
    
    return None

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: python3 detect_docker_commands.py [docker|docker-compose]")
        sys.exit(1)
    
    command_type = sys.argv[1]
    
    if command_type == "docker":
        docker_cmd = find_docker_command()
        if docker_cmd:
            print(docker_cmd)
        else:
            print("DOCKER_NOT_FOUND")
            sys.exit(1)
    
    elif command_type == "docker-compose":
        docker_compose_cmd = find_docker_compose_command()
        if docker_compose_cmd:
            print(docker_compose_cmd)
        else:
            print("DOCKER_COMPOSE_NOT_FOUND")
            sys.exit(1)
    
    else:
        print("Tipo de comando inválido. Use 'docker' ou 'docker-compose'")
        sys.exit(1)