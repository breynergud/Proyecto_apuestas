package vista;

import controlador.UsuarioControlador;
import controlador.ApuestaControlador;
import modelo.Usuario;
import modelo.Partido;
import modelo.Apuesta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends JFrame {

    private Usuario usuarioLogueado;
    private UsuarioControlador usuarioControlador;
    private ApuestaControlador apuestaControlador;

    // Elementos de la interfaz
    private JTabbedPane tabbedPane;
    private JComboBox<String> comboGruposPronosticos;
    private JPanel panelPartidosApuestas;
    private List<PartidoApuestaPanel> listaPanelesApuestas;

    private JComboBox<String> comboGruposResultados;
    private JPanel panelPartidosResultados;
    private List<PartidoResultadoPanel> listaPanelesResultados;

    private JTable tablaPosiciones;
    private DefaultTableModel modeloTablaPosiciones;

    private JComboBox<String> comboGruposConsulta;
    private JTextArea txtAreaConsultaEquipos;
    private JTable tablaConsultaPartidos;
    private DefaultTableModel modeloTablaConsultaPartidos;
    private JTextField txtBuscarEquipo;
    private JButton btnBuscarEquipo;

    private String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    public MenuPrincipal(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.usuarioControlador = new UsuarioControlador();
        this.apuestaControlador = new ApuestaControlador();

        setTitle("Quiniela Mundial 2026 - Dashboard");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Principal con estilo Dark
        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(new Color(25, 25, 30));
        setContentPane(panelRaiz);

        // Banner superior
        JPanel panelBanner = new JPanel(new BorderLayout());
        panelBanner.setBackground(new Color(30, 30, 36));
        panelBanner.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblBienvenida = new JLabel("⚽ Bienvenido, " + usuarioLogueado.getNombre() + " !", SwingConstants.LEFT);
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBienvenida.setForeground(Color.WHITE);
        panelBanner.add(lblBienvenida, BorderLayout.WEST);

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            new Login().setVisible(true);
            this.dispose();
        });
        panelBanner.add(btnLogout, BorderLayout.EAST);

        panelRaiz.add(panelBanner, BorderLayout.NORTH);

        // Inicializar el JTabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(38, 38, 45));
        tabbedPane.setForeground(Color.WHITE);

        // Los usuarios normales registran apuestas
        if (!usuarioLogueado.esAdministrador()) {
            configurarPestanaPronosticos();
        }
        
        // Solo el administrador puede registrar resultados reales
        if (usuarioLogueado.esAdministrador()) {
            configurarPestanaResultados();
        }

        configurarPestanaPosiciones();
        configurarPestanaConsultas();

        panelRaiz.add(tabbedPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // PESTAÑA 1: REGISTRAR PRONÓSTICOS / APUESTAS
    // =========================================================================
    private void configurarPestanaPronosticos() {
        JPanel panelTab = new JPanel(new BorderLayout(10, 10));
        panelTab.setBackground(new Color(25, 25, 30));
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltro.setOpaque(false);
        JLabel lblGrupo = new JLabel("Seleccionar Grupo:");
        lblGrupo.setForeground(Color.WHITE);
        lblGrupo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltro.add(lblGrupo);

        comboGruposPronosticos = new JComboBox<>(gruposLetras);
        comboGruposPronosticos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboGruposPronosticos.addActionListener(e -> cargarPartidosParaApuestas());
        panelFiltro.add(comboGruposPronosticos);

        panelTab.add(panelFiltro, BorderLayout.NORTH);

        // Panel de partidos (dentro de un scroll pane)
        panelPartidosApuestas = new JPanel();
        panelPartidosApuestas.setLayout(new BoxLayout(panelPartidosApuestas, BoxLayout.Y_AXIS));
        panelPartidosApuestas.setBackground(new Color(30, 30, 36));

        JScrollPane scrollPane = new JScrollPane(panelPartidosApuestas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 60)));
        panelTab.add(scrollPane, BorderLayout.CENTER);

        JButton btnGuardarApuestas = new JButton("Guardar Pronósticos del Grupo");
        btnGuardarApuestas.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardarApuestas.setBackground(new Color(46, 204, 113));
        btnGuardarApuestas.setForeground(Color.WHITE);
        btnGuardarApuestas.setFocusPainted(false);
        btnGuardarApuestas.addActionListener(this::guardarApuestasGrupo);
        panelTab.add(btnGuardarApuestas, BorderLayout.SOUTH);

        tabbedPane.addTab("📝 Mis Pronósticos", panelTab);
        cargarPartidosParaApuestas();
    }

    private void cargarPartidosParaApuestas() {
        panelPartidosApuestas.removeAll();
        listaPanelesApuestas = new ArrayList<>();

        String grupoSeleccionado = (String) comboGruposPronosticos.getSelectedItem();
        List<Partido> partidos = apuestaControlador.obtenerPartidosPorGrupo(grupoSeleccionado);

        for (Partido p : partidos) {
            // Cargar apuesta previa si existe
            Apuesta previa = apuestaControlador.buscarApuestaUsuario(usuarioLogueado.getId(), p.getId());
            PartidoApuestaPanel panelPart = new PartidoApuestaPanel(p, previa);
            panelPartidosApuestas.add(panelPart);
            listaPanelesApuestas.add(panelPart);
        }

        panelPartidosApuestas.revalidate();
        panelPartidosApuestas.repaint();
    }

    private void guardarApuestasGrupo(ActionEvent e) {
        for (PartidoApuestaPanel pPanel : listaPanelesApuestas) {
            int golesL = pPanel.getGolesLocal();
            int golesV = pPanel.getGolesVisitante();
            if (golesL >= 0 && golesV >= 0) {
                apuestaControlador.guardarApuestaUsuario(
                    usuarioLogueado.getId(),
                    pPanel.getPartidoId(),
                    golesL,
                    golesV
                );
            }
        }
        JOptionPane.showMessageDialog(this, "¡Tus pronósticos del grupo han sido guardados con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        actualizarRankingTable(); // Recargar posiciones por si hay aciertos
    }

    // Panel contenedor individual para cada partido en la lista de pronósticos
    class PartidoApuestaPanel extends JPanel {
        private int partidoId;
        private JSpinner spinLocal;
        private JSpinner spinVisita;

        public PartidoApuestaPanel(Partido part, Apuesta apuestaPrevia) {
            this.partidoId = part.getId();
            setLayout(new GridLayout(1, 5, 10, 0));
            setBackground(new Color(40, 40, 48));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 55, 65)),
                new EmptyBorder(10, 15, 10, 15)
            ));

            JLabel lblEquipos = new JLabel(part.getLocal() + "  vs  " + part.getVisitante());
            lblEquipos.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblEquipos.setForeground(Color.WHITE);
            add(lblEquipos);

            JLabel lblFecha = new JLabel("📅 " + part.getFecha(), SwingConstants.CENTER);
            lblFecha.setForeground(Color.GRAY);
            add(lblFecha);

            int valLocal = (apuestaPrevia != null) ? apuestaPrevia.getGolesLocalApuesta() : 0;
            int valVisita = (apuestaPrevia != null) ? apuestaPrevia.getGolesVisitanteApuesta() : 0;

            spinLocal = new JSpinner(new SpinnerNumberModel(valLocal, 0, 20, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(valVisita, 0, 20, 1));

            JPanel panelSpinLocal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelSpinLocal.setOpaque(false);
            panelSpinLocal.add(new JLabel("Goles Local: "));
            panelSpinLocal.getComponent(0).setForeground(Color.LIGHT_GRAY);
            panelSpinLocal.add(spinLocal);
            add(panelSpinLocal);

            JPanel panelSpinVisita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelSpinVisita.setOpaque(false);
            panelSpinVisita.add(new JLabel("Goles Visita: "));
            panelSpinVisita.getComponent(0).setForeground(Color.LIGHT_GRAY);
            panelSpinVisita.add(spinVisita);
            add(panelSpinVisita);
            
            JLabel lblEstado = new JLabel(part.isRegistrado() ? "Finalizado" : "Pendiente", SwingConstants.CENTER);
            lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblEstado.setForeground(part.isRegistrado() ? new Color(46, 204, 113) : new Color(241, 196, 15));
            add(lblEstado);
        }

        public int getPartidoId() { return partidoId; }
        public int getGolesLocal() { return (int) spinLocal.getValue(); }
        public int getGolesVisitante() { return (int) spinVisita.getValue(); }
    }

    // =========================================================================
    // PESTAÑA 2: REGISTRAR RESULTADOS REALES (ADMIN MODE)
    // =========================================================================
    private void configurarPestanaResultados() {
        JPanel panelTab = new JPanel(new BorderLayout(10, 10));
        panelTab.setBackground(new Color(25, 25, 30));
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltro.setOpaque(false);
        JLabel lblGrupo = new JLabel("Seleccionar Grupo (Admin):");
        lblGrupo.setForeground(Color.WHITE);
        lblGrupo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltro.add(lblGrupo);

        comboGruposResultados = new JComboBox<>(gruposLetras);
        comboGruposResultados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboGruposResultados.addActionListener(e -> cargarPartidosParaResultados());
        panelFiltro.add(comboGruposResultados);

        panelTab.add(panelFiltro, BorderLayout.NORTH);

        // Lista de partidos para ingresar resultados reales
        panelPartidosResultados = new JPanel();
        panelPartidosResultados.setLayout(new BoxLayout(panelPartidosResultados, BoxLayout.Y_AXIS));
        panelPartidosResultados.setBackground(new Color(30, 30, 36));

        JScrollPane scrollPane = new JScrollPane(panelPartidosResultados);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 60)));
        panelTab.add(scrollPane, BorderLayout.CENTER);

        JButton btnGuardarResultados = new JButton("Registrar Resultados del Grupo");
        btnGuardarResultados.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardarResultados.setBackground(new Color(52, 152, 219));
        btnGuardarResultados.setForeground(Color.WHITE);
        btnGuardarResultados.setFocusPainted(false);
        btnGuardarResultados.addActionListener(this::guardarResultadosGrupo);
        panelTab.add(btnGuardarResultados, BorderLayout.SOUTH);

        tabbedPane.addTab("🏆 Resultados Reales (Admin)", panelTab);
        cargarPartidosParaResultados();
    }

    private void cargarPartidosParaResultados() {
        panelPartidosResultados.removeAll();
        listaPanelesResultados = new ArrayList<>();

        String grupoSeleccionado = (String) comboGruposResultados.getSelectedItem();
        List<Partido> partidos = apuestaControlador.obtenerPartidosPorGrupo(grupoSeleccionado);

        for (Partido p : partidos) {
            PartidoResultadoPanel panelPart = new PartidoResultadoPanel(p);
            panelPartidosResultados.add(panelPart);
            listaPanelesResultados.add(panelPart);
        }

        panelPartidosResultados.revalidate();
        panelPartidosResultados.repaint();
    }

    private void guardarResultadosGrupo(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas actualizar los marcadores oficiales?\nEsto recalculará todos los puntos del ranking automáticamente.", "Confirmar Resultados", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        for (PartidoResultadoPanel pPanel : listaPanelesResultados) {
            if (pPanel.isFinalizadoChecked()) {
                apuestaControlador.registrarResultadoReal(
                    pPanel.getPartidoId(),
                    pPanel.getGolesLocal(),
                    pPanel.getGolesVisitante()
                );
            } else {
                // Si desmarcan finalizado, vuelve a pendiente (nulo)
                apuestaControlador.registrarResultadoReal(pPanel.getPartidoId(), null, null);
            }
        }
        JOptionPane.showMessageDialog(this, "¡Resultados oficiales registrados y puntajes recalculados!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        cargarPartidosParaResultados();
        actualizarRankingTable(); // Recargar el ranking en la UI
    }

    class PartidoResultadoPanel extends JPanel {
        private int partidoId;
        private JSpinner spinLocal;
        private JSpinner spinVisita;
        private JCheckBox chkFinalizado;

        public PartidoResultadoPanel(Partido part) {
            this.partidoId = part.getId();
            setLayout(new GridLayout(1, 5, 10, 0));
            setBackground(new Color(40, 40, 48));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 55, 65)),
                new EmptyBorder(10, 15, 10, 15)
            ));

            JLabel lblEquipos = new JLabel(part.getLocal() + "  vs  " + part.getVisitante());
            lblEquipos.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblEquipos.setForeground(Color.WHITE);
            add(lblEquipos);

            int valLocal = (part.getGolesLocal() != null) ? part.getGolesLocal() : 0;
            int valVisita = (part.getGolesVisitante() != null) ? part.getGolesVisitante() : 0;

            spinLocal = new JSpinner(new SpinnerNumberModel(valLocal, 0, 20, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(valVisita, 0, 20, 1));

            JPanel panelSpinLocal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelSpinLocal.setOpaque(false);
            panelSpinLocal.add(new JLabel("Marcador Local: "));
            panelSpinLocal.getComponent(0).setForeground(Color.LIGHT_GRAY);
            panelSpinLocal.add(spinLocal);
            add(panelSpinLocal);

            JPanel panelSpinVisita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelSpinVisita.setOpaque(false);
            panelSpinVisita.add(new JLabel("Marcador Visita: "));
            panelSpinVisita.getComponent(0).setForeground(Color.LIGHT_GRAY);
            panelSpinVisita.add(spinVisita);
            add(panelSpinVisita);

            chkFinalizado = new JCheckBox("Finalizado", part.isRegistrado());
            chkFinalizado.setOpaque(false);
            chkFinalizado.setForeground(Color.WHITE);
            chkFinalizado.setFont(new Font("Segoe UI", Font.BOLD, 12));
            add(chkFinalizado);
        }

        public int getPartidoId() { return partidoId; }
        public int getGolesLocal() { return (int) spinLocal.getValue(); }
        public int getGolesVisitante() { return (int) spinVisita.getValue(); }
        public boolean isFinalizadoChecked() { return chkFinalizado.isSelected(); }
    }

    // =========================================================================
    // PESTAÑA 3: TABLA DE POSICIONES / LEADERBOARD
    // =========================================================================
    private void configurarPestanaPosiciones() {
        JPanel panelTab = new JPanel(new BorderLayout(10, 10));
        panelTab.setBackground(new Color(25, 25, 30));
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblRanking = new JLabel("Tabla General de Posiciones", SwingConstants.CENTER);
        lblRanking.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRanking.setForeground(Color.WHITE);
        panelTab.add(lblRanking, BorderLayout.NORTH);

        String[] columnas = {"Puesto", "Apostador / Jugador", "Puntos Acumulados"};
        modeloTablaPosiciones = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaPosiciones = new JTable(modeloTablaPosiciones);
        tablaPosiciones.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaPosiciones.setRowHeight(25);
        tablaPosiciones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollTable = new JScrollPane(tablaPosiciones);
        panelTab.add(scrollTable, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("🔄 Actualizar Clasificación");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.addActionListener(e -> actualizarRankingTable());
        panelTab.add(btnActualizar, BorderLayout.SOUTH);

        tabbedPane.addTab("📈 Tabla de Posiciones", panelTab);
        actualizarRankingTable();
    }

    private void actualizarRankingTable() {
        modeloTablaPosiciones.setRowCount(0);
        List<Object[]> list = usuarioControlador.obtenerTablaPosiciones();
        int puesto = 1;
        for (Object[] fila : list) {
            modeloTablaPosiciones.addRow(new Object[]{
                puesto,
                fila[0],
                fila[1] + " pts"
            });
            puesto++;
        }
    }

    // =========================================================================
    // PESTAÑA 4: CONSULTAS (EQUIPOS, FIXTURE, BUSCADOR)
    // =========================================================================
    private void configurarPestanaConsultas() {
        JPanel panelTab = new JPanel(new BorderLayout(15, 15));
        panelTab.setBackground(new Color(25, 25, 30));
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel superior - Filtros y Buscador
        JPanel panelControles = new JPanel(new GridLayout(2, 1, 8, 8));
        panelControles.setOpaque(false);

        // Control 1: Consulta de grupo
        JPanel panelFiltroGrupo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltroGrupo.setOpaque(false);
        JLabel lblGrupo = new JLabel("Ver Equipos del Grupo:");
        lblGrupo.setForeground(Color.WHITE);
        lblGrupo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltroGrupo.add(lblGrupo);

        comboGruposConsulta = new JComboBox<>(gruposLetras);
        comboGruposConsulta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboGruposConsulta.addActionListener(e -> ejecutarConsultaGrupo());
        panelFiltroGrupo.add(comboGruposConsulta);
        
        txtAreaConsultaEquipos = new JTextArea(1, 40);
        txtAreaConsultaEquipos.setEditable(false);
        txtAreaConsultaEquipos.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 13));
        txtAreaConsultaEquipos.setBackground(new Color(30,30,36));
        txtAreaConsultaEquipos.setForeground(new Color(46, 204, 113));
        panelFiltroGrupo.add(txtAreaConsultaEquipos);

        panelControles.add(panelFiltroGrupo);

        // Control 2: Buscador por equipo
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBusqueda.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar partidos de un equipo:");
        lblBuscar.setForeground(Color.WHITE);
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelBusqueda.add(lblBuscar);

        txtBuscarEquipo = new JTextField(15);
        txtBuscarEquipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelBusqueda.add(txtBuscarEquipo);

        btnBuscarEquipo = new JButton("Buscar");
        btnBuscarEquipo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscarEquipo.addActionListener(e -> buscarPartidosDeEquipo());
        panelBusqueda.add(btnBuscarEquipo);

        panelControles.add(panelBusqueda);

        panelTab.add(panelControles, BorderLayout.NORTH);

        // Tabla de partidos consultados
        String[] colPartidos = {"Grupo", "Local", "Marcador", "Visitante", "Fecha", "Estado"};
        modeloTablaConsultaPartidos = new DefaultTableModel(colPartidos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaConsultaPartidos = new JTable(modeloTablaConsultaPartidos);
        tablaConsultaPartidos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaConsultaPartidos.setRowHeight(24);

        JScrollPane scrollTable = new JScrollPane(tablaConsultaPartidos);
        panelTab.add(scrollTable, BorderLayout.CENTER);

        tabbedPane.addTab("🔍 Buscador / Consultas", panelTab);
        ejecutarConsultaGrupo();
    }

    private void ejecutarConsultaGrupo() {
        String grupoSeleccionado = (String) comboGruposConsulta.getSelectedItem();
        
        // 1. Mostrar los 4 equipos en el JTextArea superior
        List<String> equipos = apuestaControlador.obtenerEquiposDelGrupo(grupoSeleccionado);
        StringBuilder sb = new StringBuilder("  Equipos: ");
        for (int i = 0; i < equipos.size(); i++) {
            sb.append(equipos.get(i));
            if (i < equipos.size() - 1) sb.append(" | ");
        }
        txtAreaConsultaEquipos.setText(sb.toString());

        // 2. Cargar partidos en la tabla inferior
        modeloTablaConsultaPartidos.setRowCount(0);
        List<Partido> partidos = apuestaControlador.obtenerPartidosPorGrupo(grupoSeleccionado);
        for (Partido p : partidos) {
            String marcador = p.isRegistrado() ? (p.getGolesLocal() + " - " + p.getGolesVisitante()) : "vs";
            String estado = p.isRegistrado() ? "Finalizado" : "Pendiente";
            modeloTablaConsultaPartidos.addRow(new Object[]{
                p.getGrupoId(),
                p.getLocal(),
                marcador,
                p.getVisitante(),
                p.getFecha(),
                estado
            });
        }
    }

    private void buscarPartidosDeEquipo() {
        String nombreEquipo = txtBuscarEquipo.getText().trim();
        if (nombreEquipo.isEmpty()) {
            ejecutarConsultaGrupo();
            return;
        }

        modeloTablaConsultaPartidos.setRowCount(0);
        List<Partido> partidos = apuestaControlador.obtenerPartidosDelMundial();
        boolean encontrado = false;

        for (Partido p : partidos) {
            if (p.getLocal().equalsIgnoreCase(nombreEquipo) || p.getVisitante().equalsIgnoreCase(nombreEquipo)) {
                String marcador = p.isRegistrado() ? (p.getGolesLocal() + " - " + p.getGolesVisitante()) : "vs";
                String estado = p.isRegistrado() ? "Finalizado" : "Pendiente";
                modeloTablaConsultaPartidos.addRow(new Object[]{
                    p.getGrupoId(),
                    p.getLocal(),
                    marcador,
                    p.getVisitante(),
                    p.getFecha(),
                    estado
                });
                encontrado = true;
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(this, "No se encontraron partidos para el equipo: " + nombreEquipo, "Sin coincidencias", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
