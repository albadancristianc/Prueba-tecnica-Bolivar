# API de Gestión de Pólizas — Prueba Técnica Seguros Bolívar

## Descripción
API REST para la gestión de pólizas (Individuales y Colectivas) y sus riesgos asociados,
desarrollada con Spring Boot 4.1.0 y Java 17.


## Estructura del proyecto
```
com.bolivar.polizas
├── controller/     -> Expone los endpoints REST
├── service/         -> Lógica de negocio (PolizaService) y Adapter hacia el CORE (CoreAdapterService)
├── repository/      -> Acceso a datos (Spring Data JPA)
├── model/           -> Entidades (Poliza, Riesgo) y enums
├── dto/             -> Objetos de transferencia para requests
├── exception/       -> Excepciones de negocio y manejador global de errores
├── config/          -> Configuración de beans (RestTemplate)
└── security/        -> Filtro de autenticación por API Key
```

## Decisiones de diseño y supuestos
- **Persistencia:** se usa H2 en memoria en lugar de Oracle/MySQL para agilizar la
  ejecución de la prueba sin requerir infraestructura externa. La capa de repository
  (Spring Data JPA) es intercambiable a cualquier motor relacional cambiando solo la
  configuración de datasource.
- **Modelo de datos:** se maneja una única entidad `Poliza` con un campo `tipo`
  (INDIVIDUAL/COLECTIVA) en lugar de dos entidades separadas, evitando duplicar los
  atributos comunes (vigencia, canon, prima).
- **Creación de pólizas:** el enunciado no incluye un endpoint de creación de pólizas,
  por lo que se precargan datos de prueba vía `data.sql` al arrancar la aplicación.
  Como consecuencia, la regla "Individual máximo 1 riesgo" queda garantizada porque el
  único endpoint para agregar riesgos ya rechaza cualquier póliza que no sea Colectiva.
- **IPC de renovación:** se maneja como una constante de configuración
  (`poliza.ipc=0.05` en `application.properties`), ya que el enunciado lo trata como
  una regla de negocio del sistema, no como un dato que envíe el cliente.
- **Adapter hacia el CORE:** siguiendo el patrón Anti-Corruption Layer definido en el
  Módulo 1, toda acción que modifica el estado de una póliza/riesgo notifica al mock
  del CORE (`POST /core-mock/evento`) a través de `CoreAdapterService`, vía HTTP real
  (no una llamada interna de método), para reflejar el patrón de diseño propuesto.
  Un fallo de esta notificación no revierte la operación de negocio (se registra en
  logs para monitoreo/reintento posterior).

## Endpoints

Todos requieren el header: `x-api-key: 123456`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/polizas?tipo=&estado=` | Listar pólizas (ambos filtros opcionales) |
| GET | `/polizas/{id}/riesgos` | Listar riesgos de una póliza |
| POST | `/polizas/{id}/renovar` | Renovar póliza (+IPC, estado → RENOVADA) |
| POST | `/polizas/{id}/cancelar` | Cancelar póliza y sus riesgos |
| POST | `/polizas/{id}/riesgos` | Agregar riesgo (solo Colectiva) |
| POST | `/riesgos/{id}/cancelar` | Cancelar un riesgo puntual |
| POST | `/core-mock/evento` | Mock del CORE legado (uso interno del Adapter) |

## Reglas de negocio implementadas
- Una póliza Individual no admite más de 1 riesgo (solo Colectivas pueden agregar riesgos).
- No se puede renovar ni agregar riesgos a una póliza CANCELADA.
- Cancelar una póliza cancela automáticamente todos sus riesgos activos.
- La renovación incrementa canon y prima en el porcentaje de IPC configurado.

## Datos de prueba precargados (data.sql)
| ID | Tipo | Estado | Riesgos |
|----|------|--------|---------|
| 1 | INDIVIDUAL | ACTIVA | 1 |
| 2 | COLECTIVA | ACTIVA | 2 |
| 3 | INDIVIDUAL | CANCELADA | 0 |

## Cómo ejecutar
1. Clonar/descomprimir el proyecto.
2. `./mvnw spring-boot:run`
3. La API queda disponible en `http://localhost:8080`
4. Consola H2 disponible en `http://localhost:8080/h2-console`
   (JDBC URL: `jdbc:h2:mem:polizasdb`, usuario: `sa`, sin contraseña)

