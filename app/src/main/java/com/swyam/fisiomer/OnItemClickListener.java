package com.swyam.fisiomer;

import entidad.Paciente;
import entidad.Tratamiento;

/**
 * Created by Reyes Yam on 25/02/2017.
 */
public interface OnItemClickListener {
    void onItemClick(Paciente item);
    void onItemClick(Tratamiento item);

}
