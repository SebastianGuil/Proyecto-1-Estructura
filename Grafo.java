package com.mycompany.proyecto1;

import java.util.*;

/**
 * Representa el tablero de la sopa de letras como un grafo,
 * permitiendo la búsqueda de palabras usando algoritmos DFS y BFS.
 */
public class Grafo {
    private Node[][] nodes; // Matriz de nodos que representa el tablero
    private int rows;       // Número de filas del tablero
    private int cols;       // Número de columnas del tablero

    // Direcciones de los vecinos (8 direcciones: horizontal, vertical, diagonal)
    private final int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
    private final int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

    /**
     * Constructor para el grafo.
     * Inicializa el grafo a partir de una matriz de caracteres (el tablero).
     * @param board La matriz de caracteres del tablero de la sopa de letras.
     */
    public Grafo(char[][] board) {
        this.rows = board.length;
        this.cols = board[0].length;
        this.nodes = new Node[rows][cols];

        // Crear nodos para cada celda del tablero
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                nodes[r][c] = new Node(board[r][c], r, c);
            }
        }
    }

    /**
     * Verifica si una posición (r, c) es válida dentro de los límites del tablero.
     * @param r Fila a verificar.
     * @param c Columna a verificar.
     * @return true si la posición es válida, false en caso contrario.
     */
    private boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Realiza una búsqueda en profundidad (DFS) para encontrar una palabra en el grafo.
     * @param startNode El nodo desde el cual iniciar la búsqueda.
     * @param word La palabra a buscar.
     * @param index El índice actual del carácter de la palabra que se está buscando.
     * @param path La lista de nodos que forman el camino actual.
     * @param visited El conjunto de nodos ya visitados en el camino actual para evitar ciclos.
     * @return true si se encuentra la palabra, false en caso contrario.
     */
    private boolean dfs(Node startNode, String word, int index, List<Node> path, Set<Node> visited) {
        // Si el carácter actual del nodo no coincide con el carácter de la palabra
        if (startNode.getValue() != word.charAt(index)) {
            return false;
        }

        // Si se encontró el último carácter de la palabra
        if (index == word.length() - 1) {
            path.add(startNode);
            return true;
        }

        // Marcar el nodo como visitado para el camino actual
        visited.add(startNode);
        path.add(startNode); // Añadir el nodo al camino

        // Explorar vecinos
        for (int i = 0; i < 8; i++) {
            int newRow = startNode.getRow() + dRow[i];
            int newCol = startNode.getCol() + dCol[i];

            if (isValid(newRow, newCol)) {
                Node neighbor = nodes[newRow][newCol];
                // Si el vecino no ha sido visitado en este camino
                if (!visited.contains(neighbor)) {
                    if (dfs(neighbor, word, index + 1, path, visited)) {
                        return true; // Se encontró la palabra en un camino descendente
                    }
                }
            }
        }

        // Si la palabra no se encontró a partir de este nodo, retroceder (backtrack)
        path.remove(path.size() - 1); // Quitar el nodo del camino
        visited.remove(startNode);    // Desmarcar como visitado para que otros caminos puedan usarlo
        return false;
    }

    /**
     * Busca una palabra en el tablero usando el algoritmo DFS.
     * Este método es público y se encarga de iniciar la búsqueda DFS desde todos los posibles nodos iniciales.
     * @param word La palabra a buscar.
     * @return Una lista de nodos que forman el camino si la palabra es encontrada; una lista vacía en caso contrario.
     */
    public List<Node> searchWordDFS(String word) {
        if (word == null || word.isEmpty()) {
            return Collections.emptyList();
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                List<Node> path = new ArrayList<>();
                Set<Node> visited = new HashSet<>();
                if (dfs(nodes[r][c], word, 0, path, visited)) {
                    return path; // Retorna el primer camino encontrado
                }
            }
        }
        return Collections.emptyList(); // Palabra no encontrada en ningún camino
    }

    /**
     * Realiza una búsqueda en amplitud (BFS) para encontrar una palabra en el grafo,
     * devolviendo solo el camino. Útil para búsquedas del diccionario sin visualización detallada.
     *
     * @param word La palabra a buscar.
     * @return Una lista de nodos que forman el camino si la palabra es encontrada; una lista vacía en caso contrario.
     */
    public List<Node> searchWordBFS(String word) {
        if (word == null || word.isEmpty()) {
            return Collections.emptyList();
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node startNode = nodes[r][c];
                if (startNode.getValue() != word.charAt(0)) {
                    continue; // Saltar si la primera letra no coincide
                }

                Queue<List<Node>> queue = new LinkedList<>(); // Cola de caminos
                List<Node> initialPath = new ArrayList<>();
                initialPath.add(startNode);
                queue.offer(initialPath);

                while (!queue.isEmpty()) {
                    List<Node> currentPath = queue.poll();
                    Node lastNode = currentPath.get(currentPath.size() - 1);
                    int currentIndex = currentPath.size();

                    if (currentIndex == word.length()) {
                        return currentPath;
                    }

                    // Explorar vecinos
                    for (int i = 0; i < 8; i++) {
                        int newRow = lastNode.getRow() + dRow[i];
                        int newCol = lastNode.getCol() + dCol[i];

                        if (isValid(newRow, newCol)) {
                            Node neighbor = nodes[newRow][newCol];
                            if (!currentPath.contains(neighbor) &&
                                (currentIndex < word.length() && neighbor.getValue() == word.charAt(currentIndex))) {

                                List<Node> newPath = new ArrayList<>(currentPath);
                                newPath.add(neighbor);
                                queue.offer(newPath);
                            }
                        }
                    }
                }
            }
        }
        return Collections.emptyList(); // Palabra no encontrada
    }


    /**
     * Realiza una búsqueda en amplitud (BFS) para encontrar una palabra en el grafo,
     * devolviendo información adicional para la visualización del recorrido.
     *
     * @param word La palabra a buscar.
     * @return Un objeto ResultadoBFS que contiene el camino encontrado (si existe)
     * y el mapa de padres para reconstruir el árbol de búsqueda.
     */
    public ResultadoBFS searchWordBFSForVisualization(String word) {
        List<Node> foundPath = Collections.emptyList();
        Map<Node, Node> parentMap = new HashMap<>(); // Para reconstruir el árbol de búsqueda
        Node bfsStartNode = null; // Para saber desde dónde se inició la visualización BFS

        if (word == null || word.isEmpty()) {
            return new ResultadoBFS(false, foundPath, parentMap, bfsStartNode);
        }

        Queue<List<Node>> queue = new LinkedList<>(); // Cola de caminos actuales (para encontrar la palabra)
        Queue<Node> bfsQueue = new LinkedList<>(); // Cola para el recorrido BFS general (para la visualización)
        Set<Node> visitedBfsNodes = new HashSet<>(); // Nodos visitados en el BFS general

        // Iterar sobre todos los nodos del tablero para posibles inicios de búsqueda
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node startNode = nodes[r][c];

                // Si la primera letra del nodo coincide con la primera letra de la palabra
                if (startNode.getValue() == word.charAt(0)) {
                    // Reiniciar colas y mapas para CADA posible inicio de palabra si la palabra no se ha encontrado aún
                    // Esto es para que la visualización del BFS (parentMap) empiece desde el inicio más "relevante"
                    // y muestre la expansión correcta si la palabra NO se encuentra.
                    // Si la palabra se encuentra, el bucle externo se romperá con el 'return'

                    queue.clear(); // Limpiar la cola de caminos para la búsqueda de palabra
                    bfsQueue.clear(); // Limpiar la cola para la visualización BFS
                    visitedBfsNodes.clear(); // Limpiar nodos visitados para la visualización BFS
                    parentMap.clear(); // Limpiar el mapa de padres
                    bfsStartNode = startNode; // Establecer este como el nuevo nodo de inicio para la visualización

                    List<Node> initialPath = new ArrayList<>();
                    initialPath.add(startNode);
                    queue.offer(initialPath);

                    bfsQueue.offer(startNode);
                    visitedBfsNodes.add(startNode);

                    // Este bucle interno procesa las colas de búsqueda y visualización
                    while (!queue.isEmpty()) { // Solo nos interesa seguir la queue de palabra para encontrarla
                        List<Node> currentPath = queue.poll();
                        Node lastNodeInPath = currentPath.get(currentPath.size() - 1);
                        int currentIndex = currentPath.size();

                        if (currentIndex == word.length()) {
                            // Palabra encontrada! Retorna el resultado inmediatamente.
                            return new ResultadoBFS(true, currentPath, parentMap, bfsStartNode);
                        }

                        // Explorar vecinos para extender el camino de la palabra
                        for (int i = 0; i < 8; i++) {
                            int newRow = lastNodeInPath.getRow() + dRow[i];
                            int newCol = lastNodeInPath.getCol() + dCol[i];

                            if (isValid(newRow, newCol)) {
                                Node neighbor = nodes[newRow][newCol];
                                // Condición clave: el vecino NO debe estar en el camino actual (para evitar ciclos y caminos incorrectos)
                                // Y su valor debe coincidir con la siguiente letra de la palabra.
                                if (!currentPath.contains(neighbor) &&
                                    (currentIndex < word.length() && neighbor.getValue() == word.charAt(currentIndex))) {

                                    List<Node> newPath = new ArrayList<>(currentPath);
                                    newPath.add(neighbor);
                                    queue.offer(newPath);

                                    // Si este vecino no ha sido visitado en el BFS general (para visualización), añadirlo
                                    if (!visitedBfsNodes.contains(neighbor)) {
                                        bfsQueue.offer(neighbor); // Agrega para explorar en BFS general (solo para visualización)
                                        visitedBfsNodes.add(neighbor);
                                        parentMap.put(neighbor, lastNodeInPath); // Registrar el padre para la visualización
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Si la palabra no fue encontrada después de explorar todos los posibles inicios
        // En este punto, parentMap y bfsStartNode contendrán el último recorrido BFS intentado.
        // Para la visualización, esto es útil incluso si no se encontró la palabra.
        return new ResultadoBFS(false, foundPath, parentMap, bfsStartNode);
    }
}