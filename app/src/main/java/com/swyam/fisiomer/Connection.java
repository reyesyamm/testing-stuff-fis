package com.swyam.fisiomer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entidad.Terapeuta;

import static com.swyam.fisiomer.Helpers.MD5;
import static com.swyam.fisiomer.Helpers.esconderTeclado;

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
    public static final String SUF_BUSCAR_CONTEO_PACIENTE = "paciente/buscar.php";
    public static final String SUF_DATOS_PACIENTE = "paciente/obtener.php";
    public static final String SUF_OBTENER_RES_SIMILARES = "paciente/similitud.php";
    public static final String SUF_AGREGAR_NUEVOS_OBJS = "paciente/modificarobjetivos.php";
    public static final String SUF_GUARDAR_NUEVO_TRATAMIENTO = "paciente/nuevotratamiento.php";
    public static final String SUF_EXPEDIENTE_PACIENTE = "paciente/expediente.php";
    public static final String SUF_GUARDAR_CAMBIOS_DATOS_PACIENTE="paciente/actualizar.php";


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


    public static final int MAX_TRATAMIENTO_PS = 5;
    public static final int MAX_TRATAMIENTO_FIB = Integer.MAX_VALUE;
    public static final int MAX_TRATAMIENTO_TM = Integer.MAX_VALUE;

    public static final int MAX_TRATAMIENTO_ELECTRO = Integer.MAX_VALUE;
    public static final int MAX_TRATAMIENTO_ULTRASONIDO = Integer.MAX_VALUE;
    public static final int MAX_TRATAMIENTO_LASSER = 1;

    public static final int MAX_TRATAMIENTO_VENDAJE = 2;
    public static final int MAX_TRATAMIENTO_FORTALECIMIENTO = 1;
    public static final int MAX_TRATAMIENTO_AUTOESTIRAMIENTO = 3;


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
        String strError="";
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

    public static void cargarListaTerapeutas(Context context,final VolleyResponseListener listener){
        String url = getHostServer(context)+SUF_LISTA_USUARIO_TERAPEUTAS;
        VolleySingleton.getInstance(context).addToRequestQueue(new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
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

    public static void abrirDialogoSeleccionarMusculo(final Activity activity,final String lastSearchFilter,final ArrayAdapter<String> mAdapter, final OnDialogMusculo listener){
        final Context context = activity.getBaseContext();
        final Dialog dialog = new Dialog(activity);
        final EditText txtFiltrarMusculo;
        final ListView listMusculos;
        final Button btnCerrarListaMusculos;
        final ImageButton btnFiltrar;

        dialog.setContentView(R.layout.dialog_musculos);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.setTitle("Seleccionar Músculo");
        txtFiltrarMusculo = (EditText) dialog.findViewById(R.id.txt_busqueda_rapida_musculo);
        if(lastSearchFilter.trim().length()>0)
            txtFiltrarMusculo.setText(lastSearchFilter);
        listMusculos = (ListView) dialog.findViewById(R.id.lv_musculos);
        btnCerrarListaMusculos = (Button) dialog.findViewById(R.id.btn_cerrar_dialogo_musculos);
        btnFiltrar = (ImageButton) dialog.findViewById(R.id.btn_filtrar_busqueda_musculo);
        //mAdapter = ArrayAdapter.createFromResource(this, R.array.str_musculos, android.R.layout.simple_list_item_1);
        listMusculos.setAdapter(mAdapter);
        listMusculos.setTextFilterEnabled(true);

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mAdapter.getFilter().filter(txtFiltrarMusculo.getText().toString());
                    //lastSearchFilter = txtFiltrarMusculo.getText().toString();
                    listener.OnBtnFilter(txtFiltrarMusculo.getText().toString());
                }catch(Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(context,"No se pudo filtrar la lista",Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtFiltrarMusculo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    try{
                        mAdapter.getFilter().filter(v.getText().toString());
                        listener.OnSoftKeyFilter(v.getText().toString());
                        //lastSearchFilter = v.getText().toString();
                    }catch(Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(context,"No se pudo filtrar la lista",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        btnCerrarListaMusculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        listMusculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context, "Musculo: "+mAdapter.getItem(position),Toast.LENGTH_SHORT).show();
                listener.OnMusculoSeleccionado(mAdapter.getItem(position));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void abrirDialogoCredenciales(final Activity activity, final OnDialogCred listener){
        final Context context = activity.getBaseContext();
        List<String> listaTerapeutas = obtenerListaUsuarioTerapeutas(context);
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_confirmar_credenciales);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.setTitle("Ingresa tus Credenciales");

        final View contProgress = dialog.findViewById(R.id.contenedor_progressbar);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressbar);
        final Spinner sp = (Spinner) dialog.findViewById(R.id.spinner_lista_terapeutas_dialog);
        final EditText txtContrasena = (EditText) dialog.findViewById(R.id.txt_contrasena_terapeuta_dialog);
        final Button btnVerificarCredenciales = (Button) dialog.findViewById(R.id.btn_verificar_credenciales_dialog);
        final TextView tvStatusVerificacion = (TextView) dialog.findViewById(R.id.tv_status_verificacion_credenciales);

        final ArrayAdapter<String> adapterListaTerapeutas = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item,listaTerapeutas);
        adapterListaTerapeutas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapterListaTerapeutas);

        btnVerificarCredenciales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contrasena = txtContrasena.getText().toString();
                if(sp.getSelectedItemPosition()>=0){
                    if(contrasena.trim().length()>0){
                        contProgress.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        tvStatusVerificacion.setText("Verificando credenciales");
                        tvStatusVerificacion.requestFocus();
                        tvStatusVerificacion.setTextColor(Color.BLACK);
                        btnVerificarCredenciales.setEnabled(false);
                        String usuario = sp.getSelectedItem().toString();
                        contrasena = MD5(contrasena);

                        if(verificarCredencialesLocal(context,usuario,contrasena)){
                            dialog.dismiss();
                            listener.credencialesValidasLocales();
                        }else{
                            String servidor = getHostServer(context);
                            String autentHost = servidor+SUF_AUTENTICAR;
                            HashMap<String,String> hmap = new HashMap<String, String>();
                            hmap.put(KEY_USUARIO_USUARIO_LOGEADO, usuario);
                            hmap.put(KEY_CONTRASENA_USUARIO_LOGEADO, contrasena);
                            JSONObject obj = new JSONObject(hmap);

                            VerificarCredencialesRemotas(activity.getApplicationContext(), obj, new VolleyResponseListener() {
                                @Override
                                public void onError(VolleyError error) {
                                    btnVerificarCredenciales.setEnabled(true);
                                    Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
                                    tvStatusVerificacion.setText("Ha ocurrido un error");
                                    tvStatusVerificacion.setTextColor(Color.RED);
                                    progressBar.setVisibility(View.GONE);
                                    contProgress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            contProgress.setVisibility(View.GONE);
                                        }
                                    },2000);
                                }

                                @Override
                                public void onResponse(JSONObject response) {
                                    btnVerificarCredenciales.setEnabled(true);
                                    try{
                                        int estado = response.getInt("estado");
                                        if(estado==0){
                                            JSONObject obj = response.getJSONObject("terapeuta");
                                            if(obj.getInt("existe")==1){
                                                int id= obj.getInt(KEY_ID_USUARIO_LOGEADO);
                                                String usuario = obj.getString(KEY_USUARIO_USUARIO_LOGEADO);
                                                String nombre = obj.getString(KEY_NOMBRE_USUARIO_LOGEADO);
                                                String contrasena = obj.getString(KEY_CONTRASENA_USUARIO_LOGEADO);
                                                boolean esAdmin = obj.getInt(KEY_ESADMIN_USUARIO_LOGEADO)>0;
                                                boolean permiso = obj.getInt(KEY_PERMISO_USUARIO_LOGEADO)>0;
                                                actualizarCredenciales(context,new Terapeuta(id,nombre,usuario,contrasena,esAdmin,permiso));
                                                dialog.dismiss();
                                                esconderTeclado(activity);
                                                listener.credencialesValidasRemotas();
                                            }else{
                                                tvStatusVerificacion.setText("Credenciales Invalidas");
                                                tvStatusVerificacion.setTextColor(Color.RED);
                                                progressBar.setVisibility(View.GONE);
                                                contProgress.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        contProgress.setVisibility(View.GONE);
                                                    }
                                                },2000);
                                            }
                                        }else{
                                            tvStatusVerificacion.setText(response.getString("mensaje"));
                                            tvStatusVerificacion.setTextColor(Color.RED);
                                            progressBar.setVisibility(View.GONE);
                                            contProgress.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    contProgress.setVisibility(View.GONE);
                                                }
                                            },2000);
                                        }
                                    }catch(Exception ex){
                                        Toast.makeText(context,"No se han podido verificar las credenciales",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else{
                        txtContrasena.requestFocus();
                    }

                }else{
                    Toast.makeText(context, "Selecciona tu usuario",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }



}
