package entidad;

import java.io.Serializable;

/**
 * Created by Reyes Yam on 01/03/2017.
 */
public class TratamientoPreventivo implements Serializable{

    public String tratamiento;
    public String tipo;
    public int articulacionMusculo;
    public TratamientoPreventivo(String tratamiento, String tipo, int articulacionMusculo){
        this.tratamiento = tratamiento;
        this.tipo = tipo;
        this.articulacionMusculo = articulacionMusculo;
    }

    public String resumenTratamiento(){
        return "";
    }


}
