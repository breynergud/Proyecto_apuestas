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

    public Usuario ingresarORegistrarUsuario(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.registrar(nombre.trim());
    }

    public List<Object[]> obtenerTablaPosiciones() {
        return usuarioDAO.obtenerRanking();
    }
}
