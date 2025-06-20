package com.mycompany.proyecto1;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Panel personalizado para visualizar el tablero de la sopa de letras
 * y el recorrido de un algoritmo BFS.
 */
public class PanelBFST extends JPanel {

    private char[][] board;
    private ResultadoBFS bfsResult; // Ahora usa ResultadoBFS
    private final int CELL_SIZE = 50;
    private final int PADDING = 20;

    /**
     * Constructor para PanelBFST.
     * Configura el tamaño preferido y el color de fondo.
     */
    public PanelBFST() {
        setPreferredSize(new Dimension(CELL_SIZE * 4 + PADDING * 2, CELL_SIZE * 4 + PADDING * 2));
        setBackground(Color.WHITE);
    }

    /**
     * Establece el tablero de la sopa de letras a visualizar.
     * @param board La matriz de caracteres que representa el tablero.
     */
    public void setBoard(char[][] board) {
        this.board = board;
        repaint(); // Vuelve a dibujar el panel cuando el tablero cambia
    }

    /**
     * Establece los resultados de un BFS para visualización.
     * Esto incluye el camino encontrado y el mapa de padres para el árbol de búsqueda.
     * @param result El objeto ResultadoBFS que contiene la información del BFS.
     */
    public void setBFSResult(ResultadoBFS result) {
        this.bfsResult = result;
        repaint(); // Vuelve a dibujar el panel cuando los resultados del BFS cambian
    }

    /**
     * Método de pintura principal para el panel.
     * Dibuja el tablero, y opcionalmente el camino encontrado o la expansión del BFS.
     * @param g El contexto gráfico.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; // Cast a Graphics2D para usar setRenderingHint
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (board == null) {
            g2d.setColor(Color.BLACK);
            g2d.drawString("Carga un tablero para visualizar el BFS.", PADDING, PADDING + 20);
            return;
        }

        // Dibujar el tablero de fondo
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(1));
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                int x = PADDING + c * CELL_SIZE;
                int y = PADDING + r * CELL_SIZE;
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Monospaced", Font.BOLD, CELL_SIZE / 2));
                String letter = String.valueOf(board[r][c]);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (CELL_SIZE - fm.stringWidth(letter)) / 2;
                int textY = y + (fm.getAscent() + (CELL_SIZE - (fm.getAscent() + fm.getDescent())) / 2);
                g2d.drawString(letter, textX, textY);
            }
        }

        if (bfsResult != null && bfsResult.isPathFound()) {
            // Dibujar el camino encontrado (resaltado)
            g2d.setColor(new Color(0, 150, 0, 150)); // Verde semi-transparente
            g2d.setStroke(new BasicStroke(3)); // Línea más gruesa

            List<Node> path = bfsResult.getFoundPath();
            for (int i = 0; i < path.size() - 1; i++) {
                Node n1 = path.get(i);
                Node n2 = path.get(i + 1);
                int x1 = PADDING + n1.getCol() * CELL_SIZE + CELL_SIZE / 2;
                int y1 = PADDING + n1.getRow() * CELL_SIZE + CELL_SIZE / 2;
                int x2 = PADDING + n2.getCol() * CELL_SIZE + CELL_SIZE / 2;
                int y2 = PADDING + n2.getRow() * CELL_SIZE + CELL_SIZE / 2;
                g2d.drawLine(x1, y1, x2, y2);
            }

            // Dibujar círculos en los nodos del camino
            for (Node n : path) {
                int x = PADDING + n.getCol() * CELL_SIZE;
                int y = PADDING + n.getRow() * CELL_SIZE;
                g2d.setColor(new Color(0, 200, 0, 200)); // Verde más sólido
                g2d.fillOval(x + CELL_SIZE / 4, y + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
            }
        } else if (bfsResult != null && !bfsResult.isPathFound() && bfsResult.getStartNode() != null) {
            // Si la palabra no fue encontrada, pero hubo una búsqueda BFS (mostrando la expansión general)
            g2d.setColor(new Color(255, 165, 0, 100)); // Naranja semi-transparente para nodos explorados
            g2d.setStroke(new BasicStroke(1));

            Map<Node, Node> parentMap = bfsResult.getParentMap();
            Set<Node> exploredNodes = new HashSet<>(parentMap.keySet());
            exploredNodes.add(bfsResult.getStartNode()); // Asegurarse de que el nodo inicial también se muestre

            for (Node node : exploredNodes) {
                // Resaltar los nodos visitados
                int x = PADDING + node.getCol() * CELL_SIZE;
                int y = PADDING + node.getRow() * CELL_SIZE;
                g2d.fillOval(x + CELL_SIZE / 4, y + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);

                // Dibujar líneas a los padres (representando el árbol de búsqueda)
                Node parent = parentMap.get(node);
                if (parent != null) {
                    int x1 = PADDING + parent.getCol() * CELL_SIZE + CELL_SIZE / 2;
                    int y1 = PADDING + parent.getRow() * CELL_SIZE + CELL_SIZE / 2;
                    int x2 = PADDING + node.getCol() * CELL_SIZE + CELL_SIZE / 2;
                    int y2 = PADDING + node.getRow() * CELL_SIZE + CELL_SIZE / 2;
                    g2d.setColor(new Color(255, 140, 0, 150)); // Naranja más oscuro
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }
}