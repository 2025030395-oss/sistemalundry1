package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final InsumoDAO insumoDAO = new InsumoDAO();
    private final MaquinariaDAO maquinariaDAO = new MaquinariaDAO();
    private final ServicioInsumoDAO servicioInsumoDAO = new ServicioInsumoDAO();

    @GetMapping
    public String mostrarInventario(Model model) {
        List<Insumo> listaInsumos = insumoDAO.obtenerTodos();
        List<Maquinaria> listaMaquinaria = maquinariaDAO.obtenerTodas();

        model.addAttribute("insumos", listaInsumos);
        model.addAttribute("maquinarias", listaMaquinaria);

        return "inventario";
    }

    @PostMapping("/guardar-insumo")
    // ELIMINAMOS @ResponseBody para que Spring sepa que queremos redirigir a una vista HTML
    public String guardarInsumo(
            @RequestParam("nombreProducto") String nombre,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "unidadMedida", required = false) String unidadMedida,
            @RequestParam(value = "presentacion", required = false) String presentacion,
            @RequestParam("stockActual") double stockActual,
            @RequestParam("stockMinimo") double stockMinimo,
            @RequestParam("stockMaximo") double stockMaximo,
            @RequestParam(value = "proveedor", required = false) String proveedor,
            @RequestParam(value = "fechaCompraStr", required = false) String fechaCompraStr,
            @RequestParam("costoUsoServicio") double costoUsoServicio) {

        Insumo insumo = new Insumo();
        insumo.setNombreProducto(nombre);
        insumo.setCategoria(categoria);
        insumo.setUnidadMedida(unidadMedida);
        insumo.setPresentacion(presentacion);
        insumo.setStockActual(stockActual);
        insumo.setStockMinimo(stockMinimo);
        insumo.setStockMaximo(stockMaximo);
        insumo.setProveedor(proveedor);
        insumo.setCostoUsoServicio(costoUsoServicio);

        if (fechaCompraStr != null && !fechaCompraStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                insumo.setFechaCompra(sdf.parse(fechaCompraStr));
            } catch (Exception e) {
                insumo.setFechaCompra(new Date());
            }
        } else {
            insumo.setFechaCompra(new Date());
        }

        insumoDAO.insertar(insumo);

        // REDIRIGIMOS a la página principal del inventario
        return "redirect:/inventario";
    }

    @PostMapping("/guardar-maquinaria")
    // ELIMINAMOS @ResponseBody aquí también por el mismo motivo
    public String guardarMaquinaria(
            @RequestParam("nombreEquipo") String nombreEquipo,
            @RequestParam("tipo") String tipo,
            @RequestParam(value = "capacidad", required = false) String capacidad,
            @RequestParam(value = "estadoOperacion", defaultValue = "Disponible") String estadoOperacion) {

        Maquinaria m = new Maquinaria();
        m.setNombreEquipo(nombreEquipo);
        m.setTipo(tipo);
        m.setCapacidad(capacidad);
        m.setEstadoOperacion(estadoOperacion);

        maquinariaDAO.insertar(m);

        // REDIRIGIMOS a la página principal del inventario
        return "redirect:/inventario";
    }

    // Nota: Dejé estos dos métodos con @ResponseBody asumiendo que los usas con llamadas de
    // JavaScript (AJAX) por debajo del agua sin recargar la página. Si también te dan
    // pantalla blanca al usarlos, dímelo y los cambiamos a redirect también.

    @PostMapping("/actualizar-estado-maquinaria")
    @ResponseBody
    public String actualizarEstadoMaquinaria(
            @RequestParam("idEquipo") int idEquipo,
            @RequestParam("estado") String estado) {

        boolean ok = maquinariaDAO.actualizarEstado(idEquipo, estado);
        return ok ? "{\"status\":\"ok\"}" : "{\"status\":\"error\"}";
    }

    @PostMapping("/vincular-servicio-insumos")
    @ResponseBody
    public String vincularInsumos(
            @RequestParam("idServicioConfig") int idServicioConfig,
            @RequestParam("idsProductos") List<Integer> idsProductos) {

        boolean ok = servicioInsumoDAO.vincularInsumosAServicio(idServicioConfig, idsProductos);
        return ok ? "{\"status\":\"ok\"}" : "{\"status\":\"error\"}";
    }
}