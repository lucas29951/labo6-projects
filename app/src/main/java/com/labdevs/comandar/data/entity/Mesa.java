package com.labdevs.comandar.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.labdevs.comandar.data.entity.enums.EstadoMesa;

@Entity(tableName = "mesas",
        foreignKeys = @ForeignKey(entity = Camarero.class,
                parentColumns = "camarero_id",
                childColumns = "camarero_id",
                onDelete = ForeignKey.SET_NULL)) // Si se borra un camarero, la mesa queda sin asignar
public class Mesa {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mesa_id")
    public int mesaId;

    @ColumnInfo(name = "numero_mesa")
    public int numeroMesa;

    public int capacidad;

    @NonNull
    public EstadoMesa estado;

    @ColumnInfo(name = "posicion_mapa")
    public String posicionMapa;

    @ColumnInfo(name = "camarero_id", index = true)
    public Integer camareroId; // Usamos Integer para permitir nulos

    public Mesa() {
        this.capacidad = 4;
        this.estado = EstadoMesa.libre;
    }
}
