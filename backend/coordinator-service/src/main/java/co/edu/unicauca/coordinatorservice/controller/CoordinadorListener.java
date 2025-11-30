package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.coordinatorservice.repository.CoordinadorRepository;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoordinadorListener {

    private final CoordinadorRepository coordinadorRepository;

    public CoordinadorListener(CoordinadorRepository coordinadorRepository) {
        this.coordinadorRepository = coordinadorRepository;
    }

    /**
     * Escucha eventos de creación de usuario desde auth-service.
     * Filtra solo los usuarios con rol COORDINADOR y los sincroniza localmente.
     */
    @RabbitListener(queues = "${messaging.queues.coordinatorAuth}")
    @Transactional
    public void recibirCoordinador(UserCreatedEvent dto) {
        if (dto == null || dto.roles() == null) return;

        boolean esCoordinador = dto.roles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("COORDINADOR"));

        if (!esCoordinador) {
            return; // ignorar usuarios que no sean coordinadores
        }

        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.nombre());

        Optional<Coordinador> existente = coordinadorRepository.findByCorreo(dto.email());
        Coordinador coordinador = existente.orElseGet(Coordinador::new);

        // Si es nuevo, copia el UUID que viene del auth-service
        if (coordinador.getId() == null) {
            coordinador.setId(dto.id());
        }

        coordinador.setNombres(dto.nombre());
        coordinador.setCorreo(dto.email());
        coordinador.setPrograma(dto.programa()); // ya es enum Programa compartido

        coordinadorRepository.save(coordinador);

        System.out.println("[CoordinatorService] ✅ Coordinador guardado/actualizado correctamente: "
                + coordinador.getNombres());
    }
}
