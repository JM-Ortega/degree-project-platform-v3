package co.edu.unicauca.departmentheadservice.infra.messaging;

import co.edu.unicauca.shared.contracts.events.departmenthead.AnteproyectoConEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.messaging.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Publica eventos de dominio relacionados con Anteproyectos con evaluadores asignados en RabbitMQ.
 *
 * <p>Este componente envía mensajes al exchange principal cuando ocurre un
 * evento relevante dentro del microservicio de Departamento, como la asignación de evaluadores a un Anteproyecto.</p>
 *
 * <p>Utiliza las claves de enrutamiento definidas en {@link RoutingKeys} y
 * los contratos compartidos del módulo <code>shared-contracts</code>.</p>
 */
@Component
public class DepartmentHeadEventsPublisher {

    private static final Logger log = LoggerFactory.getLogger(DepartmentHeadEventsPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    // Inyección del nombre del exchange desde el archivo application.yml
    @Value("${messaging.exchange.main}")
    private String exchangeName;

    /**
     * Inyección por constructor: garantiza inmutabilidad y facilita pruebas unitarias.
     *
     * @param rabbitTemplate plantilla de RabbitMQ para publicar mensajes.
     */
    public DepartmentHeadEventsPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica un evento de anteproyecto con evaluadores asignados en el exchange principal.
     *
     * @param event evento {@link AnteproyectoConEvaluadoresEvent} con los datos del Anteproyecto y evaluadores asignados.
     */
    public void publishAnteproyectoConEvaluadoresEvent(AnteproyectoConEvaluadoresEvent event) {
        try {
            // Publicamos el evento utilizando el nombre del exchange y la clave de enrutamiento adecuada
            rabbitTemplate.convertAndSend(exchangeName, RoutingKeys.DEPARTMENT_PROPOSAL_APPROVED, event);
            log.info("Evento de Anteproyecto con Evaluadores publicado: {} -> {}", RoutingKeys.DEPARTMENT_PROPOSAL_APPROVED, event);
        } catch (Exception ex) {
            log.error("Error al publicar evento {}: {}", RoutingKeys.DEPARTMENT_PROPOSAL_APPROVED, ex.getMessage(), ex);
        }
    }
}
