package com.swyam.fisiomer;

import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

/**
 * Created by Reyes Yam on 28/02/2017.
 */
public interface OnItemClickListenerT {

    void onItemClickTF(int tratamiento, int position);
    void onItemClickTP(int tratamiento, int position);
    void onItemClickTA(int tratamiento, int position);
}
