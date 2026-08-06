package com.lavanderia.sistemalundry;

public class UsuarioDTO {
    private int idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String rol;
    private String sucursal;
    private String miembroDesde;
    private int pedidosRegistrados;
    private int turnosCerrados;

    public UsuarioDTO() {}

    public UsuarioDTO(int idUsuario, String nombre, String correo, String telefono, String rol, String sucursal, String miembroDesde, int pedidosRegistrados, int turnosCerrados) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
        this.sucursal = sucursal;
        this.miembroDesde = miembroDesde;
        this.pedidosRegistrados = pedidosRegistrados;
        this.turnosCerrados = turnosCerrados;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public String getMiembroDesde() { return miembroDesde; }
    public void setMiembroDesde(String miembroDesde) { this.miembroDesde = miembroDesde; }
    public int getPedidosRegistrados() { return pedidosRegistrados; }
    public void setPedidosRegistrados(int pedidosRegistrados) { this.pedidosRegistrados = pedidosRegistrados; }
    public int getTurnosCerrados() { return turnosCerrados; }
    public void setTurnosCerrados(int turnosCerrados) { this.turnosCerrados = turnosCerrados; }

    // Genera automáticamente las iniciales para el avatar (ej: Paulina Martínez -> PM)
    public String getIniciales() {
        if (nombre == null || nombre.trim().isEmpty()) return "US";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        }
        return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
    }
}