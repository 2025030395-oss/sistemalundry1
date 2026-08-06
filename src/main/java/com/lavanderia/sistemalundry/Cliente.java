package com.lavanderia.sistemalundry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cliente {
    // 1. Agregamos la variable para el ID
    private int id;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private int pedidos;
    private String ultimoPedido;
    private String estado;

    // Constructor completo (incluyendo ID por si lees desde la BD)
    public Cliente(int id, String nombre, String telefono, String correo, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.pedidos = 0;
        this.ultimoPedido = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.estado = "Activo";
    }

    // Constructor sin ID (útil para crear un nuevo cliente antes de guardarlo en BD)
    public Cliente(String nombre, String telefono, String correo, String direccion) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.pedidos = 0;
        this.ultimoPedido = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.estado = "Activo";
    }

    public Cliente() {

    }

    // --- GETTERS Y SETTERS ---

    // 2. Agregamos el Getter y Setter para 'id'
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getPedidos() { return pedidos; }
    public void setPedidos(int pedidos) { this.pedidos = pedidos; }

    public String getUltimoPedido() { return ultimoPedido; }
    public void setUltimoPedido(String ultimoPedido) { this.ultimoPedido = ultimoPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}