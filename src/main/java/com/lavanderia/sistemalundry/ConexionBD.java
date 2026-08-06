package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Configuración de tu MySQL local
    private static final String URL = "jdbc:mysql://localhost:3306/su_laundry?serverTimezone=UTC";
    private static final String USUARIO = "root";       // Tu usuario de MySQL
    private static final String CONTRASENA = "1234"; // ⚠️ Pon aquí tu contraseña de Workbench

    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Registrar el driver de MySQL (opcional en versiones nuevas, pero buena práctica)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Intentar conectar
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("¡Conexión exitosa a SU Laundry BD! 🎉");

        } catch (ClassNotFoundException e) {
            System.out.println("Error: ¡No se encontró el driver de MySQL! Revisa el JAR.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Falló la conexión. Revisa el usuario, contraseña o si MySQL está encendido.");
            e.printStackTrace();
        }
        return conexion;
    }
}

