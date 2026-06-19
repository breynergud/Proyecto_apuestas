package vista;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

    private JTextField txtCedula;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnRegistrar;
    private UsuarioControlador usuarioControlador;

    public Login() {
        usuarioControlador = new UsuarioControlador();

        // Configuración básica de ventana
        setTitle("Mundial 2026 - Quiniela");
        setSize(400, 310); // Ventana adaptada para el nuevo campo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        // Panel Principal con fondo oscuro moderno
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(30, 30, 36));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panelPrincipal);

        // Header / Título
        JLabel lblTitulo = new JLabel("POOLS MUNDIAL 2026", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(46, 204, 113)); // Verde brillante fútbol
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel Central (Formulario)
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 6, 6));
        panelForm.setOpaque(false);

        // Entrada de Cédula
        JLabel lblCedula = new JLabel("Cédula / ID:");
        lblCedula.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCedula.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblCedula);

        txtCedula = new JTextField();
        txtCedula.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCedula.setBackground(new Color(45, 45, 52));
        txtCedula.setForeground(Color.WHITE);
        txtCedula.setCaretColor(Color.WHITE);
        txtCedula.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        panelForm.add(txtCedula);

        // Entrada de Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBackground(new Color(45, 45, 52));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        panelForm.add(txtPassword);

        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        // Panel Inferior (Botones)
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(46, 204, 113));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.addActionListener(this::accionIngresar);
        panelBotones.add(btnIngresar);

        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(new Color(52, 152, 219)); // Azul brillante
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(this::accionRegistrar);
        panelBotones.add(btnRegistrar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void accionIngresar(ActionEvent e) {
        String cedula = txtCedula.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        if (cedula.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa tu cédula y tu contraseña.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!cedula.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "La cédula debe contener únicamente números.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuarioLogueado = usuarioControlador.iniciarSesion(cedula, password);
        if (usuarioLogueado != null) {
            abrirMenuPrincipal(usuarioLogueado);
        } else {
            JOptionPane.showMessageDialog(this, "Cédula o contraseña incorrectas.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRegistrar(ActionEvent e) {
        Registro dialog = new Registro(this);
        dialog.setVisible(true);
    }

    public void abrirMenuPrincipal(Usuario usuario) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal(usuario);
            menu.setVisible(true);
            this.dispose();
        });
    }
}
