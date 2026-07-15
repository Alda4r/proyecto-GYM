package com.example.demo.controller.api;

// ============================================================
// API REST — devuelve JSON en vez de HTML
// ============================================================
// Diferencia con @Controller:
//   @Controller       → devuelve nombres de plantillas (Thymeleaf .html)
//   @RestController   → devuelve datos directamente (JSON)
//
// @RestController = @Controller + @ResponseBody en cada método
// Spring convierte automáticamente el objeto que devuelves a JSON
// usando Jackson (viene incluido en Spring Boot Web).
//
// Por convención, las rutas API empiezan con /api/
// ============================================================

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Ejercicio;
import com.example.demo.service.EjercicioService;
import com.example.demo.service.RutinaService;

/**
 * Controlador REST para consultas de rutinas.
 *
 * @RestController indica que los métodos devuelven JSON automáticamente.
 * @RequestMapping("/api") define la ruta base para todos los endpoints
 * de esta clase, así no repetimos "/api" en cada @GetMapping.
 */
@RestController
@RequestMapping("/api")
public class RutinaApiController {

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private EjercicioService ejercicioService;

    /**
     * GET /api/rutinas/{id}/ejercicios
     *
     * Devuelve la lista de ejercicios de una rutina en formato JSON.
     * {id} es un "path variable" — se extrae de la URL.
     *
     * ResponseEntity<?> nos permite controlar el código HTTP de respuesta
     * (200 OK, 404 Not Found, etc.) y el cuerpo del mensaje.
     *
     * Spring convierte el Map/List a JSON automáticamente.
     */
    @GetMapping("/rutinas/{id}/ejercicios")
    public ResponseEntity<?> getEjercicios(@PathVariable Long id) {
        /*
         * @PathVariable extrae el valor de {id} de la URL.
         * Si la URL es /api/rutinas/5/ejercicios, id = 5.
         */

        // Verificar que la rutina existe
        var rutinaOpt = rutinaService.findById(id);
        if (rutinaOpt.isEmpty()) {
            /*
             * ofNotFound() → HTTP 404 con un mensaje JSON.
             * El Map se convierte a: {"error": "Rutina no encontrada", "id": 5}
             */
            return ResponseEntity.notFound().build();
        }

        // Obtener la lista de ejercicios desde la base de datos
        List<Ejercicio> ejercicios = ejercicioService.findByRutinaId(id);

        /*
         * Convertimos los objetos Ejercicio (entidad JPA) a Map simples
         * para enviar solo los datos que el frontend necesita.
         *
         * Podríamos devolver los objetos Ejercicio directamente y Jackson
         * los convertiría a JSON, pero es mejor controlar qué se envía
         * para evitar:
         *   - Bucles infinitos (Ejercicio -> Rutina -> Ejercicios...)
         *   - Exponer datos internos
         *   - Enviar campos que el frontend no usa
         */
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Ejercicio e : ejercicios) {
            /*
             * Construimos un Map por cada ejercicio.
             * Map<String, Object> → { "clave": valor }
             * Jackson lo convierte a: {"nombre": "Press banca", "series": 4, ...}
             */
            Map<String, Object> item = new HashMap<>();
            item.put("id", e.getId());
            item.put("nombre", e.getNombre());
            item.put("series", e.getSeries());
            item.put("repeticiones", e.getRepeticiones());
            item.put("pesoSugerido", e.getPesoSugerido());
            resultado.add(item);
        }

        /*
         * ResponseEntity.ok(cuerpo) → HTTP 200 OK
         * Spring serializa el List<Map> a un array JSON.
         *
         * Respuesta ejemplo:
         * [
         *   {"id":1, "nombre":"Press banca","series":4,"repeticiones":10,"pesoSugerido":"60 kg"},
         *   {"id":2, "nombre":"Aperturas","series":3,"repeticiones":12,"pesoSugerido":"20 kg"}
         * ]
         */
        return ResponseEntity.ok(resultado);
    }
}
