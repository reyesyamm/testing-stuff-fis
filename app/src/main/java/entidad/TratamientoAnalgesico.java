package entidad;

/**
 * Created by Reyes Yam on 02/03/2017.
 */
public class TratamientoAnalgesico {
    public String tratamiento;
    public String tipo;
    public String parte;
    public TratamientoAnalgesico(String tratamiento, String tipo, String parte){
        this.tratamiento = tratamiento;
        this.tipo = tipo;
        this.parte = parte;
    }

    public String resumenTratamiento(){
        return "Analgésico en "+tipo+" , "+tipo;
    }
}
