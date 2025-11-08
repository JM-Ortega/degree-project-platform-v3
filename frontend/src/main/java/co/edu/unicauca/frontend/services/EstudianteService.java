package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.infra.dto.AnteproyectoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class EstudianteService {
    private final RestTemplate restTemplate;
    private final String baseUrlEstudiante = "http://localhost:8080/api/academic/estudiantes";

    public EstudianteService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * /estudiantes/libre/{correo} => { correo: String, libre: Boolean }
     */
    public boolean estudianteLibrePorCorreo(String correo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlEstudiante)
                .path("/libre/{correo}")
                .buildAndExpand(correo)
                .encode()
                .toUriString();
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            Object libre = resp.getBody() != null ? resp.getBody().get("libre") : null;
            return libre instanceof Boolean ? (Boolean) libre : false;
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("No existe un estudiante con ese correo");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Correo inválido");
        } catch (Exception e) {
            throw new RuntimeException("Error al validar estudiante", e);
        }
    }

    public boolean estudianteExistePorCorreo(String correo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlEstudiante)
                .path("/existe/{correo}")
                .buildAndExpand(correo)
                .encode()
                .toUriString();
        try {
            ResponseEntity<Boolean> resp = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error consultando existencia de estudiante", e);
        }
    }

    public boolean estudianteTieneProyectoEnTramitePorCorreo(String correo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlEstudiante)
                .path("/tieneProyectoEnTramite/{correo}")
                .buildAndExpand(correo)
                .encode()
                .toUriString();
        try {
            ResponseEntity<Boolean> resp = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error consultando proyecto en trámite", e);
        }
    }

    public boolean estudianteTieneFormatoAAprobado(String correo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlEstudiante)
                .path("/tieneFormatoAAprobado/{correo}")
                .buildAndExpand(correo)
                .encode()
                .toUriString();
        try {
            ResponseEntity<Boolean> resp = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Error consultando Formato A", e);
        }
    }

    public boolean estudianteTieneAnteproyectoAsociado(String correo) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrlEstudiante)
                .path("/{correo}/tieneAnteproyecto")
                .buildAndExpand(correo)
                .encode()
                .toUriString();
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("No se encontró el estudiante con el correo ingresado");
        } catch (Exception ex) {
            throw new RuntimeException("Error al verificar el anteproyecto del estudiante: " + ex.getMessage(), ex);
        }
    }

    public void setAntepAProyectoEst(AnteproyectoDTO a) {
        String correo = a.getEstudianteCorreo();

        if (!estudianteExistePorCorreo(correo)) {
            throw new IllegalArgumentException("El estudiante con el correo ingresado no existe");
        }
        if (!estudianteTieneProyectoEnTramitePorCorreo(correo)) {
            // mensaje alineado con la verificación real
            throw new IllegalArgumentException("El estudiante no tiene un proyecto en estado EN_TRAMITE");
        }
        if (!estudianteTieneFormatoAAprobado(correo)) {
            throw new IllegalArgumentException("El Formato A del estudiante no está en estado APROBADO");
        }
        if (estudianteTieneAnteproyectoAsociado(correo)) {
            throw new IllegalArgumentException("El estudiante ya tiene un anteproyecto asociado");
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrlEstudiante)
                .path("/asociarAnteproyecto/{correo}")
                .buildAndExpand(correo)
                .encode()
                .toUriString();

        try {
            restTemplate.postForObject(url, a, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String mensajeError = ex.getResponseBodyAsString();
            throw new RuntimeException(mensajeError, ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado: " + ex.getMessage(), ex);
        }
    }
}
