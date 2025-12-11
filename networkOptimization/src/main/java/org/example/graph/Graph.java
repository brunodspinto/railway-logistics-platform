package org.example.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Interface para estrutura de dados Grafo
 * Baseada em ESINF06-Graph.pdf (slides 25-26)
 *
 * @param <V> Tipo dos vértices
 * @param <E> Tipo das arestas (peso/informação)
 */
public interface Graph<V, E> extends Cloneable {

    /**
     * Retorna se o grafo é dirigido
     */
    boolean isDirected();

    /**
     * Retorna o número de vértices do grafo
     */
    int numVertices();

    /**
     * Retorna todos os vértices do grafo
     */
    ArrayList<V> vertices();

    /**
     * Valida se um vértice pertence ao grafo
     */
    boolean validVertex(V vert);

    /**
     * Retorna a chave (índice) de um vértice
     */
    int key(V vert);

    /**
     * Retorna o vértice com uma dada chave
     */
    V vertex(int key);

    /**
     * Retorna o primeiro vértice que satisfaz o predicado
     */
    V vertex(Predicate<V> p);

    /**
     * Retorna os vértices adjacentes a um dado vértice
     */
    Collection<V> adjVertices(V vert);

    /**
     * Retorna o número de arestas do grafo
     */
    int numEdges();

    /**
     * Retorna todas as arestas do grafo
     */
    Collection<Edge<V, E>> edges();

    /**
     * Retorna a aresta entre dois vértices (ou null se não existir)
     */
    Edge<V, E> edge(V vOrig, V vDest);

    /**
     * Retorna a aresta entre dois vértices dados pelas chaves
     */
    Edge<V, E> edge(int vOrigKey, int vDestKey);

    /**
     * Retorna o grau de saída (out-degree) de um vértice
     */
    int outDegree(V vert);

    /**
     * Retorna o grau de entrada (in-degree) de um vértice
     */
    int inDegree(V vert);

    /**
     * Retorna as arestas que saem de um vértice
     */
    Collection<Edge<V, E>> outgoingEdges(V vert);

    /**
     * Retorna as arestas que entram num vértice
     */
    Collection<Edge<V, E>> incomingEdges(V vert);

    /**
     * Adiciona um vértice ao grafo
     * @return true se adicionado com sucesso, false caso contrário
     */
    boolean addVertex(V vert);

    /**
     * Adiciona uma aresta ao grafo
     * @return true se adicionado com sucesso, false caso contrário
     */
    boolean addEdge(V vOrig, V vDest, E weight);

    /**
     * Remove um vértice do grafo
     * @return true se removido com sucesso, false caso contrário
     */
    boolean removeVertex(V vert);

    /**
     * Remove uma aresta do grafo
     * @return true se removido com sucesso, false caso contrário
     */
    boolean removeEdge(V vOrig, V vDest);

    /**
     * Clona o grafo
     */
    Graph<V, E> clone();
}
