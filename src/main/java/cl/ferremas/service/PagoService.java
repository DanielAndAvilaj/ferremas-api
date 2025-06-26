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

    // Inicia la transacción con datos personalizables
    public Mono<Map<String, Object>> iniciarTransaccion(Double monto) {
        return iniciarTransaccionCompleta(monto, null, null);
    }

    // Versión completa que permite personalizar más datos
    public Mono<Map<String, Object>> iniciarTransaccionCompleta(Double monto, String descripcion, String emailCliente) {
        String buyOrder = "FERREMAS-" + System.currentTimeMillis(); // Más descriptivo
        String sessionId = UUID.randomUUID().toString().substring(0, 26);

        // IMPORTANTE: En Chile, Webpay espera montos en pesos, NO en centavos
        // Si envías 25990, debe cobrar $25,990 CLP
        int montoEnCentavos = monto.intValue(); // SIN multiplicar por 100

        Map<String, Object> payload = new HashMap<>();
        payload.put("buy_order", buyOrder);
        payload.put("session_id", sessionId);
        payload.put("amount", montoEnCentavos);
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
                    
                    // Agregar datos adicionales para tracking
                    response.put("full_url", url + "?token_ws=" + token);
                    response.put("monto_original", monto);
                    response.put("descripcion", descripcion != null ? descripcion : "Compra en Ferremas");
                    response.put("email_cliente", emailCliente);
                    
                    return response;
                });
    }

    // Confirma la transacción con el token recibido
    public Mono<Map<String, Object>> confirmarTransaccion(String token) {
        return webClient.put()
                .uri("/transactions/{token}", token)
                .header("Tbk-Api-Key-Id", COMMERCE_CODE)
                .header("Tbk-Api-Key-Secret", API_KEY)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(response -> {
                    // Enriquecer la respuesta con datos calculados
                    Object amountObj = response.get("amount");
                    if (amountObj != null) {
                        double montoPesos = 0;
                        if (amountObj instanceof Integer) {
                            montoPesos = ((Integer) amountObj).doubleValue(); // Ya está en pesos
                        } else if (amountObj instanceof Double) {
                            montoPesos = (Double) amountObj; // Ya está en pesos
                        }
                        response.put("monto_pesos", montoPesos);
                    }
                    
                    // Agregar información de estado más amigable
                    String status = (String) response.get("status");
                    response.put("es_exitoso", "AUTHORIZED".equals(status));
                    response.put("mensaje_estado", getMensajeEstado(status));
                    
                    return response;
                });
    }

    private String getMensajeEstado(String status) {
        return switch (status != null ? status : "UNKNOWN") {
            case "AUTHORIZED" -> "Pago autorizado exitosamente";
            case "FAILED" -> "Pago rechazado";
            case "NULLIFIED" -> "Pago anulado";
            case "PARTIALLY_NULLIFIED" -> "Pago parcialmente anulado";
            case "CAPTURED" -> "Pago capturado";
            case "REVERSED" -> "Pago reversado";
            default -> "Estado desconocido: " + status;
        };
    }
}