package com.mycompany.proyecto1;

import java.util.List;
import java.util.Map;

/**
 * Clase para encapsular los resultados de una búsqueda BFS,
 * incluyendo si se encontró un camino, el camino encontrado y el mapa de padres para la visualización.
 */
public class ResultadoBFS {
    private final boolean pathFound; // <--- Esta variable es CRÍTICA y debe estar aquí.
    private final List<Node> foundPath;
    private final Map<Node, Node> parentMap; // Mapa de padres para reconstruir el árbol de búsqueda para visualización
    private final Node startNode; // Nodo desde el que se inició la búsqueda

    /**
     * Constructor para un resultado de BFS.
     * @param pathFound true si se encontró el camino.
     * @param foundPath La lista de nodos que forman el camino encontrado.
     * @param parentMap El mapa de padres para la visualización del recorrido.
     * @param startNode El nodo inicial de la búsqueda.
     */
    public ResultadoBFS(boolean pathFound, List<Node> foundPath, Map<Node, Node> parentMap, Node startNode) {
        this.pathFound = pathFound;
        this.foundPath = foundPath;
        this.parentMap = parentMap;
        this.startNode = startNode;
    }

    /**
     * Indica si se encontró un camino.
     * @return true si el camino fue encontrado, false en caso contrario.
     */
    public boolean isPathFound() {
        return pathFound;
    }

    /**
     * Obtiene el camino encontrado.
     * @return Una lista de nodos que forman el camino. Vacía si no se encontró el camino.
     */
    public List<Node> getFoundPath() {
        return foundPath;
    }

    /**
     * Obtiene el mapa de padres, útil para reconstruir el árbol de búsqueda para visualización.
     * @return Un mapa donde la clave es un nodo y el valor es su nodo padre en el recorrido BFS.
     */
    public Map<Node, Node> getParentMap() {
        return parentMap;
    }

    /**
     * Obtiene el nodo desde el que se inició la búsqueda.
     * @return El nodo inicial.
     */
    public Node getStartNode() {
        return startNode;
    }
}