# Temporal Analysis Complexity — USEI08

## 1. Estrutura de Dados: 2D-Tree (KD-Tree)
A pesquisa é feita sobre um 2D-tree equilibrado, onde cada nível divide alternadamente por latitude e longitude.  
As decisões de poda são feitas em O(1) por nó visitado.

## 2. Range Search na Região [latMin, latMax] × [lonMin, lonMax]
A travessia processa apenas:
- nós cujo retângulo pode intersectar a região procurada;
- nós totalmente dentro da região (aceitação imediata);
- nós totalmente fora (poda imediata).

A poda reduz o número de nós visitados para **O(√n)** num 2D-tree equilibrado.

## 3. Aplicação de Filtros
Cada estação encontrada pode ser filtrada por:
- `isCity`
- `isMainStation`
- `country`

O custo de cada filtro é **O(1)**, logo o custo total associado a resultados é **O(k)**.

## 4. Custo de Produção da Lista Final
Cada estação válida é apenas adicionada ao resultado em O(1).  
Não existe ordenação adicional obrigatória na especificação, logo não há custo extra como no USEI06.

## 5. Complexidade Final
A soma das parcelas da pesquisa é:

- Nós visitados pela poda do KD-tree: **O(√n)**
- Processamento dos resultados (com filtros): **O(k)**

### **Overall Temporal Complexity:**  
**O(√n + k)**

## 6. Tabela Resumo
| Operação                         | Complexidade |
|----------------------------------|--------------|
| Travessia do 2D-tree (poda)      | O(√n)        |
| Verificação de filtros           | O(k)         |
| Construção da lista de resultados| O(k)         |
| **Total**                        | **O(√n + k)** |