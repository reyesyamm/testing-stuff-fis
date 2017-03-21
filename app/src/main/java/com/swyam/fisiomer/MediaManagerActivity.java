package com.swyam.fisiomer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaManagerActivity extends AppCompatActivity {

    Context context;
    int idTratamiento;
    ImageButton btnNuevaImagen, btnNuevoVideo, btnImportar;
    ImageView imagenPrevia;
    VideoView videoPrevio;
    View contenedorBotones;
    ImageButton btnPlay,btnPause;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int IMPORT_IMAGE_REQUEST_CODE = 300;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Uri fileUri;

    int type_media = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        idTratamiento = getIntent().getIntExtra("idTratamiento",-1);
        solicitarPermisos();

        context = getBaseContext();
        btnNuevaImagen = (ImageButton) findViewById(R.id.btnNuevaImagen);
        btnNuevoVideo = (ImageButton) findViewById(R.id.btnNuevoVideo);
        btnImportar = (ImageButton) findViewById(R.id.btnImportarMedia);
        imagenPrevia = (ImageView) findViewById(R.id.image_previa_media);
        videoPrevio = (VideoView) findViewById(R.id.video_previa_media);
        contenedorBotones = findViewById(R.id.contenedor_botones_video);
        btnPlay = (ImageButton) findViewById(R.id.btnPlayVideo);
        btnPause = (ImageButton) findViewById(R.id.btnPauseVideo);

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

}
