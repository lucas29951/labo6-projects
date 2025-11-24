package com.labdevs.comandar.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "notificaciones",
        foreignKeys = @ForeignKey(entity = Mesa.class,
                parentColumns = "mesa_id",
                childColumns = "mesa_id",
                onDelete = ForeignKey.CASCADE), // Si se borra la mesa, se borran sus notificaciones
        indices = {@Index(value = {"mesa_id"})})
public class Notificacion {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notificacion_id")
    public int notificacionId;

    @ColumnInfo(name = "mesa_id")
    public int mesaId;

    @NonNull
    public String mensaje;

    @ColumnInfo(name = "area_origen")
    public String areaOrigen; // Ej: "Cocina", "Barra"

    @NonNull
    @ColumnInfo(name = "fecha_hora_creacion")
    public Date fechaHoraCreacion;

    public boolean leida;

    // Constructor para establecer valores por defecto (default: now(), default: false)
    public Notificacion() {
        this.fechaHoraCreacion = new Date();
        this.leida = false;
    }
}
