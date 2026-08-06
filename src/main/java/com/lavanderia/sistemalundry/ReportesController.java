package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportesController {

    private final ReportesDAO reportesDAO = new ReportesDAO();

    @GetMapping("/reportes")
    public String verReportes(Model model) {
        int totalPedidos = reportesDAO.obtenerTotalPedidos();
        double ingresosTotales = reportesDAO.obtenerIngresosTotales();
        int prendas = reportesDAO.obtenerTotalPrendas();
        double agua = reportesDAO.obtenerTotalAgua();

        // Totales principales
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("ingresosTotales", ingresosTotales);
        model.addAttribute("prendasLavadas", prendas);
        model.addAttribute("gastoAgua", agua);
        model.addAttribute("productosUso", reportesDAO.obtenerUsoProductos());

        // --- CÁLCULO REAL DE PORCENTAJES POR ESTADO ---
        int listos = reportesDAO.obtenerConteoPorEstado("Listo");
        int enProceso = reportesDAO.obtenerConteoPorEstado("En Proceso");
        int pendientes = reportesDAO.obtenerConteoPorEstado("Pendiente");

        int pctListos = 0;
        int pctProceso = 0;
        int pctPendientes = 0;

        if (totalPedidos > 0) {
            pctListos = (int) Math.round((listos * 100.0) / totalPedidos);
            pctProceso = (int) Math.round((enProceso * 100.0) / totalPedidos);
            pctPendientes = (int) Math.round((pendientes * 100.0) / totalPedidos);
        }

        model.addAttribute("porcentajeListos", pctListos);
        model.addAttribute("porcentajeProceso", pctProceso);
        model.addAttribute("porcentajePendientes", pctPendientes);

        // --- DATOS REALES DE TIPO DE LAVADO DESDE LA BD ---
        model.addAttribute("pedidosCompartidos", reportesDAO.obtenerPedidosPorTipoServicio("Compartido"));
        model.addAttribute("ingresosCompartidos", reportesDAO.obtenerIngresosPorTipoServicio("Compartido"));

        model.addAttribute("pedidosExclusivos", reportesDAO.obtenerPedidosPorTipoServicio("Exclusivo"));
        model.addAttribute("ingresosExclusivos", reportesDAO.obtenerIngresosPorTipoServicio("Exclusivo"));

        return "reportes";
    }
}