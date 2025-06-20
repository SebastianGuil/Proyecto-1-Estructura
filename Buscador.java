package com.mycompany.proyecto1;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Buscador extends JFrame {

    private char[][] board;
    private List<String> dictionary;
    private Grafo wordSearchGrafo;

    // Componentes de la GUI
    private JTextArea boardDisplayArea;
    private JTextArea dictionaryDisplayArea;
    private JTextArea resultsDisplayArea;
    private JButton loadFileButton;
    private JTextField specificWordTextField;
    private JButton searchSpecificWordButton;
    private JButton exitButton; // Botón para salir

    private PanelBFST panelBFST; // Panel de visualización BFS

    // Componentes para gestionar el diccionario
    private JTextField newWordTextField;
    private JButton addWordButton;
    private JButton removeWordButton;
    private JButton saveDictionaryButton;

    // Componentes para la selección del algoritmo de búsqueda
    private JRadioButton dfsRadioButton;
    private JRadioButton bfsRadioButton;
    private ButtonGroup searchAlgorithmGroup; // Grupo para que solo uno pueda ser seleccionado
    private String selectedSearchAlgorithm = "DFS"; // Valor por defecto

    private final int MIN_WORD_LENGTH = 3; // Mínimo de caracteres para las palabras

    public Buscador() {
        super("Sopa de Letras - Buscador");
        this.board = null;
        this.dictionary = new ArrayList<>();

        initComponents();
        setupLayout();
        setupListeners();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        boardDisplayArea = new JTextArea(8, 20);
        boardDisplayArea.setEditable(false);
        boardDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
        boardDisplayArea.setBorder(BorderFactory.createTitledBorder("Tablero"));

        dictionaryDisplayArea = new JTextArea(10, 15);
        dictionaryDisplayArea.setEditable(false);
        dictionaryDisplayArea.setBorder(BorderFactory.createTitledBorder("Diccionario Cargado"));

        resultsDisplayArea = new JTextArea(10, 30);
        resultsDisplayArea.setEditable(false);
        resultsDisplayArea.setBorder(BorderFactory.createTitledBorder("Resultados de Búsqueda"));

        loadFileButton = new JButton("Cargar Archivo TXT");

        specificWordTextField = new JTextField(15);
        specificWordTextField.setBorder(BorderFactory.createTitledBorder("Buscar Palabra Específica"));

        searchSpecificWordButton = new JButton("Buscar");
        exitButton = new JButton("Salir del Programa"); // Nuevo botón de salir

        panelBFST = new PanelBFST(); // Instancia del PanelBFST
        panelBFST.setBorder(BorderFactory.createTitledBorder("Visualización Recorrido BFS (Palabra Específica)")); // Título más específico

        // Inicializar componentes de gestión del diccionario
        newWordTextField = new JTextField(15);
        newWordTextField.setBorder(BorderFactory.createTitledBorder("Añadir/Eliminar Palabra"));

        addWordButton = new JButton("Añadir");
        removeWordButton = new JButton("Eliminar");
        saveDictionaryButton = new JButton("Guardar Diccionario");

        // Inicializar componentes para la selección del algoritmo de búsqueda
        dfsRadioButton = new JRadioButton("DFS (Búsqueda en Profundidad)");
        bfsRadioButton = new JRadioButton("BFS (Búsqueda en Amplitud)");
        dfsRadioButton.setSelected(true); // DFS por defecto

        searchAlgorithmGroup = new ButtonGroup();
        searchAlgorithmGroup.add(dfsRadioButton);
        searchAlgorithmGroup.add(bfsRadioButton);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(loadFileButton);
        topPanel.add(specificWordTextField);
        topPanel.add(searchSpecificWordButton);
        topPanel.add(exitButton); // Añadir botón de salir al panel superior
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerBottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerBottomPanel.add(new JScrollPane(boardDisplayArea));

        // Panel para el diccionario y controles de gestión
        JPanel dictionaryPanel = new JPanel(new BorderLayout());
        dictionaryPanel.add(new JScrollPane(dictionaryDisplayArea), BorderLayout.CENTER);

        JPanel dictionaryControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        dictionaryControlsPanel.setBorder(BorderFactory.createTitledBorder("Gestionar Diccionario"));
        dictionaryControlsPanel.add(newWordTextField);
        dictionaryControlsPanel.add(addWordButton);
        dictionaryControlsPanel.add(removeWordButton);
        dictionaryControlsPanel.add(saveDictionaryButton);
        dictionaryPanel.add(dictionaryControlsPanel, BorderLayout.SOUTH); // Controles bajo el diccionario

        centerBottomPanel.add(dictionaryPanel);

        JPanel centralSectionPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centralSectionPanel.add(centerBottomPanel);
        centralSectionPanel.add(panelBFST);
        mainPanel.add(centralSectionPanel, BorderLayout.CENTER);

        // Panel derecho para resultados y selección de algoritmo
        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel searchAlgorithmPanel = new JPanel();
        searchAlgorithmPanel.setLayout(new BoxLayout(searchAlgorithmPanel, BoxLayout.Y_AXIS));
        searchAlgorithmPanel.setBorder(BorderFactory.createTitledBorder("Algoritmo de Búsqueda para Diccionario"));
        searchAlgorithmPanel.add(dfsRadioButton);
        searchAlgorithmPanel.add(bfsRadioButton);

        rightPanel.add(searchAlgorithmPanel, BorderLayout.NORTH); // Selección de algoritmo al norte del panel derecho
        rightPanel.add(new JScrollPane(resultsDisplayArea), BorderLayout.CENTER); // Resultados en el centro
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private void setupListeners() {
        loadFileButton.addActionListener(e -> {
            if (loadAndProcessFile()) {
                displayBoard();
                displayDictionary();
                panelBFST.setBoard(board);
                resultsDisplayArea.setText("Archivo cargado correctamente.\nIniciando búsqueda de palabras del diccionario...\n");
                searchAllDictionaryWords(); // Ejecuta la búsqueda inicial del diccionario
            } else {
                resultsDisplayArea.setText("Error al cargar o procesar el archivo.");
            }
        });

        searchSpecificWordButton.addActionListener(e -> {
            searchSpecificWord();
        });

        // Listeners para los nuevos botones de gestión del diccionario
        addWordButton.addActionListener(e -> addWordToDictionary());
        removeWordButton.addActionListener(e -> removeWordFromDictionary());
        saveDictionaryButton.addActionListener(e -> saveDictionaryToFile());
        exitButton.addActionListener(e -> System.exit(0)); // Listener para el botón de salir

        // Listeners para los Radio Buttons del algoritmo de búsqueda
        dfsRadioButton.addActionListener(e -> selectedSearchAlgorithm = "DFS");
        bfsRadioButton.addActionListener(e -> selectedSearchAlgorithm = "BFS");
    }

    private void displayBoard() {
        if (board != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    sb.append(board[i][j]).append("  ");
                }
                sb.append("\n");
            }
            boardDisplayArea.setText(sb.toString());
        } else {
            boardDisplayArea.setText("Tablero no cargado.");
        }
    }

    private void displayDictionary() {
        if (dictionary != null && !dictionary.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String word : dictionary) {
                sb.append(word).append("\n");
            }
            dictionaryDisplayArea.setText(sb.toString());
        } else {
            dictionaryDisplayArea.setText("Diccionario no cargado.");
        }
    }

    // Método que busca todas las palabras del diccionario usando el algoritmo seleccionado
    private void searchAllDictionaryWords() {
        if (wordSearchGrafo == null || dictionary.isEmpty()) {
            resultsDisplayArea.append("Grafo o diccionario no disponibles para búsqueda.\n");
            return;
        }

        resultsDisplayArea.append("Resultados del diccionario (usando " + selectedSearchAlgorithm + "):\n");
        for (String word : dictionary) {
            List<Node> foundPath;
            if (selectedSearchAlgorithm.equals("DFS")) {
                foundPath = wordSearchGrafo.searchWordDFS(word);
            } else { // BFS
                foundPath = wordSearchGrafo.searchWordBFS(word); // Usar el nuevo método BFS sin visualización
            }

            if (!foundPath.isEmpty()) {
                resultsDisplayArea.append("  ENCONTRADA: '" + word + "'\n");
            } else {
                resultsDisplayArea.append("  NO ENCONTRADA: '" + word + "'\n");
            }
        }
    }

    private void searchSpecificWord() {
        String word = specificWordTextField.getText().trim().toUpperCase();

        // Validaciones de la palabra buscada (minimo 3 caracteres, solo letras)
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa una palabra para buscar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isValidWord(word)) {
            JOptionPane.showMessageDialog(this,
                "La palabra '" + word + "' no es válida. Debe contener al menos " + MIN_WORD_LENGTH + " letras y solo caracteres alfabéticos (A-Z).",
                "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (wordSearchGrafo == null) {
            JOptionPane.showMessageDialog(this, "Por favor, carga un archivo primero para inicializar el tablero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultsDisplayArea.setText("Buscando palabra específica: '" + word + "'\n");
        // La búsqueda específica SIEMPRE usa BFS para la visualización en el PanelBFST
        ResultadoBFS resultadoBFS = wordSearchGrafo.searchWordBFSForVisualization(word);

        if (resultadoBFS.isPathFound()) {
            resultsDisplayArea.append("  ENCONTRADA: '" + word + "' en ");
            for (Node n : resultadoBFS.getFoundPath()) {
                resultsDisplayArea.append(n.getValue() + "(" + n.getRow() + "," + n.getCol() + ") ");
            }
            resultsDisplayArea.append("\n");

            // Si la palabra es encontrada, añadirla automáticamente al diccionario
            if (!dictionary.contains(word)) {
                dictionary.add(word);
                displayDictionary(); // Actualiza la visualización
                resultsDisplayArea.append("  Palabra '" + word + "' añadida automáticamente al diccionario.\n");
            }

        } else {
            resultsDisplayArea.append("  NO ENCONTRADA: '" + word + "'. No se pudo construir un camino válido con las letras adyacentes y no repetidas.\n");
        }

        panelBFST.setBFSResult(resultadoBFS); // Actualizar el panel de visualización
    }

    // Método de utilidad para validar si un carácter es una letra mayúscula (A-Z)
    private boolean isValidLetter(char c) {
        return (c >= 'A' && c <= 'Z');
    }

    // Método de utilidad para validar una palabra (longitud mínima y solo letras)
    private boolean isValidWord(String word) {
        if (word.length() < MIN_WORD_LENGTH) {
            return false;
        }
        for (char c : word.toCharArray()) {
            if (!isValidLetter(c)) {
                return false;
            }
        }
        return true;
    }

    // Métodos para gestionar el diccionario
    private void addWordToDictionary() {
        String word = newWordTextField.getText().trim().toUpperCase();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce una palabra para añadir.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Validar la palabra antes de añadirla
        if (!isValidWord(word)) {
            JOptionPane.showMessageDialog(this,
                "La palabra '" + word + "' no es válida. Debe contener al menos " + MIN_WORD_LENGTH + " letras y solo caracteres alfabéticos (A-Z).",
                "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dictionary.contains(word)) {
            dictionary.add(word);
            displayDictionary(); // Actualiza la visualización del diccionario
            newWordTextField.setText(""); // Limpia el campo de texto
            resultsDisplayArea.append("Palabra '" + word + "' añadida al diccionario.\n");
        } else {
            JOptionPane.showMessageDialog(this, "La palabra '" + word + "' ya existe en el diccionario.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeWordFromDictionary() {
        String word = newWordTextField.getText().trim().toUpperCase();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, introduce una palabra para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dictionary.remove(word)) { // remove() devuelve true si la palabra fue eliminada
            displayDictionary(); // Actualiza la visualización del diccionario
            newWordTextField.setText(""); // Limpia el campo de texto
            resultsDisplayArea.append("Palabra '" + word + "' eliminada del diccionario.\n");
        } else {
            JOptionPane.showMessageDialog(this, "La palabra '" + word + "' no se encontró en el diccionario.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveDictionaryToFile() {
        if (dictionary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El diccionario está vacío, no hay nada que guardar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Diccionario Como...");
        fileChooser.setSelectedFile(new File("diccionario_actualizado.txt")); // Nombre de archivo sugerido

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                for (String word : dictionary) {
                    writer.write(word);
                    writer.newLine(); // Escribe cada palabra en una nueva línea
                }
                JOptionPane.showMessageDialog(this, "Diccionario guardado exitosamente en:\n" + fileToSave.getAbsolutePath(), "Guardado", JOptionPane.INFORMATION_MESSAGE);
                resultsDisplayArea.append("Diccionario guardado en: " + fileToSave.getName() + "\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el diccionario:\n" + ex.getMessage(), "Error de Guardado", JOptionPane.ERROR_MESSAGE);
                resultsDisplayArea.append("Error al guardar diccionario: " + ex.getMessage() + "\n");
            }
        } else {
            resultsDisplayArea.append("Guardado de diccionario cancelado.\n");
        }
    }

    // Método para cargar y procesar el archivo, con las correcciones y validaciones para el tablero
    public boolean loadAndProcessFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona el archivo de la sopa de letras (.txt)");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            boolean success = parseFile(selectedFile);
            if(success && board != null) {
                this.wordSearchGrafo = new Grafo(this.board);
            } else {
                this.wordSearchGrafo = null;
            }
            return success;
        } else {
            return false;
        }
    }

    private boolean parseFile(File file) {
        this.dictionary.clear(); // Limpiar el diccionario existente antes de cargar uno nuevo
        boolean inDictionarySection = false;
        boolean inBoardSection = false;
        List<String> boardLinesRaw = new ArrayList<>(); // Almacenará las líneas tal como se leen (con o sin espacios)

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();

                // Manejo de las etiquetas de sección
                if (trimmedLine.equalsIgnoreCase("<dic>")) {
                    inDictionarySection = true;
                    inBoardSection = false;
                    continue;
                } else if (trimmedLine.equalsIgnoreCase("</dic>")) {
                    inDictionarySection = false;
                    continue;
                } else if (trimmedLine.equalsIgnoreCase("</tab>")) {
                    inBoardSection = false;
                    continue;
                } else if (trimmedLine.equalsIgnoreCase("<tab>")) {
                    inBoardSection = true;
                    inDictionarySection = false;
                    continue;
                }

                if (inDictionarySection) {
                    if (!trimmedLine.isEmpty()) {
                        // Validar palabras del diccionario cargado
                        if (!isValidWord(trimmedLine)) {
                            JOptionPane.showMessageDialog(this,
                                "Advertencia: La palabra '" + trimmedLine + "' en el diccionario no es válida (mín. " + MIN_WORD_LENGTH + " letras, solo alfabéticas). Será ignorada.",
                                "Formato de Diccionario", JOptionPane.WARNING_MESSAGE);
                        } else {
                            dictionary.add(trimmedLine);
                        }
                    }
                } else if (inBoardSection) {
                    if (!trimmedLine.isEmpty()) {
                        String cleanedLineForValidation = trimmedLine.replace(" ", ""); // Elimina espacios para validar longitud

                        // Validar que la línea solo contenga letras y tenga la longitud correcta
                        for (char c : cleanedLineForValidation.toCharArray()) {
                            if (!isValidLetter(c)) {
                                JOptionPane.showMessageDialog(this,
                                    "Error de formato: El tablero contiene caracteres no alfabéticos (ej. números o símbolos).",
                                    "Error de Archivo", JOptionPane.ERROR_MESSAGE);
                                board = null; // Reinicia el tablero
                                dictionary.clear(); // Reinicia el diccionario
                                return false; // Aborta la carga del archivo
                            }
                        }

                        // Validar la longitud de la línea DESPUÉS de limpiar los espacios
                        if (boardLinesRaw.isEmpty() || cleanedLineForValidation.length() == boardLinesRaw.get(0).replace(" ", "").length()) {
                            boardLinesRaw.add(trimmedLine); // Añadir la línea original a la lista temporal
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Error de formato: Las líneas del tablero no tienen la misma longitud (después de quitar espacios).",
                                "Error de Archivo", JOptionPane.ERROR_MESSAGE);
                            board = null;
                            dictionary.clear();
                            return false;
                        }
                    }
                }
            }

            if (!boardLinesRaw.isEmpty()) {
                // Primero, verifica la longitud del tablero después de limpiar espacios de la primera línea
                String firstCleanedBoardLine = boardLinesRaw.get(0).replace(" ", "");
                if (boardLinesRaw.size() != 4 || firstCleanedBoardLine.length() != 4) {
                    JOptionPane.showMessageDialog(this,
                        "Error: El tablero debe ser de 4x4. Se encontró " + boardLinesRaw.size() + "x" + firstCleanedBoardLine.length() + ".",
                        "Error de Archivo", JOptionPane.ERROR_MESSAGE);
                    board = null;
                    dictionary.clear();
                    return false;
                }

                // Ahora, llena el array 'board' correctamente eliminando espacios
                board = new char[boardLinesRaw.size()][4]; // Definimos la columna como 4 porque ya validamos el 4x4
                for (int i = 0; i < boardLinesRaw.size(); i++) {
                    String cleanedBoardLine = boardLinesRaw.get(i).replace(" ", ""); // Limpia espacios de cada línea
                    board[i] = cleanedBoardLine.toCharArray(); // Convierte la línea limpia a char array
                }
            } else {
                JOptionPane.showMessageDialog(this, "Advertencia: No se encontró la sección de tablero en el archivo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            if (dictionary.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Advertencia: No se encontró la sección de diccionario o está vacía.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
            return true;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error: El archivo no fue encontrado.\n" + e.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo:\n" + e.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Buscador();
        });
    }
}