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

import entidad.TratamientoAnalgesico;

/**
 * Created by Reyes Yam on 02/03/2017.
 */
public class RVTAAdapter extends RecyclerView.Adapter<RVTAAdapter.TAViewHolder> {

    List<TratamientoAnalgesico> tas;
    Context context;
    OnItemClickListenerT onItemClickListenerT;

    RVTAAdapter(Context context, List<TratamientoAnalgesico> tas){
        this.tas = tas;
        this.context = context;
    }

    public void setOnItemClickListenerT(OnItemClickListenerT onItemClickListenerT){
        this.onItemClickListenerT = onItemClickListenerT;
    }

    public OnItemClickListenerT getOnItemClickListenerT(){
        return this.onItemClickListenerT;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public TAViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_tan,parent,false);
        TAViewHolder ta = new TAViewHolder(v);
        return ta;
    }

    @Override
    public void onBindViewHolder(TAViewHolder holder, int position) {
        final TratamientoAnalgesico ta = tas.get(position);
        holder.nombreTratamiento.setText(ta.tratamiento);
        holder.resumen.setText(ta.resumenTratamiento());
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onItemClickListenerT.onItemClickTA(ta);
            }
        };

        holder.btnEliminar.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return tas.size();
    }

    public static class TAViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView nombreTratamiento,resumen;
        ImageButton btnEliminar;
        public TAViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_tratamiento_analgesico);
            nombreTratamiento = (TextView) itemView.findViewById(R.id.tv_nombre_tratamiento);
            resumen = (TextView) itemView.findViewById(R.id.tv_descripcion_tratamiento);
            btnEliminar = (ImageButton) itemView.findViewById(R.id.btn_eliminar_ta);
        }
    }
}