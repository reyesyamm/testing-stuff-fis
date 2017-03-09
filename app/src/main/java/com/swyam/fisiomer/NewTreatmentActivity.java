package com.swyam.fisiomer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entidad.Tratamiento;
import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Connection.SUF_AGREGAR_NUEVOS_OBJS;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.parsearError;

public class NewTreatmentActivity extends AppCompatActivity  implements  View.OnClickListener{

    Button btnAgregarTF, btnAgregarTA, btnAgregarTP;
    Button btnGuardarTratamiento;
    ImageButton btnIni1, btnIni2, btnIni3, btnIni4, btnFin1, btnFin2, btnFin3, btnFin4;
    int estadoInitSeleccionado = -1;
    int estadoFinSeleccionado  = -1;

    Tratamiento tratamiento = new Tratamiento();

    private static int SOLICITUD_TERAPIA_FUNCIONAL = 1;
    private static int SOLICITUD_TERAPIA_ANALGESICA = 2;
    private static int SOLICITUD_TERAPIA_PREVENTIVA = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_treatment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setSubtitle("Nombre Paciente tratamiento");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                regresar();
            }
        });


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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == SOLICITUD_TERAPIA_FUNCIONAL && resultCode == RESULT_OK) {
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD FUNCIONAL

            List<TratamientoFuncional> tfuns = (List<TratamientoFuncional>) data.getSerializableExtra("tfuns");
            tratamiento.tfun = tfuns;
            notificarModificacionTfuns();
        }else if(requestCode == SOLICITUD_TERAPIA_ANALGESICA && resultCode == RESULT_OK){
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD ANALGESICA

            List<TratamientoAnalgesico> tanals = (List<TratamientoAnalgesico>) data.getSerializableExtra("tanals");
            tratamiento.tanal = tanals;
            notificarModificacionTanals();
        }else if(requestCode == SOLICITUD_TERAPIA_PREVENTIVA && resultCode == RESULT_OK){
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD PREVENTIVA

            List<TratamientoPreventivo> tprevs = (List<TratamientoPreventivo>) data.getSerializableExtra("tprevs");
            tratamiento.tprev = tprevs;
            notificarModificacionTprevs();
        }
    }

    public void regresar(){
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
                startActivityForResult(intent1, SOLICITUD_TERAPIA_FUNCIONAL);
                break;
            case R.id.btn_agregar_terapia_preventiva:
                Intent intent2 = new Intent(NewTreatmentActivity.this, PreventTreatmentActivity.class);
                intent2.putExtra("tprevs",(Serializable)tratamiento.tprev);
                startActivityForResult(intent2, SOLICITUD_TERAPIA_PREVENTIVA);
                break;
            case R.id.btn_agregar_terapia_analgesica:
                Intent intent3 = new Intent(NewTreatmentActivity.this, AnalgesicTreatmentActivity.class);
                intent3.putExtra("tanals",(Serializable)tratamiento.tanal);
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
        Toast.makeText(getBaseContext(), "Modificaciones de tratamientos funcionales cubiertas", Toast.LENGTH_SHORT).show();
    }

    public void notificarModificacionTprevs(){
        Toast.makeText(getBaseContext(), "Modificaciones de tratamientos preventivas cubiertas", Toast.LENGTH_SHORT).show();
    }

    public void notificarModificacionTanals(){
        Toast.makeText(getBaseContext(), "Modificaciones de tratamientos analgésicas cubiertas", Toast.LENGTH_SHORT).show();
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
        JSONObject objetivos = new JSONObject();
        if(obj1!=null && obj2!=null && obj3!=null){
            try {
                objetivos.put("objetivo1",obj1);
                objetivos.put("objetivo2",obj2);
                objetivos.put("objetivo3",obj3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Toast.makeText(getBaseContext(),"Se guardará el tratamiento", Toast.LENGTH_LONG).show();
    }

}
