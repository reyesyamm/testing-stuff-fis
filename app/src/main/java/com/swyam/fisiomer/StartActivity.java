package com.swyam.fisiomer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entidad.Terapeuta;

import static com.swyam.fisiomer.Connection.KEY_CONTRASENA_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_ESADMIN_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_ID_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_NOMBRE_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_PERMISO_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_USUARIO_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.SUF_AUTENTICAR;
import static com.swyam.fisiomer.Connection.SUF_LISTA_USUARIO_TERAPEUTAS;
import static com.swyam.fisiomer.Connection.actualizarCredenciales;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.guardarListaUsuarioTerapeutas;
import static com.swyam.fisiomer.Connection.limpiarSiExistenCredenciales;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Connection.parsearError;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        hide();
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        context= getBaseContext();

        String server = getHostServer(context);

        // Primero verificaremos la correcta conectividad al servidor
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new StringRequest(
                Request.Method.GET,
                server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        continuarCarga();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String strError = parsearError(error);
                        if (strError.length()==0 || error instanceof NoConnectionError || error instanceof ServerError || error instanceof NoConnectionError) {

                            Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("reiniciar",true);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        } else {
                            Toast.makeText(context,strError,Toast.LENGTH_LONG).show();

                        }
                    }
                }

        ));

    }

    @Override
    public void onBackPressed(){
        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

        super.onBackPressed();
    }

    private void continuarCarga(){
        // en este apartado verificaremos si existe una sesion iniciada
        cargarListaTerapeutas();
        Terapeuta ter = obtenerTerapeutaLogeado(context);
        if(ter!=null){
            validarContrasenas(ter.usuario,ter.contrasena);
        }else{
            abrirLogin();
        }
    }

    private void  cargarListaTerapeutas(){
        final ArrayList<String> terapeutas = new ArrayList<String>();
        String dirServidor = getHostServer(context);
        String dirListaTerapeutas=dirServidor+SUF_LISTA_USUARIO_TERAPEUTAS;
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                Request.Method.GET,
                dirListaTerapeutas,
                null,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int estado  = Integer.parseInt(response.getString("estado"));
                            if(estado==0){ // datos obtenidos
                                JSONArray usuarios = response.getJSONArray("usuarios");
                                for(int i=0;i<usuarios.length();i++){
                                    JSONObject obj = usuarios.getJSONObject(i);
                                    terapeutas.add(obj.getString("usuario"));
                                }

                                //Toast.makeText(context, Helpers.SerializarLista(terapeutas), Toast.LENGTH_LONG).show();
                                guardarListaUsuarioTerapeutas(context,terapeutas);
                            }else{
                                Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception ex){
                            informarError();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                informarError();
                error.printStackTrace();
            }
        }
        ).setRetryPolicy(new DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )));
    }

    private void abrirLogin(){
        // guardamos la lista de los terapeutas
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

    }

    public void informarError(){
        Toast.makeText(this,"Ocurrió un error intentando conectarse al servidor. Reinicia la aplicación y comprueba la conectividad",Toast.LENGTH_LONG).show();
    }

    public boolean validarContrasenas(String usuario,String contrasena){
        String servidor = getHostServer(context);
        String autentHost = servidor+SUF_AUTENTICAR;
        HashMap<String,String> mapTerapeuta = new HashMap<>();
        mapTerapeuta.put("usuario",usuario);
        mapTerapeuta.put("contrasena",contrasena);
        JSONObject objDatos = new JSONObject(mapTerapeuta);

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                Request.Method.POST,
                autentHost,
                objDatos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        procesarRespuesta(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
            }
        }

        ));
        return true;
    }

    private void procesarRespuesta(JSONObject response){
        limpiarSiExistenCredenciales(context);
        try{
            int estado = Integer.parseInt(response.getString("estado"));
            if(estado==0){
                JSONObject obj = response.getJSONObject("terapeuta");
                int existe = obj.getInt("existe");
                if(existe==1){
                    int id= obj.getInt(KEY_ID_USUARIO_LOGEADO);
                    String usuario = obj.getString(KEY_USUARIO_USUARIO_LOGEADO);
                    String nombre = obj.getString(KEY_NOMBRE_USUARIO_LOGEADO);
                    String contrasena = obj.getString(KEY_CONTRASENA_USUARIO_LOGEADO);
                    boolean esAdmin = obj.getInt(KEY_ESADMIN_USUARIO_LOGEADO)>0;
                    boolean permiso = obj.getInt(KEY_PERMISO_USUARIO_LOGEADO)>0;
                    actualizarCredenciales(context,new Terapeuta(id,nombre,usuario,contrasena,esAdmin,permiso));
                    redireccionarHogar();
                }else{
                    // redireccionar a Login
                    abrirLogin();
                }
            }else{
                Toast.makeText(this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(this,"Error en la respuesta del servidor",Toast.LENGTH_SHORT).show();
        }
    }

    private void redireccionarHogar(){
        Intent intent = new Intent(StartActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
