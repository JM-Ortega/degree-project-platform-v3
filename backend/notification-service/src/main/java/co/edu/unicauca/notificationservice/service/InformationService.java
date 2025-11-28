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
        this.baseUrlAuth = "http://localhost:8080/api/auth/information";
    }

    public  String getEmailCoordinador(String programa){
        String url = baseUrlAuth + "/" + programa;
        return restTemplate.getForObject(url, String.class);
    }

    public List<String> getTelefonos(List<String> correos) {
        String url = baseUrlAuth + "/telefonos";

        // Usamos UriComponentsBuilder para agregar la lista como query params
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        for (String correo : correos) {
            builder.queryParam("correos", correo);
        }

        String finalUrl = builder.toUriString();

        // Hacemos la petición GET esperando una lista de Strings
        ResponseEntity<List<String>> response = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {}
        );

        return response.getBody();
    }

}
