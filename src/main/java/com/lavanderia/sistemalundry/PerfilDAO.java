package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PerfilDAO {

    // Obtener datos del responsable activo por ID
    public UsuarioDTO obtenerUsuarioPorId(int idResponsable) {
        String sql = "SELECT id_responsable, nombre, correo, telefono, rol, sucursal " +
                "FROM responsables WHERE id_responsable = ?";

        UsuarioDTO usr = null;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResponsable);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usr = new UsuarioDTO();
                    usr.setIdUsuario(rs.getInt("id_responsable"));
                    usr.setNombre(rs.getString("nombre"));
                    usr.setCorreo(rs.getString("correo"));
                    usr.setTelefono(rs.getString("telefono"));
                    usr.setRol(rs.getString("rol") != null ? rs.getString("rol") : "Administrador");
                    usr.setSucursal(rs.getString("sucursal") != null ? rs.getString("sucursal") : "SU Laundry Centro");
                    usr.setMiembroDesde("Mayo, 2026");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener responsable: " + e.getMessage());
        }

        // Respaldo de seguridad
        if (usr == null) {
            usr = new UsuarioDTO(idResponsable, "Paulina Martínez", "paulina.m@sulaundry.com", "+52 669 123 4567", "Administradora", "SU Laundry Centro", "Mayo, 2026", 0, 0);
        }

        // Cargar métricas INDIVIDUALES del mes actual
        usr.setPedidosRegistrados(obtenerPedidosMesResponsable(idResponsable));
        usr.setTurnosCerrados(obtenerTurnosCerradosResponsable(idResponsable));

        return usr;
    }

    // Actualizar nombre, correo y teléfono en responsables
    public boolean actualizarDatosPersonales(int idResponsable, String nombre, String correo, String telefono) {
        String sql = "UPDATE responsables SET nombre = ?, correo = ?, telefono = ? WHERE id_responsable = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, telefono);
            stmt.setInt(4, idResponsable);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar datos en responsables: " + e.getMessage());
            return false;
        }
    }

    // Verificar contraseña actual
    public boolean verificarPasswordActual(int idResponsable, String passwordActual) {
        String sql = "SELECT COUNT(*) FROM responsables WHERE id_responsable = ? AND password = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResponsable);
            stmt.setString(2, passwordActual);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar contraseña: " + e.getMessage());
        }
        return true;
    }

    // Actualizar contraseña
    public boolean actualizarPassword(int idResponsable, String nuevaPassword) {
        String sql = "UPDATE responsables SET password = ? WHERE id_responsable = ?";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevaPassword);
            stmt.setInt(2, idResponsable);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    // Cuenta los pedidos del responsable en el mes actual
    private int obtenerPedidosMesResponsable(int idResponsable) {
        String sql = "SELECT COUNT(*) FROM pedidos " +
                "WHERE id_responsable = ? " +
                "AND MONTH(fecha_recepcion) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(fecha_recepcion) = YEAR(CURRENT_DATE())";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResponsable);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al contar pedidos del responsable: " + e.getMessage());
        }
        return 0;
    }

    // Cuenta los turnos cerrados del responsable en el mes usando 'cortes_de_caja'
    private int obtenerTurnosCerradosResponsable(int idResponsable) {
        String sql = "SELECT COUNT(*) FROM cortes_de_caja " +
                "WHERE id_responsable = ? " +
                "AND LOWER(estado) = 'cerrado' " +
                "AND MONTH(fecha_apertura) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(fecha_apertura) = YEAR(CURRENT_DATE())";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idResponsable);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al contar cortes_de_caja del responsable: " + e.getMessage());
        }
        return 0;
    }
}