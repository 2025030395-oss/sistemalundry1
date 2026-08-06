package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class PedidoController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final DetallePedidoDAO detalleDAO = new DetallePedidoDAO();
    private final ConsumoAguaDAO aguaDAO = new ConsumoAguaDAO(); // <--- 1. INSTANCIAMOS EL NUEVO DAO

    @GetMapping("/tu-pedido")
    public String verResumenPedido(Model model) {
        return "tu-pedido";
    }

    @PostMapping("/tu-pedido")
    public String procesarYPasoResumen(
            @RequestParam(value = "nombreCliente", required = false, defaultValue = "Cliente General") String cliente,
            @RequestParam(value = "idCliente", required = false, defaultValue = "") String idCliente,
            @RequestParam(value = "telefonoCliente", required = false, defaultValue = "") String telefono,
            @RequestParam(value = "totalPiezas", required = false, defaultValue = "1") int totalPiezas,
            Model model) {

        model.addAttribute("nombreCliente", cliente);
        model.addAttribute("idCliente", idCliente);
        model.addAttribute("telefonoCliente", telefono);
        model.addAttribute("totalPiezas", totalPiezas);

        return "tu-pedido";
    }

    @PostMapping("/pedidos/guardar")
    public String guardarNuevoPedido(
            @RequestParam(value = "nombreCliente", required = false, defaultValue = "Cliente General") String cliente,
            @RequestParam(value = "idCliente", required = false, defaultValue = "") String idCliente,
            @RequestParam(value = "telefonoCliente", required = false, defaultValue = "") String telefono,
            @RequestParam(value = "tipoServicio", required = false, defaultValue = "Lavado Pesado / Industrial") String tipoServicio,
            @RequestParam(value = "totalPiezas", required = false, defaultValue = "1") int prendas,
            @RequestParam(value = "total", required = false, defaultValue = "0.0") double total,
            @RequestParam(value = "descripcionDetalle", required = false, defaultValue = "Servicio de lavandería general") String descripcionDetalle,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== GUARDANDO NUEVO PEDIDO ===");

        String nombreFinal = (cliente != null && !cliente.trim().isEmpty()) ? cliente.trim() : "Cliente General";

        PedidoForm nuevoPedido = new PedidoForm();
        nuevoPedido.setNombreCliente(nombreFinal);
        nuevoPedido.setTelefonoCliente(telefono);
        nuevoPedido.setTipoServicio(tipoServicio);
        nuevoPedido.setTotalPiezas(prendas > 0 ? prendas : 1);
        nuevoPedido.setTotal(total);

        int idPedidoGenerado = pedidoDAO.insertarYObtenerId(nuevoPedido, idCliente, telefono);

        if (idPedidoGenerado > 0) {
            int totalPrendasFinal = prendas > 0 ? prendas : 1;

            // 1. Guardar detalle del pedido
            DetallePedido detalle = new DetallePedido(idPedidoGenerado, totalPrendasFinal, descripcionDetalle, total);
            detalleDAO.guardarDetalle(detalle);

            // =========================================================================
            // 2. CÁLCULO Y REGISTRO AUTOMÁTICO DE AGUA EN BASE DE DATOS
            // =========================================================================
            double kilosEstimados = totalPrendasFinal * 2.5; // Supuesto: 2.5 Kg por prenda (o ajusta la constante)
            double litrosPorKilo = 19.5;                     // 19.5 Litros por cada Kg
            double precioPorLitro = 0.02;                    // $0.02 por litro estimado

            double litrosConsumidos = kilosEstimados * litrosPorKilo;
            double costoEstimadoAgua = litrosConsumidos * precioPorLitro;

            ConsumoAgua consumoAgua = new ConsumoAgua(
                    LocalDate.now(),
                    litrosConsumidos,
                    kilosEstimados,
                    costoEstimadoAgua,
                    "automatico_pedido",
                    idPedidoGenerado
            );

            boolean aguaGuardada = aguaDAO.guardarConsumo(consumoAgua);
            if (aguaGuardada) {
                System.out.println("💧 Consumo de agua de " + litrosConsumidos + "L registrado automáticamente para el Pedido #" + idPedidoGenerado);
            }
            // =========================================================================

            System.out.println("✅ ¡Pedido #" + idPedidoGenerado + " guardado exitosamente para: " + nombreFinal + "!");
            redirectAttributes.addFlashAttribute("pedidoCreado", true);
        } else {
            System.out.println("❌ Error al intentar guardar el pedido en BD.");
            redirectAttributes.addFlashAttribute("errorPedido", true);
        }

        return "redirect:/pedidos";
    }

    @PostMapping("/pedidos/eliminar")
    public String eliminarPedido(@RequestParam("idPedido") int idPedido, RedirectAttributes redirectAttributes) {
        boolean eliminado = pedidoDAO.eliminarPedido(idPedido);
        if (eliminado) {
            redirectAttributes.addFlashAttribute("pedidoEliminado", true);
        } else {
            redirectAttributes.addFlashAttribute("errorEliminar", true);
        }
        return "redirect:/pedidos";
    }
    @PostMapping("/pedidos/actualizar-estado")
    public String actualizarEstado(
            @RequestParam("idPedido") int idPedido,
            @RequestParam("estado") String estado) {

        // Llamamos al DAO para que haga el UPDATE en la base de datos
        pedidoDAO.actualizarEstado(idPedido, estado);

        // Recargamos la misma página para ver el cambio reflejado al instante
        return "redirect:/pedidos";
    }
}