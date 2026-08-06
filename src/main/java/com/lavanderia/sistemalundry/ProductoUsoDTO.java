package com.lavanderia.sistemalundry;

public class ProductoUsoDTO {
    private String nombre;
    private String cantidadConUnidad;

    public ProductoUsoDTO(String nombre, String cantidadConUnidad) {
        this.nombre = nombre;
        this.cantidadConUnidad = cantidadConUnidad;
    }

    public String getNombre() { return nombre; }
    public String getCantidadConUnidad() { return cantidadConUnidad; }
}