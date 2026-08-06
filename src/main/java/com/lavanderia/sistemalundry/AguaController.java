package com.lavanderia.sistemalundry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AguaController {

    private final ConsumoAguaDAO aguaDAO = new ConsumoAguaDAO();

    @GetMapping("/gasto-agua")
    public String mostrarGastoAgua(Model model) {

        List<ConsumoAgua> listaAgua = aguaDAO.obtenerTodos();

        double gastoTotal = 0;
        double costoTotal = 0;

        for (ConsumoAgua item : listaAgua) {
            gastoTotal += item.getLitrosConsumidos();
            costoTotal += item.getCostoEstimado();
        }

        double promedioDiario = listaAgua.isEmpty() ? 0 : (gastoTotal / listaAgua.size());

        model.addAttribute("registrosAgua", listaAgua);
        model.addAttribute("gastoTotal", gastoTotal);
        model.addAttribute("costoTotal", costoTotal);
        model.addAttribute("promedioDiario", promedioDiario);
        model.addAttribute("litrosPorKilo", 19.5);

        return "gasto-agua";
    }
}