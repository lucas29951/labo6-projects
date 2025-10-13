package com.labdevs.comandar.entity;

public class Camarero {
    private Integer camarero_id;
    private String nombre;
    private String apellido;
    private String email;
    private String password_hash;
    private String numero_contacto;
    private String foto_url;

    public Camarero(Integer id, String nombre, String apellido, String email, String password_hash, String numero_contacto, String foto_url) {
        this.camarero_id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password_hash = password_hash;
        this.numero_contacto = numero_contacto;
        this.foto_url = foto_url;
    }

    public Integer getId() {
        return camarero_id;
    }

    public void setId(Integer id) {
        this.camarero_id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getNumero_contacto() {
        return numero_contacto;
    }

    public void setNumero_contacto(String numero_contacto) {
        this.numero_contacto = numero_contacto;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }
}
