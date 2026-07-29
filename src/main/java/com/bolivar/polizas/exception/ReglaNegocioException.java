package com.bolivar.polizas.exception;

// Excepción genérica para violaciones de reglas de negocio (400 Bad Request)
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
