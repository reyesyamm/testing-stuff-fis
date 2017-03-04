package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewPatientActivity extends AppCompatActivity {

    EditText nombre,edad,ocupacion,telefono,diagnosticoRef,medicoDiagnosticoRef,motivoConsulta1,motivoConsulta2;
    EditText antecedenteMotivo1, antecedenteMotivo2, exploracionClinicaMotivo1, exploracionClinicaMotivo2;
    EditText diagnosticoFunMotivo1, diagnosticoFunMotivo2,objetivo1,objetivo2,objetivo3;
    ImageButton agregarMotivo2,eliminarMotivo2, eliminarAntecedente2,eliminarExploracion2,eliminarDiagnostico2;
    Button guardar,pintar;
    TextView pintado;
    View contMotivo2,contAntecedente2,contExploracion2, contDiagnostico2;

    static int REQUEST_COORDS_DIBUJADAS=1;

    boolean segundoElementosVisibles;
    ProgressDialog progressDialog;
    ArrayList<String> coordsPintadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);
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
                confirmarRegreso();
            }
        });

        // Inicializar variables
        nombre = (EditText) findViewById(R.id.txt_nombre_paciente);
        edad = (EditText) findViewById(R.id.txt_edad_paciente);
        ocupacion = (EditText) findViewById(R.id.txt_ocupacion_paciente);
        telefono = (EditText) findViewById(R.id.txt_telefono_paciente);
        diagnosticoRef = (EditText) findViewById(R.id.txt_diagnostico_referencia_paciente);
        medicoDiagnosticoRef = (EditText) findViewById(R.id.txt_medico_referencia_paciente);
        motivoConsulta1 = (EditText) findViewById(R.id.txt_motivo_consulta1_paciente);
        motivoConsulta2 = (EditText) findViewById(R.id.txt_motivo_consulta2_paciente);
        antecedenteMotivo1 = (EditText) findViewById(R.id.txt_antecedente_motivo_consulta1_paciente);
        antecedenteMotivo2 = (EditText) findViewById(R.id.txt_antecedente_motivo_consulta2_paciente);
        exploracionClinicaMotivo1 = (EditText) findViewById(R.id.txt_exploracion_motivo_consulta1_paciente);
        exploracionClinicaMotivo2 = (EditText) findViewById(R.id.txt_exploracion_motivo_consulta2_paciente);
        diagnosticoFunMotivo1 = (EditText) findViewById(R.id.txt_diagnostico_motivo_consulta1_paciente);
        diagnosticoFunMotivo2 = (EditText) findViewById(R.id.txt_diagnostico_motivo_consulta2_paciente);
        objetivo1 = (EditText) findViewById(R.id.txt_objetivo1_paciente);
        objetivo2 = (EditText) findViewById(R.id.txt_objetivo2_paciente);
        objetivo3 = (EditText) findViewById(R.id.txt_objetivo3_paciente);
        agregarMotivo2 = (ImageButton) findViewById(R.id.btn_agregar_motivo);
        eliminarMotivo2 = (ImageButton) findViewById(R.id.btn_remover_motivo);
        eliminarAntecedente2 = (ImageButton) findViewById(R.id.btn_remover_antecedente_motivo);
        eliminarExploracion2 = (ImageButton) findViewById(R.id.btn_remover_exploracion_motivo);
        eliminarDiagnostico2 = (ImageButton) findViewById(R.id.btn_remover_diagnostico_motivo);
        pintar = (Button) findViewById(R.id.btn_pintar_areas_malestar);
        guardar = (Button) findViewById(R.id.btn_guardar_expediente_clinico);
        pintado = (TextView) findViewById(R.id.tv_info_areas_pintadas);
        contMotivo2 = findViewById(R.id.contenedor_segundo_motivo);
        contAntecedente2 = findViewById(R.id.contenedor_segundo_antecedente_motivo);
        contExploracion2 = findViewById(R.id.contenedor_segunda_exploracion_motivo);
        contDiagnostico2 = findViewById(R.id.contenedor_segundo_diagnostico_motivo);

        // configuracion de la barra de progreso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Guardando");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        segundoElementosVisibles = false;

        agregarMotivo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!segundoElementosVisibles){
                    segundoElementosVisibles = true;
                    mostrarContenedores();
                }else{
                    Toast.makeText(getBaseContext(),"Solo estan permitidos dos motivos de consulta",Toast.LENGTH_LONG).show();
                }
            }
        });

        eliminarAntecedente2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segundoElementosVisibles = false;
                mostrarContenedores();
            }
        });

        eliminarMotivo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segundoElementosVisibles = false;
                mostrarContenedores();
            }
        });

        eliminarExploracion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segundoElementosVisibles = false;
                mostrarContenedores();
            }
        });

        eliminarDiagnostico2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                segundoElementosVisibles = false;
                mostrarContenedores();
            }
        });


        // operacion pintar
        pintar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesarGuardadoPrevio();
                Intent intent = new Intent(NewPatientActivity.this, DrawBodyActivity.class);
                intent.putExtra("coordenadas",coordsPintadas);
                startActivityForResult(intent,REQUEST_COORDS_DIBUJADAS);
            }
        });

        // guardar expediente
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                procesarCampos();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_COORDS_DIBUJADAS && resultCode == RESULT_OK) {
            coordsPintadas = (ArrayList<String>) data.getSerializableExtra("coordenadas");
            if(coordsPintadas.size()>0){
                pintado.setText("Pintado!!");
            }else{
                pintado.setText("Aun no se han pintado");
            }
        }
    }

    @Override
    public void onBackPressed(){
        confirmarRegreso();
    }


    public void confirmarRegreso(){
        finish();
    }

    public void procesarGuardadoPrevio(){

    }

    public void mostrarContenedores(){
        contMotivo2.setVisibility(segundoElementosVisibles?View.VISIBLE:View.GONE);
        contAntecedente2.setVisibility(segundoElementosVisibles?View.VISIBLE:View.GONE);
        contExploracion2.setVisibility(segundoElementosVisibles?View.VISIBLE:View.GONE);
        contDiagnostico2.setVisibility(segundoElementosVisibles?View.VISIBLE:View.GONE);
    }

    public void procesarCampos(){
        String strNombre = nombre.getText().toString();
        String strEdad = edad.getText().toString();
        String strOcupacion = ocupacion.getText().toString();
        String strTelefono = telefono.getText().toString();
        String strDiagnosticoReferencia = diagnosticoRef.getText().toString();
        String strMedicoDiagnosticoRef = medicoDiagnosticoRef.getText().toString();
        String strMotivo1 = motivoConsulta1.getText().toString();
        String strAntecedente1 = antecedenteMotivo1.getText().toString();
        String strExploracion1 = exploracionClinicaMotivo1.getText().toString();
        String strDiagnostico1 = diagnosticoFunMotivo1.getText().toString();
        String strObjetivo1 = objetivo1.getText().toString();
        String strObjetivo2 = objetivo2.getText().toString();
        String strObjetivo3 = objetivo3.getText().toString();
        String strMotivo2=null, strAntecedente2=null, strExploracion2=null, strDiagnostico2=null;
        if(segundoElementosVisibles){
            strMotivo2 = motivoConsulta2.getText().toString();
            strAntecedente2 = antecedenteMotivo2.getText().toString();
            strExploracion2 = exploracionClinicaMotivo2.getText().toString();
            strDiagnostico2 = diagnosticoFunMotivo2.getText().toString();
        }

        boolean validez = camposValidos(strNombre, strEdad, strMotivo1, strAntecedente1, strExploracion1, strDiagnostico1, strObjetivo1, strMotivo2,
                strAntecedente2, strExploracion2, strDiagnostico2);

        if(validez){
            Toast.makeText(this,"Los datos son correctos, se agregará el nuevo paciente al sistema.", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }else{
            Toast.makeText(this,"Algunos campos tienen valores no válidos", Toast.LENGTH_LONG).show();
        }

    }

    public void limpiarErrores(){
        nombre.setError(null);
        edad.setError(null);
        motivoConsulta1.setError(null);
        motivoConsulta2.setError(null);
        antecedenteMotivo1.setError(null);
        antecedenteMotivo2.setError(null);
        exploracionClinicaMotivo1.setError(null);
        exploracionClinicaMotivo2.setError(null);
        diagnosticoFunMotivo1.setError(null);
        diagnosticoFunMotivo2.setError(null);
        objetivo1.setError(null);
        objetivo2.setError(null);
        objetivo3.setError(null);
    }

    public boolean camposValidos(String strNombre, String strEdad, String strMotivo1, String strAntecedente1,
                                 String strExploracion1, String strDiagnostico1, String strObjetivo1, String strMotivo2,
                                 String strAntecedente2, String strExploracion2, String strDiagnostico2){
        boolean retorno;
        View elementError = null;
        String strStandardError="Valor no válido o vacío";

        if(strNombre.trim().length()==0 || strNombre.trim().length()>80){
            nombre.setError("Nombre no válido");
            elementError = nombre;
        }

        if(strEdad.trim().length()==0){
            edad.setError("Edad no válida");
            if(elementError==null)
                elementError = edad;

        }

        if(strMotivo1.trim().length()==0 || strMotivo1.trim().length()>50){
            motivoConsulta1.setError(strStandardError);
            if(elementError==null)
                elementError = motivoConsulta1;
        }

        if(segundoElementosVisibles && (strMotivo2.trim().length()==0 || strMotivo2.trim().length()>50)){
            motivoConsulta2.setError(strStandardError);
            if(elementError==null)
                elementError = motivoConsulta2;
        }

        if(strAntecedente1.trim().length()==0 || strAntecedente1.trim().length()>50){
            antecedenteMotivo1.setError(strStandardError);
            if(elementError==null)
                elementError = antecedenteMotivo1;
        }

        if(segundoElementosVisibles && (strAntecedente2.trim().length()==0 || strAntecedente2.trim().length()>50)){
            antecedenteMotivo2.setError(strStandardError);
            if(elementError==null)
                elementError = antecedenteMotivo2;
        }

        if(strExploracion1.trim().length()==0 || strExploracion1.trim().length()>50){
            exploracionClinicaMotivo1.setError(strStandardError);
            if(elementError==null)
                elementError = exploracionClinicaMotivo1;
        }

        if(segundoElementosVisibles && ( strExploracion2.trim().length()==0 || strExploracion2.trim().length()>50)){
            exploracionClinicaMotivo2.setError(strStandardError);
            if(elementError==null)
                elementError = exploracionClinicaMotivo2;
        }

        if(strDiagnostico1.trim().length()==0 || strDiagnostico1.trim().length()>50){
            diagnosticoFunMotivo1.setError(strStandardError);
            if(elementError==null)
                elementError = diagnosticoFunMotivo1;
        }

        if(segundoElementosVisibles && (strDiagnostico2.trim().length()==0 || strDiagnostico2.trim().length()>50)){
            diagnosticoFunMotivo2.setError(strStandardError);
            if(elementError==null)
                elementError = diagnosticoFunMotivo2;
        }

        if(strObjetivo1.trim().length()==0 || strObjetivo1.trim().length()>50){
            objetivo1.setError("Al menos el primer objetivo debe ser llenado");
            if(elementError==null)
                elementError = objetivo1;
        }


        retorno=true;
        if(elementError!=null) {
            progressDialog.dismiss();
            elementError.requestFocus();
            retorno=false;

        }

        return retorno;
    }

}
