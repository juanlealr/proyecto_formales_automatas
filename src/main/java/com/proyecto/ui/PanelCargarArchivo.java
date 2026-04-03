package com.proyecto.ui;

import com.proyecto.model.Automata;
import com.proyecto.persistence.GestorArchivos;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;

public class PanelCargarArchivo extends JPanel {

    private InterfazUsuario controlador;
    private JTextArea txtVistaPrevia;
    private JLabel lblArchivoSeleccionado;
    private File archivoSeleccionado;

    public PanelCargarArchivo(InterfazUsuario controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.COLOR_FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- Panel superior: selector de archivo ---
        JPanel panelSelector = new JPanel(new BorderLayout(10, 5));
        panelSelector.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        panelSelector.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Seleccionar Archivo"),
                BorderFactory.createEmptyBorder(10, 20, 15, 20)));

        lblArchivoSeleccionado = new JLabel("Ningún archivo seleccionado");
        lblArchivoSeleccionado.setFont(EstilosUI.FUENTE_TEXTO);

        JButton btnExaminar = new JButton("Examinar...");
        btnExaminar.setBackground(EstilosUI.COLOR_BOTON);
        btnExaminar.setFont(EstilosUI.FUENTE_TITULOS);
        btnExaminar.setFocusable(false);
        btnExaminar.setPreferredSize(new Dimension(150, 38));

        btnExaminar.addActionListener(e -> seleccionarArchivo());

        panelSelector.add(lblArchivoSeleccionado, BorderLayout.CENTER);
        panelSelector.add(btnExaminar, BorderLayout.EAST);

        // --- Panel central: vista previa del JSON ---
        JPanel panelPrevia = new JPanel(new BorderLayout());
        panelPrevia.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        panelPrevia.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Vista Previa del Archivo"),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        txtVistaPrevia = new JTextArea();
        txtVistaPrevia.setEditable(false);
        txtVistaPrevia.setFont(EstilosUI.FUENTE_CONSOLA);
        txtVistaPrevia.setBackground(new Color(245, 245, 245));
        txtVistaPrevia.setText("(Seleccione un archivo .json para ver su contenido)");

        JScrollPane scroll = new JScrollPane(txtVistaPrevia);
        scroll.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_LINEA_BORDE));
        panelPrevia.add(scroll, BorderLayout.CENTER);

        // --- Panel inferior: botón cargar ---
        JButton btnCargar = new JButton("Cargar Autómata al Formulario");
        btnCargar.setBackground(EstilosUI.COLOR_BOTON);
        btnCargar.setFont(EstilosUI.FUENTE_TITULOS);
        btnCargar.setPreferredSize(new Dimension(280, 45));
        btnCargar.setFocusable(false);

        btnCargar.addActionListener(e -> cargarAutomata());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        panelBoton.setBackground(EstilosUI.COLOR_FONDO_PANEL);
        panelBoton.add(btnCargar);

        add(panelSelector, BorderLayout.NORTH);
        add(panelPrevia, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void seleccionarArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir autómata");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos JSON (*.json)", "json"));

        int resultado = chooser.showOpenDialog(controlador.getFrame());
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = chooser.getSelectedFile();
            lblArchivoSeleccionado.setText(archivoSeleccionado.getAbsolutePath());
            try {
                String contenido = new String(Files.readAllBytes(archivoSeleccionado.toPath()));
                txtVistaPrevia.setText(contenido);
            } catch (Exception ex) {
                txtVistaPrevia.setText("Error al leer el archivo: " + ex.getMessage());
            }
        }
    }

    private void cargarAutomata() {
        if (archivoSeleccionado == null) {
            JOptionPane.showMessageDialog(controlador.getFrame(),
                    "Primero seleccione un archivo .json", "Sin archivo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Leer el archivo
            Automata automata = GestorArchivos.importar(archivoSeleccionado);

            // --- VALIDACIONES ESTRUCTURALES ---
            if (automata == null) {
                throw new Exception("El archivo no pudo mapearse a un autómata válido.");
            }
            if (automata.getTipo() == null) {
                throw new Exception("No se especificó el tipo de autómata (AFD o AFN).");
            }
            if (automata.getEstados() == null || automata.getEstados().isEmpty()) {
                throw new Exception("El archivo no contiene estados definidos.");
            }
            if (automata.getAlfabeto() == null || automata.getAlfabeto().isEmpty()) {
                throw new Exception("El alfabeto está vacío o es nulo en el archivo.");
            }
            if (automata.getEstadoInicial() == null || automata.getEstadoInicial().trim().isEmpty()) {
                throw new Exception("No se definió un estado inicial en el archivo.");
            }
            if (automata.getTransiciones() == null || automata.getTransiciones().isEmpty()) {
                throw new Exception("No se encontraron transiciones en el archivo.");
            }
            
            // Los estados de aceptación pueden ser cero, pero evitamos que la lista sea nula
            if (automata.getEstadosAceptacion() == null) {
                automata.setEstadosAceptacion(new java.util.ArrayList<>());
            }
            // -------------------------------------------------------------------------

            // Validar la lógica matemática en el motor (estados inalcanzables, no determinismo, etc.)
            var resultado = controlador.getMotor().validarDefinicionAutomata(automata);
            if (!resultado.esAceptada()) {
                JOptionPane.showMessageDialog(controlador.getFrame(),
                        "El archivo tiene inconsistencias lógicas:\n" + resultado.trazaPasoAPaso(),
                        "Autómata inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1. Guardar en el estado global de la app
            controlador.setAutomataActual(automata);

            // 2. Llenar el formulario automáticamente
            controlador.getPanelCrearAutomata().llenarFormulario(automata);

            // 3. Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(controlador.getFrame(),
                    "¡Éxito! Los datos han sido cargados en la pestaña 'Crear Autómata'.",
                    "Carga exitosa", JOptionPane.INFORMATION_MESSAGE);

            // 4. Cambiar de pestaña automáticamente (es mejor hacerlo después del JOptionPane)
            controlador.irAPestanaCrear();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(controlador.getFrame(),
                    "Error estructural en el JSON:\n" + ex.getMessage(),
                    "Error de Archivo", JOptionPane.ERROR_MESSAGE);
        }
    }
}
