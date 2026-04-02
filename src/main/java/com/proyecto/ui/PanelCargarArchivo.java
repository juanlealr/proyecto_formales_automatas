package com.proyecto.ui;

import javax.swing.*;
import java.awt.*;

// Panel para la carga de JSON (almacenista)
public class PanelCargarArchivo extends JPanel {
    public PanelCargarArchivo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(EstilosUI.COLOR_FONDO_PANEL);

        JLabel lblMsg = new JLabel("esperando", SwingConstants.CENTER);
        add(lblMsg, BorderLayout.CENTER);
    }
}