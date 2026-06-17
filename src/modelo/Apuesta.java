package modelo;

public class Apuesta {
    private int id;
    private int usuarioId;
    private int partidoId;
    private int golesLocalApuesta;
    private int golesVisitanteApuesta;

    public Apuesta(int id, int usuarioId, int partidoId, int golesLocalApuesta, int golesVisitanteApuesta) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.partidoId = partidoId;
        this.golesLocalApuesta = golesLocalApuesta;
        this.golesVisitanteApuesta = golesVisitanteApuesta;
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
}
