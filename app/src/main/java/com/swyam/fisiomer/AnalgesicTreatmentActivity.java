package com.swyam.fisiomer;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Helpers.getColor2;

public class AnalgesicTreatmentActivity extends AppCompatActivity implements  View.OnClickListener {

    Context context;
    RecyclerView rv;
    LinearLayoutManager llm;
    RVTAAdapter adapter;
    List<TratamientoAnalgesico> tas = new ArrayList<>();


    Button btnElectro, btnUltrasonido, btnLasser;
    TextView tvElectro,tvUltrasonido, tvLasser;
    View contElectro, contUltrasonido, contLasser;


    Button btnAgregarTElectro, btnAgregarTUltrasonido, btnAgregarTLasser;

    int tabSeleccionado=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analgesic_treatment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setSubtitle("Nombre Paciente Tratamiento");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });

        context = getBaseContext();
        btnElectro = (Button) findViewById(R.id.btn_tab_electro);
        btnUltrasonido = (Button) findViewById(R.id.btn_tab_ultrasonido);
        btnLasser = (Button) findViewById(R.id.btn_tab_lasser);
        tvElectro = (TextView) findViewById(R.id.tv_barra_electro);
        tvUltrasonido = (TextView) findViewById(R.id.tv_barra_ultrasonido);
        tvLasser = (TextView) findViewById(R.id.tv_barra_lasser);
        contElectro = findViewById(R.id.contenedor_ll_opciones_electro);
        contUltrasonido = findViewById(R.id.contenedor_ll_opciones_ultrasonido);
        contLasser = findViewById(R.id.contenedor_ll_opciones_lasser);

        btnAgregarTElectro = (Button) findViewById(R.id.btn_agregar_tratamiento_electro);
        btnAgregarTUltrasonido = (Button) findViewById(R.id.btn_agregar_tratamiento_ultrasonido);
        btnAgregarTLasser = (Button) findViewById(R.id.btn_agregar_tratamiento_lasser);


        rv = (RecyclerView) findViewById(R.id.rv_tratamientos_analgesicos);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        llenarLista();

        adapter = new RVTAAdapter(context, tas);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListenerT(new OnItemClickListenerT() {
            @Override
            public void onItemClickTF(TratamientoFuncional tratamiento) {

            }

            @Override
            public void onItemClickTP(TratamientoPreventivo tratamiento) {

            }

            @Override
            public void onItemClickTA(TratamientoAnalgesico tratamiento) {
                Toast.makeText(context, "Tratamiento: "+tratamiento.tratamiento,Toast.LENGTH_SHORT).show();
            }
        });

        btnLasser.setOnClickListener(this);
        btnUltrasonido.setOnClickListener(this);
        btnElectro.setOnClickListener(this);
        btnAgregarTElectro.setOnClickListener(this);
        btnAgregarTLasser.setOnClickListener(this);
        btnAgregarTUltrasonido.setOnClickListener(this);

        cambiarATab(0);
    }


    @Override
    public void onClick(View v){
        int id = v.getId();
        switch(id){
            case R.id.btn_tab_electro:
                cambiarATab(0);
                break;
            case R.id.btn_tab_ultrasonido:
                cambiarATab(1);
                break;
            case R.id.btn_tab_lasser:
                cambiarATab(2);
                break;
            case R.id.btn_agregar_tratamiento_electro:
            case R.id.btn_agregar_tratamiento_lasser:
            case R.id.btn_agregar_tratamiento_ultrasonido:
                agregarTratamiento();
                break;
        }

    }

    public void agregarTratamiento(){
        Toast.makeText(this, "Agregar tratamiento seleccionado",Toast.LENGTH_SHORT).show();
    }

    public void llenarLista(){
        tas.add(new TratamientoAnalgesico("Electro","TENS","texto de prueba"));
        tas.add(new TratamientoAnalgesico("Ultrasonido","Pulsado - 3MHZ","5 minutos"));
        tas.add(new TratamientoAnalgesico("Lasser","Lasser","texto de prueba"));
    }

    public void regresar(){
        finish();
    }


    private void coloresOculto(){
        btnElectro.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvElectro.setBackgroundColor(getColor2(context,android.R.color.transparent));

        btnUltrasonido.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvUltrasonido.setBackgroundColor(getColor2(context,android.R.color.transparent));

        btnLasser.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvLasser.setBackgroundColor(getColor2(context,android.R.color.transparent));
    }

    private void ocultarVistas(){
        contElectro.setVisibility(View.GONE);
        contUltrasonido.setVisibility(View.GONE);
        contLasser.setVisibility(View.GONE);
    }

    public void cambiarATab(int tab){
        if(tabSeleccionado!=tab){
            coloresOculto();
            ocultarVistas();
            switch(tab){
                case 0:

                    btnElectro.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvElectro.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contElectro.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    btnUltrasonido.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvUltrasonido.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contUltrasonido.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    btnLasser.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvLasser.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contLasser.setVisibility(View.VISIBLE);
                    break;
            }
            tabSeleccionado=tab;

        }
    }

}
