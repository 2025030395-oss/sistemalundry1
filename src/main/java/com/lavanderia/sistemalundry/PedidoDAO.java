package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class PedidoDAO {

    public int insertarYObtenerId(PedidoForm pedido, String idClienteString, String telefono) {
        int idClienteReal = resolverIdCliente(idClienteString, pedido.getNombreCliente(), telefono);

        if (idClienteReal == -1) {
            System.out.println("❌ Error: No se pudo resolver ni registrar el cliente.");
            return -1;
        }

        CorteCajaDAO corteDAO = new CorteCajaDAO();
        int idCorteActivo = corteDAO.obtenerIdCorteActivo();

        String sql = "INSERT INTO pedidos (" +
                "id_cliente, fecha_recepcion, fecha_entrega, total_prendas, " +
                "tipo_lavado, estado, subtotal, iva, total_estimado, " +
                "gasto_agua_estimado_litros, id_corte_perteneciente, id_responsable, tipo_servicio" +
                ") VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL 1 DAY), ?, ?, 'En proceso', ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            double total = pedido.getTotal();
            double subtotal = total > 0 ? (total / 1.16) : 0;
            double iva = total - subtotal;
            int totalPiezas = pedido.getTotalPiezas();
            int gastoAgua = (int) Math.round(totalPiezas * 12.5);

            ps.setInt(1, idClienteReal);
            ps.setInt(2, totalPiezas);
            ps.setString(3, pedido.getTipoServicio() != null ? pedido.getTipoServicio() : "General"); // Ej. Lavado Pesado
            ps.setDouble(4, subtotal);
            ps.setDouble(5, iva);
            ps.setDouble(6, total);
            ps.setInt(7, gastoAgua);

            if (idCorteActivo > 0) {
                ps.setInt(8, idCorteActivo);
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            // AQUÍ ESTÁ EL CAMBIO: Asignamos el ID 1 al pedido.
            // Nota a futuro: Cuando agregues el login real, aquí deberás pasar la variable del usuario activo en lugar del '1'.
            ps.setInt(9, 1);

            // ESTO GUARDA "Compartido" o "Exclusivo" en tu nueva columna
            ps.setString(10, pedido.getModalidad() != null ? pedido.getModalidad() : "Compartido");

            int afectadas = ps.executeUpdate();
            if (afectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);

                        // REGISTRO AUTOMÁTICO EN LA CAJA CHICA
                        if (total > 0) {
                            String clienteNombre = (pedido.getNombreCliente() != null && !pedido.getNombreCliente().isEmpty())
                                    ? pedido.getNombreCliente() : "Cliente General";

                            String concepto = "Cobro Pedido #" + idGenerado + " - " + clienteNombre;
                            corteDAO.registrarMovimiento("Ingreso", concepto, "Efectivo", total, idCorteActivo);
                            System.out.println("💵 Ingreso de $" + total + " registrado automáticamente.");
                        }

                        return idGenerado;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar pedido en MySQL: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    private int resolverIdCliente(String idClienteStr, String nombre, String telefono) {
        if (idClienteStr != null && !idClienteStr.trim().isEmpty()) {
            try {
                int idNum = Integer.parseInt(idClienteStr.replaceAll("[^0-9]", ""));
                String sqlBuscarId = "SELECT id_cliente FROM clientes WHERE id_cliente = ?";
                try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sqlBuscarId)) {
                    ps.setInt(1, idNum);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return rs.getInt("id_cliente");
                    }
                } catch (SQLException e) {}
            } catch (NumberFormatException e) { }
        }

        if (telefono != null && !telefono.trim().isEmpty() && !telefono.equals("0000000000")) {
            String sqlBuscarTel = "SELECT id_cliente FROM clientes WHERE telefono = ? LIMIT 1";
            try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sqlBuscarTel)) {
                ps.setString(1, telefono.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("id_cliente");
                }
            } catch (SQLException e) {}
        }

        if (nombre != null && !nombre.trim().isEmpty() && !nombre.equalsIgnoreCase("Cliente General")) {
            String sqlBuscarNombre = "SELECT id_cliente FROM clientes WHERE nombre = ? LIMIT 1";
            try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sqlBuscarNombre)) {
                ps.setString(1, nombre.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("id_cliente");
                }
            } catch (SQLException e) {}
        }

        String sqlInsertar = "INSERT INTO clientes (nombre, telefono, correo, calle_numero, estado) VALUES (?, ?, ?, 'Dirección General', 'Activo')";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
            String correoUnico = "cliente_" + System.currentTimeMillis() + "@mail.com";
            ps.setString(1, (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "Cliente General");
            ps.setString(2, (telefono != null && !telefono.trim().isEmpty()) ? telefono.trim() : "0000000000");
            ps.setString(3, correoUnico);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<PedidoForm> obtenerTodos() {
        List<PedidoForm> lista = new ArrayList<>();
        // Agregamos tipo_servicio a la consulta SELECT
        String sql = "SELECT p.id_pedido, c.nombre AS nombre_cliente, p.tipo_lavado, p.tipo_servicio, p.total_prendas, p.estado, p.total_estimado, p.fecha_entrega " +
                "FROM pedidos p LEFT JOIN clientes c ON p.id_cliente = c.id_cliente ORDER BY p.id_pedido DESC";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String clienteNombre = rs.getString("nombre_cliente");
                if (clienteNombre == null || clienteNombre.trim().isEmpty()) clienteNombre = "Cliente General";

                PedidoForm p = new PedidoForm();
                p.setIdPedido(rs.getInt("id_pedido"));
                p.setNombreCliente(clienteNombre);
                p.setTipoServicio(rs.getString("tipo_lavado"));
                p.setModalidad(rs.getString("tipo_servicio")); // Leemos Compartido/Exclusivo de BD
                p.setTotalPiezas(rs.getInt("total_prendas"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total_estimado"));
                p.setFechaEntrega(rs.getString("fecha_entrega"));
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public int obtenerPedidosHoy() {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE DATE(fecha_recepcion) = CURDATE()";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int obtenerListosParaEntrega() {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = 'Listo' OR estado = 'Entregado'";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int obtenerPorVencer() {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = 'En proceso' OR estado = 'Pendiente'";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int obtenerGastoAguaHoy() {
        String sql = "SELECT COALESCE(SUM(gasto_agua_estimado_litros), 0) FROM pedidos WHERE DATE(fecha_recepcion) = CURDATE()";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public PedidoForm buscarPorId(int idPedido) {
        String sql = "SELECT p.id_pedido, c.nombre AS nombre_cliente, p.tipo_lavado, p.tipo_servicio, p.total_prendas, p.estado, p.total_estimado, p.fecha_entrega " +
                "FROM pedidos p LEFT JOIN clientes c ON p.id_cliente = c.id_cliente WHERE p.id_pedido = ?";
        try (Connection con = ConexionBD.conectar(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String clienteNombre = rs.getString("nombre_cliente");
                    if (clienteNombre == null || clienteNombre.trim().isEmpty()) clienteNombre = "Cliente General";
                    PedidoForm p = new PedidoForm();
                    p.setIdPedido(rs.getInt("id_pedido"));
                    p.setNombreCliente(clienteNombre);
                    p.setTipoServicio(rs.getString("tipo_lavado"));
                    p.setModalidad(rs.getString("tipo_servicio"));
                    p.setTotalPiezas(rs.getInt("total_prendas"));
                    p.setEstado(rs.getString("estado"));
                    p.setTotal(rs.getDouble("total_estimado"));
                    p.setFechaEntrega(rs.getString("fecha_entrega"));
                    return p;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean eliminarPedido(int idPedido) {
        String sqlDetalles = "DELETE FROM detalle_pedidos WHERE id_pedido = ?";
        String sqlPedido = "DELETE FROM pedidos WHERE id_pedido = ?";
        try (Connection con = ConexionBD.conectar()) {
            try (PreparedStatement psDet = con.prepareStatement(sqlDetalles)) {
                psDet.setInt(1, idPedido);
                psDet.executeUpdate();
            } catch (SQLException e) { }
            try (PreparedStatement psPed = con.prepareStatement(sqlPedido)) {
                psPed.setInt(1, idPedido);
                if (psPed.executeUpdate() > 0) return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void actualizarEstado(int idPedido, String nuevoEstado) {
        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try (java.sql.Connection conn = ConexionBD.conectar();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idPedido);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("❌ Error al cambiar estado: " + e.getMessage());
        }
    }
}