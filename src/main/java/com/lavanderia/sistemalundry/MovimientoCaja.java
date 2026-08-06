package com.lavanderia.sistemalundry;

public class MovimientoCaja {
    private int idMovimiento; // AGREGADO para poder borrarlo
    private String hora;
    private String tipo;
    private String concepto;
    private String responsable;
    private String metodo;
    private double monto;
    private int idCorte;

    // Getters y Setters
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getResponsable() {
        return (responsable != null && !responsable.trim().isEmpty()) ? responsable : "Paulina Martínez";
    }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    // Compatibilidad Thymeleaf
    public String getMetodoPago() { return metodo; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public int getIdCorte() { return idCorte; }
    public void setIdCorte(int idCorte) { this.idCorte = idCorte; }
}