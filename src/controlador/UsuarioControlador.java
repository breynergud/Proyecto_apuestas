package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import java.util.List;

public class UsuarioControlador {
    private UsuarioDAO usuarioDAO;

    public UsuarioControlador() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> obtenerListaUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario iniciarSesion(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.obtenerPorCedula(cedula.trim());
    }

    public Usuario registrarUsuario(String nombre, String cedula) {
        if (nombre == null || nombre.trim().isEmpty() || cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.registrar(nombre.trim(), cedula.trim());
    }

    public List<Object[]> obtenerTablaPosiciones() {
        return usuarioDAO.obtenerRanking();
    }
}
