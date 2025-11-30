package co.edu.unicauca.academicprojectservice.adapter.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.academicprojectservice.application.services.ProyectoService;
import co.edu.unicauca.academicprojectservice.port.in.rest.ProyectoPort;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController implements ProyectoPort {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    // =================== CREAR PROYECTO ===================
    @PostMapping("/crearProyecto")
    @Override
    public ResponseEntity<String> crearProyecto(@RequestBody ProyectoDTO dto) {
        proyectoService.crearProyectoConArchivos(dto);
        return ResponseEntity.ok("Proyecto creado exitosamente");
    }

    // =================== FORMATOS A ===================
    @PostMapping("/insertarFormatoAProyecto/{proyectoId}")
    @Override
    public ResponseEntity<String> insertarFormatoAProyecto(
            @PathVariable UUID proyectoId,
            @RequestBody FormatoADTO formatoA
    ) {
        proyectoService.insertarFormatoAEnProyecto(proyectoId, formatoA);
        return ResponseEntity.ok("Formato A insertado correctamente");
    }

    @GetMapping("/{proyectoId}/formatoA/max-version")
    @Override
    public ResponseEntity<Integer> getMaxVersionFormatoA(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(proyectoService.getMaxVersionFormatoA(proyectoId));
    }

    @GetMapping("/observacionesFA/{proyectoId}")
    @Override
    public ResponseEntity<Boolean> tieneObservacionesFA(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(proyectoService.tieneObservaciones(proyectoId));
    }

    @GetMapping("/ultimoFormatoAConObservaciones/{proyectoId}")
    @Override
    public ResponseEntity<FormatoADTO> obtenerUltimoFormatoAConObservaciones(@PathVariable UUID proyectoId) {
        FormatoADTO dto = proyectoService.obtenerUltimoFormatoAConObservaciones(proyectoId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/resubmit/{proyectoId}")
    @Override
    public ResponseEntity<Boolean> canResubmit(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(proyectoService.canResubmit(proyectoId));
    }

    // =================== LISTAR PROYECTOS ===================
    @GetMapping("/docente/{correo}")
    @Override
    public ResponseEntity<List<ProyectoInfoDTO>> listarPorDocente(
            @PathVariable String correo,
            @RequestParam(required = false) String filtro
    ) {
        List<ProyectoInfoDTO> proyectos = proyectoService.listarInfoPorCorreoDocente(correo, filtro);
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/listar/{correo}")
    @Override
    public ResponseEntity<List<ProyectoEstudianteDTO>> listarPorEstudiante(@PathVariable String correo) {
        return ResponseEntity.ok(proyectoService.listarPorEstudiante(correo));
    }

    // =================== ESTADO DEL PROYECTO ===================
    @GetMapping("/{id}/enforceAutoCancel")
    @Override
    public ResponseEntity<EstadoProyecto> enforceAutoCancelIfNeeded(@PathVariable("id") UUID proyectoId) {
        return ResponseEntity.ok(proyectoService.enforceAutoCancelIfNeeded(proyectoId));
    }

    // =================== ESTADISTICAS ===================
    @GetMapping("/countProyectosBy")
    @Override
    public ResponseEntity<Integer> countProyectosByEstadoYTipo(
            @RequestParam TipoProyecto tipoProyecto,
            @RequestParam EstadoProyecto estadoProyecto,
            @RequestParam String correoDocente
    ) {
        return ResponseEntity.ok(
                proyectoService.countProyectosByEstadoYTipo(tipoProyecto, estadoProyecto, correoDocente)
        );
    }

    // =================== ANTEPROYECTOS ===================
    @GetMapping("/docente/{correo}/anteproyectos")
    @Override
    public ResponseEntity<List<AnteproyectoDTO>> listarAnteproyectosDocente(
            @PathVariable String correo,
            @RequestParam(required = false) String filtro
    ) {
        return ResponseEntity.ok(proyectoService.listarAnteproyectosDocente(correo, filtro));
    }

    @GetMapping("/{proyectoId}/anteproyecto")
    @Override
    public ResponseEntity<AnteproyectoDTO> obtenerAnteproyecto(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(proyectoService.obtenerAnteproyecto(proyectoId));
    }
}

