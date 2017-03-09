package entidad;

import java.util.Date;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public class Paciente {
    public int id;
    public String nombre;
    public int edad;
    public String fecha_ultimo_tratamiento;
    public String nombre_terapeuta;
    public int totales;

    public Paciente(int id, String nombre, int edad, String fecha, String terapeuta, int totales){
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.fecha_ultimo_tratamiento = fecha;
        this.nombre_terapeuta = terapeuta;
        this.totales = totales;
    }


}
