package com.swyam.fisiomer;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import entidad.Tratamiento;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public class RVTAdapter extends RecyclerView.Adapter<RVTAdapter.TratamientoViewHolder>  {

    public static class TratamientoViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView fecha,terapeuta,resumen;
        View contenedor;
        ImageView ini,fin;
        TextView totalTFuns,totalTAnals,totalTPrevs;

        public TratamientoViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_paciente);
            fecha = (TextView) itemView.findViewById(R.id.tv_fecha_tratamiento);
            terapeuta = (TextView) itemView.findViewById(R.id.tv_nombre_terapeuta_tratamiento);
            resumen = (TextView) itemView.findViewById(R.id.tv_resumen_tratamiento);
            contenedor = itemView.findViewById(R.id.contenedor_detalles_tratamiento);
            ini =(ImageView) itemView.findViewById(R.id.image_estado_paciente_inicio);
            fin =(ImageView) itemView.findViewById(R.id.image_estado_paciente_fin);
            totalTFuns = (TextView) itemView.findViewById(R.id.tv_total_tfuns);
            totalTAnals = (TextView) itemView.findViewById(R.id.tv_total_tanals);
            totalTPrevs = (TextView) itemView.findViewById(R.id.tv_total_tprevs);
        }
    }

    List<Tratamiento> tratamientos;
    private OnItemClickListener onItemClickListener;
    private Context context;

    RVTAdapter(Context context,List<Tratamiento> tratamientos){
        this.context = context;
        this.tratamientos = tratamientos;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RVTAdapter.TratamientoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_tratamiento, parent, false);
        TratamientoViewHolder tvh = new TratamientoViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(RVTAdapter.TratamientoViewHolder holder, final int position){
        final Tratamiento tratamiento = tratamientos.get(position);
        holder.fecha.setText(tratamiento.getFechaFormateada());
        holder.terapeuta.setText("Terapeuta: "+tratamiento.terapeuta);
        holder.resumen.setText(tratamiento.obtenerResumen());
        holder.ini.setImageResource(tratamiento.estadoInicio);
        holder.fin.setImageResource(tratamiento.estadoFin);
        holder.totalTFuns.setText(tratamiento.totalFun()+"");
        holder.totalTAnals.setText(tratamiento.totalAna()+"");
        holder.totalTPrevs.setText(tratamiento.totalPrev()+"");
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onItemClickListener.onItemClick(tratamiento);
            }
        };
        holder.contenedor.setOnClickListener(listener);
    }

    @Override
    public int getItemCount(){
        return tratamientos.size();
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
