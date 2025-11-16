package co.edu.unicauca.academicprojectservice.application.services;

import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.DocenteDTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.EstudianteDTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.FormatoADTOSend;
import co.edu.unicauca.academicprojectservice.Old.infra.DTOs.ProyectoDTOSend;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Docente;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.Estudiante;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.FormatoA;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.DocenteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.EstudianteRepository;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.ProyectoRepository;
import co.edu.unicauca.shared.contracts.events.notification.NotificationEvent;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;

    private EstudianteRepository estudianteRepository;
    @Autowired
    private DocenteRepository docenteRepository;












// ==========================================================

    public List<ProyectoInfoDTO> listarInfoPorCorreoDocente(String correo, String filtro) {
        Docente docente = docenteRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con correo: " + correo));

        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtro);
    }


// ==========================================================






















    @Transactional
    public void crearProyectoConArchivos(ProyectoDTO dto) {

        Estudiante estudiante = estudianteRepository.findByCorreoIgnoreCase(dto.getEstudiante())
                .orElseThrow(() -> new IllegalArgumentException("No existe un estudiante con ese correo"));

        Docente docente = docenteRepository.findByCorreo(dto.getDirector())
                .orElseThrow(() -> new IllegalArgumentException("No existe un docente con ese correo"));

        List<EstudianteId> estudiantes = new ArrayList<>();

        Proyecto proyecto = new Proyecto(dto.getTitulo(), estudiantes, docente, dto.getTipoProyecto());


        // ===== Asociar Formato A (si lo hay) =====
        FormatoA formatoA = dto.getFormatoA();
        if (formatoA != null) {
            formatoA.setProyecto(proyecto);
            formatoA.setEstado(dto.getFormatoA().getEstado());
            formatoA.setNombreFormato(dto.getFormatoA().getNombreFormato());
            formatoA.setFechaCreacion(dto.getFormatoA().getFechaCreacion());
            formatoA.setNroVersion(dto.getFormatoA().getNroVersion());
            proyecto.addFormato(formatoA);
        }

        // ===== Asociar Carta Laboral (si la hay) =====
        CartaLaboral cartaLaboral = dto.getCartaLaboral();
        if (cartaLaboral != null) {
            cartaLaboral.setProyecto(proyecto);
            cartaLaboral.setNombreCartaLaboral(dto.getCartaLaboral().getNombreCartaLaboral());
            cartaLaboral.setFechaCreacion(dto.getCartaLaboral().getFechaCreacion());
            proyecto.setCartaLaboral(cartaLaboral);
        }

        // ===== Guardar el proyecto y obtener su ID =====
        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
        Long proyectoId = proyectoGuardado.getId();

        // =====================================================
        // Construcción del DTO que se enviará por RabbitMQ
        // =====================================================
        ProyectoDTOSend pDtoSend = new ProyectoDTOSend();
        pDtoSend.setId(proyectoId);
        pDtoSend.setTitulo(proyectoGuardado.getTitulo());
        pDtoSend.setTipoProyecto(proyectoGuardado.getTipoProyecto());
        pDtoSend.setEstado(proyectoGuardado.getEstadoProyecto());

        // ======= Estudiantes DTO =======
        List<EstudianteDTOSend> estudiantes = new ArrayList<>();
        EstudianteDTOSend estDto = new EstudianteDTOSend();
        estDto.setId(proyectoGuardado.getEstudiantes().get(0).getId());
        estDto.setPrograma(estudiante.getPrograma());
        estDto.setEmail(estudiante.getCorreo());
        estDto.setNombres(estudiante.getNombres());
        estDto.setApellidos(estudiante.getApellidos());
        estDto.setCelular(estudiante.getCelular());

        // Referencia inversa de trabajos (opcional; evitar ciclos de serialización)
        estDto.setTrabajos(List.of(pDtoSend));
        estudiantes.add(estDto);
        pDtoSend.setEstudiantes(estudiantes);

        // ======= Director DTO =======
        DocenteDTOSend docDto = new DocenteDTOSend();
        docDto.setId(proyectoGuardado.getDirector().getId());
        docDto.setDepartamento(docente.getDepartamento());
        docDto.setEmail(docente.getCorreo());
        docDto.setNombres(docente.getNombres());
        docDto.setApellidos(docente.getApellidos());
        docDto.setCelular(docente.getCelular());
        docDto.setTrabajosComoDirector(List.of(pDtoSend));
        docDto.setTrabajosComoCodirector(null);

        pDtoSend.setDirector(docDto);
        pDtoSend.setCodirector(null); // En caso de no tener

        // ======= Formato A DTO =======
        if (formatoA != null) {
            FormatoADTOSend formatoSend = new FormatoADTOSend();
            formatoSend.setId(formatoA.getId());
            formatoSend.setProyectoId(proyectoId);
            formatoSend.setNroVersion(formatoA.getNroVersion());
            formatoSend.setNombreFormatoA(formatoA.getNombreFormato());
            formatoSend.setFechaSubida(formatoA.getFechaCreacion());
            formatoSend.setBlob(formatoA.getBlob());
            formatoSend.setEstado(formatoA.getEstado());
            pDtoSend.setFormatoA(formatoSend);
        } else {
            pDtoSend.setFormatoA(null);
        }

        // No hay anteproyecto al crear
        pDtoSend.setAnteproyecto(null);

        // ======= Envío del mensaje =======
        rabbitTemplate.convertAndSend(mainExchange, routingKeyProjectCreated, pDtoSend);

        co.edu.unicauca.academicprojectservice.Old.Service.ProyectoService.log.info("[RabbitMQ] Proyecto creado enviado a la cola: {} con ID: {}",
                routingKeyProjectCreated, proyectoId);

        // ======= Notificación a coordinadores =======
        try {
            List<String> destinatarios = new ArrayList<>();
            List<String> celulares = new ArrayList<>();

            List<Coordinador> coordinadores = coordinadorRepository.findAll();
            if (coordinadores != null && !coordinadores.isEmpty()) {
                for (Coordinador coordinador : coordinadores) {
                    if (coordinador.getCorreo() != null && !coordinador.getCorreo().isEmpty()) {
                        destinatarios.add(coordinador.getCorreo());
                    }
                    if (coordinador.getCelular() != null && !coordinador.getCelular().isEmpty()) {
                        celulares.add(coordinador.getCelular());
                    }
                }
            }

            String subject = "Nuevo Proyecto Creado";
            String message = String.format(
                    "Se ha creado el proyecto '%s' para el estudiante %s %s, bajo la dirección de %s %s.",
                    proyectoGuardado.getTitulo(),
                    estudiante.getNombres(), estudiante.getApellidos(),
                    docente.getNombres(), docente.getApellidos()
            );

            NotificationEvent notificationEvent = new NotificationEvent(
                    "project.created",
                    subject,
                    message,
                    estudiantes.get(0).getPrograma(),
                    OffsetDateTime.now()
            );

            rabbitTemplate.convertAndSend(
                    mainExchange,
                    "notification.send.project.created",
                    notificationEvent
            );

            co.edu.unicauca.academicprojectservice.Old.Service.ProyectoService.log.info("📨 Notificación enviada: {}", notificationEvent.getSubject());

        } catch (Exception e) {
            co.edu.unicauca.academicprojectservice.Old.Service.ProyectoService.log.error("Error al enviar notificación: {}", e.getMessage(), e);
        }
    }
}
