package co.edu.unicauca.frontend.services;

import co.edu.unicauca.frontend.entities.EstadoArchivo;
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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class ProyectoService implements ObservableService{
    private final String baseUrlProyectos = "http://localhost:8080/api/academic/proyectos";
    private final RestTemplate restTemplate;
    private final List<Observer> observers = new ArrayList<>();
    private final DocenteService docenteService;
    private final EstudianteService estudianteService;

    public ProyectoService(DocenteService docenteService, EstudianteService estudianteService) {
        this.restTemplate = new RestTemplate();

        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            String token = SessionManager.getInstance().getAccessToken();
            if (token != null && !token.isBlank()) {
                request.getHeaders().set("Authorization", "Bearer " + token);
            }
            return execution.execute(request, body);
        });

        this.docenteService = docenteService;
        this.estudianteService = estudianteService;
    }


    public EstadoProyecto enforceAutoCancelIfNeeded(long proyectoId) {
        String url = baseUrlProyectos + "/" + proyectoId + "/enforceAutoCancel";
        return restTemplate.getForObject(url, EstadoProyecto.class);
    }

    public List<ProyectoInfoDTO> listarProyectosDocente(String correoDocente, String filtro) {
        String url = baseUrlProyectos + "/docente/" + correoDocente;

        if (filtro != null && !filtro.isEmpty()) {
            url += "?filtro=" + filtro;
        }

        ResponseEntity<List<ProyectoInfoDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ProyectoInfoDTO>>() {}
        );

        return response.getBody();
    }

    public void crearProyecto(ProyectoDTO proyecto) {
        try {
            if (!docenteService.docenteTieneCupo(proyecto.getDirector())) {
                throw new IllegalStateException("El docente alcanzó el límite de 7 proyectos en curso");
            }

            if (!estudianteService.estudianteExistePorCorreo(proyecto.getEstudiante())) {
                throw new IllegalArgumentException("El correo no pertenece a un estudiante");
            }

            if (!estudianteService.estudianteLibrePorCorreo(proyecto.getEstudiante())) {
                throw new IllegalStateException("El estudiante ya tiene un proyecto en curso");
            }

            restTemplate.postForEntity(baseUrlProyectos + "/crearConArchivos", proyecto, String.class);
            notifyObservers();

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al crear el proyecto "+ e.getMessage(), e);
        }
    }

    public int maxVersionFormatoA(long id) {
        String url = baseUrlProyectos + "/" + id + "/formatoA/max-version";
        ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
        return response.getBody() != null ? response.getBody() : 0;
    }

    public boolean canResubmit(long proyectoId) {
        String url = baseUrlProyectos + "/resubmit/" + proyectoId;
        return restTemplate.getForObject(url, Boolean.class);
    }

    public boolean tieneObservacionesFormatoA(long proyectoId) {
        String url = baseUrlProyectos + "/observacionesFA/" + proyectoId;
        return restTemplate.getForObject(url, Boolean.class);
    }

    public boolean existeProyecto(long proyectoId){
        String url = baseUrlProyectos + "/existeProyecto/" + proyectoId;
        return restTemplate.getForObject(url, Boolean.class);
    }

    public String getEstadoProyecto(long proyectoId){
        String url = baseUrlProyectos + "/estadoProyecto/" + proyectoId;
        return restTemplate.getForObject(url, String.class);
    }

    public void insertarFormatoA(FormatoADTO formatoADTO, long proyectoId){
        String url = baseUrlProyectos + "/insertarFormatoAProyecto/" + proyectoId;
        restTemplate.postForEntity(url, formatoADTO, String.class);
    }

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

    public FormatoADTO subirNuevaVersionFormatoA(long proyectoId, FormatoADTO formatoADTO){
        if (!existeProyecto(proyectoId))
            throw new IllegalArgumentException("Proyecto no existe");

        String estado = getEstadoProyecto(proyectoId);
        if (!"EN_TRAMITE".equalsIgnoreCase(estado))
            throw new IllegalStateException("El proyecto no está en curso");

        int max = maxVersionFormatoA(proyectoId);
        if (max >= 3)
            throw new IllegalStateException("Se alcanzó el máximo de 3 versiones del Formato A");

        PdfValidator.assertPdf(formatoADTO.getNombreFormato(), formatoADTO.getBlob());

        formatoADTO.setNroVersion(max + 1);
        formatoADTO.setEstado(EstadoArchivo.PENDIENTE);

        insertarFormatoA(formatoADTO, proyectoId);
        notifyObservers();
        return formatoADTO;
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
                new ParameterizedTypeReference<List<AnteproyectoDTO>>() {}
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

    public AnteproyectoDTO obtenerAnteproyecto(long proyectoId) {
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

    public FormatoADTO obtenerUltimoFormatoAConObservaciones(long proyectoId) {
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
