# 📦 Value Objects vs DTOs - Guia de Decisão

## 🎯 **Quando Usar Cada Abordagem?**

Este documento explica quando usar **Value Objects** (Clean Architecture/DDD) versus **DTOs** (MVC/REST APIs) no contexto de desenvolvimento de APIs.

---

## 🏗️ **Contextos Arquiteturais**

### **📦 Value Objects = Clean Architecture / DDD**
- **Propósito**: Conceitos ricos de domínio com comportamento
- **Onde**: Camada de domínio (domain layer)
- **Quando**: Aplicações complexas com lógica de negócio rica

### **📄 DTOs = MVC / API REST**  
- **Propósito**: Transferência de dados entre camadas
- **Onde**: Controllers, Services, API boundaries
- **Quando**: APIs REST, microserviços, BFFs

---

## ⚖️ **Matriz de Decisão**

### **✅ Use Value Objects quando:**

| Critério | Exemplo Pokemon | Justificativa |
|----------|----------------|---------------|
| **Lógica complexa** | `Money(100.50, "BRL").convertTo("USD")` | Cálculos monetários |
| **Múltiplas validações** | `Email("user@domain.com").isValid()` | Regras específicas |
| **Comportamentos ricos** | `PokemonNumber("025").getGeneration()` | Lógica de negócio |
| **Reutilização alta** | `UserId` usado em 10+ places | Evita duplicação |
| **Domain expertise** | Sistema bancário, e-commerce | Domínio complexo |

### **✅ Use DTOs quando:**

| Critério | Exemplo Pokemon | Justificativa |
|----------|----------------|---------------|
| **Transferência simples** | `PokemonResponse(id, name, number)` | API boundaries |
| **Serialização** | JSON/XML para REST APIs | Comunicação |
| **Validação de entrada** | `@Valid CreatePokemonRequest` | Bean Validation |
| **Formatação para UI** | `"#025"` vs `"025"` | Apresentação |
| **BFF/Microserviços** | Pokedex BFF | Arquitetura simples |

---

## 📊 **Análise do Pokédx BFF**

### **🔍 Análise dos Value Objects Atuais:**

```kotlin
// ❌ Value Object atual (over-engineering)
@JvmInline
value class PokemonNumber(val value: String) {
    init { 
        require(value.matches(Regex("\\d{1,4}"))) { "Invalid format" }
    }
    
    fun formatForDisplay(): String = value.padStart(3, '0')
    fun toDisplayString(): String = "Nº${formatForDisplay()}"
    fun getGeneration(): Int = when(toNumeric()) { /* ... */ }
    // 73 linhas de código para algo simples!
}

// ✅ Alternativa simples com DTO
data class PokemonResponse(
    val id: Long,
    val number: String,  // Já formatado no DTO
    val name: String
) {
    companion object {
        fun from(entity: PokemonEntity): PokemonResponse {
            return PokemonResponse(
                id = entity.id,
                number = entity.number?.let { "#${it.padStart(3, '0')}" } ?: "#UNK",
                name = entity.name
            )
        }
    }
}
```

### **📈 Resultado da Análise:**

| Aspecto | Value Objects | DTOs | Vencedor |
|---------|---------------|------|----------|
| **Simplicidade** | ❌ 73 linhas | ✅ 15 linhas | DTOs |
| **Manutenibilidade** | ❌ Complexo | ✅ Direto | DTOs |
| **Testabilidade** | ❌ Mocks extras | ✅ Testes simples | DTOs |
| **Performance** | ❌ Overhead | ✅ Direto | DTOs |
| **Uso real** | ❌ Não usado | ✅ Usado | DTOs |

---

## 🎯 **Recomendações por Tipo de Projeto**

