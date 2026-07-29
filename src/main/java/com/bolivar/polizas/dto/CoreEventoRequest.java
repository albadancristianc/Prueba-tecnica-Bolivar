package com.bolivar.polizas.dto;

public class CoreEventoRequest {
    private String evento;
    private Long polizaId;

    public CoreEventoRequest() {}

    public CoreEventoRequest(String evento, Long polizaId) {
        this.evento = evento;
        this.polizaId = polizaId;
    }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }
    public Long getPolizaId() { return polizaId; }
    public void setPolizaId(Long polizaId) { this.polizaId = polizaId; }
}
