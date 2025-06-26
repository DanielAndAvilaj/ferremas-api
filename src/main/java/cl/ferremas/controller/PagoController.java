package cl.ferremas.controller;

import cl.ferremas.dto.PagoRequest;
import cl.ferremas.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    private final PagoService pagoService;
    private final DecimalFormat formatoPesos = new DecimalFormat("#,###");

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /**
     * Inicia una transacción en Webpay Plus (versión simple)
     * @param body JSON con el monto a pagar (ej: {"monto": 1000})
     * @return Respuesta con URL y token
     */
    @PostMapping("/iniciar")
    public Mono<ResponseEntity<Map<String, Object>>> iniciarPago(@RequestBody Map<String, Object> body) {
        try {
            Double monto = Double.valueOf(body.get("monto").toString());
            
            // Validaciones antes de enviar a Webpay
            Map<String, Object> validacion = validarMonto(monto);
            if (!((Boolean) validacion.get("valido"))) {
                return Mono.just(ResponseEntity.badRequest().body(validacion));
            }
            
            return pagoService.iniciarTransaccion(monto)
                    .map(ResponseEntity::ok)
                    .onErrorResume(error -> {
                        Map<String, Object> errorResponse = manejarErrorWebpay(error);
                        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
                    });
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("mensaje", "Error al procesar el monto. Verifique que sea un número válido.");
            errorResponse.put("detalle", e.getMessage());
            return Mono.just(ResponseEntity.badRequest().body(errorResponse));
        }
    }

    /**
     * Inicia una transacción completa con más datos
     * @param pagoRequest Datos completos del pago
     * @return Respuesta con URL y token
     */
    @PostMapping("/iniciar-completo")
    public Mono<ResponseEntity<Map<String, Object>>> iniciarPagoCompleto(@RequestBody PagoRequest pagoRequest) {
        // Validaciones
        Map<String, Object> validacion = validarMonto(pagoRequest.getMonto());
        if (!((Boolean) validacion.get("valido"))) {
            return Mono.just(ResponseEntity.badRequest().body(validacion));
        }
        
        return pagoService.iniciarTransaccionCompleta(
                pagoRequest.getMonto(),
                pagoRequest.getDescripcion(),
                pagoRequest.getEmailCliente()
        ).map(ResponseEntity::ok)
         .onErrorResume(error -> {
             Map<String, Object> errorResponse = manejarErrorWebpay(error);
             return Mono.just(ResponseEntity.badRequest().body(errorResponse));
         });
    }

    /**
     * Valida que el monto sea correcto para Webpay
     */
    private Map<String, Object> validarMonto(Double monto) {
        Map<String, Object> response = new HashMap<>();
        
        if (monto == null) {
            response.put("valido", false);
            response.put("error", true);
            response.put("mensaje", "❌ El monto es requerido");
            response.put("detalle", "Debe especificar un monto válido para el pago");
            return response;
        }
        
        if (monto <= 0) {
            response.put("valido", false);
            response.put("error", true);
            response.put("mensaje", "❌ No se pueden generar pagos con montos negativos o cero");
            response.put("detalle", String.format("El monto ingresado ($%s CLP) debe ser mayor a cero", formatoPesos.format(monto)));
            return response;
        }
        
        if (monto < 50) {
            response.put("valido", false);
            response.put("error", true);
            response.put("mensaje", "❌ El monto mínimo para pagos es de $50 CLP");
            response.put("detalle", String.format("El monto ingresado ($%s CLP) es menor al mínimo permitido", formatoPesos.format(monto)));
            return response;
        }
        
        if (monto > 999999999) {
            response.put("valido", false);
            response.put("error", true);
            response.put("mensaje", "❌ El monto excede el límite máximo permitido");
            response.put("detalle", String.format("El monto ingresado ($%s CLP) supera el límite de $999,999,999 CLP", formatoPesos.format(monto)));
            return response;
        }
        
        response.put("valido", true);
        return response;
    }

    /**
     * Maneja errores específicos de Webpay
     */
    private Map<String, Object> manejarErrorWebpay(Throwable error) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        
        String errorMessage = error.getMessage();
        
        if (errorMessage.contains("422")) {
            errorResponse.put("mensaje", "❌ Datos de pago inválidos");
            errorResponse.put("detalle", "Webpay rechazó la transacción. Verifique que el monto sea positivo y válido.");
            errorResponse.put("codigo_error", "WEBPAY_422");
        } else if (errorMessage.contains("400")) {
            errorResponse.put("mensaje", "❌ Solicitud mal formada");
            errorResponse.put("detalle", "Los datos enviados a Webpay no tienen el formato correcto.");
            errorResponse.put("codigo_error", "WEBPAY_400");
        } else if (errorMessage.contains("401")) {
            errorResponse.put("mensaje", "❌ Error de autenticación");
            errorResponse.put("detalle", "Credenciales de Webpay inválidas.");
            errorResponse.put("codigo_error", "WEBPAY_401");
        } else if (errorMessage.contains("timeout") || errorMessage.contains("connect")) {
            errorResponse.put("mensaje", "❌ Error de conexión");
            errorResponse.put("detalle", "No se pudo conectar con Webpay. Intente nuevamente.");
            errorResponse.put("codigo_error", "WEBPAY_TIMEOUT");
        } else {
            errorResponse.put("mensaje", "❌ Error inesperado al procesar el pago");
            errorResponse.put("detalle", "Ocurrió un error no esperado. Contacte al soporte técnico.");
            errorResponse.put("codigo_error", "WEBPAY_UNKNOWN");
        }
        
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        return errorResponse;
    }

    /**
     * Confirma una transacción luego de retorno desde Webpay (GET)
     * @param token Token entregado por Webpay (token_ws)
     * @return Página HTML con resultado de la transacción
     */
    @GetMapping("/confirmar")
    public Mono<ResponseEntity<String>> confirmarPagoGet(@RequestParam("token_ws") String token) {
        return pagoService.confirmarTransaccion(token)
                .map(this::generarPaginaConfirmacion)
                .map(html -> ResponseEntity.ok().header("Content-Type", "text/html").body(html));
    }

    /**
     * Confirma una transacción luego de retorno desde Webpay (POST)
     * @param token Token entregado por Webpay (token_ws)
     * @return Página HTML con resultado de la transacción
     */
    @PostMapping("/confirmar")
    public Mono<ResponseEntity<String>> confirmarPagoPost(@RequestParam("token_ws") String token) {
        return pagoService.confirmarTransaccion(token)
                .map(this::generarPaginaConfirmacion)
                .map(html -> ResponseEntity.ok().header("Content-Type", "text/html").body(html));
    }

    /**
     * Endpoint para obtener solo los datos JSON (útil para debugging)
     */
    @GetMapping("/confirmar/json")
    public Mono<ResponseEntity<Map<String, Object>>> confirmarPagoJson(@RequestParam("token_ws") String token) {
        return pagoService.confirmarTransaccion(token)
                .map(ResponseEntity::ok);
    }

    private String generarPaginaConfirmacion(Map<String, Object> response) {
        String status = (String) response.get("status");
        boolean esExitoso = "AUTHORIZED".equals(status);
        
        // El monto ya viene en pesos desde Webpay (NO hay que dividir por 100)
        Object amountObj = response.get("amount");
        double montoPesos = 0;
        if (amountObj instanceof Integer) {
            montoPesos = ((Integer) amountObj).doubleValue();
        } else if (amountObj instanceof Double) {
            montoPesos = (Double) amountObj;
        }
        
        String buyOrder = (String) response.get("buy_order");
        String authorizationCode = (String) response.get("authorization_code");
        String cardNumber = "";
        
        if (response.containsKey("card_detail") && response.get("card_detail") instanceof Map) {
            Map<String, Object> cardDetail = (Map<String, Object>) response.get("card_detail");
            cardNumber = (String) cardDetail.get("card_number");
        }
        
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        
        if (esExitoso) {
            return generarPaginaExito(montoPesos, buyOrder, authorizationCode, cardNumber, fechaHora);
        } else {
            return generarPaginaError(montoPesos, buyOrder, status, fechaHora);
        }
    }

    private String generarPaginaExito(double monto, String ordenCompra, String codigoAutorizacion, String tarjeta, String fechaHora) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Pago Exitoso - Ferremas</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            padding: 20px;
                        }
                        .container {
                            background: white;
                            border-radius: 20px;
                            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                            padding: 40px;
                            max-width: 500px;
                            width: 100%%;
                            text-align: center;
                            animation: slideUp 0.6s ease-out;
                        }
                        @keyframes slideUp {
                            from { transform: translateY(30px); opacity: 0; }
                            to { transform: translateY(0); opacity: 1; }
                        }
                        .success-icon {
                            width: 80px;
                            height: 80px;
                            background: #4CAF50;
                            border-radius: 50%%;
                            margin: 0 auto 20px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 40px;
                            color: white;
                            animation: pulse 1s ease-in-out;
                        }
                        @keyframes pulse {
                            0%% { transform: scale(0.8); }
                            50%% { transform: scale(1.1); }
                            100%% { transform: scale(1); }
                        }
                        h1 {
                            color: #2E7D32;
                            margin-bottom: 10px;
                            font-size: 28px;
                        }
                        .subtitle {
                            color: #666;
                            margin-bottom: 30px;
                            font-size: 16px;
                        }
                        .detail-card {
                            background: #f8f9fa;
                            border-radius: 12px;
                            padding: 20px;
                            margin: 20px 0;
                            text-align: left;
                        }
                        .detail-row {
                            display: flex;
                            justify-content: space-between;
                            margin: 10px 0;
                            padding: 8px 0;
                            border-bottom: 1px solid #e0e0e0;
                        }
                        .detail-row:last-child {
                            border-bottom: none;
                            font-weight: bold;
                            color: #2E7D32;
                        }
                        .label {
                            color: #666;
                            font-weight: 500;
                        }
                        .value {
                            font-weight: 600;
                            color: #333;
                        }
                        .btn-container {
                            margin-top: 30px;
                        }
                        .btn {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: white;
                            border: none;
                            padding: 12px 30px;
                            border-radius: 25px;
                            font-size: 16px;
                            cursor: pointer;
                            transition: transform 0.2s;
                            text-decoration: none;
                            display: inline-block;
                            margin: 0 10px;
                        }
                        .btn:hover {
                            transform: translateY(-2px);
                        }
                        .ferremas-logo {
                            color: #667eea;
                            font-size: 20px;
                            font-weight: bold;
                            margin-top: 20px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="success-icon">✓</div>
                        <h1>¡Pago Exitoso!</h1>
                        <p class="subtitle">Tu transacción ha sido procesada correctamente</p>
                        
                        <div class="detail-card">
                            <div class="detail-row">
                                <span class="label">💳 Tarjeta terminada en:</span>
                                <span class="value">**** %s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">🆔 Orden de compra:</span>
                                <span class="value">%s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">🔐 Código autorización:</span>
                                <span class="value">%s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">📅 Fecha y hora:</span>
                                <span class="value">%s</span>
                            </div>
                            <div class="detail-row">
                                <span class="label">💰 Monto pagado:</span>
                                <span class="value">$%s CLP</span>
                            </div>
                        </div>
                        
                        <div class="btn-container">
                            <a href="/" class="btn">🏠 Volver al inicio</a>
                            <a href="javascript:window.print()" class="btn">🖨️ Imprimir comprobante</a>
                        </div>
                        
                        <div class="ferremas-logo">⚡ FERREMAS</div>
                    </div>
                </body>
                </html>
                """.formatted(tarjeta, ordenCompra, codigoAutorizacion, fechaHora, formatoPesos.format(monto));
    }

    private String generarPaginaError(double monto, String ordenCompra, String status, String fechaHora) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Error en el Pago - Ferremas</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background: linear-gradient(135deg, #ff6b6b 0%%, #ee5a24 100%%);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            padding: 20px;
                        }
                        .container {
                            background: white;
                            border-radius: 20px;
                            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                            padding: 40px;
                            max-width: 500px;
                            width: 100%%;
                            text-align: center;
                        }
                        .error-icon {
                            width: 80px;
                            height: 80px;
                            background: #f44336;
                            border-radius: 50%%;
                            margin: 0 auto 20px;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-size: 40px;
                            color: white;
                        }
                        h1 { color: #d32f2f; margin-bottom: 10px; }
                        .btn {
                            background: #f44336;
                            color: white;
                            border: none;
                            padding: 12px 30px;
                            border-radius: 25px;
                            font-size: 16px;
                            cursor: pointer;
                            text-decoration: none;
                            display: inline-block;
                            margin: 20px 10px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="error-icon">✗</div>
                        <h1>Error en el Pago</h1>
                        <p>Lo sentimos, no se pudo procesar tu pago.</p>
                        <p><strong>Estado:</strong> %s</p>
                        <p><strong>Orden:</strong> %s</p>
                        <p><strong>Fecha:</strong> %s</p>
                        <a href="/" class="btn">🏠 Volver al inicio</a>
                        <a href="/api/pago/iniciar" class="btn">🔄 Intentar nuevamente</a>
                    </div>
                </body>
                </html>
                """.formatted(status, ordenCompra, fechaHora);
    }
}