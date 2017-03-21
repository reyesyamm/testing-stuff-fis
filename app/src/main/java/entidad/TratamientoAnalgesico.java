package entidad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.swyam.fisiomer.Helpers.Capitalize;
import static com.swyam.fisiomer.Helpers.SEPARATOR_STRING_SER;

/**
 * Created by Reyes Yam on 02/03/2017.
 */
public class TratamientoAnalgesico  implements Serializable {

    public static Map<Integer,String> tratamientos;
    static{
        tratamientos = new HashMap<>();
        tratamientos.put(1,"electro");
        tratamientos.put(2,"ultrasonido");
        tratamientos.put(3,"lasser");
    }

    public static Map<Integer,String> tipos;
    static{
        tipos = new HashMap<>();
        tipos.put(1,"EMS");
        tipos.put(2,"TENS");
        tipos.put(3,"interferencial");
        tipos.put(4,"pulsado");
        tipos.put(5,"continuo");
        tipos.put(6,"lasser");
    }

    public int tratamiento;
    public int tipo;
    public String contenido;
    public TratamientoAnalgesico(int tratamiento, int tipo, String contenido){
        this.tratamiento = tratamiento;
        this.tipo = tipo;
        this.contenido = contenido;
    }

    public String resumenTratamiento(){
        if(tratamiento==2)
        {
            String[] elems = contenido.split(SEPARATOR_STRING_SER);
            if(elems.length==2)
                return elems[0]+" por "+elems[1];
            else
                return "Detalles desconocidos";

        }
        else{
            return contenido;
        }
    }

    public String obtenerTratamientoStr(){
        return Capitalize(tratamientos.get(tratamiento));
    }

    public String obtenerTipoStr(){
        return Capitalize(tipos.get(tipo));
    }
}
