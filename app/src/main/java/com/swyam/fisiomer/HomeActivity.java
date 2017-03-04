package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvNombreTerapeuta;
    EditText txtBuscarPaciente;
    Button btnBuscarPaciente;
    TextInputLayout til;
    ProgressDialog progressDialog;
    ImageButton btnLimpiar;

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

        cargarNombreTerapeuta();


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
        }else if(id==R.id.nav_pacientes){

        }else if(id==R.id.nav_manage){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        //tvNombreTerapeuta.setText("");
    }
}
