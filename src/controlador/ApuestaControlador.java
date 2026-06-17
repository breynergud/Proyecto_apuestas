package controlador;

import dao.PartidoDAO;
import dao.ApuestaDAO;
import modelo.Partido;
import modelo.Apuesta;
import java.util.List;

public class ApuestaControlador {
    private PartidoDAO partidoDAO;
    private ApuestaDAO apuestaDAO;

    public ApuestaControlador() {
        this.partidoDAO = new PartidoDAO();
        this.apuestaDAO = new ApuestaDAO();
    }

    public List<Partido> obtenerPartidosDelMundial() {
        return partidoDAO.obtenerTodos();
    }

    public List<Partido> obtenerPartidosPorGrupo(String grupo) {
        if (grupo == null || grupo.trim().isEmpty()) {
            return partidoDAO.obtenerTodos();
        }
        return partidoDAO.obtenerPorGrupo(grupo.trim().toUpperCase());
    }

    public List<String> obtenerEquiposDelGrupo(String grupo) {
        return partidoDAO.obtenerEquiposPorGrupo(grupo.trim().toUpperCase());
    }

    public Apuesta buscarApuestaUsuario(int usuarioId, int partidoId) {
        return apuestaDAO.obtenerApuesta(usuarioId, partidoId);
    }

    public void guardarApuestaUsuario(int usuarioId, int partidoId, int golesLocal, int golesVisitante) {
        apuestaDAO.guardarApuesta(usuarioId, partidoId, golesLocal, golesVisitante);
    }

    public void registrarResultadoReal(int partidoId, Integer golesLocal, Integer golesVisitante) {
        partidoDAO.registrarMarcador(partidoId, golesLocal, golesVisitante);
    }
}
