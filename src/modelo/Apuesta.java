package modelo;

public class Apuesta {
    private int id;
    private int usuarioId;
    private int partidoId;
    private int golesLocalApuesta;
    private int golesVisitanteApuesta;
    private String nombreApostador;
    private String equipoLocal;
    private String equipoVisitante;

    public Apuesta(int id, int usuarioId, int partidoId, int golesLocalApuesta, int golesVisitanteApuesta) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.partidoId = partidoId;
        this.golesLocalApuesta = golesLocalApuesta;
        this.golesVisitanteApuesta = golesVisitanteApuesta;
    }

    public Apuesta(int id, int usuarioId, int partidoId, int golesLocalApuesta, int golesVisitanteApuesta, String nombreApostador, String equipoLocal, String equipoVisitante) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.partidoId = partidoId;
        this.golesLocalApuesta = golesLocalApuesta;
        this.golesVisitanteApuesta = golesVisitanteApuesta;
        this.nombreApostador = nombreApostador;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public int getPartidoId() {
        return partidoId;
    }

    public int getGolesLocalApuesta() {
        return golesLocalApuesta;
    }

    public int getGolesVisitanteApuesta() {
        return golesVisitanteApuesta;
    }

    public String getNombreApostador() {
        return nombreApostador;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocalApuesta;
    }

    public int getGolesVisitante() {
        return golesVisitanteApuesta;
    }
}
