package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/caja")
public class CajaController {

    private final CorteCajaDAO cajaDAO = new CorteCajaDAO();

    @GetMapping
    public String verCaja(Model model) {
        int idCorteActivo = cajaDAO.obtenerIdCorteActivo();

        List<MovimientoCaja> movimientos = cajaDAO.obtenerMovimientosPorCorte(idCorteActivo);
        double[] saldos = cajaDAO.calcularSaldosCaja(idCorteActivo);
        List<CorteCaja> cortes = cajaDAO.obtenerHistorialCortes();

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("saldoInicial", String.format(Locale.US, "$%.2f", saldos[0]));
        model.addAttribute("saldoActual", String.format(Locale.US, "$%.2f", saldos[1]));
        model.addAttribute("cortes", cortes != null ? cortes : List.of());

        return "caja";
    }

    @PostMapping("/movimiento")
    public String guardarMovimiento(@RequestParam("tipo") String tipo,
                                    @RequestParam("concepto") String concepto,
                                    @RequestParam("metodo") String metodo,
                                    @RequestParam("monto") double monto,
                                    RedirectAttributes flash) {

        int idCorteActivo = cajaDAO.obtenerIdCorteActivo();
        boolean guardado = cajaDAO.registrarMovimiento(tipo, concepto, metodo, monto, idCorteActivo);

        if (guardado) {
            flash.addFlashAttribute("registroExitoso", true);
        }

        return "redirect:/caja";
    }

    @PostMapping("/corte")
    public String realizarCorte(@RequestParam("montoReal") double montoReal,
                                RedirectAttributes flash) {

        int idCorteActivo = cajaDAO.obtenerIdCorteActivo();

        if (idCorteActivo > 0) {
            cajaDAO.cerrarCorte(idCorteActivo, montoReal);
        }

        double fondoBaseSiguienteTurno = 2000.00;
        cajaDAO.abrirCorte("Vespertino", fondoBaseSiguienteTurno, 1);

        flash.addFlashAttribute("corteExitoso", true);
        return "redirect:/caja";
    }

    @PostMapping("/movimiento/eliminar")
    public String eliminarMovimiento(@RequestParam("id") int idMovimiento, RedirectAttributes flash) {
        cajaDAO.eliminarMovimiento(idMovimiento);
        flash.addFlashAttribute("mensaje", "Movimiento eliminado");
        return "redirect:/caja";
    }

    @GetMapping("/cortes")
    public String listarTodosCortes(@RequestParam(name = "page", defaultValue = "1") int page,
                                    @RequestParam(name = "turno", required = false) String turno,
                                    @RequestParam(name = "fechaInicio", required = false) String fechaInicio,
                                    @RequestParam(name = "fechaFin", required = false) String fechaFin,
                                    Model model) {
        int tamanoPagina = 10;

        List<CorteCaja> todosLosCortes = cajaDAO.obtenerHistorialCortes();
        if (todosLosCortes == null) {
            todosLosCortes = List.of();
        }

        List<CorteCaja> cortesFiltrados = todosLosCortes.stream()
                .filter(c -> {
                    if (turno != null && !turno.trim().isEmpty()) {
                        if (c.getTurno() == null || !c.getTurno().equalsIgnoreCase(turno)) {
                            return false;
                        }
                    }
                    if (fechaInicio != null && !fechaInicio.trim().isEmpty() && c.getFecha() != null) {
                        if (c.getFecha().toString().compareTo(fechaInicio) < 0) {
                            return false;
                        }
                    }
                    if (fechaFin != null && !fechaFin.trim().isEmpty() && c.getFecha() != null) {
                        if (c.getFecha().toString().compareTo(fechaFin) > 0) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();

        int totalRegistros = cortesFiltrados.size();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / tamanoPagina);
        if (totalPaginas == 0) totalPaginas = 1;

        int desde = (page - 1) * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, totalRegistros);

        List<CorteCaja> cortesPaginados;
        if (desde < totalRegistros && desde >= 0) {
            cortesPaginados = cortesFiltrados.subList(desde, hasta);
        } else {
            cortesPaginados = List.of();
        }

        double sumIngresos = cortesFiltrados.stream().mapToDouble(CorteCaja::getIngresos).sum();
        double sumGastos = cortesFiltrados.stream().mapToDouble(CorteCaja::getGastos).sum();
        double sumNeto = sumIngresos - sumGastos;

        model.addAttribute("cortes", cortesPaginados);
        model.addAttribute("paginaActual", page);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalElementos", totalRegistros);
        model.addAttribute("sumIngresos", sumIngresos);
        model.addAttribute("sumGastos", sumGastos);
        model.addAttribute("sumNeto", sumNeto);

        return "cortes";
    }

    @GetMapping("/cortes/exportar")
    public void exportarCortesCSV(@RequestParam(name = "turno", required = false) String turno,
                                  @RequestParam(name = "fechaInicio", required = false) String fechaInicio,
                                  @RequestParam(name = "fechaFin", required = false) String fechaFin,
                                  HttpServletResponse response) throws IOException {

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String fechaActual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "Cortes_Caja_" + fechaActual + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        List<CorteCaja> todosLosCortes = cajaDAO.obtenerHistorialCortes();
        if (todosLosCortes == null) todosLosCortes = List.of();

        List<CorteCaja> cortesFiltrados = todosLosCortes.stream()
                .filter(c -> {
                    if (turno != null && !turno.trim().isEmpty()) {
                        if (c.getTurno() == null || !c.getTurno().equalsIgnoreCase(turno)) return false;
                    }
                    if (fechaInicio != null && !fechaInicio.trim().isEmpty() && c.getFecha() != null) {
                        if (c.getFecha().toString().compareTo(fechaInicio) < 0) return false;
                    }
                    if (fechaFin != null && !fechaFin.trim().isEmpty() && c.getFecha() != null) {
                        if (c.getFecha().toString().compareTo(fechaFin) > 0) return false;
                    }
                    return true;
                })
                .toList();

        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF');
        writer.println("Fecha,Turno,Responsable,Ingresos,Gastos,Total,Diferencia,Estado");

        for (CorteCaja corte : cortesFiltrados) {
            String fechaStr = (corte.getFecha() != null) ? corte.getFecha().toString() : "";
            String horaStr = (corte.getHoraInicio() != null) ? corte.getHoraInicio().toString() : "08:00 AM";
            String estado = (corte.getDiferencia() == 0) ? "CUADRADO" : "REVISIÓN";

            writer.println(String.format(Locale.US, "\"%s\",%s,\"%s\",%.2f,%.2f,%.2f,%.2f,%s",
                    fechaStr + " " + horaStr,
                    corte.getTurno() != null ? corte.getTurno() : "",
                    corte.getNombreResponsable() != null ? corte.getNombreResponsable() : "",
                    corte.getIngresos(),
                    corte.getGastos(),
                    corte.getTotalCalculado(),
                    corte.getDiferencia(),
                    estado
            ));
        }

        writer.flush();
        writer.close();
    }
}