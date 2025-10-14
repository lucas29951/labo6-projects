package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.labdevs.comandar.data.entity.Producto;

import java.util.List;

@Dao
public interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Producto> productos);

    // Requisito: Mostrar los productos de una categoría seleccionada
    @Query("SELECT * FROM productos WHERE categoria_id = :categoriaId AND disponible = 1 ORDER BY nombre ASC")
    LiveData<List<Producto>> getProductosByCategoria(int categoriaId);
}
