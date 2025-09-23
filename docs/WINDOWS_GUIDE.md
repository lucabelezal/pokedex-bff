# 🪟 **Pokedex BFF - Guia Windows**

Este guia específico para **Windows** oferece **4 opções** para executar o projeto.

## 🎯 **Opções Disponíveis**

### **📊 Comparação das Opções**

| Opção | Facilidade | Setup | Compatibilidade | Funcionalidade |
|-------|------------|-------|-----------------|----------------|
| **1. Scripts Batch (.bat)** | ⭐⭐⭐ | ✅ Zero | ⭐⭐⭐ | ⭐⭐ |
| **2. PowerShell (.ps1)** | ⭐⭐⭐ | ⭐ Mínimo | ⭐⭐ | ⭐⭐⭐ |
| **3. Instalar Make** | ⭐⭐ | ⭐⭐ Setup | ⭐⭐⭐ | ⭐⭐⭐ |
| **4. Comandos Diretos** | ⭐ | ✅ Zero | ⭐⭐⭐ | ⭐ |
| **Facilidade** | ⭐⭐⭐ Muito fácil | ⭐⭐ Fácil | ⭐ Básico |
| **Automação** | ✅ Completa | ✅ Completa | ❌ Manual |
| **Validações** | ✅ Sim | ✅ Sim | ❌ Não |
| **Feedback** | ✅ Colorido | ✅ Colorido | ❌ Básico |
| **Compatibilidade** | 🪟 Todos Windows | 🪟 Windows 10+ | 🪟 Todos Windows |

## 🚀 **Opção 1: Scripts Batch (.bat)** ⭐ **Mais Fácil**

### **✅ Vantagens**
- ✅ **Zero Setup**: Funciona imediatamente
- ✅ **Plug & Play**: Só fazer double-click
- ✅ **Validações**: Verifica tudo automaticamente
- ✅ **Compatibilidade**: Qualquer Windows

### **🔧 Como Usar**
```cmd
# Sequência completa
scripts\windows\setup.bat           # 1. Verificar dependências
scripts\windows\generate-data.bat   # 2. Gerar dados SQL
scripts\windows\start-db.bat        # 3. Subir banco
scripts\windows\validate-db.bat     # 4. Validar funcionamento
scripts\windows\start-app.bat       # 5. Subir aplicação

# Utilitários
scripts\windows\stop.bat            # Parar tudo
scripts\windows\logs.bat            # Ver logs
scripts\windows\test.bat            # Executar testes
scripts\windows\build.bat           # Build da aplicação
```

## 🔥 **Opção 2: PowerShell (.ps1)** ⭐ **Moderno**

### **✅ Vantagens**
- ✅ **Syntax Moderna**: PowerShell é mais limpo
- ✅ **Melhor Feedback**: Cores e formatação
- ✅ **Error Handling**: Tratamento robusto de erros
- ✅ **Cross-platform**: Funciona no PowerShell Core

### **⚙️ Setup Inicial**
```powershell
# Executar uma vez como Administrador
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### **🔧 Como Usar**
```powershell
# Sequência completa
.\scripts\powershell\Setup.ps1          # 1. Verificar dependências
.\scripts\powershell\Generate-Data.ps1   # 2. Gerar dados
.\scripts\powershell\Start-Database.ps1  # 3. Subir banco
.\scripts\powershell\Validate-Database.ps1  # 4. Validar
```

## 🔧 **Opção 3: Instalar Make** ⭐ **Padrão Universal**

### **✅ Vantagens**
- ✅ **Comandos Únicos**: Mesmos comandos em todas plataformas
- ✅ **Makefile Completo**: Acesso a todos os targets
- ✅ **Documentação Única**: Uma fonte de verdade
- ✅ **Experiência Consistente**: Time todo usa igual

### **⚙️ Setup Inicial** 
📖 **Guia completo**: [**Windows Make Setup**](WINDOWS_MAKE_SETUP.md)

**Opção mais fácil - Chocolatey**:
```powershell
# 1. Instalar Chocolatey (como Administrador)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# 2. Instalar Make
choco install make

# 3. Verificar
make --version
```

### **🔧 Como Usar**
```bash
# Agora funciona igual Linux/macOS!
make check-deps         # Verificar dependências
make generate-sql-data  # Gerar dados SQL
make up                 # Subir banco + aplicação
make validate-db        # Validar funcionamento
make down               # Parar tudo
```

## ⚡ **Opção 4: Comandos Diretos** ⭐ **Para Experts**

### **✅ Vantagens**
- ✅ **Controle Total**: Sabe exatamente o que está executando
- ✅ **Zero Dependencies**: Só Docker + Python
- ✅ **Flexibilidade**: Pode customizar comandos
- ✅ **Debug Fácil**: Vê cada passo separadamente

### **🔧 Como Usar**
```cmd
# 1. Setup inicial (verificar dependências)
scripts\windows\setup.bat

# 2. Gerar dados SQL dos JSONs
scripts\windows\generate-data.bat

# 3. Subir banco PostgreSQL
scripts\windows\start-db.bat

# 4. Validar banco e dados
scripts\windows\validate-db.bat

