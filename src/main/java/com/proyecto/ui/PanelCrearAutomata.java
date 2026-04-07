package com.proyecto.ui;

import com.proyecto.engine.ResultadoEvaluacion;
import com.proyecto.model.Automata;
import com.proyecto.model.Estado;
import com.proyecto.model.TipoAutomata;
import com.proyecto.model.Transicion;
import com.proyecto.persistence.GestorArchivos;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PanelCrearAutomata extends JPanel {
    private InterfazUsuario controlador;
    
    // Componentes declarados como atributos para poder ser editados externamente
    private JComboBox<String> cbTipo;
    private JTextField txtEstados;
    private JTextField txtAlfabeto;
    private JTextField txtInicial;
    private JTextField txtAceptacion;
    private JTextArea txtTransiciones;

    public PanelCrearAutomata(InterfazUsuario controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.COLOR_FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- Panel de definición ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(EstilosUI.COLOR_FONDO_INTERNO);

        Border margenInterno = BorderFactory.createEmptyBorder(15, 25, 15, 25);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Definición del Autómata"), margenInterno));

        cbTipo = new JComboBox<>(new String[]{"AFD", "AFN"});
        txtEstados = new JTextField();
        txtAlfabeto = new JTextField();
        txtInicial = new JTextField();
        txtAceptacion = new JTextField();

        JComponent[] campos = {cbTipo, txtEstados, txtAlfabeto, txtInicial, txtAceptacion};
        String[] etiquetas = {
                "Tipo de Autómata:", "Estados (ej: q0,q1,q2):",
                "Alfabeto (ej: a,b o 0,1):", "Estado Inicial (ej: q0):",
                "Estados de Aceptación (ej: q2,q3):"
        };

        for (int i = 0; i < etiquetas.length; i++) {
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(EstilosUI.FUENTE_TEXTO);
            campos[i].setFont(EstilosUI.FUENTE_TEXTO);
            formPanel.add(lbl);
            formPanel.add(campos[i]);
        }

        // --- Panel de transiciones ---
        JPanel transicionesPanel = new JPanel(new BorderLayout());
        transicionesPanel.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        transicionesPanel.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Transiciones"), BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel lblHint = new JLabel("Formato: origen, símbolo, destino (una por línea)");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        transicionesPanel.add(lblHint, BorderLayout.NORTH);

        txtTransiciones = new JTextArea(6, 20);
        txtTransiciones.setFont(EstilosUI.FUENTE_CONSOLA);

        JScrollPane scroll = new JScrollPane(txtTransiciones);
        scroll.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_LINEA_BORDE));
        transicionesPanel.add(scroll, BorderLayout.CENTER);

        // --- Panel de botones ---
        JButton btnValidar = new JButton("Guardar y Validar Autómata");
        btnValidar.setBackground(EstilosUI.COLOR_BOTON);
        btnValidar.setFont(EstilosUI.FUENTE_TITULOS);
        btnValidar.setPreferredSize(new Dimension(250, 45));
        btnValidar.setFocusable(false);

        btnValidar.addActionListener(e -> validarYGuardar());

        JButton btnExportar = new JButton("Exportar a JSON");
        btnExportar.setBackground(EstilosUI.COLOR_BOTON);
        btnExportar.setFont(EstilosUI.FUENTE_TITULOS);
        btnExportar.setPreferredSize(new Dimension(220, 45));
        btnExportar.setFocusable(false);

        btnExportar.addActionListener(e -> exportarAJson());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBoton.setBackground(EstilosUI.COLOR_FONDO_PANEL);
        panelBoton.add(btnValidar);
        panelBoton.add(btnExportar);

        add(formPanel, BorderLayout.NORTH);
        add(transicionesPanel, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }

    // Llena los campos de texto con un objeto Automata al cargar archivo
    public void llenarFormulario(Automata automata) {
        if (automata == null) return;

        // 1. Tipo
        if (automata.getTipo() != null) {
            cbTipo.setSelectedItem(automata.getTipo().toString());
        }
        
        // 2. Estados
        if (automata.getEstados() != null) {
            StringBuilder sbEstados = new StringBuilder();
            for (int i = 0; i < automata.getEstados().size(); i++) {
                // CORRECCIÓN: Como Estado es un record, usamos nombre() en lugar de getNombre()
                sbEstados.append(automata.getEstados().get(i).nombre()); 
                
                if (i < automata.getEstados().size() - 1) sbEstados.append(",");
            }
            txtEstados.setText(sbEstados.toString());
        }

        // 3. Alfabeto
        if (automata.getAlfabeto() != null) {
            txtAlfabeto.setText(String.join(",", automata.getAlfabeto()));
        }

        // 4. Estado Inicial
        if (automata.getEstadoInicial() != null) {
            txtInicial.setText(automata.getEstadoInicial());
        }

        // 5. Estados de Aceptación
        if (automata.getEstadosAceptacion() != null) {
            txtAceptacion.setText(String.join(",", automata.getEstadosAceptacion()));
        }

        // 6. Transiciones
        if (automata.getTransiciones() != null) {
            StringBuilder sb = new StringBuilder();
            for (Transicion t : automata.getTransiciones()) {
                // CORRECCIÓN: Como Transicion es un record, usamos los nombres directos de los atributos
                sb.append(t.estadoOrigen()).append(",")
                  .append(t.simbolo()).append(",")
                  .append(t.estadoDestino()).append("\n");
            }
            txtTransiciones.setText(sb.toString());
        }
    }

    private void validarYGuardar() {
        try {
            Automata nuevo = new Automata();
            nuevo.setTipo(cbTipo.getSelectedItem().equals("AFD") ? TipoAutomata.AFD : TipoAutomata.AFN);

            // 1. PROCESAR Y VALIDAR ESTADOS
            List<Estado> listaEstados = new ArrayList<>();
            for (String s : txtEstados.getText().split(",")) {
                if(!s.trim().isEmpty()) listaEstados.add(new Estado(s.trim()));
            }
            if (listaEstados.isEmpty()) {
                JOptionPane.showMessageDialog(controlador.getFrame(), 
                        "Debe ingresar al menos un estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo.setEstados(listaEstados);

            // 2. PROCESAR Y VALIDAR ALFABETO
            List<String> listaAlfabeto = new ArrayList<>();
            for (String s : txtAlfabeto.getText().split(",")) {
                if(!s.trim().isEmpty()) listaAlfabeto.add(s.trim());
            }
            if (listaAlfabeto.isEmpty()) {
                JOptionPane.showMessageDialog(controlador.getFrame(), 
                        "El alfabeto no puede estar vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo.setAlfabeto(listaAlfabeto);

            // 3. PROCESAR Y VALIDAR ESTADO INICIAL
            String inicial = txtInicial.getText().trim();
            if (inicial.isEmpty()) {
                JOptionPane.showMessageDialog(controlador.getFrame(), 
                        "Debe especificar un estado inicial.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo.setEstadoInicial(inicial);

            // 4. PROCESAR Y LIMPIAR ESTADOS DE ACEPTACIÓN
            List<String> listaAceptacion = new ArrayList<>();
            for (String s : txtAceptacion.getText().split(",")) {
                if(!s.trim().isEmpty()) listaAceptacion.add(s.trim());
            }
            nuevo.setEstadosAceptacion(listaAceptacion); // Es válido que esta lista esté vacía

            // 5. PROCESAR Y VALIDAR TRANSICIONES
            List<Transicion> listaTransiciones = new ArrayList<>();
            for (String linea : txtTransiciones.getText().split("\n")) {
                if(linea.trim().isEmpty()) continue; // Ignorar líneas en blanco
                
                String[] partes = linea.split(",");
                // Asegurarse de que la línea tiene exactamente 3 partes (origen, símbolo, destino)
                if (partes.length >= 3) { 
                    // Limpiamos los espacios en blanco de cada parte
                    String origen = partes[0].trim();
                    String simbolo = partes[1].trim();
                    String destino = partes[2].trim();
                    
                    if(!origen.isEmpty() && !simbolo.isEmpty() && !destino.isEmpty()) {
                        listaTransiciones.add(new Transicion(origen, simbolo, destino));
                    }
                }
            }
            // VALIDACIÓN: ¿Hay transiciones válidas?
            if (listaTransiciones.isEmpty()) {
                JOptionPane.showMessageDialog(controlador.getFrame(), 
                        "Debe ingresar al menos una transición con el formato correcto (origen, símbolo, destino).", 
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            nuevo.setTransiciones(listaTransiciones);

            // 6. ENVIAR AL MOTOR DE EVALUACIÓN
            ResultadoEvaluacion res = controlador.getMotor().validarDefinicionAutomata(nuevo);
            if (res.esAceptada()) {
                controlador.setAutomataActual(nuevo);
                JOptionPane.showMessageDialog(controlador.getFrame(), "¡Autómata guardado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(controlador.getFrame(), "Error de Validación en la lógica del autómata:\n" + res.trazaPasoAPaso(), "Error Lógico", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(controlador.getFrame(), "Error inesperado al procesar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarAJson() {
        Automata actual = controlador.getAutomataActual();
        if (actual == null) {
            JOptionPane.showMessageDialog(controlador.getFrame(), "Primero guarda y valida un autómata.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(controlador.getFrame()) == JFileChooser.APPROVE_OPTION) {
            File destino = chooser.getSelectedFile();
            if (!destino.getName().endsWith(".json")) destino = new File(destino.getAbsolutePath() + ".json");
            try {
                GestorArchivos.exportar(actual, destino);
                JOptionPane.showMessageDialog(controlador.getFrame(), "Exportado con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(controlador.getFrame(), "Error al exportar.");
            }
        }
    }
}