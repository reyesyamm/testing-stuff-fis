package com.swyam.fisiomer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import entidad.Tratamiento;
import entidad.TratamientoFuncional;

/**
 * Created by Reyes Yam on 28/02/2017.
 */
public class RVTFAdapter extends RecyclerView.Adapter<RVTFAdapter.TFViewHolder> {

    List<TratamientoFuncional> tfs;
    Context context;
    OnItemClickListenerT onItemClickListener;

    public RVTFAdapter(Context context, List<TratamientoFuncional> pacientes){
        this.tfs = pacientes;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TFViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_tfun, parent, false);
        TFViewHolder tfvh = new TFViewHolder(v);
        return tfvh;
    }

    @Override
    public void onBindViewHolder(TFViewHolder holder, int position) {
        final TratamientoFuncional tf = tfs.get(position);
        holder.tvNombreTratamiento.setText(tf.nombreTratamiento());
        holder.tvDescripcionSimpleTratamiento.setText(tf.descripcionTratamiento());
        switch(tf.estadoPaciente){
            case 0: // bien
                holder.imagenEstadoPaciente.setImageResource(R.drawable.cara1);
                break;
            case 1: // mediobien
                holder.imagenEstadoPaciente.setImageResource(R.drawable.cara2);
                break;
            case 2: // mal
                holder.imagenEstadoPaciente.setImageResource(R.drawable.cara3);
                break;
            case 3: // muymal
                holder.imagenEstadoPaciente.setImageResource(R.drawable.cara4);
                break;
            default: //android
                holder.imagenEstadoPaciente.setImageResource(android.R.drawable.sym_def_app_icon);
                break;
        }

        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onItemClickListener.onItemClickTF(tf);
            }
        };

        holder.btnEliminarTratamiento.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return tfs.size();
    }

    public OnItemClickListenerT getOnItemTouchListener(){
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListenerT onItemClickLListener){
        this.onItemClickListener = onItemClickLListener;
    }

    public static class TFViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreTratamiento, tvDescripcionSimpleTratamiento;
        ImageView imagenEstadoPaciente;
        ImageButton btnEliminarTratamiento;
        CardView cv;

        public TFViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_tratamiento_funcional);
            tvNombreTratamiento = (TextView) itemView.findViewById(R.id.tv_nombre_tratamiento);
            tvDescripcionSimpleTratamiento = (TextView) itemView.findViewById(R.id.tv_descripcion_tratamiento);
            imagenEstadoPaciente = (ImageView) itemView.findViewById(R.id.imagen_estado_paciente);
            btnEliminarTratamiento = (ImageButton) itemView.findViewById(R.id.btn_eliminar_tf);
        }
    }
}