# 5. Subir aplicação completa
scripts\windows\start-app.bat
```

### **Scripts Disponíveis**

#### **📋 Setup e Dependências**
```cmd
scripts\windows\setup.bat           # Verifica Docker, Python, psycopg2
```

#### **🗄️ Banco de Dados**
```cmd
scripts\windows\generate-data.bat   # Converte JSON → SQL
scripts\windows\start-db.bat        # Inicia PostgreSQL
scripts\windows\validate-db.bat     # Valida tabelas e dados
```

#### **🚀 Aplicação**
```cmd
scripts\windows\start-app.bat       # Inicia aplicação completa
scripts\windows\stop.bat            # Para todos os serviços
scripts\windows\logs.bat            # Mostra logs em tempo real
```

#### **🧪 Desenvolvimento**
```cmd
scripts\windows\test.bat            # Executa testes unitários
scripts\windows\build.bat           # Compila aplicação
```

### **✅ Vantagens dos Scripts Batch**
- ✅ **Plug & Play**: Funciona imediatamente
- ✅ **Validações Automáticas**: Verifica dependências antes de executar
- ✅ **Feedback Visual**: Mensagens coloridas de sucesso/erro
- ✅ **Error Handling**: Para execução se algo der errado
- ✅ **Compatibilidade**: Funciona em qualquer versão do Windows

## 🔥 **Opção 2: PowerShell (.ps1)**

### **Pré-requisitos**
```powershell
# Permitir execução de scripts (executar como Administrador)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### **Sequência de Comandos**
```powershell
# 1. Setup inicial
.\scripts\powershell\Setup.ps1

# 2. Gerar dados SQL
.\scripts\powershell\Generate-Data.ps1

# 3. Subir banco
.\scripts\powershell\Start-Database.ps1

# 4. Validar banco
.\scripts\powershell\Validate-Database.ps1
```

### **✅ Vantagens PowerShell**
- ✅ **Moderno**: Sintaxe mais limpa
- ✅ **Rico em Features**: Melhor tratamento de erros
- ✅ **Multiplataforma**: Funciona no PowerShell Core
- ✅ **Output Colorido**: Feedback visual aprimorado

## ⚡ **Opção 3: Comandos Diretos**

### **Para Usuários Avançados**
```cmd
# 1. Gerar dados SQL
python scripts\json_to_sql.py

# 2. Subir banco PostgreSQL
docker compose -f docker\docker-compose.dev.yml up -d db

# 3. Aguardar inicialização (aguarde ~10 segundos)
timeout /t 10

# 4. Validar banco
python scripts\check_db.py

# 5. Subir aplicação completa
docker compose -f docker\docker-compose.dev.yml up -d

# 6. Ver logs
docker compose -f docker\docker-compose.dev.yml logs -f

# 7. Parar tudo
docker compose -f docker\docker-compose.dev.yml down
```

### **Comandos Gradle**
```cmd
# Testes
gradlew.bat test

# Build
gradlew.bat build

# Executar aplicação local (sem Docker)
gradlew.bat bootRun
```

## 🛠️ **Solução de Problemas Windows**

### **❌ Problema: Docker não encontrado**
```cmd
# Solução: Instalar Docker Desktop
# Download: https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe
```

### **❌ Problema: Python não encontrado**
```cmd
# Solução: Instalar Python
# Download: https://www.python.org/downloads/
# ⚠️ IMPORTANTE: Marcar "Add Python to PATH" durante instalação
```

### **❌ Problema: psycopg2 não instala**
```cmd
# Solução 1: Instalar psycopg2-binary
pip install psycopg2-binary

# Solução 2: Se ainda falhar, instalar Visual C++ Build Tools
# Download: https://visualstudio.microsoft.com/visual-cpp-build-tools/
```

### **❌ Problema: PowerShell não executa scripts**
```powershell
# Solução: Permitir execução (como Administrador)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Verificar política atual
Get-ExecutionPolicy
```

### **❌ Problema: Porta já em uso**
```cmd
# Verificar o que está usando a porta 8080
netstat -ano | findstr :8080

# Matar processo (substitua PID pelo número encontrado)
taskkill /PID <PID> /F

# Ou usar portas diferentes no docker-compose.yml
```

### **❌ Problema: Erro de permissão do Docker**
```cmd
# Solução: Verificar se Docker Desktop está rodando
# Se ainda falhar, reiniciar Docker Desktop
```

## 📊 **Verificação Final**

### **✅ Banco Funcionando**
```cmd
# Banco deve estar acessível em:
# Host: localhost
# Porta: 5434
# Database: pokedex_db
# Username: pokedx_user
# Password: pokedx_password
```

### **✅ Aplicação Funcionando**
```cmd
# Aplicação deve estar acessível em:
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# Health: http://localhost:8080/actuator/health
```

### **🧪 Teste Rápido**
```cmd
# Testar endpoint principal
curl http://localhost:8080/api/v1/pokemons?page=0&size=10

# Ou abrir no navegador:
# http://localhost:8080/swagger-ui.html
```

## 🎯 **Recomendação Final**

Para **iniciantes**: Use **Opção 1 (Scripts Batch)** - é plug & play!

Para **usuários avançados**: Use **Opção 3 (Comandos Diretos)** - máximo controle!

Para **PowerShell lovers**: Use **Opção 2 (PowerShell)** - sintaxe moderna!

---

> 💡 **Dica**: Todos os scripts incluem validações e mensagens de erro claras. Se algo der errado, leia a mensagem - ela indica exatamente o que fazer!