package vista;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class Login extends JFrame {

    // Colores del diseño
    private static final Color VERDE_OSCURO   = new Color(20, 60, 30);
    private static final Color VERDE_BTN      = new Color(22, 68, 35);
    private static final Color AMARILLO       = new Color(230, 190, 40);
    private static final Color FONDO_CLARO    = new Color(245, 245, 240);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color TEXTO_GRIS     = new Color(120, 120, 120);
    private static final Color AMARILLO_SUAVE = new Color(255, 248, 220);

    private JTextField txtNombre;
    private UsuarioControlador usuarioControlador;

    public Login() {
        usuarioControlador = new UsuarioControlador();

        setTitle("WC 2026 - Quiniela");
        setSize(420, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        // Panel raíz con fondo degradado
        JPanel panelRaiz = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                // Fondo superior verde oscuro (estadio)
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 80, 40), 0, getHeight() / 2, FONDO_CLARO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight() / 2);
                // Fondo inferior gris claro
                g2.setColor(FONDO_CLARO);
                g2.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);
            }
        };
        setContentPane(panelRaiz);

        // ── HEADER (logo + título) ──
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);
        panelHeader.setBorder(new EmptyBorder(40, 0, 20, 0));

        // Ícono balón
        JLabel lblIcono = new JLabel("⚽", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(VERDE_OSCURO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblIcono.setForeground(AMARILLO);
        lblIcono.setOpaque(false);
        lblIcono.setPreferredSize(new Dimension(80, 80));
        lblIcono.setMaximumSize(new Dimension(80, 80));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título WC 2026
        JLabel lblTitulo = new JLabel("WC 2026", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 38));
        lblTitulo.setForeground(VERDE_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Línea amarilla decorativa
        JPanel lineaAmarilla = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(AMARILLO);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        lineaAmarilla.setOpaque(false);
        lineaAmarilla.setPreferredSize(new Dimension(60, 4));
        lineaAmarilla.setMaximumSize(new Dimension(60, 4));
        lineaAmarilla.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelHeader.add(lblIcono);
        panelHeader.add(Box.createVerticalStrut(12));
        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createVerticalStrut(8));
        panelHeader.add(lineaAmarilla);

        panelRaiz.add(panelHeader, BorderLayout.NORTH);

        // ── CENTRO: tarjeta blanca + features + footer ──
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setOpaque(false);
        panelCentro.setBorder(new EmptyBorder(10, 24, 10, 24));

        // Tarjeta blanca
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(28, 28, 24, 28));
        card.setMaximumSize(new Dimension(400, 400));

        // Bienvenido
        JLabel lblBienvenido = new JLabel("Bienvenido al Mundial 2026", SwingConstants.CENTER);
        lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblBienvenido.setForeground(VERDE_OSCURO);
        lblBienvenido.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("<html><center>¿Listo para apostar? Ingresa tu nombre<br>para comenzar.</center></html>", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TEXTO_GRIS);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblBienvenido);
        card.add(Box.createVerticalStrut(8));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(22));

        // Label campo
        JLabel lblNombreLabel = new JLabel("Nombre Completo");
        lblNombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombreLabel.setForeground(VERDE_OSCURO);
        lblNombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblNombreLabel);
        card.add(Box.createVerticalStrut(6));

        // Campo texto con placeholder y ícono
        txtNombre = new JTextField("Tu nombre completo") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 248, 248));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setForeground(TEXTO_GRIS);
        txtNombre.setOpaque(false);
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNombre.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (txtNombre.getText().equals("Tu nombre completo")) {
                    txtNombre.setText("");
                    txtNombre.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (txtNombre.getText().isEmpty()) {
                    txtNombre.setText("Tu nombre completo");
                    txtNombre.setForeground(TEXTO_GRIS);
                }
            }
        });
        card.add(txtNombre);
        card.add(Box.createVerticalStrut(20));

        // Botón Registrar e Iniciar
        JButton btnRegistrar = new JButton("Registrar e Iniciar  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? VERDE_OSCURO.darker() : VERDE_BTN);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setOpaque(false);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(this::accionRegistrar);
        card.add(btnRegistrar);
        card.add(Box.createVerticalStrut(16));

        // Texto seguro
        JLabel lblSeguro = new JLabel("⊕  Acceso seguro y oficial Pitch Precision", SwingConstants.CENTER);
        lblSeguro.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSeguro.setForeground(TEXTO_GRIS);
        lblSeguro.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSeguro);

        panelCentro.add(card);
        panelCentro.add(Box.createVerticalStrut(20));

        // ── FEATURE CARDS ──
        JPanel panelFeatures = new JPanel(new GridLayout(1, 2, 12, 0));
        panelFeatures.setOpaque(false);
        panelFeatures.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        panelFeatures.add(crearFeatureCard("⚡", "Pagos Rápidos", false));
        panelFeatures.add(crearFeatureCard("🏆", "Mejores Cuotas", true));

        panelCentro.add(panelFeatures);
        panelCentro.add(Box.createVerticalStrut(20));

        // ── FOOTER ──
        JPanel panelFooter = new JPanel();
        panelFooter.setOpaque(false);
        panelFooter.setLayout(new BoxLayout(panelFooter, BoxLayout.Y_AXIS));

        JLabel lblIconosFooter = new JLabel("🛡  📷  👤", SwingConstants.CENTER);
        lblIconosFooter.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblIconosFooter.setForeground(TEXTO_GRIS);
        lblIconosFooter.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCopyright = new JLabel("© 2026 FIFA World Cup™ Betting Partner", SwingConstants.CENTER);
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(TEXTO_GRIS);
        lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelFooter.add(lblIconosFooter);
        panelFooter.add(Box.createVerticalStrut(4));
        panelFooter.add(lblCopyright);

        panelCentro.add(panelFooter);

        panelRaiz.add(panelCentro, BorderLayout.CENTER);
    }

    private JPanel crearFeatureCard(String icono, String texto, boolean destacado) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(destacado ? AMARILLO_SUAVE : CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 12, 16, 12));

        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcono.setForeground(destacado ? AMARILLO.darker() : VERDE_OSCURO);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTexto = new JLabel(texto, SwingConstants.CENTER);
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTexto.setForeground(destacado ? new Color(160, 120, 0) : VERDE_OSCURO);
        lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(lblIcono);
        p.add(Box.createVerticalStrut(6));
        p.add(lblTexto);
        return p;
    }

    private void accionRegistrar(ActionEvent e) {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty() || nombre.equals("Tu nombre completo")) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa tu nombre.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario usuario = usuarioControlador.ingresarORegistrarUsuario(nombre);
        if (usuario != null) {
            abrirMenuPrincipal(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Intenta de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal(usuario);
            menu.setVisible(true);
            this.dispose();
        });
    }
}
