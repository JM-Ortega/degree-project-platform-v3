package co.edu.unicauca.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO interno que representa la respuesta del endpoint
 * /protocol/openid-connect/token de Keycloak.
 *
 * <p>Incluye únicamente los campos que el backend utiliza;
 * cualquier campo adicional que envíe Keycloak es ignorado.</p>
 */
@Schema(description = "Respuesta cruda del token endpoint de Keycloak (uso interno).")
@JsonIgnoreProperties(ignoreUnknown = true)
public record KeycloakTokenResponse(

        @JsonProperty("access_token")
        @Schema(description = "Access token JWT emitido por Keycloak.")
        String accessToken,

        @JsonProperty("refresh_token")
        @Schema(description = "Refresh token para obtener nuevos access tokens.")
        String refreshToken,

        @JsonProperty("expires_in")
        @Schema(description = "Tiempo de expiración del access token, en segundos.", example = "300")
        Long expiresIn,

        @JsonProperty("token_type")
        @Schema(description = "Tipo de token, normalmente 'Bearer'.", example = "Bearer")
        String tokenType
) {
}