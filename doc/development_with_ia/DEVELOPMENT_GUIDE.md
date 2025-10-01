# GUIA DE USO DE IA NO DESENVOLVIMENTO

## 🤖 **Visão Geral**

Este guia estabelece as **normas e melhores práticas** para usar ferramentas de IA (GitHub Copilot, ChatGPT, Claude, etc.) no desenvolvimento do projeto Pokédex BFF.

## 📋 **Normas Obrigatórias**

### **1. Template Padrão**
- ✅ **SEMPRE** use o template em `doc/ai/PROMPT_TEMPLATE.md`
- ✅ **SEMPRE** referencie `doc/CONTEXT.md` no prompt
- ✅ **SEMPRE** especifique restrições arquiteturais

### **2. Contexto Arquitetural**
- ✅ Mencione **Clean Architecture + Hexagonal Architecture**
- ✅ Especifique camada alvo (domain/application/infrastructure/interfaces)
- ✅ Cite princípios SOLID aplicáveis

### **3. Validação Obrigatória**
- ✅ Revisar código gerado contra `doc/development/STYLE_GUIDE.md`
- ✅ Verificar separação domain/infrastructure
- ✅ Executar `./gradlew build` e `./gradlew test`
- ✅ Atualizar documentação se necessário

## 🎯 **Casos de Uso Permitidos**

### ✅ **Implementação de Features**
```markdown
**Exemplo:**
- Criar novo Use Case para busca por tipo
- Implementar Value Object para stats de Pokémon
- Adicionar adapter para API externa
```

### ✅ **Refatoração**
```markdown
**Exemplo:**
- Extrair lógica para Domain Service
- Melhorar separação de responsabilidades
- Otimizar queries de repository
```

### ✅ **Testes**
```markdown
**Exemplo:**
- Gerar testes unitários para Value Objects
- Criar mocks para Use Cases
- Implementar testes de integração
```

### ✅ **Documentação**
```markdown
**Exemplo:**
- Atualizar ADRs (Architecture Decision Records)
- Gerar documentação de API
- Criar diagramas arquiteturais
```

## ❌ **Casos de Uso Proibidos**

### ❌ **Violações Arquiteturais**
- Misturar domain com infrastructure
- Criar dependências circulares
- Anotações JPA em domain entities

### ❌ **Código Sem Contexto**
- Gerar código sem ler documentação
- Ignorar princípios estabelecidos
- Não considerar arquitetura existente

### ❌ **Alterações Destrutivas**
- Refatorações sem testes
- Mudanças que quebram contratos
- Alterações sem backup/versionamento

## 🛠️ **Ferramentas Recomendadas**

### **GitHub Copilot**
- ✅ **Ideal para**: Autocompletar seguindo padrões existentes
- ✅ **Usar com**: Comentários descritivos da arquitetura
- ⚠️ **Cuidado**: Revisar sugestões contra princípios

### **ChatGPT/Claude**
- ✅ **Ideal para**: Planejamento arquitetural e refatorações
- ✅ **Usar com**: Template completo e contexto
- ⚠️ **Cuidado**: Validar contra documentação existente

### **Cursor/Continue**
- ✅ **Ideal para**: Desenvolvimento assistido com contexto
- ✅ **Usar com**: Base de código carregada
- ⚠️ **Cuidado**: Manter princípios arquiteturais

## 📚 **Workflow Recomendado**

### **1. Preparação (5 min)**
```bash
# 1. Ler contexto atual
cat doc/CONTEXT.md

# 2. Verificar style guide
cat doc/development/STYLE_GUIDE.md

# 3. Preparar template
cp doc/ai/PROMPT_TEMPLATE.md /tmp/my_prompt.md
```

### **2. Elaboração do Prompt (10 min)**
- Preencher template com contexto específico
- Incluir exemplos de código desejado
- Especificar testes esperados
- Definir critérios de aceite

### **3. Implementação Assistida (variável)**
- Usar IA para gerar código base
- Revisar contra style guide
- Ajustar para seguir Clean Architecture
- Implementar testes correspondentes

### **4. Validação (5 min)**
```bash
# 1. Compilação
./gradlew build

# 2. Testes
./gradlew test

# 3. Análise estática (se configurado)
./gradlew check
```

### **5. Documentação (5 min)**
- Atualizar docs relevantes
- Commitar com mensagem descritiva
- Atualizar CONTEXT.md se necessário

## 🎯 **Exemplos Práticos**

### **Implementar Novo Use Case**
```markdown
**Contexto:** Clean Architecture, camada application
**Prompt:** "Implemente SearchPokemonByTypeUseCase seguindo padrão 
de GetPaginatedPokemonsUseCase, com validações e testes unitários"
```

### **Criar Value Object**
```markdown
**Contexto:** Clean Architecture, camada domain
**Prompt:** "Crie PokemonStats Value Object seguindo padrão de 
PokemonNumber, com validações de stats base (HP, Attack, etc)"
```

### **Refatorar Repository**
```markdown
**Contexto:** Clean Architecture, separação domain/infrastructure
**Prompt:** "Refatore PokemonRepository para melhor separação,
mantendo interface no domain e implementação na infrastructure"
```

## ⚠️ **Checklist Final**

Antes de aceitar código gerado por IA:

- [ ] Segue Clean Architecture rigorosamente?
- [ ] Mantém separação domain/infrastructure?
- [ ] Inclui testes unitários adequados?
- [ ] Compila sem erros ou warnings?
- [ ] Está documentado adequadamente?
- [ ] Segue style guide do projeto?
- [ ] Atualiza CONTEXT.md se necessário?

## 🚀 **Benefícios Esperados**

Seguindo estas normas, você deve obter:

- ✅ **Código consistente** com arquitetura existente
- ✅ **Produtividade aumentada** sem sacrificar qualidade
- ✅ **Menos retrabalho** por violações arquiteturais
- ✅ **Documentação mantida** automaticamente
- ✅ **Testes abrangentes** desde o início
