package com.proyecto.ui;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.*;
import java.awt.*;

// Utilidad para mantener el diseño visual uniforme en toda la aplicación
public class EstilosUI {
    // Paleta de colores centralizada
    public static final Color COLOR_FONDO_PANEL = new Color(235, 235, 235);
    public static final Color COLOR_FONDO_INTERNO = new Color(235, 235, 235);
    public static final Color COLOR_LINEA_BORDE = new Color(131, 131, 131);
    public static final Color COLOR_BOTON = new Color(177, 177, 177);

    // Fuentes
    public static final Font FUENTE_TITULOS = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FUENTE_TEXTO = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font FUENTE_CONSOLA = new Font("Consolas", Font.PLAIN, 18);

    // Método para la creación del borde con título
    public static TitledBorder crearBordeTitulo(String titulo) {
        BordeRedondeado bordaPadrao = new BordeRedondeado(20, COLOR_LINEA_BORDE);
        TitledBorder tb = BorderFactory.createTitledBorder(bordaPadrao, titulo);
        tb.setTitleFont(FUENTE_TITULOS);
        tb.setTitleColor(new Color(80, 80, 80));
        tb.setTitlePosition(TitledBorder.TOP);
        return tb;
    }

    // Clase interna para el borde redondeado
    public static class BordeRedondeado implements Border {
        private int radio;
        private Color color;

        public BordeRedondeado(int radio, Color color) {
            this.radio = radio;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(0, 15, 5, 15);
        }

        public boolean isBorderOpaque() { return false; }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
        }
    }
}