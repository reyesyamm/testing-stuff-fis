package com.swyam.fisiomer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import entidad.Multimedia;

/**
 * Created by Reyes Yam on 28/03/2017.
 */

public class RVTMAdapter extends RecyclerView.Adapter<RVTMAdapter.MViewHolder> {

    List<Multimedia> lmul;
    Context context;
    OnItemClickListenerM listenerM;

    public RVTMAdapter(Context context, List<Multimedia> lmul){
        this.lmul = lmul;
        this.context = context;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_multimedia, parent, false);
        MViewHolder vm = new MViewHolder(v);
        return vm;
    }

    @Override
    public void onBindViewHolder(MViewHolder holder,final int position) {
        final Multimedia m = lmul.get(position);
        holder.tvFormatoFecha.setText(Helpers.Upper(m.formato)+" | "+m.obtenerFechaFormateada());
        holder.tvTipoArchivo.setText(m.obtenerTipoArchivo());
        holder.tvTerapeuta.setText("Terapeuta: "+m.terapeuta);

        if(m.existe()){
            if("VIDEO".equalsIgnoreCase(m.obtenerTipoArchivo())){
                holder.icono.setImageResource(R.drawable.ic_videocam_white_36dp);
                Bitmap bmThumbnail;
                bmThumbnail = ThumbnailUtils.createVideoThumbnail(m.fileObj().getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND);
                holder.thumb.setImageBitmap(bmThumbnail);
            }else{
                holder.thumb.setImageBitmap(Helpers.decodeSampledBitmapFromResource(context.getResources(), m.fileObj(), 96, 96));
                holder.icono.setImageResource(R.drawable.ic_camera_alt_white_36dp);
            }
            holder.tvInfoDescargado.setText("Click para ver archivo");
        }else{
            holder.icono.setImageResource(R.drawable.ic_cloud_white_36dp);
            holder.tvInfoDescargado.setText("Click para descargar");
        }

        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listenerM.OnClick(m,position);
            }
        };
        holder.v.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return lmul.size();
    }

    public OnItemClickListenerM getOnItemTouchListener(){
        return listenerM;
    }

    public void setOnItemClickListener(OnItemClickListenerM onItemClickLListener){
        this.listenerM = onItemClickLListener;
    }

    public void removeAt(int position) {
        lmul.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, lmul.size());
    }

    public static class MViewHolder extends RecyclerView.ViewHolder{
        TextView tvTipoArchivo, tvFormatoFecha, tvInfoDescargado, tvTerapeuta;
        ImageView thumb,icono;
        CardView cv;
        View v;
        public MViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_item_multimedia);
            v = itemView.findViewById(R.id.contenedor_item_multimedia);
            tvTipoArchivo = (TextView) itemView.findViewById(R.id.tv_tipo_archivo_multimedia);
            tvFormatoFecha = (TextView) itemView.findViewById(R.id.tv_formato_fecha_multimedia);
            tvInfoDescargado = (TextView) itemView.findViewById(R.id.tv_info_descargado);
            tvTerapeuta = (TextView) itemView.findViewById(R.id.tv_nombre_terapeuta_multimedia);
            icono = (ImageView) itemView.findViewById(R.id.image_view_icono_tipo_multimedia);
            thumb = (ImageView) itemView.findViewById(R.id.image_thumb_preview);

        }
    }
}
