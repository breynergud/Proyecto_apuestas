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
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends JFrame {

    private static final Color SIDEBAR_BG    = new Color(20, 60, 30);   // Deep green sidebar bg
    private static final Color VERDE_OSCURO  = new Color(20, 60, 30);   // Deep green for text/labels
    private static final Color VERDE_BTN     = new Color(22, 68, 35);   // Deep green accent / buttons
    private static final Color AMARILLO      = new Color(230, 190, 40); // Gold yellow select text
    private static final Color FONDO         = new Color(242, 244, 240); // Light main background
    private static final Color CARD_BG       = Color.WHITE;             // White card background
    private static final Color TEXTO_GRIS    = new Color(110, 110, 110); // Gray secondary text
    private static final Color ROJO_LIVE     = new Color(210, 50, 50);   // Bright red
    private static final Color VERDE_OK      = new Color(39, 174, 96);   // Emerald green success
    private static final Color AMARILLO_WARN = new Color(200, 150, 10);  // Gold yellow warning
    private static final Color AMARILLO_SUAVE= new Color(255, 248, 210); // Highlight row bg (light yellow)

    private Usuario usuarioLogueado;
    private UsuarioControlador usuarioControlador;
    private ApuestaControlador apuestaControlador;

    // Pestaña pronósticos
    private JComboBox<String> comboGruposPronosticos;
    private JPanel panelPartidosApuestas;
    private List<PartidoApuestaPanel> listaPanelesApuestas = new ArrayList<>();

    // Pestaña resultados
    private JComboBox<String> comboGruposResultados;
    private JPanel panelPartidosResultados;
    private List<PartidoResultadoPanel> listaPanelesResultados = new ArrayList<>();

    // Pestaña posiciones
    private DefaultTableModel modeloTablaPosiciones;

    // Pestaña consultas
    private JComboBox<String> comboGruposConsulta;
    private JTextArea txtAreaConsultaEquipos;
    private DefaultTableModel modeloTablaConsultaPartidos;

    private JTable tablaHistorial;
    private DefaultTableModel modeloTablaHistorial;
    private JComboBox<String> comboFiltroUsuario;
    private JComboBox<String> comboFiltroGrupo;
    private JComboBox<String> comboApuestasUsuario;
    private JComboBox<String> comboApuestasGrupo;
    private JTextField txtBuscarEquipo;

    private JTable tablaApuestas;
    private DefaultTableModel modeloTablaApuestas;

    private final String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    // Panel de contenido central (cambia según tab activo)
    private JPanel panelContenido;
    // Navbar botones
    private JButton[] navBtns;

    private JLabel lblPuntos;
    private JLabel lblRango;

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
        raiz.setBackground(FONDO);
        setContentPane(raiz);

        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(FONDO);
        panelContenido.add(construirPanelHome(),         "home");
        panelContenido.add(construirPanelPronosticos(),  "pronosticos");
        panelContenido.add(construirPanelPosiciones(),   "posiciones");
        panelContenido.add(construirPanelApuestas(),     "apuestas");
        if (usuarioLogueado.esAdministrador()) {
            panelContenido.add(construirPanelResultados(), "resultados");
            panelContenido.add(construirPanelHistorial(),  "historial");
        }
        raiz.add(panelContenido, BorderLayout.CENTER);

        raiz.add(construirSidebar(), BorderLayout.WEST);
        mostrarTab("home", 0);
    }

    // ── HEADER ───────────────────────────────────────────────────────────────
    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(FONDO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 20, 10, 20));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izq.setOpaque(false);
        JLabel lblLogo = new JLabel("");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        JLabel lblTitulo = new JLabel("WC 2026");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(VERDE_OSCURO);
        izq.add(lblLogo); izq.add(lblTitulo);
        header.add(izq, BorderLayout.WEST);

        // Avatar circular con inicial
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(180, 200, 180));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(VERDE_OSCURO);
                String ini = usuarioLogueado.getNombre().substring(0, 1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini, (getWidth() - fm.stringWidth(ini)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);

        JButton btnLogout = new JButton("Salir");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setToolTipText("Cerrar Sesión");
        btnLogout.setForeground(VERDE_OSCURO);
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new Login().setVisible(true);
            this.dispose();
        });

        der.add(btnLogout);
        der.add(avatar);
        header.add(der, BorderLayout.EAST);
        return header;
    }

    // ── PANEL HOME ────────────────────────────────────────────────────────────
    private JPanel construirPanelHome() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(FONDO);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // LEFT COLUMN (Greeting + Stats + Quick Actions) - Now expanded to full width
        JPanel colLeft = new JPanel();
        colLeft.setLayout(new BoxLayout(colLeft, BoxLayout.Y_AXIS));
        colLeft.setOpaque(false);

        // Saludo
        JLabel lblHola = new JLabel("Hola, " + usuarioLogueado.getNombre() + "!");
        lblHola.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHola.setForeground(VERDE_OSCURO);
        lblHola.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSub = new JLabel("Listo para la jornada de hoy en el Mundial?");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(TEXTO_GRIS);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        colLeft.add(lblHola);
        colLeft.add(Box.createVerticalStrut(4));
        colLeft.add(lblSub);
        colLeft.add(Box.createVerticalStrut(18));

        // Badge PRO MEMBER
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMARILLO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setMaximumSize(new Dimension(150, 32));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblBadge = new JLabel("PRO MEMBER");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(VERDE_OSCURO);
        badge.add(lblBadge);
        
        colLeft.add(badge);
        colLeft.add(Box.createVerticalStrut(20));

        // Stats cards row
        JPanel rowStats = new JPanel(new GridLayout(1, 2, 16, 0));
        rowStats.setOpaque(false);
        rowStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        rowStats.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card puntos
        JPanel cardPuntos = card();
        cardPuntos.setLayout(new BoxLayout(cardPuntos, BoxLayout.Y_AXIS));
        cardPuntos.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel lblPLabel = new JLabel("TUS PUNTOS");
        lblPLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPLabel.setForeground(TEXTO_GRIS);
        lblPuntos = new JLabel("0");
        lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblPuntos.setForeground(VERDE_OSCURO);
        JLabel lblPChange = new JLabel("Puntos acumulados");
        lblPChange.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPChange.setForeground(TEXTO_GRIS);
        cardPuntos.add(lblPLabel); cardPuntos.add(Box.createVerticalStrut(4));
        cardPuntos.add(lblPuntos); cardPuntos.add(lblPChange);

        // Card rango
        JPanel cardRango = card();
        cardRango.setLayout(new BoxLayout(cardRango, BoxLayout.Y_AXIS));
        cardRango.setBorder(new EmptyBorder(16, 20, 16, 20));
        JLabel lblRLabel = new JLabel("RANGO ACTUAL");
        lblRLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRLabel.setForeground(TEXTO_GRIS);
        lblRango = new JLabel("S/R");
        lblRango.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblRango.setForeground(VERDE_OSCURO);
        JLabel lblRSub = new JLabel("Posición actual");
        lblRSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRSub.setForeground(TEXTO_GRIS);
        cardRango.add(lblRLabel); cardRango.add(Box.createVerticalStrut(4));
        cardRango.add(lblRango); cardRango.add(lblRSub);

        rowStats.add(cardPuntos); rowStats.add(cardRango);
        colLeft.add(rowStats);
        colLeft.add(Box.createVerticalStrut(25));

        // Acciones Rápidas
        JLabel lblAcciones = new JLabel("Acciones Rápidas");
        lblAcciones.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblAcciones.setForeground(VERDE_OSCURO);
        lblAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        colLeft.add(lblAcciones);
        colLeft.add(Box.createVerticalStrut(12));

        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 12, 12));
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        gridAcciones.setOpaque(false);
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (usuarioLogueado.esAdministrador()) {
            gridAcciones.add(accionCard("Gestionar\nPartidos", false,  e -> mostrarTab("resultados", 3)));
            gridAcciones.add(accionCard("Consultar\nPosiciones", false, e -> mostrarTab("posiciones", 2)));
            gridAcciones.add(accionCard("Ver Todas Apuestas",  false,  e -> mostrarTab("apuestas", 1)));
            gridAcciones.add(accionCard("Historial Auditoría", false,  e -> {
                cargarFiltroUsuarios();
                actualizarHistorialTable();
                ((CardLayout) panelContenido.getLayout()).show(panelContenido, "historial");
                if (navBtns != null) {
                    for (JButton btn : navBtns) btn.setForeground(new Color(190, 220, 190));
                }
            }));
        } else {
            gridAcciones.add(accionCard("Ver Grupos",         false, e -> mostrarTab("pronosticos", 1)));
            gridAcciones.add(accionCard("Consultar\nPosiciones", false, e -> mostrarTab("posiciones", 3)));
            gridAcciones.add(accionCard("Mis Apuestas",       false, e -> mostrarTab("apuestas", 2)));
            gridAcciones.add(accionCard("Gestionar\nPartidos", true,   e -> JOptionPane.showMessageDialog(this, "Sólo disponible para Administradores.", "Restringido", JOptionPane.WARNING_MESSAGE)));
        }
        colLeft.add(gridAcciones);

        // GridBagLayout constraints for single column layout
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.weightx = 1.0;
        gbcLeft.weighty = 1.0;
        gbcLeft.fill = GridBagConstraints.BOTH;
        gbcLeft.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(colLeft, gbcLeft);

        // Put mainPanel inside JScrollPane so it's scrollable if resized too small
        JScrollPane sp = new JScrollPane(mainPanel);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        sp.setBackground(FONDO);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FONDO);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel accionCard(String texto, boolean gris, java.awt.event.ActionListener action) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(gris ? new Color(220, 220, 220) : CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(22, 10, 22, 10));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.actionPerformed(null); }
        });

        // Manejo de texto con salto de línea
        String htmlText = "<html><center>" + texto.replace("\n", "<br>") + "</center></html>";
        JLabel lblTxt = new JLabel(htmlText, SwingConstants.CENTER);
        lblTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTxt.setForeground(gris ? new Color(80, 80, 80) : VERDE_OSCURO);
        lblTxt.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(lblTxt);
        p.add(Box.createVerticalGlue());
        return p;
    }


    // ── SIDEBAR (DESKTOP NAVIGATION) ──────────────────────────────────────────
    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
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
        lblLogo.setForeground(AMARILLO);
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
                g2.setColor(VERDE_OSCURO);
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
                {"Resultados", "resultados"}
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
                    if (getModel().isRollover() || getForeground().equals(AMARILLO)) {
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
            btn.setForeground(i == 0 ? AMARILLO : new Color(190, 220, 190));
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

    private void mostrarTab(String key, int idx) {
        ((CardLayout) panelContenido.getLayout()).show(panelContenido, key);
        if (navBtns != null && idx < navBtns.length) {
            for (int i = 0; i < navBtns.length; i++) {
                if (i == idx) {
                    navBtns[i].setForeground(AMARILLO);
                } else {
                    navBtns[i].setForeground(new Color(190, 220, 190));
                }
            }
        }
        if ("home".equals(key)) actualizarPuntosYRango();
        if ("posiciones".equals(key)) actualizarRankingTable();
        if ("pronosticos".equals(key) && comboGruposPronosticos != null) cargarPartidosParaApuestas();
        if ("resultados".equals(key) && comboGruposResultados != null) cargarPartidosParaResultados();
        if ("apuestas".equals(key)) {
            cargarFiltroApuestasUsuarios();
            cargarDatosTablaApuestas();
        }
        if ("historial".equals(key)) {
            cargarFiltroUsuarios();
            actualizarHistorialTable();
        }
    }

    // ── PANEL PRONÓSTICOS ─────────────────────────────────────────────────────
    private JPanel construirPanelPronosticos() {
        JPanel tab = new JPanel(new BorderLayout(0, 8));
        tab.setBackground(FONDO);
        tab.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        norte.setOpaque(false);
        JLabel lbl = new JLabel("Grupo:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(VERDE_OSCURO);
        comboGruposPronosticos = new JComboBox<>(gruposLetras);
        comboGruposPronosticos.setBackground(Color.WHITE);
        comboGruposPronosticos.setForeground(Color.BLACK);
        comboGruposPronosticos.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        comboGruposPronosticos.addActionListener(e -> cargarPartidosParaApuestas());
        norte.add(lbl); norte.add(comboGruposPronosticos);
        tab.add(norte, BorderLayout.NORTH);

        panelPartidosApuestas = new JPanel();
        panelPartidosApuestas.setLayout(new BoxLayout(panelPartidosApuestas, BoxLayout.Y_AXIS));
        panelPartidosApuestas.setBackground(CARD_BG);
        JScrollPane sp = new JScrollPane(panelPartidosApuestas);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        sp.getViewport().setBackground(FONDO);
        tab.add(sp, BorderLayout.CENTER);

        JButton btn = btnRedondeado("Guardar Pronósticos", VERDE_BTN, Color.WHITE);
        btn.addActionListener(this::guardarApuestasGrupo);
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setOpaque(false);
        sur.add(btn);
        tab.add(sur, BorderLayout.SOUTH);
        return tab;
    }

    private void cargarPartidosParaApuestas() {
        if (panelPartidosApuestas == null) return;
        panelPartidosApuestas.removeAll();
        listaPanelesApuestas = new ArrayList<>();
        String grupo = (String) comboGruposPronosticos.getSelectedItem();
        for (Partido p : apuestaControlador.obtenerPartidosPorGrupo(grupo)) {
            Apuesta prev = apuestaControlador.buscarApuestaUsuario(usuarioLogueado.getId(), p.getId());
            PartidoApuestaPanel pp = new PartidoApuestaPanel(p, prev);
            panelPartidosApuestas.add(pp);
            listaPanelesApuestas.add(pp);
        }
        panelPartidosApuestas.revalidate();
        panelPartidosApuestas.repaint();
    }

    private void guardarApuestasGrupo(ActionEvent e) {
        boolean guardadoAlguna = false;
        for (PartidoApuestaPanel pPanel : listaPanelesApuestas) {
            if (!pPanel.tieneApuestaPrevia() && !pPanel.isFechaPasada()) {
                int golesL = pPanel.getGolesLocal();
                int golesV = pPanel.getGolesVisitante();
                if (golesL >= 0 && golesV >= 0) {
                    apuestaControlador.guardarApuestaUsuario(
                        usuarioLogueado.getId(),
                        pPanel.getPartidoId(),
                        golesL,
                        golesV
                    );
                    guardadoAlguna = true;
                }
            }
        }
        if (guardadoAlguna) {
            JOptionPane.showMessageDialog(this, "¡Tus nuevos pronósticos han sido guardados con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            actualizarRankingTable(); // Recargar posiciones por si hay aciertos
            cargarPartidosParaApuestas(); // Recargar pestaña para bloquear las apuestas recién guardadas
            cargarDatosTablaApuestas(); // Recargar la tabla de apuestas
        } else {
            JOptionPane.showMessageDialog(this, "No hay nuevos pronósticos para guardar en este grupo.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class PartidoApuestaPanel extends JPanel {
        private final int partidoId;
        private final JSpinner spinLocal, spinVisita;
        private final boolean tieneApuestaPrevia;
        private final boolean fechaPasada;

        PartidoApuestaPanel(Partido part, Apuesta prev) {
            this.partidoId = part.getId();
            this.tieneApuestaPrevia = (prev != null);
            this.fechaPasada = haPasadoFecha(part.getFecha());
            setLayout(new GridLayout(1, 5, 8, 0));
            setBackground(CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 215)),
                new EmptyBorder(10, 14, 10, 14)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel lbl = new JLabel(part.getLocal() + " vs " + part.getVisitante());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(VERDE_OSCURO);
            add(lbl);

            JLabel fecha = new JLabel(part.getFecha(), SwingConstants.CENTER);
            fecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            fecha.setForeground(TEXTO_GRIS);
            add(fecha);

            int vL = prev != null ? prev.getGolesLocalApuesta() : 0;
            int vV = prev != null ? prev.getGolesVisitanteApuesta() : 0;
            spinLocal  = new JSpinner(new SpinnerNumberModel(vL, 0, 20, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(vV, 0, 20, 1));
            configurarSoloNumeros(spinLocal);
            configurarSoloNumeros(spinVisita);
            styleSpinner(spinLocal);
            styleSpinner(spinVisita);
            
            if (tieneApuestaPrevia || fechaPasada) {
                spinLocal.setEnabled(false);
                spinVisita.setEnabled(false);
            }
            
            add(spinPanel("Local", spinLocal));
            add(spinPanel("Visita", spinVisita));

            String estadoTexto = part.isRegistrado() ? "Final" : (fechaPasada ? "Finalizado" : "Pendiente");
            JLabel estado = new JLabel(estadoTexto, SwingConstants.CENTER);
            estado.setFont(new Font("Segoe UI", Font.BOLD, 11));
            estado.setForeground((part.isRegistrado() || fechaPasada) ? VERDE_OK : AMARILLO_WARN);
            add(estado);
        }

        int getPartidoId()      { return partidoId; }
        
        int getGolesLocal() {
            try {
                spinLocal.commitEdit();
            } catch (java.text.ParseException pe) {
                // Revert to last valid value if editing text was invalid
            }
            return (int) spinLocal.getValue();
        }
        
        int getGolesVisitante() {
            try {
                spinVisita.commitEdit();
            } catch (java.text.ParseException pe) {
                // Revert to last valid value if editing text was invalid
            }
            return (int) spinVisita.getValue();
        }
        
        boolean tieneApuestaPrevia() { return tieneApuestaPrevia; }
        boolean isFechaPasada() { return fechaPasada; }
    }

    // ── PANEL POSICIONES ─────────────────────────────────────────────────────
    private JPanel construirPanelPosiciones() {
        JPanel tab = new JPanel(new BorderLayout(0, 10));
        tab.setBackground(FONDO);
        tab.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Tabla de Posiciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(VERDE_OSCURO);
        tab.add(titulo, BorderLayout.NORTH);

        String[] cols = {"Puesto", "Apostador", "Puntos"};
        modeloTablaPosiciones = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modeloTablaPosiciones);
        styleTable(tabla);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        sp.getViewport().setBackground(FONDO);
        tab.add(sp, BorderLayout.CENTER);

        JButton btn = btnRedondeado("Actualizar", VERDE_BTN, Color.WHITE);
        btn.addActionListener(e -> actualizarRankingTable());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setOpaque(false); sur.add(btn);
        tab.add(sur, BorderLayout.SOUTH);
        return tab;
    }

    private void actualizarRankingTable() {
        if (modeloTablaPosiciones == null) return;
        modeloTablaPosiciones.setRowCount(0);
        int puesto = 1;
        for (Object[] fila : usuarioControlador.obtenerTablaPosiciones()) {
            modeloTablaPosiciones.addRow(new Object[]{puesto++, fila[1], fila[2] + " pts"});
        }
    }

    private void actualizarPuntosYRango() {
        if (lblPuntos == null || lblRango == null) return;
        if (usuarioLogueado.esAdministrador()) {
            lblPuntos.setText("0");
            lblRango.setText("S/R");
            return;
        }
        List<Object[]> posiciones = usuarioControlador.obtenerTablaPosiciones();
        int puntos = 0;
        int puesto = 0;
        boolean encontrado = false;
        for (int i = 0; i < posiciones.size(); i++) {
            Object[] fila = posiciones.get(i);
            int id = (Integer) fila[0];
            if (id == usuarioLogueado.getId()) {
                puntos = (Integer) fila[2];
                puesto = i + 1;
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            lblPuntos.setText(String.valueOf(puntos));
            lblRango.setText("#" + puesto);
        } else {
            Object[] data = usuarioControlador.obtenerPuntosYRankUsuario(usuarioLogueado.getId());
            lblPuntos.setText(String.valueOf(data[0]));
            lblRango.setText(data[1].equals(0) ? "S/R" : "#" + data[1]);
        }
    }

    private static boolean haPasadoFecha(String fechaStr) {
        return false;
    }

    // ── PANEL RESULTADOS ─────────────────────────────────────────────────────
    private JPanel construirPanelResultados() {
        JPanel tab = new JPanel(new BorderLayout(0, 8));
        tab.setBackground(FONDO);
        tab.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        norte.setOpaque(false);
        JLabel lbl = new JLabel("Grupo (Admin):");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(VERDE_OSCURO);
        comboGruposResultados = new JComboBox<>(gruposLetras);
        comboGruposResultados.setBackground(Color.WHITE);
        comboGruposResultados.setForeground(Color.BLACK);
        comboGruposResultados.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        comboGruposResultados.addActionListener(e -> cargarPartidosParaResultados());
        norte.add(lbl); norte.add(comboGruposResultados);
        tab.add(norte, BorderLayout.NORTH);

        panelPartidosResultados = new JPanel();
        panelPartidosResultados.setLayout(new BoxLayout(panelPartidosResultados, BoxLayout.Y_AXIS));
        panelPartidosResultados.setBackground(CARD_BG);
        JScrollPane sp = new JScrollPane(panelPartidosResultados);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        sp.getViewport().setBackground(FONDO);
        tab.add(sp, BorderLayout.CENTER);

        JButton btn = btnRedondeado("Registrar Resultados", new Color(52, 130, 200), Color.WHITE);
        btn.addActionListener(this::guardarResultadosGrupo);
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sur.setOpaque(false); sur.add(btn);
        tab.add(sur, BorderLayout.SOUTH);
        return tab;
    }

    private void cargarPartidosParaResultados() {
        if (panelPartidosResultados == null) return;
        panelPartidosResultados.removeAll();
        listaPanelesResultados = new ArrayList<>();
        String grupo = (String) comboGruposResultados.getSelectedItem();
        for (Partido p : apuestaControlador.obtenerPartidosPorGrupo(grupo)) {
            PartidoResultadoPanel pp = new PartidoResultadoPanel(p);
            panelPartidosResultados.add(pp);
            listaPanelesResultados.add(pp);
        }
        panelPartidosResultados.revalidate();
        panelPartidosResultados.repaint();
    }

    private void guardarResultadosGrupo(ActionEvent e) {
        int ok = JOptionPane.showConfirmDialog(this, "¿Confirmar actualización de marcadores?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        for (PartidoResultadoPanel p : listaPanelesResultados) {
            if (p.isFinalizadoChecked())
                apuestaControlador.registrarResultadoReal(p.getPartidoId(), p.getGolesLocal(), p.getGolesVisitante());
            else
                apuestaControlador.registrarResultadoReal(p.getPartidoId(), null, null);
        }
        JOptionPane.showMessageDialog(this, "Resultados registrados.", "Listo", JOptionPane.INFORMATION_MESSAGE);
        cargarPartidosParaResultados();
        actualizarRankingTable();
    }

    class PartidoResultadoPanel extends JPanel {
        private final int partidoId;
        private final JSpinner spinLocal, spinVisita;
        private final JCheckBox chk;

        PartidoResultadoPanel(Partido part) {
            this.partidoId = part.getId();
            setLayout(new GridLayout(1, 4, 8, 0));
            setBackground(CARD_BG);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 215)),
                new EmptyBorder(10, 14, 10, 14)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

            JLabel lbl = new JLabel(part.getLocal() + " vs " + part.getVisitante());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(VERDE_OSCURO);
            add(lbl);

            int vL = part.getGolesLocal()     != null ? part.getGolesLocal()     : 0;
            int vV = part.getGolesVisitante() != null ? part.getGolesVisitante() : 0;
            spinLocal  = new JSpinner(new SpinnerNumberModel(vL, 0, 20, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(vV, 0, 20, 1));
            configurarSoloNumeros(spinLocal);
            configurarSoloNumeros(spinVisita);
            styleSpinner(spinLocal);
            styleSpinner(spinVisita);
            add(spinPanel("Local", spinLocal));
            add(spinPanel("Visita", spinVisita));

            boolean fechaPasada = haPasadoFecha(part.getFecha());
            chk = new JCheckBox("Finalizado", part.isRegistrado() || fechaPasada);
            chk.setOpaque(false);
            chk.setForeground(VERDE_OSCURO);
            add(chk);

            // Seleccionar "Finalizado" automáticamente al cambiar los goles (flechas o texto)
            javax.swing.event.ChangeListener autoCheck = e -> chk.setSelected(true);
            spinLocal.addChangeListener(autoCheck);
            spinVisita.addChangeListener(autoCheck);

            if (spinLocal.getEditor() instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) spinLocal.getEditor()).getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                });
            }
            if (spinVisita.getEditor() instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) spinVisita.getEditor()).getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { chk.setSelected(true); }
                });
            }
        }

        int getPartidoId()        { return partidoId; }
        
        int getGolesLocal() {
            try {
                spinLocal.commitEdit();
            } catch (java.text.ParseException pe) {
                // Revert to last valid value
            }
            return (int) spinLocal.getValue();
        }
        
        int getGolesVisitante() {
            try {
                spinVisita.commitEdit();
            } catch (java.text.ParseException pe) {
                // Revert to last valid value
            }
            return (int) spinVisita.getValue();
        }
        
        boolean isFinalizadoChecked() { return chk.isSelected(); }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JButton btnRedondeado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel spinPanel(String label, JSpinner spin) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXTO_GRIS);
        p.add(lbl); p.add(spin);
        return p;
    }

    private void configurarSoloNumeros(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField txt = ((JSpinner.DefaultEditor) editor).getTextField();
            
            javax.swing.text.DocumentFilter digitFilter = new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws javax.swing.text.BadLocationException {
                    if (string != null && string.matches("\\d+")) {
                        super.insertString(fb, offset, string, attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    if (text != null && text.matches("\\d*")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            };
            
            txt.addPropertyChangeListener("formatter", evt -> {
                ((javax.swing.text.AbstractDocument) txt.getDocument()).setDocumentFilter(digitFilter);
            });
            
            txt.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyTyped(java.awt.event.KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE && c != java.awt.event.KeyEvent.VK_DELETE) {
                        e.consume();
                    }
                }
            });
            
            ((javax.swing.text.AbstractDocument) txt.getDocument()).setDocumentFilter(digitFilter);
        }
    }

    private void styleTable(JTable table) {
        table.setBackground(CARD_BG);
        table.setForeground(Color.BLACK);
        table.setGridColor(new Color(220, 225, 215));
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(VERDE_BTN);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);
        
        // Header styling
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(230, 235, 225));
        header.setForeground(VERDE_OSCURO);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, VERDE_BTN));
        
        // Centered cells with alternating background renderer
        javax.swing.table.DefaultTableCellRenderer cellRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    c.setBackground(VERDE_BTN);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setForeground(Color.BLACK);
                    if (row % 2 == 0) {
                        c.setBackground(CARD_BG);
                    } else {
                        c.setBackground(new Color(245, 247, 242));
                    }
                }
                return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    private void styleSpinner(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField ft = ((JSpinner.DefaultEditor) editor).getTextField();
            ft.setBackground(Color.WHITE);
            ft.setForeground(Color.BLACK);
            ft.setCaretColor(Color.BLACK);
            ft.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }
    }

    // =========================================================================
    // PESTAÑA: VER APUESTAS (NUEVO PANEL PARA VISUALIZACIÓN DE APUESTAS)
    // =========================================================================
    private JPanel construirPanelApuestas() {
        JPanel panelTab = new JPanel(new BorderLayout(10, 10));
        panelTab.setBackground(FONDO);
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel Norte: Título + Filtros
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);

        String tituloLabel = usuarioLogueado.esAdministrador() ? 
            "Listado General de Pronósticos del Sistema" : 
            "Mis Pronósticos Registrados";
        JLabel lblTitulo = new JLabel(tituloLabel, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(VERDE_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblTitulo);
        panelNorte.add(Box.createVerticalStrut(10));

        // Panel de Filtros para Apuestas
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelFiltros.setOpaque(false);

        if (usuarioLogueado.esAdministrador()) {
            JLabel lblFiltroUsuario = new JLabel("Filtrar por Usuario:");
            lblFiltroUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblFiltroUsuario.setForeground(VERDE_OSCURO);

            comboApuestasUsuario = new JComboBox<>();
            comboApuestasUsuario.setBackground(Color.WHITE);
            comboApuestasUsuario.setForeground(Color.BLACK);

            panelFiltros.add(lblFiltroUsuario);
            panelFiltros.add(comboApuestasUsuario);
        }

        JLabel lblFiltroGrupo = new JLabel("Filtrar por Grupo:");
        lblFiltroGrupo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroGrupo.setForeground(VERDE_OSCURO);

        String[] gruposConTodos = new String[gruposLetras.length + 1];
        gruposConTodos[0] = "Todos";
        System.arraycopy(gruposLetras, 0, gruposConTodos, 1, gruposLetras.length);
        comboApuestasGrupo = new JComboBox<>(gruposConTodos);
        comboApuestasGrupo.setBackground(Color.WHITE);
        comboApuestasGrupo.setForeground(Color.BLACK);

        panelFiltros.add(lblFiltroGrupo);
        panelFiltros.add(comboApuestasGrupo);

        panelNorte.add(panelFiltros);
        panelTab.add(panelNorte, BorderLayout.NORTH);

        // Columnas exactas del ejemplo
        String[] columnas = {"Apostador", "Local", "Visitante", "Goles L", "Goles V"};
        modeloTablaApuestas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaApuestas = new JTable(modeloTablaApuestas);
        styleTable(tablaApuestas);

        // El JScrollPane es vital, si no lo pones, no se verán los encabezados de la tabla
        JScrollPane scrollPane = new JScrollPane(tablaApuestas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        scrollPane.getViewport().setBackground(FONDO);
        panelTab.add(scrollPane, BorderLayout.CENTER);

        JButton btnActualizar = btnRedondeado("Actualizar Pronósticos", VERDE_BTN, Color.WHITE);
        btnActualizar.addActionListener(e -> cargarDatosTablaApuestas());
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(btnActualizar);
        panelTab.add(panelBoton, BorderLayout.SOUTH);

        // Agregar listeners para actualizar automáticamente al cambiar los filtros
        java.awt.event.ActionListener filtroListener = e -> cargarDatosTablaApuestas();
        if (comboApuestasUsuario != null) {
            comboApuestasUsuario.addActionListener(filtroListener);
        }
        comboApuestasGrupo.addActionListener(filtroListener);

        cargarFiltroApuestasUsuarios();
        cargarDatosTablaApuestas();
        
        return panelTab;
    }

    private void cargarFiltroApuestasUsuarios() {
        if (comboApuestasUsuario == null) return;
        
        // Detener listeners temporalmente
        java.awt.event.ActionListener[] listeners = comboApuestasUsuario.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            comboApuestasUsuario.removeActionListener(l);
        }
        
        String seleccionActual = (String) comboApuestasUsuario.getSelectedItem();
        
        comboApuestasUsuario.removeAllItems();
        comboApuestasUsuario.addItem("Todos");
        
        List<modelo.Usuario> usuarios = usuarioControlador.obtenerListaUsuarios();
        for (modelo.Usuario u : usuarios) {
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

    private void cargarDatosTablaApuestas() {
        if (modeloTablaApuestas == null) return;
        
        // 1. Limpiar la tabla antes de cargar para no duplicar datos visualmente
        modeloTablaApuestas.setRowCount(0);

        String usuario = comboApuestasUsuario != null ? (String) comboApuestasUsuario.getSelectedItem() : "Todos";
        String grupo = comboApuestasGrupo != null ? (String) comboApuestasGrupo.getSelectedItem() : "Todos";
        if (usuario == null) usuario = "Todos";
        if (grupo == null) grupo = "Todos";

        // 2. Pedirle al DAO la lista de apuestas (BD -> Java)
        List<Apuesta> historial;
        if (usuarioLogueado.esAdministrador()) {
            historial = apuestaControlador.obtenerTodasLasApuestas(usuario, grupo);
        } else {
            historial = apuestaControlador.obtenerApuestasPorUsuario(usuarioLogueado.getId(), grupo);
        }

        // 3. Recorrer la lista y añadir filas al modelo de la tabla
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

    // =========================================================================
    // PESTAÑA: HISTORIAL DE AUDITORÍA (ADMIN ONLY)
    // =========================================================================
    private JPanel construirPanelHistorial() {
        JPanel panelTab = new JPanel(new BorderLayout(10, 10));
        panelTab.setBackground(FONDO);
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel Norte: Título + Filtros
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);

        JLabel lblHistorial = new JLabel("Historial General de Pronósticos Guardados", SwingConstants.CENTER);
        lblHistorial.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHistorial.setForeground(VERDE_OSCURO);
        lblHistorial.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelNorte.add(lblHistorial);
        panelNorte.add(Box.createVerticalStrut(10));

        // Panel de Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panelFiltros.setOpaque(false);

        JLabel lblFiltroUsuario = new JLabel("Filtrar por Usuario:");
        lblFiltroUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroUsuario.setForeground(VERDE_OSCURO);

        comboFiltroUsuario = new JComboBox<>();
        comboFiltroUsuario.setBackground(Color.WHITE);
        comboFiltroUsuario.setForeground(Color.BLACK);

        JLabel lblFiltroGrupo = new JLabel("Filtrar por Grupo:");
        lblFiltroGrupo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroGrupo.setForeground(VERDE_OSCURO);

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
        panelTab.add(panelNorte, BorderLayout.NORTH);

        String[] columnas = {"ID Log", "Apostador / Jugador", "Partido", "Predicción", "Fecha de Registro", "Acción"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTablaHistorial);
        styleTable(tablaHistorial);

        JScrollPane scrollTable = new JScrollPane(tablaHistorial);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70)));
        scrollTable.getViewport().setBackground(FONDO);
        panelTab.add(scrollTable, BorderLayout.CENTER);

        JButton btnActualizar = btnRedondeado("Actualizar Historial", VERDE_BTN, Color.WHITE);
        btnActualizar.addActionListener(e -> actualizarHistorialTable());
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setOpaque(false);
        panelBoton.add(btnActualizar);
        panelTab.add(panelBoton, BorderLayout.SOUTH);

        // Cargar usuarios inicialmente
        cargarFiltroUsuarios();

        // Agregar listeners para actualizar automáticamente al cambiar los filtros
        java.awt.event.ActionListener filtroListener = e -> actualizarHistorialTable();
        comboFiltroUsuario.addActionListener(filtroListener);
        comboFiltroGrupo.addActionListener(filtroListener);

        actualizarHistorialTable();
        return panelTab;
    }

    private void cargarFiltroUsuarios() {
        if (comboFiltroUsuario == null) return;
        
        // Detener listeners temporalmente para evitar recursión al limpiar/agregar
        java.awt.event.ActionListener[] listeners = comboFiltroUsuario.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            comboFiltroUsuario.removeActionListener(l);
        }
        
        String seleccionActual = (String) comboFiltroUsuario.getSelectedItem();
        
        comboFiltroUsuario.removeAllItems();
        comboFiltroUsuario.addItem("Todos");
        
        List<modelo.Usuario> usuarios = usuarioControlador.obtenerListaUsuarios();
        for (modelo.Usuario u : usuarios) {
            if (!u.esAdministrador()) {
                comboFiltroUsuario.addItem(u.getNombre());
            }
        }
        
        if (seleccionActual != null) {
            comboFiltroUsuario.setSelectedItem(seleccionActual);
        } else {
            comboFiltroUsuario.setSelectedIndex(0);
        }
        
        // Reactivar los listeners
        for (java.awt.event.ActionListener l : listeners) {
            comboFiltroUsuario.addActionListener(l);
        }
    }

    private void actualizarHistorialTable() {
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
