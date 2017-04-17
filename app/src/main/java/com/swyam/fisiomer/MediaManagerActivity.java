package com.swyam.fisiomer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import conexiones.AndroidMultiPartEntity;
import entidad.Terapeuta;

import static com.swyam.fisiomer.Connection.SUF_ELIMINAR_MULTIMEDIA;
import static com.swyam.fisiomer.Connection.getHostServer;
import static com.swyam.fisiomer.Connection.obtenerTerapeutaLogeado;
import static com.swyam.fisiomer.Helpers.formatearFechaString;
import static com.swyam.fisiomer.Helpers.getStrFechaFormateada;


public class MediaManagerActivity extends AppCompatActivity {



    Context context;
    int idTratamiento;
    int idTerapeuta;
    String hostServer;

    long totalSize=0;
    ImageButton btnNuevaImagen, btnNuevoVideo, btnImportar, btnEliminar;
    ImageView imagenPrevia;
    VideoView videoPrevio;
    View contenedorBotones;
    ImageButton btnPlay,btnPause;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;

    Button uploadMedia;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int IMPORT_IMAGE_REQUEST_CODE = 300;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Uri fileUri;
    String finalFilePath = null;
    HashMap<Integer,String> mediaSubida = new HashMap<>();

    int type_media = 0;

