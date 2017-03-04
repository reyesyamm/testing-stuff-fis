package entidad;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public class Tratamiento {

    public int idTratamiento;
    public String nombreTerapeuta="";
    public String fecha="";
    public String resumen="";

    public Tratamiento(int idTratamiento, String nombreTerapeuta, String fecha, String resumen){
        this.idTratamiento=idTratamiento;
        this.nombreTerapeuta = nombreTerapeuta;
        this.fecha = fecha;
        this.resumen = resumen;
    }

}
