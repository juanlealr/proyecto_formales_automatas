package com.proyecto.ui;

import com.proyecto.engine.ResultadoEvaluacion;
import javax.swing.*;
import java.awt.*;

// Panel para procesamiento por lotes de cadenas
public class PanelEvaluarLote extends JPanel {
    private InterfazUsuario controlador;

    public PanelEvaluarLote(InterfazUsuario controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBackground(EstilosUI.COLOR_FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel centroContainer = new JPanel(new GridLayout(2, 1, 0, 15));
        centroContainer.setBackground(EstilosUI.COLOR_FONDO_PANEL);

        // Entrada
        JPanel panelEntrada = new JPanel(new BorderLayout());
        panelEntrada.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        panelEntrada.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Ingresa las Cadenas"), BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel lblHint = new JLabel("(una por línea, máximo 10)");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        panelEntrada.add(lblHint, BorderLayout.NORTH);

        JTextArea txtCadenas = new JTextArea(6, 20);
        txtCadenas.setFont(EstilosUI.FUENTE_CONSOLA);
        JScrollPane spEntrada = new JScrollPane(txtCadenas);
        spEntrada.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_LINEA_BORDE));
        panelEntrada.add(spEntrada, BorderLayout.CENTER);

        // Resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        panelResultados.setBorder(BorderFactory.createCompoundBorder(
                EstilosUI.crearBordeTitulo("Resultados"), BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        JTextArea txtResultados = new JTextArea(6, 20);
        txtResultados.setEditable(false);
        txtResultados.setFont(EstilosUI.FUENTE_CONSOLA);
        JScrollPane spResultados = new JScrollPane(txtResultados);
        spResultados.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_LINEA_BORDE));
        panelResultados.add(spResultados, BorderLayout.CENTER);

        centroContainer.add(panelEntrada);
        centroContainer.add(panelResultados);

        // Botón evaluar
        JButton btnEvaluar = new JButton("Evaluar Lote");
        btnEvaluar.setBackground(EstilosUI.COLOR_BOTON);
        btnEvaluar.setFont(EstilosUI.FUENTE_TITULOS);
        btnEvaluar.setPreferredSize(new Dimension(250, 45));
        btnEvaluar.setFocusable(false);
        btnEvaluar.setFocusPainted(false);
        btnEvaluar.setBorder(new EstilosUI.BordeRedondeado(15, new Color(170, 170, 170)));
        btnEvaluar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEvaluar.addActionListener(e -> {
            if (controlador.getAutomataActual() == null) {
                JOptionPane.showMessageDialog(controlador.getFrame(), "Primero debes crear o cargar un autómata.");
                return;
            }
            String texto = txtCadenas.getText().trim();
            if (texto.isEmpty()) return;

            String[] lineas = texto.split("\n");
            StringBuilder sb = new StringBuilder();

            // Limpia el ComboBox de la otra pestaña usando el controlador
            controlador.getCbCadenasTraza().removeAllItems();

            for (String s : lineas) {
                String c = s.trim();
                if (c.isEmpty()) continue;
                ResultadoEvaluacion res = controlador.getMotor().procesarCadena(controlador.getAutomataActual(), c);
                sb.append(res.esAceptada() ? "Aceptada: " : "Rechazada: ").append(c).append("\n");

                // Pasa la cadena evaluada a la pestaña de Trazabilidad
                controlador.getCbCadenasTraza().addItem(c);
            }
            txtResultados.setText(sb.toString());
        });

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        panelBoton.setBackground(EstilosUI.COLOR_FONDO_PANEL);
        panelBoton.add(btnEvaluar);

        add(centroContainer, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }
}