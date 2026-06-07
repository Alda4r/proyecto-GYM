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
import com.example.demo.repository.ComidaRepository;
import com.example.demo.repository.RecomendacionRepository;
import com.example.demo.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CaloriasController {

    @Autowired
    private ComidaRepository comidaRepository;

    @Autowired
    private RecomendacionRepository recomendacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==========================================
    // 1. VER PANEL DE CALORÍAS Y MACROS
    // ==========================================
    @GetMapping("/calorias")
    public String verCalorias(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        boolean isAdmin = usuarioLogueado.getEmail().equalsIgnoreCase("admin@gym.com");
        model.addAttribute("isAdmin", isAdmin);

        // Cargar las comidas registradas hoy por este usuario
        List<Comida> comidasConsumidasHoy = comidaRepository.findByUsuarioEmailAndFecha(
                usuarioLogueado.getEmail(), LocalDate.now());
        model.addAttribute("comidasHoy", comidasConsumidasHoy);

        // Calcular sumatorias para el anillo dinámico y las barras de progreso
        int metaDiaria = 2500; 
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

        // Controlar la visualización de sugerencias de acuerdo al Rol
        if (isAdmin) {
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("todasLasSugerencias", recomendacionRepository.findAll());
        } else {
            List<RecomendacionNutricional> sugerenciasAlumno = recomendacionRepository.findByUsuarioEmail(usuarioLogueado.getEmail());
            model.addAttribute("sugerencias", sugerenciasAlumno);
        }

        return "calorias"; 
    }

    // ==========================================
    // 2. AGREGAR COMIDA EN EL DIARIO (MANUAL)
    // ==========================================
    @PostMapping("/calorias/agregar")
    public String agregarComida(
            @RequestParam("tipoComida") String tipoComida,
            @RequestParam("nombreAlimento") String nombreAlimento,
            @RequestParam("calorias") int calorias,
            @RequestParam("proteina") int proteina,
            @RequestParam("carbohidratos") int carbohidratos,
            @RequestParam("grasas") int grasas,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Comida nuevaComida = new Comida();
        nuevaComida.setTipoComida(tipoComida);
        nuevaComida.setNombreAlimento(nombreAlimento);
        nuevaComida.setCalorias(calorias);
        nuevaComida.setProteina(proteina);
        nuevaComida.setCarbohidratos(carbohidratos);
        nuevaComida.setGrasas(grasas);
        nuevaComida.setFecha(LocalDate.now()); 
        nuevaComida.setUsuarioEmail(usuarioLogueado.getEmail()); 

        comidaRepository.save(nuevaComida);

        return "redirect:/calorias";
    }

    // ==========================================
    // 3. NUEVO: AGREGAR SUGERENCIA DEL ENTRENADOR AL DIARIO
    // ==========================================
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

        // Creamos la comida usando los datos sugeridos por el administrador
        Comida sugerenciaConsumida = new Comida();
        sugerenciaConsumida.setNombreAlimento(nombre);
        sugerenciaConsumida.setCalorias(calorias);
        sugerenciaConsumida.setTipoComida(tipoComida);
        sugerenciaConsumida.setUsuarioEmail(usuarioLogueado.getEmail());
        sugerenciaConsumida.setFecha(LocalDate.now()); // Registrado para hoy

        // Inicializamos los macros en 0 para cumplir con la estructura del modelo
        sugerenciaConsumida.setProteina(0);
        sugerenciaConsumida.setCarbohidratos(0);
        sugerenciaConsumida.setGrasas(0);

        // Guardamos de forma efectiva el registro en SQL Server
        comidaRepository.save(sugerenciaConsumida);

        // Redirecciona a la misma página refrescando macros e indicadores instantáneamente
        return "redirect:/calorias";
    }
}