    Terapeuta T;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_manager);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Subiendo archivo");
        progressDialog.setMessage("");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setTitle("Eliminando archivo en el servidor");
        progressDialog2.setMessage("Espere porfavor");
        progressDialog2.setCancelable(false);

        idTratamiento = getIntent().getIntExtra("idTratamiento",-1);
        solicitarPermisos();
        context = getBaseContext();
        Terapeuta t = Connection.obtenerTerapeutaLogeado(context);
        idTerapeuta = t.id;

        hostServer = getHostServer(context)+Connection.SUF_SUBIR_MULTIMEDIA;

        T = obtenerTerapeutaLogeado(context);

        btnNuevaImagen = (ImageButton) findViewById(R.id.btnNuevaImagen);
        btnNuevoVideo = (ImageButton) findViewById(R.id.btnNuevoVideo);
        btnImportar = (ImageButton) findViewById(R.id.btnImportarMedia);
        imagenPrevia = (ImageView) findViewById(R.id.image_previa_media);
        videoPrevio = (VideoView) findViewById(R.id.video_previa_media);
        contenedorBotones = findViewById(R.id.contenedor_botones_video);
        btnPlay = (ImageButton) findViewById(R.id.btnPlayVideo);
        btnPause = (ImageButton) findViewById(R.id.btnPauseVideo);
        uploadMedia = (Button) findViewById(R.id.btn_anexar_tratamiento);
        btnEliminar = (ImageButton) findViewById(R.id.btnEliminarMediaSeleccionado);

        btnNuevaImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturarImagen();
            }
        });

        btnNuevoVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturarVideo();
            }
        });

        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importarMultimedia();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type_media==MEDIA_TYPE_VIDEO && !videoPrevio.isPlaying()){
                        videoPrevio.start();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type_media==MEDIA_TYPE_VIDEO && videoPrevio.isPlaying()){
                    videoPrevio.pause();
                }
            }
        });

        if(!dispositivoConCamara()){
            btnNuevaImagen.setEnabled(false);
            btnNuevoVideo.setEnabled(false);
            Toast.makeText(context,"Este dispositivo no tiene una cámara disponible",Toast.LENGTH_LONG).show();
        }

        contenedorBotones.setVisibility(View.GONE);

        uploadMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarSubirMedia();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentarEliminar();
            }
        });

    }

    public void verificarRemotoParaEliminar(String fname){
        int id = -1;
        for(Map.Entry<Integer, String> entry : mediaSubida.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            if(value.equals(fname)){
                id = key;
                break;
            }
        }
        final int idF = id;
        if(idF>0){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_camera)
                    .setTitle("¿Eliminar en el servidor?")
                    .setMessage("Este archivo ha sido subido al servidor. ¿Deseas eliminarlo igualmente?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            eliminarRemoto(idF);
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(context,"El archivo ha sido eliminado en este dispositivo",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        }else
            Toast.makeText(context,"El archivo ha sido eliminado",Toast.LENGTH_LONG).show();

        limpiarVistas();
    }

    public void limpiarVistas(){
        contenedorBotones.setVisibility(View.GONE);
        uploadMedia.setVisibility(View.GONE);
        imagenPrevia.setImageResource(R.drawable.logotransparente);
        videoPrevio.setVisibility(View.GONE);
        imagenPrevia.setVisibility(View.VISIBLE);
    }

    public void eliminarRemoto(final int id){

        String url = Connection.getHostServer(context)+SUF_ELIMINAR_MULTIMEDIA;
        JSONObject datos = new JSONObject();



        try {

            datos.put("id",id);
            datos.put("apikey",T.apikey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog2.show();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                datos,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog2.dismiss();
                        try{

                            if(response.getInt("estado")==0){
                                mediaSubida.remove(id);
                                Toast.makeText(context,"Archivo multimedia eliminado",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog2.dismiss();
                        error.printStackTrace();
                        Toast.makeText(context,"Error al intentar eliminar el archivo multimedia. Intenta mas tarde",Toast.LENGTH_LONG).show();
                    }
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    public void intentarEliminar(){
        if(fileUri!=null){
            String message = "No se ha podido eliminar el archivo seleccionado";
            try{
                File  fl = new File(fileUri.getPath());
                String fname = fl.getName();
                if(fl.exists()){
                    fl.delete();
                }
                verificarRemotoParaEliminar(fname);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(context,"No existe ningún archivo multimedia seleccionado",Toast.LENGTH_LONG).show();
        }
    }

    public void capturarImagen(){
        if(videoPrevio.isPlaying())
            videoPrevio.stopPlayback();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    public void capturarVideo(){
        if(videoPrevio.isPlaying())
            videoPrevio.stopPlayback();

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,Helpers.DURATION_LIMIT_SEC_VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
        }
    }

    public void importarMultimedia(){
        if(videoPrevio.isPlaying())
            videoPrevio.stopPlayback();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Selecciona una Imagen"), IMPORT_IMAGE_REQUEST_CODE);
    }

    private void asignarImagen(){
        String filePath = fileUri.getPath();
        contenedorBotones.setVisibility(View.VISIBLE);
        uploadMedia.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imagenPrevia.setImageBitmap(bitmap);
        type_media = MEDIA_TYPE_IMAGE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            asignarImagen();
        }

        if(requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK){
            String filePath = fileUri.getPath();
            type_media = MEDIA_TYPE_VIDEO;
            contenedorBotones.setVisibility(View.VISIBLE);
            uploadMedia.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.VISIBLE);
            imagenPrevia.setVisibility(View.GONE);
            videoPrevio.setVisibility(View.VISIBLE);
            videoPrevio.setVideoPath(filePath);
            videoPrevio.start();
        }
        if (requestCode == IMPORT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            try{
                Uri uri = data.getData();
                Log.i("uri",uri.toString());
                String selectedImagePath = Helpers.getPath(context, uri);
                Log.i("uri name",selectedImagePath);
                File src = new File(selectedImagePath);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Helpers.IMAGE_DIRECTORY_NAME);
                File dest = new File(mediaStorageDir.getPath() + File.separator+ src.getName());

                FileChannel fsrc = new FileInputStream(src).getChannel();
                FileChannel fdst = new FileOutputStream(dest).getChannel();
                fdst.transferFrom(fsrc, 0, fsrc.size());
                fsrc.close();
                fdst.close();

                fileUri = Uri.fromFile(dest);

                asignarImagen();

            }catch(Exception ex){
                ex.printStackTrace();
                Toast.makeText(context,"Error al importar la imagen",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static final String TAG = MediaManagerActivity.class.getSimpleName();
    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Helpers.IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Helpers.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 2;
    private void solicitarPermisos(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){

            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_USE_CAMERA);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    btnNuevaImagen.setEnabled(false);
                    btnNuevoVideo.setEnabled(false);
                    btnImportar.setEnabled(false);
                    Toast.makeText(context,"Se han deshabilitado las opciones para lectura y escritura",Toast.LENGTH_LONG).show();
                }


            }break;
            case MY_PERMISSIONS_REQUEST_USE_CAMERA:
            {
                if(!(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)){
                    btnNuevaImagen.setEnabled(false);
                    btnNuevoVideo.setEnabled(false);
                    Toast.makeText(context,"Se han deshabilitado las opciones que utilizan la cámara",Toast.LENGTH_LONG).show();
                }
            }break;
        }
    }

    private boolean dispositivoConCamara(){
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void intentarSubirMedia(){
        if(fileUri==null){
            Toast.makeText(context,"Ningun archivo multimedia seleccionado",Toast.LENGTH_SHORT).show();
            return;
        }

        String filename = (new File(fileUri.getPath())).getName();
        //mediaSubida.put(id,nombre);
        if(!mediaSubida.containsValue(filename)){
            finalFilePath = fileUri.getPath();
            progressDialog.show();
            UploadFileToServer uploadFileToServer =  new UploadFileToServer();
            uploadFileToServer.execute();
        }else{
            Toast.makeText(getBaseContext(),"Este archivo ya ha sido anexado al tratamiento",Toast.LENGTH_LONG).show();
        }

    }

    public void procesarRespuesta(String strJsonResponseTmp){
        String strJsonResponse = strJsonResponseTmp;

        if(strJsonResponseTmp.startsWith("ï»¿")){
            strJsonResponse = strJsonResponseTmp.substring(3);
        }
        //Toast.makeText(context,strJsonResponse,Toast.LENGTH_LONG).show();
        try{
            JSONObject response = new JSONObject(strJsonResponse);
            if(response.getInt("estado")==0){
                int id = response.getInt("id");
                String nombre = response.getString("nombre");
                String formato = response.getString("formato");
                String tmpFecha = response.getString("fecha");
                String fecha = getStrFechaFormateada(formatearFechaString(tmpFecha));
                mediaSubida.put(id,nombre);
                Toast.makeText(context,"Archivo en formato "+formato+" añadido exitosamente.\n"+fecha,Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,response.getString("mensaje"),Toast.LENGTH_LONG).show();
            }

        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context,"Error en la respuesta recibida",Toast.LENGTH_LONG).show();
        }catch(Exception ex){
            ex.printStackTrace();
            Toast.makeText(context,"Error desconocido",Toast.LENGTH_LONG).show();
        }

    }


    private class UploadFileToServer extends AsyncTask<Void, Integer,String> {

        @Override
        public void onPreExecute(){
            progressDialog.setProgress(0);
            progressDialog.setMessage("0%");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            // updating progress bar value
            progressDialog.setProgress(progress[0]);
            // updating percentage value
            progressDialog.setMessage(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(hostServer);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(finalFilePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server

                try{
                    entity.addPart("idTratamiento",new StringBody(String.valueOf(idTratamiento)));
                    entity.addPart("idTerapeuta", new StringBody(String.valueOf(idTerapeuta)));
                    entity.addPart("apikey", new StringBody(T.apikey));
                }catch(Exception ex){

                }


                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            progressDialog.dismiss();
            procesarRespuesta(result);
            super.onPostExecute(result);
        }


    }

}