### **📦 Use Value Objects em:**
```kotlin
// ✅ Sistema financeiro
Money(amount = 1500.50, currency = "BRL")
    .convertTo("USD")
    .applyTax(0.15)
    .formatForDisplay() // "US$ 275.59"

// ✅ E-commerce complexo  
ProductCode("SKU-123-ABC")
    .validateChecksum()
    .getCategory() // "Electronics"
    .isPremium() // true

// ✅ Sistema médico
PatientId(uuid)
    .anonymize()
    .validateGDPRCompliance()
```

### **📄 Use DTOs em:**
```kotlin
// ✅ APIs REST simples
data class PokemonResponse(val id: Long, val name: String)

// ✅ BFFs e microserviços
data class UserProfileResponse(val email: String, val displayName: String)

// ✅ CRUD básico
data class CreateProductRequest(val name: String, val price: Double)
```

---

## 🔄 **Migração: Value Objects → DTOs**

### **Passo 1: Identificar Value Objects sem valor**
```bash
# Buscar VOs no projeto
find src/ -name "*ValueObject*.kt" -o -name "valueobjects/*.kt"
```

### **Passo 2: Analisar uso real**
```kotlin
// ❌ VO não usado nas entidades reais?
@Entity
class PokemonEntity(
    val number: String  // Primitivo, não usa PokemonNumber VO
)

// ❌ Lógica duplicada em Use Cases?
fun formatPokemonNumber(number: String): String {
    return "Nº$number"  // Mesma lógica do VO!
}
```

### **Passo 3: Simplificar para DTOs**
```kotlin
// ✅ DTO com formatação simples
data class PokemonResponse(val number: String) {
    companion object {
        fun from(entity: PokemonEntity): PokemonResponse {
            return PokemonResponse(
                number = entity.number?.let { "#${it.padStart(3, '0')}" } ?: "#UNK"
            )
        }
    }
}

// ✅ Validação no Service
@Service
class PokemonService {
    fun createPokemon(request: CreatePokemonRequest) {
        require(request.number.matches(Regex("\\d{1,4}"))) { 
            "Invalid pokemon number format" 
        }
        // ...
    }
}
```

---

## 📋 **Checklist de Decisão**

### **Antes de criar um Value Object, pergunte:**

- [ ] Este conceito tem **3+ comportamentos específicos** de domínio?
- [ ] A lógica é **reutilizada em 5+ lugares** diferentes?
- [ ] Existem **regras de negócio complexas** envolvidas?
- [ ] O valor **muda frequentemente** e precisa de encapsulamento?
- [ ] O domínio é **complexo o suficiente** para justificar a abstração?

**Se respondeu NÃO para a maioria**: **Use DTOs simples**

### **Antes de criar um DTO, pergunte:**

- [ ] Preciso **transferir dados** entre camadas?
- [ ] A API precisa de **formato específico** de resposta?
- [ ] Existem **validações de entrada** necessárias?
- [ ] O dado será **serializado** (JSON/XML)?
- [ ] A formatação é **específica para apresentação**?

**Se respondeu SIM para a maioria**: **Use DTOs**

---

## 🎯 **Conclusão para Pokédx BFF**

### **✅ Recomendação: DTOs Only**

**Razões:**
1. **Domínio simples**: Pokémon não tem lógica complexa
2. **BFF pattern**: Focado em formatação para UI
3. **API REST**: DTOs são naturais
4. **MVC estruturado**: Alinhado com arquitetura escolhida
5. **Manutenibilidade**: Código mais simples e direto

### **🔄 Ação: Remover Value Objects**

```bash
# Passos de limpeza
rm -rf src/main/kotlin/com/pokedex/bff/domain/valueobjects/
# Atualizar imports
# Simplificar para DTOs
# Mover validações para Services
```

---

## 📚 **Referências**

- **Value Objects**: Domain-Driven Design by Eric Evans
- **DTOs**: Patterns of Enterprise Application Architecture by Martin Fowler  
- **Clean Architecture**: Clean Architecture by Robert C. Martin
- **MVC REST APIs**: Spring Boot Best Practices

---

*Documento criado para auxiliar decisões arquiteturais - Janeiro 2025*