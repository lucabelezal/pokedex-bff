# 📦 Value Objects vs DTOs - Guia de Decisão

## 🎯 **Quando Usar Cada Abordagem?**

Este documento explica quando usar **Value Objects** (DDD/Clean Architecture) versus **DTOs** (REST APIs, boundaries) no contexto de desenvolvimento de APIs.

---

## 🏗️ **Contextos Arquiteturais**

### **📦 Value Objects = DDD / Clean Architecture**
- **Propósito**: Conceitos ricos de domínio com comportamento
- **Onde**: Camada de domínio (domain layer)
- **Quando**: Aplicações com lógica de negócio relevante

### **📄 DTOs = API Boundaries**  
- **Propósito**: Transferência de dados entre camadas
- **Onde**: Controllers REST, Use Cases, Application Layer
- **Quando**: APIs REST, BFFs, integração entre camadas

---

## ⚖️ **Matriz de Decisão**

### **✅ Use Value Objects quando:**
- Precisa encapsular regras de negócio
- Precisa de validação e comportamento
- Reutilização em múltiplos contextos do domínio

### **✅ Use DTOs quando:**
- Precisa transferir dados entre camadas (REST, application)
- Serialização/deserialização (JSON, XML)
- Formatação para UI

---

## 📊 **No Pokédex BFF**
- Value Objects são usados apenas no domínio
- DTOs são usados apenas em boundaries (REST, application)
- Nunca exponha entidades ou value objects do domínio diretamente em APIs públicas

Consulte exemplos reais no README.md e doc/OVERVIEW.md.