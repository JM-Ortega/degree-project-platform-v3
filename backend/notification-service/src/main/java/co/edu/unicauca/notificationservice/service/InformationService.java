package co.edu.unicauca.notificationservice.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class InformationService {
    private final String baseUrlAuth;
    private final RestTemplate restTemplate;

    public InformationService(){
        this.restTemplate = new RestTemplate();
        this.baseUrlAuth = "http://localhost:8080/api/auth";
    }

    public  String getEmailCoordinador(String programa){
        String url = baseUrlAuth + "/" + programa;
        return restTemplate.getForObject(url, String.class);
    }

    public String getEmailJefeDepartamento (String departamento){
        String url = baseUrlAuth + "/" + departamento;
        return restTemplate.getForObject(url, String.class);
    }

    public String getTelefono(String correo) {
        String url = baseUrlAuth + "/telefono?correo=" + correo;
        return restTemplate.getForObject(url, String.class);
    }
}
