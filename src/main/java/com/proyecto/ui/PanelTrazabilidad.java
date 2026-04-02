package com.proyecto.ui;

import com.proyecto.engine.ResultadoEvaluacion;
import javax.swing.*;
import java.awt.*;

// Panel para ver el recorrido paso a paso (Traza)
public class PanelTrazabilidad extends JPanel {
    private InterfazUsuario controlador;

    public PanelTrazabilidad(InterfazUsuario controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(0, 20));
        setBackground(EstilosUI.COLOR_FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelSeleccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        panelSeleccion.setBackground(EstilosUI.COLOR_FONDO_INTERNO);
        panelSeleccion.setBorder(new EstilosUI.BordeRedondeado(15, EstilosUI.COLOR_LINEA_BORDE));

        JLabel lbl = new JLabel("Selecciona la cadena:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Obtiene el ComboBox global (Lleno desde la pestaña Evaluar)
        JComboBox<String> cbCadenas = controlador.getCbCadenasTraza();
        cbCadenas.setBackground(Color.WHITE);
        cbCadenas.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbCadenas.setPreferredSize(new Dimension(200, 35));
        cbCadenas.setBorder(BorderFactory.createLineBorder(EstilosUI.COLOR_LINEA_BORDE, 1));
        cbCadenas.setFocusable(false);

        JButton btnVer = new JButton("Ver traza");
        btnVer.setFont(EstilosUI.FUENTE_TITULOS);
        btnVer.setBackground(new Color(225, 225, 225));
        btnVer.setFocusable(false);
        btnVer.setPreferredSize(new Dimension(110, 35));
        btnVer.setBorder(new EstilosUI.BordeRedondeado(10, new Color(170, 170, 170)));

        JTextArea txtTrazaArea = new JTextArea();
        txtTrazaArea.setFont(EstilosUI.FUENTE_CONSOLA);
        txtTrazaArea.setEditable(false);

        btnVer.addActionListener(e -> {
            String seleccion = (String) cbCadenas.getSelectedItem();
            if (seleccion != null && controlador.getAutomataActual() != null) {
                ResultadoEvaluacion res = controlador.getMotor().procesarCadena(controlador.getAutomataActual(), seleccion);
                txtTrazaArea.setText(res.trazaPasoAPaso());
            }
        });

        panelSeleccion.add(lbl);
        panelSeleccion.add(cbCadenas);
        panelSeleccion.add(btnVer);

        JPanel containerScroll = new JPanel(new BorderLayout());
        containerScroll.setBackground(new Color(216, 216, 216));

        JScrollPane sp = new JScrollPane(txtTrazaArea);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBackground(Color.WHITE);

        containerScroll.add(sp, BorderLayout.CENTER);

        add(panelSeleccion, BorderLayout.NORTH);
        add(containerScroll, BorderLayout.CENTER);
    }
}