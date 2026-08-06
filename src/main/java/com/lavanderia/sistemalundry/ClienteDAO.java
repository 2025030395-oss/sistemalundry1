package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public int obtenerTotalClientes() {
        String sql = "SELECT COUNT(*) FROM clientes";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar clientes en MySQL");
            e.printStackTrace();
        }
        return 0;
    }

    // 1. Obtener todos los clientes leyendo tus columnas REALES
    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int idReal = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                String correo = rs.getString("correo");
                String direccion = rs.getString("calle_numero");

                // Creamos el cliente con sus datos reales de la BD
                Cliente c = new Cliente(idReal, nombre, telefono, correo, direccion);

                // Asignamos el estado real si viene en la BD
                String estadoBD = rs.getString("estado");
                if (estadoBD != null && !estadoBD.trim().isEmpty()) {
                    c.setEstado(estadoBD);
                }

                lista.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener clientes desde MySQL: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 2. Insertar mapeando exacto a tus columnas (id_cliente es autoincrementable)
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, telefono, correo, calle_numero, estado) VALUES (?, ?, ?, ?, 'Activo')";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Validamos que no vayan nulos a campos obligatorios (NOT NULL)
            String nombre = (cliente.getNombre() != null && !cliente.getNombre().trim().isEmpty())
                    ? cliente.getNombre().trim() : "Sin Nombre";

            String telefono = (cliente.getTelefono() != null && !cliente.getTelefono().trim().isEmpty())
                    ? cliente.getTelefono().trim() : "0000000000";

            // Correo es UNIQUE. Si no envías correo, genera uno con timestamp para evitar duplicados
            String correo = (cliente.getCorreo() != null && !cliente.getCorreo().trim().isEmpty())
                    ? cliente.getCorreo().trim() : "cliente_" + System.currentTimeMillis() + "@mail.com";

            String direccion = (cliente.getDireccion() != null && !cliente.getDireccion().trim().isEmpty())
                    ? cliente.getDireccion().trim() : "Sin Dirección";

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, correo);
            ps.setString(4, direccion);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar en MySQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // 3. Eliminar cliente por su ID real de la BD (id_cliente)
    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar cliente en MySQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}