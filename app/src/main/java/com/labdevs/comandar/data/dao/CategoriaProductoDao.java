package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.labdevs.comandar.data.entity.CategoriaProducto;

import java.util.List;

@Dao
public interface CategoriaProductoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<CategoriaProducto> categorias);

    // Requisito: Mostrar la lista de categorías en el menú
    @Query("SELECT * FROM categorias_producto ORDER BY nombre_categoria ASC")
    LiveData<List<CategoriaProducto>> getAllCategorias();
}
