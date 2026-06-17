package vista;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

    private JTextField txtCedula;
    private JButton btnIngresar;
    private JButton btnRegistrar;
    private UsuarioControlador usuarioControlador;

    public Login() {
        usuarioControlador = new UsuarioControlador();

        // Configuración básica de ventana
        setTitle("Mundial 2026 - Quiniela");
        setSize(400, 260); // Ventana adaptada
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

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
        JPanel panelForm = new JPanel(new GridLayout(3, 1, 8, 8));
        panelForm.setOpaque(false);

        // Entrada de Cédula
        JLabel lblExistente = new JLabel("Ingresa tu Cédula / ID:", SwingConstants.CENTER);
        lblExistente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblExistente.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblExistente);

        txtCedula = new JTextField();
        txtCedula.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtCedula.setHorizontalAlignment(JTextField.CENTER);
        txtCedula.setBackground(new Color(45, 45, 52));
        txtCedula.setForeground(Color.WHITE);
        txtCedula.setCaretColor(Color.WHITE);
        txtCedula.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        panelForm.add(txtCedula);

        // Indicación rápida
        JLabel lblTip = new JLabel("(Admin: 12345 | Usuario: 1)", SwingConstants.CENTER);
        lblTip.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTip.setForeground(Color.GRAY);
        panelForm.add(lblTip);

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
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa tu cédula.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioLogueado = usuarioControlador.iniciarSesion(cedula);
        if (usuarioLogueado != null) {
            abrirMenuPrincipal(usuarioLogueado);
        } else {
            JOptionPane.showMessageDialog(this, "La cédula ingresada no está registrada. Por favor regístrate.", "Usuario No Encontrado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRegistrar(ActionEvent e) {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa la cédula que deseas registrar en el campo de texto.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si la cédula ya existe
        Usuario existente = usuarioControlador.iniciarSesion(cedula);
        if (existente != null) {
            JOptionPane.showMessageDialog(this, "Esta cédula ya se encuentra registrada para el usuario: " + existente.getNombre(), "Cédula Duplicada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Solicitar el nombre del usuario
        String nombre = JOptionPane.showInputDialog(this, "Ingresa tu Nombre completo para registrarte:", "Registro de Nuevo Apostador", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null) {
            return; // Cancelado
        }
        nombre = nombre.trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = usuarioControlador.registrarUsuario(nombre, cedula);
        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            abrirMenuPrincipal(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario. El nombre o la cédula podrían estar duplicados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirMenuPrincipal(Usuario usuario) {
        // Ejecución en hilo seguro de Swing
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal(usuario);
            menu.setVisible(true);
            this.dispose(); // Cerrar Login
        });
    }
}
