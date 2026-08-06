package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsumoAguaDAO {


    public boolean guardarConsumo(ConsumoAgua consumo) {
        String sql = "INSERT INTO registro_consumo_agua (fecha, litros_consumidos, kilos_lavados, costo_estimado, origen, id_pedido) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(consumo.getFecha()));
            stmt.setDouble(2, consumo.getLitrosConsumidos());
            stmt.setDouble(3, consumo.getKilosLavados());
            stmt.setDouble(4, consumo.getCostoEstimado());
            stmt.setString(5, consumo.getOrigen());
            stmt.setInt(6, consumo.getIdPedido());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al guardar el consumo de agua: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 2. Método para LEER la base de datos y llenar la tabla de la vista 'gasto-agua'
    public List<ConsumoAgua> obtenerTodos() {
        List<ConsumoAgua> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_consumo_agua ORDER BY fecha DESC, id DESC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ConsumoAgua agua = new ConsumoAgua(
                        rs.getDate("fecha").toLocalDate(),
                        rs.getDouble("litros_consumidos"),
                        rs.getDouble("kilos_lavados"),
                        rs.getDouble("costo_estimado"),
                        rs.getString("origen"),
                        rs.getInt("id_pedido")
                );
                lista.add(agua);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al consultar consumo de agua: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}