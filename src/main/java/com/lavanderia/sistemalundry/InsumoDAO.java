package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsumoDAO {

    public List<Insumo> obtenerTodos() {
        List<Insumo> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos_insumos";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Insumo i = new Insumo();
                i.setIdProducto(rs.getInt("id_producto"));
                i.setNombreProducto(rs.getString("nombre_producto"));
                i.setCategoria(rs.getString("categoria"));
                i.setUnidadMedida(rs.getString("unidad_medida"));
                i.setPresentacion(rs.getString("presentacion"));
                i.setStockActual(rs.getDouble("stock_actual"));
                i.setStockMinimo(rs.getDouble("stock_minimo"));
                i.setStockMaximo(rs.getDouble("stock_maximo"));
                i.setProveedor(rs.getString("proveedor"));
                i.setFechaCompra(rs.getDate("fecha_compra"));
                i.setCostoUsoServicio(rs.getDouble("costo_uso_servicio"));

                lista.add(i);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener insumos de MySQL");
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Insumo insumo) {
        String sql = "INSERT INTO productos_insumos (nombre_producto, categoria, unidad_medida, presentacion, " +
                "stock_actual, stock_minimo, stock_maximo, proveedor, fecha_compra, costo_uso_servicio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, insumo.getNombreProducto());
            ps.setString(2, insumo.getCategoria() != null ? insumo.getCategoria() : "Otros");
            ps.setString(3, insumo.getUnidadMedida() != null ? insumo.getUnidadMedida() : "Unidad");
            ps.setString(4, insumo.getPresentacion());
            ps.setDouble(5, insumo.getStockActual());
            ps.setDouble(6, insumo.getStockMinimo());
            ps.setDouble(7, insumo.getStockMaximo());
            ps.setString(8, insumo.getProveedor());

            if (insumo.getFechaCompra() != null) {
                ps.setDate(9, new java.sql.Date(insumo.getFechaCompra().getTime()));
            } else {
                ps.setDate(9, new java.sql.Date(System.currentTimeMillis()));
            }

            ps.setDouble(10, insumo.getCostoUsoServicio());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar insumo en MySQL");
            e.printStackTrace();
            return false;
        }
    }
}