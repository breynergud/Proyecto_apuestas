package modelo;

public class Partido {
    private int id;
    private String grupoId;
    private String local;
    private String visitante;
    private String fecha;
    private Integer golesLocal;      // Puede ser null si no está registrado
    private Integer golesVisitante;  // Puede ser null si no está registrado
    private boolean registrado;

    public Partido(int id, String grupoId, String local, String visitante, String fecha, Integer golesLocal, Integer golesVisitante, boolean registrado) {
        this.id = id;
        this.grupoId = grupoId;
        this.local = local;
        this.visitante = visitante;
        this.fecha = fecha;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.registrado = registrado;
    }

    public int getId() {
        return id;
    }

    public String getGrupoId() {
        return grupoId;
    }

    public String getLocal() {
        return local;
    }

    public String getVisitante() {
        return visitante;
    }

    public String getFecha() {
        return fecha;
    }

    public Integer getGolesLocal() {
        return golesLocal;
    }

    public Integer getGolesVisitante() {
        return golesVisitante;
    }

    public boolean isRegistrado() {
        return registrado;
    }
}
