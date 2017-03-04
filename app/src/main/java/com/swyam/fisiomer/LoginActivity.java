package com.swyam.fisiomer;

import android.app.ActionBar;
import android.app.ProgressDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.swyam.fisiomer.Helpers.*;

public class LoginActivity extends AppCompatActivity {

    HashMap<String, Integer> terapeutas = new HashMap<>();
    List<String> listaTerapeutas =  new ArrayList<String>();
    Spinner sp_lista_terapeutas;
    EditText txt_contrasena_terapeuta;
    Button btn_iniciar_sesion;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // Inicializando las variables
        sp_lista_terapeutas = (Spinner) findViewById(R.id.spinner_lista_terapeutas);
        txt_contrasena_terapeuta = (EditText) findViewById(R.id.edit_text_contrasena_terapeuta);
        btn_iniciar_sesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Cargando Datos");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // en este apartado verificaremos si existe una sesion iniciada
        if(false){
            redireccionarHogar();
        }else{
            // lo primer es cargar los terapeutas en el spinner
            cargarTerapeutas();
        }

        btn_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicion = sp_lista_terapeutas.getSelectedItemPosition();
                if(posicion>0){
                    String usuario = sp_lista_terapeutas.getSelectedItem().toString();
                    String contrasena = txt_contrasena_terapeuta.getText().toString();

                    if(contrasena.length()>7){
                        progressDialog.setTitle("Verificando Credenciales");
                        progressDialog.setMessage("Espere porfavor");
                        progressDialog.show();
                        contrasena = MD5(contrasena);
                        boolean resultado =credencialesValidas(usuario,contrasena);
                        progressDialog.dismiss();
                        if(resultado){
                            sp_lista_terapeutas.setSelection(0);
                            txt_contrasena_terapeuta.setText("");
                            redireccionarHogar();
                        }else{
                            Toast.makeText(getBaseContext(),"Datos incorrectos", Toast.LENGTH_SHORT).show();
                        }
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
        sp_lista_terapeutas.setFocusable(true);

    }

    private void redireccionarHogar(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void cargarTerapeutas(){
        listaTerapeutas.add("Selecciona");
        listaTerapeutas.add(Upper("Juan"));
        listaTerapeutas.add(Upper("Carlos"));

        progressDialog.dismiss();
        if(listaTerapeutas.size()==1){
            Toast.makeText(this,"No existen terapeutas registrados",Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> adapterListaTerapeutas = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,listaTerapeutas);
        adapterListaTerapeutas.setDropDownViewResource(R.layout.spinner_item);
        sp_lista_terapeutas.setAdapter(adapterListaTerapeutas);
    }

    public boolean credencialesValidas(String usuario,String contrasena){


        return true;
    }




}
