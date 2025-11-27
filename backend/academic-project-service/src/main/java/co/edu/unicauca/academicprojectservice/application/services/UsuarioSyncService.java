package co.edu.unicauca.academicprojectservice.application.services;


import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.port.in.messaging.UsuarioSyncUseCase;
import co.edu.unicauca.shared.contracts.events.auth.UserCreatedEvent;
import co.edu.unicauca.shared.contracts.model.Rol;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioSyncService implements UsuarioSyncUseCase {

    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;

    public UsuarioSyncService(DocenteRepository docenteRepository,
                              EstudianteRepository estudianteRepository) {
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    public void sincronizarUsuario(UserCreatedEvent evt) {
        final String email = evt.email() == null ? null : evt.email().trim().toLowerCase();
        if (email == null || email.isBlank()) return;

        final List<Rol> roles = evt.roles();
        if (roles == null || roles.isEmpty()) return;

        String nombres = evt.nombre();
        String apellidos = evt.apellido();
        if (nombres != null) {
            int idx = nombres.lastIndexOf(' ');
            if (idx > 0) {
                apellidos = nombres.substring(idx + 1).trim();
                nombres = nombres.substring(0, idx).trim();
            }
        }

        for (Rol r : roles) {
            switch (r) {
                case DOCENTE -> procesarDocente(email, nombres, apellidos, evt);
                case ESTUDIANTE -> procesarEstudiante(email, nombres, apellidos, evt);
                default -> {
                }
            }
        }
    }

    private void procesarDocente(String email, String nombres, String apellidos, UserCreatedEvent evt) {
        Optional<Docente> existente = docenteRepository.findByCorreo(email);
        Docente d = existente.orElse(new Docente());
        d.setCorreo(email);
        d.setNombres(nombres);
        d.setApellidos(apellidos);
        d.setDepartamento(evt.departamento());
        docenteRepository.save(d);
    }

    private void procesarEstudiante(String email, String nombres, String apellidos, UserCreatedEvent evt) {
        Optional<Estudiante> existente = estudianteRepository.findByCorreoIgnoreCase(email);
        Estudiante e = existente.orElse(new Estudiante());
        e.setCorreo(email);
        e.setNombres(nombres);
        e.setApellidos(apellidos);
        e.setPrograma(evt.programa());
        estudianteRepository.save(e);
    }
}