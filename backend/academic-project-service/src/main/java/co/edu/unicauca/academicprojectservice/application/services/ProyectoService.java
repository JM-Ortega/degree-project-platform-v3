package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.DocenteDTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.EstudianteDTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.FormatoADTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.ProyectoDTOSend;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.ProyectoRepository;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;

    private EstudianteRepository estudianteRepository;
    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${messaging.exchange.main}")
    private String mainExchange;

    @Value("${messaging.routing.projectCreated}")
    private String routingKeyProjectCreated;












// ==========================================================

    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        Docente docente = docenteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con correo: " + correo));

        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtro);
    }


// ==========================================================






















    @Transactional
    public void crearProyectoConArchivos(ProyectoDTO dto) {
        List<String> correos = dto.getEstudiantes();

        List<Estudiante> estudiantesDTO = correos.stream()
                .map(correo -> estudianteRepository.findByCorreoIgnoreCase(correo)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No existe un estudiante con el correo: " + correo)))
                .toList();

        Estudiante estudiante1 = estudiantesDTO.getFirst();
        Estudiante estudiante2;
        if (estudiantesDTO.size() == 2) {
            estudiante2 = estudiantesDTO.get(1);
        } else {
            estudiante2 = null;
        }

        List<EstudianteId> estudiantes = new ArrayList<>();
        EstudianteId e1 = new EstudianteId(estudiante1.getId());
        estudiantes.add(e1);
        if (estudiante2 != null) {
            EstudianteId e2 = new EstudianteId(estudiante2.getId());
            estudiantes.add(e2);
        }

        Docente docente = docenteRepository.findByCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("No existe un docente con ese correo"));

        DocenteId docenteId = new DocenteId(docente.getId());

        Proyecto proyecto = new Proyecto(dto.getTitulo(), estudiantes, docenteId, dto.getTipoProyecto());

        proyecto.agregarFormatoAInicial(dto.getFormatoA().getNombreFormato(), dto.getFormatoA().getBlob());
        if(dto.getCartaLaboral()!=null){
            proyecto.adjuntarCartaLaboral(dto.getCartaLaboral());
        }

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);

        publicarMensajeRMQ(proyectoGuardado);
        notificarACoordinadores(proyectoGuardado);
    }

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
            Estudiante estudiante = obtenerEstudiantePorId(estId.value());

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

        Docente docente = obtenerDocentePorId(proyectoCreado.getDirectorId().value());

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

    public void notificarACoordinadores(Proyecto proyectoCreado){
        try {
            Estudiante estudiante1 = obtenerEstudiantePorId(proyectoCreado.getEstudiantesId().getFirst().value());

            Docente director = obtenerDocentePorId(proyectoCreado.getDirectorId().value());

            String est1 = estudiante1.getNombres() + " " + estudiante1.getApellidos();
            String est2 = "";

            if(proyectoCreado.getEstudiantesId().size()==2){
                Estudiante estudiante2 = obtenerEstudiantePorId(proyectoCreado.getEstudiantesId().getLast().value());
                est2 = estudiante2.getNombres() + " " + estudiante2.getApellidos();
            }

            String subject = "Nuevo Proyecto Creado";
            String message = String.format(
                    """
                            Se ha creado el proyecto:
                            '%s'
                            Estudiante(s):
                            %s
                            %s
                            Bajo la dirección de:
                            %s %s
                    """,
                    proyectoCreado.getTitulo(),
                    est1,
                    est2,
                    director.getNombres(), director.getApellidos()
            );

            NotificationEvent notificationEvent = new NotificationEvent(
                    "project.created",
                    subject,
                    message,
                    estudiante1.getPrograma(),
                    OffsetDateTime.now()
            );

            rabbitTemplate.convertAndSend(mainExchange, "notification.send.project.created", notificationEvent);

            log.info("📨 Notificación enviada: {}", notificationEvent.getSubject());

        } catch (Exception e) {
            log.error("Error al enviar notificación: {}", e.getMessage(), e);
        }
    }

    private Estudiante obtenerEstudiantePorId(UUID id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un estudiante con el id: " + id
                ));
    }

    private Docente obtenerDocentePorId(UUID id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un docente con el id: " + id
                ));
    }
}
