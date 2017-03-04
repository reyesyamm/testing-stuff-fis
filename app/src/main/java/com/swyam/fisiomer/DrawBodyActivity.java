package com.swyam.fisiomer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawBodyActivity extends AppCompatActivity {

    ArrayList<String> coordsPintadas = new ArrayList<>();

    Button btnNegro, btnRojo, btnAzul, btnVerde, btnDeshacer, btnGuardar;
    TextView colorSeleccionado;
    ImageView imagenCuerpo;

    // elementos del pintado
    Paint pincel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_body);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                guardarDatos();
            }
        });

        // inicializamos variables
        btnNegro = (Button) findViewById(R.id.btn_color_negro);
        btnRojo = (Button) findViewById(R.id.btn_color_rojo);
        btnAzul = (Button) findViewById(R.id.btn_color_azul);
        btnVerde = (Button) findViewById(R.id.btn_color_verde);
        btnDeshacer = (Button) findViewById(R.id.btn_deshacer);
        btnGuardar = (Button) findViewById(R.id.btn_guardar_pintado);
        colorSeleccionado = (TextView) findViewById(R.id.tv_color_pincel);
        imagenCuerpo = (ImageView) findViewById(R.id.imagen_cuerpo);

        coordsPintadas = (ArrayList<String>) getIntent().getSerializableExtra("coordenadas");

        // inicializamos configuraciones del pincel
        pincel = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincel.setColor(Color.BLACK);
        pincel.setStyle(Paint.Style.FILL);
        pincel.setStrokeWidth(3);
        pincel.setAlpha(127);

        btnNegro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincel.setColor(Color.BLACK);
                pintarCoordenadas();
            }
        });

        btnRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincel.setColor(Color.RED);
                pintarCoordenadas();
            }
        });

        btnVerde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincel.setColor(Color.GREEN);
                pintarCoordenadas();
            }
        });

        btnAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pincel.setColor(Color.BLUE);
                pintarCoordenadas();
            }
        });

        btnDeshacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(coordsPintadas.size()>0){
                    coordsPintadas.remove(coordsPintadas.size()-1);
                    pintarCoordenadas();
                }
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });

        imagenCuerpo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float coordX = event.getX();
                float coordY = event.getY();
                String nuevaCoordenada = coordX+","+coordY;
                coordsPintadas.add(nuevaCoordenada);


                return false;
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus){
        super.onWindowFocusChanged(hasWindowFocus);
        pintarCoordenadas();
    }

    @Override
    public void onBackPressed(){
    	guardarDatos();
    }

    public void guardarDatos(){
        Intent intent = getIntent();
        intent.putExtra("coordenadas",coordsPintadas);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void pintarCoordenadas(){

    }

}
