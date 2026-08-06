package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ClienteController {

    // Instanciamos el DAO encargado de hablar con la Base de Datos
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @GetMapping("/clientes")
    public String mostrarClientes(Model model) {
        // Traemos la lista actualizada directamente desde MySQL
        List<Cliente> listaClientesBD = clienteDAO.obtenerTodos();

        // Le mandamos los datos reales a clientes.html
        model.addAttribute("clientes", listaClientesBD);
        return "clientes";
    }

    @PostMapping("/clientes/guardar")
    public String guardarNuevoCliente(
            @RequestParam("nombre") String nombre,
            @RequestParam("telefono") String telefono,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "direccion", required = false) String direccion,
            RedirectAttributes redirectAttributes) {

        // Creamos el objeto con los datos del formulario
        Cliente nuevoCliente = new Cliente(nombre, telefono, correo, direccion);

        // Guardamos el cliente de verdad en MySQL a través del DAO
        boolean guardadoExitoso = clienteDAO.insertar(nuevoCliente);

        if (guardadoExitoso) {
            System.out.println("¡Guardado en BD con éxito! -> Nombre: " + nombre);
            redirectAttributes.addFlashAttribute("clienteCreado", true);
        } else {
            System.out.println("❌ Error crítico: No se pudo guardar el cliente en la base de datos.");
            redirectAttributes.addFlashAttribute("errorGuardar", true);
        }

        // Redirige dinámicamente a la lista general para recargar los datos actualizados de MySQL
        return "redirect:/clientes";
    }
    @GetMapping("/clientes/eliminar/{id}")
    public String eliminarCliente(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        boolean eliminado = clienteDAO.eliminar(id);

        if (eliminado) {
            System.out.println("🗑️ Cliente con ID " + id + " eliminado con éxito de MySQL.");
            redirectAttributes.addFlashAttribute("clienteEliminado", true);
        } else {
            System.out.println("❌ No se pudo eliminar el cliente con ID " + id);
            redirectAttributes.addFlashAttribute("errorEliminar", true);
        }

        return "redirect:/clientes";
    }
}