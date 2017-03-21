package com.swyam.fisiomer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;

import entidad.Terapeuta;

import static com.swyam.fisiomer.Connection.KEY_CONTRASENA_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_ESADMIN_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_NOMBRE_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_PERMISO_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.KEY_USUARIO_USUARIO_LOGEADO;
import static com.swyam.fisiomer.Connection.SUF_REGISTRAR_TERAPEUTA;
import static com.swyam.fisiomer.Connection.actualizarCredenciales;
import static com.swyam.fisiomer.Connection.agregarTerapeutaALista;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Helpers.MD5;
import static com.swyam.fisiomer.Helpers.esconderTeclado;

public class PerfilActivity extends AppCompatActivity {

    Context context;
    EditText txtNombre, txtContrasena, txtConfirmarContrasena;
    TextView tvUsuario, tvPermisos;
    Button btnGuardarCambiosPerfil;
    ImageButton btnEditarDatos, btnInfoUsuario;
    View contContrasena,contConfirmarContrasena, contInfoContrasena;
    Button btnNuevoTerapeuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // inicializar variables
        context = getBaseContext();
        txtNombre = (EditText) findViewById(R.id.txt_nombre_terapeuta);
        tvUsuario = (TextView) findViewById(R.id.tv_usuario_terapeuta);
        txtContrasena = (EditText) findViewById(R.id.txt_contrasena_terapeuta);
        txtConfirmarContrasena = (EditText) findViewById(R.id.txt_confirmar_contrasena_terapeuta);
        tvPermisos = (TextView) findViewById(R.id.tv_permisos_terapeuta);
        btnGuardarCambiosPerfil = (Button) findViewById(R.id.btn_guardar_cambios);
        contContrasena = findViewById(R.id.contenedor_contrasena);
        contConfirmarContrasena = findViewById(R.id.contenedor_confirmar_contrasena);
        btnEditarDatos = (ImageButton) findViewById(R.id.btn_editar_datos);
        btnInfoUsuario = (ImageButton) findViewById(R.id.btn_info_usuario_terapeuta);
        btnNuevoTerapeuta = (Button) findViewById(R.id.btn_agregar_terapeuta);
        contInfoContrasena = findViewById(R.id.tv_info_contrasenas);



        llenarDatos();

        IniciarConfiguracionesPredeterminadas();

        btnEditarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrar(true);
            }
        });

        btnGuardarCambiosPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarGuardarDatos();
            }
        });

        btnInfoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"El usuario es único y no puede ser cambiado",Toast.LENGTH_SHORT).show();
            }
        });

        btnNuevoTerapeuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFormDialog();
            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean v){
        super.onWindowFocusChanged(v);
        btnEditarDatos.requestFocus();
    }

    private void intentarGuardarDatos(){
        String nombre = txtNombre.getText().toString();
        String contrasena = txtContrasena.getText().toString();
        String confirmarContrasena = txtConfirmarContrasena.getText().toString();


        txtNombre.setError(null);
        txtConfirmarContrasena.setError(null);
        txtContrasena.setError(null);

        if(nombre.trim().equals("")){
            txtNombre.setError("El nombre no puede estar vacío");
        }else{
            Terapeuta t = Connection.obtenerTerapeutaLogeado(context);
            String md5Contrasena;
            if(contrasena.trim().equals("")){
                md5Contrasena = t.contrasena;
                contrasena = t.contrasena;
                confirmarContrasena = t.contrasena;
            }else{
                md5Contrasena = MD5(contrasena);
            }

            if(contrasena.length()<8 || contrasena.length()>32){
                txtContrasena.setError("Longitud mínima 8 caracteres. Longitud máxima 32 caractéres");
            }else{
                if(contrasena.equals(confirmarContrasena)){
                    guardarCambiosPerfil(t.id,nombre,md5Contrasena);
                }else{
                    txtContrasena.setError("Las contraseñas no coinciden");
                    txtConfirmarContrasena.setError("Las contraseñas no coinciden");
                }
            }

        }

    }

    private void guardarCambiosPerfil(final int id, final String nombre, final String contrasena){

        final ProgressDialog progressDialog = new ProgressDialog(PerfilActivity.this);
        progressDialog.setTitle("Guardando los cambios");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = getHostServer(context)+Connection.SUF_MODIFICAR_TERAPEUTA;
        HashMap<String,String> hmap = new HashMap<>();
        hmap.put(Connection.KEY_ID_USUARIO_LOGEADO, String.valueOf(id));
        hmap.put(Connection.KEY_NOMBRE_USUARIO_LOGEADO,nombre);
        hmap.put(Connection.KEY_CONTRASENA_USUARIO_LOGEADO,contrasena);
        JSONObject obj = new JSONObject(hmap);
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                Request.Method.POST,
                url,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getInt("estado")==0){
                                Toast.makeText(context,"Cambios Guardados", Toast.LENGTH_LONG).show();
                                Terapeuta t = obtenerTerapeutaLogeado(context);
                                t.nombre = nombre;
                                t.contrasena = contrasena;
                                actualizarCredenciales(context,t);
                                mostrar(false);
                            }else{
                                Toast.makeText(context, response.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception ex){
                            Toast.makeText(context, "Error al intentar actualizar los datos",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, parsearError(error),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
        ));


    }

    public void llenarDatos(){
        Terapeuta ter = Connection.obtenerTerapeutaLogeado(context);
        txtNombre.setText(ter.nombre);
        tvUsuario.setText(ter.usuario);
        if(ter.esAdmin){
            tvPermisos.setText("Tienes todos los permisos disponibles. Eres administrador");
            btnNuevoTerapeuta.setEnabled(true);
        }else{
            if(ter.permiso){
                tvPermisos.setText("Puedes guardar tratamientos realizados. Eres usuario estándar");
            }else{
                tvPermisos.setText("Tienes permisos de solo lectura. No puedes agregar tratamientos");
            }
            btnNuevoTerapeuta.setEnabled(false);
            btnNuevoTerapeuta.setVisibility(View.GONE);
        }
    }

    public void IniciarConfiguracionesPredeterminadas(){
        mostrar(false);
    }

    public void mostrar(boolean b){
        contContrasena.setVisibility(b?View.VISIBLE:View.GONE);
        contConfirmarContrasena.setVisibility(b?View.VISIBLE:View.GONE);
        btnGuardarCambiosPerfil.setVisibility(b?View.VISIBLE:View.GONE);
        contInfoContrasena.setVisibility(b?View.VISIBLE:View.GONE);
        txtNombre.setEnabled(b);
        txtContrasena.setEnabled(b);
        txtConfirmarContrasena.setEnabled(b);

    }

    public void mostrarFormDialog(){
        final Dialog dialog = new Dialog(PerfilActivity.this);
        dialog.setContentView(R.layout.dialog_nuevo_terapeuta);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final EditText txtNuevoNombre, txtNuevoUsuario, txtNuevaContrasena, txtNuevaConfirmContrasena;
        final CheckBox chPAdmin, chPDarTerapia;
        final Button btnGuardarNuevoTerapeuta;
        final View contenedorprogress;
        final ProgressBar progressBar;
        final TextView statusRegistro;

        txtNuevoNombre = (EditText) dialog.findViewById(R.id.txt_nombre_nuevo_terapeuta);
        txtNuevoUsuario = (EditText) dialog.findViewById(R.id.txt_usuario_nuevo_terapeuta);
        txtNuevaContrasena = (EditText) dialog.findViewById(R.id.txt_contrasena_nuevo_terapeuta);
        txtNuevaConfirmContrasena = (EditText) dialog.findViewById(R.id.txt_confirmar_contrasena_nuevo_terapeuta);
        chPAdmin = (CheckBox) dialog.findViewById(R.id.checkbox_permiso_administrador);
        chPDarTerapia = (CheckBox) dialog.findViewById(R.id.checkbox_permiso_registrar_terapia);
        btnGuardarNuevoTerapeuta = (Button) dialog.findViewById(R.id.btn_registrar_nuevo_terapeuta);
        contenedorprogress = dialog.findViewById(R.id.contenedor_progressbar);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progressbar);
        statusRegistro = (TextView) dialog.findViewById(R.id.tv_status_registro_terapeuta);

        dialog.setTitle("Nuevo Terapeuta");

        chPAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                chPDarTerapia.setChecked(isChecked);
            }
        });

        chPDarTerapia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chPAdmin.isChecked()){
                    chPAdmin.setChecked(isChecked);
                }
            }
        });

        btnGuardarNuevoTerapeuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            txtNuevoNombre.setError(null);
            txtNuevoUsuario.setError(null);
            txtNuevaContrasena.setError(null);
            txtNuevaConfirmContrasena.setError(null);

            String nombre = txtNuevoNombre.getText().toString();
            final String usuario = txtNuevoUsuario.getText().toString();
            String contrasena = txtNuevaContrasena.getText().toString();
            String confContrasena = txtNuevaConfirmContrasena.getText().toString();
            boolean permAdmin = chPAdmin.isChecked();
            boolean permRegistratTerapia = chPDarTerapia.isChecked();



            boolean datosValidos = true;

            if(nombre.trim().length()==0){
                datosValidos=false;
                txtNuevoNombre.setError("El nombre no puede estar vacío");
            }

            if(usuario.trim().length()==0){
                datosValidos = false;
                txtNuevoUsuario.setError("Usuario no válido");
            }

            if(contrasena.length()<8 || contrasena.length()>32){
                datosValidos=false;
                txtNuevaContrasena.setError("Contraseña no válida. Longitud mínima 8, máxima 32");
            }

            if(!contrasena.equals(confContrasena)){
                datosValidos=false;
                txtNuevaContrasena.setError("Las contraseñas no coinciden");
                txtNuevaConfirmContrasena.setError("Las contraseñas no coinciden");
            }


            if(datosValidos){
                contenedorprogress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                statusRegistro.setText("Espere porfavor");
                statusRegistro.requestFocus();
                statusRegistro.setTextColor(Color.BLACK);
                btnGuardarNuevoTerapeuta.setEnabled(false);

                String url = getHostServer(context)+SUF_REGISTRAR_TERAPEUTA;
                HashMap<String,String> hmap = new HashMap<String, String>();
                hmap.put(KEY_NOMBRE_USUARIO_LOGEADO, nombre);
                hmap.put(KEY_USUARIO_USUARIO_LOGEADO, usuario);
                hmap.put(KEY_CONTRASENA_USUARIO_LOGEADO, MD5(contrasena));
                hmap.put(KEY_ESADMIN_USUARIO_LOGEADO,permAdmin?"1":"0");
                hmap.put(KEY_PERMISO_USUARIO_LOGEADO,permRegistratTerapia?"1":"0");
                JSONObject obj = new JSONObject(hmap);

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            btnGuardarNuevoTerapeuta.setEnabled(true);
                            try{
                                if(response.getInt("estado")==0){
                                    agregarTerapeutaALista(context,usuario);
                                    Toast.makeText(context, "Terapeuta registrado", Toast.LENGTH_LONG).show();
                                    statusRegistro.setText("Terapeuta registrado");
                                    statusRegistro.setTextColor(Color.BLUE);
                                    progressBar.setVisibility(View.GONE);
                                    txtNuevoNombre.setText("");
                                    txtNuevoUsuario.setText("");
                                    txtNuevaContrasena.setText("");
                                    txtNuevaConfirmContrasena.setText("");
                                    chPAdmin.setChecked(false);
                                    chPDarTerapia.setChecked(false);
                                    contenedorprogress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            contenedorprogress.setVisibility(View.GONE);
                                        }
                                    },3000);
                                    contenedorprogress.requestFocus();

                                }else{
                                    contenedorprogress.requestFocus();
                                    Toast.makeText(context, response.getString("mensaje"), Toast.LENGTH_LONG).show();
                                    statusRegistro.setText(response.getString("mensaje"));
                                    statusRegistro.setTextColor(Color.RED);
                                    progressBar.setVisibility(View.GONE);
                                    contenedorprogress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            contenedorprogress.setVisibility(View.GONE);
                                        }
                                    },4000);
                                }
                            }catch(Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(context, "Error al obtener la respuesta del servidor", Toast.LENGTH_LONG).show();
                                contenedorprogress.requestFocus();
                                statusRegistro.setText("Error al obtener la respuesta del servidor");
                                statusRegistro.setTextColor(Color.RED);
                                progressBar.setVisibility(View.GONE);
                                contenedorprogress.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        contenedorprogress.setVisibility(View.GONE);
                                    }
                                },2000);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            btnGuardarNuevoTerapeuta.setEnabled(true);
                            contenedorprogress.requestFocus();
                            statusRegistro.setText(parsearError(error));
                            statusRegistro.setTextColor(Color.RED);
                            progressBar.setVisibility(View.GONE);
                            contenedorprogress.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    contenedorprogress.setVisibility(View.GONE);
                                }
                            },2000);

                        }
                    }
                ));

            }else{
                Toast.makeText(context, "Tienes errores en los datos del formulario",Toast.LENGTH_SHORT).show();
            }

            }
        });
        dialog.show();
    }

}
