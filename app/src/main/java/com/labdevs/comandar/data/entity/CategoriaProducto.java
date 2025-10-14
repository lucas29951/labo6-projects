package com.labdevs.comandar.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "categorias_producto",
        indices = {@Index(value = {"nombre_categoria"}, unique = true)})
public class CategoriaProducto {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoria_id")
    public int categoriaId;

    @NonNull
    @ColumnInfo(name = "nombre_categoria")
    public String nombreCategoria;
}
