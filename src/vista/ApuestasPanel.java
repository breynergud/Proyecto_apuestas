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
    private List<Apuesta> listaApuestasMostradas;
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

        JButton btnActualizar;
        if (usuarioLogueado.esAdministrador()) {
            btnActualizar = UIStyleUtil.btnRedondeado("Actualizar Tabla", UIStyleUtil.VERDE_BTN, Color.WHITE);
            btnActualizar.addActionListener(e -> cargarDatosTablaApuestas());
        } else {
            btnActualizar = UIStyleUtil.btnRedondeado("Actualizar Pron\u00f3stico Seleccionado", UIStyleUtil.VERDE_BTN, Color.WHITE);
            btnActualizar.addActionListener(e -> abrirDialogoActualizarApuesta());
        }
        
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
        this.listaApuestasMostradas = historial;

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

    private void abrirDialogoActualizarApuesta() {
        int filaSel = tablaApuestas.getSelectedRow();
        if (filaSel < 0 || listaApuestasMostradas == null || filaSel >= listaApuestasMostradas.size()) {
            JOptionPane.showMessageDialog(parent, "Por favor, seleccione un pron\u00f3stico de la lista para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Apuesta ap = listaApuestasMostradas.get(filaSel);
        // Doble validación: solo permitir si la apuesta pertenece al usuario logueado
        if (ap.getUsuarioId() != usuarioLogueado.getId()) {
            JOptionPane.showMessageDialog(parent, "Solo puedes actualizar tus propios pron\u00f3sticos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener datos del partido en tiempo real
        modelo.Partido part = apuestaControlador.obtenerPartidoPorId(ap.getPartidoId());
        if (part == null) {
            JOptionPane.showMessageDialog(parent, "No se pudo obtener la informaci\u00f3n del partido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación: que el partido no haya comenzado/finalizado
        if (part.isRegistrado() || UIStyleUtil.haPasadoFecha(part.getFecha())) {
            JOptionPane.showMessageDialog(
                parent,
                "No puedes actualizar este pron\u00f3stico porque el partido ya ha comenzado o finalizado.",
                "Partido no disponible",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Modal emergente de edición
        JDialog dialog = new JDialog(parent, "Actualizar Pron\u00f3stico", true);
        dialog.setSize(380, 240);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UIStyleUtil.FONDO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Cabecera: nombres de los equipos
        JLabel lblEquipos = new JLabel(part.getLocal() + " vs " + part.getVisitante(), SwingConstants.CENTER);
        lblEquipos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEquipos.setForeground(UIStyleUtil.VERDE_OSCURO);
        content.add(lblEquipos, BorderLayout.NORTH);

        // Spinners de marcador
        JPanel panelCuerpo = new JPanel(new GridLayout(1, 2, 16, 0));
        panelCuerpo.setOpaque(false);

        int golesL = ap.getGolesLocal();
        int golesV = ap.getGolesVisitante();

        JSpinner spinLocal = new JSpinner(new SpinnerNumberModel(golesL, 0, 15, 1));
        JSpinner spinVisita = new JSpinner(new SpinnerNumberModel(golesV, 0, 15, 1));

        UIStyleUtil.configurarSoloNumeros(spinLocal, 15);
        UIStyleUtil.configurarSoloNumeros(spinVisita, 15);
        UIStyleUtil.styleSpinner(spinLocal);
        UIStyleUtil.styleSpinner(spinVisita);

        panelCuerpo.add(UIStyleUtil.spinPanel("Goles Local", spinLocal));
        panelCuerpo.add(UIStyleUtil.spinPanel("Goles Visita", spinVisita));
        content.add(panelCuerpo, BorderLayout.CENTER);

        // Botonera
        JPanel panelBotonera = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotonera.setOpaque(false);

        JButton btnGuardar = UIStyleUtil.btnRedondeado("Guardar", UIStyleUtil.VERDE_BTN, Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(110, 36));
        btnGuardar.addActionListener(e -> {
            int nLocal = ((Number) spinLocal.getValue()).intValue();
            int nVisita = ((Number) spinVisita.getValue()).intValue();

            // Guardar apuesta
            apuestaControlador.guardarApuestaUsuario(usuarioLogueado.getId(), part.getId(), nLocal, nVisita);
            dialog.dispose();

            JOptionPane.showMessageDialog(parent, "\u00a1Pron\u00f3stico actualizado con \u00e9xito!", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            parent.refrescarTodo();
        });

        JButton btnCancelar = UIStyleUtil.btnRedondeado("Cancelar", new Color(130, 140, 130), Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(110, 36));
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotonera.add(btnGuardar);
        panelBotonera.add(btnCancelar);
        content.add(panelBotonera, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}
