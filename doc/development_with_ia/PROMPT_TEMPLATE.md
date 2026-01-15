# Template de Prompt para Copilot Space

## 1. Contexto do Projeto
Descreva brevemente o objetivo do projeto, stack principal e se há padrões/arquiteturas em uso.  
**Exemplo:**  
> Projeto BFF para app de Pokédex, backend em Kotlin + Spring Boot, seguindo Clean Architecture e DDD.

---

## 2. Tipo de Solicitação
Escolha e destaque **um** dos tipos abaixo:

- [ ] Refatoração
- [ ] Correção de bug
- [ ] Nova funcionalidade
- [ ] Revisão de código
- [ ] Outro: _______

---

## 3. Descrição Detalhada da Solicitação

### a) Refatoração  
- Explique o objetivo da refatoração (ex: melhorar coesão, separar camadas, reduzir acoplamento).
- Cite pontos fracos atuais e resultados esperados.
- Se possível, indique as classes, pacotes ou métodos-alvo.

### b) Correção de Bug  
- Descreva o comportamento atual e o esperado.
- Inclua stack trace, prints, ou contexto do erro.
- Indique arquivos/pacotes afetados.

### c) Nova Funcionalidade  
- Explique a feature desejada, fluxos de entrada/saída e regras.
- Aponte endpoints, entidades e integrações afetadas.

### d) Revisão de Código  
- Indique arquivos/PRs ou trechos para revisar.
- Peça sugestões de boas práticas, SOLID, DDD, testes, etc.

---

## 4. Restrições Técnicas e Arquiteturais
Liste requisitos obrigatórios, padrões a seguir, bibliotecas preferidas ou limitações.  
**Exemplo:**  
> Usar apenas JPA, evitar dependências externas, não modificar entidades do domínio diretamente, etc.

---

## 5. Exemplos e Testes
Se possível, inclua exemplos de entrada/saída, casos de teste esperados ou cenários de uso.

---

## 6. Output Esperado
Diga se espera:
- Código pronto (e linguagem preferida)
- Justificativas arquiteturais
- Sugestão de organização de pacotes
- Exemplos de testes
- Alertas de más práticas
- Outros detalhes

---

## 7. Observações Adicionais
Inclua qualquer detalhe, contexto, links de referência, ou limitações.

---

### Dicas para Preenchimento

- Seja objetivo, mas inclua contexto (quem usa, para quê, restrições).
- Liste claramente o objetivo da solicitação.
- Para refatoração, sempre explique o motivo e o resultado esperado.
- Para correção, forneça o máximo de detalhes do erro.
- Para novas features, pense como um caso de uso: início, processamento, fim, exceções.
- Se quiser sugestões arquiteturais, peça explicitamente.
- Marque se deseja exemplos de testes.

---

## Exemplo Preenchido

> **Contexto:**  
> Microserviço em Kotlin, Spring Boot, Clean Architecture. Responsável por buscar pokémons e evoluções.
>
> **Solicitação:**  
> [X] Refatoração
>
> **Descrição:**  
> Quero separar melhor as responsabilidades entre domínio e infraestrutura. Atualmente, o repositório contém lógicas de negócio e integrações externas misturadas. Esperado: manter domínio puro e isolar detalhes técnicos.
>
> **Restrições:**  
> Usar interfaces para comunicação entre camadas. Não adicionar frameworks novos.
>
> **Exemplo/Teste:**  
> Buscar pokémon por ID, garantir que regras de negócio estão no domínio.
>
> **Output:**  
> Sugira nova estrutura de pacotes, exemplos de código, justificativa arquitetural.
>
> **Obs:**  
> Foco em baixo acoplamento e testabilidade.

---

