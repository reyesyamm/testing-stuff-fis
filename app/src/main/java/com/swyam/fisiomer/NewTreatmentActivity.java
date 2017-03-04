package com.swyam.fisiomer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewTreatmentActivity extends AppCompatActivity {

    Button btnAgregarTF, btnAgregarTA, btnAgregarTP;



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


        btnAgregarTF = (Button) findViewById(R.id.btn_agregar_terapia_funcional);
        btnAgregarTA = (Button) findViewById(R.id.btn_agregar_terapia_analgesica);
        btnAgregarTP = (Button) findViewById(R.id.btn_agregar_terapia_preventiva);

        btnAgregarTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTreatmentActivity.this, FunctionalTreatmentActivity.class);
                startActivityForResult(intent, SOLICITUD_TERAPIA_FUNCIONAL);
            }
        });


        btnAgregarTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTreatmentActivity.this, AnalgesicTreatmentActivity.class);
                startActivityForResult(intent, SOLICITUD_TERAPIA_ANALGESICA);
            }
        });

        btnAgregarTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTreatmentActivity.this, PreventTreatmentActivity.class);
                startActivityForResult(intent, SOLICITUD_TERAPIA_PREVENTIVA);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SOLICITUD_TERAPIA_FUNCIONAL && resultCode == RESULT_OK) {
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD FUNCIONAL
        }else if(requestCode == SOLICITUD_TERAPIA_ANALGESICA && resultCode == RESULT_OK){
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD ANALGESICA
        }else if(requestCode == SOLICITUD_TERAPIA_PREVENTIVA && resultCode == RESULT_OK){
            // SE MANEJA EL RESULTADO DEVUELTO DE LA ACTIVIDAD PREVENTIVA
        }
    }

    public void regresar(){
        finish();
    }

}
