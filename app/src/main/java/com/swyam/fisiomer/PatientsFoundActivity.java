package com.swyam.fisiomer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entidad.Paciente;
import entidad.Tratamiento;

import static com.swyam.fisiomer.Connection.SUF_OBTENER_RES_SIMILARES;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Helpers.formatearFechaString;
import static com.swyam.fisiomer.Helpers.getStrFechaFormateada;

public class PatientsFoundActivity extends AppCompatActivity {

    Toolbar toolbar;

    RecyclerView rv;
    LinearLayoutManager llm;
    RVPAdapter adapter;
    List<Paciente> pacientes = new ArrayList<>();
    SimpleDateFormat format;
    Context context;
    ProgressDialog progressDialog;
    String token="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_found);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        token = getIntent().getStringExtra("token");


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Listando Resultados");
        progressDialog.setMessage("Espere porfavor");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        context = getBaseContext();
        llm = new LinearLayoutManager(context);
        rv = (RecyclerView) findViewById(R.id.my_rv);
        format = new SimpleDateFormat("MMMM dd, yyyy hh:mm a");
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        progressDialog.show();
        cargarDatos();



    }

    @Override
    public void onWindowFocusChanged(boolean change){
        super.onWindowFocusChanged(change);

    }

    public void cargarDatos(){
        try{
            String url = getHostServer(context)+SUF_OBTENER_RES_SIMILARES;
            JSONObject obj = new JSONObject();
            obj.put("token",token);

            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressDialog.dismiss();
                            try{
                                if(response.getInt("estado")==0){
                                    JSONArray ps = response.getJSONArray("datos");
                                    toolbar.setSubtitle(ps.length()+" resultados para '"+token+"'");
                                    for(int i=0;i<ps.length();i++){
                                        JSONObject p = ps.getJSONObject(i);
                                        Date dt = formatearFechaString(p.getString("fecha"));
                                        pacientes.add(new Paciente(p.getInt("id"),p.getString("nombre"),p.getInt("edad"),
                                                getStrFechaFormateada(dt),p.getString("terapeuta"),p.getInt("totales")
                                                ));
                                    }
                                }else{
                                    Toast.makeText(context, response.getString("mensaje"),Toast.LENGTH_LONG).show();
                                }
                            }catch(Exception ex){
                                ex.printStackTrace();
                                Toast.makeText(context, "Error al parsear la respuesta recibida",Toast.LENGTH_LONG).show();
                            }
                            llenarRV();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context,parsearError(error),Toast.LENGTH_LONG).show();
                        }
                    }
            ));



        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(context,"Error al intentar enviar los datos",Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarRV(){
        adapter = new RVPAdapter(pacientes);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Paciente item) {
                //Toast.makeText(context,"Has seleccionado al paciente "+item.nombre,Toast.LENGTH_SHORT).show();
                int id = item.id;
                Intent intent = new Intent(PatientsFoundActivity.this, PatientActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }

            @Override
            public void onItemClick(Tratamiento t){
                // no se utiliza
            }
        });
    }

}
