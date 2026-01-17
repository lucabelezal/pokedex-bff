#!/usr/bin/env python3
"""
Script para verificar dependências necessárias para o ambiente de desenvolvimento.
Verifica se todas as ferramentas necessárias estão instaladas e configuradas.
"""

import sys
import subprocess
import platform
from typing import Tuple, Dict

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
    # Locais comuns onde Docker pode estar instalado
    docker_paths = [
        "docker",  # No PATH
        "/usr/local/bin/docker",  # Instalação padrão
        "/Applications/Docker.app/Contents/Resources/bin/docker",  # Docker Desktop no macOS
        "/usr/bin/docker"  # Linux
    ]
    
    docker_cmd = None
    version_output = None
    
    # Procura Docker em diferentes locais
    for path in docker_paths:
        if path == "docker":
            success, output = run_command("docker --version")
        else:
            success, output = run_command(f"{path} --version")
        
        if success:
            docker_cmd = path
            version_output = output
            break
    
    if not docker_cmd:
        return False, "Docker não encontrado. Instale o Docker Desktop ou adicione Docker ao PATH"
    
    # Verifica se Docker daemon está rodando
    info_cmd = f"{docker_cmd} info" if docker_cmd != "docker" else "docker info"
    success_ping, _ = run_command(info_cmd)
    if not success_ping:
        return False, f"{version_output} (Docker Desktop não está rodando - abra o Docker Desktop e aguarde inicializar)"
    
    return True, version_output

def check_docker_compose() -> Tuple[bool, str]:
    """Verifica se Docker Compose está instalado."""
    # Locais comuns onde Docker pode estar instalado
    docker_paths = [
        "docker",  # No PATH
        "/usr/local/bin/docker",  # Instalação padrão
        "/Applications/Docker.app/Contents/Resources/bin/docker",  # Docker Desktop no macOS
        "/usr/bin/docker"  # Linux
    ]
    
    # Primeiro tenta docker compose (versão nova)
    for docker_path in docker_paths:
        if docker_path == "docker":
            success, output = run_command("docker compose version")
        else:
            success, output = run_command(f"{docker_path} compose version")
        
        if success:
            return True, output
    
    # Fallback para docker-compose (versão legacy)
    docker_compose_paths = [
        "docker-compose",  # No PATH
        "/usr/local/bin/docker-compose",  # Instalação padrão
        "/Applications/Docker.app/Contents/Resources/bin/docker-compose",  # Docker Desktop no macOS
        "/usr/bin/docker-compose"  # Linux
    ]
    
    for path in docker_compose_paths:
        success, output = run_command(f"{path} --version")
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

def run_essential_checks():
    """Executa verificações de dependências essenciais."""
    essential_checks = [
        ("Docker", check_docker),
        ("Make", check_make),
        ("Python3", check_python)
    ]
    
    results = []
    all_ok = True
    
    print("\n🛠️  DEPENDÊNCIAS ESSENCIAIS:")
    print("-" * 40)
    
    for name, check_func in essential_checks:
        try:
            success, message = check_func()
            results.append((name, success, message))
            status = "✅" if success else "❌"
            print(f"{status} {name}: {message}")
            if not success:
                all_ok = False
        except Exception as e:
            results.append((name, False, f"Erro na verificação: {str(e)}"))
            print(f"❌ {name}: Erro na verificação: {str(e)}")
            all_ok = False
    
    return results, all_ok


def run_optional_checks():
    """Executa verificações de dependências opcionais."""
    optional_checks = [
        ("psycopg2 (Python)", check_psycopg2)
    ]
    
    results = []
    
    print("\n🔧 DEPENDÊNCIAS OPCIONAIS:")
    print("-" * 40)
    
    for name, check_func in optional_checks:
        try:
            success, message = check_func()
            results.append((name, success, message))
            status = "✅" if success else "⚠️"
            print(f"{status} {name}: {message}")
            if not success:
                print("   💡 Esta dependência é opcional para comandos básicos")
        except Exception as e:
            results.append((name, False, f"Erro na verificação: {str(e)}"))
            print(f"⚠️ {name}: Erro na verificação: {str(e)}")
            print("   💡 Esta dependência é opcional para comandos básicos")
    
    return results


def print_installation_instructions(results, system):
    """Imprime instruções de instalação para dependências ausentes."""
    instructions = get_installation_instructions()
    system_key = system.lower()
    
    print("\n📋 INSTRUÇÕES DE INSTALAÇÃO:")
    print("-" * 60)
    
    for name, success, message in results:
        if not success and name != "psycopg2 (Python)":  # Skip optional deps
            tool_key = name.lower().split()[0]
            if tool_key in instructions and system_key in instructions[tool_key]:
                print(f"\n🔧 {name}:")
                print(f"   {instructions[tool_key][system_key]}")
    
    print("\n💡 DICA: Após instalar, execute novamente 'make check-deps'")


def main():
    """Executa todas as verificações de dependências."""
    print("🔍 VERIFICANDO DEPENDÊNCIAS DO PROJETO...")
    print("=" * 60)
    
    system = platform.system()
    
    # Executar verificações
    essential_results, all_ok = run_essential_checks()
    optional_results = run_optional_checks()
    
    # Combinar resultados
    all_results = essential_results + optional_results
    
    print("\n" + "=" * 60)
    
    if all_ok:
        print("\n🎉 DEPENDÊNCIAS ESSENCIAIS OK!")
        print("   Você pode executar os comandos de desenvolvimento.")
        return 0
    else:
        print("\n❌ ALGUMAS DEPENDÊNCIAS ESSENCIAIS ESTÃO AUSENTES!")
        print_installation_instructions(all_results, system)
        return 1

if __name__ == "__main__":
    sys.exit(main())