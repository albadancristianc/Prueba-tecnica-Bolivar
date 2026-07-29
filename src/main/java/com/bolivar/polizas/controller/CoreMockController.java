package com.bolivar.polizas.controller;

import com.bolivar.polizas.dto.CoreEventoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    // Simula el servicio agnóstico de edición del CORE legado (WebLogic).
    // Único propósito: registrar en logs que la operación se intentó enviar al CORE.
    @PostMapping("/core-mock/evento")
    public ResponseEntity<Void> recibirEvento(@RequestBody CoreEventoRequest request) {
        log.info("[CORE-MOCK] Evento recibido -> tipo={}, polizaId={}", request.getEvento(), request.getPolizaId());
        return ResponseEntity.ok().build();
    }
}
