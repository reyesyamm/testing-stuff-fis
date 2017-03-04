package com.swyam.fisiomer;

import entidad.TratamientoAnalgesico;
import entidad.TratamientoFuncional;
import entidad.TratamientoPreventivo;

/**
 * Created by Reyes Yam on 28/02/2017.
 */
public interface OnItemClickListenerT {

    void onItemClickTF(TratamientoFuncional tratamiento);
    void onItemClickTP(TratamientoPreventivo tratamiento);
    void onItemClickTA(TratamientoAnalgesico tratamiento);
}
