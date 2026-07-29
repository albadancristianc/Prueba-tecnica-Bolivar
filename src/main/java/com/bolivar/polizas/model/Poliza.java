package com.bolivar.polizas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
@Getter
@Setter
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPoliza estado = EstadoPoliza.ACTIVA;

    @Column(nullable = false)
    private LocalDate fechaInicioVigencia;

    @Column(nullable = false)
    private LocalDate fechaFinVigencia;

    @Column(nullable = false)
    private BigDecimal canonMensual;

    @Column(nullable = false)
    private BigDecimal prima;

    @Column(nullable = false)
    private Integer mesesVigencia;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Riesgo> riesgos = new ArrayList<>();
}
