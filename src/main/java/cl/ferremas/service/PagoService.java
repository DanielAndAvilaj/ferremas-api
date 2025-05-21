package cl.ferremas.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PagoService {

    private final WebClient webClient;

    // Constantes para ambiente de integración (API REST oficial)
    private static final String BASE_URL = "https://webpay3gint.transbank.cl/rswebpaytransaction/api/webpay/v1.0";
    private static final String COMMERCE_CODE = "597055555532";
    private static final String API_KEY = "579B532A7440BB0C9079DED94D31EA1615BACEB56610332264630D42D0A36B1C";
    private static final String RETURN_URL = "http://localhost:8080/api/pago/confirmar";

    public PagoService() {
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public Mono<Map<String, Object>> iniciarTransaccion(Double monto) {
    String buyOrder = UUID.randomUUID().toString().substring(0, 26);
    String sessionId = UUID.randomUUID().toString().substring(0, 26);

    Map<String, Object> payload = new HashMap<>();
    payload.put("buy_order", buyOrder);
    payload.put("session_id", sessionId);
    payload.put("amount", monto);
    payload.put("return_url", RETURN_URL);

    return webClient.post()
            .uri("/transactions")
            .header("Tbk-Api-Key-Id", COMMERCE_CODE)
            .header("Tbk-Api-Key-Secret", API_KEY)
            .bodyValue(payload)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .map(response -> {
                String token = (String) response.get("token");
                String url = (String) response.get("url");
                response.put("full_url", url + "?token_ws=" + token);
                return response;
            });
}


    public Mono<Map<String, Object>> confirmarTransaccion(String token) {
        return webClient.put()
                .uri("/transactions/{token}", token)
                .header("Tbk-Api-Key-Id", COMMERCE_CODE)
                .header("Tbk-Api-Key-Secret", API_KEY)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
