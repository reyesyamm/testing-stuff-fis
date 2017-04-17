package com.swyam.fisiomer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import entidad.Multimedia;

import static com.swyam.fisiomer.Connection.UPLOADS_SERVER_PATH;
import static com.swyam.fisiomer.Connection.parsearError;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MultimediaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultimediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultimediaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View v;
    RecyclerView rv;
    RVTMAdapter adapter;

    ProgressDialog progressDialog;

    List<Multimedia> lmul = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MultimediaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultimediaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultimediaFragment newInstance(String param1, String param2) {
        MultimediaFragment fragment = new MultimediaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        lmul = ((TreatmentDetailsActivity)getActivity()).obtenerArchivosMultimedia();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(true);
        progressDialog.setProgress(0);
        progressDialog.setTitle("Descargando");
        progressDialog.setMessage("Espere porfavor");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_multimedia, container, false);
        rv = (RecyclerView) v.findViewById(R.id.rv_lista_multimedia);
        //RecyclerView.LayoutManager llm = new GridLayoutManager(getActivity(),2);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        //rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
        //rv.setItemAnimator(new DefaultItemAnimator());


        if(lmul!=null){
            adapter = new RVTMAdapter(getContext(),lmul);
            rv.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListenerM() {
                @Override
                public void OnClick(Multimedia m, int position) {
                    //Toast.makeText(getContext(),"Archivo: "+m.nombreArchivo,Toast.LENGTH_SHORT).show();
                    if(m.existe()){
                        //mostrarDialogoPrevio(position);
                        if(m.obtenerTipoArchivo().equals("FOTO")){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(m.fileUri(), "image/*");
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(m.fileUri(), "video/*");
                            startActivity(intent);
                        }

                    }else{
                        /*if(m.obtenerTipoArchivo().equals("VIDEO")){
                            mostrarDialogoPrevio(position);
                        }else{
                            peticionDescargar(m,position);
                        }*/
                        peticionDescargar(m,position);

                    }
                }
            });
        }

        return v;
    }

    public void procesarResultadoDescarga(boolean descargado, boolean esImagen, int posicion){
        if(descargado){
            adapter.notifyItemChanged(posicion);
        }else{
            Toast.makeText(getContext(),"Error al intentar descargar el archivo",Toast.LENGTH_SHORT).show();
        }
    }

    public void peticionDescargar(final Multimedia m,final int position){
        //Toast.makeText(getContext(),"Archivo a descargar: "+m.nombreArchivo,Toast.LENGTH_SHORT).show();

        String url = Connection.getHostServer(getContext())+UPLOADS_SERVER_PATH+m.nombreArchivo;
        if(m.obtenerTipoArchivo().equals("VIDEO")){
            //progressDialog.dismiss();
            try{
                final DownloadTask downloadTask = new DownloadTask(false,position);
                downloadTask.execute(url,m.fileObj().getAbsolutePath());
            }catch(Exception ex){
                ex.printStackTrace();
                Toast.makeText(getContext(),"Error intentando descargar el archivo",Toast.LENGTH_LONG);
            }

        }else{

            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Descargando archivo");
            progressDialog.setMessage("Espere porfavor");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            progressDialog.dismiss();
                            File nuevaImagen = m.fileObj();
                            boolean result = Helpers.guardarBitmap(response, nuevaImagen);
                            if(!result){
                                Toast.makeText(getContext(),"Error tratando de descargar el archivo",Toast.LENGTH_LONG).show();
                            }else{
                                adapter.notifyItemChanged(position);
                                Toast.makeText(getContext(),"Archivo descargado",Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 0, 0,
                    null
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(),parsearError(error),Toast.LENGTH_SHORT).show();
                }
            }
            );

            VolleySingleton.getInstance(getContext()).addToRequestQueue(imageRequest);
        }
    }


    int posicion_actual_media;
    public void mostrarDialogoPrevio(int position){
        posicion_actual_media = position;
        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_preview_media);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        dialog.setCancelable(false);
        Multimedia m = lmul.get(posicion_actual_media);
        dialog.setTitle(m.obtenerTipoArchivo());

        final ImageButton btnPlay, btnPause, btnPrevious, btnNext;
        final Button btnClose;
        final ImageView imageView;
        final VideoView videoView;
        final View contenendor_buffering;

        btnPlay = (ImageButton) dialog.findViewById(R.id.btn_play_video);
        btnPause = (ImageButton) dialog.findViewById(R.id.btn_pause_video);
        btnClose = (Button) dialog.findViewById(R.id.btn_cerrar_dialogo_multimedia);

        btnPrevious = (ImageButton) dialog.findViewById(R.id.btn_previo_multimedia);
        btnNext = (ImageButton) dialog.findViewById(R.id.btn_siguiente_multimedia);
        imageView = (ImageView) dialog.findViewById(R.id.image_multimedia);
        videoView = (VideoView) dialog.findViewById(R.id.video_multimedia);
        contenendor_buffering = dialog.findViewById(R.id.contenendor_buffering);

        videoView.setMediaController(new MediaController(getActivity()));

        contenendor_buffering.setVisibility(View.GONE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                contenendor_buffering.setVisibility(View.GONE);
                videoView.start();
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.getVisibility()==View.VISIBLE){
                    if(!videoView.isPlaying()){
                        videoView.start();
                    }
                }else{
                    Toast.makeText(getContext(),"El archivo actual no es video",Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.getVisibility()==View.VISIBLE){
                    if(videoView.isPlaying()){
                        videoView.pause();
                    }
                }else{
                    Toast.makeText(getContext(),"El archivo actual no es video",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.stopPlayback();
                }

                posicion_actual_media--;
                posicion_actual_media = ( posicion_actual_media<0 )?lmul.size()-1:posicion_actual_media;

                Multimedia mb = lmul.get(posicion_actual_media);
                if(mb.existe){
                    if("VIDEO".equals(mb.obtenerTipoArchivo())){
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(mb.fileUri());

                        contenendor_buffering.setVisibility(View.VISIBLE);

                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            // Close the progress bar and play the video
                            public void onPrepared(MediaPlayer mp) {
                                contenendor_buffering.setVisibility(View.GONE);
                                videoView.start();
                            }
                        });


                        //videoView.start();
                    }else{
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(mb.fileObj().getAbsolutePath()));
                    }
                }else{
                    if("VIDEO".equals(mb.obtenerTipoArchivo())){
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        String path = Connection.getHostServer(getContext())+Connection.UPLOADS_SERVER_PATH+mb.nombreArchivo;
                        videoView.setVideoURI(Uri.parse(path));

                        contenendor_buffering.setVisibility(View.VISIBLE);
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            // Close the progress bar and play the video
                            public void onPrepared(MediaPlayer mp) {
                                contenendor_buffering.setVisibility(View.GONE);
                                videoView.start();
                            }
                        });


                        //videoView.start();
                    }else{
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.logotransparente);
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.stopPlayback();
                }

                posicion_actual_media++;
                posicion_actual_media = ( posicion_actual_media>=lmul.size())?0:posicion_actual_media;

                Multimedia mf = lmul.get(posicion_actual_media);
                if(mf.existe){
                    if("VIDEO".equals(mf.obtenerTipoArchivo())){
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(mf.fileUri());

                        contenendor_buffering.setVisibility(View.VISIBLE);
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            // Close the progress bar and play the video
                            public void onPrepared(MediaPlayer mp) {
                                contenendor_buffering.setVisibility(View.GONE);
                                videoView.start();
                            }
                        });

                        //videoView.start();
                    }else{
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(mf.fileObj().getAbsolutePath()));
                    }
                }else{
                    if("VIDEO".equals(mf.obtenerTipoArchivo())){
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        String path = Connection.getHostServer(getContext())+Connection.UPLOADS_SERVER_PATH+mf.nombreArchivo;
                        videoView.setVideoURI(Uri.parse(path));
                        contenendor_buffering.setVisibility(View.VISIBLE);
                        //videoView.start();
                    }else{
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.logotransparente);
                    }
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    videoView.stopPlayback();
                }

                dialog.dismiss();
            }
        });

        if(m.obtenerTipoArchivo().equals("VIDEO")){
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            if(m.existe()){
                videoView.setVideoURI(m.fileUri());
            }else{
                String path = Connection.getHostServer(getContext())+Connection.UPLOADS_SERVER_PATH+m.nombreArchivo;
                videoView.setVideoURI(Uri.parse(path));
            }

            contenendor_buffering.setVisibility(View.VISIBLE);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    contenendor_buffering.setVisibility(View.GONE);
                    videoView.start();
                }
            });

            //videoView.start();
        }else{
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            imageView.setImageBitmap(BitmapFactory.decodeFile(m.fileObj().getAbsolutePath()));
        }

        dialog.show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private boolean tipoArchivo;
        private int posicionLmul;

        public DownloadTask(boolean tipoArchivo, int posicionLmul) {
            this.tipoArchivo = tipoArchivo;
            this.posicionLmul = posicionLmul;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                File fout = new File(sUrl[1]);
                input = connection.getInputStream();
                output = new FileOutputStream(fout);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            progressDialog.show();
            progressDialog.setTitle("Descargando");
            progressDialog.setMessage("Espere porfavor");
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false

            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            /*if (result != null)
                Toast.makeText(getContext(),"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getContext(),"File downloaded", Toast.LENGTH_SHORT).show();*/

            procesarResultadoDescarga(result==null, tipoArchivo, posicionLmul);
        }


    }
}
