package com.labdevs.comandar.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "detalles_pedido",
        primaryKeys = {"pedido_id", "producto_id"}, // Clave primaria compuesta
        foreignKeys = {
                @ForeignKey(entity = Pedido.class,
                        parentColumns = "pedido_id",
                        childColumns = "pedido_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Producto.class,
                        parentColumns = "producto_id",
                        childColumns = "producto_id",
                        onDelete = ForeignKey.RESTRICT)
        })
public class DetallePedido {
    @ColumnInfo(name = "pedido_id")
    public int pedidoId;

    @ColumnInfo(name = "producto_id")
    public int productoId;

    public int cantidad;

    @ColumnInfo(name = "precio_unitario")
    public double precioUnitario;

    @ColumnInfo(name = "caracteristicas_particulares")
    public String caracteristicasParticulares;

    public DetallePedido() {
        this.cantidad = 1;
    }
}
