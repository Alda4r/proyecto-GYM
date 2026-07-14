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
import com.example.demo.model.HistorialEntrenamiento;
import com.example.demo.model.Rutina;
import com.example.demo.model.Usuario;
import com.example.demo.service.EjercicioService;
import com.example.demo.service.HistorialService;
import com.example.demo.service.RutinaService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RutinasController {

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private EjercicioService ejercicioService;

    @Autowired
    private HistorialService historialService;

    @GetMapping("/rutinas")
    public String rutinas(
            @RequestParam(value = "tab", required = false, defaultValue = "espalda") String tabActiva,
            @RequestParam(value = "objetivoFiltro", required = false) String objetivoFiltro,
            HttpSession session,
            Model model) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

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
        List<Rutina> todasLasRutinas = rutinaService.findAll();

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
            if (!grupo.isEmpty() && !grupo.equalsIgnoreCase(r.getGrupoMuscular())) {
                continue;
            }
            if (!filtroNombre.isEmpty() && !r.getNombre().toLowerCase().contains(filtroNombre)) {
                continue;
            }
            if (!normalizarObjetivo(r.getObjetivoAsociado()).equalsIgnoreCase(objetivoNormalizado)) {
                continue;
            }
            if (!(r.getUsuarioEmail() == null || r.getUsuarioEmail().equalsIgnoreCase(email))) {
                continue;
            }

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

    @GetMapping("/rutina/detalle")
    public String verDetailRutina(@RequestParam("id") Long idRutina, Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Rutina rutina = rutinaService.findById(idRutina).orElse(null);
        if (rutina == null) {
            return "redirect:/rutinas";
        }

        model.addAttribute("idRutina", rutina.getId());
        model.addAttribute("nombreRutina", rutina.getNombre().replaceAll(" - .*", ""));
        model.addAttribute("ejercicios", ejercicioService.findByRutinaId(rutina.getId()));

        return "detalle_rutina";
    }

    @PostMapping("/rutina/personalizar")
    public String guardarRutinaPersonalizada(
            @RequestParam("idRutinaBase") Long idRutinaBase,
            @RequestParam("ejercicioId") List<Long> ejercicioIds,
            @RequestParam("series") List<Integer> listaSeries,
            @RequestParam("repeticiones") List<Integer> listaRepeticiones,
            @RequestParam("peso") List<String> listaPesos,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Rutina rutinaBase = rutinaService.findById(idRutinaBase).orElse(null);
        if (rutinaBase == null) {
            return "redirect:/rutinas";
        }

        Rutina rutinaDestino;
        String emailAtleta = usuarioLogueado.getEmail();
        String nombreBase = rutinaBase.getNombre().replaceAll(" - .*", "");
        String nombreUnicoBD = nombreBase + " - " + emailAtleta;

        // Lógica de actualización (si es dueño, actualiza; si es global, clona)
        if (emailAtleta.equalsIgnoreCase(rutinaBase.getUsuarioEmail())) {
            rutinaDestino = rutinaBase;
            rutinaDestino.setNombre(nombreUnicoBD);
            rutinaDestino = rutinaService.save(rutinaDestino);
        } else {
            Rutina copiaExistente = rutinaService.findAll().stream()
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
                rutinaDestino = rutinaService.save(rutinaDestino);
            }
        }

        List<Ejercicio> ejerciciosActuales = ejercicioService.findByRutinaId(rutinaDestino.getId());

        if (ejerciciosActuales.isEmpty()) {
            List<Ejercicio> nuevos = new ArrayList<>();
            for (int i = 0; i < ejercicioIds.size(); i++) {
                Ejercicio ej = new Ejercicio();
                ej.setNombre(ejercicioService.findById(ejercicioIds.get(i)).map(Ejercicio::getNombre).orElse("Ejercicio"));
                ej.setSeries(listaSeries.get(i));
                ej.setRepeticiones(listaRepeticiones.get(i));
                ej.setPesoSugerido(listaPesos.get(i).contains("kg") ? listaPesos.get(i) : listaPesos.get(i) + " kg");
                ej.setRutina(rutinaDestino);
                nuevos.add(ej);
            }
            ejercicioService.saveAll(nuevos);
        } else {
            for (int i = 0; i < listaSeries.size() && i < ejerciciosActuales.size(); i++) {
                Ejercicio ej = ejerciciosActuales.get(i);
                ej.setSeries(listaSeries.get(i));
                ej.setRepeticiones(listaRepeticiones.get(i));
                ej.setPesoSugerido(listaPesos.get(i).contains("kg") ? listaPesos.get(i) : listaPesos.get(i) + " kg");
                ejercicioService.save(ej);
            }
        }

        String tab = "espalda";
        String nombreMin = nombreBase.toLowerCase();
        String grupoMin = rutinaBase.getGrupoMuscular() != null ? rutinaBase.getGrupoMuscular().toLowerCase() : "";
        if (nombreMin.contains("espalda")) {
            tab = "espalda";
        } else if (nombreMin.contains("pecho")) {
            tab = "pecho";
        } else if (grupoMin.contains("inferior") || nombreMin.contains("pierna")) {
            tab = "pierna";
        } else if (nombreMin.contains("fuerza") || nombreMin.contains("olímpico") || nombreMin.contains("pura")) {
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
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Rutina r = rutinaService.findById(idRutina).orElse(null);
        if (r == null) {
            return "redirect:/rutinas";
        }

        boolean isAdmin = "admin@gym.com".equalsIgnoreCase(usuarioLogueado.getEmail());
        if (isAdmin || usuarioLogueado.getEmail().equalsIgnoreCase(r.getUsuarioEmail())) {
            rutinaService.deleteById(idRutina);
        }

        String redirect = "redirect:/rutinas";
        String params = "";
        if (tab != null && !tab.isBlank()) {
            params += "tab=" + java.net.URLEncoder.encode(tab, java.nio.charset.StandardCharsets.UTF_8);
        }
        if (objetivoFiltro != null && !objetivoFiltro.isBlank()) {
            params += (params.isEmpty() ? "" : "&") + "objetivoFiltro=" + java.net.URLEncoder.encode(objetivoFiltro, java.nio.charset.StandardCharsets.UTF_8);
        }
        if (!params.isEmpty()) {
            redirect += "?" + params;
        }
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

        Rutina rutina = rutinaService.findById(idRutina).orElse(null);
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

        Rutina rutina = rutinaService.findById(idRutina).orElse(null);
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
        ejercicioService.save(nuevoEjercicio);

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

        String baseNombre = nombre;
        boolean exists = rutinaService.existsByNombre(baseNombre);
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
        nueva.setUsuarioEmail(null);
        nueva = rutinaService.save(nueva);

        return "redirect:/rutina/agregar-ejercicio?id=" + nueva.getId() + "&tab=" + tab + "&objetivoFiltro=" + URLEncoder.encode(objetivoAsociado, StandardCharsets.UTF_8);
    }

    @PostMapping("/rutina/finalizar")
    public String finalizarRutina(
            @RequestParam("id") Long idRutina,
            @RequestParam("nombre") String nombreRutina,
            HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        Rutina rutina = rutinaService.findById(idRutina).orElse(null);

        if (rutina == null) {
            return "redirect:/rutinas";
        }

        boolean rutinaGlobal = rutina.getUsuarioEmail() == null;
        boolean rutinaDelUsuario = usuarioLogueado.getEmail().equalsIgnoreCase(rutina.getUsuarioEmail());

        if (!rutinaGlobal && !rutinaDelUsuario) {
            return "redirect:/rutinas";
        }

        int duracionMinutos = extraerDuracion(rutina.getTiempo());
        int caloriasQuemadas = calcularCaloriasQuemadas(duracionMinutos);

        HistorialEntrenamiento historial = new HistorialEntrenamiento(
                usuarioLogueado.getEmail(),
                nombreRutina,
                caloriasQuemadas,
                duracionMinutos
        );

        historialService.save(historial);

        return "redirect:/dashboard";
    }

    private int extraerDuracion(String tiempo) {
        if (tiempo == null || tiempo.isBlank()) {
            return 45;
        }

        String soloNumeros = tiempo.replaceAll("[^0-9]", "");

        if (soloNumeros.isBlank()) {
            return 45;
        }

        return Integer.parseInt(soloNumeros);
    }

    private int calcularCaloriasQuemadas(int duracionMinutos) {
        return duracionMinutos * 8;
    }
}
