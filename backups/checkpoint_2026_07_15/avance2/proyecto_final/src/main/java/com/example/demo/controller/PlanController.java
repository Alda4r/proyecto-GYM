package com.example.demo.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Usuario;
import com.example.demo.service.PlanMembresiaService;
import com.example.demo.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PlanController {

    private static final Logger log = LoggerFactory.getLogger(PlanController.class);

    @Autowired
    private PlanMembresiaService planMembresiaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/planes")
    public String verPlanes(Model model, HttpSession session) {
        log.warn("=== INICIO /planes ===");
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) {
            log.warn("Usuario null, redirect a login");
            return "redirect:/login";
        }
        log.warn("Usuario: {} admin: {}", user.getEmail(), "admin@gym.com".equalsIgnoreCase(user.getEmail()));

        try {
            boolean isAdmin = "admin@gym.com".equalsIgnoreCase(user.getEmail());
            var planes = planMembresiaService.findAll();
            log.warn("Planes encontrados: {}", planes.size());
            model.addAttribute("planes", planes);
            model.addAttribute("planActual", isAdmin ? null : user.getPlanMembresia());
            model.addAttribute("isAdmin", isAdmin);
            log.warn("=== FIN /planes OK ===");
            return "planes";
        } catch (Exception e) {
            log.error("Error en /planes:", e);
            throw e;
        }
    }

    @PostMapping("/planes/seleccionar")
    public String seleccionarPlan(@RequestParam("planId") Long planId,
                                   HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        var planOpt = planMembresiaService.findById(planId);
        if (planOpt.isEmpty()) return "redirect:/planes";

        var plan = planOpt.get();
        user.setPlanMembresia(plan);
        user.setFechaInicioPlan(LocalDate.now());
        user.setFechaFinPlan(LocalDate.now().plusMonths(plan.getDuracionMeses()));
        user.setMembresia("Activo");

        usuarioService.save(user);
        session.setAttribute("usuarioLogueado", user);

        return "redirect:/planes?contratado=ok";
    }

    @GetMapping("/planes/checkout")
    public String verCheckoutPlan(@RequestParam("planId") Long planId,
                                   Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        var planOpt = planMembresiaService.findById(planId);
        if (planOpt.isEmpty() || "admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/planes";
        }

        model.addAttribute("plan", planOpt.get());
        return "pago-plan";
    }

    @PostMapping("/planes/procesar-pago")
    public String procesarPagoPlan(@RequestParam("planId") Long planId,
                                    @RequestParam("cardNumber") String cardNumber,
                                    @RequestParam("cardName") String cardName,
                                    @RequestParam("cardExpiry") String cardExpiry,
                                    @RequestParam("cardCvv") String cardCvv,
                                    HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        if (cardNumber == null || cardNumber.replace(" ", "").length() < 13 ||
            cardName == null || cardName.isBlank() ||
            cardExpiry == null || !cardExpiry.matches("\\d{2}/\\d{2}") ||
            cardCvv == null || cardCvv.length() < 3) {
            return "redirect:/planes/checkout?planId=" + planId + "&error=datos";
        }

        var planOpt = planMembresiaService.findById(planId);
        if (planOpt.isEmpty()) return "redirect:/planes";

        var plan = planOpt.get();
        user.setPlanMembresia(plan);
        user.setFechaInicioPlan(LocalDate.now());
        user.setFechaFinPlan(LocalDate.now().plusMonths(plan.getDuracionMeses()));
        user.setMembresia("Activo");

        usuarioService.save(user);
        session.setAttribute("usuarioLogueado", user);

        return "redirect:/planes?contratado=ok";
    }

    @PostMapping("/planes/admin/guardar")
    public String guardarPlan(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam("nombre") String nombre,
                              @RequestParam("descripcion") String descripcion,
                              @RequestParam("duracionMeses") int duracionMeses,
                              @RequestParam("precio") double precio,
                              @RequestParam(value = "beneficios", required = false) String beneficios,
                              HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }

        var plan = (id != null) ? planMembresiaService.findById(id).orElse(new com.example.demo.model.PlanMembresia())
                                : new com.example.demo.model.PlanMembresia();
        plan.setNombre(nombre);
        plan.setDescripcion(descripcion);
        plan.setDuracionMeses(duracionMeses);
        plan.setPrecio(precio);
        plan.setActivo(true);
        plan.setBeneficios(beneficios);
        planMembresiaService.save(plan);

        return "redirect:/planes";
    }

    @PostMapping("/planes/admin/toggle-estado")
    public String toggleEstadoPlan(@RequestParam("id") Long id,
                                    HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        planMembresiaService.findById(id).ifPresent(p -> {
            p.setActivo(!p.isActivo());
            planMembresiaService.save(p);
        });
        return "redirect:/planes";
    }

    @PostMapping("/planes/admin/eliminar")
    public String eliminarPlan(@RequestParam("id") Long id,
                                HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        planMembresiaService.deleteById(id);
        return "redirect:/planes";
    }
}
