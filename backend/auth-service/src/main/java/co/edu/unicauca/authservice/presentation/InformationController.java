package co.edu.unicauca.authservice.presentation;

import co.edu.unicauca.authservice.services.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InformationController {
    @Autowired
    private InformationService informationService;

    @GetMapping("/{programa}")
    public ResponseEntity<String> getEmailCoordinador(@PathVariable String programa)
    {
        String email =informationService.obtenerEmailCoordinadorPorPrograma(programa);
        return ResponseEntity.ok(email);
    }

    @GetMapping("/telefono")
    public ResponseEntity<String> getNumerosTelefono(
            @RequestParam String correo
    ){
        String telefono = informationService.obtenerTelefonoPorCorreo(correo);
        return ResponseEntity.ok(telefono);
    }
}
