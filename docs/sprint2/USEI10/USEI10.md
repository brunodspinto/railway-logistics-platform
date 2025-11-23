# Temporal Analysis Complexity — USEI10 (Radius Search)

## 1. Estrutura de Dados: 2D-Tree (KD-Tree)

A pesquisa radial usa a mesma estrutura 2D-Tree equilibrada.
Cada nó decide de forma constante (**O(1)**):

* se o seu plano de divisão pode intersetar o círculo de raio R;
* se toda a subárvore pode ser descartada.

## 2. Radius Search dentro de R km

A procura processa apenas:

* nós dentro do raio (aceitação imediata);
* nós cujo retângulo mínimo pode intersetar o círculo (pruning adiada);
* ignora por completo subárvores fora da zona.

A complexidade típica documentada para range search em 2D-trees é:

**O(√n + k)**

onde k é o número de estações devolvidas.

## 3. Construção dos Resultados

Cada estação válida é:

* Testada com Harvesine -> custo **O(1)**
* inserida num AVL Tree com chave (distância, nome) -> custo **O(log k)** por inserção

Total:
**O(k log k)**

## 4. Construção das Estatísticas (byCountry / byIsCity)

HashMap.merge() executa em **O(1)**.

Para k elementos:
**O(k)**.

## 5. Complexidade Final

Somando as componentes:

* Travessia com poda no KD-Tree → **O(√n)**
* Cálculo de distâncias e filtros → **O(k)**
* Inserção no AVL ordenado → **O(k log k)**

### Overall Temporal Complexity:

**O(√n + k log k)**

## 6. Tabela Resumo


| Operação                     | Complexidade         |
| ------------------------------ | -------------------- |
| Travessia do KD-Tree (pruning) | O(√n)               |
| Teste de distância + filtros  | O(k)                 |
| Inserções no AVL Tree        | O(k log k)           |
| Atualização de HashMaps      | O(k)                 |
| **Total**                      | **O(√n + k log k)** |
