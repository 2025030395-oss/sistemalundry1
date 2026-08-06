package com.lavanderia.sistemalundry;

import java.sql.Date;
import java.sql.Time;

public class CorteCaja {
    private int idCorte;
    private Date fecha;
    private String turno;
    private Time horaInicio;
    private Time horaFinEstimado;
    private double saldoInicial;
    private double montoReal;
    private double ingresos;
    private double gastos;
    private double diferencia;
    private String estado;
    private int idResponsable;
    private String nombreResponsable;

    // Getters y Setters
    public int getIdCorte() { return idCorte; }
    public void setIdCorte(int idCorte) { this.idCorte = idCorte; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFinEstimado() { return horaFinEstimado; }
    public void setHoraFinEstimado(Time horaFinEstimado) { this.horaFinEstimado = horaFinEstimado; }

    public double getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(double saldoInicial) { this.saldoInicial = saldoInicial; }

    public double getMontoReal() { return montoReal; }
    public void setMontoReal(double montoReal) { this.montoReal = montoReal; }

    public double getIngresos() { return ingresos; }
    public void setIngresos(double ingresos) { this.ingresos = ingresos; }

    public double getGastos() { return gastos; }
    public void setGastos(double gastos) { this.gastos = gastos; }

    public double getDiferencia() { return diferencia; }
    public void setDiferencia(double diferencia) { this.diferencia = diferencia; }

    public double getTotalCalculado() { return saldoInicial + ingresos - gastos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdResponsable() { return idResponsable; }
    public void setIdResponsable(int idResponsable) { this.idResponsable = idResponsable; }

    public String getNombreResponsable() {
        return (nombreResponsable != null && !nombreResponsable.isEmpty()) ? nombreResponsable : "Ana Martínez";
    }
    public void setNombreResponsable(String nombreResponsable) { this.nombreResponsable = nombreResponsable; }
}