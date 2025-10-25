package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.labdevs.comandar.data.entity.Mesa;

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
}
