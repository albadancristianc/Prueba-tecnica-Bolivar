package com.bolivar.polizas.service;

import com.bolivar.polizas.exception.PolizaNotFoundException;
import com.bolivar.polizas.exception.ReglaNegocioException;
import com.bolivar.polizas.exception.RiesgoNotFoundException;
import com.bolivar.polizas.model.*;
import com.bolivar.polizas.repository.PolizaRepository;
import com.bolivar.polizas.repository.RiesgoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreAdapterService coreAdapterService;

    @Value("${poliza.ipc}")
    private BigDecimal ipc;

    public PolizaService(PolizaRepository polizaRepository,
                          RiesgoRepository riesgoRepository,
                          CoreAdapterService coreAdapterService) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreAdapterService = coreAdapterService;
    }

    // GET /polizas?tipo=&estado=  -- ambos filtros son opcionales
    @Transactional(readOnly = true)
    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        List<Poliza> polizas;
        if (tipo != null && estado != null) {
            polizas = polizaRepository.findByTipoAndEstado(tipo, estado);
        } else if (tipo != null) {
            polizas = polizaRepository.findByTipo(tipo);
        } else if (estado != null) {
            polizas = polizaRepository.findByEstado(estado);
        } else {
            polizas = polizaRepository.findAll();
        }
        // Se fuerza la inicialización de la colección lazy "riesgos" mientras
        // la transacción sigue abierta, para que Jackson pueda serializarla
        // después sin lanzar LazyInitializationException.
        polizas.forEach(p -> p.getRiesgos().size());
        return polizas;
    }

    @Transactional(readOnly = true)
    public List<Riesgo> listarRiesgos(Long polizaId) {
        obtenerPoliza(polizaId); // valida que la póliza exista
        return riesgoRepository.findByPolizaId(polizaId);
    }

    @Transactional
    public Poliza renovar(Long polizaId) {
        Poliza poliza = obtenerPoliza(polizaId);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException(
                "No se puede renovar una póliza cancelada (id=" + polizaId + ")");
        }

        BigDecimal factor = BigDecimal.ONE.add(ipc);
        BigDecimal nuevoCanon = poliza.getCanonMensual().multiply(factor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nuevaPrima = poliza.getPrima().multiply(factor).setScale(2, RoundingMode.HALF_UP);

        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        poliza.setEstado(EstadoPoliza.RENOVADA);

        Poliza guardada = polizaRepository.save(poliza);
        guardada.getRiesgos().size(); // evita LazyInitializationException al serializar
        coreAdapterService.notificarEvento("RENOVACION", polizaId);
        return guardada;
    }

    @Transactional
    public Poliza cancelar(Long polizaId) {
        Poliza poliza = obtenerPoliza(polizaId);

        poliza.setEstado(EstadoPoliza.CANCELADA);
        for (Riesgo riesgo : poliza.getRiesgos()) {
            if (riesgo.getEstado() == EstadoRiesgo.ACTIVO) {
                riesgo.setEstado(EstadoRiesgo.CANCELADO);
            }
        }

        Poliza guardada = polizaRepository.save(poliza);
        coreAdapterService.notificarEvento("CANCELACION_POLIZA", polizaId);
        return guardada;
    }

    @Transactional
    public Riesgo agregarRiesgo(Long polizaId, String descripcion) {
        Poliza poliza = obtenerPoliza(polizaId);

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new ReglaNegocioException(
                "Solo las pólizas Colectivas admiten agregar riesgos (poliza id=" + polizaId + ")");
        }

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException(
                "No se puede agregar un riesgo a una póliza cancelada (id=" + polizaId + ")");
        }

        Riesgo riesgo = new Riesgo();
        riesgo.setPoliza(poliza);
        riesgo.setDescripcion(descripcion);
        riesgo.setEstado(EstadoRiesgo.ACTIVO);

        Riesgo guardado = riesgoRepository.save(riesgo);
        coreAdapterService.notificarEvento("RIESGO_AGREGADO", polizaId);
        return guardado;
    }

    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
            .orElseThrow(() -> new RiesgoNotFoundException(riesgoId));

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        Riesgo guardado = riesgoRepository.save(riesgo);
        coreAdapterService.notificarEvento("RIESGO_CANCELADO", riesgo.getPoliza().getId());
        return guardado;
    }

    private Poliza obtenerPoliza(Long id) {
        return polizaRepository.findById(id)
            .orElseThrow(() -> new PolizaNotFoundException(id));
    }
}
