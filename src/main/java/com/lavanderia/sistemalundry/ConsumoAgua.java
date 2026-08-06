package com.lavanderia.sistemalundry;

import java.time.LocalDate;

public class ConsumoAgua {
    private int id;
    private LocalDate fecha;
    private double litrosConsumidos;
    private double kilosLavados;
    private double costoEstimado;
    private String origen;
    private int idPedido;

    public ConsumoAgua(LocalDate fecha, double litrosConsumidos, double kilosLavados, double costoEstimado, String origen, int idPedido) {
        this.fecha = fecha;
        this.litrosConsumidos = litrosConsumidos;
        this.kilosLavados = kilosLavados;
        this.costoEstimado = costoEstimado;
        this.origen = origen;
        this.idPedido = idPedido;
    }

    // Getters
    public LocalDate getFecha() { return fecha; }
    public double getLitrosConsumidos() { return litrosConsumidos; }
    public double getKilosLavados() { return kilosLavados; }
    public double getCostoEstimado() { return costoEstimado; }
    public String getOrigen() { return origen; }
    public int getIdPedido() { return idPedido; }
}