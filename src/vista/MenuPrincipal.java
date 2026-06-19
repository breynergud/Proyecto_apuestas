package vista;

import controlador.UsuarioControlador;
import controlador.ApuestaControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MenuPrincipal extends JFrame {

    private final Usuario usuarioLogueado;
    private final UsuarioControlador usuarioControlador;
    private final ApuestaControlador apuestaControlador;

    private JPanel panelContenido;
    private JButton[] navBtns;

    private HomePanel panelHome;
    private PronosticosPanel panelPronosticos;
    private PosicionesPanel panelPosiciones;
    private ApuestasPanel panelApuestas;
    private ResultadosPanel panelResultados;
    private HistorialPanel panelHistorial;

    public MenuPrincipal(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.usuarioControlador = new UsuarioControlador();
        this.apuestaControlador = new ApuestaControlador();

        setTitle("Quiniela Mundial 2026 - Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(UIStyleUtil.FONDO);
        setContentPane(raiz);

        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(UIStyleUtil.FONDO);

        // Inicializar paneles individuales
        panelHome = new HomePanel(this, usuarioLogueado, usuarioControlador);
        panelPronosticos = new PronosticosPanel(this, usuarioLogueado, apuestaControlador);
        panelPosiciones = new PosicionesPanel(usuarioControlador);
        panelApuestas = new ApuestasPanel(this, usuarioLogueado, usuarioControlador, apuestaControlador);

        panelContenido.add(panelHome, "home");
        panelContenido.add(panelPronosticos, "pronosticos");
        panelContenido.add(panelPosiciones, "posiciones");
        panelContenido.add(panelApuestas, "apuestas");

        if (usuarioLogueado.esAdministrador()) {
            panelResultados = new ResultadosPanel(this, apuestaControlador);
            panelHistorial = new HistorialPanel(usuarioControlador, apuestaControlador);
            panelContenido.add(panelResultados, "resultados");
            panelContenido.add(panelHistorial, "historial");
        }

        raiz.add(panelContenido, BorderLayout.CENTER);
        raiz.add(construirSidebar(), BorderLayout.WEST);
        
        mostrarTab("home", 0);
    }

    public void mostrarTab(String key, int idx) {
        ((CardLayout) panelContenido.getLayout()).show(panelContenido, key);
        if (navBtns != null && idx < navBtns.length) {
            for (int i = 0; i < navBtns.length; i++) {
                if (i == idx) {
                    navBtns[i].setForeground(UIStyleUtil.AMARILLO);
                } else {
                    navBtns[i].setForeground(new Color(190, 220, 190));
                }
            }
        }
        refrescarTab(key);
    }

    private void refrescarTab(String key) {
        if ("home".equals(key) && panelHome != null) {
            panelHome.actualizarPuntosYRango();
        }
        if ("posiciones".equals(key) && panelPosiciones != null) {
            panelPosiciones.actualizarRankingTable();
        }
        if ("pronosticos".equals(key) && panelPronosticos != null) {
            panelPronosticos.cargarPartidosParaApuestas();
        }
        if ("resultados".equals(key) && panelResultados != null) {
            panelResultados.cargarPartidosParaResultados();
        }
        if ("apuestas".equals(key) && panelApuestas != null) {
            panelApuestas.cargarFiltroApuestasUsuarios();
            panelApuestas.cargarDatosTablaApuestas();
        }
        if ("historial".equals(key) && panelHistorial != null) {
            panelHistorial.cargarFiltroUsuarios();
            panelHistorial.actualizarHistorialTable();
        }
    }

    public void refrescarTodo() {
        if (panelHome != null) panelHome.actualizarPuntosYRango();
        if (panelPosiciones != null) panelPosiciones.actualizarRankingTable();
        if (panelApuestas != null) {
            panelApuestas.cargarFiltroApuestasUsuarios();
            panelApuestas.cargarDatosTablaApuestas();
        }
        if (panelHistorial != null) {
            panelHistorial.cargarFiltroUsuarios();
            panelHistorial.actualizarHistorialTable();
        }
    }

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UIStyleUtil.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setOpaque(false);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 60, 70)));

        // --- TOP SECTION: Logo + Profile ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel lblLogo = new JLabel("WC 2026");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(UIStyleUtil.AMARILLO);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblLogo);
        topPanel.add(Box.createVerticalStrut(20));

        // Profile Avatar
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 200, 180));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(UIStyleUtil.VERDE_OSCURO);
                String ini = usuarioLogueado.getNombre().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini, (getWidth() - fm.stringWidth(ini)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(48, 48));
        avatar.setMaximumSize(new Dimension(48, 48));
        avatar.setOpaque(false);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(avatar);
        topPanel.add(Box.createVerticalStrut(10));

        JLabel lblUser = new JLabel(usuarioLogueado.getNombre());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(Color.WHITE);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblUser);

        JLabel lblRole = new JLabel(usuarioLogueado.esAdministrador() ? "Administrador" : "Apostador");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(new Color(170, 200, 170));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(lblRole);

        sidebar.add(topPanel, BorderLayout.NORTH);

        // --- CENTER SECTION: Menu Buttons ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        String[][] tabs;
        if (usuarioLogueado.esAdministrador()) {
            tabs = new String[][]{
                {"Inicio", "home"},
                {"Apuestas", "apuestas"},
                {"Posiciones", "posiciones"},
                {"Resultados", "resultados"},
                {"Historial", "historial"}
            };
        } else {
            tabs = new String[][]{
                {"Inicio", "home"},
                {"Partidos", "pronosticos"},
                {"Mis Apuestas", "apuestas"},
                {"Posiciones", "posiciones"}
            };
        }

        navBtns = new JButton[tabs.length];

        for (int i = 0; i < tabs.length; i++) {
            final int idx = i;
            final String key = tabs[i][1];
            JButton btn = new JButton(tabs[i][0]) {
                @Override protected void paintComponent(Graphics g) {
                    if (getModel().isRollover() || getForeground().equals(UIStyleUtil.AMARILLO)) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(30, 80, 42));
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setForeground(i == 0 ? UIStyleUtil.AMARILLO : new Color(190, 220, 190));
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setPreferredSize(new Dimension(200, 40));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(new EmptyBorder(0, 15, 0, 0));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> mostrarTab(key, idx));
            navBtns[i] = btn;
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(6));
        }

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // --- BOTTOM SECTION: Logout Button ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JButton btnLogout = new JButton("Cerrar Sesión") {
            @Override protected void paintComponent(Graphics g) {
                if (getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 60, 60));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(0, 40));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new Login().setVisible(true);
            this.dispose();
        });

        bottomPanel.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }
}
