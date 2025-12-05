package co.edu.unicauca.notificationservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio que consulta datos de contacto (correo y teléfono)
 * desde el servicio de autenticación.
 */
@Service
public class InformationService {
    /** URL base del servicio de autenticación. */
    private final String baseUrlAuth;

    /** Cliente HTTP usado para realizar las solicitudes. */
    private final RestTemplate restTemplate;

    /**
     * Construye el servicio inicializando el cliente HTTP y la URL base.
     */
    public InformationService(){
        this.restTemplate = new RestTemplate();
        this.baseUrlAuth = "http://localhost:8080/api/auth";
    }

    /**
     * Obtiene el correo del coordinador asociado a un programa.
     *
     * @param programa nombre o código del programa
     * @return correo del coordinador, o null si no existe
     */
    public  String getEmailCoordinador(String programa){
        String url = baseUrlAuth + "/coordinador/" + programa;
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Obtiene el correo del jefe de departamento.
     *
     * @param departamento nombre o código del departamento
     * @return correo del jefe de departamento, o null si no existe
     */
    public String getEmailJefeDepartamento (String departamento){
        String url = baseUrlAuth + "/jefe/" + departamento;
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * Obtiene el número de teléfono asociado a un correo.
     *
     * @param correo correo de la persona
     * @return número de teléfono, o null si no está registrado
     */
    public String getTelefono(String correo) {
        String url = baseUrlAuth + "/telefono?correo=" + correo;
        return restTemplate.getForObject(url, String.class);
    }
}
