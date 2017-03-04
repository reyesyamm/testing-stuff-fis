package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entidad.Paciente;
import entidad.Tratamiento;


public class PatientActivity extends AppCompatActivity {

    TextView nombrePaciente;
    ProgressDialog progressDialog;
    RecyclerView rv;
    LinearLayoutManager llm;
    Context context;
    SimpleDateFormat format;
    RVTAdapter adapter;
    List<Tratamiento> tratamientos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
        context = getBaseContext();
        nombrePaciente = (TextView) findViewById(R.id.tv_nombre_paciente);
        rv = (RecyclerView) findViewById(R.id.my_rv);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Obteniendo datos");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        String idPaciente = getIntent().getStringExtra("id");
        progressDialog.show();

        cargarDatosPaciente(idPaciente);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientActivity.this, NewTreatmentActivity.class);
                startActivity(intent);
            }
        });


        cargarDatos();
        llenarRV();
        progressDialog.dismiss();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Paciente item) {
                // no se utiliza
            }

            @Override
            public void onItemClick(Tratamiento t){
                Toast.makeText(context,"Has seleccionado al tratamiento realizado por "+t.nombreTerapeuta,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void cargarDatosPaciente(String id){
        Date hoy = new Date();
        tratamientos.add(new Tratamiento(1,"Juan Perez",format.format(hoy),"1 Breve resumen de lo que podria estar aqui. Este se supone será un texto relativamente largo para desplegarse hasta en mínimo 3 lineas"));
        tratamientos.add(new Tratamiento(2,"Mario Casas",format.format(hoy),"2 Breve resumen de lo que podria estar aqui. Este se supone será un texto relativamente largo para desplegarse hasta en mínimo 3 lineas"));
        tratamientos.add(new Tratamiento(3,"Carlos Sanchez",format.format(hoy),"3 Breve resumen de lo que podria estar aqui. Este se supone será un texto relativamente largo para desplegarse hasta en mínimo 3 lineas"));
        tratamientos.add(new Tratamiento(4,"Juanito Escarcha",format.format(hoy),"4 Breve resumen de lo que podria estar aqui. Este se supone será un texto relativamente largo para desplegarse hasta en mínimo 3 lineas"));
        tratamientos.add(new Tratamiento(5,"Juanito Escarcha",format.format(hoy),"5 Breve resumen de lo que podria estar aqui. Este se supone será un texto relativamente largo para desplegarse hasta en mínimo 3 lineas"));

    }

    public void cargarDatos(){

    }

    public void llenarRV(){
        adapter = new RVTAdapter(tratamientos);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paciente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_detalles_paciente) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
