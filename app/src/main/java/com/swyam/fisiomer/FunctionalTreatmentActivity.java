package com.swyam.fisiomer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import entidad.Tratamiento;
import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_FIB;
import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_PS;
import static com.swyam.fisiomer.Connection.MAX_TRATAMIENTO_TM;
import static com.swyam.fisiomer.Helpers.*;
import static entidad.Tratamiento.obtenerIndiceMusculo;

public class FunctionalTreatmentActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnPS,btnFib, btnTM;
    TextView tvPS, tvFib, tvTM;
    View contPS, contFib, contTM;
    int tabSeleccionado=0;
    RVTFAdapter adapter;
    LinearLayoutManager llm;
    RecyclerView rv;

    //  radios para tfuncionales, tfibrolisis, ttm
    RadioButton rMantenida, rES, rEstiramiento,rMovilizacion;


    // para seleccionar musculo
    Button btnSelMusPS, btnSelMusFib, btnSelMusTM;
    TextView tvMusPS, tvMusFib, tvMusTM;
    String lastFilter="";
    ArrayAdapter<String> mAdapter;
    int totalMusculosPSSeleccionados = 0;
    int totalMusculosFibSeleccionados = 0;
    int totalMusculosTMSeleccionados = 0;
    int indexMuscPS = -1, indexMuscFib=-1,indexMuscTM=-1;

    // para seleccionar caritas
    ImageButton[] btnEstados = new ImageButton[12];

    // para tm movilizacion
    EditText txtTmMovilizacion;

    // para agregar tratamientos
    Button[] btnAgregarTratamiento = new Button[3];

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                regresar();
            }
        });

        String nombrePaciente = getIntent().getStringExtra("paciente");
        getSupportActionBar().setSubtitle(nombrePaciente);
        tfs = (List<TratamientoFuncional>) getIntent().getSerializableExtra("tfuns");
        actualizarTotales();
        // inicializar variables
        context = getBaseContext();

        // radiobuttons
        rMantenida = (RadioButton) findViewById(R.id.radio_mantenida);
        rES = (RadioButton) findViewById(R.id.radio_e_s);
        rEstiramiento = (RadioButton) findViewById(R.id.radio_estiramiento);
        rMovilizacion = (RadioButton) findViewById(R.id.radio_movilizacion);

        // btns para musculos
        btnSelMusPS = (Button) findViewById(R.id.btn_ps_seleccionar_musculo);
        btnSelMusFib = (Button) findViewById(R.id.btn_fib_seleccionar_musculo);
        btnSelMusTM = (Button) findViewById(R.id.btn_tm_estiramiento_seleccionar_musculo);
        tvMusPS = (TextView) findViewById(R.id.tv_nombre_musculo_seleccionado_ps_mantenida);
        tvMusFib = (TextView) findViewById(R.id.tv_nombre_musculo_seleccionado_fibrolisis);
        tvMusTM = (TextView) findViewById(R.id.tv_nombre_musculo_seleccionado_tm_estiramiento);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Tratamiento.lMusculos);

        // botones caritas
        btnEstados[0] = (ImageButton) findViewById(R.id.btn_ps_estado_bien);
        btnEstados[1] = (ImageButton) findViewById(R.id.btn_ps_estado_mediobien);
        btnEstados[2] = (ImageButton) findViewById(R.id.btn_ps_estado_mal);
        btnEstados[3] = (ImageButton) findViewById(R.id.btn_ps_estado_muymal);

        btnEstados[4] = (ImageButton) findViewById(R.id.btn_fib_estado_bien);
        btnEstados[5] = (ImageButton) findViewById(R.id.btn_fib_estado_mediobien);
        btnEstados[6] = (ImageButton) findViewById(R.id.btn_fib_estado_mal);
        btnEstados[7] = (ImageButton) findViewById(R.id.btn_fib_estado_muymal);

        btnEstados[8] = (ImageButton) findViewById(R.id.btn_tm_estado_bien);
        btnEstados[9] = (ImageButton) findViewById(R.id.btn_tm_estado_mediobien);
        btnEstados[10] = (ImageButton) findViewById(R.id.btn_tm_estado_mal);
        btnEstados[11] = (ImageButton) findViewById(R.id.btn_tm_estado_muymal);

        //
        txtTmMovilizacion = (EditText) findViewById(R.id.txt_tm_movilizacion);

        //
        btnAgregarTratamiento[0] = (Button) findViewById(R.id.btn_ps_agregar_tratamiento);
        btnAgregarTratamiento[1] = (Button) findViewById(R.id.btn_fib_agregar_tratamiento);
        btnAgregarTratamiento[2] = (Button) findViewById(R.id.btn_tm_agregar_tratamiento);


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
        adapter = new RVTFAdapter(context,tfs,true);
        rv.setAdapter(adapter);


        adapter.setOnItemClickListener(new OnItemClickListenerT() {
            @Override
            public void onItemClickTF(int tratamiento,int position) {
                //Toast.makeText(context,"Se eliminará este tf: "+tratamiento.obtenerStrTratamiento(),Toast.LENGTH_SHORT).show();
                adapter.removeAt(position);
                switch(tratamiento){
                    case 1:
                        totalMusculosPSSeleccionados--;
                        break;
                    case 2:
                        totalMusculosFibSeleccionados--;
                        break;
                    case 3:
                        totalMusculosTMSeleccionados--;
                        break;
                }
            }

            @Override
            public void onItemClickTP(int tratamiento, int position){

            }

            @Override
            public void onItemClickTA(int tratamiento, int position){

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

        btnSelMusPS.setOnClickListener(this);
        btnSelMusFib.setOnClickListener(this);
        btnSelMusTM.setOnClickListener(this);
        btnPS.setOnClickListener(this);
        btnFib.setOnClickListener(this);
        btnTM.setOnClickListener(this);
        for(int i=0;i<12;i++)
            btnEstados[i].setOnClickListener(this);

        for(int i=0;i<3;i++)
            btnAgregarTratamiento[i].setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.btn_tab_puncion_seca:
                cambiarATab(0);
                break;
            case R.id.btn_tab_fibrolisis:
                cambiarATab(1);
                break;
            case R.id.btn_tab_puncion_tecnica_manual:
                cambiarATab(2);
                break;
            case R.id.btn_ps_seleccionar_musculo:
            case R.id.btn_fib_seleccionar_musculo:
            case R.id.btn_tm_estiramiento_seleccionar_musculo:
                abrirVentanaSeleccionMusculo();
                break;
            case R.id.btn_ps_estado_bien:
            case R.id.btn_ps_estado_mediobien:
            case R.id.btn_ps_estado_mal:
            case R.id.btn_ps_estado_muymal:
                estadoPSSeleccionado(id);
                break;
            case R.id.btn_fib_estado_bien:
            case R.id.btn_fib_estado_mediobien:
            case R.id.btn_fib_estado_mal:
            case R.id.btn_fib_estado_muymal:
                estadoFibSeleccionado(id);
                break;
            case R.id.btn_tm_estado_bien:
            case R.id.btn_tm_estado_mediobien:
            case R.id.btn_tm_estado_mal:
            case R.id.btn_tm_estado_muymal:
                estadoTMSeleccionado(id);
                break;
            case R.id.btn_ps_agregar_tratamiento:
                intentarAgregarTratamientoPS();
                break;
            case R.id.btn_fib_agregar_tratamiento:
                intentarAgregarTratamientoFib();
                break;
            case R.id.btn_tm_agregar_tratamiento:
                intentarAgregarTratamientoTM();
                break;

        }
    }

    private void validarYGuardar(boolean esPs, boolean esFib, boolean esTM, TratamientoFuncional tf){
        boolean datosValidos = true;
        if(tf.musculo==-1){
            datosValidos = false;
            Toast.makeText(context,"Selecciona el músculo porfavor",Toast.LENGTH_SHORT).show();
        }

        if(tf.estado==-1){
            if(datosValidos)
                Toast.makeText(context,"Selecciona el estado del paciente (carita)",Toast.LENGTH_SHORT).show();
            datosValidos = false;
        }

        if(tf.tratamiento==3 && tf.tipo==5 && tf.movilizacion.trim().length()==0){
            if(datosValidos)
                Toast.makeText(context,"Llena el campo descriptivo de la movilización",Toast.LENGTH_SHORT).show();
            txtTmMovilizacion.requestFocus();
            datosValidos = false;
        }

        if(datosValidos){
            Toast.makeText(context,"Agregado",Toast.LENGTH_SHORT).show();
            tfs.add(tf);
            adapter.notifyItemInserted(tfs.size()-1);

            if(tf.tratamiento==1) {
                totalMusculosPSSeleccionados++;
                indexMuscPS=-1;
            }if(tf.tratamiento==2) {
                totalMusculosFibSeleccionados++;
                indexMuscFib=-1;
            }if(tf.tratamiento==3) {
                totalMusculosTMSeleccionados++;
                indexMuscTM=-1;
            }

            switch(tf.tratamiento){
                case 1:
                    estadoPSSeleccionado(1);
                    tvMusPS.setText("No se ha seleccionado ningún músculo");
                    break;
                case 2:
                    estadoFibSeleccionado(1);
                    tvMusFib.setText("No se ha seleccionado ningún músculo");
                    break;
                case 3:
                    if(tf.tipo==4)
                        tvMusTM.setText("No se ha seleccionado ningún músculo");
                    else
                        txtTmMovilizacion.setText("");

                    estadoTMSeleccionado(1);
                    break;
            }
            Helpers.esconderTeclado(this);
        }
    }

    private void intentarAgregarTratamientoPS(){
        if(totalMusculosPSSeleccionados<MAX_TRATAMIENTO_PS){
            int tratamiento = 1;
            int tipo = (rMantenida.isChecked())?1:2;
            int musculo = indexMuscPS;
            int estado = estadoPSSel;
            String movilizacion = "";
            validarYGuardar(true,false,false,new TratamientoFuncional(tratamiento,tipo,musculo,estado,movilizacion));
        }else{
            Toast.makeText(context,"No puedes agregar más tratamientos de punción seca",Toast.LENGTH_SHORT).show();
        }
    }

    private void intentarAgregarTratamientoFib(){
        if(totalMusculosFibSeleccionados<MAX_TRATAMIENTO_FIB){
            int tratamiento = 2;
            int tipo = 3;
            int musculo = indexMuscFib;
            int estado = estadoFibSel;
            String movilizacion ="";
            validarYGuardar(true,false,false,new TratamientoFuncional(tratamiento,tipo,musculo,estado,movilizacion));
        }else{
            Toast.makeText(context,"No puedes agregar más tratamientos de fibrólisis",Toast.LENGTH_SHORT).show();
        }
    }

    private void intentarAgregarTratamientoTM(){
        if(totalMusculosTMSeleccionados<MAX_TRATAMIENTO_TM){
            int tratamiento = 3;
            int tipo = rEstiramiento.isChecked()?4:5;
            int musculo = rEstiramiento.isChecked()?indexMuscTM:0;
            int estado = estadoTMSel;
            String movilizacion = rMovilizacion.isChecked()?txtTmMovilizacion.getText().toString():"";
            validarYGuardar(true,false,false,new TratamientoFuncional(tratamiento,tipo,musculo,estado,movilizacion));
        }else{
            Toast.makeText(context,"No puedes agregar más tratamientos de técnicas manuales",Toast.LENGTH_SHORT).show();
        }
    }

    int estadoPSSel = -1;
    private void estadoPSSeleccionado(int id){
        if(estadoPSSel>=1 && estadoPSSel<=4){
            estadoPSSel = -1;
            for(int i=0;i<4;i++)
                btnEstados[i].setVisibility(View.VISIBLE);
        }else{
            for(int i=0;i<4;i++)
                btnEstados[i].setVisibility(View.GONE);
            switch(id){
                case R.id.btn_ps_estado_bien:
                    btnEstados[0].setVisibility(View.VISIBLE);
                    estadoPSSel = 1;
                    break;
                case R.id.btn_ps_estado_mediobien:
                    btnEstados[1].setVisibility(View.VISIBLE);
                    estadoPSSel = 2;
                    break;
                case R.id.btn_ps_estado_mal:
                    btnEstados[2].setVisibility(View.VISIBLE);
                    estadoPSSel = 3;
                    break;
                default:
                    btnEstados[3].setVisibility(View.VISIBLE);
                    estadoPSSel = 4;
            }
        }
    }

    int estadoFibSel = -1;
    private void estadoFibSeleccionado(int id){
        if(estadoFibSel>=1 && estadoFibSel<=4){
            estadoFibSel = -1;
            for(int i=4;i<8;i++)
                btnEstados[i].setVisibility(View.VISIBLE);
        }else{
            for(int i=4;i<8;i++)
                btnEstados[i].setVisibility(View.GONE);
            switch(id){
                case R.id.btn_fib_estado_bien:
                    btnEstados[4].setVisibility(View.VISIBLE);
                    estadoFibSel = 1;
                    break;
                case R.id.btn_fib_estado_mediobien:
                    btnEstados[5].setVisibility(View.VISIBLE);
                    estadoFibSel = 2;
                    break;
                case R.id.btn_fib_estado_mal:
                    btnEstados[6].setVisibility(View.VISIBLE);
                    estadoFibSel = 3;
                    break;
                default:
                    btnEstados[7].setVisibility(View.VISIBLE);
                    estadoFibSel = 4;
            }
        }
    }

    int estadoTMSel = -1;
    private void estadoTMSeleccionado(int id){
        if(estadoTMSel>=1 && estadoTMSel<=4){
            estadoTMSel = -1;
            for(int i=8;i<12;i++)
                btnEstados[i].setVisibility(View.VISIBLE);
        }else{
            for(int i=8;i<12;i++)
                btnEstados[i].setVisibility(View.GONE);
            switch(id){
                case R.id.btn_tm_estado_bien:
                    btnEstados[8].setVisibility(View.VISIBLE);
                    estadoTMSel = 1;
                    break;
                case R.id.btn_tm_estado_mediobien:
                    btnEstados[9].setVisibility(View.VISIBLE);
                    estadoTMSel = 2;
                    break;
                case R.id.btn_tm_estado_mal:
                    btnEstados[10].setVisibility(View.VISIBLE);
                    estadoTMSel = 3;
                    break;
                default:
                    btnEstados[11].setVisibility(View.VISIBLE);
                    estadoTMSel = 4;
            }
        }
    }

    @Override
    public void onBackPressed(){
        regresar();
    }

    private void abrirVentanaSeleccionMusculo(){
        Connection.abrirDialogoSeleccionarMusculo(this, lastFilter, mAdapter, new OnDialogMusculo() {
            @Override
            public void OnMusculoSeleccionado(String StrMusculo) {

                switch(tabSeleccionado){
                    case 0: // se trata de seleccionar musculo para tratamiento funcional
                        if(totalMusculosPSSeleccionados<MAX_TRATAMIENTO_PS){
                            tvMusPS.setText("Músculo: "+StrMusculo);
                            indexMuscPS= obtenerIndiceMusculo(StrMusculo);
                        }else{
                            Toast.makeText(context, "No puedes agregar más tratamientos de punción seca",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 1: // se trata de seleccionar musculo para tratamiento fibrolisis
                        if(totalMusculosFibSeleccionados<MAX_TRATAMIENTO_FIB){
                            tvMusFib.setText("Músculo: "+StrMusculo);
                            indexMuscFib = obtenerIndiceMusculo(StrMusculo);
                        }else{
                            Toast.makeText(context, "No puedes agregar más tratamientos de fibrólisis",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2: // se trata de seleccionar musculo para tratamiento tecnica manual
                        if(totalMusculosTMSeleccionados<MAX_TRATAMIENTO_TM){
                            tvMusTM.setText("Músculo: "+ StrMusculo);
                            indexMuscTM=obtenerIndiceMusculo(StrMusculo);
                        }else{
                            Toast.makeText(context, "No puedes agregar más tratamientos de técnica manual",Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }

            @Override
            public void OnBtnFilter(String filter) {
                lastFilter = filter;
            }

            @Override
            public void OnSoftKeyFilter(String filter) {
                lastFilter = filter;
            }
        });
    }

    public void regresar(){
        Intent intent = getIntent();
        intent.putExtra("tfuns",(Serializable) tfs);
        setResult(RESULT_OK, intent);
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

    public void actualizarTotales(){
        if(tfs.size()>0){
            for(TratamientoFuncional tf:tfs){
                if(tf.tratamiento == 1)
                    totalMusculosPSSeleccionados++;
                else if(tf.tratamiento == 2)
                    totalMusculosFibSeleccionados++;
                else if(tf.tratamiento == 3)
                    totalMusculosTMSeleccionados++;
            }
        }
    }
}
