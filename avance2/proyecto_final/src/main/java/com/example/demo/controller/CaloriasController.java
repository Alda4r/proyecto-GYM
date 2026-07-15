package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Comida;
import com.example.demo.model.RecomendacionNutricional;
import com.example.demo.model.Usuario;
import com.example.demo.service.ComidaService;
import com.example.demo.service.RecomendacionService;
import com.example.demo.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CaloriasController {

    @Autowired
    private ComidaService comidaService;

    @Autowired
    private RecomendacionService recomendacionService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/calorias")
    public String verCalorias(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        boolean isAdmin = usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com");
        model.addAttribute("isAdmin", isAdmin);

        List<Comida> comidasConsumidasHoy = comidaService.findByUsuarioEmailAndFecha(
                usuarioLogueado.getEmail(), LocalDate.now());
        model.addAttribute("comidasHoy", comidasConsumidasHoy);

        int metaDiaria = usuarioLogueado.getMetaCalorias() != null
                ? usuarioLogueado.getMetaCalorias()
                : 2500;
        int totalCaloriasConsumidas = comidasConsumidasHoy.stream().mapToInt(Comida::getCalorias).sum();
        int totalProteina = comidasConsumidasHoy.stream().mapToInt(Comida::getProteina).sum();
        int totalCarbos = comidasConsumidasHoy.stream().mapToInt(Comida::getCarbohidratos).sum();
        int totalGrasas = comidasConsumidasHoy.stream().mapToInt(Comida::getGrasas).sum();

        model.addAttribute("meta", metaDiaria);
        model.addAttribute("restantes", Math.max(0, metaDiaria - totalCaloriasConsumidas));
        model.addAttribute("totalProteina", totalProteina);
        model.addAttribute("totalCarbos", totalCarbos);
        model.addAttribute("totalGrasas", totalGrasas);
        model.addAttribute("objetivoActual", usuarioLogueado.getObjetivo());

        if (isAdmin) {
            /*
             * Excluir al admin del dropdown de destinatarios
             * para que no pueda auto-asignarse dietas.
             */
            List<Usuario> alumnos = usuarioService.findAll().stream()
                .filter(u -> !"admin@gym.com".equalsIgnoreCase(u.getEmail()))
                .toList();
            model.addAttribute("usuarios", alumnos);
            model.addAttribute("todasLasSugerencias", recomendacionService.findAll());
        } else {
            List<RecomendacionNutricional> sugerenciasAlumno = recomendacionService
                    .findByUsuarioEmail(usuarioLogueado.getEmail());
            model.addAttribute("sugerencias", sugerenciasAlumno);
        }

        return "calorias";
    }

    @PostMapping("/calorias/agregar")
    public String agregarComida(
            @RequestParam("tipoComida") String tipoComida,
            @RequestParam("nombreAlimento") String nombreAlimento,
            @RequestParam("calorias") int calorias,
            @RequestParam("proteina") int proteina,
            @RequestParam("carbohidratos") int carbohidratos,
            @RequestParam("grasas") int grasas,
            HttpSession session) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        Usuario usuarioLogueado = usuarioService.findByEmail(usuarioSesion.getEmail())
                .orElse(usuarioSesion);

        session.setAttribute("usuarioLogueado", usuarioLogueado);

        Comida nuevaComida = new Comida();
        nuevaComida.setTipoComida(tipoComida);
        nuevaComida.setNombreAlimento(nombreAlimento);
        nuevaComida.setCalorias(calorias);
        nuevaComida.setProteina(proteina);
        nuevaComida.setCarbohidratos(carbohidratos);
        nuevaComida.setGrasas(grasas);
        nuevaComida.setFecha(LocalDate.now());
        nuevaComida.setUsuarioEmail(usuarioLogueado.getEmail());

        comidaService.save(nuevaComida);

        return "redirect:/calorias";
    }

    @PostMapping("/calorias/agregar-sugerencia")
    public String procesarSugerenciaEntrenador(
            @RequestParam("nombre") String nombre,
            @RequestParam("calorias") int calorias,
            @RequestParam("tipoComida") String tipoComida,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Comida sugerenciaConsumida = new Comida();
        sugerenciaConsumida.setNombreAlimento(nombre);
        sugerenciaConsumida.setCalorias(calorias);
        sugerenciaConsumida.setTipoComida(tipoComida);
        sugerenciaConsumida.setUsuarioEmail(usuarioLogueado.getEmail());
        sugerenciaConsumida.setFecha(LocalDate.now());

        sugerenciaConsumida.setProteina(0);
        sugerenciaConsumida.setCarbohidratos(0);
        sugerenciaConsumida.setGrasas(0);

        comidaService.save(sugerenciaConsumida);

        return "redirect:/calorias";
    }
}
