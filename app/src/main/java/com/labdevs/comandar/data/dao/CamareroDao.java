package com.labdevs.comandar.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.labdevs.comandar.data.entity.Camarero;

@Dao
public interface CamareroDao {
    // Requisito: "Permitir a los camareros crear una cuenta"
    @Insert
    void insert(Camarero camarero);

    // Requisito: "Administrar su información personal"
    @Update
    void update(Camarero camarero);

    // Requisito: "Iniciar sesión" (necesitamos buscar por email)
    @Query("SELECT * FROM camareros WHERE email = :email LIMIT 1")
    Camarero getCamareroByEmail(String email); // No es LiveData, el login es una acción única

    // Útil para recuperar el perfil una vez logueado
    @Query("SELECT * FROM camareros WHERE camarero_id = :id LIMIT 1")
    LiveData<Camarero> getCamareroById(int id);

    //Usado para poblar la BD, inserta el Camarero y devuelve el id
    @Insert
    long insertAndGetId(Camarero camarero);
}
