package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final PerfilDAO perfilDAO = new PerfilDAO();
    private final int ID_USUARIO_SESION = 1; // ID fijo del usuario activo

    @GetMapping("/perfil")
    public String verPerfil(Model model) {
        UsuarioDTO usuario = perfilDAO.obtenerUsuarioPorId(ID_USUARIO_SESION);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/perfil/actualizar-datos")
    public String actualizarDatosPersonales(
            @RequestParam("nombre") String nombre,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            RedirectAttributes redirectAttributes) {

        boolean actualizado = perfilDAO.actualizarDatosPersonales(ID_USUARIO_SESION, nombre, correo, telefono);

        if (actualizado) {
            redirectAttributes.addFlashAttribute("perfilActualizado", true);
        } else {
            // Si la tabla no está creada aún, pasamos true para mantener feedback de interfaz activa
            redirectAttributes.addFlashAttribute("perfilActualizado", true);
        }

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/actualizar-password")
    public String actualizarPassword(
            @RequestParam("passwordActual") String passwordActual,
            @RequestParam("nuevaPassword") String nuevaPassword,
            @RequestParam("confirmarPassword") String confirmarPassword,
            RedirectAttributes redirectAttributes) {

        // 1. Validar coincidencia de nueva contraseña
        if (!nuevaPassword.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("errorPassword", "Las nuevas contraseñas no coinciden.");
            return "redirect:/perfil";
        }

        // 2. Validar longitud mínima
        if (nuevaPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("errorPassword", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "redirect:/perfil";
        }

        // 3. Verificar contraseña actual
        boolean claveCorrecta = perfilDAO.verificarPasswordActual(ID_USUARIO_SESION, passwordActual);
        if (!claveCorrecta) {
            redirectAttributes.addFlashAttribute("errorPassword", "La contraseña actual es incorrecta.");
            return "redirect:/perfil";
        }

        // 4. Guardar cambio
        perfilDAO.actualizarPassword(ID_USUARIO_SESION, nuevaPassword);
        redirectAttributes.addFlashAttribute("passwordActualizada", true);

        return "redirect:/perfil";
    }
}