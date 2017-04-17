package com.swyam.fisiomer;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import entidad.Terapeuta;

import static com.swyam.fisiomer.Connection.SUF_RUTA_PDF_GENERADO;
import static com.swyam.fisiomer.Connection.parsearError;
import static com.swyam.fisiomer.Helpers.stripAccents;

public class GeneratePdfReportActivity extends AppCompatActivity {

    boolean solicitaExpediente;
    boolean solicitaObjetivos;
    boolean solicitaTratamientos;
    boolean solicitaImagenes;
    int id;

    boolean descargado = false;

    TextView tvTitulo;
    TextView tvLista;
    Button btnVerPDF;

    String strFilename;

    String nombrePaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pdf_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTitulo = (TextView) findViewById(R.id.tv_titulo_generando_pdf_de_paciente);
        tvLista = (TextView) findViewById(R.id.tv_lista_elementos_reporte);
        btnVerPDF = (Button) findViewById(R.id.btn_ver_reporte_pdf);

        solicitaExpediente = getIntent().getBooleanExtra("expediente",false);
        solicitaObjetivos = getIntent().getBooleanExtra("objetivos",false);
        solicitaTratamientos = getIntent().getBooleanExtra("tratamientos",false);
        solicitaImagenes = getIntent().getBooleanExtra("imagenes",false);
        id = getIntent().getIntExtra("id",-1);
        String paciente = getIntent().getStringExtra("paciente");
        if(paciente==null){
            paciente = "Desconocido";
        }

        nombrePaciente = Helpers.Capitalize(paciente);
        toolbar.setSubtitle(nombrePaciente);
        tvTitulo.setText("Generando reporte de "+nombrePaciente);

        String elementos = "Elementos solicitados";
        if(solicitaExpediente){
            elementos+="\n - Expediente";
        }

        if(solicitaObjetivos){
            elementos+="\n - Objetivos";
        }

        if(solicitaTratamientos){
            elementos+="\n - Tratamientos";
        }

        if(solicitaImagenes){
            elementos+="\n - Imágenes";
        }

        tvLista.setText(elementos);

        iniciarSolicitud();


        btnVerPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descargado){
                    verPDF();
                }else{
                    Toast.makeText(getBaseContext(),"No se ha descargado el reporte",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void verPDF()
    {
        viewLog();
    }

    public void viewLog() {
        Intent intent  = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void mostrarError(int idError, String mensaje){
        findViewById(R.id.progressbar_descarga_pdf).setVisibility(View.GONE);
        findViewById(R.id.tv_espere_porfavor_pdf).setVisibility(View.GONE);
        tvTitulo.setText("Error,código "+idError);
        tvLista.setText(mensaje);
    }

    private void iniciarSolicitud(){
        JSONObject obj = new JSONObject();
        Terapeuta t = Connection.obtenerTerapeutaLogeado(getBaseContext());

        boolean datosCorrectos = true;

        try{
            obj.put("id",id);
            obj.put("expediente",solicitaExpediente);
            obj.put("objetivos",solicitaObjetivos);
            obj.put("tratamientos",solicitaTratamientos);
            obj.put("imagenes",solicitaImagenes);
            obj.put("apikey",t.apikey);
        }catch(Exception ex){
            ex.printStackTrace();
            datosCorrectos = false;
        }

        if (!datosCorrectos) {
            Toast.makeText(getBaseContext(),"Error en los datos solicitados en la aplicación",Toast.LENGTH_SHORT).show();
            return;
        }

        String url = Connection.getHostServer(getBaseContext())+Connection.SUF_GENERAR_REPORTE_PDF_PACIENTE;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if (response.getInt("estado")==0) {
                                Date dNow = new Date( );
                                SimpleDateFormat ft = new SimpleDateFormat("ddMMyyyyhhmmss");
                                //System.out.println("Current Date: " + ft.format(dNow));
                                String nombreP = stripAccents(nombrePaciente);
                                strFilename = "REPORTE_PACIENTE_"+nombreP.toUpperCase().trim().replace(' ','_')+"_"+ft.format(dNow)+".pdf";//response.getString("filename");
                                descargarPDF(response.getString("filename"));
                            }else{
                                Toast.makeText(getBaseContext(), response.getString("mensaje"),Toast.LENGTH_LONG).show();
                                mostrarError(2,response.getString("mensaje"));
                            }
                        }catch(Exception ex){
                            ex.printStackTrace();
                            Toast.makeText(getBaseContext(), "Error en la respues recibida",Toast.LENGTH_LONG).show();
                            mostrarError(1,"Error en la respuesta recibida");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),parsearError(error),Toast.LENGTH_LONG).show();
                        mostrarError(1,parsearError(error));
                    }
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void descargarPDF(String filename){
        String urlFile = Connection.getHostServer(getBaseContext())+SUF_RUTA_PDF_GENERADO+filename;
        tvTitulo.setText("Descargando Reporte en formato PDF");
        tvLista.setText("Nombre de archivo: \n"+strFilename);
        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        startDownload(urlFile);

    }

    private void mostrarBotonVisualizar(){
        tvTitulo.setText("Reporte PDF descargado");
        findViewById(R.id.progressbar_descarga_pdf).setVisibility(View.GONE);
        findViewById(R.id.tv_espere_porfavor_pdf).setVisibility(View.GONE);
        btnVerPDF.setVisibility(View.VISIBLE);
        Toast.makeText(getBaseContext(),"El reporte en formato PDF se encuentra en la carpeta DESCARGAS",Toast.LENGTH_LONG).show();
    }

    /***************************************************************************/

    private DownloadManager mgr=null;
    private long lastDownload=-1L;

    public void startDownload(String url) {
        Uri uri=Uri.parse(url);

        Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .mkdirs();

        lastDownload=
                mgr.enqueue(new DownloadManager.Request(uri)
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(strFilename)
                        .setDescription("Reporte de tratamientos paciente "+nombrePaciente)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                strFilename));
    }

    public void queryStatus() {
        Cursor c=mgr.query(new DownloadManager.Query().setFilterById(lastDownload));

        if (c==null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG).show();
        }
        else {
            c.moveToFirst();

            Log.d(getClass().getName(), "COLUMN_ID: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(getClass().getName(), "COLUMN_LOCAL_URI: "+
                    c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(getClass().getName(), "COLUMN_STATUS: "+
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(getClass().getName(), "COLUMN_REASON: "+
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

            Toast.makeText(this, statusMessage(c), Toast.LENGTH_LONG).show();
        }
    }

    private String statusMessage(Cursor c) {
        String msg="???";

        switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg="Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg="Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg="Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg="Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg="Download complete!";
                break;

            default:
                msg="Download is nowhere in sight";
                break;
        }

        return(msg);
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            descargado = true;
            mostrarBotonVisualizar();
        }
    };

    BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            viewLog();
        }
    };


    /**************************************************************************/

    public class DescargarArchivoPDF extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            String fileUrl = params[0];
            String fileName = params[1];
            Boolean retorno = false;

            File folder =  Connection.obtenerRutaArchivosPDF();

            if(!folder.exists())
                folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }

            try {

                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[1024 * 1024];
                int bufferLength = 0;
                while((bufferLength = inputStream.read(buffer))>0 ){
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
                retorno = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return retorno;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(result){
                mostrarBotonVisualizar();
            }else{
                mostrarError(3,"Error al intentar descargar el reporte PDF");
            }

        }
    }



}
