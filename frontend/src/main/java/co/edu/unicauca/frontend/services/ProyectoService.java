package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.entities.EstadoFormatoA;
import co.edu.unicauca.frontend.entities.EstadoProyecto;
import co.edu.unicauca.frontend.entities.TipoProyecto;
import co.edu.unicauca.frontend.infra.config.PdfValidator;
import co.edu.unicauca.frontend.infra.dto.AnteproyectoDTO;
import co.edu.unicauca.frontend.infra.dto.FormatoADTO;
import co.edu.unicauca.frontend.infra.dto.ProyectoDTO;
import co.edu.unicauca.frontend.infra.dto.ProyectoInfoDTO;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

public class ProyectoService implements ObservableService {
    private final String baseUrlProyectos = "http://localhost:8080/api/academic/proyectos";
    private final RestTemplate restTemplate;
    private final List<Observer> observers = new ArrayList<>();

    public ProyectoService() {
        this.restTemplate = new RestTemplate();

        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            String token = SessionManager.getInstance().getAccessToken();
            if (token != null && !token.isBlank()) {
                request.getHeaders().set("Authorization", "Bearer " + token);
            }
            return execution.execute(request, body);
        });
    }

    public EstadoProyecto enforceAutoCancelIfNeeded(UUID proyectoId) {
        String url = baseUrlProyectos + "/" + proyectoId + "/enforceAutoCancel";
        return restTemplate.getForObject(url, EstadoProyecto.class);
    }

    public List<ProyectoInfoDTO> listarProyectosDocente(String correoDocente, String filtro) {
        try {

            String url = baseUrlProyectos + "/docente/{correoDocente}";

            if (filtro != null && !filtro.isEmpty()) {
                url += "?filtro={filtro}";
            }

            Map<String, String> params = new HashMap<>();
            params.put("correoDocente", correoDocente);
            params.put("filtro", filtro);

            ResponseEntity<List<ProyectoInfoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProyectoInfoDTO>>() {},
                    params
            );

            return response.getBody() != null ? response.getBody() : Collections.emptyList();

        } catch (HttpStatusCodeException e) {
            System.err.println("[ProyectoService] Error HTTP al listar proyectos del docente " + correoDocente);
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al consultar los proyectos del docente", e);

        } catch (RestClientException e) {
            System.err.println("[ProyectoService] Error de comunicación con el backend: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servidor", e);
        }
    }
    
    public void crearProyecto(ProyectoDTO proyecto) {
        try {
            /*
            Validacion mepeada al back
            if (!docenteService.docenteTieneCupo(proyecto.getDirector())) {
                throw new IllegalStateException("El docente alcanzó el límite de 7 proyectos en curso");
            }

            Esta validacion ya existia en el back
            if (!estudianteService.estudianteExistePorCorreo(proyecto.getEstudiante())) {
                throw new IllegalArgumentException("El correo no pertenece a un estudiante");
            }

            Validacion mapeada al back
            if (!estudianteService.estudianteLibrePorCorreo(proyecto.getEstudiante())) {
                throw new IllegalStateException("El estudiante ya tiene un proyecto en curso");
            }
             */

            restTemplate.postForEntity(baseUrlProyectos + "/crearProyecto", proyecto, String.class);
            notifyObservers();

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear el proyecto " + e.getMessage(), e);
        }
    }

    public boolean canResubmit(UUID proyectoId) {
        String url = baseUrlProyectos + "/resubmit/" + proyectoId;
        return restTemplate.getForObject(url, Boolean.class);
    }

    public boolean tieneObservacionesFormatoA(UUID proyectoId) {
        String url = baseUrlProyectos + "/observacionesFA/" + proyectoId;
        return restTemplate.getForObject(url, Boolean.class);
    }

    /*Esta validacion ya se hace en el back al insertar un nuevo proyecto
    public boolean existeProyecto(long proyectoId){
        String url = baseUrlProyectos + "/existeProyecto/" + proyectoId;
        return restTemplate.getForObject(url, Boolean.class);
    }
     */
    /*Esta validacion ya se hace en el back al insertar un nuevo proyecto
    public String getEstadoProyecto(long proyectoId){
        String url = baseUrlProyectos + "/estadoProyecto/" + proyectoId;
        return restTemplate.getForObject(url, String.class);
    }
    */

    public int countProyectosByEstadoYTipo(String tipo, String estado, String correo) {
        TipoProyecto tipoEnum = TipoProyecto.valueOf(tipo.toUpperCase().replace(" ", "_"));
        EstadoProyecto estadoEnum = EstadoProyecto.valueOf(estado.toUpperCase().replace(" ", "_"));

        String url = String.format("%s/countProyectosBy?tipoProyecto=%s&estadoProyecto=%s&correoDocente=%s",
                baseUrlProyectos, tipoEnum.name(), estadoEnum.name(), correo);

        try {
            ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
            return response.getBody() != null ? response.getBody() : 0;
        } catch (Exception e) {
            System.err.println("Error al contar proyectos: " + e.getMessage());
            return 0;
        }
    }

    public FormatoADTO subirNuevaVersionFormatoA(UUID proyectoId, FormatoADTO formatoADTO) {
        /*Esta validacion ya se hace en el back al insertar un nuevo proyecto
        if (!existeProyecto(proyectoId))
            throw new IllegalArgumentException("Proyecto no existe");
         */
        /*Esto se valida en el back
        String estado = getEstadoProyecto(proyectoId);
        if (!"EN_TRAMITE".equalsIgnoreCase(estado))
            throw new IllegalStateException("El proyecto no está en curso");
         */
        /*
        - Validacion mapeada al back
        if (max >= 3)
            throw new IllegalStateException("Se alcanzó el máximo de 3 versiones del Formato A");
         */

        PdfValidator.assertPdf(formatoADTO.getNombreFormato(), formatoADTO.getBlob());

        int max = maxVersionFormatoA(proyectoId);

        formatoADTO.setNroVersion(max + 1);
        formatoADTO.setEstado(EstadoFormatoA.PENDIENTE);

        insertarFormatoA(formatoADTO, proyectoId);
        notifyObservers();
        return formatoADTO;
    }

    public int maxVersionFormatoA(UUID id) {
        String url = baseUrlProyectos + "/" + id + "/formatoA/max-version";
        ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
        return response.getBody() != null ? response.getBody() : 0;
    }

    public void insertarFormatoA(FormatoADTO formatoADTO, UUID proyectoId) {
        String url = baseUrlProyectos + "/insertarFormatoAProyecto/" + proyectoId;
        restTemplate.postForEntity(url, formatoADTO, String.class);
    }

    public List<AnteproyectoDTO> listarAnteproyectosDocente(String correo, String filtro) {
        String url = baseUrlProyectos + "/docente/" + correo + "/anteproyectos";

        if (filtro != null && !filtro.isEmpty()) {
            url += "?filtro=" + filtro;
        }

        ResponseEntity<List<AnteproyectoDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AnteproyectoDTO>>() {
                }
        );
        return response.getBody();
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }

    public AnteproyectoDTO obtenerAnteproyecto(UUID proyectoId) {
        String url = baseUrlProyectos + "/" + proyectoId + "/anteproyecto";

        try {
            ResponseEntity<AnteproyectoDTO> response = restTemplate.getForEntity(url, AnteproyectoDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("No se encontró anteproyecto para el proyecto con ID: " + proyectoId);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener el anteproyecto: " + ex.getMessage(), ex);
        }
    }

    public FormatoADTO obtenerUltimoFormatoAConObservaciones(UUID proyectoId) {
        String url = baseUrlProyectos + "/ultimoFormatoAConObservaciones" + "/" + proyectoId;
        try {
            ResponseEntity<FormatoADTO> response = restTemplate.getForEntity(url, FormatoADTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("No se encontró un Formato A observado para el proyecto con ID: " + proyectoId);
        } catch (Exception ex) {
            throw new RuntimeException("Error al obtener el Formato A observado: " + ex.getMessage(), ex);
        }
    }
}
