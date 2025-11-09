package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.labdevs.comandar.data.entity.Pedido;
import com.labdevs.comandar.data.entity.enums.EstadoPedido;
import com.labdevs.comandar.data.model.PedidoConResumen;

import java.util.Date;
import java.util.List;

@Dao
public interface PedidoDao {
    // Requisito: Iniciar la "confección del pedido" para una mesa
    @Insert
    long insert(Pedido pedido); // Devuelve el ID del nuevo pedido, muy útil

    // Requisito: "Reapertura", "Envío automático", "cerrada la comanda" (cambiar estado)
    @Update
    void update(Pedido pedido);

    // Requisito: Al seleccionar una mesa, ver si ya tiene un pedido abierto.
    @Query("SELECT * FROM pedidos WHERE mesa_id = :mesaId AND estado != 'cerrado' LIMIT 1")
    LiveData<Pedido> getPedidoActivoPorMesa(int mesaId);

    // --- NUEVO MÉTODO ---
    // Este método es síncrono y debe ser llamado desde un hilo de fondo.
    // Es perfecto para la lógica de negocio dentro del Repository.
    @Query("SELECT * FROM pedidos WHERE mesa_id = :mesaId AND estado != 'cerrado' LIMIT 1")
    Pedido getPedidoActivoPorMesaSync(int mesaId);

    @Query("SELECT COUNT(*) FROM pedidos WHERE mesa_id = :mesaId AND estado != 'cerrado'")
    int countPedidosActivosPorMesa(int mesaId);

    @Transaction
    @Query("SELECT p.*, " +
            "COUNT(d.producto_id) as totalItems, " +
            "SUM(d.cantidad * d.precio_unitario) as totalPrecio " +
            "FROM pedidos p " +
            // Usamos LEFT JOIN para incluir pedidos que aún no tienen items.
            "LEFT JOIN detalles_pedido d ON p.pedido_id = d.pedido_id " +
            // Filtramos por el ID del camarero.
            "WHERE p.camarero_id = :camareroId AND p.estado = :estado " +
            // NUEVA CONDICIÓN: Filtra por rango de fechas
            "AND p.fecha_hora_creacion BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY p.pedido_id " +
            "ORDER BY p.fecha_hora_creacion DESC")
    LiveData<List<PedidoConResumen>> getPedidosFiltrados(int camareroId, EstadoPedido estado, Date fechaInicio, Date fechaFin);

    @Delete
    void delete(Pedido pedido);
}
