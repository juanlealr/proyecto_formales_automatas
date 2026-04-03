package com.proyecto.ui;

import com.proyecto.engine.MotorEvaluacion;
import com.proyecto.model.Automata;

import javax.swing.*;
import java.awt.*;

// Clase principal que inicializa la ventana y comparte datos entre las pestañas
public class InterfazUsuario {
    private MotorEvaluacion motor;
    private Automata automataActual;

    private JFrame frame;
    private JLabel lblEstadoAutomata;
    private JComboBox<String> cbCadenasTraza; // Compartido entre Evaluar Lote y Trazabilidad

    public InterfazUsuario() {
        this.motor = new MotorEvaluacion();
        this.cbCadenasTraza = new JComboBox<>();

        // Configuración de UI del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 16));
        } catch (Exception ignored) {}

        inicializarGUI();
    }

    public void iniciar() {
        frame.setVisible(true);
    }

    private void inicializarGUI() {
        frame = new JFrame("Simulador de autómatas (AFD/AFN)");
        frame.getContentPane().setBackground(new Color(225, 230, 235));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 750);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Panel superior para mostrar el estado actual
        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(220, 226, 235));
        lblEstadoAutomata = new JLabel("Ningún autómata cargado en memoria", SwingConstants.CENTER);
        lblEstadoAutomata.setForeground(Color.BLACK);
        lblEstadoAutomata.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTop.add(lblEstadoAutomata);
        frame.add(panelTop, BorderLayout.NORTH);

        // Estilos de las pestañas
        UIManager.put("TabbedPane.selected", new Color(200, 210, 220));
        UIManager.put("TabbedPane.unselectedBackground", new Color(242, 245, 248));
        UIManager.put("TabbedPane.unselectedForeground", new Color(120, 130, 140));
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 20, 8, 20));
        UIManager.put("TabbedPane.textIconGap", 10);
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI());
        tabbedPane.setBackground(new Color(242, 245, 248));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Integración de los paneles separados
        tabbedPane.addTab("Crear Autómata", new PanelCrearAutomata(this));
        tabbedPane.addTab("Cargar Archivo", new PanelCargarArchivo(this));
        tabbedPane.addTab("Evaluar Lote", new PanelEvaluarLote(this));
        tabbedPane.addTab("Trazabilidad", new PanelTrazabilidad(this));

        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    // Métodos para compartir datos entre las pestañas

    public MotorEvaluacion getMotor() { return motor; }
    public Automata getAutomataActual() { return automataActual; }
    public JFrame getFrame() { return frame; }
    public JComboBox<String> getCbCadenasTraza() { return cbCadenasTraza; }

    public void setAutomataActual(Automata automata) {
        this.automataActual = automata;
        lblEstadoAutomata.setText("Autómata cargado: " + automata.getTipo());
    }
}