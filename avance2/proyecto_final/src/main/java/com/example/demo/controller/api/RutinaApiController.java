package com.example.demo.controller.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Ejercicio;
import com.example.demo.model.Rutina;
import com.example.demo.service.EjercicioService;
import com.example.demo.service.RutinaService;

@RestController
@RequestMapping("/api")
public class RutinaApiController {

    private final RutinaService rutinaService;
    private final EjercicioService ejercicioService;

    public RutinaApiController(RutinaService rutinaService, EjercicioService ejercicioService) {
        this.rutinaService = rutinaService;
        this.ejercicioService = ejercicioService;
    }

    @GetMapping("/rutinas")
    public ResponseEntity<?> listarRutinas() {
        List<Rutina> rutinas = rutinaService.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Rutina r : rutinas) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("nombre", r.getNombre());
            item.put("nivel", r.getNivel());
            item.put("tiempo", r.getTiempo());
            item.put("grupoMuscular", r.getGrupoMuscular());
            item.put("objetivoAsociado", r.getObjetivoAsociado());
            resultado.add(item);
        }
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/rutinas/{id}/ejercicios")
    public ResponseEntity<?> getEjercicios(@PathVariable Long id) {
        var rutinaOpt = rutinaService.findById(id);
        if (rutinaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Ejercicio> ejercicios = ejercicioService.findByRutinaId(id);
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Ejercicio e : ejercicios) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", e.getId());
            item.put("nombre", e.getNombre());
            item.put("series", e.getSeries());
            item.put("repeticiones", e.getRepeticiones());
            item.put("pesoSugerido", e.getPesoSugerido());
            resultado.add(item);
        }
        return ResponseEntity.ok(resultado);
    }

    private boolean esAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping("/rutinas")
    public ResponseEntity<?> crearRutina(@RequestBody Map<String, Object> body,
                                          Authentication auth) {
        if (!esAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        String nombre = (String) body.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre es obligatorio"));
        }

        Rutina rutina = new Rutina();
        rutina.setNombre(nombre);
        rutina.setNivel((String) body.get("nivel"));
        rutina.setTiempo((String) body.get("tiempo"));
        rutina.setGrupoMuscular((String) body.get("grupoMuscular"));
        rutina.setObjetivoAsociado((String) body.get("objetivoAsociado"));

        Rutina saved = rutinaService.save(rutina);
        return ResponseEntity.ok(Map.of(
            "id", saved.getId(),
            "nombre", saved.getNombre(),
            "mensaje", "Rutina creada correctamente"
        ));
    }

    @PutMapping("/rutinas/{id}")
    public ResponseEntity<?> actualizarRutina(@PathVariable Long id,
                                               @RequestBody Map<String, Object> body,
                                               Authentication auth) {
        if (!esAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        var rutinaOpt = rutinaService.findById(id);
        if (rutinaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Rutina rutina = rutinaOpt.get();

        if (body.containsKey("nombre")) {
            rutina.setNombre((String) body.get("nombre"));
        }
        if (body.containsKey("nivel")) {
            rutina.setNivel((String) body.get("nivel"));
        }
        if (body.containsKey("tiempo")) {
            rutina.setTiempo((String) body.get("tiempo"));
        }
        if (body.containsKey("grupoMuscular")) {
            rutina.setGrupoMuscular((String) body.get("grupoMuscular"));
        }
        if (body.containsKey("objetivoAsociado")) {
            rutina.setObjetivoAsociado((String) body.get("objetivoAsociado"));
        }

        Rutina saved = rutinaService.save(rutina);
        return ResponseEntity.ok(Map.of(
            "id", saved.getId(),
            "nombre", saved.getNombre(),
            "mensaje", "Rutina actualizada correctamente"
        ));
    }

    @DeleteMapping("/rutinas/{id}")
    public ResponseEntity<?> eliminarRutina(@PathVariable Long id,
                                            Authentication auth) {
        if (!esAdmin(auth)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        var rutinaOpt = rutinaService.findById(id);
        if (rutinaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        rutinaService.deleteById(id);
        return ResponseEntity.ok(Map.of("mensaje", "Rutina eliminada correctamente"));
    }
}