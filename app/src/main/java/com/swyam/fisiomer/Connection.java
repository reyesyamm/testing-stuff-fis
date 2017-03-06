package com.swyam.fisiomer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import entidad.Terapeuta;

/**
 * Created by Reyes Yam on 04/03/2017.
 */

public class Connection{

    public static final String KEY_SERVER_HOST ="pref_key_host_server";
    public static final String SUF_LISTA_USUARIO_TERAPEUTAS="terapeuta/todos.php";
    public static final String SUF_AUTENTICAR="terapeuta/autenticar.php";
    public static final String SUF_REGISTRAR_TERAPEUTA="terapeuta/registrar.php";
    public static final String SUF_MODIFICAR_TERAPEUTA="terapeuta/modificar.php";
    public static final String SUF_REGISTRAR_PACIENTE ="paciente/registrar.php";


    public static final String KEY_USUARIO_LOGEADO="datosUsuario";
    public static final String KEY_ID_USUARIO_LOGEADO="id";
    public static final String KEY_NOMBRE_USUARIO_LOGEADO="nombre";
    public static final String KEY_USUARIO_USUARIO_LOGEADO="usuario";
    public static final String KEY_CONTRASENA_USUARIO_LOGEADO ="contrasena";
    public static final String KEY_ESADMIN_USUARIO_LOGEADO ="esAdmin";
    public static final String KEY_PERMISO_USUARIO_LOGEADO="permiso";
    public static final String KEY_DATOS_GENERALES_TERAPEUTAS="datos_generales_terapeutas";
    public static final String KEY_LISTA_USUARIO_TERAPEUTAS="lista_usuarios_terapeutas";

    public static final String KEY_PACIENTE_ID ="id";
    public static final String KEY_PACIENTE_NOMBRE ="nombre";
    public static final String KEY_PACIENTE_EDAD ="edad";
    public static final String KEY_PACIENTE_OCUPACION ="ocupacion";
    public static final String KEY_PACIENTE_TELEFONO ="telefono";
    public static final String KEY_PACIENTE_DIAGREF ="diagnosticoReferencia";
    public static final String KEY_PACIENTE_MEDDIAG ="medicoDiagnostico";
    public static final String KEY_PACIENTE_IDENGEOG ="identificacionGeografica";
    public static final String KEY_PACIENTE_TERAPEUTA ="terapeuta";
    public static final String KEY_PACIENTE_FECHA = "fecha";

    public static final String KEY_EXP_PACIENTE_PACIENTE="paciente";
    public static final String KEY_EXP_PACIENTE_MOTCONSULT ="motivoConsulta";
    public static final String KEY_EXP_PACIENTE_ANTECEDENTES ="antecedentes";
    public static final String KEY_EXP_PACIENTE_EXPLORACION ="exploracionClinica";
    public static final String KEY_EXP_PACIENTE_DIAGNOSTICOFUN ="diagnosticoFuncional";

    public static final String KEY_OBJETIVOS_TRAT_ID= "id";
    public static final String KEY_OBJETIVOS_TRAT_OBJ1= "objetivo1";
    public static final String KEY_OBJETIVOS_TRAT_OBJ2= "objetivo2";
    public static final String KEY_OBJETIVOS_TRAT_OBJ3= "objetivo3";
    public static final String KEY_OBJETIVOS_TRAT_IDTERAPEUTA= "terapeuta";
    public static final String KEY_OBJETIVOS_TRAT_IDPACIENTE= "paciente";
    public static final String KEY_OBJETIVOS_TRAT_FECHA= "fecha";


    public static String getHostServer(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String h = sharedPreferences.getString(KEY_SERVER_HOST, "");
        if(h.length()>0){
            if(h.charAt(h.length()-1)!='/'){
                h=h+"/";
            }
        }

        return h;
    }

