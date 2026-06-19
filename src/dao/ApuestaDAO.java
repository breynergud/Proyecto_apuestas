package dao;

import modelo.Apuesta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApuestaDAO {

    public void guardarApuesta(int usuarioId, int partidoId, int golesLocal, int golesVisitante) {
        String sql = "INSERT INTO apuestas (apostador_id, partido_id, goles_local_apuesta, goles_visitante_apuesta) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE goles_local_apuesta = VALUES(goles_local_apuesta), goles_visitante_apuesta = VALUES(goles_visitante_apuesta)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, partidoId);
            pstmt.setInt(3, golesLocal);
            pstmt.setInt(4, golesVisitante);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar apuesta del usuario: " + e.getMessage());
        }
    }

    public Apuesta obtenerApuesta(int usuarioId, int partidoId) {
        String sql = "SELECT id, goles_local_apuesta, goles_visitante_apuesta FROM apuestas " +
                     "WHERE apostador_id = ? AND partido_id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, partidoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Apuesta(
                        rs.getInt("id"),
                        usuarioId,
                        partidoId,
                        rs.getInt("goles_local_apuesta"),
                        rs.getInt("goles_visitante_apuesta")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener apuesta del usuario: " + e.getMessage());
        }
        return null;
    }

    public List<Object[]> obtenerHistorial() {
        return obtenerHistorial("Todos", "Todos");
    }

    public List<Object[]> obtenerHistorial(String apostadorFilter, String grupoFilter) {
        List<Object[]> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT h.id, h.apostador, h.partido, h.goles_local_apuesta, h.goles_visitante_apuesta, h.fecha_registro, h.accion " +
            "FROM historial_apuestas h " +
            "LEFT JOIN apuestas a ON h.apuesta_id = a.id " +
            "LEFT JOIN partidos p ON a.partido_id = p.id " +
            "WHERE 1=1 "
        );
        
        List<Object> params = new ArrayList<>();
        
        if (apostadorFilter != null && !apostadorFilter.equalsIgnoreCase("Todos")) {
            sql.append("AND h.apostador = ? ");
            params.add(apostadorFilter);
        }
        
        if (grupoFilter != null && !grupoFilter.equalsIgnoreCase("Todos")) {
            sql.append("AND p.grupo_id = ? ");
            params.add(grupoFilter);
        }
        
        sql.append("ORDER BY h.fecha_registro DESC");
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
             
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("apostador"),
                        rs.getString("partido"),
                        rs.getInt("goles_local_apuesta") + " - " + rs.getInt("goles_visitante_apuesta"),
                        rs.getTimestamp("fecha_registro"),
                        rs.getString("accion")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial de apuestas filtrado: " + e.getMessage());
        }
        return lista;
    }

    public List<Apuesta> obtenerTodasLasApuestas() {
        List<Apuesta> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.apostador_id, a.partido_id, ap.nombre AS apostador, " +
                     "el.nombre AS local, ev.nombre AS visitante, " +
                     "a.goles_local_apuesta, a.goles_visitante_apuesta " +
                     "FROM apuestas a " +
                     "JOIN apostadores ap ON a.apostador_id = ap.id " +
                     "JOIN roles ro ON ap.rol_id = ro.id " +
                     "JOIN partidos p ON a.partido_id = p.id " +
                     "JOIN equipos el ON p.local_id = el.id " +
                     "JOIN equipos ev ON p.visitante_id = ev.id " +
                     "WHERE ro.nombre != 'ADMINISTRADOR' " +
                     "ORDER BY ap.nombre, el.nombre";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Apuesta(
                    rs.getInt("id"),
                    rs.getInt("apostador_id"),
                    rs.getInt("partido_id"),
                    rs.getInt("goles_local_apuesta"),
                    rs.getInt("goles_visitante_apuesta"),
                    rs.getString("apostador"),
                    rs.getString("local"),
                    rs.getString("visitante")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las apuestas: " + e.getMessage());
        }
        return lista;
    }

    public List<Apuesta> obtenerApuestasPorUsuario(int usuarioId) {
        List<Apuesta> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.apostador_id, a.partido_id, ap.nombre AS apostador, " +
                     "el.nombre AS local, ev.nombre AS visitante, " +
                     "a.goles_local_apuesta, a.goles_visitante_apuesta " +
                     "FROM apuestas a " +
                     "JOIN apostadores ap ON a.apostador_id = ap.id " +
                     "JOIN partidos p ON a.partido_id = p.id " +
                     "JOIN equipos el ON p.local_id = el.id " +
                     "JOIN equipos ev ON p.visitante_id = ev.id " +
                     "WHERE a.apostador_id = ? " +
                     "ORDER BY el.nombre";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Apuesta(
                        rs.getInt("id"),
                        rs.getInt("apostador_id"),
                        rs.getInt("partido_id"),
                        rs.getInt("goles_local_apuesta"),
                        rs.getInt("goles_visitante_apuesta"),
                        rs.getString("apostador"),
                        rs.getString("local"),
                        rs.getString("visitante")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener apuestas del usuario: " + e.getMessage());
        }
        return lista;
    }
}
