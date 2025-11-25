package co.edu.unicauca.academicprojectservice.infrastructure.adapters.input.messaging;

import co.edu.unicauca.academicprojectservice.application.port.output.messaging.MessagingPort;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.infrastructure.adapters.output.persistence.entity.Estudiante;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MessagingAdapter implements MessagingPort {
    @Autowired
    private RabbitTemplate  rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.projectCreated}")
    private String routingKeyProjectCreated;

    public void publicarMensajeRMQ(Proyecto proyectoCreado) {
        UUID proyectoId = proyectoCreado.getId();
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyectoId);
        pDtoSend.setTitulo(proyectoCreado.getTitulo());
        pDtoSend.setTipoProyecto(proyectoCreado.getTipoProyecto());
        pDtoSend.setEstado(proyectoCreado.getEstadoProyecto());

        List<EstudianteDTOSend> estudiantes = new ArrayList<>();

        List<EstudianteId> idsEstudiantes = proyectoCreado.getEstudiantesId();

        for (EstudianteId estId : idsEstudiantes) {
            Estudiante estudiante = dbPort.obtenerEstudiantePorId(estId.value());

            EstudianteDTOSend estDto = new EstudianteDTOSend();
            estDto.setId(estudiante.getId());
            estDto.setNombres(estudiante.getNombres());
            estDto.setApellidos(estudiante.getApellidos());
            estDto.setEmail(estudiante.getCorreo());
            estDto.setCelular(estudiante.getCelular());
            estDto.setPrograma(estudiante.getPrograma());

            estudiantes.add(estDto);
        }

        pDtoSend.setEstudiantes(estudiantes);

        DocenteDTOSend docDto = new DocenteDTOSend();

        Docente docente = dbPort.obtenerDocentePorId(proyectoCreado.getDirectorId().value());

        docDto.setId(docente.getId());
        docDto.setDepartamento(docente.getDepartamento());
        docDto.setEmail(docente.getCorreo());
        docDto.setNombres(docente.getNombres());
        docDto.setApellidos(docente.getApellidos());
        docDto.setCelular(docente.getCelular());

        pDtoSend.setDirector(docDto);
        pDtoSend.setCodirector(null);

        FormatoA formatoA = proyectoCreado.getUltimoFormatoA();
        FormatoADTOSend formatoSend = new FormatoADTOSend();
        formatoSend.setId(formatoA.getId());
        formatoSend.setProyectoId(proyectoId);
        formatoSend.setNroVersion(formatoA.getNroVersion());
        formatoSend.setNombreFormatoA(formatoA.getNombreFormato());
        formatoSend.setFechaSubida(formatoA.getFechaCreacion());
        formatoSend.setBlob(formatoA.getBlob());
        formatoSend.setEstado(formatoA.getEstado());
        pDtoSend.setFormatoA(formatoSend);

        if (proyectoCreado.getCartaLaboral() != null) {
            pDtoSend.setCartaLaboral(proyectoCreado.getCartaLaboral());
        }

        pDtoSend.setAnteproyecto(null);

        rabbitTemplate.convertAndSend(mainExchange, routingKeyProjectCreated, pDtoSend);

        log.info("[RabbitMQ] Proyecto creado enviado a la cola: {} con ID: {}", routingKeyProjectCreated, proyectoId);
    }
}
