package vista;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Login extends JFrame {

    private JComboBox<String> comboUsuarios;
    private JTextField txtNuevoUsuario;
    private JButton btnIngresar;
    private JButton btnRegistrar;
    private UsuarioControlador usuarioControlador;

    public Login() {
        usuarioControlador = new UsuarioControlador();

        // Configuración básica de ventana
        setTitle("Mundial 2026 - Quiniela");
        setSize(400, 320);
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
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 8, 8));
        panelForm.setOpaque(false);

        // Selector de usuario existente
        JLabel lblExistente = new JLabel("Selecciona tu usuario:");
        lblExistente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblExistente.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblExistente);

        comboUsuarios = new JComboBox<>();
        comboUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboUsuarios.setBackground(new Color(45, 45, 52));
        comboUsuarios.setForeground(Color.WHITE);
        cargarUsuariosExistentes();
        panelForm.add(comboUsuarios);

        // Entrada de nuevo usuario
        JLabel lblNuevo = new JLabel("O ingresa un nuevo nombre:");
        lblNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNuevo.setForeground(Color.LIGHT_GRAY);
        panelForm.add(lblNuevo);

        txtNuevoUsuario = new JTextField();
        txtNuevoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNuevoUsuario.setBackground(new Color(45, 45, 52));
        txtNuevoUsuario.setForeground(Color.WHITE);
        txtNuevoUsuario.setCaretColor(Color.WHITE);
        txtNuevoUsuario.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        panelForm.add(txtNuevoUsuario);

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

        btnRegistrar = new JButton("Registrar y Entrar");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setBackground(new Color(52, 152, 219)); // Azul brillante
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.addActionListener(this::accionRegistrar);
        panelBotones.add(btnRegistrar);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarUsuariosExistentes() {
        comboUsuarios.removeAllItems();
        comboUsuarios.addItem("-- Seleccionar Apostador --");
        List<Usuario> lista = usuarioControlador.obtenerListaUsuarios();
        for (Usuario u : lista) {
            comboUsuarios.addItem(u.getNombre());
        }
    }

    private void accionIngresar(ActionEvent e) {
        if (comboUsuarios.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un usuario o escribe uno nuevo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreSeleccionado = (String) comboUsuarios.getSelectedItem();
        Usuario usuarioLogueado = null;
        List<Usuario> lista = usuarioControlador.obtenerListaUsuarios();
        for (Usuario u : lista) {
            if (u.getNombre().equals(nombreSeleccionado)) {
                usuarioLogueado = u;
                break;
            }
        }

        if (usuarioLogueado != null) {
            abrirMenuPrincipal(usuarioLogueado);
        }
    }

    private void accionRegistrar(ActionEvent e) {
        String nuevoNombre = txtNuevoUsuario.getText().trim();
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = usuarioControlador.ingresarORegistrarUsuario(nuevoNombre);
        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            abrirMenuPrincipal(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario o el nombre ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
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
