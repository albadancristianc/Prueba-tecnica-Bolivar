package com.bolivar.polizas.exception;

public class PolizaNotFoundException extends RuntimeException {
    public PolizaNotFoundException(Long id) {
        super("No se encontró la póliza con id: " + id);
    }
}
