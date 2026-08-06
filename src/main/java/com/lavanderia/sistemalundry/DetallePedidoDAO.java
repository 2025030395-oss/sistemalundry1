package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DetallePedidoDAO {

    public boolean guardarDetalle(DetallePedido detalle) {
        String sql = "INSERT INTO detalles_pedido (id_pedido, id_servicio_config, descripcion_detallada, monto_servicio) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // 1. Asignar id_pedido
            ps.setInt(1, detalle.getIdPedido());

            // 2. COMO TUS TARJETAS HTML SON FIJAS, MANDAMOS NULL PARA EVITAR EL ERROR DE FOREIGN KEY
            ps.setNull(2, Types.INTEGER);

            // 3. Descripción detallada (guardará el nombre del servicio / prendas)
            ps.setString(3, detalle.getDescripcionDetailed());

            // 4. Monto del servicio
            ps.setDouble(4, detalle.getMontoServicio());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al guardar detalle del pedido: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}