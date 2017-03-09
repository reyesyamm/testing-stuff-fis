package com.swyam.fisiomer;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entidad.Terapeuta;

import static com.swyam.fisiomer.Helpers.*;
import static com.swyam.fisiomer.Connection.*;

public class LoginActivity extends AppCompatActivity {

    HashMap<String, Integer> terapeutas = new HashMap<>();
    List<String> listaTerapeutas =  new ArrayList<String>();
    Spinner sp_lista_terapeutas;
    EditText txt_contrasena_terapeuta;
    Button btn_iniciar_sesion;
    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        context = getBaseContext();

        // Inicializando las variables
        sp_lista_terapeutas = (Spinner) findViewById(R.id.spinner_lista_terapeutas);
        txt_contrasena_terapeuta = (EditText) findViewById(R.id.edit_text_contrasena_terapeuta);
        btn_iniciar_sesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Verificando conexión");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        // obtenemos de los encontrados y llenamos
        ArrayList<String> listTer = obtenerListaUsuarioTerapeutas(context);//(ArrayList<String>) getIntent().getSerializableExtra("usuarioTerapeutas");
        if (listTer.size() > 0){
            for (String usuario : listTer) {
                listaTerapeutas.add(usuario);
            }
            ArrayAdapter<String> adapterListaTerapeutas = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listaTerapeutas);
            adapterListaTerapeutas.setDropDownViewResource(R.layout.spinner_item);
            sp_lista_terapeutas.setAdapter(adapterListaTerapeutas);
        }else{
            Toast.makeText(context, "No se ha podido cargar la lista de los usuarios. Reinicia la aplicación y comprueba disponibilidad del servidor",Toast.LENGTH_LONG).show();
            /*Snackbar.make(this,"",Snackbar.LENGTH_LONG).setAction("Reintentar", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }).show();*/
        }

        btn_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicion = sp_lista_terapeutas.getSelectedItemPosition();
                if(posicion>=0){
                    String usuario = sp_lista_terapeutas.getSelectedItem().toString();
                    String contrasena = txt_contrasena_terapeuta.getText().toString();

                    if(contrasena.length()>7){
                        progressDialog.setTitle("Verificando Credenciales");
                        progressDialog.setMessage("Espere porfavor");
                        progressDialog.show();
                        contrasena = MD5(contrasena);
                        validarContrasenas(usuario,contrasena);
                    }else{
                        Toast.makeText(getBaseContext(),"La contraseña es muy corta",Toast.LENGTH_LONG).show();
                        txt_contrasena_terapeuta.setFocusable(true);
                        txt_contrasena_terapeuta.setSelected(true);
                    }
                }else{
                    Toast.makeText(getBaseContext(),"Selecciona un usuario",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // una vez cargados los datos pasamos el foco al spinner
        sp_lista_terapeutas.requestFocus();
    }

    private void redireccionarHogar(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
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
                        progressDialog.dismiss();
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
                sp_lista_terapeutas.setSelection(0);
                txt_contrasena_terapeuta.setText("");
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
                    progressDialog.dismiss();
                    Toast.makeText(this,"Los datos son incorrectos", Toast.LENGTH_SHORT).show();
                }
            }else{
                progressDialog.dismiss();
                Toast.makeText(this,response.getString("mensaje"),Toast.LENGTH_SHORT).show();
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(this,"Error en la respuesta del servidor",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }




}