    public static void actualizarCredenciales(Context context, Terapeuta t){
        SharedPreferences preferences = context.getSharedPreferences(KEY_USUARIO_LOGEADO,0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_ID_USUARIO_LOGEADO,t.id);
        editor.putString(KEY_NOMBRE_USUARIO_LOGEADO,t.nombre);
        editor.putString(KEY_USUARIO_USUARIO_LOGEADO,t.usuario);
        editor.putString(KEY_CONTRASENA_USUARIO_LOGEADO,t.contrasena);
        editor.putBoolean(KEY_ESADMIN_USUARIO_LOGEADO,t.esAdmin);
        editor.putBoolean(KEY_PERMISO_USUARIO_LOGEADO,t.permiso);
        editor.commit();
    }

    public static Terapeuta obtenerTerapeutaLogeado(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_USUARIO_LOGEADO,0);
        int id=preferences.getInt(KEY_ID_USUARIO_LOGEADO,-1);
        if(id==-1){
            return null;
        }
        String nombre = preferences.getString(KEY_NOMBRE_USUARIO_LOGEADO,"");
        String usuario = preferences.getString(KEY_USUARIO_USUARIO_LOGEADO,"");
        String contrasena = preferences.getString(KEY_CONTRASENA_USUARIO_LOGEADO,"");
        boolean esAdmin = preferences.getBoolean(KEY_ESADMIN_USUARIO_LOGEADO,false);
        boolean permiso = preferences.getBoolean(KEY_PERMISO_USUARIO_LOGEADO,false);
        return new Terapeuta(id,nombre,usuario,contrasena,esAdmin,permiso);
    }

    public static void limpiarSiExistenCredenciales(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_USUARIO_LOGEADO,0);
        preferences.edit().clear().commit();
    }

    public static void guardarListaUsuarioTerapeutas(Context context, ArrayList<String> terapeutas){
        Set<String> set = new HashSet<String>();
        set.addAll(terapeutas);
        SharedPreferences preferences = context.getSharedPreferences(KEY_DATOS_GENERALES_TERAPEUTAS,0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(KEY_LISTA_USUARIO_TERAPEUTAS,set);
        editor.commit();
    }

    public static void agregarTerapeutaALista(Context context, String usuarioTerapeuta){
        ArrayList<String> ters = obtenerListaUsuarioTerapeutas(context);
        ters.add(usuarioTerapeuta);
        guardarListaUsuarioTerapeutas(context,ters);
    }

    public static ArrayList<String> obtenerListaUsuarioTerapeutas(Context context){
        SharedPreferences preferences = context.getSharedPreferences(KEY_DATOS_GENERALES_TERAPEUTAS,0);
        Set<String> set = preferences.getStringSet(KEY_LISTA_USUARIO_TERAPEUTAS,null);
        if(set==null){
            return null;
        }
        String[] elementos = set.toArray(new String[set.size()]);
        Arrays.sort(elementos);
        ArrayList<String> listaTerapeutas = new ArrayList<String>();
        for(String e:elementos)
            listaTerapeutas.add(e);

        return listaTerapeutas;
    }

    public static String parsearError(VolleyError error){
        String strError="Error desconocido";
        if (error instanceof NoConnectionError) {
            strError="No se pudo hacer la conexion";
        } else if (error instanceof AuthFailureError) {
            strError="Error en el servidor";
        } else if (error instanceof ServerError) {
            strError="Verifica la disponibilidad del servidor";
        } else if (error instanceof NetworkError) {
            strError="Error en la red";
        } else if (error instanceof ParseError) {
            strError="Error en los datos a enviar";
        }else if (error instanceof TimeoutError) {
            strError="El tiempo de espera de respuesta se agotó";
        }
        return strError;
    }


    public static boolean verificarCredencialesLocal(Context context, String usuario, String contrasena){
        Terapeuta ter = obtenerTerapeutaLogeado(context);
        return (ter.usuario.equals(usuario) && ter.contrasena.equals(contrasena));
    }

    public static void VerificarCredencialesRemotas(Context context, JSONObject datos, final VolleyResponseListener listener){
        String url = getHostServer(context)+SUF_AUTENTICAR;
        VolleySingleton.getInstance(context).addToRequestQueue(new JsonObjectRequest(
            Request.Method.POST,
            url,
            datos,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    listener.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            }
        ));
    }



}
