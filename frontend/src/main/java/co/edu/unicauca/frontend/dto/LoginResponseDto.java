package co.edu.unicauca.frontend.dto;

import java.util.List;

/**
 * Respuesta del backend tras un inicio de sesión exitoso.
 *
 * Incluye tokens OIDC entregados por Keycloak y la información
 * de sesión interpretada por el backend.
 */
public record LoginResponseDto(

        String accessToken,

        String refreshToken,

        Long expiresIn,

        String tokenType,

        SessionInfo session,

        List<String> roles

) { }
