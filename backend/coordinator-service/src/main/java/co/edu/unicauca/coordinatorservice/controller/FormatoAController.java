package co.edu.unicauca.coordinatorservice.controller;

import co.edu.unicauca.coordinatorservice.entity.FormatoA;
import co.edu.unicauca.coordinatorservice.infra.DTOSInternos.FormatoAResumenDTO;
import co.edu.unicauca.coordinatorservice.repository.FormatoARepository;
import co.edu.unicauca.coordinatorservice.service.FormatoAService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/formatoA")
public class FormatoAController {

    private final FormatoARepository formatoARepository;
    private final FormatoAService formatoAService;

    public FormatoAController(FormatoARepository formatoARepository,
                              FormatoAService formatoAService) {
        this.formatoARepository = formatoARepository;
        this.formatoAService = formatoAService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<FormatoAResumenDTO>> listarFormatosAResumen(
            @RequestParam("email") String emailCoordinador) {

        List<FormatoAResumenDTO> lista =
                formatoAService.listarResumenPorCoordinador(emailCoordinador);

        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    public ResponseEntity<FormatoA> obtenerPorId(@PathVariable UUID id) {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FormatoA no encontrado con id: " + id));

        return ResponseEntity.ok(formato);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarFormato(@PathVariable UUID id) {
        FormatoA formato = formatoARepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formato A no encontrado con id: " + id));

        byte[] archivo = formato.getBlob();
        if (archivo == null || archivo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("FormatoA_" + formato.getNroVersion() + ".pdf")
                        .build()
        );

        return new ResponseEntity<>(archivo, headers, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<FormatoA> actualizarFormato(
            @PathVariable UUID id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nuevoEstado") String nuevoEstado,
            @RequestParam("nombreArchivo") String nombreArchivo
    ) throws IOException {

        FormatoA actualizado = formatoAService.actualizarFormato(id, archivo, nuevoEstado, nombreArchivo);
        return ResponseEntity.ok(actualizado);
    }
}
