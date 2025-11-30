package co.edu.unicauca.academicprojectservice.adapter.out.persistence;

import co.edu.unicauca.academicprojectservice.adapter.out.persistence.entity.*;
import co.edu.unicauca.academicprojectservice.adapter.out.persistence.repository.*;
import co.edu.unicauca.academicprojectservice.application.dto.AnteproyectoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.DocenteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoEstudianteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.application.mapper.AnteproyectoMapper;
import co.edu.unicauca.academicprojectservice.application.mapper.FormatoAMapper;
import co.edu.unicauca.academicprojectservice.application.mapper.ProyectoMapper;
import co.edu.unicauca.academicprojectservice.domain.model.DocenteId;
import co.edu.unicauca.academicprojectservice.domain.model.EstudianteId;
import co.edu.unicauca.academicprojectservice.port.out.persistence.DbPortProyecto;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DbAdapterProyecto implements DbPortProyecto {

    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;
    private final ProyectoRepository proyectoRepository;
    private final FormatoARepository formatoARepository;
    private final AnteproyectoRepository anteproyectoRepository;
    private final FormatoAMapper formatoAMapper;
    private final ProyectoMapper proyectoMapper;
    private final AnteproyectoMapper anteproyectoMapper;

    public DbAdapterProyecto(EstudianteRepository estudianteRepository, DocenteRepository docenteRepository,
                             ProyectoRepository proyectoRepository, FormatoARepository formatoARepository, AnteproyectoRepository anteproyectoRepository,
                             FormatoAMapper formatoAMapper, ProyectoMapper proyectoMapper, AnteproyectoMapper anteproyectoMapper) {
        this.estudianteRepository = estudianteRepository;
        this.docenteRepository = docenteRepository;
        this.proyectoRepository = proyectoRepository;
        this.formatoARepository = formatoARepository;
        this.anteproyectoRepository = anteproyectoRepository;
        this.formatoAMapper = formatoAMapper;
        this.proyectoMapper = proyectoMapper;
        this.anteproyectoMapper = anteproyectoMapper;
    }

    // ===================== ESTUDIANTE =====================

    @Override
    public Optional<EstudianteId> buscarEstudianteIdPorCorreo(String correo) {
        return estudianteRepository.findByCorreoIgnoreCase(correo)
                .map(e -> new EstudianteId(e.getId()));
    }

    @Override
    public Estudiante obtenerEstudiantePorId(UUID id) {
        return estudianteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado "));
    }

    // ======================= DOCENTE =======================
    @Override
    public Optional<DocenteId> buscarDocenteIdPorCorreo(String correo) {
        return docenteRepository.findByCorreo(correo)
                .map(d -> new DocenteId(d.getId()));
    }

    @Override
    public Docente obtenerDocenteInfoPorId(UUID id) {
        return docenteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Docente no encontrado "));
    }

    @Override
    public Optional<DocenteDTO> obtenerDocentePorCorreo(String correo) {
        return docenteRepository.findByCorreo(correo)
                .map(d -> new DocenteDTO(
                        d.getNombres(),
                        d.getApellidos(),
                        d.getCelular(),
                        d.getCorreo(),
                        d.getDepartamento().name()
                ));
    }


    // ======================= PROYECTO =======================
    @Override
    public List<ProyectoInfoDTO> listarInfoProyectosPorCorreoDocente(String correoDocente, String filtro) {
        Docente docente = docenteRepository.findByCorreo(correoDocente)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con correo: " + correoDocente));

        String filtroNormalizado =
                (filtro == null || filtro.isBlank()) ? null : filtro.trim();

        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtroNormalizado);
    }


    @Override
    public void guardarProyecto(co.edu.unicauca.academicprojectservice.domain.model.Proyecto proyecto) {
        List<UUID> idsEstudiantes = proyecto.getEstudiantesId().stream()
                .map(EstudianteId::value)
                .toList();

        List<Estudiante> listaEstudiantes = estudianteRepository.findAllById(idsEstudiantes);

        Docente docente = docenteRepository.findById(proyecto.getDirectorId().value())
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));

        List<FormatoA> formatosA = formatoAMapper.toEntityList(proyecto.getFormatosA());

        Anteproyecto anteproyecto = null;

        if (proyecto.getAnteproyecto() != null) {
            anteproyecto = anteproyectoMapper.domainToEntity(proyecto.getAnteproyecto());

            if (proyecto.getAnteproyecto().getEvaluadores() != null) {
                List<Docente> evaluadores = proyecto.getAnteproyecto().getEvaluadores().stream()
                        .map(e -> docenteRepository.findById(e.value())
                                .orElseThrow(() -> new IllegalArgumentException("Evaluador no encontrado")))
                        .toList();

                anteproyecto.setEvaluadores(evaluadores);
            }
        }

        Proyecto p = new Proyecto(
                proyecto.getId(),
                proyecto.getTitulo(),
                listaEstudiantes,
                docente,
                null,
                formatosA,
                proyecto.getCartaLaboral(),
                anteproyecto,
                proyecto.getTipoProyecto(),
                proyecto.getEstadoProyecto()
        );

        proyectoRepository.save(p);
    }


    @Override
    public void actualizarEstadoProyecto(UUID proyectoId, EstadoProyecto estado) {
        proyectoRepository.actualizarEstadoProyecto(proyectoId, estado);
    }

    @Override
    public EstadoProyecto obtenerEstadoProyecto(UUID proyectoId) {
        String estado = proyectoRepository.getEstadoProyecto(proyectoId);
        return EstadoProyecto.valueOf(estado);
    }

    @Override
    public List<ProyectoEstudianteDTO> listarProyectosPorCorreoEstudiante(String correo) {
        List<Proyecto> proyectos = proyectoRepository.findByEstudianteCorreo(correo);

        return proyectos.stream()
                .map(p -> {
                    Docente director = docenteRepository.findById(p.getDirector().getId())
                            .orElse(null);

                    String nombreDirector = (director == null)
                            ? null
                            : director.getNombres() + " " + director.getApellidos();

                    return new ProyectoEstudianteDTO(
                            p.getId(),
                            p.getTitulo(),
                            nombreDirector,
                            p.getTipoProyecto().toString(),
                            p.getEstadoProyecto().toString()
                    );
                })
                .toList();
    }

    @Override
    public co.edu.unicauca.academicprojectservice.domain.model.Proyecto findById(UUID proyectoId) {
        Proyecto p = proyectoRepository.findById(proyectoId).orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
        co.edu.unicauca.academicprojectservice.domain.model.Proyecto proyecto = proyectoMapper.entityToDomain(p);
        return proyecto;
    }

    @Override
    public co.edu.unicauca.academicprojectservice.domain.model.Proyecto buscarPorCorreo(String correo){
        Proyecto p = proyectoRepository.findByEstudianteCorreoTramite(correo).orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
        co.edu.unicauca.academicprojectservice.domain.model.Proyecto proyecto = proyectoMapper.entityToDomain(p);
        return proyecto;
    }

    @Override
    public int countProyectosByEstadoYTipo(TipoProyecto tipo, EstadoProyecto estado, String correoDocente) {
        return proyectoRepository.countProyectosByEstadoYTipo(tipo, estado, correoDocente);
    }

    // ===================== FORMATO A =====================

    @Override
    public int getMaxVersionFormatoA(UUID proyectoId) {
        Integer maxVersion = formatoARepository.findMaxVersionByProyectoId(proyectoId);
        return maxVersion != null ? maxVersion : 0;
    }

    @Override
    public co.edu.unicauca.academicprojectservice.domain.model.FormatoA obtenerUltimoFormatoA(UUID proyectoId) {
        List<FormatoA> resultados =
                formatoARepository.findUltimoFormatoA(proyectoId, PageRequest.of(0, 1));
        co.edu.unicauca.academicprojectservice.domain.model.FormatoA f = formatoAMapper.toDomain(resultados.isEmpty() ? null : resultados.get(0));
        return f;
    }


    @Override
    public int contarFormatoAObservados(UUID proyectoId) {
        return formatoARepository.countByProyectoIdAndEstado(
                proyectoId,
                EstadoFormatoA.OBSERVADO
        );
    }

    // ============== ANTEPROYECTO ===============

    @Override
    public List<AnteproyectoDTO> listarAnteproyectosPorCorreoDocente(String correo, String filtro){
        return anteproyectoRepository.listarAnteproyectosPorCorreoDocente(correo, filtro);
    }

    @Override
    public AnteproyectoDTO obtenerAnteproyecto (UUID proyectoId){
        Proyecto proyecto = proyectoRepository.findById(proyectoId).orElseThrow(() -> new EntityNotFoundException("Proyecto no encontrado"));
        Anteproyecto anteproyecto = proyecto.getAnteproyecto();

        AnteproyectoDTO dto = new AnteproyectoDTO();
        dto.setId(anteproyecto.getId());
        dto.setNombreArchivo(anteproyecto.getNombreArchivo());
        dto.setDescripcion(anteproyecto.getDescripcion());
        dto.setTitulo(anteproyecto.getTitulo());
        dto.setBlob(anteproyecto.getBlob());
        dto.setFechaCreacion(anteproyecto.getFechaCreacion());

        if (proyecto.getEstudiantes() != null && !proyecto.getEstudiantes().isEmpty()) {
            dto.setEstudianteNombre(proyecto.getEstudiantes().get(0).getNombres());
            dto.setEstudianteCorreo(proyecto.getEstudiantes().get(0).getCorreo());
        }

        return dto;
    }
}

