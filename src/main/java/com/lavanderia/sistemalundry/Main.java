package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // 1. Obtener la conexión
        Connection con = ConexionBD.conectar();

        if (con != null) {
            // 2. Escribir la consulta SQL para las máquinas
            String query = "SELECT nombre_equipo, estado_operacion, id_pedido_actual FROM maquinaria_equipos";

            try (PreparedStatement ps = con.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                System.out.println("\n--- LISTA DE MAQUINARIA DESDE MYSQL ---");

                // 3. Recorrer los resultados devueltos por MySQL
                while (rs.next()) {
                    String nombre = rs.getString("nombre_equipo");
                    String estado = rs.getString("estado_operacion");
                    int pedido = rs.getInt("id_pedido_actual");

                    // Si el pedido es 0 en Java, significa que en la BD era NULL (Disponible)
                    String infoPedido = (pedido == 0) ? "Ninguno" : "Pedido #" + pedido;

                    System.out.println("Máquina: " + nombre + " | Estado: " + estado + " | Asignada a: " + infoPedido);
                }

            } catch (SQLException e) {
                System.out.println("Error al ejecutar la consulta.");
                e.printStackTrace();
            } finally {
                // 4. Siempre cerrar la conexión al terminar
                try {
                    con.close();
                    System.out.println("\nConexión cerrada de forma segura.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}