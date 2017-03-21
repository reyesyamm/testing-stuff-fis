package com.swyam.fisiomer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entidad.Terapeuta;
import entidad.Tratamiento;
import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Connection.SUF_AGREGAR_NUEVOS_OBJS;
import static com.swyam.fisiomer.Connection.SUF_GUARDAR_NUEVO_TRATAMIENTO;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Connection.parsearError;


public class NewTreatmentActivity extends AppCompatActivity  implements  View.OnClickListener{

    Button btnAgregarTF, btnAgregarTA, btnAgregarTP;
    Button btnGuardarTratamiento;
    ImageButton btnIni1, btnIni2, btnIni3, btnIni4, btnFin1, btnFin2, btnFin3, btnFin4;
    int estadoInitSeleccionado = -1;
    int estadoFinSeleccionado  = -1;

    int idNuevoTratamiento = -1;

    Tratamiento tratamiento = new Tratamiento();

    private static int SOLICITUD_TERAPIA_FUNCIONAL = 1;
    private static int SOLICITUD_TERAPIA_ANALGESICA = 2;
    private static int SOLICITUD_TERAPIA_PREVENTIVA = 3;

    // RV
    RecyclerView rvTFuns, rvTPrev, rvTAn;
    RVTFAdapter rvtfAdapter;
    RVTPAdapter rvtpAdapter;
    RVTAAdapter rvtaAdapter;
    LinearLayoutManager llm1;


