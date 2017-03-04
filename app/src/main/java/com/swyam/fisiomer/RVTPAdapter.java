package com.swyam.fisiomer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import entidad.TratamientoPreventivo;

/**
 * Created by Reyes Yam on 01/03/2017.
 */
public class RVTPAdapter extends RecyclerView.Adapter<RVTPAdapter.TPViewHolder> {

    List<TratamientoPreventivo> tps;
    Context context;
    OnItemClickListenerT onItemClickListenerT;

    RVTPAdapter(Context context, List<TratamientoPreventivo> tps){
        this.context = context;
        this.tps = tps;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TPViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_tprev, parent, false);
        TPViewHolder tpvh = new TPViewHolder(v);
        return tpvh;
    }

    @Override
    public void onBindViewHolder(TPViewHolder holder, int position) {
        final TratamientoPreventivo tp = tps.get(position);
        holder.nombreTratamiento.setText(tp.tratamiento);
        holder.descripcionTratamiento.setText(tp.resumenTratamiento());

        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onItemClickListenerT.onItemClickTP(tp);
            }
        };

        holder.btnEliminar.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return tps.size();
    }

    public OnItemClickListenerT getOnItemClickListenerT(){return onItemClickListenerT; }

    public void setOnItemClickListenerT(OnItemClickListenerT onItemClickLListener){
        this.onItemClickListenerT = onItemClickLListener;
    }

    public static class TPViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView nombreTratamiento;
        TextView descripcionTratamiento;
        ImageButton btnEliminar;
        public TPViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_tratamiento_preventivo);
            nombreTratamiento = (TextView) itemView.findViewById(R.id.tv_nombre_tratamiento);
            descripcionTratamiento = (TextView) itemView.findViewById(R.id.tv_descripcion_tratamiento);
            btnEliminar = (ImageButton) itemView.findViewById(R.id.btn_eliminar_tp);
        }
    }
}
