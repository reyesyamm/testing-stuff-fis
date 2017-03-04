package com.swyam.fisiomer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import entidad.Paciente;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public class RVPAdapter extends RecyclerView.Adapter<RVPAdapter.PacienteViewHolder> {
    public static class PacienteViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView nombrePaciente;
        TextView fechaUltimoTratamiento;
        TextView ultimoTerapeuta;
        ImageButton btnIcon;
        View contenedor;

        PacienteViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_paciente);
            nombrePaciente = (TextView) itemView.findViewById(R.id.tv_nombre_paciente);
            fechaUltimoTratamiento = (TextView) itemView.findViewById(R.id.tv_ultimo_tratamiento);
            ultimoTerapeuta = (TextView) itemView.findViewById(R.id.tv_nombre_terapeuta);
            btnIcon = (ImageButton) itemView.findViewById(R.id.btnIconPaciente);
            contenedor = itemView.findViewById(R.id.contenedor_detalles_paciente);

        }
    }

    List<Paciente> pacientes;
    private OnItemClickListener onItemClickListener;

    RVPAdapter(List<Paciente> pacientes){
        this.pacientes = pacientes;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RVPAdapter.PacienteViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_paciente, parent, false);
        PacienteViewHolder pvh = new PacienteViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RVPAdapter.PacienteViewHolder holder, final int position){
        final Paciente paciente = pacientes.get(position);
        holder.nombrePaciente.setText(paciente.nombre);
        holder.fechaUltimoTratamiento.setText(paciente.edad+" años, su último tratamiento fue en: "+paciente.fecha_ultimo_tratamiento);
        holder.ultimoTerapeuta.setText("Por "+paciente.nombre_terapeuta);
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onItemClickListener.onItemClick(paciente);
            }
        };

        holder.btnIcon.setOnClickListener(listener);
        holder.contenedor.setOnClickListener(listener);
    }

    @Override
    public int getItemCount(){
        return pacientes.size();
    }

    public OnItemClickListener getOnItemClickListener(){
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


}
