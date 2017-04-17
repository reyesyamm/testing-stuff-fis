package entidad;

/**
 * Created by Reyes Yam on 04/03/2017.
 */

public class Terapeuta {

    public int id;
    public String nombre;
    public String usuario;
    public String contrasena;
    public boolean esAdmin;
    public boolean permiso;
    public String apikey;

    public Terapeuta(int id, String nombre, String usuario, String contrasena, boolean esAdmin, boolean permiso, String apikey){
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.esAdmin = esAdmin;
        this.permiso = permiso;
        this.apikey = apikey;
    }
}
