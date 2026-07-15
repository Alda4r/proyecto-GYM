package com.example.demo.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PlanExpirationService {

    private static final Logger log = LoggerFactory.getLogger(PlanExpirationService.class);
    private final UsuarioService usuarioService;

    public PlanExpirationService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Scheduled(cron = "0 0 6 * * ?")
    public void desactivarPlanesVencidos() {
        var usuarios = usuarioService.findAll();
        var hoy = LocalDate.now();
        for (var u : usuarios) {
            if ("Activo".equalsIgnoreCase(u.getMembresia())
                    && u.getFechaFinPlan() != null
                    && u.getFechaFinPlan().isBefore(hoy)) {
                u.setMembresia("Inactivo");
                u.setPlanMembresia(null);
                u.setFechaInicioPlan(null);
                u.setFechaFinPlan(null);
                usuarioService.save(u);
                log.info("Plan vencido desactivado para usuario: {}", u.getEmail());
            }
        }
    }
}
