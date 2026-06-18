package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.HashUtil;
import java.util.List;

public class UsuarioControlador {
    private UsuarioDAO usuarioDAO;

    public UsuarioControlador() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> obtenerListaUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario iniciarSesion(String cedula, String password) {
        if (cedula == null || cedula.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        String passwordHash = HashUtil.hashPassword(password.trim());
        return usuarioDAO.validarIngreso(cedula.trim(), passwordHash);
    }

    public Usuario registrarUsuario(String nombre, String cedula, String password) throws java.sql.SQLException {
        if (nombre == null || nombre.trim().isEmpty() || 
            cedula == null || cedula.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }
        String passwordHash = HashUtil.hashPassword(password.trim());
        return usuarioDAO.registrar(nombre.trim(), cedula.trim(), passwordHash);
    }

    public boolean existeNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return usuarioDAO.existeNombre(nombre.trim());
    }

    public boolean existeCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return false;
        }
        return usuarioDAO.obtenerPorCedula(cedula.trim()) != null;
    }

    public List<Object[]> obtenerTablaPosiciones() {
        return usuarioDAO.obtenerRanking();
    }
}
