#!/usr/bin/env python3
"""
Script para verificar dependências necessárias para o ambiente de desenvolvimento.
Verifica se todas as ferramentas necessárias estão instaladas e configuradas.
"""

import sys
import subprocess
import platform
import shutil
from typing import List, Tuple, Dict, Optional

def run_command(command: str) -> Tuple[bool, str]:
    """Executa um comando e retorna status e output."""
    try:
        result = subprocess.run(
            command.split(),
            capture_output=True,
            text=True,
            timeout=10
        )
        return result.returncode == 0, result.stdout.strip()
    except Exception as e:
        return False, str(e)

def check_python() -> Tuple[bool, str]:
    """Verifica se Python 3.7+ está disponível."""
    version = sys.version_info
    if version.major >= 3 and version.minor >= 7:
        return True, f"Python {version.major}.{version.minor}.{version.micro}"
    return False, f"Python {version.major}.{version.minor}.{version.micro} (requer 3.7+)"

def check_docker() -> Tuple[bool, str]:
    """Verifica se Docker está instalado e funcionando."""
    success, output = run_command("docker --version")
    if not success:
        return False, "Docker não encontrado"
    
    # Verifica se Docker daemon está rodando
    success_ping, _ = run_command("docker info")
    if not success_ping:
        return False, f"{output} (daemon não está rodando)"
    
    return True, output

def check_docker_compose() -> Tuple[bool, str]:
    """Verifica se Docker Compose está instalado."""
    # Primeiro tenta docker compose (versão nova)
    success, output = run_command("docker compose version")
    if success:
        return True, output
    
    # Fallback para docker-compose (versão legacy)
    success, output = run_command("docker-compose --version")
    if success:
        return True, output
    
    return False, "Docker Compose não encontrado"

def check_make() -> Tuple[bool, str]:
    """Verifica se Make está instalado."""
    success, output = run_command("make --version")
    if success:
        return True, output.split('\n')[0]
    return False, "Make não encontrado"

def check_psycopg2() -> Tuple[bool, str]:
    """Verifica se psycopg2 está instalado."""
    try:
        import psycopg2
        return True, f"psycopg2 {psycopg2.__version__}"
    except ImportError:
        return False, "psycopg2 não instalado"

def get_installation_instructions() -> Dict[str, Dict[str, str]]:
    """Retorna instruções de instalação para cada sistema operacional."""
    system = platform.system().lower()
    
    instructions = {
        "python": {
            "linux": "sudo apt update && sudo apt install python3 python3-pip",
            "darwin": "brew install python3",
            "windows": "Baixe do https://python.org ou use winget install Python.Python.3"
        },
        "docker": {
            "linux": "curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh",
            "darwin": "brew install --cask docker",
            "windows": "Baixe Docker Desktop do https://docker.com"
        },
        "docker-compose": {
            "linux": "sudo apt install docker-compose-plugin",
            "darwin": "Incluído com Docker Desktop",
            "windows": "Incluído com Docker Desktop"
        },
        "make": {
            "linux": "sudo apt install build-essential",
            "darwin": "xcode-select --install",
            "windows": "choco install make ou use WSL"
        },
        "psycopg2": {
            "linux": "pip3 install psycopg2-binary",
            "darwin": "pip3 install psycopg2-binary", 
            "windows": "pip install psycopg2-binary"
        }
    }
    
    return instructions

def main():
    """Função principal para verificar todas as dependências."""
    print("🔍 VERIFICADOR DE DEPENDÊNCIAS - POKÉDX BFF")
    print("=" * 60)
    
    system = platform.system()
    print(f"🖥️  Sistema Operacional: {system} {platform.release()}")
    print("=" * 60)
    
    checks = [
        ("Python 3.7+", check_python),
        ("Docker", check_docker),
        ("Docker Compose", check_docker_compose),
        ("Make", check_make),
        ("psycopg2 (Python)", check_psycopg2)
    ]
    
    results = []
    all_ok = True
    
    for name, check_func in checks:
        success, message = check_func()
        status = "✅" if success else "❌"
        results.append((name, success, message))
        print(f"{status} {name:20} | {message}")
        if not success:
            all_ok = False
    
    print("=" * 60)
    
    if all_ok:
        print("🎉 TODAS AS DEPENDÊNCIAS ESTÃO INSTALADAS!")
        print("   Você pode executar os comandos de desenvolvimento.")
        return 0
    else:
        print("⚠️  DEPENDÊNCIAS FALTANDO ENCONTRADAS!")
        print("\n📋 INSTRUÇÕES DE INSTALAÇÃO:")
        print("-" * 60)
        
        instructions = get_installation_instructions()
        system_key = system.lower()
        
        for name, success, message in results:
            if not success:
                tool_key = name.lower().split()[0]
                if tool_key in instructions and system_key in instructions[tool_key]:
                    print(f"\n🔧 {name}:")
                    print(f"   {instructions[tool_key][system_key]}")
        
        print(f"\n💡 DICA: Após instalar, execute novamente 'make check-deps'")
        return 1

if __name__ == "__main__":
    sys.exit(main())