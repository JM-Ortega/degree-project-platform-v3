package co.edu.unicauca.academicprojectservice.port.in.rest;

import co.edu.unicauca.academicprojectservice.application.dto.*;
import co.edu.unicauca.shared.contracts.model.EstadoProyecto;
import co.edu.unicauca.shared.contracts.model.TipoProyecto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


public interface ProyectoPort {
    // ===== se mantiene pero se camia la logica
    @PostMapping("/crearProyecto")
    public ResponseEntity<String> crearProyecto(@RequestBody ProyectoDTO dto);

    @GetMapping("/docente/{correo}")
    public ResponseEntity<List<ProyectoInfoDTO>> listarPorDocente(
            @PathVariable("correo") String correo,
            @RequestParam(value = "filtro", required = false) String filtro
    );

    @GetMapping("/listar/{correo}")
    public ResponseEntity<List<ProyectoEstudianteDTO>> listarPorEstudiante(@PathVariable String correo);

    @GetMapping("/{id}/enforceAutoCancel")
    public ResponseEntity<EstadoProyecto> enforceAutoCancelIfNeeded(@PathVariable("id") UUID proyectoId);

    @GetMapping("/{proyectoId}/formatoA/max-version")
    public ResponseEntity<Integer> getMaxVersionFormatoA(@PathVariable UUID proyectoId);

    @GetMapping("/resubmit/{proyectoId}")
    public ResponseEntity<Boolean> canResubmit(@PathVariable UUID proyectoId);

    @GetMapping("/observacionesFA/{proyectoId}")
    public ResponseEntity<Boolean> tieneObservacionesFA(@PathVariable UUID proyectoId);

    /*No se usa, ya se valida en otra parte
    @GetMapping("/estadoProyecto/{proyectoId}")
    public ResponseEntity<String> estadoProyecto(@PathVariable UUID proyectoId);
     */

    @PostMapping("/insertarFormatoAProyecto/{proyectoId}")
    public ResponseEntity<String> insertarFormatoAProyecto(@PathVariable Long proyectoId, @RequestBody FormatoADTO formatoA);

    @GetMapping("/ultimoFormatoAConObservaciones/{proyectoId}")
    public ResponseEntity<FormatoADTO> obtenerUltimoFormatoAConObservaciones(@PathVariable UUID proyectoId);

    /*
    - Se usaba para validar que no hubieran más de 7 proyectos activos para el docente en el front pero ya se paso la
    - validacion al back
    @GetMapping("/countProyectosBy")
    public ResponseEntity<Integer> countProyectosByEstadoYTipo(@RequestParam TipoProyecto tipoProyecto, @RequestParam EstadoProyecto estadoProyecto, @RequestParam String correoDocente);
     */
    @GetMapping("/docente/{correo}/anteproyectos")
    public ResponseEntity<List<AnteproyectoDTO>> listarAnteproyectosDocente(
            @PathVariable("correo") String correo,
            @RequestParam(value = "filtro", required = false) String filtro
    );

    @GetMapping("/{proyectoId}/anteproyecto")
    public ResponseEntity<AnteproyectoDTO> obtenerAnteproyecto(@PathVariable UUID proyectoId);


    /*
    @PostMapping("/actualizarFormatoA/{proyectoId}")
    public ResponseEntity<String> actualizarFormatoA(@PathVariable UUID proyectoId, @RequestBody EstadoRequest request);
*/
}