    // nombrepaciente
    String nombrePaciente="";
    int idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);
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

        nombrePaciente = getIntent().getStringExtra("paciente");
        idPaciente = getIntent().getIntExtra("idPaciente",-1);
        getSupportActionBar().setSubtitle(nombrePaciente);

        btnGuardarTratamiento = (Button) findViewById(R.id.btn_terminar_tratamiento);
        btnAgregarTF = (Button) findViewById(R.id.btn_agregar_terapia_funcional);
        btnAgregarTA = (Button) findViewById(R.id.btn_agregar_terapia_analgesica);
        btnAgregarTP = (Button) findViewById(R.id.btn_agregar_terapia_preventiva);

        btnIni1 = (ImageButton) findViewById(R.id.btn_estado_inicio_bien);
        btnIni2 = (ImageButton) findViewById(R.id.btn_estado_inicio_mediobien);
        btnIni3 = (ImageButton) findViewById(R.id.btn_estado_inicio_mal);
        btnIni4 = (ImageButton) findViewById(R.id.btn_estado_inicio_muymal);

        btnFin1 = (ImageButton) findViewById(R.id.btn_estado_fin_bien);
        btnFin2 = (ImageButton) findViewById(R.id.btn_estado_fin_mediobien);
        btnFin3 = (ImageButton) findViewById(R.id.btn_estado_fin_mal);
        btnFin4 = (ImageButton) findViewById(R.id.btn_estado_fin_muymal);

        btnAgregarTF.setOnClickListener(this);
        btnAgregarTA.setOnClickListener(this);
        btnAgregarTP.setOnClickListener(this);
        btnIni1.setOnClickListener(this);
        btnIni2.setOnClickListener(this);
        btnIni3.setOnClickListener(this);
        btnIni4.setOnClickListener(this);
        btnFin1.setOnClickListener(this);
        btnFin2.setOnClickListener(this);
        btnFin3.setOnClickListener(this);
        btnFin4.setOnClickListener(this);
        btnGuardarTratamiento.setOnClickListener(this);

        rvTFuns = (RecyclerView) findViewById(R.id.rv_tfuns);
        LinearLayoutManager llm1 = new LinearLayoutManager(this);
        rvTFuns.setLayoutManager(llm1);
        rvtfAdapter = new RVTFAdapter(getBaseContext(),tratamiento.tfun,false);
        rvTFuns.setAdapter(rvtfAdapter);


        rvTAn = (RecyclerView) findViewById(R.id.rv_tanals);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        rvTAn.setLayoutManager(llm2);
        rvtaAdapter = new RVTAAdapter(getBaseContext(),tratamiento.tanal,false);
        rvTAn.setAdapter(rvtaAdapter);

        rvTPrev = (RecyclerView) findViewById(R.id.rv_tprevs);
        LinearLayoutManager llm3 = new LinearLayoutManager(this);
        rvTPrev.setLayoutManager(llm3);
        rvtpAdapter = new RVTPAdapter(getBaseContext(),tratamiento.tprev,false);
        rvTPrev.setAdapter(rvtpAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == SOLICITUD_TERAPIA_FUNCIONAL && resultCode == RESULT_OK) {
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD FUNCIONAL

            List<TratamientoFuncional> tfuns2 = (List<TratamientoFuncional>) data.getSerializableExtra("tfuns");
            //tfuns = tfuns2;
            tratamiento.tfun.clear();
            if(tfuns2.size()>0)
            {
                for(TratamientoFuncional tf:tfuns2)
                    tratamiento.tfun.add(tf);
            }
            rvtfAdapter.notifyDataSetChanged();
            notificarModificacionTfuns();
        }else if(requestCode == SOLICITUD_TERAPIA_ANALGESICA && resultCode == RESULT_OK){
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD ANALGESICA
            List<TratamientoAnalgesico> tanals2 = (List<TratamientoAnalgesico>) data.getSerializableExtra("tanals");
            tratamiento.tanal.clear();
            if(tanals2.size()>0){
                for(TratamientoAnalgesico ta:tanals2)
                    tratamiento.tanal.add(ta);
            }
            rvtaAdapter.notifyDataSetChanged();

            notificarModificacionTanals();
        }else if(requestCode == SOLICITUD_TERAPIA_PREVENTIVA && resultCode == RESULT_OK){
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD PREVENTIVA

            List<TratamientoPreventivo> tprevs2 = (List<TratamientoPreventivo>) data.getSerializableExtra("tprevs");
            tratamiento.tprev.clear();
            if(tprevs2.size()>0)
                for(TratamientoPreventivo tp:tprevs2)
                    tratamiento.tprev.add(tp);

            rvtpAdapter.notifyDataSetChanged();
            notificarModificacionTprevs();
        }else if(requestCode == SOLICITUD_MULTIMEDIA){
            regresarConResultado();
        }
    }

    @Override
    public void onBackPressed(){
        regresar();
    }

    public void regresar(){
        if(tratamiento.totalTratamientos()>0){
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación de Salida.")
                    .setMessage("Tienes un tratamiento sin guardar. ¿Realmente deseas cerrar esta vista?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }else
            finish();
    }

    public static final int SOLICITUD_MULTIMEDIA = 4;

    public void mostrarVentanaParaMultimedia(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_camera)
                .setTitle("Agregar Imagen/Video")
                .setMessage("¿Deseas añadir alguna imagen o video al tratamiento aplicado recientemente?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(NewTreatmentActivity.this,MediaManagerActivity.class);
                        intent.putExtra("idTratamiento",idNuevoTratamiento);
                        startActivityForResult(intent,SOLICITUD_MULTIMEDIA);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        regresarConResultado();
                    }
                })
                .show();
    }

    public void regresarConResultado(){
        Intent intent = getIntent();
        intent.putExtra("idTratamiento",idNuevoTratamiento);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int edoIni=-1;
        switch(id){
            case R.id.btn_agregar_terapia_funcional:
                Intent intent1 = new Intent(NewTreatmentActivity.this, FunctionalTreatmentActivity.class);
                intent1.putExtra("tfuns",(Serializable)tratamiento.tfun);
                intent1.putExtra("paciente",nombrePaciente);
                startActivityForResult(intent1, SOLICITUD_TERAPIA_FUNCIONAL);
                break;
            case R.id.btn_agregar_terapia_preventiva:
                Intent intent2 = new Intent(NewTreatmentActivity.this, PreventTreatmentActivity.class);
                intent2.putExtra("tprevs",(Serializable)tratamiento.tprev);
                intent2.putExtra("paciente",nombrePaciente);
                startActivityForResult(intent2, SOLICITUD_TERAPIA_PREVENTIVA);
                break;
            case R.id.btn_agregar_terapia_analgesica:
                Intent intent3 = new Intent(NewTreatmentActivity.this, AnalgesicTreatmentActivity.class);
                intent3.putExtra("tanals",(Serializable)tratamiento.tanal);
                intent3.putExtra("paciente",nombrePaciente);
                startActivityForResult(intent3, SOLICITUD_TERAPIA_ANALGESICA);
                break;
            case R.id.btn_estado_inicio_bien:
            case R.id.btn_estado_inicio_mediobien:
            case R.id.btn_estado_inicio_mal:
            case R.id.btn_estado_inicio_muymal:
                seleccionEstadoInicio(id);
                break;
            case R.id.btn_estado_fin_bien:
            case R.id.btn_estado_fin_mediobien:
            case R.id.btn_estado_fin_mal:
            case R.id.btn_estado_fin_muymal:
                seleccionarEstadoFin(id);
                break;
            case R.id.btn_terminar_tratamiento:
                intentarGuardarTratamiento();
                break;
        }
    }

    public void seleccionEstadoInicio(int id){
        if(estadoInitSeleccionado>=1 && estadoInitSeleccionado<=4){
            estadoInitSeleccionado = -1;
            mostrarTodosIni(true);
        }else{
            mostrarTodosIni(false);
            switch(id){
                case R.id.btn_estado_inicio_bien:
                    estadoInitSeleccionado = 1;
                    btnIni1.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_estado_inicio_mediobien:
                    estadoInitSeleccionado = 2;
                    btnIni2.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_estado_inicio_mal:
                    estadoInitSeleccionado = 3;
                    btnIni3.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_estado_inicio_muymal:
                    estadoInitSeleccionado = 4;
                    btnIni4.setVisibility(View.VISIBLE);
                    break;
            }
        }
        tratamiento.estadoInicio=estadoInitSeleccionado;
    }

    public void mostrarTodosIni(boolean opt){
        btnIni1.setVisibility(opt?View.VISIBLE:View.GONE);
        btnIni2.setVisibility(opt?View.VISIBLE:View.GONE);
        btnIni3.setVisibility(opt?View.VISIBLE:View.GONE);
        btnIni4.setVisibility(opt?View.VISIBLE:View.GONE);
    }

    public void mostrarTodosFin(boolean opt){
        btnFin1.setVisibility(opt?View.VISIBLE:View.GONE);
        btnFin2.setVisibility(opt?View.VISIBLE:View.GONE);
        btnFin3.setVisibility(opt?View.VISIBLE:View.GONE);
        btnFin4.setVisibility(opt?View.VISIBLE:View.GONE);
    }

    public void seleccionarEstadoFin(int id){
        if(estadoFinSeleccionado>=1 && estadoFinSeleccionado<=4){
            estadoFinSeleccionado = -1;
            mostrarTodosFin(true);
        }else{
            mostrarTodosFin(false);
            switch(id){
                case R.id.btn_estado_fin_bien:
                    estadoFinSeleccionado = 1;
                    btnFin1.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_estado_fin_mediobien:
                    estadoFinSeleccionado = 2;
                    btnFin2.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_estado_fin_mal:
                    estadoFinSeleccionado = 3;
                    btnFin3.setVisibility(View.VISIBLE);
                    break;
                case R.id.btn_estado_fin_muymal:
                    estadoFinSeleccionado = 4;
                    btnFin4.setVisibility(View.VISIBLE);
                    break;
            }
        }
        tratamiento.estadoFin = estadoFinSeleccionado;
    }

    public void notificarModificacionTfuns(){
        Toast.makeText(getBaseContext(), "Cambios Agregados", Toast.LENGTH_SHORT).show();

    }

    public void notificarModificacionTprevs(){
        Toast.makeText(getBaseContext(), "Cambios Agregados", Toast.LENGTH_SHORT).show();
    }

    public void notificarModificacionTanals(){
        Toast.makeText(getBaseContext(), "Cambios Agregados", Toast.LENGTH_SHORT).show();
    }

    public void intentarGuardarTratamiento(){
        boolean tratamientoValido = true;
        if(estadoInitSeleccionado<1 || estadoInitSeleccionado>4){
            tratamientoValido = false;
            Toast.makeText(getBaseContext(), "Debes seleccionar el estado del paciente al iniciar el tratamiento",
                    Toast.LENGTH_LONG).show();
            findViewById(R.id.tv_titulo_estado_inicio).requestFocus();
        }

        if(tratamiento.totalTratamientos()==0){
            if(tratamientoValido)
                Toast.makeText(getBaseContext(), "No se ha aplicado ningun tratamiento",
                        Toast.LENGTH_LONG).show();
            tratamientoValido =false;
        }

        if(estadoFinSeleccionado<1 || estadoFinSeleccionado>4){
            if(tratamientoValido){
                Toast.makeText(getBaseContext(), "Debes seleccionar el estado del paciente al terminar el tratamiento",
                        Toast.LENGTH_LONG).show();
                findViewById(R.id.tv_titulo_estado_fin).requestFocus();
            }
            tratamientoValido = false;
        }

        if(tratamientoValido){
            verificarCredenciales();
        }
    }

    private void verificarCredenciales(){
        Connection.abrirDialogoCredenciales(NewTreatmentActivity.this, new OnDialogCred() {
            @Override
            public void credencialesValidasLocales() {
                credencialesVerificadas();
            }

            @Override
            public void credencialesValidasRemotas() {
                credencialesVerificadas();
            }
        });
    }

    private void credencialesVerificadas(){
        final Dialog dialog = new Dialog(NewTreatmentActivity.this);
        dialog.setContentView(R.layout.dialog_actualizar_objetivos);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final View contBtns = dialog.findViewById(R.id.contenedor_botones_sino_modificar_objs);
        final View contForm = dialog.findViewById(R.id.contenedor_formulario_modificar_objs);
        final EditText txtObj1 = (EditText) dialog.findViewById(R.id.txt_objetivo1_paciente);
        final EditText txtObj2 = (EditText) dialog.findViewById(R.id.txt_objetivo2_paciente);
        final EditText txtObj3 = (EditText) dialog.findViewById(R.id.txt_objetivo3_paciente);
        final Button btnSi = (Button) dialog.findViewById(R.id.btn_si_modificar_objetivos);
        final Button btnNo = (Button) dialog.findViewById(R.id.btn_no_modificar_objetivos);
        final Button btnGuardarObjs = (Button) dialog.findViewById(R.id.btn_guardar_objetivos);
        final Button btncancelarObjs = (Button) dialog.findViewById(R.id.btn_cancelar_objetivos);
        final View contProgress = dialog.findViewById(R.id.contenedor_status_guardado_obj);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressbar_guardado_objs);
        final TextView tvStatusGuardado = (TextView) dialog.findViewById(R.id.tv_status_guardado_objetivos);

        dialog.setTitle("¿Modificar objetivos?");
        contBtns.setVisibility(View.VISIBLE);
        contForm.setVisibility(View.GONE);

        btnSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Nuevos objetivos");
                contBtns.setVisibility(View.GONE);
                contForm.setVisibility(View.VISIBLE);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                guardarNuevoTratamiento(null,null,null);
            }
        });

        btncancelarObjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnGuardarObjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strObj1 = txtObj1.getText().toString();
                final String strObj2 = txtObj2.getText().toString();
                final String strObj3 = txtObj3.getText().toString();
                if(strObj1.trim().length()>0){
                    guardarNuevoTratamiento(strObj1, strObj2, strObj3);
                    dialog.dismiss();
                }else{
                    txtObj1.setError("Al menos el objetivo 1 debe ser llenado");
                }
            }
        });

        dialog.show();
    }

    private void guardarNuevoTratamiento(String obj1, String obj2, String obj3){

        if(idPaciente<=0){
            Toast.makeText(getBaseContext(),"No existe un paciente seleccionado",Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Guardando");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();


        try{
            JSONObject objetivos = new JSONObject();
            JSONObject tratamientopadre = new JSONObject();
            JSONArray tratamientosFuncionales = new JSONArray();
            JSONArray tratamientosAnalgesicos = new JSONArray();
            JSONArray tratamientosPreventivos = new JSONArray();

            boolean nobv = false;
            if(obj1!=null && obj2!=null && obj3!=null) {
                objetivos.put("objetivo1", obj1);
                objetivos.put("objetivo2", obj2);
                objetivos.put("objetivo3", obj3);
                nobv = true;
            }

            // agregamos los tratamientos funcionales

            if(tratamiento.totalFun()>0){
                for(TratamientoFuncional tf:tratamiento.tfun){
                    JSONObject jsonTF = new JSONObject();
                    jsonTF.put("tratamiento",tf.tratamiento);
                    jsonTF.put("tipo",tf.tipo);
                    jsonTF.put("musculo",tf.musculo);
                    jsonTF.put("estado",tf.estado);
                    jsonTF.put("movilizacion",tf.movilizacion);
                    tratamientosFuncionales.put(jsonTF);
                }

            }

            // agregamos los tratamientos analgesicos
            if(tratamiento.totalAna()>0){
                for(TratamientoAnalgesico ta:tratamiento.tanal){
                    JSONObject jsonTA = new JSONObject();
                    jsonTA.put("tratamiento",ta.tratamiento);
                    jsonTA.put("tipo",ta.tipo);
                    jsonTA.put("contenido",ta.contenido);
                    tratamientosAnalgesicos.put(jsonTA);
                }
            }

            // agregamos los tratamientos preventivos
            if(tratamiento.totalPrev()>0){
                for(TratamientoPreventivo tp:tratamiento.tprev){
                    JSONObject jsonTP = new JSONObject();
                    jsonTP.put("tratamiento",tp.tratamiento);
                    jsonTP.put("tipo",tp.tipo);
                    jsonTP.put("articulacionMusculo",tp.articulacionMusculo);
                    tratamientosPreventivos.put(jsonTP);
                }
            }

            Terapeuta t = obtenerTerapeutaLogeado(getBaseContext());
            tratamientopadre.put("estadoPacienteInicio",tratamiento.estadoInicio);
            tratamientopadre.put("estadoPacienteFin",tratamiento.estadoFin);
            tratamientopadre.put("paciente",idPaciente);
            tratamientopadre.put("terapeuta",t.id);

            tratamientopadre.put("nuevosObjetivos",nobv?"1":"0");
            tratamientopadre.put("objetivos",objetivos);

            tratamientopadre.put("funcionales",tratamientosFuncionales);
            tratamientopadre.put("analgesicos",tratamientosAnalgesicos);
            tratamientopadre.put("preventivos",tratamientosPreventivos);

            String url = getHostServer(getBaseContext())+SUF_GUARDAR_NUEVO_TRATAMIENTO;

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    tratamientopadre,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try{
                                if(response.getInt("estado")==0){
                                    idNuevoTratamiento = response.getInt("id");
                                    mostrarVentanaParaMultimedia();
                                }else
                                    Toast.makeText(getBaseContext(),response.getString("mensaje"),Toast.LENGTH_LONG).show();

                            }catch (Exception ex){
                                Toast.makeText(getBaseContext(),"Error en la respuesta",Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getBaseContext(), parsearError(error)+". Inténtalo más tarde", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
            );

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(getBaseContext(),"Error en los datos. Intenta nuevamente",Toast.LENGTH_SHORT).show();
        }
    }

}
