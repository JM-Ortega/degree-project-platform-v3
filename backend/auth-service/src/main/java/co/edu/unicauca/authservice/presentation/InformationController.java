package co.edu.unicauca.authservice.presentation;

import co.edu.unicauca.authservice.services.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/information")
public class InformationController {
    @Autowired
    private InformationService informationService;

    @GetMapping("/{programa}")
    public ResponseEntity<String> getEmailCoordinador(@PathVariable String programa)
    {
        String email =informationService.obtenerEmailCoordinadorPorPrograma(programa);
        return ResponseEntity.ok(email);
    }

}
