package com.lavanderia.sistemalundry;

public class DetallePedido {
    private int idDetalle;
    private int idPedido;
    private int idServicioConfig;
    private String descripcionDetailed;
    private double montoServicio;

    // Constructor vacío
    public DetallePedido() {}

    // Constructor lleno
    public DetallePedido(int idPedido, int idServicioConfig, String descripcionDetailed, double montoServicio) {
        this.idPedido = idPedido;
        this.idServicioConfig = idServicioConfig;
        this.descripcionDetailed = descripcionDetailed;
        this.montoServicio = montoServicio;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdServicioConfig() { return idServicioConfig; }
    public void setIdServicioConfig(int idServicioConfig) { this.idServicioConfig = idServicioConfig; }

    public String getDescripcionDetailed() { return descripcionDetailed; }
    public void setDescripcionDetailed(String descripcionDetailed) { this.descripcionDetailed = descripcionDetailed; }

    public double getMontoServicio() { return montoServicio; }
    public void setMontoServicio(double montoServicio) { this.montoServicio = montoServicio; }
}