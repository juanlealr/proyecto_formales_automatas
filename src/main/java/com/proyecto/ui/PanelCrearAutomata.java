package com.proyecto.ui;

import com.proyecto.engine.ResultadoEvaluacion;
import com.proyecto.model.Automata;
import com.proyecto.model.Estado;
import com.proyecto.model.TipoAutomata;
import com.proyecto.model.Transicion;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Panel responsable de la creación manual de autómatas
public class PanelCrearAutomata extends JPanel {
    private InterfazUsuario controlador;

    public PanelCrearAutomata(InterfazUsuario controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.COLOR_FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Painel de definición
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBackground(EstilosUI.COLOR_FONDO_INTERNO);

        Border margenInterno = BorderFactory.createEmptyBorder(15, 25, 15, 25);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Definición del Autómata"), margenInterno));

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"AFD", "AFN"});
        JTextField txtEstados = new JTextField();
        JTextField txtAlfabeto = new JTextField();
        JTextField txtInicial = new JTextField();
        JTextField txtAceptacion = new JTextField();

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

        // Panel de transiciones
        JPanel transicionesPanel = new JPanel(new BorderLayout());
        transicionesPanel.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        transicionesPanel.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Transiciones"), BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel lblHint = new JLabel("Formato: origen, símbolo, destino (una por línea)");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        transicionesPanel.add(lblHint, BorderLayout.NORTH);

        JTextArea txtTransiciones = new JTextArea(6, 20);
        txtTransiciones.setFont(EstilosUI.FUENTE_CONSOLA);
        txtTransiciones.setText("q0,a,q1\nq1,b,q2");

        JScrollPane scroll = new JScrollPane(txtTransiciones);
        scroll.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_LINEA_BORDE));
        transicionesPanel.add(scroll, BorderLayout.CENTER);

        // Botón guardar
        JButton btnValidar = new JButton("Guardar y Validar Autómata");
        btnValidar.setBackground(EstilosUI.COLOR_BOTON);
        btnValidar.setFont(EstilosUI.FUENTE_TITULOS);
        btnValidar.setPreferredSize(new Dimension(250, 45));
        btnValidar.setFocusable(false);

        btnValidar.addActionListener(e -> {
            try {
                Automata nuevo = new Automata();
                nuevo.setTipo(cbTipo.getSelectedItem().equals("AFD") ? TipoAutomata.AFD : TipoAutomata.AFN);

                List<Estado> listaEstados = new ArrayList<>();
                for (String s : txtEstados.getText().split(",")) listaEstados.add(new Estado(s.trim()));
                nuevo.setEstados(listaEstados);

                nuevo.setAlfabeto(List.of(txtAlfabeto.getText().split(",")));
                nuevo.setEstadoInicial(txtInicial.getText().trim());
                nuevo.setEstadosAceptacion(List.of(txtAceptacion.getText().split(",")));

                List<Transicion> listaTransiciones = new ArrayList<>();
                for (String linea : txtTransiciones.getText().split("\n")) {
                    String[] partes = linea.split(",");
                    if (partes.length == 3) {
                        listaTransiciones.add(new Transicion(partes[0].trim(), partes[1].trim(), partes[2].trim()));
                    }
                }
                nuevo.setTransiciones(listaTransiciones);

                // Llama al motor desde el controlador
                ResultadoEvaluacion res = controlador.getMotor().validarDefinicionAutomata(nuevo);
                if (res.esAceptada()) {
                    controlador.setAutomataActual(nuevo); // Actualiza el estado global
                    JOptionPane.showMessageDialog(controlador.getFrame(), "¡Autómata válido!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(controlador.getFrame(), res.trazaPasoAPaso(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(controlador.getFrame(), "Error en los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.NORTH);
        add(transicionesPanel, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        panelBoton.setBackground(EstilosUI.COLOR_FONDO_PANEL);
        panelBoton.add(btnValidar);
        add(panelBoton, BorderLayout.SOUTH);
    }
}