package com.lavanderia.sistemalundry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CorteCajaDAO {

    // 1. Obtener el corte activo (Acepta 'Pendiente' o 'Activo')
    public int obtenerIdCorteActivo() {
        String sql = "SELECT id_corte FROM cortes_de_caja WHERE estado IN ('Activo', 'Pendiente') ORDER BY id_corte DESC LIMIT 1";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("id_corte");
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar corte de caja activo");
            e.printStackTrace();
        }
        return 0; // 0 significa que no hay turno abierto
    }

    // 2. Registrar apertura de turno/caja
    public boolean abrirCorte(String turno, double saldoInicial, int idResponsable) {
        String sql = "INSERT INTO cortes_de_caja (fecha_apertura, turno, hora_inicio, saldo_inicial, estado, id_responsable) " +
                "VALUES (CURDATE(), ?, CURTIME(), ?, 'Pendiente', ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, turno);
            ps.setDouble(2, saldoInicial);
            ps.setInt(3, idResponsable);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al abrir corte de caja: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 3. Listar únicamente los movimientos pertenecientes al corte activo
    public List<MovimientoCaja> obtenerMovimientosPorCorte(int idCorteActivo) {
        List<MovimientoCaja> lista = new ArrayList<>();

        if (idCorteActivo <= 0) {
            return lista;
        }

        String sql = "SELECT id_movimiento, TIME_FORMAT(fecha_hora, '%h:%i %p') AS HORA, tipo, concepto, " +
                "metodo_pago AS METODO, monto, id_corte " +
                "FROM movimientos_caja WHERE id_corte = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCorteActivo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MovimientoCaja m = new MovimientoCaja();
                    m.setIdMovimiento(rs.getInt("id_movimiento"));
                    m.setHora(rs.getString("HORA"));
                    m.setTipo(rs.getString("tipo"));
                    m.setConcepto(rs.getString("concepto"));
                    m.setResponsable("Sistema");
                    m.setMetodo(rs.getString("METODO"));
                    m.setMonto(rs.getDouble("monto"));
                    m.setIdCorte(rs.getInt("id_corte"));
                    lista.add(m);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener movimientos del corte: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 4. Registrar un nuevo movimiento vinculado al corte activo
    public boolean registrarMovimiento(String tipo, String concepto, String metodo, double monto, int idCorte) {
        // CORRECCIÓN: Se agrega nuevamente 'categoria' con el valor 'General' ya que la BD lo requiere (NOT NULL)
        String sql = "INSERT INTO movimientos_caja (fecha_hora, tipo, concepto, categoria, metodo_pago, monto, id_corte) " +
                "VALUES (NOW(), ?, ?, 'General', ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, tipo);
            ps.setString(2, concepto);
            ps.setString(3, metodo);
            ps.setDouble(4, monto);

            if (idCorte > 0) {
                ps.setInt(5, idCorte);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al registrar movimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 5. Eliminar movimiento
    public boolean eliminarMovimiento(int idMovimiento) {
        String sql = "DELETE FROM movimientos_caja WHERE id_movimiento = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMovimiento);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar movimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 6. Obtener Saldo Inicial y Saldo Actual
    public double[] calcularSaldosCaja(int idCorte) {
        double saldoInicial = 2000.00;
        double ingresos = 0.0;
        double egresos = 0.0;

        if (idCorte > 0) {
            String sqlCorte = "SELECT saldo_inicial FROM cortes_de_caja WHERE id_corte = ?";
            try (Connection con = ConexionBD.conectar();
                 PreparedStatement ps = con.prepareStatement(sqlCorte)) {
                ps.setInt(1, idCorte);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        saldoInicial = rs.getDouble("saldo_inicial");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String sqlMov = "SELECT tipo, SUM(monto) as total FROM movimientos_caja WHERE id_corte = ? GROUP BY tipo";
            try (Connection con = ConexionBD.conectar();
                 PreparedStatement ps = con.prepareStatement(sqlMov)) {
                ps.setInt(1, idCorte);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String tipo = rs.getString("tipo");
                        if ("Ingreso".equalsIgnoreCase(tipo)) {
                            ingresos = rs.getDouble("total");
                        } else if ("Egreso".equalsIgnoreCase(tipo)) {
                            egresos = rs.getDouble("total");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        double saldoActual = saldoInicial + ingresos - egresos;
        return new double[]{saldoInicial, saldoActual};
    }

    // 7. Cerrar el turno activo
    public boolean cerrarCorte(int idCorte, double montoRealContado) {
        double ingresos = 0.0;
        double gastos = 0.0;
        double saldoInicial = 2000.0;

        String sqlInit = "SELECT saldo_inicial FROM cortes_de_caja WHERE id_corte = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sqlInit)) {
            ps.setInt(1, idCorte);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) saldoInicial = rs.getDouble("saldo_inicial");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String sqlMovs = "SELECT tipo, SUM(monto) as total FROM movimientos_caja WHERE id_corte = ? GROUP BY tipo";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sqlMovs)) {
            ps.setInt(1, idCorte);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if ("Ingreso".equalsIgnoreCase(rs.getString("tipo"))) {
                        ingresos = rs.getDouble("total");
                    } else if ("Egreso".equalsIgnoreCase(rs.getString("tipo"))) {
                        gastos = rs.getDouble("total");
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        double totalEstimadoEnCaja = saldoInicial + ingresos - gastos;
        double diferencia = montoRealContado - totalEstimadoEnCaja;

        String sqlUpdate = "UPDATE cortes_de_caja SET estado = 'Cerrado', hora_fin_estimado = CURTIME(), " +
                "monto_real = ?, ingresos = ?, gastos = ?, diferencia = ? WHERE id_corte = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setDouble(1, montoRealContado);
            ps.setDouble(2, ingresos);
            ps.setDouble(3, gastos);
            ps.setDouble(4, diferencia);
            ps.setInt(5, idCorte);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cerrar corte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean realizarCorte(double nuevoSaldoInicial, int idResponsable) {
        int idCorteActivo = obtenerIdCorteActivo();
        if (idCorteActivo > 0) {
            cerrarCorte(idCorteActivo, nuevoSaldoInicial);
        }
        return abrirCorte("Vespertino", nuevoSaldoInicial, idResponsable);
    }

    public List<CorteCaja> obtenerHistorialCortes() {
        List<CorteCaja> lista = new ArrayList<>();
        String sql = "SELECT id_corte, fecha_apertura, turno, hora_inicio, hora_fin_estimado, saldo_inicial, " +
                "monto_real, ingresos, gastos, diferencia, estado, id_responsable " +
                "FROM cortes_de_caja ORDER BY id_corte DESC";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                CorteCaja corte = new CorteCaja();
                corte.setIdCorte(rs.getInt("id_corte"));

                if (rs.getDate("fecha_apertura") != null) {
                    corte.setFecha(rs.getDate("fecha_apertura"));
                }

                corte.setTurno(rs.getString("turno"));
                corte.setHoraInicio(rs.getTime("hora_inicio"));
                corte.setHoraFinEstimado(rs.getTime("hora_fin_estimado"));
                corte.setSaldoInicial(rs.getDouble("saldo_inicial"));
                corte.setMontoReal(rs.getDouble("monto_real"));
                corte.setIngresos(rs.getDouble("ingresos"));
                corte.setGastos(rs.getDouble("gastos"));
                corte.setDiferencia(rs.getDouble("diferencia"));
                corte.setEstado(rs.getString("estado"));
                corte.setIdResponsable(rs.getInt("id_responsable"));
                lista.add(corte);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener historial de cortes: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }
}