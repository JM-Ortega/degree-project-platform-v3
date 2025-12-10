package co.edu.unicauca.frontend.services.academic;

import co.edu.unicauca.frontend.dto.SessionInfo;
import co.edu.unicauca.frontend.infra.dto.ProyectoEstudianteDTO;
import co.edu.unicauca.frontend.infra.session.SessionData;
import co.edu.unicauca.frontend.infra.session.SessionManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class ProyectoEstudianteService {

    private static final String BASE_URL = "https://api.narvaezlab.dev/api/academic/proyectos";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public ProyectoEstudianteService() {
        mapper = new ObjectMapper();
        // Modulo que permite a Jackson leer LocalDate / LocalDateTime
        mapper.registerModule(new JavaTimeModule());
    }

    public List<ProyectoEstudianteDTO> obtenerProyectosEstudiante() {
        try {
            // Obtener SessionData → SessionInfo
            SessionData data = SessionManager.getInstance().getCurrentSession();
            SessionInfo estudiante = (data != null) ? data.getSessionInfo() : null;

            if (estudiante == null) {
                System.err.println("No hay sesión activa");
                return List.of();
            }

            String correo = estudiante.email();

            // Obtener el JWT
            String token = SessionManager.getInstance().getAccessToken();

            HttpRequest.Builder rb = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/listar/" + correo))
                    .GET();

            // Agregar token si existe
            if (token != null && !token.isBlank()) {
                rb.header("Authorization", "Bearer " + token);
            }

            HttpRequest request = rb.build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            List<ProyectoEstudianteJsonDTO> jsonList = mapper.readValue(
                    response.body(),
                    new TypeReference<List<ProyectoEstudianteJsonDTO>>() {
                    }
            );

            return jsonList.stream()
                    .map(json -> new ProyectoEstudianteDTO(
                            json.getId(),
                            json.getTitulo(),
                            json.getNombreDirector(),
                            json.getTipoProyecto(),
                            json.getEstadoProyecto()
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


}
