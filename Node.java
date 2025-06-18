package com.mycompany.proyecto1;

import java.util.Objects;

/**
 * Representa un nodo individual (celda) en el tablero de la sopa de letras.
 * Cada nodo contiene una letra, su fila y su columna.
 */
public class Node {
    private char value; // La letra en esta celda.
    private int row;    // La fila de la celda en el tablero.
    private int col;    // La columna de la celda en el tablero.

    /**
     * Constructor para crear un nuevo nodo.
     * @param value La letra que representa este nodo.
     * @param row La fila del nodo en el tablero.
     * @param col La columna del nodo en el tablero.
     */
    public Node(char value, int row, int col) {
        this.value = value;
        this.row = row;
        this.col = col;
    }

    /**
     * Obtiene el valor (letra) de este nodo.
     * @return El carácter que representa la letra en este nodo.
     */
    public char getValue() {
        return value;
    }

    /**
     * Obtiene la fila de este nodo en el tablero.
     * @return El índice de la fila del nodo.
     */
    public int getRow() {
        return row;
    }

    /**
     * Obtiene la columna de este nodo en el tablero.
     * @return El índice de la columna del nodo.
     */
    public int getCol() {
        return col;
    }

    /**
     * Compara este nodo con otro objeto para determinar si son iguales.
     * La igualdad se basa en la fila, columna y valor del nodo.
     * @param o El objeto a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value == node.value &&
               row == node.row &&
               col == node.col;
    }

    /**
     * Genera un valor de código hash para este nodo.
     * El código hash se basa en la fila, columna y valor del nodo.
     * @return El valor del código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, row, col);
    }

    /**
     * Devuelve una representación de cadena de este nodo.
     * @return Una cadena que representa el nodo en formato (value, row, col).
     */
    @Override
    public String toString() {
        return "(" + value + "," + row + "," + col + ")";
    }
}