package com.labdevs.comandar.data.database;

import androidx.room.TypeConverter;

import com.labdevs.comandar.data.entity.enums.EstadoMesa;
import com.labdevs.comandar.data.entity.enums.EstadoPedido;

import java.util.Date;


public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


    @TypeConverter
    public static String fromEstadoMesa(EstadoMesa estado) {
        return estado == null ? null : estado.name();
    }

    @TypeConverter
    public static EstadoMesa toEstadoMesa(String estado) {
        return estado == null ? null : EstadoMesa.valueOf(estado);
    }


    @TypeConverter
    public static String fromEstadoPedido(EstadoPedido estado) {
        return estado == null ? null : estado.name();
    }

    @TypeConverter
    public static EstadoPedido toEstadoPedido(String estado) {
        return estado == null ? null : EstadoPedido.valueOf(estado);
    }
}
