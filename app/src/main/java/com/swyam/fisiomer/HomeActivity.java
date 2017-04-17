package com.swyam.fisiomer;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entidad.Terapeuta;

import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;
import static com.swyam.fisiomer.Connection.KEY_CONTRASENA_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_ESADMIN_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_ID_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_NOMBRE_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_PERMISO_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_USUARIO_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.SUF_AUTENTICAR;
import static com.swyam.fisiomer.Connection.SUF_BUSCAR_CONTEO_PACIENTE;
import static com.swyam.fisiomer.Connection.VerificarCredencialesRemotas;
import static com.swyam.fisiomer.Connection.abrirDialogoCredenciales;
import static com.swyam.fisiomer.Connection.actualizarCredenciales;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.limpiarSiExistenCredenciales;
import static com.swyam.fisiomer.Connection.obtenerListaUsuarioTerapeutas;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Connection.verificarCredencialesLocal;
import static com.swyam.fisiomer.Helpers.Capitalize;
import static com.swyam.fisiomer.Helpers.MD5;
import static com.swyam.fisiomer.Helpers.Upper;
import static com.swyam.fisiomer.Helpers.esconderTeclado;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvNombreTerapeuta;
    EditText txtBuscarPaciente;
    ImageButton btnBuscarPaciente;
    ProgressDialog progressDialog;
    ImageButton btnLimpiar;
    List<String> listaTerapeutas =  new ArrayList<String>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // inicializamos variables
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        context = getBaseContext();
        tvNombreTerapeuta = (TextView) findViewById(R.id.tv_nombre_terapeuta);
        txtBuscarPaciente = (EditText) findViewById(R.id.txt_buscar_paciente);
        btnBuscarPaciente = (ImageButton) findViewById(R.id.btn_buscar_paciente);
        //btnLimpiar = (ImageButton) findViewById(R.id.btn_limpiar_cuadro_busqueda);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Buscando");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        solicitarPermisos();



        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelarBusqueda();
                dialog.dismiss();
            }
        });

        txtBuscarPaciente.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    procesarBusqueda();
                    return true;
                }
                return false;
            }
        });

        btnBuscarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               procesarBusqueda();

            }
        });

        /*btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtBuscarPaciente.setError(null);
                txtBuscarPaciente.setText("");
            }
        });*/


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewPatientActivity.class);
                startActivity(intent);
            }
        });

        crearCarpetaImagenes();

    }

    public void crearCarpetaImagenes(){
        try{
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Helpers.IMAGE_DIRECTORY_NAME);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("HomeActivity", "Oops! Failed create "
                            + Helpers.IMAGE_DIRECTORY_NAME + " directory");
                    Toast.makeText(context,"No se pudo crear la carpeta de imagenes",Toast.LENGTH_SHORT).show();
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(context,"No se pudo crear la carpeta de imagenes",Toast.LENGTH_SHORT).show();
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 2;
    private void solicitarPermisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){

            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_USE_CAMERA);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        cargarTerapeutas();
        cargarNombreTerapeuta();
    }



    private void cargarTerapeutas(){

        ArrayList<String> tmp = obtenerListaUsuarioTerapeutas(context);
        listaTerapeutas.clear();
        listaTerapeutas.addAll(tmp);
        if(listaTerapeutas.size()==1){
            Toast.makeText(this,"No existen terapeutas registrados o los datos no estan actualizados",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_terapeuta) {
            // Handle the camera action
            //verificarCredencialYAbrirPerfil();

            abrirDialogoCredenciales(HomeActivity.this, new OnDialogCred() {
                @Override
                public void credencialesValidasLocales() {
                    Intent intent = new Intent(HomeActivity.this,PerfilActivity.class);
                    startActivity(intent);
                }

                @Override
                public void credencialesValidasRemotas() {
                    Intent intent = new Intent(HomeActivity.this,PerfilActivity.class);
                    startActivity(intent);
                }
            });

        }else if(id==R.id.nav_pacientes){

        }else if(id==R.id.nav_manage){
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(id==R.id.nav_finalizar_sesion){
            limpiarSiExistenCredenciales(context);
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void procesarBusqueda(){
        esconderTeclado(this);
        String nombrePaciente = txtBuscarPaciente.getText().toString();
        if(nombrePaciente.length()>0){
            txtBuscarPaciente.setError(null);
            progressDialog.show();
            realizarBusqueda(nombrePaciente);
        }else{
            txtBuscarPaciente.setError("Valor inválido");
        }
    }

    public void realizarBusqueda(final String token){
        String url = getHostServer(context)+SUF_BUSCAR_CONTEO_PACIENTE;
        JSONObject obj = new JSONObject();

        Terapeuta t = Connection.obtenerTerapeutaLogeado(context);
        if(t==null){
            Toast.makeText(context,"Error, terapeuta no logeado, reinicia la aplicación",Toast.LENGTH_SHORT).show();
            return;
        }

        try{

            obj.put("token",token);
            obj.put("apikey",t.apikey);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try{
                                if(response.getInt("estado")==0){
                                    JSONObject paciente = response.getJSONObject("resultado");
                                    int total = paciente.getInt("total");
                                    if(total==1){
                                        int id = paciente.getInt("id");
                                        Intent intent = new Intent(HomeActivity.this, PatientActivity.class);
                                        intent.putExtra("id",id);
                                        startActivity(intent);
                                    }else if(total>1){
                                        Intent intent = new Intent(HomeActivity.this, PatientsFoundActivity.class);
                                        intent.putExtra("token",token);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(context,"No se han encontrado pacientes para '"+token+"'",Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception ex){
                                Toast.makeText(context, "Error en la respuesta", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
                        }
                    }
            ));
        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_LONG).show();
        }
    }

    public void cancelarBusqueda(){
        VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public void cargarNombreTerapeuta(){
        Terapeuta t = obtenerTerapeutaLogeado(context);
        if(t!=null){
            tvNombreTerapeuta.setText(Capitalize(t.nombre));
        }
    }
}
