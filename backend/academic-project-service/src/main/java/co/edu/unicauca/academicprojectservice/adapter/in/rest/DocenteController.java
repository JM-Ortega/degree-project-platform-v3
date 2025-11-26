package co.edu.unicauca.academicprojectservice.infrastructure.adapters.input.rest;

import co.edu.unicauca.academicprojectservice.application.services.DocenteService;
import co.edu.unicauca.academicprojectservice.application.dto.DocenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/docentes")
public class DocenteController {
    @Autowired
    private DocenteService docenteService;

    // Revisar si se usan =======================================
//    @GetMapping("/{correo}")
//    public ResponseEntity<DocenteDTO> getDocentePorCorreo(@PathVariable String correo) {
//        DocenteDTO dto = docenteService.obtenerDocentePorCorreo(correo);
//        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
//    }
//
//    @PostMapping("/agregarDocente")
//    public ResponseEntity<DocenteDTO> agregarDocente(@RequestBody DocenteDTO dto) {
//        docenteService.agregarDocente(dto);
//        return ResponseEntity.ok(dto);
//    }
    //===========================================================

    @GetMapping("/countProyectos/{correo}")
    public ResponseEntity<Integer> contarProyectosEnTramite(@PathVariable String correo) {
        int cantidad = docenteService.countProyectosEnTramitePorCorreo(correo);
        return ResponseEntity.ok(cantidad);
    }
}
