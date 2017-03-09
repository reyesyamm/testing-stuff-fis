package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entidad.Paciente;
import entidad.Tratamiento;
import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

import static com.swyam.fisiomer.Connection.SUF_DATOS_PACIENTE;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Helpers.Capitalize;
import static com.swyam.fisiomer.Helpers.formatearFechaString;


public class PatientActivity extends AppCompatActivity {

    TextView tvObjetivos, tvTerapeutaObjetivos, tvFechaObjetivos;
    ProgressDialog progressDialog;
    RecyclerView rv;
    LinearLayoutManager llm;
    Context context;
    SimpleDateFormat format;
    RVTAdapter adapter;
    List<Tratamiento> tratamientos = new ArrayList<>();
    int idPaciente;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
        context = getBaseContext();
        tvObjetivos = (TextView) findViewById(R.id.tv_objetivos);
        tvTerapeutaObjetivos = (TextView) findViewById(R.id.tv_nombre_terapeuta_objetivos);
        tvFechaObjetivos = (TextView) findViewById(R.id.tv_fecha_objetivos);


        rv = (RecyclerView) findViewById(R.id.my_rv);
        llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Obteniendo datos");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        idPaciente = getIntent().getIntExtra("id",-1);
        progressDialog.show();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientActivity.this, NewTreatmentActivity.class);
                startActivity(intent);
            }
        });

        cargarTodoDatos(idPaciente);
    }



    public void cargarTodoDatos(final int id){
        if(id<=0){
            progressDialog.dismiss();
            Toast.makeText(context,"No se ha seleccionado un usuario",Toast.LENGTH_LONG).show();
        }else{
            String url = getHostServer(context)+SUF_DATOS_PACIENTE;
            try{
                JSONObject obj = new JSONObject();
                obj.put("id",id+"");

                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                procesarRespuesta(response);
                                //progressDialog.dismiss();
                                //Toast.makeText(context,"entregado",Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
                            }
                        }
                ));



            }catch(Exception ex){
                ex.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(context,"Error al intentar obtener los datos del paciente",Toast.LENGTH_LONG).show();
            }


        }
    }

    public void procesarRespuesta(JSONObject response){

        try{
            if(response.getInt("estado")==0){
                // Obtenemos nombre del paciente
                JSONObject datos = response.getJSONObject("datos");
                toolbar.setSubtitle(Capitalize(datos.getString("nombre")));

                // Obtenemos objetivos del paciente
                JSONObject objetivos = datos.getJSONObject("objetivos");
                String o1 = objetivos.getString("objetivo1");
                String o2 = objetivos.getString("objetivo2");
                String o3 = objetivos.getString("objetivo3");
                String strObjetivos = ((o1.length()>0)?("- "+o1+"\n"):"");
                strObjetivos+=((o2.length()>0)?("- "+o2+"\n"):"");
                strObjetivos+=((o3.length()>0)?("- "+o3):"");
                tvObjetivos.setText(strObjetivos);
                String terObj = "Actualizados por "+objetivos.getString("terapeuta");

                tvTerapeutaObjetivos.setText(terObj);
                tvFechaObjetivos.setText(format.format( formatearFechaString(objetivos.getString("fecha"))));

                // Obtenemos la lista de tratamientos
                JSONArray tratamientosJSON = datos.getJSONArray("tratamientos");
                if(tratamientosJSON.length()>0){
                    for(int i=0;i<tratamientosJSON.length();i++){
                        JSONObject tr = tratamientosJSON.getJSONObject(i);
                        boolean tf =  tr.getBoolean("existeFuncional");
                        boolean tp = tr.getBoolean("existePreventivo");
                        boolean ta = tr.getBoolean("existeAnalgesico");
                        Tratamiento tratamientoObj = new Tratamiento(
                            tr.getInt("id"),
                            tr.getString("estadoPacienteInicio"),
                            tr.getString("estadoPacienteFin"),
                            formatearFechaString(tr.getString("fecha")),
                            tr.getString("terapeuta"),
                            tf,
                            tp,
                            ta
                        );

                        if(tf){
                            tratamientoObj.asignarTratamientosFuncionales(parsearJSONTFs(tr.getJSONArray("funcional")));
                        }

                        if(tp){
                            tratamientoObj.asignarTratamientoPreventivo(parsearJSONTPs(tr.getJSONArray("preventivo")));
                        }

                        if(ta){
                            tratamientoObj.asignarTratamientoAnalgesico(parsearJSONTAs(tr.getJSONArray("analgesico")));
                        }

                        tratamientos.add(tratamientoObj);
                    }
                }else{
                    Toast.makeText(context,"El paciente no tiene tratamientos aplicados",Toast.LENGTH_LONG).show();
                }
                llenarRV();
            }else{
                Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }catch(JSONException ex){
            ex.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(context, "Error en la respuesta obtenida del servidor",Toast.LENGTH_LONG).show();
        }catch(Exception ex){
            ex.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(context, "Error desconocido en la respuesta",Toast.LENGTH_LONG).show();
        }


    }

    public List<TratamientoFuncional> parsearJSONTFs(JSONArray tfs){
        List<TratamientoFuncional> tfun = new ArrayList<TratamientoFuncional>();

        try{
            if(tfs.length()>0){
                for(int i=0;i<tfs.length();i++){
                    JSONObject temptf = tfs.getJSONObject(i);
                    tfun.add(new TratamientoFuncional(
                            temptf.getString("tratamiento"),
                            temptf.getString("tipo"),
                            temptf.getInt("musculo"),
                            temptf.getString("estado"),
                            temptf.getString("movilizacion")
                    ));
                }
            }
        }catch(Exception ex){
            Toast.makeText(context,"Algo ha ocurrido en el formateo de los datos recibidos",Toast.LENGTH_SHORT).show();
        }

        return tfun;
    }

    public List<TratamientoPreventivo> parsearJSONTPs(JSONArray tps){
        List<TratamientoPreventivo> tprev = new ArrayList<TratamientoPreventivo>();
        try{
            if(tps.length()>0){
                for(int i=0;i<tps.length();i++){
                    JSONObject temptp = tps.getJSONObject(i);
                    tprev.add(new TratamientoPreventivo(
                            temptp.getString("tratamiento"),
                            temptp.getString("tipo"),
                            temptp.getInt("articulacionMusculo")
                    ));

                }
            }
        }catch(Exception ex){
            Toast.makeText(context,"Algo ha ocurrido en el formateo de los datos recibidos",Toast.LENGTH_SHORT).show();
        }

        return tprev;
    }

    public List<TratamientoAnalgesico> parsearJSONTAs(JSONArray tas){
        List<TratamientoAnalgesico> tanal = new ArrayList<TratamientoAnalgesico>();

        try{
            if(tas.length()>0){
                for(int i=0;i<tas.length();i++){
                    JSONObject tempta = tas.getJSONObject(i);
                    tanal.add(new TratamientoAnalgesico(
                            tempta.getString("tratamiento"),
                            tempta.getString("tipo"),
                            tempta.getString("contenido")
                    ));
                }
            }
        }catch(Exception ex){
            Toast.makeText(context,"Algo ha ocurrido en el formateo de los datos recibidos",Toast.LENGTH_SHORT).show();
        }

        return tanal;
    }

    public void llenarRV(){
        adapter = new RVTAdapter(context,tratamientos);
        rv.setAdapter(adapter);
         adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Paciente item) {
                // no se utiliza
            }

            @Override
            public void onItemClick(Tratamiento t){
                //Toast.makeText(context,"Has seleccionado al tratamiento realizado por "+t.terapeuta+", "+t.totalTratamientos()+" tratamientos realizados",Toast.LENGTH_SHORT).show();
                String paciente =toolbar.getSubtitle().toString();
                Intent intent = new Intent(PatientActivity.this, TreatmentDetailsActivity.class);
                intent.putExtra("paciente",paciente);
                intent.putExtra("tratamiento",t);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paciente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_detalles_paciente) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
