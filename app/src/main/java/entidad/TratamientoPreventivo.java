package entidad;

import java.io.Serializable;

/**
 * Created by Reyes Yam on 01/03/2017.
 */
public class TratamientoPreventivo implements Serializable{
    public String tratamiento;
    public String tipo;
    public String parte;
    public TratamientoPreventivo(String tratamiento, String tipo, String parte){
        this.tratamiento = tratamiento;
        this.tipo = tipo;
        this.parte = parte;
    }

    public String resumenTratamiento(){
        return "Prevención en "+tipo+" , "+tipo;
    }

}
