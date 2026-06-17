package modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String cedula;
    private String rol;

    public Usuario(int id, String nombre, String cedula, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public String getRol() {
        return rol;
    }

    public boolean esAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(this.rol);
    }
}
