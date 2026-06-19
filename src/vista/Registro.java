package vista;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

public class Registro extends JDialog {

    private static final Color SIDEBAR_BG  = new Color(20, 60, 30);
    private static final Color VERDE_OSCURO= new Color(20, 60, 30);
    private static final Color AMARILLO    = new Color(230, 190, 40);
    private static final Color FONDO       = new Color(242, 244, 240);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color TEXTO_GRIS  = new Color(110, 110, 110);
    private static final Color VERDE_BTN   = new Color(22, 68, 35);

    private JTextField txtCedula;
    private JTextField txtNombre;
    private JPasswordField txtPassword;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private UsuarioControlador usuarioControlador;
    private Login loginFrame;

    public Registro(Login parent) {
        super(parent, "Mundial 2026 - Registro de Apostador", true);
        this.loginFrame = parent;
        this.usuarioControlador = new UsuarioControlador();

        setSize(820, 520);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(FONDO);
        setContentPane(raiz);

        raiz.add(construirSidebar(), BorderLayout.WEST);
        raiz.add(construirPanelForm(), BorderLayout.CENTER);
    }

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setOpaque(false);
        sidebar.setBorder(new EmptyBorder(60, 30, 40, 30));

        JLabel lblLogo = new JLabel("WC 2026");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(AMARILLO);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Crea tu cuenta");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(new Color(170, 200, 170));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(lblSub);
        sidebar.add(Box.createVerticalStrut(40));

        JPanel linea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(AMARILLO); g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        linea.setOpaque(false);
        linea.setMaximumSize(new Dimension(50, 3));
        linea.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(linea);
        sidebar.add(Box.createVerticalStrut(30));

        String[] features = {"Acceso a todos los partidos", "Compite en el ranking global", "Gana con tus predicciones"};
        for (String f : features) {
            JLabel lf = new JLabel("\u2022  " + f);
            lf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lf.setForeground(new Color(170, 200, 170));
            lf.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(lf);
            sidebar.add(Box.createVerticalStrut(10));
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel lblFooter = new JLabel("\u00a9 2026 FIFA World Cup\u2122");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(120, 160, 120));
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(lblFooter);

        return sidebar;
    }

    private JPanel construirPanelForm() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(FONDO);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(32, 36, 32, 36));
        card.setPreferredSize(new Dimension(360, 420));

        JLabel lblTitulo = new JLabel("Registro de Apostador");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(VERDE_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Completa los campos para crear tu cuenta.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXTO_GRIS);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(22));

        card.add(campoLabel("C\u00e9dula / ID"));
        card.add(Box.createVerticalStrut(5));
        txtCedula = new JTextField();
        estiloCampo(txtCedula);
        card.add(txtCedula);
        card.add(Box.createVerticalStrut(12));

        card.add(campoLabel("Nombre Completo"));
        card.add(Box.createVerticalStrut(5));
        txtNombre = new JTextField();
        estiloCampo(txtNombre);
        card.add(txtNombre);
        card.add(Box.createVerticalStrut(12));

        card.add(campoLabel("Contrase\u00f1a"));
        card.add(Box.createVerticalStrut(5));
        txtPassword = new JPasswordField();
        estiloCampo(txtPassword);
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(24));

        JPanel rowBtns = new JPanel(new GridLayout(1, 2, 12, 0));
        rowBtns.setOpaque(false);
        rowBtns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        rowBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRegistrar = btnRedondeado("Registrar", VERDE_BTN, Color.WHITE);
        btnRegistrar.addActionListener(this::accionRegistrar);

        btnCancelar = btnRedondeado("Cancelar", new Color(52, 130, 200), Color.WHITE);
        btnCancelar.addActionListener(e -> dispose());

        rowBtns.add(btnRegistrar);
        rowBtns.add(btnCancelar);
        card.add(rowBtns);

        outer.add(card);
        return outer;
    }

    private JLabel campoLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(VERDE_OSCURO);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void estiloCampo(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBackground(new Color(248, 249, 246));
        f.setForeground(Color.BLACK);
        f.setCaretColor(VERDE_OSCURO);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 200), 1, true),
            new EmptyBorder(9, 12, 9, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton btnRedondeado(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void accionRegistrar(ActionEvent e) {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (cedula.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error de Validaci\u00f3n", JOptionPane.ERROR_MESSAGE); return;
        }
        if (!cedula.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "La c\u00e9dula debe contener \u00fanicamente n\u00fameros.", "Error de Validaci\u00f3n", JOptionPane.ERROR_MESSAGE); return;
        }
        if (nombre.length() < 3) {
            JOptionPane.showMessageDialog(this, "El nombre completo debe tener al menos 3 caracteres.", "Error de Validaci\u00f3n", JOptionPane.ERROR_MESSAGE); return;
        }
        if (!nombre.matches("[a-zA-Z\u00e1\u00e9\u00ed\u00f3\u00fa\u00c1\u00c9\u00cd\u00d3\u00da\u00f1\u00d1\\s]+")) {
            JOptionPane.showMessageDialog(this, "El nombre debe contener \u00fanicamente letras y espacios.", "Error de Validaci\u00f3n", JOptionPane.ERROR_MESSAGE); return;
        }
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contrase\u00f1a debe tener al menos 4 caracteres.", "Error de Validaci\u00f3n", JOptionPane.ERROR_MESSAGE); return;
        }
        if (usuarioControlador.existeCedula(cedula)) {
            JOptionPane.showMessageDialog(this, "Esta c\u00e9dula ya se encuentra registrada.", "C\u00e9dula Duplicada", JOptionPane.ERROR_MESSAGE); return;
        }
        if (usuarioControlador.existeNombre(nombre)) {
            JOptionPane.showMessageDialog(this, "El nombre '" + nombre + "' ya est\u00e1 registrado.", "Nombre Duplicado", JOptionPane.ERROR_MESSAGE); return;
        }

        try {
            Usuario usuario = usuarioControlador.registrarUsuario(nombre, cedula, password);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "\u00a1Usuario registrado con \u00e9xito!", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.abrirMenuPrincipal(usuario);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
