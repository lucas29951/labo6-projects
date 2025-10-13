package com.labdevs.comandar.entity;

import com.labdevs.comandar.entity.enums.EstadoPedido;

import java.util.Date;

public class Pedido {
    private Integer pedido_id;
    private Integer mesa_id;
    private Integer camarero_id;
    private Date fecha_hora_creacion;
    private EstadoPedido estado;
    private String notas_generales;

    public Pedido(Integer pedido_id, Integer mesa_id, Integer camarero_id, Date fecha_hora_creacion, EstadoPedido estado, String notas_generales) {
        this.pedido_id = pedido_id;
        this.mesa_id = mesa_id;
        this.camarero_id = camarero_id;
        this.fecha_hora_creacion = fecha_hora_creacion;
        this.estado = estado;
        this.notas_generales = notas_generales;
    }

    public Integer getPedido_id() {
        return pedido_id;
    }

    public void setPedido_id(Integer pedido_id) {
        this.pedido_id = pedido_id;
    }

    public Integer getMesa_id() {
        return mesa_id;
    }

    public void setMesa_id(Integer mesa_id) {
        this.mesa_id = mesa_id;
    }

    public Integer getCamarero_id() {
        return camarero_id;
    }

    public void setCamarero_id(Integer camarero_id) {
        this.camarero_id = camarero_id;
    }

    public Date getFecha_hora_creacion() {
        return fecha_hora_creacion;
    }

    public void setFecha_hora_creacion(Date fecha_hora_creacion) {
        this.fecha_hora_creacion = fecha_hora_creacion;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getNotas_generales() {
        return notas_generales;
    }

    public void setNotas_generales(String notas_generales) {
        this.notas_generales = notas_generales;
    }
}
