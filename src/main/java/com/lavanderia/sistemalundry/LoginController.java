package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "password", required = false) String password) {


        if (username == null || password == null) {
            return "redirect:/login?error";
        }

        String usuarioCorrecto = "Paulina";
        String contrasenaCorrecta = "1234";

        if (username.equals(usuarioCorrecto) && password.equals(contrasenaCorrecta)) {
            return "redirect:/index"; // Redirige al dashboard
        } else {
            return "redirect:/login?error";
        }
    }
}