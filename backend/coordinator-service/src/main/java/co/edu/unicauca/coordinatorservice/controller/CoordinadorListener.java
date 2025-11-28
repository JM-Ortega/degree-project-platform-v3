package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Coordinador;
import co.edu.unicauca.shared.contracts.model.Programa;
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
    @RabbitListener(queues = "${messaging.queues.coordinatorAuth}") // ✅ cola dedicada
    @Transactional
    public void recibirCoordinador(UserCreatedEvent dto) {
        if (dto == null || dto.roles() == null) return;

        // ✅ Verifica si el usuario tiene el rol COORDINADOR
        boolean esCoordinador = dto.roles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("COORDINADOR"));

        if (!esCoordinador) return; // Ignora si no aplica

        System.out.println("📩 [RabbitMQ] Mensaje recibido en CoordinatorService: " + dto.nombre());

        // Buscar si ya existe
        Optional<Coordinador> existente = coordinadorRepository.findByCorreo(dto.email());
        Coordinador coordinador = existente.orElse(new Coordinador());

        // Actualizar campos
        coordinador.setNombres(dto.nombre());
        coordinador.setCorreo(dto.email());
        coordinador.setPrograma(Programa.valueOf(dto.programa().toString()));

        coordinadorRepository.save(coordinador);

        System.out.println("[CoordinatorService] ✅ Coordinador guardado/actualizado correctamente: " + coordinador.getNombres());
    }
}
