package entidad;

import android.net.Uri;

import com.swyam.fisiomer.Helpers;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Reyes Yam on 28/03/2017.
 */

public class Multimedia implements Serializable {

    public int id;
    public String nombreArchivo;
    public String fecha;
    public String formato;
    public String terapeuta;
    public String fechaFormateada = null;

    public boolean existe = false;

    public static Map<String,String> tiposFormato;
    static
    {
        tiposFormato = new HashMap<String,String>();
        tiposFormato.put("JPG","FOTO");
        tiposFormato.put("PNG","FOTO");
        tiposFormato.put("GIF","FOTO");
        tiposFormato.put("BMP","FOTO");
        tiposFormato.put("MP4","VIDEO");
        tiposFormato.put("MKV","VIDEO");
        tiposFormato.put("3GP","VIDEO");
        tiposFormato.put("AVI","VIDEO");
    }

    public Multimedia(int id, String nombreArchivo, String fecha, String formato, String terapeuta){
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.fecha = fecha;
        this.formato = formato;
        this.terapeuta = terapeuta;
        existe();
    }

    public String obtenerFechaFormateada(){
        if(fechaFormateada==null)
         fechaFormateada = Helpers.getStrFechaFormateada(Helpers.formatearFechaString(this.fecha));
        return fechaFormateada;
    }


    public String obtenerTipoArchivo(){
        String tipo = tiposFormato.get(Helpers.Upper(this.formato));
        return (tipo==null)?"Desconocido":tipo;
    }

    public boolean existe(){
        File fl = new File(Helpers.storageDir(),this.nombreArchivo);
        existe = fl.exists();
        return existe;
    }

    public File fileObj(){
        File archivo = new File(Helpers.storageDir(),this.nombreArchivo);
        return archivo;
    }

    public Uri fileUri(){
        Uri uri = Uri.fromFile(fileObj());
        return uri;
    }



}
