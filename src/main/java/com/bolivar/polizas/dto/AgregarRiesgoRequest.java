package com.bolivar.polizas.dto;

import jakarta.validation.constraints.NotBlank;

public class AgregarRiesgoRequest {

    @NotBlank(message = "La descripción del riesgo es obligatoria")
    private String descripcion;

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
