package com.bolivar.polizas.exception;

public class RiesgoNotFoundException extends RuntimeException {
    public RiesgoNotFoundException(Long id) {
        super("No se encontró el riesgo con id: " + id);
    }
}
