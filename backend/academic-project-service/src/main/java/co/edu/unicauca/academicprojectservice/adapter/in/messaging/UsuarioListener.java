package co.edu.unicauca.academicprojectservice.adapter.in.messaging;

import co.edu.unicauca.academicprojectservice.port.in.messaging.UsuarioSyncUseCase;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class UsuarioListener {

    private final UsuarioSyncUseCase usuarioSyncUseCase;

    public UsuarioListener(UsuarioSyncUseCase usuarioSyncUseCase) {
        this.usuarioSyncUseCase = usuarioSyncUseCase;
    }

    @RabbitListener(queues = "${messaging.queues.projectAuth}")
    @Transactional
    public void onUserCreated(UserCreatedEvent evt) {
        try {
            if (evt == null) {
                return;
            }
            usuarioSyncUseCase.sincronizarUsuario(evt);
        } catch (Exception ex) {
            throw new AmqpRejectAndDontRequeueException("Error procesando UserCreatedEvent", ex);
        }
    }
}
