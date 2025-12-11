# Temporal Analysis Complexity — USEI12

## 1. Estrutura de Dados: Grafo de Estações

- Grafo não dirigido.
- Representação em listas de adjacência.
- `|V|` = número de estações
- `|E|` = número de ligações (arestas) com distância.

Iterar todos os vértices é `O(V)` e todas as arestas é `O(E)`.

---

## 2. Algoritmo para a Minimal Backbone Network

A Minimal Backbone Network é uma **Árvore Geradora Mínima (MST)** construída sobre o grafo tratado como não dirigido.

### Algoritmo escolhido: Kruskal

1. **Ordenação das arestas por comprimento**
    - Todas as arestas são colocadas numa lista e ordenadas por peso.
    - Custo: **`O(E log E)`**.

2. **Seleção das arestas com Union–Find**
    - Percorre-se a lista ordenada.
    - Para cada aresta `(u, v)` faz-se `find(u)` e `find(v)`:
        - se pertencem a componentes diferentes → `union(u, v)` e a aresta entra na MST;
        - caso contrário, é ignorada.
    - Com Union–Find com path compression e union by rank:
        - cada operação é `O(α(V)) ≈ O(1)`;
        - total: **`O(E)`**.

Passo dominante: **ordenar as arestas**.

---

## 3. Produção dos Resultados (Grafo / DOT / SVG)

Sobre a MST obtida:

- Construção do grafo resultado (MST): `O(E_MST)` com `E_MST = V − 1`.
- Geração do ficheiro DOT:
    - escrever todos os vértices e arestas → **`O(V + E_MST)`**.
- A chamada externa ao `neato` (SVG) não é considerada na análise assintótica do algoritmo.

Logo, esta fase é **`O(V + E)`**, dominada por `O(E log E)` da fase de cálculo.

---

## 4. Complexidade Final

Somando as parcelas dominantes:

- Ordenação das arestas: **`O(E log E)`**
- Operações de Union–Find: **`O(E)`**

Complexidade temporal total da USEI12 (Kruskal):

> **`T(|V|, |E|) = O(E log E)`**  
> (equivalente a **`O(E log V)`**, dado que `E ≥ V − 1` em grafos conexos).