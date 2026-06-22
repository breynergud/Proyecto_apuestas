package vista;

import modelo.Usuario;
import modelo.Apuesta;
import controlador.UsuarioControlador;
import controlador.ApuestaControlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApuestasPanel extends JPanel {

    private final MenuPrincipal parent;
    private final Usuario usuarioLogueado;
    private final UsuarioControlador usuarioControlador;
    private final ApuestaControlador apuestaControlador;

    private JComboBox<String> comboApuestasUsuario;
    private JComboBox<String> comboApuestasGrupo;
    private JTable tablaApuestas;
    private DefaultTableModel modeloTablaApuestas;
    private final String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    public ApuestasPanel(MenuPrincipal parent, Usuario usuarioLogueado, UsuarioControlador usuarioControlador, ApuestaControlador apuestaControlador) {
        this.parent = parent;
        this.usuarioLogueado = usuarioLogueado;
        this.usuarioControlador = usuarioControlador;
        this.apuestaControlador = apuestaControlador;

        setLayout(new BorderLayout(10, 10));
        setBackground(UIStyleUtil.FONDO);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);

        String tituloLabel = usuarioLogueado.esAdministrador() ? 
            "Listado General de Pronósticos del Sistema" : 
            "Mis Pronósticos Registrados";
        JLabel lblTitulo = new JLabel(tituloLabel, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(UIStyleUtil.VERDE_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblTitulo);
        panelNorte.add(Box.createVerticalStrut(10));

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelFiltros.setOpaque(false);

        if (usuarioLogueado.esAdministrador()) {
            JLabel lblFiltroUsuario = new JLabel("Filtrar por Usuario:");
            lblFiltroUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblFiltroUsuario.setForeground(UIStyleUtil.VERDE_OSCURO);

            comboApuestasUsuario = new JComboBox<>();
            comboApuestasUsuario.setBackground(Color.WHITE);
            comboApuestasUsuario.setForeground(Color.BLACK);

            panelFiltros.add(lblFiltroUsuario);
            panelFiltros.add(comboApuestasUsuario);
        }

        JLabel lblFiltroGrupo = new JLabel("Filtrar por Grupo:");
        lblFiltroGrupo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroGrupo.setForeground(UIStyleUtil.VERDE_OSCURO);

        String[] gruposConTodos = new String[gruposLetras.length + 1];
        gruposConTodos[0] = "Todos";
        System.arraycopy(gruposLetras, 0, gruposConTodos, 1, gruposLetras.length);
        comboApuestasGrupo = new JComboBox<>(gruposConTodos);
        comboApuestasGrupo.setBackground(Color.WHITE);
        comboApuestasGrupo.setForeground(Color.BLACK);

        panelFiltros.add(lblFiltroGrupo);
        panelFiltros.add(comboApuestasGrupo);

        panelNorte.add(panelFiltros);
        add(panelNorte, BorderLayout.NORTH);

        String[] columnas = {"Apostador", "Local", "Visitante", "Goles L", "Goles V"};
        modeloTablaApuestas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaApuestas = new JTable(modeloTablaApuestas);
        UIStyleUtil.styleTable(tablaApuestas);

        JScrollPane scrollPane = new JScrollPane(tablaApuestas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        scrollPane.getViewport().setBackground(UIStyleUtil.FONDO);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnActualizar = UIStyleUtil.btnRedondeado("Actualizar Pron\u00f3sticos", UIStyleUtil.VERDE_BTN, Color.WHITE);
        btnActualizar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                parent,
                "Para actualizar tus pron\u00f3sticos debes ir al panel de Partidos.\n\u00bfDeseas ir ahora?",
                "Actualizar Pron\u00f3sticos",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                parent.mostrarTab("pronosticos", 1);
            }
        });
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(btnActualizar);
        add(panelBoton, BorderLayout.SOUTH);

        java.awt.event.ActionListener filtroListener = e -> cargarDatosTablaApuestas();
        if (comboApuestasUsuario != null) {
            comboApuestasUsuario.addActionListener(filtroListener);
        }
        comboApuestasGrupo.addActionListener(filtroListener);

        cargarFiltroApuestasUsuarios();
        cargarDatosTablaApuestas();
    }

    public void cargarFiltroApuestasUsuarios() {
        if (comboApuestasUsuario == null) return;
        
        java.awt.event.ActionListener[] listeners = comboApuestasUsuario.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            comboApuestasUsuario.removeActionListener(l);
        }
        
        String seleccionActual = (String) comboApuestasUsuario.getSelectedItem();
        
        comboApuestasUsuario.removeAllItems();
        comboApuestasUsuario.addItem("Todos");
        
        List<Usuario> usuarios = usuarioControlador.obtenerListaUsuarios();
        for (Usuario u : usuarios) {
            if (!u.esAdministrador()) {
                comboApuestasUsuario.addItem(u.getNombre());
            }
        }
        
        if (seleccionActual != null) {
            comboApuestasUsuario.setSelectedItem(seleccionActual);
        } else {
            comboApuestasUsuario.setSelectedIndex(0);
        }
        
        for (java.awt.event.ActionListener l : listeners) {
            comboApuestasUsuario.addActionListener(l);
        }
    }

    public void cargarDatosTablaApuestas() {
        if (modeloTablaApuestas == null) return;
        modeloTablaApuestas.setRowCount(0);

        String usuario = comboApuestasUsuario != null ? (String) comboApuestasUsuario.getSelectedItem() : "Todos";
        String grupo = comboApuestasGrupo != null ? (String) comboApuestasGrupo.getSelectedItem() : "Todos";
        if (usuario == null) usuario = "Todos";
        if (grupo == null) grupo = "Todos";

        List<Apuesta> historial;
        if (usuarioLogueado.esAdministrador()) {
            historial = apuestaControlador.obtenerTodasLasApuestas(usuario, grupo);
        } else {
            historial = apuestaControlador.obtenerApuestasPorUsuario(usuarioLogueado.getId(), grupo);
        }

        for (Apuesta ap : historial) {
            Object[] fila = {
                ap.getNombreApostador(),
                ap.getEquipoLocal(),
                ap.getEquipoVisitante(),
                ap.getGolesLocal(),
                ap.getGolesVisitante()
            };
            modeloTablaApuestas.addRow(fila);
        }
    }
}
