package com.labdevs.comandar.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos",
        foreignKeys = @ForeignKey(entity = CategoriaProducto.class,
                parentColumns = "categoria_id",
                childColumns = "categoria_id",
                onDelete = ForeignKey.RESTRICT))
public class Producto {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "producto_id")
    public int productoId;

    @NonNull
    public String nombre;

    public String descripcion;

    public double precio;

    public boolean disponible;

    @ColumnInfo(name = "categoria_id", index = true)
    public int categoriaId;

    public Producto() {
        this.disponible = true;
    }
}
