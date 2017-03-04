package com.swyam.fisiomer;

import android.app.Dialog;
import android.content.Context;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Helpers.*;

public class PreventTreatmentActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Button btnVen, btnFort, btnAuto;
    TextView tvVen, tvFort, tvAuto;
    View contVen, contFort, contAuto;

    // Views para vendaje
    View contVenEstabilizarLinfatico, contVenFacilitarRelajar;
    RadioButton rFacilitar, rRelajar, rLinfatico, rEstabilizar;

    // botones de seleccionar musculo
    Button btnAutoSelMus, btnFortaSelMus, btnVenSelMus;

    // Botones agregar tratamiento
    Button btnAgregarELTratamiento, btnAgregarRFTratamiento, btnAgregarFortTratamiento, btnAgregarAutoTratamiento;

    RecyclerView rv;
    LinearLayoutManager llm;

    int tabSeleccionado=0;
    RVTPAdapter adapter;
    List<TratamientoPreventivo> tps = new ArrayList<>();

    // para vista musculos
    ArrayAdapter<CharSequence> mAdapter;// = ArrayAdapter.createFromResource(this, R.array.str_musculos, android.R.layout.simple_list_item_1);;
    String lastSearchFilter="";


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

        mAdapter = ArrayAdapter.createFromResource(this, R.array.str_musculos, android.R.layout.simple_list_item_1);
        lastSearchFilter="";
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

        // vendaje
        contVenEstabilizarLinfatico = findViewById(R.id.contenedor_ll_opciones_vendaje_estabilizar_o_linfatico);
        contVenFacilitarRelajar = findViewById(R.id.contenedor_ll_opciones_vendaje_facilitar_o_relajar);
        rFacilitar = (RadioButton) findViewById(R.id.radio_facilitar);
        rRelajar = (RadioButton) findViewById(R.id.radio_relajar);
        rLinfatico = (RadioButton) findViewById(R.id.radio_linfatico);
        rEstabilizar = (RadioButton) findViewById(R.id.radio_estabilizar);

        // botones seleccionar musculo
        btnAutoSelMus = (Button) findViewById(R.id.btn_autoestiramiento_seleccionar_musculo);
        btnFortaSelMus = (Button) findViewById(R.id.btn_fortalecimiento_seleccionar_musculo);
        btnVenSelMus = (Button) findViewById(R.id.btn_seleccionar_musculo_vendaje_fac_rel);

        // botones agregar tratamiento
        btnAgregarELTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_vendaje_estabilizar_o_linfatico);
        btnAgregarRFTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_vendaje_facilitar_o_relajar);
        btnAgregarAutoTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_autoestiramiento);
        btnAgregarFortTratamiento = (Button) findViewById(R.id.btn_agregar_tratamiento_fortalecimiento);

        rv = (RecyclerView) findViewById(R.id.rv_tratamientos_preventivos);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);

        // El primer tab esta seleccionado predeterminadamente
        btnVen.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
        tvVen.setBackgroundColor(getColor2(context,R.color.colorPrimaryDark));
        contVen.setVisibility(View.VISIBLE);
        tabSeleccionado = 0;

        // llenar el lista
        llenarLista();

        //asignar adaptador a rv
        adapter = new RVTPAdapter(context, tps);
        rv.setAdapter(adapter);

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

        adapter.setOnItemClickListenerT(new OnItemClickListenerT() {
            @Override
            public void onItemClickTF(TratamientoFuncional tratamiento) { //no se usa

            }

            @Override
            public void onItemClickTP(TratamientoPreventivo tratamiento) {
                Toast.makeText(context, "Seleccionaste el tratamiento "+tratamiento.tratamiento,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClickTA(TratamientoAnalgesico t){

            }
        });

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
                    abrirVentanaSeleccionarMusculo();
                break;
            case R.id.btn_agregar_tratamiento_vendaje_facilitar_o_relajar:
            case R.id.btn_agregar_tratamiento_vendaje_estabilizar_o_linfatico:
            case R.id.btn_agregar_tratamiento_fortalecimiento:
            case R.id.btn_agregar_tratamiento_autoestiramiento:
                    agregarTratamientoCorrespondiente();
                break;

        }
    }

    public void agregarTratamientoCorrespondiente(){
        Toast.makeText(this,"Agregar Tratamiento en creación ",Toast.LENGTH_SHORT).show();
    }

    public void abrirVentanaSeleccionarMusculo(){
        //Toast.makeText(this,"Seleccionar musculo en creación",Toast.LENGTH_SHORT).show();
        // configuracion cuadro dialogo
        final Dialog dialog = new Dialog(PreventTreatmentActivity.this);
        final EditText txtFiltrarMusculo;
        final ListView listMusculos;
        final Button btnCerrarListaMusculos;
        final ImageButton btnFiltrar;

        dialog.setContentView(R.layout.dialog_musculos);
        dialog.setTitle("Seleccionar Músculo");
        txtFiltrarMusculo = (EditText) dialog.findViewById(R.id.txt_busqueda_rapida_musculo);
        if(lastSearchFilter.trim().length()>0)
            txtFiltrarMusculo.setText(lastSearchFilter);
        listMusculos = (ListView) dialog.findViewById(R.id.lv_musculos);
        btnCerrarListaMusculos = (Button) dialog.findViewById(R.id.btn_cerrar_dialogo_musculos);
        btnFiltrar = (ImageButton) dialog.findViewById(R.id.btn_filtrar_busqueda_musculo);
        //mAdapter = ArrayAdapter.createFromResource(this, R.array.str_musculos, android.R.layout.simple_list_item_1);
        listMusculos.setAdapter(mAdapter);
        listMusculos.setTextFilterEnabled(true);

        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mAdapter.getFilter().filter(txtFiltrarMusculo.getText().toString());
                    lastSearchFilter = txtFiltrarMusculo.getText().toString();
                }catch(Exception ex){
                    ex.printStackTrace();
                    Toast.makeText(context,"No se pudo filtrar la lista",Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtFiltrarMusculo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    try{
                        mAdapter.getFilter().filter(v.getText().toString());
                        lastSearchFilter = v.getText().toString();
                    }catch(Exception ex){
                        ex.printStackTrace();
                        Toast.makeText(context,"No se pudo filtrar la lista",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        btnCerrarListaMusculos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        listMusculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "Musculo: "+mAdapter.getItem(position),Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
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
        finish();
    }

    public void llenarLista() {
        tps.add(new TratamientoPreventivo("Vendaje","Facilitar","Nombre músculo"));
        tps.add(new TratamientoPreventivo("Fortalecimiento","Concéntrico","Nombre músculo"));
        tps.add(new TratamientoPreventivo("Autoestiramiento","auto estiramiento","nombre músculo"));
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

}
