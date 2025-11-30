package co.edu.unicauca.academicprojectservice.port.in.messaging;

import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;

public interface UsuarioSyncUseCase {
    void sincronizarUsuario(UserCreatedEvent event);
}