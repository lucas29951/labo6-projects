package com.labdevs.comandar.data.model;

import androidx.room.Embedded;

import com.labdevs.comandar.data.entity.Mesa;

// POJO para el resultado de la consulta JOIN. No es una @Entity.
public class MesaConCamarero {

    @Embedded
    public Mesa mesa;

    // Campos adicionales de la tabla Camarero
    public String nombreCamarero;
    public String apellidoCamarero;
}
