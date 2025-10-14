package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.labdevs.comandar.data.entity.DetallePedido;
import com.labdevs.comandar.data.model.ItemPedido;

import java.util.List;

@Dao
public interface DetallePedidoDao {
    // Requisito: "Para cada producto solicitado, puede agregar..."
    // Usamos REPLACE: si se añade el mismo producto, se actualiza la entrada (ej. para cambiar cantidad)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(DetallePedido detalle);

    @Delete
    void delete(DetallePedido detalle);

    // Requisito: "La app calcula el total para su cobro" y muestra los ítems.
    // Usamos el POJO "ItemPedido".
    @Transaction
    @Query("SELECT " +
            "d.producto_id, p.nombre, d.cantidad, d.precio_unitario, d.caracteristicas_particulares " +
            "FROM detalles_pedido d " +
            "JOIN productos p ON d.producto_id = p.producto_id " +
            "WHERE d.pedido_id = :pedidoId")
    LiveData<List<ItemPedido>> getItemsPedidoConNombre(int pedidoId);
}
