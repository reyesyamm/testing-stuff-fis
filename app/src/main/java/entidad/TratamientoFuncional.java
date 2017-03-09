package entidad;

import java.io.Serializable;

/**
 * Created by Reyes Yam on 28/02/2017.
 */
public class TratamientoFuncional implements Serializable{

    public String tratamiento;
    public String tipo;
    public int musculo;
    public int estado;
    public String movilizacion;

    public TratamientoFuncional(String tr, String tipo, int musculo, String estado, String movilizacion){
        this.tratamiento = tr;
        this.tipo = tipo;
        this.musculo = musculo;
        this.estado = Tratamiento.estados.get(estado);
        this.movilizacion = movilizacion;
    }

    public String descripcionTratamiento(){
        return "";
    }




}
