package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Docente;
import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.repository.DocenteRepository;
import co.edu.unicauca.coordinatorservice.repository.EstudianteRepository;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.DocenteDTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.EstudianteDTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.FormatoADTO;
import co.edu.unicauca.shared.contracts.events.academic.DTOs.ProyectoDTO;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProyectoListener {

    private static final Logger log = LoggerFactory.getLogger(ProyectoListener.class);

    private final FormatoARepository formatoARepository;
    private final DocenteRepository docenteRepository;
    private final EstudianteRepository estudianteRepository;

    public ProyectoListener(FormatoARepository formatoARepository,
                            DocenteRepository docenteRepository,
                            EstudianteRepository estudianteRepository) {
        this.formatoARepository = formatoARepository;
        this.docenteRepository = docenteRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @RabbitListener(queues = "${messaging.queues.coordinator}")
    @Transactional
    public void onProyectoEvent(ProyectoDTO dto) {
        if (dto == null) {
            log.warn("[CoordinatorService] ProyectoDTO nulo recibido. Se ignora.");
            return;
        }

        log.info("[CoordinatorService] Evento de proyecto recibido: id={}, titulo={}",
                dto.getId(), dto.getTitulo());

        // ===== Formato A: crear o actualizar por proyectoId =====
        Optional<FormatoA> existingFormato = formatoARepository.findByProyectoId(dto.getId());
        FormatoA formato = existingFormato.orElseGet(FormatoA::new);

        formato.setProyectoId(dto.getId());
        formato.setNombreProyecto(dto.getTitulo());

        // Tipo de proyecto (enum compartido)
        TipoProyecto tipoProyecto = dto.getTipoProyecto();
        formato.setTipoProyecto(tipoProyecto);

        // ===== Mapear datos del Formato A inicial, si vienen en el DTO =====
        FormatoADTO formatoDto = dto.getFormatoA();
        if (formatoDto != null) {
            formato.setNroVersion(formatoDto.getNroVersion());
            formato.setNombreFormatoA(formatoDto.getNombreFormatoA());
            formato.setFechaSubida(formatoDto.getFechaSubida());
            formato.setBlob(formatoDto.getBlob());

            if (formatoDto.getEstado() != null) {
                formato.setEstadoFormatoA(formatoDto.getEstado());
            } else {
                formato.setEstadoFormatoA(EstadoFormatoA.PENDIENTE);
            }
        } else {
            // Si por alguna razón no llega, lo dejamos sin datos de archivo
            log.warn("[CoordinatorService] Proyecto {} llegó sin FormatoA en el DTO", dto.getId());
        }

        // ===== Director =====
        if (dto.getDirector() != null) {
            Docente director = upsertDocente(dto.getDirector());
        formato.setDirector(director);
        } else {
            log.warn("[CoordinatorService] Proyecto {} sin director en DTO", dto.getId());
            formato.setDirector(null);
        }

        // ===== Codirector (opcional) =====
        if (dto.getCodirector() != null) {
            Docente codirector = upsertDocente(dto.getCodirector());
            formato.setCoodirector(codirector);
        } else {
            formato.setCoodirector(null);
        }

        // ===== Estudiantes =====
        List<Estudiante> estudiantes = new ArrayList<>();

        if (dto.getEstudiantes() != null) {
            for (EstudianteDTO estDto : dto.getEstudiantes()) {
                Estudiante estudiante = upsertEstudiante(estDto);
            estudiantes.add(estudiante);
            }
        }

        formato.setEstudiantes(estudiantes);

        // Persistir Formato A
        formatoARepository.save(formato);

        log.info("[CoordinatorService] FormatoA persistido: proyecto='{}', version={}",
                formato.getNombreProyecto(), formato.getNroVersion());
    }


    // =================== Helpers privados ===================

    private Docente upsertDocente(DocenteDTO dto) {
        UUID id = dto.getId();

        Docente docente = docenteRepository.findById(id)
                .orElseGet(Docente::new);

        if (docente.getId() == null) {
            docente.setId(id);
        }

        docente.setNombres(dto.getNombres());
        docente.setApellidos(dto.getApellidos());
        docente.setEmail(dto.getEmail());
        docente.setCelular(dto.getCelular());

        return docenteRepository.save(docente);
    }

    private Estudiante upsertEstudiante(EstudianteDTO dto) {
        UUID id = dto.getId();

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseGet(Estudiante::new);

        if (estudiante.getId() == null) {
            estudiante.setId(id);
        }

        estudiante.setNombres(dto.getNombres());
        estudiante.setApellidos(dto.getApellidos());
        estudiante.setEmail(dto.getEmail());
        estudiante.setCelular(dto.getCelular());
        estudiante.setCodigo(dto.getCodigo());
        estudiante.setPrograma(dto.getPrograma()); // enum compartido

        return estudianteRepository.save(estudiante);
    }
}
