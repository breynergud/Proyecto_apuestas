package dao;

import modelo.Usuario;
import util.HashUtil;
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

    public Usuario validarIngreso(String cedula, String plainPassword) {
        String sql = "SELECT a.id, a.nombre, a.cedula, a.password, r.nombre AS rol FROM apostadores a JOIN roles r ON a.rol_id = r.id WHERE a.cedula = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cedula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (HashUtil.checkPassword(plainPassword, storedHash)) {
                        return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("cedula"), rs.getString("rol"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar ingreso: " + e.getMessage());
        }
        return null;
    }

    public Usuario registrar(String nombre, String cedula, String passwordHash) throws SQLException {
        String sqlInsert = "INSERT INTO apostadores (nombre, cedula, password, rol_id) VALUES (?, ?, ?, 2)"; // 2 is 'USUARIO'
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, cedula);
            pstmt.setString(3, passwordHash);
            pstmt.executeUpdate();
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
        }
        return null;
    }

    // Devuelve los datos de ranking para ser mostrados directamente en una JTable en Swing
    public List<Object[]> obtenerRanking() {
        List<Object[]> ranking = new ArrayList<>();
        String sql = "SELECT r.apostador_id, r.apostador, r.puntos_totales " +
                     "FROM ranking_apostadores r " +
                     "JOIN apostadores a ON r.apostador_id = a.id " +
                     "JOIN roles ro ON a.rol_id = ro.id " +
                     "WHERE ro.nombre != 'ADMINISTRADOR' " +
                     "ORDER BY r.puntos_totales DESC";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ranking.add(new Object[]{
                    rs.getInt("apostador_id"),
                    rs.getString("apostador"),
                    rs.getInt("puntos_totales")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ranking: " + e.getMessage());
        }
        return ranking;
    }

    public Object[] obtenerPuntosYRankUsuario(int usuarioId) {
        String sql = "SELECT " +
                     "  (SELECT COUNT(*) + 1 FROM ranking_apostadores r2 " +
                     "   JOIN apostadores a2 ON r2.apostador_id = a2.id " +
                     "   JOIN roles ro2 ON a2.rol_id = ro2.id " +
                     "   WHERE ro2.nombre != 'ADMINISTRADOR' AND r2.puntos_totales > r1.puntos_totales) AS puesto, " +
                     "  r1.puntos_totales " +
                     "FROM ranking_apostadores r1 " +
                     "WHERE r1.apostador_id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{ rs.getInt("puntos_totales"), rs.getInt("puesto") };
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener puntos y rank del usuario: " + e.getMessage());
        }
        return new Object[]{ 0, 0 };
    }
}
