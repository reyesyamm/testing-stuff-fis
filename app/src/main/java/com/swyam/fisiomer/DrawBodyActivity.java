package com.swyam.fisiomer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawBodyActivity extends AppCompatActivity {

    ArrayList<String> coordsPintadas = new ArrayList<>();

    Button btnNegro, btnRojo, btnAzul, btnVerde, btnDeshacer, btnGuardar, btnLimpiar;
    TextView colorSeleccionado;
    ImageView imagenCuerpo;


    // elementos del pintado
    Paint pincel;
    Drawable drawable;
    Rect imageBounds;
    Bitmap bmImagen=null;
    float OHeight;
    float OWidth;
    float scaledHeight;
    float scaledWidth;
    float heightRatio;
    float widthRatio;
    float rx,ry;
    View root;

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
        rx=20;
        ry=20;
        btnNegro = (Button) findViewById(R.id.btn_color_negro);
        btnRojo = (Button) findViewById(R.id.btn_color_rojo);
        btnAzul = (Button) findViewById(R.id.btn_color_azul);
        btnVerde = (Button) findViewById(R.id.btn_color_verde);
        btnDeshacer = (Button) findViewById(R.id.btn_deshacer);
        btnGuardar = (Button) findViewById(R.id.btn_guardar_pintado);
        btnLimpiar = (Button) findViewById(R.id.btn_deshacer_todo);
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
                String nuevaCoordenada = cX(coordX)+","+cY(coordY);
                Log.d("coordenadas",nuevaCoordenada);
                coordsPintadas.add(nuevaCoordenada);
                pintarCoordenadas();

                return false;
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coordsPintadas.clear();
                pintarCoordenadas();
            }
        });

    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus){
        super.onWindowFocusChanged(hasWindowFocus);
        drawable = imagenCuerpo.getDrawable();
        bmImagen = ((BitmapDrawable)drawable).getBitmap();
        imageBounds = drawable.getBounds();
        OHeight = drawable.getIntrinsicHeight();
        OWidth =  drawable.getIntrinsicWidth();
        scaledHeight = imageBounds.height();
        scaledWidth = imageBounds.width();
        heightRatio = OHeight / scaledHeight;
        widthRatio = OWidth / scaledWidth;

        String str = "O: "+OHeight+","+OWidth+" - Sc: "+scaledHeight+","+scaledWidth+" - ratio: "+heightRatio+","+widthRatio;
        Log.d("ratios",str);

        pintarCoordenadas();
    }

    public float cX(float x){
        //if(Math.abs(widthRatio)==1)
        //    return x;
        return widthRatio*(x-imageBounds.left);
        //return widthRatio*(x-root.getLeft());
    }

    public float cY(float y){
        //if(Math.abs(heightRatio)==1)
        //    return y;
        return heightRatio*(y-imageBounds.top);
        //return heightRatio*(y-root.getTop());
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
        try{
            Bitmap tempBitmap = Bitmap.createBitmap((int)OWidth, (int)OHeight, Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(bmImagen, 0, 0, null);
            try{
                for(String coords:coordsPintadas){
                    String[] scxy = coords.split(",");
                    float cx = Float.parseFloat(scxy[0]);
                    float cy = Float.parseFloat(scxy[1]);
                    //Log.d("coordenadas2",cx+","+cy);
                    RectF oval1 = new RectF((cx-rx) ,(cy-ry), (cx+rx),(cy+ry));
                    tempCanvas.drawOval(oval1, pincel);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }


            imagenCuerpo.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
