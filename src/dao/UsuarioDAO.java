package dao;

import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.nombre, a.cedula, r.nombre AS rol FROM apostadores a JOIN roles r ON a.rol_id = r.id ORDER BY a.nombre";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("cedula"), rs.getString("rol")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    public Usuario obtenerPorCedula(String cedula) {
        String sql = "SELECT a.id, a.nombre, a.cedula, r.nombre AS rol FROM apostadores a JOIN roles r ON a.rol_id = r.id WHERE a.cedula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("cedula"), rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por cedula: " + e.getMessage());
        }
        return null;
    }

    public boolean existeNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM apostadores WHERE nombre = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de nombre: " + e.getMessage());
        }
        return false;
    }

    public Usuario validarIngreso(String cedula, String passwordHash) {
        String sql = "SELECT a.id, a.nombre, a.cedula, r.nombre AS rol FROM apostadores a JOIN roles r ON a.rol_id = r.id WHERE a.cedula = ? AND a.password = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            pstmt.setString(2, passwordHash);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("cedula"), rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar ingreso: " + e.getMessage());
        }
        return null;
    }

    public Usuario registrar(String nombre, String cedula, String passwordHash) {
        String sqlInsert = "INSERT INTO apostadores (nombre, cedula, password, rol_id) VALUES (?, ?, ?, 2)"; // 2 is 'USUARIO'
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, cedula);
            pstmt.setString(3, passwordHash);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return null;
        }

        // Recuperar el usuario creado/existente con su ID
        String sqlSelect = "SELECT a.id, a.nombre, a.cedula, r.nombre AS rol FROM apostadores a JOIN roles r ON a.rol_id = r.id WHERE a.cedula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("cedula"), rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al recuperar usuario registrado: " + e.getMessage());
        }
        return null;
    }

    // Devuelve los datos de ranking para ser mostrados directamente en una JTable en Swing
    public List<Object[]> obtenerRanking() {
        List<Object[]> ranking = new ArrayList<>();
        String sql = "SELECT apostador, puntos_totales FROM ranking_apostadores";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ranking.add(new Object[]{
                    rs.getString("apostador"),
                    rs.getInt("puntos_totales")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
        }
        return ranking;
    }
}
