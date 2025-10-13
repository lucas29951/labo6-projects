package com.labdevs.comandar.entity;

import com.labdevs.comandar.entity.enums.EstadoMesa;

public class Mesa {
    private Integer mesa_id;
    private int numero_mesa;
    private int capacidad;
    private EstadoMesa estado;
    private String posicion_mapa;
    private Integer camarero_id;

    public Mesa(Integer mesa_id, int numero_mesa, int capacidad, EstadoMesa estado, String posicion_mapa, Integer camarero_id) {
        this.mesa_id = mesa_id;
        this.numero_mesa = numero_mesa;
        this.capacidad = capacidad;
        this.estado = estado;
        this.posicion_mapa = posicion_mapa;
        this.camarero_id = camarero_id;
    }

    public Integer getMesa_id() {
        return mesa_id;
    }

    public void setMesa_id(Integer mesa_id) {
        this.mesa_id = mesa_id;
    }

    public int getNumero_mesa() {
        return numero_mesa;
    }

    public void setNumero_mesa(int numero_mesa) {
        this.numero_mesa = numero_mesa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoMesa getEstado() {
        return estado;
    }

    public void setEstado(EstadoMesa estado) {
        this.estado = estado;
    }

    public String getPosicion_mapa() {
        return posicion_mapa;
    }

    public void setPosicion_mapa(String posicion_mapa) {
        this.posicion_mapa = posicion_mapa;
    }

    public Integer getCamarero_id() {
        return camarero_id;
    }

    public void setCamarero_id(Integer camarero_id) {
        this.camarero_id = camarero_id;
    }
}
