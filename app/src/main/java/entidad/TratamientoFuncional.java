package entidad;

import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.swyam.fisiomer.Helpers.Capitalize;

/**
 * Created by Reyes Yam on 28/02/2017.
 */
public class TratamientoFuncional implements Serializable{

    public int musculo;
    public int estado;
    public String movilizacion;

    public int tratamiento;
    public int tipo;

    public static Map<Integer,String> tratamientos;
    static{
        tratamientos = new HashMap<>();
        tratamientos.put(1,"punción seca");
        tratamientos.put(2,"fibrólisis");
        tratamientos.put(3,"técnica manual");
    }

    public static Map<Integer,String> tipos;
    static{
        tipos = new HashMap<>();
        tipos.put(1,"mantenido");
        tipos.put(2,"entrada-salida");
        tipos.put(3,"fibrólisis");
        tipos.put(4,"estiramiento");
        tipos.put(5,"movilización");
    }




    public TratamientoFuncional(int tr, int tipo, int musculo, int estado, String movilizacion){
        this.tratamiento = tr;
        this.tipo = tipo;
        this.musculo = musculo;
        this.estado = estado;
        this.movilizacion = movilizacion;
    }

    public String contenidoTratamiento(){
        if(tipo==5)
            return this.movilizacion;
        else
            return "sobre "+Capitalize(Tratamiento.strMusculo(this.musculo));
    }

    public String obtenerStrTratamiento(){
        return Capitalize(tratamientos.get(tratamiento));
    }

    public String obtenerStrTipo(){
        return Capitalize(tipos.get(tipo));
    }





}
