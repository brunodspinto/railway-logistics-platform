# Temporal Analysis Complexity — USEI13

## 1. Estrutura de Dados: Grafo de Estações

- Grafo **não dirigido** e ponderado.
- Representação híbrida:
    - lista global de arestas;
    - listas de adjacência por estação.
- `|V|` = número de estações
- `|E|` = número de ligações (arestas) com distância.

Iterar todas as estações é `O(V)` e todas as arestas é `O(E)`.

---

## 2. Cálculo dos Caminhos Mínimos

As métricas de centralidade requerem o cálculo dos caminhos mínimos entre pares de estações.

### Algoritmo utilizado: Dijkstra

O algoritmo é executado **uma vez por estação**, considerando cada estação como origem.

Para uma execução de Dijkstra:

- utilização de fila de prioridade (`PriorityQueue`);
- acesso aos vizinhos através de listas de adjacência.

Complexidade de uma execução:

- operações na fila de prioridade: `O(log V)`;
- processamento das arestas: `O(E)`.

Logo, uma execução tem custo:

> **`O(E log V)`**

Executando Dijkstra para todas as estações:

> **Custo total dos caminhos mínimos:**  
> **`O(V · E log V)`**

---

## 3. Cálculo das Métricas de Centralidade

### Degree e Strength

- Percorre-se a lista global de arestas uma única vez.
- Atualização direta dos valores associados às estações.

Complexidade:

> **`O(E)`**

---

### Harmonic Closeness

- Para cada estação, percorrem-se todas as distâncias calculadas.
- Número total de pares `(s, t)` é `V²`.

Complexidade:

> **`O(V²)`**

Este custo é dominado pelo cálculo prévio dos caminhos mínimos.

---

### Betweenness (abordagem simplificada)

- Triplos ciclos sobre:
    - estação origem `s`,
    - estação destino `t`,
    - estação intermediária `v`.
- As consultas às distâncias são feitas em tempo constante.

Complexidade:

> **`O(V³)`**

Nota: Não é utilizado o algoritmo de Brandes, sendo adotada uma abordagem direta
baseada na verificação de caminhos mínimos previamente calculados.

---

## 4. Cálculo do HubScore

- Normalização das métricas.
- Cálculo de uma combinação linear por estação.

Complexidade:

> **`O(V)`**

---

## 5. Complexidade Final

Somando as parcelas dominantes:

- Caminhos mínimos (Dijkstra para todas as estações): **`O(V · E log V)`**
- Betweenness: **`O(V³)`**
- Restantes métricas: `O(E)` e `O(V²)`

Complexidade temporal total da USEI13:

> **`T(|V|, |E|) = O(V³ + V · E log V)`**

Em grafos densos (`E ≈ V²`), o termo dominante é:

> **`O(V³)`**