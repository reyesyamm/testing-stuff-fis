package entidad;

import com.swyam.fisiomer.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swyam.fisiomer.Helpers.Capitalize;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public class Tratamiento implements Serializable{

    public static final Map<String,Integer> estados ;
    static{
        estados = new HashMap<>();
        estados.put("bien", R.drawable.cara1);
        estados.put("mediobien",R.drawable.cara2);
        estados.put("mal",R.drawable.cara3);
        estados.put("muymal",R.drawable.cara4);
    }

    public int id;
    public int estadoInicio;
    public int estadoFin;
    public Date fecha;
    public String terapeuta;
    public boolean tieneTFuncionales;
    public boolean tieneTPreventivos;
    public boolean tieneTAnalgesicos;

    public List<TratamientoFuncional> tfun = new ArrayList<>();
    public List<TratamientoPreventivo> tprev = new ArrayList<>();
    public List<TratamientoAnalgesico> tanal = new ArrayList<>();

    public Tratamiento(){

    }

    public Tratamiento(
            int id,
            String estadoInicio,
            String estadoFin,
            Date fecha,
            String terapeuta,
            boolean tieneTF,
            boolean tieneTP,
            boolean tieneTA
    ){
        this.id = id;
        this.estadoInicio = estados.get(estadoInicio);
        this.estadoFin = estados.get(estadoFin);
        this.fecha = fecha;
        this.terapeuta = terapeuta;
        tieneTFuncionales = tieneTF;
        tieneTPreventivos = tieneTP;
        tieneTAnalgesicos = tieneTA;
    }

    public String obtenerResumen(){
        String resumen="Se aplicaron los siguientes tratamientos \n";
        if(this.tfun!=null && this.tfun.size()>0){
            resumen+=(this.tfun.size()+" tratamientos funcionales: "+Capitalize(this.tfun.get(0).tratamiento)+"...\n");
        }

        if(this.tprev!=null && this.tprev.size()>0){
            resumen+=(this.tprev.size()+" tratamientos preventivos: "+Capitalize(this.tprev.get(0).tratamiento)+"...\n");
        }

        if(this.tanal!=null && this.tanal.size()>0){
            resumen+=(this.tanal.size()+" tratamientos analgésicos: "+Capitalize(this.tanal.get(0).tratamiento)+"...\n");
        }


        return resumen;
    }

    public void asignarTratamientosFuncionales(List<TratamientoFuncional> tfun){
        this.tfun = tfun;
    }

    public void asignarTratamientoPreventivo(List<TratamientoPreventivo> tprev){
        this.tprev = tprev;
    }

    public void asignarTratamientoAnalgesico(List<TratamientoAnalgesico> tanal){
        this.tanal = tanal;
    }

    public void agregarTratamientoFuncional(TratamientoFuncional tf){
        this.tfun.add(tf);
    }

    public void agregarTratamientoPreventivo(TratamientoPreventivo tp){
        this.tprev.add(tp);
    }

    public void agregarTratamientoAnalgesico(TratamientoAnalgesico ta){
        this.tanal.add(ta);
    }

    public int totalTratamientos(){
        return tfun.size()+tprev.size()+tanal.size();
    }

    public int totalFun(){
        return tfun.size();
    }

    public int totalPrev(){
        return tprev.size();
    }

    public int totalAna(){
        return tanal.size();
    }

    public String getFechaFormateada(){
        SimpleDateFormat format = new SimpleDateFormat("MMMMM dd, yyyy hh:mm a");
        return format.format(this.fecha);
    }

}
