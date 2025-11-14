package Ziaat.E_library.Services;

import Ziaat.E_library.Utils.UserContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MicroserviceClient {

    @Value("${gateway.url}")
    private String gatewayUrl;

    private final WebClient webClient;
    private final UserContextUtil userContextUtil;

    public MicroserviceClient(WebClient.Builder webClientBuilder, UserContextUtil userContextUtil) {
        this.webClient = webClientBuilder.baseUrl(gatewayUrl).build();
        this.userContextUtil = userContextUtil;
    }

    public <T> T getFromService(String endpoint, Class<T> responseType) {
        String token = userContextUtil.getCurrentToken();

        return webClient.get()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <T, R> R postToService(String endpoint, T body, Class<R> responseType) {
        String token = userContextUtil.getCurrentToken();

        return webClient.post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public <T, R> R putToService(String endpoint, T body, Class<R> responseType) {
        String token = userContextUtil.getCurrentToken();

        return webClient.put()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public void deleteFromService(String endpoint) {
        String token = userContextUtil.getCurrentToken();

        webClient.delete()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}