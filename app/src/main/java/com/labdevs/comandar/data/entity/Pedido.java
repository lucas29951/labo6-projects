package com.labdevs.comandar.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.labdevs.comandar.data.entity.enums.EstadoPedido;

import java.util.Date;

@Entity(tableName = "pedidos",
        foreignKeys = {
                @ForeignKey(entity = Mesa.class,
                        parentColumns = "mesa_id",
                        childColumns = "mesa_id",
                        onDelete = ForeignKey.CASCADE), // Si se borra una mesa, se borra su pedido
                @ForeignKey(entity = Camarero.class,
                        parentColumns = "camarero_id",
                        childColumns = "camarero_id",
                        onDelete = ForeignKey.RESTRICT) // No se puede borrar un camarero si tiene pedidos
        })
public class Pedido {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pedido_id")
    public int pedidoId;

    @ColumnInfo(name = "mesa_id", index = true)
    public int mesaId;

    @ColumnInfo(name = "camarero_id", index = true)
    public int camareroId;

    @NonNull
    @ColumnInfo(name = "fecha_hora_creacion")
    public Date fechaHoraCreacion;

    @NonNull
    public EstadoPedido estado;

    @ColumnInfo(name = "notas_generales")
    public String notasGenerales;

    public Pedido() {
        this.fechaHoraCreacion = new Date();
        this.estado = EstadoPedido.abierto;
    }
}
