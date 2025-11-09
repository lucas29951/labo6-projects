package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.labdevs.comandar.data.entity.Mesa;
import com.labdevs.comandar.data.model.MesaConCamarero;

import java.util.List;

@Dao
public interface MesaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mesa mesa);

    // Requisito: "El camarero se asigna una mesa" o "Desasignarse una mesa"
    @Update
    void update(Mesa mesa);

    // Requisito: "Debe poder ver un mapa gráfico con todas las mesas y su estado"
    // LiveData es perfecto aquí para que el mapa se actualice solo.
    @Query("SELECT * FROM mesas ORDER BY numero_mesa ASC")
    LiveData<List<Mesa>> getAllMesas();

    // Útil para una pantalla de "Mis Mesas"
    @Query("SELECT * FROM mesas WHERE camarero_id = :camareroId ORDER BY numero_mesa ASC")
    LiveData<List<Mesa>> getMesasByCamarero(int camareroId);

    // Nuevo método para inserción en lote para poblar la BD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Mesa> mesas);

    // --- NUEVO MÉTODO CON JOIN ---
    @Transaction
    @Query("SELECT mesas.*, camareros.nombre as nombreCamarero, camareros.apellido as apellidoCamarero " +
            "FROM mesas " +
            "LEFT JOIN camareros ON mesas.camarero_id = camareros.camarero_id " +
            "ORDER BY mesas.numero_mesa ASC")
    LiveData<List<MesaConCamarero>> getMesasConCamarero();

    @Query("SELECT COUNT(*) FROM mesas WHERE camarero_id = :camareroId")
    LiveData<Integer> countMesasAsignadas(int camareroId);

    @Query("UPDATE mesas SET camarero_id = NULL WHERE camarero_id = :camareroId")
    void desasignarTodasLasMesasDeCamarero(int camareroId); // Síncrono, para ejecutar en background

    @Query("SELECT * FROM mesas WHERE camarero_id = :camareroId ORDER BY numero_mesa ASC")
    List<Mesa> getMesasByCamareroSync(int camareroId);

    @Query("SELECT * FROM mesas WHERE mesa_id = :mesaId LIMIT 1")
    Mesa getMesaByIdSync(int mesaId);

    @Query("SELECT * FROM mesas WHERE mesa_id = :mesaId LIMIT 1")
    LiveData<Mesa> getMesaById(int mesaId); // Añadimos LiveData para observar
}
