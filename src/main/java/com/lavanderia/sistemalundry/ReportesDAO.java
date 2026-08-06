package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportesDAO {

    public int obtenerTotalPedidos() {
        String sql = "SELECT COUNT(*) FROM pedidos";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener total de pedidos: " + e.getMessage());
        }
        return 0;
    }

    public double obtenerIngresosTotales() {
        String sql = "SELECT COALESCE(SUM(total_estimado), 0) FROM pedidos";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener ingresos: " + e.getMessage());
        }
        return 0.0;
    }

    public int obtenerTotalPrendas() {
        String sql = "SELECT COALESCE(SUM(total_prendas), 0) FROM pedidos";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener prendas: " + e.getMessage());
        }
        return 0;
    }

    public double obtenerTotalAgua() {
        String sql = "SELECT COALESCE(SUM(gasto_agua_estimado_litros), 0) FROM pedidos";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener consumo de agua: " + e.getMessage());
        }
        return 0.0;
    }

    public int obtenerConteoPorEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE LOWER(estado) = LOWER(?)";
        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al contar pedidos por estado: " + e.getMessage());
        }
        return 0;
    }

    // --- NUEVOS MÉTODOS REALES PARA TIPO DE LAVADO ---

    public int obtenerPedidosPorTipoServicio(String tipo) {
        int total = 0;
        // Cuenta usando la nueva columna 'tipo_servicio'
        String sql = "SELECT COUNT(*) FROM pedidos WHERE tipo_servicio = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error contando pedidos por modalidad: " + e.getMessage());
        }
        return total;
    }

    public double obtenerIngresosPorTipoServicio(String tipo) {
        double ingresos = 0.0;
        // Suma el dinero usando la nueva columna 'tipo_servicio'
        String sql = "SELECT COALESCE(SUM(total_estimado), 0) FROM pedidos WHERE tipo_servicio = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ingresos = rs.getDouble(1);
            }
        } catch (Exception e) {
            System.err.println("Error sumando ingresos por modalidad: " + e.getMessage());
        }
        return ingresos;
    }

    public List<ProductoUsoDTO> obtenerUsoProductos() {
        List<ProductoUsoDTO> lista = new ArrayList<>();

        String sql = "SELECT nombre_producto, stock_actual, unidad_medida FROM productos_insumos LIMIT 5";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre_producto");
                double cantidad = rs.getDouble("stock_actual");
                String unidad = rs.getString("unidad_medida");
                if (unidad == null) unidad = "";

                String cantidadFormateada = (cantidad % 1 == 0)
                        ? String.format("%.0f %s", cantidad, unidad)
                        : String.format("%.1f %s", cantidad, unidad);

                lista.add(new ProductoUsoDTO(nombre, cantidadFormateada.trim()));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener productos: " + e.getMessage());
        }

        return lista;
    }
}