package com.lavanderia.sistemalundry;

public class Maquinaria {
    // 1. Atributos
    private int idEquipo;
    private String nombreEquipo;
    private String tipo;
    private String capacidad;
    private String estadoOperacion;
    private int idPedidoActual;

    // 2. Constructor vacío
    public Maquinaria() {}

    // 3. GETTERS Y SETTERS (Revisa que este se llame exactamente setIdEquipo)
    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getCapacidad() { return capacidad; }
    public void setCapacidad(String capacidad) { this.capacidad = capacidad; }

    public String getEstadoOperacion() { return estadoOperacion; }
    public void setEstadoOperacion(String estadoOperacion) { this.estadoOperacion = estadoOperacion; }

    public int getIdPedidoActual() { return idPedidoActual; }
    public void setIdPedidoActual(int idPedidoActual) { this.idPedidoActual = idPedidoActual; }
}