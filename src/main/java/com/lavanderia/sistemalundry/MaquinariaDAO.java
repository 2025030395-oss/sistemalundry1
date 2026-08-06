package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaquinariaDAO {

    public List<Maquinaria> obtenerTodas() {
        List<Maquinaria> lista = new ArrayList<>();
        String sql = "SELECT * FROM maquinaria_equipos";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Maquinaria m = new Maquinaria();
                m.setIdEquipo(rs.getInt("id_equipo"));
                m.setNombreEquipo(rs.getString("nombre_equipo"));
                m.setTipo(rs.getString("tipo"));
                m.setCapacidad(rs.getString("capacidad"));
                m.setEstadoOperacion(rs.getString("estado_operacion"));
                m.setIdPedidoActual(rs.getInt("id_pedido_actual"));

                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener maquinaria de MySQL");
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Maquinaria m) {
        String sql = "INSERT INTO maquinaria_equipos (nombre_equipo, tipo, capacidad, estado_operacion) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getNombreEquipo());
            ps.setString(2, m.getTipo());
            ps.setString(3, m.getCapacidad());
            ps.setString(4, m.getEstadoOperacion() != null ? m.getEstadoOperacion() : "Disponible");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar maquinaria en MySQL");
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarEstado(int idEquipo, String nuevoEstado) {
        String sql = "UPDATE maquinaria_equipos SET estado_operacion = ? WHERE id_equipo = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEquipo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de maquinaria");
            e.printStackTrace();
            return false;
        }
    }
}