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

    private static final Color VERDE_OSCURO  = new Color(20, 60, 30);
    private static final Color VERDE_BTN     = new Color(22, 68, 35);
    private static final Color AMARILLO      = new Color(230, 190, 40);
    private static final Color FONDO         = new Color(242, 244, 240);
    private static final Color CARD_BG       = Color.WHITE;
    private static final Color TEXTO_GRIS    = new Color(110, 110, 110);
    private static final Color ROJO_LIVE     = new Color(210, 50, 50);
    private static final Color VERDE_OK      = new Color(39, 174, 96);
    private static final Color AMARILLO_WARN = new Color(200, 150, 10);
    private static final Color AMARILLO_SUAVE= new Color(255, 248, 210);

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
    private JTextField txtBuscarEquipo;

    private final String[] gruposLetras = {"A","B","C","D","E","F","G","H","I","J","K","L"};

    // Panel de contenido central (cambia según tab activo)
    private JPanel panelContenido;
    // Navbar botones
    private JButton[] navBtns;

    public MenuPrincipal(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.usuarioControlador = new UsuarioControlador();
        this.apuestaControlador = new ApuestaControlador();

        setTitle("WC 2026");
        setSize(460, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        setContentPane(raiz);

        raiz.add(construirHeader(), BorderLayout.NORTH);

        panelContenido = new JPanel(new CardLayout());
        panelContenido.setBackground(FONDO);
        panelContenido.add(construirPanelHome(),         "home");
        panelContenido.add(construirPanelPronosticos(),  "pronosticos");
        panelContenido.add(construirPanelPosiciones(),   "posiciones");
        panelContenido.add(construirPanelResultados(),   "resultados");
        raiz.add(panelContenido, BorderLayout.CENTER);

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
        
        // Solo el administrador puede registrar resultados reales y ver historial
        if (usuarioLogueado.esAdministrador()) {
            configurarPestanaResultados();
            configurarPestanaHistorial();
        }

        configurarPestanaPosiciones();
        configurarPestanaConsultas();

        panelRaiz.add(tabbedPane, BorderLayout.CENTER);
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
        JLabel lblLogo = new JLabel("⚽");
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
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(avatar);
        header.add(der, BorderLayout.EAST);
        return header;
    }

    // ── PANEL HOME ────────────────────────────────────────────────────────────
    private JPanel construirPanelHome() {
        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBackground(FONDO);
        scroll.setBorder(new EmptyBorder(0, 18, 18, 18));

        // Saludo
        JLabel lblHola = new JLabel("Hola, " + usuarioLogueado.getNombre() + "!");
        lblHola.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHola.setForeground(VERDE_OSCURO);
        lblHola.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblSub = new JLabel("Listo para la jornada de hoy en el Mundial?");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXTO_GRIS);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.add(lblHola);
        scroll.add(Box.createVerticalStrut(4));
        scroll.add(lblSub);
        scroll.add(Box.createVerticalStrut(14));

        // Badge PRO MEMBER
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMARILLO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblBadge = new JLabel("⭐  PRO MEMBER");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBadge.setForeground(VERDE_OSCURO);
        badge.add(lblBadge);
        scroll.add(badge);
        scroll.add(Box.createVerticalStrut(16));

        // Cards puntos + rango
        JPanel rowStats = new JPanel(new GridLayout(1, 2, 12, 0));
        rowStats.setOpaque(false);
        rowStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        rowStats.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Card puntos
        JPanel cardPuntos = card();
        cardPuntos.setLayout(new BoxLayout(cardPuntos, BoxLayout.Y_AXIS));
        cardPuntos.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel lblPLabel = new JLabel("TUS PUNTOS  🎯");
        lblPLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblPLabel.setForeground(TEXTO_GRIS);
        JLabel lblPuntos = new JLabel("1,250");
        lblPuntos.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblPuntos.setForeground(VERDE_OSCURO);
        JLabel lblPChange = new JLabel("+12% vs ayer");
        lblPChange.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPChange.setForeground(VERDE_OK);
        cardPuntos.add(lblPLabel); cardPuntos.add(Box.createVerticalStrut(4));
        cardPuntos.add(lblPuntos); cardPuntos.add(lblPChange);

        // Card rango
        JPanel cardRango = card();
        cardRango.setLayout(new BoxLayout(cardRango, BoxLayout.Y_AXIS));
        cardRango.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel lblRLabel = new JLabel("RANGO ACTUAL  🏆");
        lblRLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRLabel.setForeground(TEXTO_GRIS);
        JLabel lblRango = new JLabel("#42");
        lblRango.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblRango.setForeground(VERDE_OSCURO);
        JLabel lblRSub = new JLabel("Top 5% Global");
        lblRSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRSub.setForeground(TEXTO_GRIS);
        cardRango.add(lblRLabel); cardRango.add(Box.createVerticalStrut(4));
        cardRango.add(lblRango); cardRango.add(lblRSub);

        rowStats.add(cardPuntos); rowStats.add(cardRango);
        scroll.add(rowStats);
        scroll.add(Box.createVerticalStrut(14));

        // Card LIVE NOW
        JPanel cardLive = card();
        cardLive.setLayout(new BorderLayout(10, 6));
        cardLive.setBorder(new EmptyBorder(14, 16, 14, 16));
        cardLive.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        cardLive.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel liveTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        liveTop.setOpaque(false);
        JLabel dotLive = new JLabel("● LIVE NOW");
        dotLive.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dotLive.setForeground(ROJO_LIVE);
        liveTop.add(dotLive);
        cardLive.add(liveTop, BorderLayout.NORTH);

        JLabel lblLiveMatch = new JLabel("BRA v GER");
        lblLiveMatch.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLiveMatch.setForeground(VERDE_OSCURO);
        cardLive.add(lblLiveMatch, BorderLayout.CENTER);

        JButton btnVerPartido = new JButton("Ver partido  >");
        btnVerPartido.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerPartido.setForeground(VERDE_OSCURO);
        btnVerPartido.setBackground(FONDO);
        btnVerPartido.setBorderPainted(false);
        btnVerPartido.setFocusPainted(false);
        btnVerPartido.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerPartido.addActionListener(e -> mostrarTab("pronosticos", 1));
        cardLive.add(btnVerPartido, BorderLayout.SOUTH);
        scroll.add(cardLive);
        scroll.add(Box.createVerticalStrut(14));

        // Banner predicciones finales
        JPanel banner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, VERDE_OSCURO, getWidth(), getHeight(), new Color(30, 90, 45)));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(20, 24, 20, 24));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTrophy = new JLabel("🏆", SwingConstants.CENTER);
        lblTrophy.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblTrophy.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBannerTitle = new JLabel("Predicciones Finales", SwingConstants.CENTER);
        lblBannerTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBannerTitle.setForeground(Color.WHITE);
        lblBannerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBannerSub = new JLabel("<html><center>Gana el triple de puntos acertando al<br>campeón del grupo D.</center></html>", SwingConstants.CENTER);
        lblBannerSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBannerSub.setForeground(new Color(180, 210, 180));
        lblBannerSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnApostar = new JButton("APOSTAR AHORA") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AMARILLO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnApostar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApostar.setForeground(VERDE_OSCURO);
        btnApostar.setOpaque(false); btnApostar.setContentAreaFilled(false);
        btnApostar.setBorderPainted(false); btnApostar.setFocusPainted(false);
        btnApostar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnApostar.setMaximumSize(new Dimension(180, 38));
        btnApostar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnApostar.addActionListener(e -> mostrarTab("pronosticos", 1));

        banner.add(lblTrophy); banner.add(Box.createVerticalStrut(6));
        banner.add(lblBannerTitle); banner.add(Box.createVerticalStrut(6));
        banner.add(lblBannerSub); banner.add(Box.createVerticalStrut(12));
        banner.add(btnApostar);
        scroll.add(banner);
        scroll.add(Box.createVerticalStrut(18));

        // Acciones Rápidas
        JLabel lblAcciones = new JLabel("Acciones Rápidas");
        lblAcciones.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblAcciones.setForeground(VERDE_OSCURO);
        lblAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.add(lblAcciones);
        scroll.add(Box.createVerticalStrut(10));

        JPanel gridAcciones = new JPanel(new GridLayout(2, 2, 12, 12));
        gridAcciones.setOpaque(false);
        gridAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        gridAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        gridAcciones.add(accionCard("👥", "Ver Grupos",         false, e -> mostrarTab("pronosticos", 1)));
        gridAcciones.add(accionCard("📊", "Consultar\nPosiciones", false, e -> mostrarTab("posiciones", 2)));
        gridAcciones.add(accionCard("🎯", "Mis Apuestas",       false, e -> mostrarTab("pronosticos", 1)));
        gridAcciones.add(accionCard("⚙️",  "Gestionar\nPartidos", true,  e -> mostrarTab("resultados", 3)));

        scroll.add(gridAcciones);
        scroll.add(Box.createVerticalStrut(18));

        // Partido Destacado
        scroll.add(construirPartidoDestacado());
        scroll.add(Box.createVerticalStrut(10));

        JScrollPane sp = new JScrollPane(scroll);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        sp.setBackground(FONDO);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(FONDO);
        wrapper.add(sp);
        return wrapper;
    }

    private JPanel construirPartidoDestacado() {
        JPanel cardOuter = card();
        cardOuter.setLayout(new BoxLayout(cardOuter, BoxLayout.Y_AXIS));
        cardOuter.setBorder(new EmptyBorder(16, 18, 16, 18));
        cardOuter.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header partido destacado
        JPanel rowTop = new JPanel(new BorderLayout());
        rowTop.setOpaque(false);
        JLabel lblPD = new JLabel("PARTIDO DESTACADO");
        lblPD.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPD.setForeground(TEXTO_GRIS);
        JLabel lblLive = new JLabel("● LIVE");
        lblLive.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLive.setForeground(ROJO_LIVE);
        rowTop.add(lblPD, BorderLayout.WEST);
        rowTop.add(lblLive, BorderLayout.EAST);
        rowTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        cardOuter.add(rowTop);
        cardOuter.add(Box.createVerticalStrut(12));

        // Equipos y marcador
        JPanel rowMarcador = new JPanel(new GridLayout(1, 3, 0, 0));
        rowMarcador.setOpaque(false);
        rowMarcador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Local
        JPanel panelLocal = new JPanel();
        panelLocal.setLayout(new BoxLayout(panelLocal, BoxLayout.Y_AXIS));
        panelLocal.setOpaque(false);
        JLabel lblFlagArg = new JLabel("🇦🇷", SwingConstants.CENTER);
        lblFlagArg.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblFlagArg.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblArg = new JLabel("ARG", SwingConstants.CENTER);
        lblArg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblArg.setForeground(VERDE_OSCURO);
        lblArg.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLocal.add(lblFlagArg); panelLocal.add(lblArg);

        // Marcador central
        JPanel panelScore = new JPanel();
        panelScore.setLayout(new BoxLayout(panelScore, BoxLayout.Y_AXIS));
        panelScore.setOpaque(false);
        JLabel lblScore = new JLabel("2 - 1", SwingConstants.CENTER);
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblScore.setForeground(VERDE_OSCURO);
        lblScore.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblMin = new JLabel("72' Minutos", SwingConstants.CENTER);
        lblMin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMin.setForeground(TEXTO_GRIS);
        lblMin.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelScore.add(Box.createVerticalGlue());
        panelScore.add(lblScore); panelScore.add(lblMin);
        panelScore.add(Box.createVerticalGlue());

        // Visitante
        JPanel panelVisita = new JPanel();
        panelVisita.setLayout(new BoxLayout(panelVisita, BoxLayout.Y_AXIS));
        panelVisita.setOpaque(false);
        JLabel lblFlagFra = new JLabel("🇫🇷", SwingConstants.CENTER);
        lblFlagFra.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblFlagFra.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblFra = new JLabel("FRA", SwingConstants.CENTER);
        lblFra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFra.setForeground(VERDE_OSCURO);
        lblFra.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelVisita.add(lblFlagFra); panelVisita.add(lblFra);

        rowMarcador.add(panelLocal); rowMarcador.add(panelScore); rowMarcador.add(panelVisita);
        cardOuter.add(rowMarcador);
        cardOuter.add(Box.createVerticalStrut(14));

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 225, 215));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        cardOuter.add(sep);
        cardOuter.add(Box.createVerticalStrut(10));

        // Cuotas
        JPanel rowCuotas = new JPanel(new GridLayout(1, 3, 0, 0));
        rowCuotas.setOpaque(false);
        rowCuotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        rowCuotas.add(cuotaPanel("Local", "1.85"));
        rowCuotas.add(cuotaPanel("Empate", "3.40"));
        rowCuotas.add(cuotaPanel("Visita", "4.20"));
        cardOuter.add(rowCuotas);

        return cardOuter;
    }

    private JPanel cuotaPanel(String label, String valor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXTO_GRIS);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel val = new JLabel(valor, SwingConstants.CENTER);
        val.setFont(new Font("Segoe UI", Font.BOLD, 15));
        val.setForeground(VERDE_OSCURO);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(lbl); p.add(val);
        return p;
    }

    private JPanel accionCard(String icono, String texto, boolean gris, java.awt.event.ActionListener action) {
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
        p.setBorder(new EmptyBorder(16, 10, 16, 10));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { action.actionPerformed(null); }
        });

        JLabel lblIco = new JLabel(icono, SwingConstants.CENTER);
        lblIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        lblIco.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Manejo de texto con salto de línea
        String htmlText = "<html><center>" + texto.replace("\n", "<br>") + "</center></html>";
        JLabel lblTxt = new JLabel(htmlText, SwingConstants.CENTER);
        lblTxt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTxt.setForeground(gris ? new Color(80,80,80) : VERDE_OSCURO);
        lblTxt.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(lblIco); p.add(Box.createVerticalStrut(6)); p.add(lblTxt);
        return p;
    }

    // ── NAVBAR ────────────────────────────────────────────────────────────────
    private JPanel construirNavbar() {
        JPanel nav = new JPanel(new GridLayout(1, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(VERDE_OSCURO);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(0, 64));

        String[][] tabs = {{"🏠","Home"},{"⚽","Matches"},{"🎯","Bets"},{"👤","Profile"}};
        navBtns = new JButton[4];
        String[] keys = {"home","pronosticos","posiciones","resultados"};

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            final String key = keys[i];
            JButton btn = new JButton("<html><center>" + tabs[i][0] + "<br><span style='font-size:9px'>" + tabs[i][1] + "</span></center></html>") {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(idx == 0 ? new Color(35, 85, 50) : VERDE_OSCURO);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btn.setForeground(i == 0 ? AMARILLO : new Color(160, 190, 160));
            btn.setOpaque(false); btn.setContentAreaFilled(false);
            btn.setBorderPainted(false); btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> mostrarTab(key, idx));
            navBtns[i] = btn;
            nav.add(btn);
        }
        return nav;
    }

    private void mostrarTab(String key, int idx) {
        ((CardLayout) panelContenido.getLayout()).show(panelContenido, key);
        if (navBtns != null) {
            for (int i = 0; i < navBtns.length; i++) {
                navBtns[i].setForeground(i == idx ? AMARILLO : new Color(160, 190, 160));
            }
        }
        if ("posiciones".equals(key)) actualizarRankingTable();
        if ("pronosticos".equals(key) && comboGruposPronosticos != null) cargarPartidosParaApuestas();
        if ("resultados".equals(key) && comboGruposResultados != null) cargarPartidosParaResultados();
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
        comboGruposPronosticos.addActionListener(e -> cargarPartidosParaApuestas());
        norte.add(lbl); norte.add(comboGruposPronosticos);
        tab.add(norte, BorderLayout.NORTH);

        panelPartidosApuestas = new JPanel();
        panelPartidosApuestas.setLayout(new BoxLayout(panelPartidosApuestas, BoxLayout.Y_AXIS));
        panelPartidosApuestas.setBackground(CARD_BG);
        JScrollPane sp = new JScrollPane(panelPartidosApuestas);
        sp.setBorder(null);
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
            if (!pPanel.tieneApuestaPrevia()) {
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
        } else {
            JOptionPane.showMessageDialog(this, "No hay nuevos pronósticos para guardar en este grupo.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class PartidoApuestaPanel extends JPanel {
        private int partidoId;
        private JSpinner spinLocal;
        private JSpinner spinVisita;
        private boolean tieneApuestaPrevia;

        PartidoApuestaPanel(Partido part, Apuesta prev) {
            this.partidoId = part.getId();
            this.tieneApuestaPrevia = (apuestaPrevia != null);
            setLayout(new GridLayout(1, 5, 10, 0));
            setBackground(new Color(40, 40, 48));
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
            add(spinPanel("Local", spinLocal));
            add(spinPanel("Visita", spinVisita));

            spinLocal = new JSpinner(new SpinnerNumberModel(valLocal, 0, 20, 1));
            spinVisita = new JSpinner(new SpinnerNumberModel(valVisita, 0, 20, 1));

            if (tieneApuestaPrevia) {
                spinLocal.setEnabled(false);
                spinVisita.setEnabled(false);
            }

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
        public boolean tieneApuestaPrevia() { return tieneApuestaPrevia; }
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
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tab.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btn = btnRedondeado("🔄  Actualizar", VERDE_BTN, Color.WHITE);
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
            modeloTablaPosiciones.addRow(new Object[]{puesto++, fila[0], fila[1] + " pts"});
        }
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
        comboGruposResultados.addActionListener(e -> cargarPartidosParaResultados());
        norte.add(lbl); norte.add(comboGruposResultados);
        tab.add(norte, BorderLayout.NORTH);

        panelPartidosResultados = new JPanel();
        panelPartidosResultados.setLayout(new BoxLayout(panelPartidosResultados, BoxLayout.Y_AXIS));
        panelPartidosResultados.setBackground(CARD_BG);
        JScrollPane sp = new JScrollPane(panelPartidosResultados);
        sp.setBorder(null);
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
            add(spinPanel("Local", spinLocal));
            add(spinPanel("Visita", spinVisita));

            chk = new JCheckBox("Finalizado", part.isRegistrado());
            chk.setOpaque(false);
            chk.setForeground(VERDE_OSCURO);
            add(chk);
        }

        int getPartidoId()        { return partidoId; }
        int getGolesLocal()       { return (int) spinLocal.getValue(); }
        int getGolesVisitante()   { return (int) spinVisita.getValue(); }
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

    // =========================================================================
    // PESTAÑA 5: HISTORIAL DE APUESTAS (ADMIN ONLY)
    // =========================================================================
    private void configurarPestanaHistorial() {
        JPanel panelTab = new JPanel(new BorderLayout(10, 10));
        panelTab.setBackground(new Color(25, 25, 30));
        panelTab.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblHistorial = new JLabel("Historial General de Pronósticos Guardados", SwingConstants.CENTER);
        lblHistorial.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHistorial.setForeground(Color.WHITE);
        panelTab.add(lblHistorial, BorderLayout.NORTH);

        String[] columnas = {"ID Log", "Apostador / Jugador", "Partido", "Predicción", "Fecha de Registro", "Acción"};
        modeloTablaHistorial = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaHistorial = new JTable(modeloTablaHistorial);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaHistorial.setRowHeight(25);
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollTable = new JScrollPane(tablaHistorial);
        panelTab.add(scrollTable, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("🔄 Actualizar Historial");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.addActionListener(e -> actualizarHistorialTable());
        panelTab.add(btnActualizar, BorderLayout.SOUTH);

        tabbedPane.addTab("📋 Historial de Apuestas (Admin)", panelTab);
        actualizarHistorialTable();
    }

    private void actualizarHistorialTable() {
        modeloTablaHistorial.setRowCount(0);
        List<Object[]> logs = apuestaControlador.obtenerHistorialApuestas();
        for (Object[] fila : logs) {
            modeloTablaHistorial.addRow(fila);
        }
    }
}
