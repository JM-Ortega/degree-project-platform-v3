package co.edu.unicauca.academicprojectservice.adapter.in.messaging;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.domain.model.Anteproyecto;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.port.out.messaging.MessagingPort;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortProyecto;
import co.edu.unicauca.shared.contracts.events.academic.AnteproyectoSinEvaluadoresEvent;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.DocenteDTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.EstudianteDTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.ProyectoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static co.edu.unicauca.shared.contracts.messaging.RoutingKeys.*;

@Slf4j
@Service
public class MessagingAdapter implements MessagingPort {

    private final RabbitTemplate rabbitTemplate;
    private final DbPortProyecto dbPortProyecto;
    private final String mainExchange;

    public MessagingAdapter(RabbitTemplate rabbitTemplate,
                            DbPortProyecto dbPortProyecto,
                            @Value("${messaging.exchange.main}") String mainExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.dbPortProyecto = dbPortProyecto;
        this.mainExchange = mainExchange;
    }

    @Override
    public void publicarProyectoCreado(Proyecto proyectoCreado) {
        UUID proyectoId = proyectoCreado.getId();

        ProyectoDTO pDtoSend = new ProyectoDTO();
        pDtoSend.setId(proyectoId);
        pDtoSend.setTitulo(proyectoCreado.getTitulo());
        pDtoSend.setTipoProyecto(proyectoCreado.getTipoProyecto());
        pDtoSend.setEstado(proyectoCreado.getEstadoProyecto());

        List<EstudianteDTO> estudiantes = new ArrayList<>();
        List<EstudianteId> idsEstudiantes = proyectoCreado.getEstudiantesId();

        for (EstudianteId estId : idsEstudiantes) {
            Estudiante estudiante = dbPortProyecto.obtenerEstudiantePorId(estId.value());

            EstudianteDTO estDto = new EstudianteDTO();
            estDto.setId(estudiante.getId());
            estDto.setNombres(estudiante.getNombres());
            estDto.setApellidos(estudiante.getApellidos());
            estDto.setEmail(estudiante.getCorreo());
            estDto.setCelular(estudiante.getCelular());
            estDto.setPrograma(estudiante.getPrograma());

            estudiantes.add(estDto);
        }

        pDtoSend.setEstudiantes(estudiantes);

        Docente docente = dbPortProyecto.obtenerDocenteInfoPorId(proyectoCreado.getDirectorId().value());

        DocenteDTO docDto = new DocenteDTO();
        docDto.setId(docente.getId());
        docDto.setDepartamento(docente.getDepartamento());
        docDto.setEmail(docente.getCorreo());
        docDto.setNombres(docente.getNombres());
        docDto.setApellidos(docente.getApellidos());
        docDto.setCelular(docente.getCelular());

        pDtoSend.setDirector(docDto);
        pDtoSend.setCodirector(null);

        FormatoA formatoA = dbPortProyecto.obtenerUltimoFormatoA(proyectoId);
        if (formatoA != null) {
            FormatoADTO formatoSend = new FormatoADTO();
            formatoSend.setId(formatoA.getId());
            formatoSend.setProyectoId(proyectoId);
            formatoSend.setNroVersion(formatoA.getNroVersion());
            formatoSend.setNombreFormatoA(formatoA.getNombreFormato());
            formatoSend.setFechaSubida(formatoA.getFechaCreacion());
            formatoSend.setBlob(formatoA.getBlob());
            formatoSend.setEstado(formatoA.getEstado());
            pDtoSend.setFormatoA(formatoSend);
        }

        if (proyectoCreado.getCartaLaboral() != null) {
            pDtoSend.setCartaLaboral(proyectoCreado.getCartaLaboral());
        }

        pDtoSend.setAnteproyecto(null);

        rabbitTemplate.convertAndSend(mainExchange, PROJECT_CREATED, pDtoSend);

        log.info("[RabbitMQ] Proyecto creado enviado. rk={} id={}", PROJECT_CREATED, proyectoId);
    }

    @Override
    public void publicarAnteproyectoSinEvaluadores(Proyecto proyecto) {
        Anteproyecto ante = proyecto.getAnteproyecto();
        if (ante == null) {
            log.warn("[RabbitMQ] No se pudo publicar AnteproyectoSinEvaluadoresEvent: proyecto {} sin anteproyecto",
                    proyecto.getId());
            return;
        }

        List<EstudianteId> estIds = proyecto.getEstudiantesId();
        if (estIds == null || estIds.isEmpty()) {
            log.warn("[RabbitMQ] Proyecto {} sin estudiantes, no se publica anteproyecto", proyecto.getId());
            return;
        }

        EstudianteId primerEst = estIds.getFirst();
        Estudiante estudiante = dbPortProyecto.obtenerEstudiantePorId(primerEst.value());
        Docente director = dbPortProyecto.obtenerDocenteInfoPorId(proyecto.getDirectorId().value());

        AnteproyectoSinEvaluadoresEvent event = new AnteproyectoSinEvaluadoresEvent(
                proyecto.getId(),
                ante.getId(),
                ante.getTitulo(),
                ante.getDescripcion(),
                ante.getFechaCreacion(),
                estudiante.getCorreo(),
                director.getCorreo(),
                director.getDepartamento() != null ? director.getDepartamento().name() : null
        );

        rabbitTemplate.convertAndSend(mainExchange, ACADEMIC_ANTEPROYECTO_CREATED, event);
        log.info("[RabbitMQ] AnteproyectoSinEvaluadoresEvent publicado. rk={} proyectoId={} anteId={}",
                ACADEMIC_ANTEPROYECTO_CREATED, proyecto.getId(), ante.getId());
    }

    @Override
    public void publicarFormatoActualizado(Proyecto proyectoCreado) {
        FormatoA ultimo = proyectoCreado.getFormatosA().getLast();

        FormatoADTO f = new FormatoADTO();
        f.setId(ultimo.getId());
        f.setProyectoId(proyectoCreado.getId());
        f.setNroVersion(ultimo.getNroVersion());
        f.setNombreFormatoA(ultimo.getNombreFormato());
        f.setFechaSubida(ultimo.getFechaCreacion());
        f.setBlob(ultimo.getBlob());
        f.setEstado(ultimo.getEstado());

        rabbitTemplate.convertAndSend(mainExchange, ACADEMIC_FORMATO_A_CHANGED, f);

        log.info("[RabbitMQ] Nueva version de FormatoA enviada. rk={} proyectoId={} formatoId={}",
                ACADEMIC_FORMATO_A_CHANGED, proyectoCreado.getId(), f.getId());
    }
}
