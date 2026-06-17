package dao;

import modelo.Apuesta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
