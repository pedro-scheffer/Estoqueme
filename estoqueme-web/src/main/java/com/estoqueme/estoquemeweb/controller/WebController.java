package com.estoqueme.estoquemeweb.controller; // Ajuste o pacote conforme o seu projeto

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para servir as páginas HTML do frontend.
 * Não contém lógica de backend, apenas mapeia URLs para templates.
 */
@Controller
public class WebController {

    @GetMapping("/Estoqueme")
    public String dashboard() {
        return "dashboard"; // A raiz agora vai direto para o dashboard
    }

    @GetMapping("/dashboard")
    public String dashboardPage() { // Renomeado para evitar conflito de método
        return "dashboard";
    }

    @GetMapping("/cadastro-produto")
    public String cadastroProduto() {
        return "cadastroProduto";
    }

    @GetMapping("/deposito-retirada")
    public String depositoRetirada() {
        return "depositoRetirada";
    }
}