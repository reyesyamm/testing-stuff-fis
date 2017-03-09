package com.swyam.fisiomer;

import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Helpers.*;
public class FunctionalTreatmentActivity extends AppCompatActivity {

    Button btnPS,btnFib, btnTM;
    TextView tvPS, tvFib, tvTM;
    View contPS, contFib, contTM;
    int tabSeleccionado=0;
    RVTFAdapter adapter;
    LinearLayoutManager llm;
    RecyclerView rv;

    RadioGroup rgTM;
    RadioButton estiramiento, movilizacion;
    View contTMEst, contTMMOV;


    List<TratamientoFuncional> tfs = new ArrayList<>();

    Context context;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functional_treatment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setSubtitle("Nombre paciente tratamiento");

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                regresar();
            }
        });

        // inicializar variables
        context = getBaseContext();
        btnPS = (Button) findViewById(R.id.btn_tab_puncion_seca);
        btnFib = (Button) findViewById(R.id.btn_tab_fibrolisis);
        btnTM = (Button) findViewById(R.id.btn_tab_puncion_tecnica_manual);
        tvPS = (TextView) findViewById(R.id.tv_barra_puncion_seca);
        tvFib = (TextView) findViewById(R.id.tv_barra_fibrolisis);
        tvTM = (TextView) findViewById(R.id.tv_barra_tecnica_manual);
        contPS = findViewById(R.id.contenedor_ll_opciones_puncion_seca);
        contFib = findViewById(R.id.contenedor_ll_opciones_fibrolisis);
        contTM = findViewById(R.id.contenedor_ll_opciones_tm);

        // El primer tab esta seleccionado predeterminadamente
        btnPS.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
        tvPS.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
        contPS.setVisibility(View.VISIBLE);
        tabSeleccionado = 0;



        // tm
        estiramiento = (RadioButton) findViewById(R.id.radio_estiramiento);
        movilizacion = (RadioButton) findViewById(R.id.radio_movilizacion);
        rgTM = (RadioGroup) findViewById(R.id.radiogroup_tm);
        contTMEst = findViewById(R.id.contenedor_opciones_tm_estiramiento);
        contTMMOV = findViewById(R.id.contenedor_opciones_tm_movilizacion);


        rv = (RecyclerView) findViewById(R.id.rv_tratamientos_funcionales);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        llenarTFS();

        adapter = new RVTFAdapter(context,tfs,true);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListenerT() {
            @Override
            public void onItemClickTF(TratamientoFuncional tratamiento) {
                Toast.makeText(context,"Se eliminará este tf: "+tratamiento.tratamiento,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClickTP(TratamientoPreventivo t){

            }

            @Override
            public void onItemClickTA(TratamientoAnalgesico t){

            }
        });


        btnPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarATab(0);
            }
        });

        btnFib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarATab(1);
            }
        });

        btnTM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarATab(2);
            }
        });

        estiramiento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    contTMEst.setVisibility(View.VISIBLE);
                    contTMMOV.setVisibility(View.GONE);
                }else if(movilizacion.isChecked()){
                    contTMMOV.setVisibility(View.VISIBLE);
                    contTMEst.setVisibility(View.GONE);
                }
            }
        });

        movilizacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    contTMMOV.setVisibility(View.VISIBLE);
                    contTMEst.setVisibility(View.GONE);
                }else if(movilizacion.isChecked()){
                    contTMEst.setVisibility(View.VISIBLE);
                    contTMMOV.setVisibility(View.GONE);
                }
            }
        });

    }

    public void llenarTFS(){
        //tfs.add(new TratamientoFuncional("Punción Seca","Breve descripcion del tratamiento",0));
        //tfs.add(new TratamientoFuncional("Fibrolisis","Breve descripcion del tratamiento",1));
        //tfs.add(new TratamientoFuncional("Punción Seca","Breve descripcion del tratamiento",2));

    }

    public void regresar(){
        finish();
    }

    private void coloresOculto(){
        btnPS.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvPS.setBackgroundColor(getColor2(context,android.R.color.transparent));

        btnFib.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvFib.setBackgroundColor(getColor2(context,android.R.color.transparent));

        btnTM.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvTM.setBackgroundColor(getColor2(context,android.R.color.transparent));
    }

    private void ocultarVistas(){
        contPS.setVisibility(View.GONE);
        contFib.setVisibility(View.GONE);
        contTM.setVisibility(View.GONE);
    }

    public void cambiarATab(int tab){
        if(tabSeleccionado!=tab){
            coloresOculto();
            ocultarVistas();
            switch(tab){
                case 0:

                    btnPS.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvPS.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contPS.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    btnFib.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvFib.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contFib.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    btnTM.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvTM.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contTM.setVisibility(View.VISIBLE);
                    break;
            }
            tabSeleccionado=tab;

        }
    }

}
