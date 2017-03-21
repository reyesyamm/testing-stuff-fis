package entidad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.swyam.fisiomer.Helpers.Capitalize;
import static entidad.Tratamiento.strArticulacion;
import static entidad.Tratamiento.strMusculo;

/**
 * Created by Reyes Yam on 01/03/2017.
 */
public class TratamientoPreventivo implements Serializable{

    public static Map<Integer,String> tratamientos;
    static{
        tratamientos = new HashMap<>();
        tratamientos.put(1,"vendaje");
        tratamientos.put(2,"fortalecimiento");
        tratamientos.put(3,"autoestiramiento");
    }

    public static Map<Integer,String> tipos;
    static{
        tipos = new HashMap<>();
        tipos.put(1,"estabilizar");
        tipos.put(2,"facilitar");
        tipos.put(3,"relajar");
        tipos.put(4,"linfático");
        tipos.put(5,"concéntrico");
        tipos.put(6,"excéntrico");
        tipos.put(7,"autoestiramiento");
    }


    public int tratamiento;
    public int tipo;
    public int articulacionMusculo;
    public TratamientoPreventivo(int tratamiento, int tipo, int articulacionMusculo){
        this.tratamiento = tratamiento;
        this.tipo = tipo;
        this.articulacionMusculo = articulacionMusculo;
    }

    public String resumenTratamiento(){
        if(tipo==1 || tipo==4)
            return "Articulación: "+Capitalize(strArticulacion(articulacionMusculo));
        else
            return "Músculo: "+Capitalize(strMusculo(articulacionMusculo));
    }

    public String obtenerTratamientoStr(){
        return Capitalize(tratamientos.get(tratamiento));
    }

    public String obtenerTipoStr(){
        return Capitalize(tipos.get(tipo));
    }


}
