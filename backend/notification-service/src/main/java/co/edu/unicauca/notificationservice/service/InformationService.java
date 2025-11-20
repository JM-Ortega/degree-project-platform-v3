package co.edu.unicauca.notificationservice.service;

import org.springframework.web.client.RestTemplate;

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
}
