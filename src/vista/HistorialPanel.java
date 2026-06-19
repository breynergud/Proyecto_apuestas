package vista;

import modelo.Usuario;
import controlador.UsuarioControlador;
import controlador.ApuestaControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistorialPanel extends JPanel {

    private final UsuarioControlador usuarioControlador;
    private final ApuestaControlador apuestaControlador;

    private JComboBox<String> comboFiltroUsuario;
    private JComboBox<String> comboFiltroGrupo;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTablaHistorial;
    private final String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    public HistorialPanel(UsuarioControlador usuarioControlador, ApuestaControlador apuestaControlador) {
        this.usuarioControlador = usuarioControlador;
        this.apuestaControlador = apuestaControlador;

        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyleUtil.FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);

        JLabel lblHistorial = new JLabel("Historial General de Pronósticos Guardados", SwingConstants.CENTER);
        lblHistorial.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHistorial.setForeground(UIStyleUtil.VERDE_OSCURO);
        lblHistorial.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblHistorial);
        panelNorte.add(Box.createVerticalStrut(10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelFiltros.setOpaque(false);

        JLabel lblFiltroUsuario = new JLabel("Filtrar por Usuario:");
        lblFiltroUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroUsuario.setForeground(UIStyleUtil.VERDE_OSCURO);

        comboFiltroUsuario = new JComboBox<>();
        comboFiltroUsuario.setBackground(Color.WHITE);
        comboFiltroUsuario.setForeground(Color.BLACK);

        JLabel lblFiltroGrupo = new JLabel("Filtrar por Grupo:");
        lblFiltroGrupo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroGrupo.setForeground(UIStyleUtil.VERDE_OSCURO);

        String[] gruposConTodos = new String[gruposLetras.length + 1];
        gruposConTodos[0] = "Todos";
        System.arraycopy(gruposLetras, 0, gruposConTodos, 1, gruposLetras.length);
        comboFiltroGrupo = new JComboBox<>(gruposConTodos);
        comboFiltroGrupo.setBackground(Color.WHITE);
        comboFiltroGrupo.setForeground(Color.BLACK);

        panelFiltros.add(lblFiltroUsuario);
        panelFiltros.add(comboFiltroUsuario);
        panelFiltros.add(lblFiltroGrupo);
        panelFiltros.add(comboFiltroGrupo);

        panelNorte.add(panelFiltros);
        add(panelNorte, BorderLayout.NORTH);

        String[] columnas = {"ID Log", "Apostador / Jugador", "Partido", "Predicción", "Fecha de Registro", "Acción"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTablaHistorial);
        UIStyleUtil.styleTable(tablaHistorial);

        JScrollPane scrollTable = new JScrollPane(tablaHistorial);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        scrollTable.getViewport().setBackground(UIStyleUtil.FONDO);
        add(scrollTable, BorderLayout.CENTER);

        JButton btnActualizar = UIStyleUtil.btnRedondeado("Actualizar Historial", UIStyleUtil.VERDE_BTN, Color.WHITE);
        btnActualizar.addActionListener(e -> actualizarHistorialTable());
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(btnActualizar);
        add(panelBoton, BorderLayout.SOUTH);

        cargarFiltroUsuarios();

        java.awt.event.ActionListener filtroListener = e -> actualizarHistorialTable();
        comboFiltroUsuario.addActionListener(filtroListener);
        comboFiltroGrupo.addActionListener(filtroListener);

        actualizarHistorialTable();
    }

    public void cargarFiltroUsuarios() {
        if (comboFiltroUsuario == null) return;
        
        java.awt.event.ActionListener[] listeners = comboFiltroUsuario.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            comboFiltroUsuario.removeActionListener(l);
        }
        
        String seleccionActual = (String) comboFiltroUsuario.getSelectedItem();
        
        comboFiltroUsuario.removeAllItems();
        comboFiltroUsuario.addItem("Todos");
        
        List<Usuario> usuarios = usuarioControlador.obtenerListaUsuarios();
        for (Usuario u : usuarios) {
            if (!u.esAdministrador()) {
                comboFiltroUsuario.addItem(u.getNombre());
            }
        }
        
        if (seleccionActual != null) {
            comboFiltroUsuario.setSelectedItem(seleccionActual);
        } else {
            comboFiltroUsuario.setSelectedIndex(0);
        }
        
        for (java.awt.event.ActionListener l : listeners) {
            comboFiltroUsuario.addActionListener(l);
        }
    }

    public void actualizarHistorialTable() {
        if (modeloTablaHistorial == null) return;
        modeloTablaHistorial.setRowCount(0);
        
        String usuario = comboFiltroUsuario != null ? (String) comboFiltroUsuario.getSelectedItem() : "Todos";
        String grupo = comboFiltroGrupo != null ? (String) comboFiltroGrupo.getSelectedItem() : "Todos";
        
        if (usuario == null) usuario = "Todos";
        if (grupo == null) grupo = "Todos";
        
        List<Object[]> logs = apuestaControlador.obtenerHistorialApuestas(usuario, grupo);
        for (Object[] fila : logs) {
            modeloTablaHistorial.addRow(fila);
        }
    }
}
