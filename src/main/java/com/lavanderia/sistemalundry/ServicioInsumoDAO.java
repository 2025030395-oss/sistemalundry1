package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ServicioInsumoDAO {

    /**
     * Vincula una lista de productos/insumos a una configuración de servicio.
     * Utiliza batch/transacción para guardar todos los IDs de un solo golpe.
     */
    public boolean vincularInsumosAServicio(int idServicioConfig, List<Integer> idsProductos) {
        if (idsProductos == null || idsProductos.isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO servicios_insumos (id_servicio_config, id_insumo) VALUES (?, ?)";

        try (Connection con = ConexionBD.conectar()) {
            // Desactivamos autocommit para procesar como una sola transacción
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (Integer idInsumo : idsProductos) {
                    ps.setInt(1, idServicioConfig);
                    ps.setInt(2, idInsumo);
                    ps.addBatch(); // Agregamos al paquete
                }

                int[] resultados = ps.executeBatch();
                con.commit(); // Confirmamos los cambios si todo salió bien

                return resultados.length > 0;

            } catch (SQLException e) {
                con.rollback(); // Revertimos en caso de error
                System.out.println("❌ Error en la transacción al vincular insumos: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error de conexión al vincular insumos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Método opcional por si necesitas desvincular o limpiar los insumos de un servicio antes de reasignar
     */
    public boolean eliminarInsumosDeServicio(int idServicioConfig) {
        String sql = "DELETE FROM servicios_insumos WHERE id_servicio_config = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idServicioConfig);
            return ps.executeUpdate() >= 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar insumos del servicio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}