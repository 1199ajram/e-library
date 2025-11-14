package Ziaat.E_library.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class JwtTokenValidator {

    @Value("${gateway.auth.verify}")
    private String verifyUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtTokenValidator(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Map<String, Object> validateAndDecodeToken(String token) {
        try {
            // Option 1: Decode locally (fast but limited info)
            Map<String, Object> claims = decodeTokenLocally(token);

            // Option 2: Verify with auth service (slower but complete info)
            // Uncomment if you need full user details from auth service
            // return verifyWithAuthService(token);

            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> decodeTokenLocally(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }

        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        Map<String, Object> claims = objectMapper.readValue(payload, Map.class);

        // Check expiration
        Long exp = ((Number) claims.get("exp")).longValue();
        if (exp < System.currentTimeMillis() / 1000) {
            return null;
        }

        return claims;
    }

    private Map<String, Object> verifyWithAuthService(String token) {
        try {
            Map<String, Object> response = webClient.post()
                    .uri(verifyUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.get("code").equals(200)) {
                return (Map<String, Object>) response.get("body");
            }
        } catch (Exception e) {
            // Log error
        }
        return null;
    }
}