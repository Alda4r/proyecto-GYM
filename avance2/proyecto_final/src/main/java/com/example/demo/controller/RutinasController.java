package com.example.demo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Ejercicio;
import com.example.demo.model.Rutina;
import com.example.demo.model.Usuario;
import com.example.demo.repository.EjercicioRepository;
import com.example.demo.repository.HistorialRepository;
import com.example.demo.repository.RutinaRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class RutinasController {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private EjercicioRepository ejercicioRepository;

    // ==========================================
    // 1. LISTAR RUTINAS (MUESTRA TÍTULOS LIMPIOS)
    // ==========================================
    @GetMapping("/rutinas")
    public String rutinas(
            @RequestParam(value = "tab", required = false, defaultValue = "espalda") String tabActiva,
            @RequestParam(value = "objetivoFiltro", required = false) String objetivoFiltro,
            HttpSession session, 
            Model model) {
        
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        model.addAttribute("activeTab", tabActiva);

        boolean isAdmin = "admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail());
        model.addAttribute("isAdmin", isAdmin);

        String objetivoUsuario = usuarioLogueado.getObjetivo();
        String objetivoMostrado = objetivoUsuario != null && !objetivoUsuario.isBlank() ? objetivoUsuario : "Ganar Peso";
        String objetivoFiltrado = normalizarObjetivo(objetivoMostrado);

        if (isAdmin && objetivoFiltro != null && !objetivoFiltro.isBlank()) {
            objetivoMostrado = objetivoFiltro;
            objetivoFiltrado = normalizarObjetivo(objetivoFiltro);
        }

        model.addAttribute("objetivoActual", objetivoMostrado);
        model.addAttribute("objetivoFiltro", objetivoMostrado);

        String emailUsuario = usuarioLogueado.getEmail();
        List<Rutina> todasLasRutinas = rutinaRepository.findAll();

        model.addAttribute("rutinasEspalda", filtrarRutinas(todasLasRutinas, "Torso", "espalda", objetivoFiltrado, emailUsuario));
        model.addAttribute("rutinasPecho", filtrarRutinas(todasLasRutinas, "Torso", "pecho", objetivoFiltrado, emailUsuario));
        model.addAttribute("rutinasPierna", filtrarRutinas(todasLasRutinas, "Tren Inferior", "", objetivoFiltrado, emailUsuario));
        
        List<Rutina> fuerzaFiltrada = new ArrayList<>();
        if ("Hacerse más fuerte".equalsIgnoreCase(objetivoFiltrado)) {
            fuerzaFiltrada = filtrarRutinas(todasLasRutinas, "", "fuerza", objetivoFiltrado, emailUsuario);
        }
        model.addAttribute("rutinasFuerza", fuerzaFiltrada);

        return "rutinas";
    }

    private List<Rutina> filtrarRutinas(List<Rutina> lista, String grupo, String filtroNombre, String obj, String email) {
        String objetivoNormalizado = normalizarObjetivo(obj);
        java.util.Map<String, Rutina> mapa = new java.util.LinkedHashMap<>();
        for (Rutina r : lista) {
            if (!grupo.isEmpty() && !grupo.equalsIgnoreCase(r.getGrupoMuscular())) continue;
            if (!filtroNombre.isEmpty() && !r.getNombre().toLowerCase().contains(filtroNombre)) continue;
            if (!normalizarObjetivo(r.getObjetivoAsociado()).equalsIgnoreCase(objetivoNormalizado)) continue;
            if (!(r.getUsuarioEmail() == null || r.getUsuarioEmail().equalsIgnoreCase(email))) continue;

            String nombreBase = r.getNombre().replaceAll(" - .*", "");
            Rutina existente = mapa.get(nombreBase);
            if (existente == null) {
                Rutina copia = new Rutina();
                copia.setId(r.getId());
                copia.setNombre(nombreBase);
                copia.setNivel(r.getNivel());
                copia.setTiempo(r.getTiempo());
                copia.setGrupoMuscular(r.getGrupoMuscular());
                copia.setObjetivoAsociado(r.getObjetivoAsociado());
                copia.setUsuarioEmail(r.getUsuarioEmail());
                copia.setListaEjercicios(r.getListaEjercicios());
                mapa.put(nombreBase, copia);
            } else {
                if (r.getUsuarioEmail() != null && r.getUsuarioEmail().equalsIgnoreCase(email)) {
                    Rutina copia = new Rutina();
                    copia.setId(r.getId());
                    copia.setNombre(nombreBase);
                    copia.setNivel(r.getNivel());
                    copia.setTiempo(r.getTiempo());
                    copia.setGrupoMuscular(r.getGrupoMuscular());
                    copia.setObjetivoAsociado(r.getObjetivoAsociado());
                    copia.setUsuarioEmail(r.getUsuarioEmail());
                    copia.setListaEjercicios(r.getListaEjercicios());
                    mapa.put(nombreBase, copia);
                }
            }
        }
        return new ArrayList<>(mapa.values());
    }

    private String normalizarObjetivo(String objetivo) {
        if (objetivo == null || objetivo.isBlank()) {
            return "Ganar Peso";
        }
        String valor = objetivo.trim();
        if (valor.equalsIgnoreCase("Bajar Peso") || valor.equalsIgnoreCase("Bajar peso")) {
            return "Perder Peso";
        }
        if (valor.equalsIgnoreCase("Perder Peso") || valor.equalsIgnoreCase("Perder peso")) {
            return "Perder Peso";
        }
        if (valor.equalsIgnoreCase("Ganar Fuerza") || valor.equalsIgnoreCase("Ganar fuerza")) {
            return "Hacerse más fuerte";
        }
        return valor;
    }

    // ==========================================
    // 2. VER DETALLE
    // ==========================================
    @GetMapping("/rutina/detalle")
    public String verDetailRutina(@RequestParam("id") Long idRutina, Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        Rutina rutina = rutinaRepository.findById(idRutina).orElse(null);
        if (rutina == null) return "redirect:/rutinas";

        model.addAttribute("idRutina", rutina.getId());
        model.addAttribute("nombreRutina", rutina.getNombre().replaceAll(" - .*", ""));
        model.addAttribute("ejercicios", ejercicioRepository.findByRutinaId(rutina.getId()));

        return "detalle_rutina";
    }

    // =========================================================================
    // 3. PERSONALIZAR: Actualiza sin duplicar ejercicios y redirige a la pestaña
    // =========================================================================
    @PostMapping("/rutina/personalizar")
    public String guardarRutinaPersonalizada(
            @RequestParam("idRutinaBase") Long idRutinaBase,
            @RequestParam("ejercicioId") List<Long> ejercicioIds,
            @RequestParam("series") List<Integer> listaSeries,
            @RequestParam("repeticiones") List<Integer> listaRepeticiones,
            @RequestParam("peso") List<String> listaPesos,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        Rutina rutinaBase = rutinaRepository.findById(idRutinaBase).orElse(null);
        if (rutinaBase == null) return "redirect:/rutinas";

        Rutina rutinaDestino;
        String emailAtleta = usuarioLogueado.getEmail();
        String nombreBase = rutinaBase.getNombre().replaceAll(" - .*", "");
        String nombreUnicoBD = nombreBase + " - " + emailAtleta;

        // Lógica de actualización (si es dueño, actualiza; si es global, clona)
        if (emailAtleta.equalsIgnoreCase(rutinaBase.getUsuarioEmail())) {
            rutinaDestino = rutinaBase;
            rutinaDestino.setNombre(nombreUnicoBD);
            rutinaDestino = rutinaRepository.save(rutinaDestino);
        } else {
            Rutina copiaExistente = rutinaRepository.findAll().stream()
                    .filter(r -> emailAtleta.equalsIgnoreCase(r.getUsuarioEmail()) && r.getNombre().equalsIgnoreCase(nombreUnicoBD))
                    .findFirst().orElse(null);
            
            if (copiaExistente != null) {
                rutinaDestino = copiaExistente;
            } else {
                rutinaDestino = new Rutina();
                rutinaDestino.setNombre(nombreUnicoBD);
                rutinaDestino.setNivel(rutinaBase.getNivel());
                rutinaDestino.setTiempo(rutinaBase.getTiempo());
                rutinaDestino.setGrupoMuscular(rutinaBase.getGrupoMuscular());
                rutinaDestino.setObjetivoAsociado(rutinaBase.getObjetivoAsociado());
                rutinaDestino.setUsuarioEmail(emailAtleta);
                rutinaDestino = rutinaRepository.save(rutinaDestino);
            }
        }

        // ACTUALIZACIÓN DE EJERCICIOS (sin duplicar, mapeando por índice)
        List<Ejercicio> ejerciciosActuales = ejercicioRepository.findByRutinaId(rutinaDestino.getId());
        
        if (ejerciciosActuales.isEmpty()) {
            List<Ejercicio> nuevos = new ArrayList<>();
            for (int i = 0; i < ejercicioIds.size(); i++) {
                Ejercicio ej = new Ejercicio();
                ej.setNombre(ejercicioRepository.findById(ejercicioIds.get(i)).map(Ejercicio::getNombre).orElse("Ejercicio"));
                ej.setSeries(listaSeries.get(i));
                ej.setRepeticiones(listaRepeticiones.get(i));
                ej.setPesoSugerido(listaPesos.get(i).contains("kg") ? listaPesos.get(i) : listaPesos.get(i) + " kg");
                ej.setRutina(rutinaDestino);
                nuevos.add(ej);
            }
            ejercicioRepository.saveAll(nuevos);
        } else {
            for (int i = 0; i < listaSeries.size() && i < ejerciciosActuales.size(); i++) {
                Ejercicio ej = ejerciciosActuales.get(i);
                ej.setSeries(listaSeries.get(i));
                ej.setRepeticiones(listaRepeticiones.get(i));
                ej.setPesoSugerido(listaPesos.get(i).contains("kg") ? listaPesos.get(i) : listaPesos.get(i) + " kg");
                ejercicioRepository.save(ej);
            }
        }

        // REDIRECCIÓN INTELIGENTE AL TAB ACTIVO (JERARQUÍA DE FUERZA PRIORIZADA)
        String tab = "espalda"; // Valor predeterminado
        String nombreMin = nombreBase.toLowerCase();
        String grupoMin = rutinaBase.getGrupoMuscular() != null ? rutinaBase.getGrupoMuscular().toLowerCase() : "";
        if (nombreMin.contains("espalda")) {
            tab = "espalda";
        } 
        // 2. Si es Pecho
        else if (nombreMin.contains("pecho")) {
            tab = "pecho";
        } 
        // 3. Si es Pierna (evaluamos grupo muscular o nombre)
        else if (grupoMin.contains("inferior") || nombreMin.contains("pierna")) {
            tab = "pierna";
        } 
        // 4. SOLO si no es ninguna de las anteriores, evaluamos Fuerza
        else if (nombreMin.contains("fuerza") || nombreMin.contains("olímpico") || nombreMin.contains("pura")) {
            tab = "fuerza";
        }
        
        return "redirect:/rutinas?tab=" + tab;
    }

    @PostMapping("/rutina/eliminar")
    public String eliminarRutina(@RequestParam("idRutina") Long idRutina,
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "objetivoFiltro", required = false) String objetivoFiltro,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        Rutina r = rutinaRepository.findById(idRutina).orElse(null);
        if (r == null) return "redirect:/rutinas";

        boolean isAdmin = "admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail());
        if (isAdmin || usuarioLogueado.getEmail().equalsIgnoreCase(r.getUsuarioEmail())) {
            rutinaRepository.deleteById(idRutina);
        }

        String redirect = "redirect:/rutinas";
        String params = "";
        if (tab != null && !tab.isBlank()) params += "tab=" + java.net.URLEncoder.encode(tab, java.nio.charset.StandardCharsets.UTF_8);
        if (objetivoFiltro != null && !objetivoFiltro.isBlank()) params += (params.isEmpty() ? "" : "&") + "objetivoFiltro=" + java.net.URLEncoder.encode(objetivoFiltro, java.nio.charset.StandardCharsets.UTF_8);
        if (!params.isEmpty()) redirect += "?" + params;
        return redirect;
    }

    @GetMapping("/rutina/agregar-ejercicio")
    public String mostrarAgregarEjercicio(
            @RequestParam("id") Long idRutina,
            @RequestParam(value = "objetivoFiltro", required = false) String objetivoFiltro,
            @RequestParam(value = "tab", required = false, defaultValue = "espalda") String tab,
            HttpSession session, Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !"admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail())) {
            return "redirect:/login";
        }

        Rutina rutina = rutinaRepository.findById(idRutina).orElse(null);
        if (rutina == null) {
            return "redirect:/rutinas";
        }

        model.addAttribute("idRutina", rutina.getId());
        model.addAttribute("nombreRutina", rutina.getNombre().replaceAll(" - .*", ""));
        model.addAttribute("objetivoFiltro", objetivoFiltro != null && !objetivoFiltro.isBlank() ? objetivoFiltro : rutina.getObjetivoAsociado());
        model.addAttribute("tab", tab);
        return "agregar_ejercicio";
    }

    @PostMapping("/rutina/agregar-ejercicio")
    public String agregarEjercicio(
            @RequestParam("idRutina") Long idRutina,
            @RequestParam("nombreEjercicio") String nombreEjercicio,
            @RequestParam("series") int series,
            @RequestParam("repeticiones") int repeticiones,
            @RequestParam("pesoSugerido") String pesoSugerido,
            @RequestParam(value = "objetivoFiltro", required = false) String objetivoFiltro,
            @RequestParam(value = "tab", required = false, defaultValue = "espalda") String tab,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !"admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail())) {
            return "redirect:/login";
        }

        Rutina rutina = rutinaRepository.findById(idRutina).orElse(null);
        if (rutina == null) {
            return "redirect:/rutinas";
        }

        Ejercicio nuevoEjercicio = new Ejercicio();
        nuevoEjercicio.setNombre(nombreEjercicio);
        nuevoEjercicio.setSeries(series);
        nuevoEjercicio.setRepeticiones(repeticiones);
        String pesoFinal = (pesoSugerido != null && pesoSugerido.toLowerCase().contains("kg")) ? pesoSugerido : pesoSugerido + " kg";
        nuevoEjercicio.setPesoSugerido(pesoFinal);
        nuevoEjercicio.setRutina(rutina);
        ejercicioRepository.save(nuevoEjercicio);

        String objetivoRedireccion = objetivoFiltro != null && !objetivoFiltro.isBlank() ? objetivoFiltro : rutina.getObjetivoAsociado();
        return "redirect:/rutinas?tab=" + tab + "&objetivoFiltro=" + URLEncoder.encode(objetivoRedireccion, StandardCharsets.UTF_8);
    }

    @GetMapping("/rutina/nueva")
    public String mostrarCrearRutina(
            @RequestParam(value = "objetivoFiltro", required = false) String objetivoFiltro,
            @RequestParam(value = "grupo", required = false, defaultValue = "Torso") String grupo,
            @RequestParam(value = "tab", required = false, defaultValue = "espalda") String tab,
            HttpSession session, Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !"admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail())) {
            return "redirect:/login";
        }

        model.addAttribute("objetivoFiltro", objetivoFiltro != null && !objetivoFiltro.isBlank() ? objetivoFiltro : "Ganar Peso");
        model.addAttribute("grupo", grupo);
        model.addAttribute("tab", tab);
        return "nueva_rutina";
    }

    @PostMapping("/rutina/nueva")
    public String crearRutina(
            @RequestParam("nombre") String nombre,
            @RequestParam("nivel") String nivel,
            @RequestParam("tiempo") String tiempo,
            @RequestParam("grupoMuscular") String grupoMuscular,
            @RequestParam("objetivoAsociado") String objetivoAsociado,
            @RequestParam(value = "tab", required = false, defaultValue = "espalda") String tab,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || !"admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail())) {
            return "redirect:/login";
        }

        // Aseguramos nombre único: si existe, añadimos timestamp
        String baseNombre = nombre;
        boolean exists = rutinaRepository.findAll().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(baseNombre));
        String nombreFinal = baseNombre;
        if (exists) {
            nombreFinal = baseNombre + " - " + System.currentTimeMillis();
        }

        Rutina nueva = new Rutina();
        nueva.setNombre(nombreFinal);
        nueva.setNivel(nivel);
        nueva.setTiempo(tiempo);
        nueva.setGrupoMuscular(grupoMuscular);
        nueva.setObjetivoAsociado(objetivoAsociado);
        nueva.setUsuarioEmail(null); // rutina global
        nueva = rutinaRepository.save(nueva);

        // Redirigimos al flujo de agregar ejercicios para poblar la rutina recién creada
        return "redirect:/rutina/agregar-ejercicio?id=" + nueva.getId() + "&tab=" + tab + "&objetivoFiltro=" + URLEncoder.encode(objetivoAsociado, StandardCharsets.UTF_8);
    }
}
