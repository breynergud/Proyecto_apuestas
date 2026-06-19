package vista;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Registro extends JDialog {

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

        // Configuración básica
        setSize(400, 360);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Panel Principal con estilo gris carbón
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(30, 30, 36));
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panelPrincipal);

        // Header / Título
        JLabel lblTitulo = new JLabel("REGISTRO DE APOSTADOR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(46, 204, 113)); // Verde esmeralda
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel Central (Formulario)
        JPanel panelForm = new JPanel(new GridLayout(6, 1, 4, 4));
        panelForm.setOpaque(false);

        // Entrada de Cédula
        JLabel lblCedula = new JLabel("Cédula / ID:");
        lblCedula.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCedula.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblCedula);

        txtCedula = estiloField(new JTextField());
        panelForm.add(txtCedula);

        // Entrada de Nombre Completo
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNombre.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblNombre);

        txtNombre = estiloField(new JTextField());
        panelForm.add(txtNombre);

        // Entrada de Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblPassword);

        txtPassword = estiloPasswordField(new JPasswordField());
        panelForm.add(txtPassword);

        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        // Panel Inferior (Botones)
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(new Color(46, 204, 113));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(this::accionRegistrar);
        panelBotones.add(btnRegistrar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(52, 152, 219));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private JTextField estiloField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(new Color(45, 45, 52));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return tf;
    }

    private JPasswordField estiloPasswordField(JPasswordField pf) {
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pf.setBackground(new Color(45, 45, 52));
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return pf;
    }

    private void accionRegistrar(ActionEvent e) {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (cedula.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cedula.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "La cédula debe contener únicamente números.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nombre.length() < 3) {
            JOptionPane.showMessageDialog(this, "El nombre completo debe tener al menos 3 caracteres.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
            JOptionPane.showMessageDialog(this, "El nombre debe contener únicamente letras y espacios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres por seguridad.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si la cédula ya existe en la base de datos
        if (usuarioControlador.existeCedula(cedula)) {
            JOptionPane.showMessageDialog(this, "Esta cédula ya se encuentra registrada por otro usuario.", "Cédula Duplicada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Verificar si el nombre de usuario ya existe
        if (usuarioControlador.existeNombre(nombre)) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario '" + nombre + "' ya está registrado por otra persona. Por favor elige otro.", "Nombre Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registrar usuario
        try {
            Usuario usuario = usuarioControlador.registrarUsuario(nombre, cedula, password);
            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                loginFrame.abrirMenuPrincipal(usuario);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el usuario en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de base de datos al registrar: " + ex.getMessage(), "Error de Registro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
