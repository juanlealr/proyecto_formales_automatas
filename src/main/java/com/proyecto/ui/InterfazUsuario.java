package com.proyecto.ui;

import com.proyecto.engine.MotorEvaluacion;
import com.proyecto.model.Automata;

import javax.swing.*;
import java.awt.*;

public class InterfazUsuario {
    private MotorEvaluacion motor;
    private Automata automataActual;

    private JFrame frame;
    private JLabel lblEstadoAutomata;
    private JComboBox<String> cbCadenasTraza; 
    
    // Declaramos el panel como atributo para poder acceder a él luego
    private PanelCrearAutomata panelCrearAutomata; 
    
    //Declaramos el contenedor de pestañas como atributo de la clase
    private JTabbedPane tabbedPane;

    public InterfazUsuario() {
        this.motor = new MotorEvaluacion();
        this.cbCadenasTraza = new JComboBox<>();

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

        JPanel panelTop = new JPanel();
        panelTop.setBackground(new Color(220, 226, 235));
        lblEstadoAutomata = new JLabel("Ningún autómata cargado en memoria", SwingConstants.CENTER);
        lblEstadoAutomata.setForeground(Color.BLACK);
        lblEstadoAutomata.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelTop.add(lblEstadoAutomata);
        frame.add(panelTop, BorderLayout.NORTH);

        UIManager.put("TabbedPane.selected", new Color(200, 210, 220));
        UIManager.put("TabbedPane.unselectedBackground", new Color(242, 245, 248));
        UIManager.put("TabbedPane.unselectedForeground", new Color(120, 130, 140));
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 20, 8, 20));
        UIManager.put("TabbedPane.textIconGap", 10);
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));

        // NUEVO: Usamos el atributo de clase en lugar de declararlo localmente
        this.tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI());
        tabbedPane.setBackground(new Color(242, 245, 248));
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Inicializamos el atributo antes de agregarlo al TabbedPane
        this.panelCrearAutomata = new PanelCrearAutomata(this);

        tabbedPane.addTab("Crear Autómata", this.panelCrearAutomata); // Usamos la variable aquí
        tabbedPane.addTab("Cargar Archivo", new PanelCargarArchivo(this));
        tabbedPane.addTab("Evaluar Lote", new PanelEvaluarLote(this));
        tabbedPane.addTab("Trazabilidad", new PanelTrazabilidad(this));

        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    public MotorEvaluacion getMotor() { return motor; }
    public Automata getAutomataActual() { return automataActual; }
    public JFrame getFrame() { return frame; }
    public JComboBox<String> getCbCadenasTraza() { return cbCadenasTraza; }

    public PanelCrearAutomata getPanelCrearAutomata() { return panelCrearAutomata; }

    public void setAutomataActual(Automata automata) {
        this.automataActual = automata;
        lblEstadoAutomata.setText("Autómata cargado: " + automata.getTipo());
    }

    public void irAPestanaCrear() {
        if (this.tabbedPane != null) {
            // El índice 0 corresponde a la pestaña "Crear Autómata"
            this.tabbedPane.setSelectedIndex(0);
        }
    }
}