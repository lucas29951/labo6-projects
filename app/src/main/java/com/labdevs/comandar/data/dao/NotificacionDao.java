package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.labdevs.comandar.data.entity.Notificacion;

import java.util.List;

@Dao
public interface NotificacionDao {

    @Insert
    void insert(Notificacion notificacion);

    @Update
    void update(Notificacion notificacion);

    // Obtener todas las notificaciones de una mesa específica
    @Query("SELECT * FROM notificaciones WHERE mesa_id = :mesaId ORDER BY fecha_hora_creacion DESC")
    LiveData<List<Notificacion>> getNotificacionesPorMesa(int mesaId);

    // Obtener notificaciones NO leídas (útil para alertas globales)
    @Query("SELECT * FROM notificaciones WHERE leida = 0 ORDER BY fecha_hora_creacion ASC")
    LiveData<List<Notificacion>> getNotificacionesNoLeidas();

    // Marcar todas las de una mesa como leídas
    @Query("UPDATE notificaciones SET leida = 1 WHERE mesa_id = :mesaId")
    void marcarTodasComoLeidasPorMesa(int mesaId);
}
