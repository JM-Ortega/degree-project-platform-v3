package co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence;

import co.edu.unicauca.academicprojectservice.application.dto.DocenteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.DocenteInfoDTO;
import co.edu.unicauca.academicprojectservice.application.dto.EstudianteDTO;
import co.edu.unicauca.academicprojectservice.application.dto.ProyectoInfoDTO;
import co.edu.unicauca.academicprojectservice.application.port.output.DbPort;
import co.edu.unicauca.academicprojectservice.domain.model.Proyecto;
import co.edu.unicauca.academicprojectservice.domain.model.FormatoA;

import co.edu.unicauca.academicprojectservice.domain.model.*;
import co.edu.unicauca.shared.contracts.model.EstadoFormatoA;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.entity.*;
import co.edu.unicauca.academicprojectservice.infraestructura.adapter.output.persistence.repository.*;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DbAdapter implements DbPort {

    private final EstudianteRepository estudianteRepository;
    private final DocenteRepository docenteRepository;
    private final ProyectoRepository proyectoRepository;
    private final FormatoARepository formatoARepository;

    public DbAdapter(EstudianteRepository estudianteRepository, DocenteRepository docenteRepository,
                     ProyectoRepository proyectoRepository, FormatoARepository formatoARepository) {
        this.estudianteRepository = estudianteRepository;
        this.docenteRepository = docenteRepository;
        this.proyectoRepository = proyectoRepository;
        this.formatoARepository = formatoARepository;
    }

    // ===================== ESTUDIANTE =====================

    @Override
    public Optional<EstudianteId> buscarEstudianteIdPorCorreo(String correo) {
        return estudianteRepository.findByCorreoIgnoreCase(correo)
                .map(e -> new EstudianteId(e.getId()));
    }

    @Override
    public Optional<EstudianteDTO> obtenerEstudiantePorId(UUID id) {
        return estudianteRepository.findById(id)
                .map(e -> new EstudianteDTO(
                        e.getNombres(),
                        e.getApellidos(),
                        e.getCelular(),
                        e.getCorreo(),
                        e.getPrograma().toString()
                ));
    }

    // ======================= DOCENTE =======================
    @Override
    public Optional<DocenteId> buscarDocenteIdPorCorreo(String correo) {
        return docenteRepository.findByCorreo(correo)
                .map(d -> new DocenteId(d.getId()));
    }

    @Override
    public Optional<DocenteInfoDTO> obtenerDocenteInfoPorId(UUID id) {
        return docenteRepository.findById(id)
                .map(d -> new DocenteInfoDTO(
                        d.getId(),
                        d.getNombres(),
                        d.getApellidos(),
                        d.getCelular(),
                        d.getCorreo(),
                        d.getDepartamento().name()
                ));
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
        return proyectoRepository.listarInfoPorDocente(docente.getId(), filtro);
    }

    public Proyecto guardarProyecto(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
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


    // ===================== FORMATO A =====================

    @Override
    public int getMaxVersionFormatoA(UUID proyectoId) {
        Integer maxVersion = formatoARepository.findMaxVersionByProyectoId(proyectoId);
        return maxVersion != null ? maxVersion : 0;
    }

    @Override
    public FormatoA obtenerUltimoFormatoA(UUID proyectoId) {
        List<FormatoA> resultados =
                formatoARepository.findUltimoFormatoA(proyectoId, PageRequest.of(0, 1));
        return resultados.isEmpty() ? null : resultados.get(0);
    }


    @Override
    public int contarFormatoAObservados(UUID proyectoId) {
        return formatoARepository.countByProyectoIdAndEstado(
                proyectoId,
                EstadoFormatoA.OBSERVADO
        );
    }
}

