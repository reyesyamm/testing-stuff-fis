package com.swyam.fisiomer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_ELECTRO;
import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_LASSER;
import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_ULTRASONIDO;
import static com.swyam.fisiomer.Helpers.SEPARATOR_STRING_SER;
import static com.swyam.fisiomer.Helpers.getColor2;

public class AnalgesicTreatmentActivity extends AppCompatActivity implements  View.OnClickListener {

    Context context;
    RecyclerView rv;
    LinearLayoutManager llm;
    RVTAAdapter adapter;
    List<TratamientoAnalgesico> tas = new ArrayList<>();

    // limites
    int totalTElectro = 0;
    int totalTUltrasonido = 0;
    int totalTLasser = 0;

    Button btnElectro, btnUltrasonido, btnLasser;
    TextView tvElectro,tvUltrasonido, tvLasser;
    View contElectro, contUltrasonido, contLasser;

    //Para electro
    RadioButton rEMS,rTENS,rInterf;
    EditText txtDosis;

    // Para ultrasonido
    RadioButton rPulsado, rContinuo,r1MHZ,r3MHZ;
    Spinner spTiempo;

    //Para lasser
    EditText txtLasser;



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

        tas = (List<TratamientoAnalgesico>) getIntent().getSerializableExtra("tanals");
        String np = getIntent().getStringExtra("paciente");
        getSupportActionBar().setSubtitle(np);
        actualizarTotales();
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

        // electro
        rEMS = (RadioButton) findViewById(R.id.radio_ems);
        rTENS = (RadioButton) findViewById(R.id.radio_tens);
        rInterf = (RadioButton) findViewById(R.id.radio_interferencial);
        txtDosis = (EditText) findViewById(R.id.txt_electro_dosis);

        //ultrasonido
        rPulsado = (RadioButton) findViewById(R.id.radio_pulsado);
        rContinuo = (RadioButton) findViewById(R.id.radio_continuo);
        r1MHZ = (RadioButton) findViewById(R.id.radio_1mhz);
        r3MHZ = (RadioButton) findViewById(R.id.radio_3mhz);
        spTiempo = (Spinner) findViewById(R.id.spinner_ultrasonido_tiempo);

        // Lasser
        txtLasser = (EditText) findViewById(R.id.txt_lasser_lasser);


        btnAgregarTElectro = (Button) findViewById(R.id.btn_agregar_tratamiento_electro);
        btnAgregarTUltrasonido = (Button) findViewById(R.id.btn_agregar_tratamiento_ultrasonido);
        btnAgregarTLasser = (Button) findViewById(R.id.btn_agregar_tratamiento_lasser);



        rv = (RecyclerView) findViewById(R.id.rv_tratamientos_analgesicos);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        adapter = new RVTAAdapter(context, tas,true);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListenerT(new OnItemClickListenerT() {
            @Override
            public void onItemClickTF(int tratamiento,int position) {

            }

            @Override
            public void onItemClickTP(int tratamiento, int position) {

            }

            @Override
            public void onItemClickTA(int tratamiento, int position) {
                Toast.makeText(context, "Tratamiento: "+position,Toast.LENGTH_SHORT).show();
                adapter.removeAt(position);
                switch(tratamiento){
                    case 1:
                        totalTElectro--;
                        break;
                    case 2:
                        totalTUltrasonido--;
                        break;
                    case 3:
                        totalTLasser--;
                        break;
                }
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
        int tratamiento=0,tipo=0;
        String contenido="";
        TratamientoAnalgesico tratamientoAnalgesico = null;
        switch(tabSeleccionado){
            case 0: // agregar un tratamiento de tipo electro
            {
                tratamiento = 1;
                if(rEMS.isChecked())
                    tipo = 1;
                else if(rTENS.isChecked())
                    tipo = 2;
                else if(rInterf.isChecked())
                    tipo = 3;

                contenido = txtDosis.getText().toString();
            }
                break;
            case 1: // agregar un tratamiento de tipo ultrasonido
            {
                tratamiento = 2;
                if(rPulsado.isChecked())
                    tipo = 4;
                else if(rContinuo.isChecked())
                    tipo = 5;

                if(r1MHZ.isChecked())
                    contenido = "1 MHZ";
                else if(r3MHZ.isChecked())
                    contenido = "3 MHZ";

                contenido+=SEPARATOR_STRING_SER+spTiempo.getSelectedItem().toString();

            }
                break;
            case 2: // agregar un tratamiento de tipo lasser
            {
                tratamiento = 3;
                tipo = 6;
                contenido = txtLasser.getText().toString();
            }
                break;
        }

        tratamientoAnalgesico = new TratamientoAnalgesico(tratamiento,tipo,contenido);
        verificarYAgregar(tratamientoAnalgesico);
    }

    public void verificarYAgregar(TratamientoAnalgesico ta){
        if(ta.tratamiento==1 && totalTElectro>=MAX_TRATAMIENTO_ELECTRO){
            Toast.makeText(context, "No puedes agregar más tratamientos 'electro'",Toast.LENGTH_LONG).show();
            return;
        }else if(ta.tratamiento==2 && totalTUltrasonido>=MAX_TRATAMIENTO_ULTRASONIDO){
            Toast.makeText(context, "No puedes agregar más tratamientos 'ultrasonido'",Toast.LENGTH_LONG).show();
            return;
        }else if(ta.tratamiento==3 && totalTLasser>=MAX_TRATAMIENTO_LASSER){
            Toast.makeText(context, "No puedes agregar más tratamientos 'lasser'",Toast.LENGTH_LONG).show();
            return;
        }

        boolean tvalido = true;
        if(ta.tratamiento==1 && ta.contenido.trim().length()==0){
            tvalido = false;
            Toast.makeText(context,"No has indicado la dosis",Toast.LENGTH_SHORT).show();
            txtDosis.requestFocus();
        }

        if(tvalido && ta.tratamiento==3 && ta.contenido.trim().length()==0){
            tvalido = false;
            Toast.makeText(context,"No has indicado el tratamiento lasser",Toast.LENGTH_SHORT).show();
            txtLasser.requestFocus();
        }

        if(tvalido){
            Toast.makeText(context,"Agregado",Toast.LENGTH_SHORT).show();
            tas.add(ta);
            adapter.notifyItemInserted(tas.size()-1);

            if(ta.tratamiento==1){
                txtDosis.setText("");
            }

            if(ta.tratamiento==3)
                txtLasser.setText("");

            Helpers.esconderTeclado(this);
        }
    }

    @Override
    public void onBackPressed(){
        regresar();
    }

    public void regresar(){
        Intent intent = getIntent();
        intent.putExtra("tanals",(Serializable) tas);
        setResult(RESULT_OK, intent);
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

    public void actualizarTotales(){
        if(tas.size()>0){
            for(TratamientoAnalgesico ta:tas){
                if(ta.tratamiento == 1)
                    totalTElectro++;
                else if(ta.tratamiento == 2)
                    totalTUltrasonido++;
                else if(ta.tratamiento == 3)
                    totalTLasser++;
            }
        }
    }

}
