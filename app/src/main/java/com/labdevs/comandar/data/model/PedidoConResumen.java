package com.labdevs.comandar.data.model;

import androidx.room.Embedded;

import com.labdevs.comandar.data.entity.Pedido;

public class PedidoConResumen {

    @Embedded
    public Pedido pedido;

    public int totalItems;

    public double totalPrecio;
}
