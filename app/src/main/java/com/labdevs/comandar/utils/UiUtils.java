package com.labdevs.comandar.utils;

import androidx.annotation.ColorRes;

import com.labdevs.comandar.R;
import com.labdevs.comandar.data.entity.enums.EstadoMesa;

public class UiUtils {

    /**
     * Devuelve el ID del recurso de color correspondiente al estado de una mesa.
     * Es estático para poder llamarlo sin necesidad de instanciar la clase.
     *
     * @param estado El estado de la mesa.
     * @return El ID del recurso de color (ej. R.color.mesa_libre).
     */
    @ColorRes
    public static int getColorForMesaEstado(EstadoMesa estado) {
        switch (estado) {
            case libre:
                return R.color.mesa_libre;
            case ocupada:
                return R.color.mesa_ocupada;
            case reservada:
                return R.color.mesa_reservada;
            default:
                return R.color.mesa_default;
        }
    }
}
