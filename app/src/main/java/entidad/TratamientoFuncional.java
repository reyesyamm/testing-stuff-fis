package entidad;

import java.io.Serializable;

/**
 * Created by Reyes Yam on 28/02/2017.
 */
public class TratamientoFuncional implements Serializable{

    public int estadoPaciente=1;
    public String tempNombre;
    public String temDesc;

    public TratamientoFuncional(String nombre, String descripcion, int estadoPaciente){
        tempNombre = nombre;
        temDesc = descripcion;
        this.estadoPaciente = estadoPaciente;
    }

    public String nombreTratamiento(){
        return tempNombre;
    }

    public String descripcionTratamiento(){
        return temDesc;
    }




}
