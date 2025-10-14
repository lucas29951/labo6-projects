package com.labdevs.comandar.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "camareros",
        indices = {@Index(value = {"email"}, unique = true)})
public class Camarero {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "camarero_id")
    public int camareroId;

    @NonNull
    public String nombre;

    @NonNull
    public String apellido;

    @NonNull
    public String email;

    @NonNull
    @ColumnInfo(name = "password_hash")
    public String passwordHash;

    @ColumnInfo(name = "numero_contacto")
    public String numeroContacto;

    @ColumnInfo(name = "foto_url")
    public String fotoUrl;

    @NonNull
    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    public Date createdAt;
}
