package com.bolivar.polizas.repository;

import com.bolivar.polizas.model.EstadoPoliza;
import com.bolivar.polizas.model.Poliza;
import com.bolivar.polizas.model.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    // GET /polizas?tipo=X&estado=Y  -- ambos filtros son opcionales
    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);

    List<Poliza> findByTipo(TipoPoliza tipo);

    List<Poliza> findByEstado(EstadoPoliza estado);
}
