package com.swyam.fisiomer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entidad.Tratamiento;
import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_AUTOESTIRAMIENTO;
import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_FORTALECIMIENTO;
import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_VENDAJE;
import static com.swyam.fisiomer.Connection.abrirDialogoSeleccionarMusculo;
import static com.swyam.fisiomer.Helpers.*;
import static entidad.Tratamiento.obtenerIndiceMusculo;

public class PreventTreatmentActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;

    // Elementos para el manejo de las pestañas
    Button btnVen, btnFort, btnAuto;
    TextView tvVen, tvFort, tvAuto;
    View contVen, contFort, contAuto;
    int tabSeleccionado=0;

    // Elementos para pestaña vendaje
    View contVenEstabilizarLinfatico, contVenFacilitarRelajar;
    RadioButton rFacilitar, rRelajar, rLinfatico, rEstabilizar;
    Spinner spArticulaciones;
    Button btnVenSelMus,btnAgregarELTratamiento, btnAgregarRFTratamiento;
    TextView tvNombreMusculoVendaje;

    // Elementos para pestaña fortalecimiento
    RadioButton rConcentrico, rExcentrico;
    Button btnFortaSelMus, btnAgregarFortTratamiento;
    TextView tvNombreMusculoFort;

    // Elementos para pestaña autoestiramiento
    Button btnAutoSelMus, btnAgregarAutoTratamiento;
    TextView tvNombreMusculoAuto;


    // Elementos para la lista
    RecyclerView rv;
    LinearLayoutManager llm;
    RVTPAdapter adapter;
    List<TratamientoPreventivo> tps = new ArrayList<>();

    // para vista musculos
    ArrayAdapter<String> mAdapter;
    String lastSearchFilter="";

    // variables para control del total y limites de seleccion de tratamientos
    int totalVendSel = 0;
    int totalFortSel = 0;
    int totalAutoEst = 0;

    int indexMuscVend=-1, indexMuscFort=-1, indexMuscAuto=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevent_treatment);
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

        // Obtenemos datos de la actividad anterior
        String nombrePaciente = getIntent().getStringExtra("paciente");
        getSupportActionBar().setSubtitle(nombrePaciente);
        tps = (List<TratamientoPreventivo>) getIntent().getSerializableExtra("tprevs");
        actualizarTotales();

        // inicializamos elementos para el manejo de las pestañas
        context = getBaseContext();
        btnVen = (Button) findViewById(R.id.btn_tab_vendaje);
        btnFort = (Button) findViewById(R.id.btn_tab_fortalecimiento);
        btnAuto = (Button) findViewById(R.id.btn_tab_autoestiramiento);
        tvVen = (TextView) findViewById(R.id.tv_barra_vendaje);
        tvFort = (TextView) findViewById(R.id.tv_barra_fortalecimiento);
        tvAuto = (TextView) findViewById(R.id.tv_barra_autoestiramiento);
        contVen = findViewById(R.id.contenedor_ll_opciones_vendaje);
        contFort = findViewById(R.id.contenedor_ll_opciones_fortalecimiento);
        contAuto = findViewById(R.id.contenedor_ll_opciones_autoestiramiento);
        lastSearchFilter="";

        // inicializamos elementos correspondientes a la pestaña vendajes
        contVenEstabilizarLinfatico = findViewById(R.id.contenedor_ll_opciones_vendaje_estabilizar_o_linfatico);
        contVenFacilitarRelajar = findViewById(R.id.contenedor_ll_opciones_vendaje_facilitar_o_relajar);
        rFacilitar = (RadioButton) findViewById(R.id.radio_facilitar);
        rRelajar = (RadioButton) findViewById(R.id.radio_relajar);
        rLinfatico = (RadioButton) findViewById(R.id.radio_linfatico);
        rEstabilizar = (RadioButton) findViewById(R.id.radio_estabilizar);
        spArticulaciones = (Spinner) findViewById(R.id.spinner_vendaje_estabilizar_o_linfatico_articulacion);
        tvNombreMusculoVendaje = (TextView) findViewById(R.id.tv_nombre_musculo_seleccionado_facilitar_o_relajar);
        btnVenSelMus = (Button) findViewById(R.id.btn_seleccionar_musculo_vendaje_fac_rel);
        btnAgregarELTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_vendaje_estabilizar_o_linfatico);
        btnAgregarRFTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_vendaje_facilitar_o_relajar);

        // inicializamos elementos correspondientes a la pestaña fortalecimiento
        btnFortaSelMus = (Button) findViewById(R.id.btn_fortalecimiento_seleccionar_musculo);
        btnAgregarFortTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_fortalecimiento);
        rConcentrico = (RadioButton) findViewById(R.id.radio_concentrico);
        rExcentrico = (RadioButton) findViewById(R.id.radio_excentrico);
        tvNombreMusculoFort = (TextView) findViewById(R.id.tv_nombre_musculo_seleccionado_fortalecimiento);

        // inicializamos elementos correspondientes a la pestaña autoestiramiento
        btnAutoSelMus = (Button) findViewById(R.id.btn_autoestiramiento_seleccionar_musculo);
        btnAgregarAutoTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_autoestiramiento);
        tvNombreMusculoAuto = (TextView) findViewById(R.id.tv_nombre_musculo_seleccionado_autoestiramiento);

        // cargamos la lista de musculos y lo asociamos al spinner de articulaciones
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Tratamiento.lMusculos); //ArrayAdapter.createFromResource(this, R.array.str_musculos, android.R.layout.simple_list_item_1);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Tratamiento.lArticulacion);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        spArticulaciones.setAdapter(spinnerAdapter);

        // inicializamos los elementos encargados de manejar la lista
        rv = (RecyclerView) findViewById(R.id.rv_tratamientos_preventivos);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        adapter = new RVTPAdapter(context, tps,true);
        rv.setAdapter(adapter);

        // El primer tab esta seleccionado predeterminadamente
        btnVen.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
        tvVen.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
        contVen.setVisibility(View.VISIBLE);
        tabSeleccionado = 0;

        // asignar callbacks
        btnVen.setOnClickListener(this);
        btnFort.setOnClickListener(this);
        btnAuto.setOnClickListener(this);
        rFacilitar.setOnClickListener(this);
        rEstabilizar.setOnClickListener(this);
        rLinfatico.setOnClickListener(this);
        rRelajar.setOnClickListener(this);
        btnAutoSelMus.setOnClickListener(this);
        btnFortaSelMus.setOnClickListener(this);
        btnVenSelMus.setOnClickListener(this);
        btnAgregarELTratamiento.setOnClickListener(this);
        btnAgregarRFTratamiento.setOnClickListener(this);
        btnAgregarFortTratamiento.setOnClickListener(this);
        btnAgregarAutoTratamiento.setOnClickListener(this);

        // manejamos los eventos en el recyclerview
        adapter.setOnItemClickListenerT(new OnItemClickListenerT() {
            @Override
            public void onItemClickTF(int tratamiento,int position) { //no se usa

            }

            @Override
            public void onItemClickTP(int tratamiento, int position) {
                adapter.removeAt(position);
                switch(tratamiento){
                    case 1:
                        totalVendSel--;
                        break;
                    case 2:
                        totalFortSel--;
                        break;
                    case 3:
                        totalAutoEst--;
                        break;
                }
            }

            @Override
            public void onItemClickTA(int tratamiento, int position){

            }
        });

    }

    @Override
    public void onBackPressed(){
        regresar();
    }


    @Override
    public void onClick(View v){
        int id = v.getId();
        //Toast.makeText(this,"Click",Toast.LENGTH_SHORT).show();
        switch(id){
            case R.id.btn_tab_vendaje:
                cambiarATab(0);
                break;
            case R.id.btn_tab_fortalecimiento:
                cambiarATab(1);
                break;
            case R.id.btn_tab_autoestiramiento:
                cambiarATab(2);
                break;
            case R.id.radio_facilitar:
            case R.id.radio_relajar:
            case R.id.radio_estabilizar:
            case R.id.radio_linfatico:
                mostrarOpcionVendaje();
                break;
            case R.id.btn_autoestiramiento_seleccionar_musculo:
            case R.id.btn_fortalecimiento_seleccionar_musculo:
            case R.id.btn_seleccionar_musculo_vendaje_fac_rel:

                abrirDialogoSeleccionarMusculo(this, lastSearchFilter, mAdapter, new OnDialogMusculo() {
                    @Override
                    public void OnMusculoSeleccionado(String musculo) {
                        musculoSeleccionado(obtenerIndiceMusculo(musculo));
                    }

                    @Override
                    public void OnBtnFilter(String filter) {
                        lastSearchFilter = filter;
                    }

                    @Override
                    public void OnSoftKeyFilter(String filter) {
                        lastSearchFilter = filter;
                    }
                });
                break;
            case R.id.btn_agregar_tratamiento_vendaje_facilitar_o_relajar:
            case R.id.btn_agregar_tratamiento_vendaje_estabilizar_o_linfatico:
            case R.id.btn_agregar_tratamiento_fortalecimiento:
            case R.id.btn_agregar_tratamiento_autoestiramiento:
                prepararTratamientoCorrespondiente();
                break;

        }
    }

    public void prepararTratamientoCorrespondiente(){
        switch(tabSeleccionado){
            case 0:
                crearTratamientoVendaje();
                break;
            case 1:
                crearTratamientoFortalecimiento();
                break;
            case 2:
                crearTratamientoAutoEstiramiento();
                break;
        }
    }

    public void intentarAgregarTratamiento(TratamientoPreventivo tp){
        if(tp.tratamiento<1 || tp.tratamiento>3)
        {
            Toast.makeText(context,"No se puede agregar un tratamiento vacío",Toast.LENGTH_SHORT).show();
            return;
        }
        String strSuf="una articulación";
        if(tp.tipo==2 || tp.tipo==3 || tp.tipo==5 || tp.tipo==6)
        {
            strSuf = "un músculo";
        }

        if(tp.articulacionMusculo<0)
        {
            Toast.makeText(context,"Debes seleccionar "+strSuf,Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context,"Agregado",Toast.LENGTH_SHORT).show();
        tps.add(tp);
        adapter.notifyItemInserted(tps.size()-1);
        switch(tp.tratamiento){
            case 1:
                totalVendSel++;

                break;
            case 2:
                totalFortSel++;
                break;
            case 3:
                totalAutoEst++;

        }
        Helpers.esconderTeclado(this);


    }

    public void crearTratamientoVendaje(){
        if(totalVendSel>=MAX_TRATAMIENTO_VENDAJE)
        {
            Toast.makeText(context,"No puedes agregar más tratamientos de 'vendaje'",Toast.LENGTH_SHORT).show();
            return;
        }

        int tratamiento = 1;
        int tipo = 0;

        if(rEstabilizar.isChecked())
            tipo = 1;
        else if(rFacilitar.isChecked())
            tipo =2;
        else if(rRelajar.isChecked())
            tipo = 3;
        else if(rLinfatico.isChecked())
            tipo = 4;

        int articulacionMusculo = 0;
        if(rEstabilizar.isChecked() || rLinfatico.isChecked())
            articulacionMusculo = spArticulaciones.getSelectedItemPosition();
        else if(rFacilitar.isChecked() || rRelajar.isChecked())
            articulacionMusculo = indexMuscVend;

        TratamientoPreventivo tp = new TratamientoPreventivo(tratamiento,tipo,articulacionMusculo);
        intentarAgregarTratamiento(tp);
    }

    public void crearTratamientoFortalecimiento(){
        if(totalFortSel>=MAX_TRATAMIENTO_FORTALECIMIENTO){
            Toast.makeText(context,"No puedes agregar más tratamientos de 'fortalecimiento'",Toast.LENGTH_SHORT).show();
            return;
        }

        int tratamiento = 2;
        int tipo = (rConcentrico.isChecked())?5:6;
        int articulacionMusculo = indexMuscFort;

        TratamientoPreventivo tp = new TratamientoPreventivo(tratamiento,tipo,articulacionMusculo);
        intentarAgregarTratamiento(tp);
    }

    public void crearTratamientoAutoEstiramiento(){
        if(totalAutoEst>=MAX_TRATAMIENTO_AUTOESTIRAMIENTO){
            Toast.makeText(context,"No puedes agregar más tratamientos de 'autoestiramiento'",Toast.LENGTH_SHORT).show();
            return;
        }

        int tratamiento = 3;
        int tipo = 7;
        int articulacionMusculo = indexMuscAuto;
        TratamientoPreventivo tp = new TratamientoPreventivo(tratamiento,tipo,articulacionMusculo);
        intentarAgregarTratamiento(tp);
    }

    public void musculoSeleccionado(int id){

        switch(tabSeleccionado){
            case 0: // vendaje
                if(totalVendSel<MAX_TRATAMIENTO_VENDAJE){
                    tvNombreMusculoVendaje.setText(Tratamiento.strMusculo(id));
                    indexMuscVend =id;
                }else{
                    Toast.makeText(context,"No puedes agregar más tratamientos de 'vendaje'",Toast.LENGTH_SHORT).show();
                }

                break;
            case 1: // fortalecimiento
                if(totalFortSel<MAX_TRATAMIENTO_FORTALECIMIENTO){
                    tvNombreMusculoFort.setText(Tratamiento.strMusculo(id));
                    indexMuscFort=id;
                }else{
                    Toast.makeText(context,"No puedes agregar más tratamientos de 'fortalecimiento'",Toast.LENGTH_SHORT).show();
                }

                break;
            case 2: // autoestiramiento
                if(totalAutoEst<MAX_TRATAMIENTO_AUTOESTIRAMIENTO){
                    tvNombreMusculoAuto.setText(Tratamiento.strMusculo(id));
                    indexMuscAuto=id;
                }else{
                    Toast.makeText(context,"No puedes agregar más tratamientos de 'autoestiramiento'",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public void mostrarOpcionVendaje(){
        if((rFacilitar.isChecked() || rRelajar.isChecked()) && contVenFacilitarRelajar.getVisibility()==View.GONE){
            contVenEstabilizarLinfatico.setVisibility(View.GONE);
            contVenFacilitarRelajar.setVisibility(View.VISIBLE);
        }else if((rEstabilizar.isChecked() || rLinfatico.isChecked()) && contVenEstabilizarLinfatico.getVisibility()==View.GONE){
            contVenEstabilizarLinfatico.setVisibility(View.VISIBLE);
            contVenFacilitarRelajar.setVisibility(View.GONE);
        }
    }

    public void regresar(){

        Intent intent = getIntent();
        intent.putExtra("tprevs",(Serializable)tps);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void coloresOculto(){
        btnVen.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvVen.setBackgroundColor(getColor2(context,android.R.color.transparent));

        btnFort.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvFort.setBackgroundColor(getColor2(context,android.R.color.transparent));

        btnAuto.setBackgroundColor(getColor2(context,R.color.colorPrimary));
        tvAuto.setBackgroundColor(getColor2(context,android.R.color.transparent));
    }

    private void ocultarVistas(){
        contVen.setVisibility(View.GONE);
        contFort.setVisibility(View.GONE);
        contAuto.setVisibility(View.GONE);
    }

    public void cambiarATab(int tab){
        if(tabSeleccionado!=tab){
            coloresOculto();
            ocultarVistas();
            switch(tab){
                case 0:

                    btnVen.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvVen.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contVen.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    btnFort.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvFort.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contFort.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    btnAuto.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    tvAuto.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
                    contAuto.setVisibility(View.VISIBLE);
                    break;
            }
            tabSeleccionado=tab;

        }
    }

    public void actualizarTotales(){
        if(tps.size()>0){
            for(TratamientoPreventivo tp:tps){
                if(tp.tratamiento==1)
                    totalVendSel++;
                else if(tp.tratamiento==2)
                    totalFortSel++;
                else if(tp.tratamiento==3)
                    totalAutoEst++;
            }
        }
    }

}
