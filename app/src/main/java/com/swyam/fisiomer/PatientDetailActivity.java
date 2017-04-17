package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import entidad.Terapeuta;

import static com.swyam.fisiomer.Connection.SUF_EXPEDIENTE_PACIENTE;
import static com.swyam.fisiomer.Connection.SUF_GUARDAR_CAMBIOS_DATOS_PACIENTE;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Helpers.SEPARATOR_STRING_SER;
import static com.swyam.fisiomer.Helpers.formatearFechaString;
import static com.swyam.fisiomer.Helpers.getStrFechaFormateada;

public class PatientDetailActivity extends AppCompatActivity {


    TextView tvFecha, tvTerapeuta;

    EditText nombre,edad,ocupacion,telefono;
    Button actualizar;

    TextView diagnosticoReferencia,medicoReferencia;

    ImageView cuerpo;

    TextView motivo1,antecedente1,exploracion1,diagnostico1;

    TextView motivo2,antecedente2, exploracion2, diagnostico2;


    Toolbar toolbar;
    Context context;

    int pacienteId;

    boolean datosEnEdicion = false;
    Paint pincel;
    String coordenadas="";
    int color = 1;
    boolean datosModificados = false;

    ProgressDialog progressDialog;

    Terapeuta T;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });

        context = getBaseContext();

        tvFecha = (TextView) findViewById(R.id.tv_fecha_registro_paciente);
        tvTerapeuta = (TextView) findViewById(R.id.tv_terapeuta_registro_paciente);

        nombre = (EditText) findViewById(R.id.txt_nombre_paciente);
        edad = (EditText) findViewById(R.id.txt_edad_paciente);
        ocupacion = (EditText) findViewById(R.id.txt_ocupacion_paciente);
        telefono = (EditText) findViewById(R.id.txt_telefono_paciente);
        actualizar = (Button) findViewById(R.id.btn_actualizar_datos_generales_paciente);

        diagnosticoReferencia = (TextView) findViewById(R.id.tv_diagnostico_referencia);
        medicoReferencia = (TextView) findViewById(R.id.tv_medico_diagnostico_referencia);

        cuerpo = (ImageView) findViewById(R.id.image_cuerpo);

        motivo1 = (TextView) findViewById(R.id.tv_motivo_1);
        antecedente1 = (TextView) findViewById(R.id.tv_antecedente_1);
        exploracion1 = (TextView) findViewById(R.id.tv_exploracion_1);
        diagnostico1 = (TextView) findViewById(R.id.tv_diagnostico_1);

        motivo2 = (TextView) findViewById(R.id.tv_motivo_2);
        antecedente2 = (TextView) findViewById(R.id.tv_antecedente_2);
        exploracion2 = (TextView) findViewById(R.id.tv_exploracion_2);
        diagnostico2 = (TextView) findViewById(R.id.tv_diagnostico_2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Obteniendo datos");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        pincel = new Paint(Paint.ANTI_ALIAS_FLAG);
        pincel.setColor(Color.BLACK);
        pincel.setStyle(Paint.Style.FILL);
        pincel.setStrokeWidth(3);
        pincel.setAlpha(127);
        T = obtenerTerapeutaLogeado(context);

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(datosEnEdicion){
                    Helpers.esconderTeclado(PatientDetailActivity.this);
                    Connection.abrirDialogoCredenciales(PatientDetailActivity.this, new OnDialogCred() {
                        @Override
                        public void credencialesValidasLocales() {
                            T = obtenerTerapeutaLogeado(context);
                            if(T.esAdmin || T.permiso){
                                procederGuardado();
                            }else{
                                Toast.makeText(context,"Tu usuario es de solo lectura. No puedes guardar el tratamiento",Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void credencialesValidasRemotas() {
                            T = obtenerTerapeutaLogeado(context);
                            if(T.esAdmin || T.permiso){
                                procederGuardado();
                            }else{
                                Toast.makeText(context,"Tu usuario es de solo lectura. No puedes guardar el tratamiento",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{
                    // habilitamos
                    actualizar.setText("Guardar Cambios");
                    activarCuadros(true);
                    nombre.requestFocus();
                    datosEnEdicion=true;
                }
            }
        });

        pacienteId = getIntent().getIntExtra("id",-1);
        obtenerDatosRemotos();

        cuerpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color++;
                switch (color){
                    case 1:
                        pincel.setColor(Color.BLACK);
                        break;
                    case 2:
                        pincel.setColor(Color.RED);
                        break;
                    case 3:
                        pincel.setColor(Color.GREEN);
                        break;
                    case 4:
                        pincel.setColor(Color.BLUE);
                        break;
                    default:
                        color=1;
                        pincel.setColor(Color.BLACK);
                }
                dibujarCoordenadas();
            }
        });
    }

    @Override
    public void onBackPressed(){
        regresar();
    }

    public void regresar(){
        if(datosModificados){
            Intent intent = getIntent();
            intent.putExtra("nombre",nombre.getText().toString());
            setResult(RESULT_OK,intent);
        }
        finish();
    }

    public void obtenerDatosRemotos(){
        if(pacienteId<=0){
            progressDialog.dismiss();
            Toast.makeText(context,"Error en el paciente seleccionado. Ningun dato se ha obtenido",Toast.LENGTH_LONG).show();
        }else{
            String url = getHostServer(context)+SUF_EXPEDIENTE_PACIENTE;
            JSONObject obj = new JSONObject();
            try{
                obj.put("id",pacienteId);
                obj.put("apikey",T.apikey);
                String str = obj.toString();
                Log.d("sss",str);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            procesarRespuesta(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
                        }
                    }
            );

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

        }
    }

    public void procesarRespuesta(JSONObject response){
        progressDialog.dismiss();
        try{
            if(response.getInt("estado")==0){
                String fechaForm = getStrFechaFormateada(formatearFechaString(response.getString("fecha")));
                tvFecha.setText(fechaForm);
                tvTerapeuta.setText(response.getString("terapeuta"));

                nombre.setText(response.getString("nombre"));
                edad.setText(response.getString("edad"));
                ocupacion.setText(response.getString("ocupacion"));
                telefono.setText(response.getString("telefono"));

                coordenadas = response.getString("coordenadas");

                diagnosticoReferencia.setText(response.getString("diagnosticoReferencia"));
                medicoReferencia.setText(response.getString("medicoDiagnosticoReferencia"));

                JSONArray expediente = response.getJSONArray("expediente");

                JSONObject exp1 = expediente.getJSONObject(0);

                motivo1.setText(exp1.getString("motivo"));
                antecedente1.setText(exp1.getString("antecedente"));
                exploracion1.setText(exp1.getString("exploracion"));
                diagnostico1.setText(exp1.getString("diagnostico"));

                if(expediente.length()==2){
                    JSONObject exp2 = expediente.getJSONObject(1);
                    motivo2.setText(exp2.getString("motivo"));
                    antecedente2.setText(exp2.getString("antecedente"));
                    exploracion2.setText(exp2.getString("exploracion"));
                    diagnostico2.setText(exp2.getString("diagnostico"));
                }else{
                    findViewById(R.id.contenedor_motivo2).setVisibility(View.GONE);
                }

                dibujarCoordenadas();

            }else{
                Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(context,"Error al parsear los datos recibidos",Toast.LENGTH_LONG).show();
        }
        activarCuadros(false);
    }

    public void activarCuadros(boolean v){
        nombre.setEnabled(v);
        edad.setEnabled(v);
        ocupacion.setEnabled(v);
        telefono.setEnabled(v);
    }

    public void procederGuardado(){
        String strNombre = nombre.getText().toString();
        String strEdad = edad.getText().toString();
        String strOcupacion = ocupacion.getText().toString();
        String strTelefono = telefono.getText().toString();

        nombre.setError(null);
        edad.setError(null);
        ocupacion.setError(null);
        telefono.setError(null);


        boolean datosValidos = true;
        if(strNombre.trim().length()==0){
            datosValidos = false;
            nombre.setError("Nombre no válido.");
        }

        if(strEdad.trim().length()==0){
            if(datosValidos)
                edad.setError("Edad no válida.");
            datosValidos=false;
        }

        if(strOcupacion.trim().length()==0){
            if(datosValidos)
                ocupacion.setError("Valor para ocupación no válida.");
            datosValidos = false;
        }

        if(datosValidos){
            progressDialog.show();
            progressDialog.setTitle("Actualizando los datos");
            String url = getHostServer(context)+SUF_GUARDAR_CAMBIOS_DATOS_PACIENTE;

            JSONObject obj = new JSONObject();
            try{
                obj.put("id",pacienteId);
                obj.put("nombre",strNombre);
                obj.put("edad",strEdad);
                obj.put("ocupacion",strOcupacion);
                obj.put("telefono",strTelefono);
                obj.put("apikey",T.apikey);
            }catch(Exception ex){
                ex.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try{
                                if(response.getInt("estado")==0){
                                    Toast.makeText(context,"Datos del paciente actualizados",Toast.LENGTH_LONG).show();
                                    datosModificados = true;
                                    activarCuadros(false);
                                    actualizar.setText("Actualizar datos");
                                }else{
                                    Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(context,"Error al leer la respuesta recibida",Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            error.printStackTrace();
                            Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
                        }
                    }
            );


            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }

    public void dibujarCoordenadas(){
        String[] cxy = coordenadas.split(SEPARATOR_STRING_SER);

        if(cxy.length==0)
            return;

        int rx=20;
        int ry=20;

        Bitmap bmImagen = BitmapFactory.decodeResource(getResources(),R.drawable.human_body);
        float OWidth = bmImagen.getWidth();
        float OHeight = bmImagen.getHeight();

        try{
            Bitmap tempBitmap = Bitmap.createBitmap((int)OWidth, (int)OHeight, Bitmap.Config.RGB_565);
            Canvas tempCanvas = new Canvas(tempBitmap);
            tempCanvas.drawBitmap(bmImagen, 0, 0, null);
            try{
                for(String coords:cxy){
                    String[] scxy = coords.split(",");
                    float cx = Float.parseFloat(scxy[0]);
                    float cy = Float.parseFloat(scxy[1]);
                    RectF oval1 = new RectF((cx-rx) ,(cy-ry), (cx+rx),(cy+ry));
                    tempCanvas.drawOval(oval1, pincel);
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            cuerpo.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


}
