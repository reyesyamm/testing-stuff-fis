package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entidad.Paciente;
import entidad.Tratamiento;

public class PatientsFoundActivity extends AppCompatActivity {


    TextView total_pacientes;
    TextView busqueda_realizada;
    RecyclerView rv;
    LinearLayoutManager llm;
    RVPAdapter adapter;
    List<Paciente> pacientes = new ArrayList<>();
    SimpleDateFormat format;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_found);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Buscando");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        total_pacientes = (TextView) findViewById(R.id.tv_total_pacientes_encontrados);
        busqueda_realizada = (TextView) findViewById(R.id.tv_busqueda_realizada);
        context = getBaseContext();
        llm = new LinearLayoutManager(context);
        rv = (RecyclerView) findViewById(R.id.my_rv);
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        progressDialog.show();

        String busqueda = getIntent().getStringExtra("busqueda");
        busqueda_realizada.setText(busqueda);

        cargarDatos();
        llenarRV();
        progressDialog.dismiss();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Paciente item) {
                Toast.makeText(context,"Has seleccionado al paciente "+item.nombre,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(Tratamiento t){
                // no se utiliza
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean change){
        super.onWindowFocusChanged(change);

    }

    public void cargarDatos(){
        Date hoy = new Date();
        pacientes.add(new Paciente(1,"Carlos Pech",23,format.format(hoy),"Juan Perez"));
        pacientes.add(new Paciente(1,"Maria Sanchez",50,format.format(hoy),"Juan Perez"));
        pacientes.add(new Paciente(1,"Laura Peniche",26,format.format(hoy),"Margarita Santana"));
        pacientes.add(new Paciente(1,"Andrea Manzanero",48,format.format(hoy),"Margarita Santana"));
        pacientes.add(new Paciente(1,"José Mendoza",25,format.format(hoy),"Andres Marquez"));
    }

    public void llenarRV(){
        adapter = new RVPAdapter(pacientes);
        rv.setAdapter(adapter);
    }

}
