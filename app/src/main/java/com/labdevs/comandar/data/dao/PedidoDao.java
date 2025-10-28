package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.labdevs.comandar.data.entity.Pedido;

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
}
