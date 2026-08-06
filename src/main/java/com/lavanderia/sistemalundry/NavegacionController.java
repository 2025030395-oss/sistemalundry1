package com.lavanderia.sistemalundry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class NavegacionController {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final InsumoDAO insumoDAO = new InsumoDAO();
    private final MaquinariaDAO maquinariaDAO = new MaquinariaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final CorteCajaDAO cajaDAO = new CorteCajaDAO();

    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/index")
    public String mostrarIndex(Model model) {
        // 1. Estadísticas Generales
        int totalClientes = clienteDAO.obtenerTotalClientes();
        int pedidosHoy = pedidoDAO.obtenerPedidosHoy();
        int listos = pedidoDAO.obtenerListosParaEntrega();
        int porVencer = pedidoDAO.obtenerPorVencer();

        // 2. Métricas de Consumo de Agua (Garantizadas)
        int aguaHoy = pedidoDAO.obtenerGastoAguaHoy();
        int aguaSemana = aguaHoy * 7; // Estimación simple semanal
        int aguaMes = aguaHoy * 30;   // Estimación simple mensual

        // 3. Métricas de Caja Chica requeridas por index.html
        int idCorteActivo = cajaDAO.obtenerIdCorteActivo();
        double[] saldos = cajaDAO.calcularSaldosCaja(idCorteActivo);

        // Calculamos los totales usando las funciones de CorteCajaDAO
        List<MovimientoCaja> movs = cajaDAO.obtenerMovimientosPorCorte(idCorteActivo);
        double ingresos = movs.stream().filter(m -> "Ingreso".equalsIgnoreCase(m.getTipo())).mapToDouble(MovimientoCaja::getMonto).sum();
        double gastos = movs.stream().filter(m -> "Egreso".equalsIgnoreCase(m.getTipo())).mapToDouble(MovimientoCaja::getMonto).sum();
        double total = saldos[1]; // Saldo Actual

        // 4. Enviar Variables a la Vista index.html
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("pedidosHoy", pedidosHoy);
        model.addAttribute("listosParaEntrega", listos);
        model.addAttribute("porVencer", porVencer);

        // Variables de Agua
        model.addAttribute("gastoAguaHoy", aguaHoy);
        model.addAttribute("gastoAguaSemana", aguaSemana);
        model.addAttribute("gastoAguaMes", aguaMes);

        // Variables de Caja Chica esperadas por index.html
        model.addAttribute("cajaIngresos", ingresos);
        model.addAttribute("cajaGastos", gastos);
        model.addAttribute("cajaTotal", total);
        model.addAttribute("cajaTurno", idCorteActivo > 0 ? "Turno Activo #" + idCorteActivo : "Sin Turno");
        model.addAttribute("cajaHoraCorte", "8:00 PM");

        // Pedidos Recientes
        List<PedidoForm> recientes = pedidoDAO.obtenerTodos();
        if (recientes.size() > 5) {
            recientes = recientes.subList(0, 5);
        }
        model.addAttribute("pedidosRecientes", recientes);

        return "index";
    }

    @GetMapping("/pedidos")
    public String mostrarPedidos(Model model) {
        List<PedidoForm> listaPedidos = pedidoDAO.obtenerTodos();
        model.addAttribute("pedidos", listaPedidos);
        return "pedidos";
    }

    @GetMapping("/nuevo-cliente")
    public String mostrarNuevoCliente() {
        return "nuevo-cliente";
    }

    @GetMapping("/nuevo-producto")
    public String mostrarNuevoProducto() {
        return "nuevo-producto";
    }

    @GetMapping("/servicios")
    public String mostrarServicios() {
        return "servicios";
    }

    @GetMapping("/servicio-convencional")
    public String mostrarConvencional() {
        return "servicio-convencional";
    }

    @GetMapping("/servicio-delicado")
    public String mostrarDelicado() {
        return "servicio-delicado";
    }

    @GetMapping("/servicio-tintoreria")
    public String mostrarTintoreria() {
        return "servicio-tintoreria";
    }

    @GetMapping("/servicio-industrial")
    public String mostrarIndustrial() {
        return "servicio-industrial";
    }

    @GetMapping("/color-ropa")
    public String mostrarColorRopa() {
        return "color-ropa";
    }

    @GetMapping("/datos-contacto")
    public String mostrarDatosContacto() {
        return "datos-contacto";
    }

    @GetMapping("/kiosco")
    public String bienvenidaKiosco() {
        return "kiosko-bienvenida";
    }

    @GetMapping("/kiosco/rastrear")
    public String formularioRastreo() {
        return "kiosko-buscar";
    }

    @GetMapping("/kiosco/resultado")
    public String resultadoRastreo(@RequestParam("ordenId") String ordenId, Model model) {
        String codigoBuscado = ordenId.replace("#", "").trim();
        PedidoForm pedidoEncontrado = null;

        try {
            int idNumerico = Integer.parseInt(codigoBuscado);
            pedidoEncontrado = pedidoDAO.buscarPorId(idNumerico);
        } catch (NumberFormatException e) {
            System.out.println("Formato de orden inválido.");
        }

        if (pedidoEncontrado != null) {
            model.addAttribute("pedido", pedidoEncontrado);
            return "kiosko-resultado"; // Corregido de 'kisko-resultado' a 'kiosko-resultado'
        } else {
            model.addAttribute("error", "No se encontró ningún pedido con el ID: " + ordenId);
            return "kiosko-buscar";
        }
    }
}