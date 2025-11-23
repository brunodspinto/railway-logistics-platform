# Temporal Analysis Complexity - USEI09

## 1. Estrutura de Dados: 2D-Tree (KD-Tree)

A procura dos N vizinhos mais próximos é executada sobre um 2D-tree equilibrado, onde cada nó alterna entre divisões em latitude e longitude.

As decisões pruning usam apenas comparações simples e são feitas em **O(1)**.

## 2. Nearest N Search com Pruning

O algoritmo percorre apenas:

* nós cujo hiperplano de separação pode conter pontos mais próximos do que os já encontrados;
* nós onde é possível que um ponto esteja dentro da melhor distância atual (armazenada no MAX-heap);
* evita subárvores inteiras quando a distância mínima possível é maior que a pior das N melhores encontradas.

Num 2D-tree equilibrado, o custo típico é:

**O(√n)** nós visitados.

Este valor é amplamente citado na análise clássica de KD-trees.

## 3. Gestão de Melhores Resultados (MAX-Heap)

Para manter os N candidatos mais próximos, usa-se um MAX-heap:

* Inserção no heap: **O(log N)**
* Substituição do pior elemento: **O(log N)**

Como no máximo são inseridos N elementos e depois apenas comparados a novos candidatos, o custo é:

**O(N log N + v log N)**
onde v é o número de nós visitados **(≈ √n)**.

## 4. Aplicação de Filtros

Cada estação encontrada é testada por um Predicate 'station', composto de filtros como:

* timeZoneGroup
* country

O custo de cada filtro é **O(1)**.

Logo, o custo total associado aos k pontos candidatos é:

**O(k)**.

## 5. Custo de Ordenação da Lista Final

Depois de terminar a pesquisa:

* Converte-se o heap numa lista: **O(N)**
* Ordena-se por distância ASC e nome DESC: **O(N log N)**

## 6. Complexidade Final

Somando as componentes:

* Nós visitados: **O(√n)**
* Operações no heap: **O((√n + N) log N)**
* Filtros: **O(k)**
* Ordenação final: **O(N log N)**

### Overall Temporal Complexity:

**O(√n + (N log N))**

## 7. Tabela Resumo


| Operação                                | Complexidade         |
| ----------------------------------------- | -------------------- |
| Travessia do KD-Tree (pruning)            | O(√n)               |
| Inserções / substituições no MAX-heap | O((√n + N) log N)   |
| Verificação de filtros                  | O(k)                 |
| Ordenação final dos resultados          | O(N log N)           |
| **Total**                                 | **O(√n + N log N)** |
