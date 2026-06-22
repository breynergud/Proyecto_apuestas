package dao;

import modelo.Partido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    public List<Partido> obtenerTodos() {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.grupo_id, e1.nombre AS local, e2.nombre AS visitante, p.fecha, p.goles_local, p.goles_visitante, p.registrado " +
                     "FROM partidos p " +
                     "JOIN equipos e1 ON p.local_id = e1.id " +
                     "JOIN equipos e2 ON p.visitante_id = e2.id " +
                     "ORDER BY p.id";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int gLocal = rs.getInt("goles_local");
                Integer golesLocal = rs.wasNull() ? null : gLocal;
                int gVisita = rs.getInt("goles_visitante");
                Integer golesVisitante = rs.wasNull() ? null : gVisita;

                lista.add(new Partido(
                    rs.getInt("id"),
                    rs.getString("grupo_id"),
                    rs.getString("local"),
                    rs.getString("visitante"),
                    rs.getString("fecha"),
                    golesLocal,
                    golesVisitante,
                    rs.getBoolean("registrado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los partidos: " + e.getMessage());
        }
        return lista;
    }

    public List<Partido> obtenerPorGrupo(String grupo) {
        List<Partido> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.grupo_id, e1.nombre AS local, e2.nombre AS visitante, p.fecha, p.goles_local, p.goles_visitante, p.registrado " +
                     "FROM partidos p " +
                     "JOIN equipos e1 ON p.local_id = e1.id " +
                     "JOIN equipos e2 ON p.visitante_id = e2.id " +
                     "WHERE p.grupo_id = ? " +
                     "ORDER BY p.id";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int gLocal = rs.getInt("goles_local");
                    Integer golesLocal = rs.wasNull() ? null : gLocal;
                    int gVisita = rs.getInt("goles_visitante");
                    Integer golesVisitante = rs.wasNull() ? null : gVisita;

                    lista.add(new Partido(
                        rs.getInt("id"),
                        rs.getString("grupo_id"),
                        rs.getString("local"),
                        rs.getString("visitante"),
                        rs.getString("fecha"),
                        golesLocal,
                        golesVisitante,
                        rs.getBoolean("registrado")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partidos por grupo: " + e.getMessage());
        }
        return lista;
    }

    public List<String> obtenerEquiposPorGrupo(String grupo) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre FROM equipos WHERE grupo_id = ? ORDER BY id";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, grupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipos del grupo: " + e.getMessage());
        }
        return lista;
    }

    public void registrarMarcador(int partidoId, Integer golesLocal, Integer golesVisitante) {
        String sql = "UPDATE partidos SET goles_local = ?, goles_visitante = ?, registrado = ? WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (golesLocal == null) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, golesLocal);
            }

            if (golesVisitante == null) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, golesVisitante);
            }

            // Si ambos marcadores son asignados, el partido se considera registrado (jugado)
            boolean registrado = (golesLocal != null && golesVisitante != null);
            pstmt.setBoolean(3, registrado);
            pstmt.setInt(4, partidoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar marcador del partido: " + e.getMessage());
        }
    }

    public Partido obtenerPorId(int partidoId) {
        String sql = "SELECT p.id, p.grupo_id, e1.nombre AS local, e2.nombre AS visitante, p.fecha, p.goles_local, p.goles_visitante, p.registrado " +
                     "FROM partidos p " +
                     "JOIN equipos e1 ON p.local_id = e1.id " +
                     "JOIN equipos e2 ON p.visitante_id = e2.id " +
                     "WHERE p.id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partidoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int gLocal = rs.getInt("goles_local");
                    Integer golesLocal = rs.wasNull() ? null : gLocal;
                    int gVisita = rs.getInt("goles_visitante");
                    Integer golesVisitante = rs.wasNull() ? null : gVisita;

                    return new Partido(
                        rs.getInt("id"),
                        rs.getString("grupo_id"),
                        rs.getString("local"),
                        rs.getString("visitante"),
                        rs.getString("fecha"),
                        golesLocal,
                        golesVisitante,
                        rs.getBoolean("registrado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener partido por ID: " + e.getMessage());
        }
        return null;
    }
}
