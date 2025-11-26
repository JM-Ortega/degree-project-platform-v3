package co.edu.unicauca.academicprojectservice.adapter.in.rest;

import co.edu.unicauca.academicprojectservice.application.services.DocenteService;
import co.edu.unicauca.academicprojectservice.port.in.rest.DocentePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docentes")
public class DocenteController implements DocentePort {
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

    public ResponseEntity<Integer> contarProyectosEnTramite(String correo) {
        int cantidad = docenteService.countProyectosEnTramitePorCorreo(correo);
        return ResponseEntity.ok(cantidad);
    }
}
