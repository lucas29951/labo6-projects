package com.labdevs.comandar.entity;

public class DetallePedido {
    private Integer pedido_id;
    private Integer producto_id;
    private int cantidad;
    private Double precio_unitario;
    private String caracteristicas_particulares;

    public DetallePedido(Integer pedido_id, Integer producto_id, int cantidad, Double precio_unitario, String caracteristicas_particulares) {
        this.pedido_id = pedido_id;
        this.producto_id = producto_id;
        this.cantidad = cantidad;
        this.precio_unitario = precio_unitario;
        this.caracteristicas_particulares = caracteristicas_particulares;
    }

    public Integer getPedido_id() {
        return pedido_id;
    }

    public void setPedido_id(Integer pedido_id) {
        this.pedido_id = pedido_id;
    }

    public Integer getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(Integer producto_id) {
        this.producto_id = producto_id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(Double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public String getCaracteristicas_particulares() {
        return caracteristicas_particulares;
    }

    public void setCaracteristicas_particulares(String caracteristicas_particulares) {
        this.caracteristicas_particulares = caracteristicas_particulares;
    }
}
