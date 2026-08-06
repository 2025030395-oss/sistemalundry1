package com.lavanderia.sistemalundry;

import java.util.HashMap;
import java.util.Map;

public class PedidoForm {
    // ID del pedido
    private Integer idPedido;

    // Campos para la lista/tabla
    private String orden;
    private String estado = "En proceso"; // Por defecto al registrar
    private String fechaEntrega = "06/06/2026";

    // Variables originales
    private String tipoServicio = "Lavado Pesado / Industrial";
    private Map<String, Integer> prendas = new HashMap<>();
    private String modalidad = "compartido";
    private double pesoEstimado = 0.0;
    private int totalPiezas = 0;
    private String colorRopa = "No seleccionado";
    private String nombreCliente;
    private String telefonoCliente;
    private String idCliente;

    // Valores monetarios
    private double subtotal = 0.0;
    private double iva = 0.0;
    private double total = 0.0;

    // 1. Constructor vacío obligatorio
    public PedidoForm() {}

    // 2. NUEVO CONSTRUCTOR (Requerido por PedidoDAO al consultar la BD)
    public PedidoForm(int idPedido, String nombreCliente, String tipoServicio, int totalPiezas, String estado, double total) {
        this.idPedido = idPedido;
        this.orden = "#" + idPedido;
        this.nombreCliente = nombreCliente;
        this.tipoServicio = tipoServicio;
        this.totalPiezas = totalPiezas;
        this.estado = estado;
        this.total = total;
    }

    // 3. Constructor rápido con orden en String
    public PedidoForm(String orden, String nombreCliente, String tipoServicio, int totalPiezas, String estado, double total) {
        this.orden = orden;
        this.nombreCliente = nombreCliente;
        this.tipoServicio = tipoServicio;
        this.totalPiezas = totalPiezas;
        this.estado = estado;
        this.total = total;
    }

    public void calcularPrecios(double precioPorKg) {
        this.subtotal = (this.pesoEstimado > 0 ? this.pesoEstimado : 1.0) * precioPorKg;
        if (this.subtotal < 150.0) this.subtotal = 150.0;
        this.iva = this.subtotal * 0.16;
        this.total = this.subtotal + this.iva;
    }

    // Getters y Setters para idPedido
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
        if (this.orden == null) {
            this.orden = "#" + idPedido;
        }
    }

    // Getters y Setters
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(String tipoServicio) { this.tipoServicio = tipoServicio; }

    public Map<String, Integer> getPrendas() { return prendas; }
    public void setPrendas(Map<String, Integer> prendas) { this.prendas = prendas; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public double getPesoEstimado() { return pesoEstimado; }
    public void setPesoEstimado(double pesoEstimado) { this.pesoEstimado = pesoEstimado; }

    // GETTER Y SETTER DE PIEZAS CORREGIDOS
    public int getTotalPiezas() {
        if (this.totalPiezas == 0 && this.prendas != null && !this.prendas.isEmpty()) {
            int suma = 0;
            for (Integer cantidad : this.prendas.values()) {
                if (cantidad != null) {
                    suma += cantidad;
                }
            }
            return suma;
        }
        return totalPiezas;
    }

    public void setTotalPiezas(int totalPiezas) {
        this.totalPiezas = totalPiezas;
    }

    public String getColorRopa() { return colorRopa; }
    public void setColorRopa(String colorRopa) { this.colorRopa = colorRopa; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getIva() { return iva; }
    public void setIva(double iva) { this.iva = iva; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}