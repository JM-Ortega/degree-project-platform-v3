package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.Estudiante;
import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.shared.contracts.model.Programa;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.FormatoAResumenDTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.FormatoAService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/formatoA")
public class FormatoAController {
    private final FormatoARepository formatoARepository;
    private final FormatoAService formatoAService;

    public FormatoAController(FormatoARepository formatoARepository,
                              FormatoAService formatoAService){
        this.formatoARepository = formatoARepository;
        this.formatoAService = formatoAService;
    }

    @GetMapping("/listar/{programa}")
    public ResponseEntity<List<FormatoAResumenDTO>> listarFormatosAResumen(
            @PathVariable String programa) {

        List<FormatoAResumenDTO> lista = formatoARepository.findAll().stream()
                .filter(f -> {
                    // Aseguramos que haya al menos un estudiante
                    if (f.getEstudiantes() == null || f.getEstudiantes().isEmpty()) return false;
                    Estudiante primerEstudiante = f.getEstudiantes().get(0);
                    return primerEstudiante.getPrograma() == Programa.valueOf(programa);
                })
                .map(f -> new FormatoAResumenDTO(
                        f.getId(),
                        f.getNombreProyecto(),
                        f.getDirector().getNombres() + " " + f.getDirector().getApellidos(),
                        f.getTipoProyecto().toString(),
                        f.getFechaSubida(),
                        f.getEstadoFormatoA(),
                        f.getNroVersion(),
                        f.getNombreFormatoA()
                ))
                .toList();

        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    public FormatoA obtenerPorId(@PathVariable Long id) {
        return formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado"));
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarFormato(@PathVariable Long id) {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato A no encontrado con id: " + id));

        byte[] archivo = formato.getBlob();
        if (archivo == null || archivo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("FormatoA_" + formato.getNroVersion() + ".pdf")
                .build());

        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<FormatoA> actualizarFormato(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam("nombreArchivo") String nombreArchivo,
            @RequestParam("horaActual") String horaActual
    ) throws IOException {

        FormatoA actualizado = formatoAService.actualizarFormato(id, archivo, nuevoEstado, nombreArchivo, horaActual);
        return ResponseEntity.ok(actualizado);
    }
}
