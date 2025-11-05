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
    // Ahora puede buscar por una lista de IDs de categoría
    @Query("SELECT * FROM productos WHERE categoria_id IN (:categoriaIds) ORDER BY disponible DESC, nombre ASC")
    LiveData<List<Producto>> getProductosByCategorias(List<Integer> categoriaIds);

    //Obtener todos los productos disponibles
    @Query("SELECT * FROM productos ORDER BY disponible DESC, nombre ASC")
    LiveData<List<Producto>> getAllProductos(); // Renombrado para más claridad

    //Obtener un producto por su ID para la pantalla de detalle
    @Query("SELECT * FROM productos WHERE producto_id = :productoId")
    LiveData<Producto> getProductoById(int productoId);

    //Buscar productos por nombre (usando LIKE)
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' ORDER BY disponible DESC, nombre ASC")
    LiveData<List<Producto>> searchProductosByName(String query);

    //Buscar productos por nombre y filtrando por categorías
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' AND categoria_id IN (:categoriaIds) ORDER BY disponible DESC, nombre ASC")
    LiveData<List<Producto>> searchProductosByNameAndCategory(String query, List<Integer> categoriaIds);
}
