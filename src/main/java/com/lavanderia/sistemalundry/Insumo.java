package com.lavanderia.sistemalundry;

import java.util.Date;

public class Insumo {
    private int idProducto;
    private String nombreProducto;
    private String categoria;
    private String unidadMedida;
    private String presentacion;
    private double stockActual;
    private double stockMinimo;
    private double stockMaximo;
    private String proveedor;
    private Date fechaCompra;
    private double costoUsoServicio;

    // Constructor vacío
    public Insumo() {}

    // Constructor rápido para tus listados o formularios
    public Insumo(String nombreProducto, String categoria, double stockActual, double stockMinimo, double costoUsoServicio) {
        this.nombreProducto = nombreProducto;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.costoUsoServicio = costoUsoServicio;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }

    public double getStockActual() { return stockActual; }
    public void setStockActual(double stockActual) { this.stockActual = stockActual; }

    public double getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(double stockMinimo) { this.stockMinimo = stockMinimo; }

    public double getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(double stockMaximo) { this.stockMaximo = stockMaximo; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public Date getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(Date fechaCompra) { this.fechaCompra = fechaCompra; }

    public double getCostoUsoServicio() { return costoUsoServicio; }
    public void setCostoUsoServicio(double costoUsoServicio) { this.costoUsoServicio = costoUsoServicio; }
}