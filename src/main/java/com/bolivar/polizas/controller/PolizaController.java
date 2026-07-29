package com.bolivar.polizas.controller;

import com.bolivar.polizas.dto.AgregarRiesgoRequest;
import com.bolivar.polizas.model.EstadoPoliza;
import com.bolivar.polizas.model.Poliza;
import com.bolivar.polizas.model.Riesgo;
import com.bolivar.polizas.model.TipoPoliza;
import com.bolivar.polizas.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    public List<Poliza> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return polizaService.listar(tipo, estado);
    }

    @GetMapping("/{id}/riesgos")
    public List<Riesgo> listarRiesgos(@PathVariable Long id) {
        return polizaService.listarRiesgos(id);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<Poliza> renovar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.renovar(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Poliza> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(polizaService.cancelar(id));
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<Riesgo> agregarRiesgo(
            @PathVariable Long id,
            @Valid @RequestBody AgregarRiesgoRequest request) {
        Riesgo riesgo = polizaService.agregarRiesgo(id, request.getDescripcion());
        return ResponseEntity.status(201).body(riesgo);
    }
}
