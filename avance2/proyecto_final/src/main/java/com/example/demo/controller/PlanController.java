package com.example.demo.controller;

import java.time.LocalDate;

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

    @Autowired
    private PlanMembresiaService planMembresiaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/planes")
    public String verPlanes(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null) return "redirect:/login";

        model.addAttribute("planes", planMembresiaService.findAllActivos());
        model.addAttribute("planActual", user.getPlanMembresia());
        return "planes";
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

    @GetMapping("/planes/admin")
    public String adminPlanes(Model model, HttpSession session) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogueado");
        if (user == null || !"admin@gym.com".equalsIgnoreCase(user.getEmail())) {
            return "redirect:/login";
        }
        model.addAttribute("planes", planMembresiaService.findAll());
        return "admin_planes";
    }

    @PostMapping("/planes/admin/guardar")
    public String guardarPlan(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam("nombre") String nombre,
                              @RequestParam("descripcion") String descripcion,
                              @RequestParam("duracionMeses") int duracionMeses,
                              @RequestParam("precio") double precio,
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
        planMembresiaService.save(plan);

        return "redirect:/planes/admin";
    }
}
