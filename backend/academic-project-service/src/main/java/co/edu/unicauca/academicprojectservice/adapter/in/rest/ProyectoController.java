package co.edu.unicauca.academicprojectservice.adapter.in.rest;


import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.academicprojectservice.application.services.ProyectoService;
import co.edu.unicauca.academicprojectservice.port.in.rest.ProyectoPort;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

public class ProyectoController implements ProyectoPort {
    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    public ResponseEntity<String> crearProyecto(ProyectoDTO dto) {
        try {
            proyectoService.crearProyectoConArchivos(dto);
            return ResponseEntity.ok("Proyecto creado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el proyecto: " + e.getMessage());
        }
    }
    public ResponseEntity<String> insertarFormatoAProyecto(Long proyectoId, FormatoADTO formatoA) {
        proyectoService.insertarFormatoAEnProyecto(UUID.fromString(proyectoId.toString()), formatoA);
        return ResponseEntity.ok("Formato A insertado correctamente");
    }

   // no se usa
        /*
        public ResponseEntity<String> actualizarFormatoA(UUID proyectoId, EstadoRequest request) {
            proyectoService.actualizarFormatoA(proyectoId, request.getEstado());
            return ResponseEntity.ok("Formato A actualizado correctamente");
        }
        */

        //No se debería usar porque se valida que exista el proyecto antes de insertar el formato
        /*
        public ResponseEntity<Boolean> existeProyecto(UUID proyectoId) {
            boolean tiene = proyectoService.existeProyecto(proyectoId);
            return ResponseEntity.ok(tiene);
        }
        */

        /* Se valida esto al insertar el proyecto por tanto ya no se usa
        public ResponseEntity<String> estadoProyecto(UUID proyectoId) {
            String estado = proyectoService.estadoProyecto(proyectoId);
            return ResponseEntity.ok(estado);
        }
         */

    // ============================ migrados =====================================
    public ResponseEntity<List<ProyectoInfoDTO>> listarPorDocente(String correo, String filtro) {
        List<ProyectoInfoDTO> proyectos = proyectoService.listarInfoPorCorreoDocente(correo, filtro);
        return ResponseEntity.ok(proyectos);
    }

    public ResponseEntity<List<ProyectoEstudianteDTO>> listarPorEstudiante(String correo) {
        List<ProyectoEstudianteDTO> lista = proyectoService.listarPorEstudiante(correo);
        return ResponseEntity.ok(lista);
    }




    public ResponseEntity<EstadoProyecto> enforceAutoCancelIfNeeded(UUID proyectoId) {
        EstadoProyecto estado = proyectoService.enforceAutoCancelIfNeeded(proyectoId);
        return ResponseEntity.ok(estado);
    }

    public ResponseEntity<Integer> getMaxVersionFormatoA(UUID proyectoId) {
        int maxVersion = proyectoService.getMaxVersionFormatoA(proyectoId);
        return ResponseEntity.ok(maxVersion);
    }

    public ResponseEntity<Boolean> canResubmit(UUID proyectoId) {
        boolean puede = proyectoService.canResubmit(proyectoId);
        return ResponseEntity.ok(puede);
    }

    public ResponseEntity<Boolean> tieneObservacionesFA(UUID proyectoId) {
        boolean tiene = proyectoService.tieneObservaciones(proyectoId);
        return ResponseEntity.ok(tiene);
    }

    public ResponseEntity<FormatoADTO> obtenerUltimoFormatoAConObservaciones(UUID proyectoId) {
        try {
            FormatoADTO formatoADTO = proyectoService.obtenerUltimoFormatoAConObservaciones(proyectoId);
            return ResponseEntity.ok(formatoADTO);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Integer> countProyectosByEstadoYTipo(TipoProyecto tipoProyecto, EstadoProyecto estadoProyecto, String correoDocente) {
        int count = proyectoService.countProyectosByEstadoYTipo(tipoProyecto, estadoProyecto, correoDocente);
        return ResponseEntity.ok(count);
    }

    public ResponseEntity<List<AnteproyectoDTO>> listarAnteproyectosDocente(String correo, String filtro) {
        List<AnteproyectoDTO> lista = proyectoService.listarAnteproyectosDocente(correo, filtro);
        return ResponseEntity.ok(lista);
    }

    public ResponseEntity<AnteproyectoDTO> obtenerAnteproyecto(UUID proyectoId) {
        try {
            AnteproyectoDTO anteproyecto = proyectoService.obtenerAnteproyecto(proyectoId);
            return ResponseEntity.ok(anteproyecto);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
