package com.labdevs.comandar.data.model;

import androidx.room.ColumnInfo;

// Esta clase no es una tabla (@Entity), es un contenedor para el resultado
// de una consulta con JOIN.
public class ItemPedido {
    @ColumnInfo(name = "producto_id")
    public int productoId;

    @ColumnInfo(name = "nombre")
    public String nombreProducto;

    public int cantidad;

    @ColumnInfo(name = "precio_unitario")
    public double precioUnitario;

    @ColumnInfo(name = "caracteristicas_particulares")
    public String caracteristicasParticulares;

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}