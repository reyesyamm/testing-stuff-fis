package com.swyam.fisiomer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

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
import static com.swyam.fisiomer.Connection.VerificarCredencialesRemotas;
import static com.swyam.fisiomer.Connection.actualizarCredenciales;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.obtenerListaUsuarioTerapeutas;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Connection.verificarCredencialesLocal;
import static com.swyam.fisiomer.Helpers.Capitalize;
import static com.swyam.fisiomer.Helpers.MD5;
import static com.swyam.fisiomer.Helpers.Upper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvNombreTerapeuta;
    EditText txtBuscarPaciente;
    Button btnBuscarPaciente;
    TextInputLayout til;
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
        btnBuscarPaciente = (Button) findViewById(R.id.btn_buscar_paciente);
        til = (TextInputLayout) findViewById(R.id.text_input_layout);
        btnLimpiar = (ImageButton) findViewById(R.id.btn_limpiar_cuadro_busqueda);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Buscando");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);



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

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til.setError(null);
                txtBuscarPaciente.setText("");
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NewPatientActivity.class);
                startActivity(intent);
            }
        });

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
            verificarCredencialYAbrirPerfil();
        }else if(id==R.id.nav_pacientes){

        }else if(id==R.id.nav_manage){
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void verificarCredencialYAbrirPerfil(){
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_confirmar_credenciales);
        final View contProgress = dialog.findViewById(R.id.contenedor_progressbar);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressbar);
        final Spinner sp = (Spinner) dialog.findViewById(R.id.spinner_lista_terapeutas_dialog);
        final EditText txtContrasena = (EditText) dialog.findViewById(R.id.txt_contrasena_terapeuta_dialog);
        final Button btnVerificarCredenciales = (Button) dialog.findViewById(R.id.btn_verificar_credenciales_dialog);
        final TextView tvStatusVerificacion = (TextView) dialog.findViewById(R.id.tv_status_verificacion_credenciales);

        final ArrayAdapter<String> adapterListaTerapeutas = new ArrayAdapter<String>(HomeActivity.this, android.R.layout.simple_spinner_item,listaTerapeutas);
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
                            Intent intent = new Intent(HomeActivity.this,PerfilActivity.class);
                            startActivity(intent);
                        }else{
                            String servidor = getHostServer(context);
                            String autentHost = servidor+SUF_AUTENTICAR;
                            HashMap<String,String> hmap = new HashMap<String, String>();
                            hmap.put(KEY_USUARIO_USUARIO_LOGEADO, usuario);
                            hmap.put(KEY_CONTRASENA_USUARIO_LOGEADO, contrasena);
                            JSONObject obj = new JSONObject(hmap);

                            VerificarCredencialesRemotas(getApplicationContext(), obj, new VolleyResponseListener() {
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
                                                Intent intent = new Intent(HomeActivity.this,PerfilActivity.class);
                                                startActivity(intent);
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

    public void procesarBusqueda(){
        String nombrePaciente = txtBuscarPaciente.getText().toString();
        if(nombrePaciente.length()>0){
            til.setError(null);
            progressDialog.show();
            realizarBusqueda();

        }else{
            til.setError("Valor inválido");
        }
    }

    public void realizarBusqueda(){
        progressDialog.dismiss();
        int total_encontrados = 1;
        if(total_encontrados==0){
            Toast.makeText(this,"No se han encontrado resultados",Toast.LENGTH_LONG).show();
        }else if(total_encontrados>1){
            Intent intent = new Intent(HomeActivity.this, PatientsFoundActivity.class);
            intent.putExtra("busqueda",txtBuscarPaciente.getText().toString());
            startActivity(intent);
        }else{
            Intent intent = new Intent(HomeActivity.this, PatientActivity.class);
            intent.putExtra("busqueda",txtBuscarPaciente.getText().toString());
            startActivity(intent);
        }
    }

    public void cancelarBusqueda(){

    }

    public void cargarNombreTerapeuta(){
        Terapeuta t = obtenerTerapeutaLogeado(context);
        if(t!=null){
            tvNombreTerapeuta.setText(Capitalize(t.nombre));
        }
    }
}
