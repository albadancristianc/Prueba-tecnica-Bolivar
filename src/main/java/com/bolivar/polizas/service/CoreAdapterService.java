package com.bolivar.polizas.service;

import com.bolivar.polizas.dto.CoreEventoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CoreAdapterService {

    private static final Logger log = LoggerFactory.getLogger(CoreAdapterService.class);

    private final RestTemplate restTemplate;

    @Value("${core.mock.url:http://localhost:8080/core-mock/evento}")
    private String coreMockUrl;

    @Value("${api.security.key}")
    private String apiKey;

    public CoreAdapterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Adapter / Anti-Corruption Layer: traduce la notificación de dominio
    // hacia el contrato que expone el CORE legado (WebLogic).
    // No debe interrumpir la operación de negocio si el CORE falla.
    public void notificarEvento(String evento, Long polizaId) {
        try {
            CoreEventoRequest request = new CoreEventoRequest(evento, polizaId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);
            HttpEntity<CoreEventoRequest> entity = new HttpEntity<>(request, headers);
            restTemplate.postForEntity(coreMockUrl, entity, Void.class);
        } catch (Exception e) {
            // No relanzamos la excepción: la actualización del CORE no puede
            // tumbar la transacción de negocio principal. Se deja registrado
            // para que un mecanismo de reintentos/monitoreo lo detecte.
            log.error("No se pudo notificar al CORE para poliza {}: {}", polizaId, e.getMessage());
        }
    }
}