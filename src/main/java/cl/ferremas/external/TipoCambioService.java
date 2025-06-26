package cl.ferremas.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TipoCambioService {

    private final String url = "https://mindicador.cl/api/dolar";
    private final DecimalFormat formatoPesos = new DecimalFormat("#,###");
    private final DecimalFormat formatoDolares = new DecimalFormat("#,##0.00");

    public Double obtenerValorDolar() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject json = new JSONObject(response.getBody());
        return json.getJSONArray("serie").getJSONObject(0).getDouble("valor");
    }

    public Double convertirAPesos(Double montoEnDolares) {
        Double valorDolar = obtenerValorDolar();
        return montoEnDolares * valorDolar;
    }

    public Double convertirADolares(Double montoEnPesos) {
        Double valorDolar = obtenerValorDolar();
        return montoEnPesos / valorDolar;
    }

    public String convertirPesosADolaresConMensaje(Double montoEnPesos, String nombreProducto) {
        try {
            Double valorDolar = obtenerValorDolar();
            Double montoEnDolares = montoEnPesos / valorDolar;
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            return String.format(
                "💰 Conversión de Precio - %s\n" +
                "📅 Fecha: %s\n" +
                "🏪 Producto: %s\n" +
                "💵 Precio en Pesos: $%s CLP\n" +
                "💲 Precio en Dólares: USD $%s\n" +
                "📊 Tipo de cambio: $%s CLP por USD\n" +
                "ℹ️  Fuente: Mindicador.cl (Datos del Banco Central de Chile)",
                nombreProducto != null ? nombreProducto : "Producto",
                fechaActual,
                nombreProducto != null ? nombreProducto : "No especificado",
                formatoPesos.format(montoEnPesos),
                formatoDolares.format(montoEnDolares),
                formatoPesos.format(valorDolar)
            );
        } catch (Exception e) {
            return "❌ Error al obtener el tipo de cambio. Por favor, intente nuevamente.";
        }
    }

    public String convertirDolaresAPesosConMensaje(Double montoEnDolares, String nombreProducto) {
        try {
            Double valorDolar = obtenerValorDolar();
            Double montoEnPesos = montoEnDolares * valorDolar;
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            return String.format(
                "💰 Conversión de Precio - %s\n" +
                "📅 Fecha: %s\n" +
                "🏪 Producto: %s\n" +
                "💲 Precio en Dólares: USD $%s\n" +
                "💵 Precio en Pesos: $%s CLP\n" +
                "📊 Tipo de cambio: $%s CLP por USD\n" +
                "ℹ️  Fuente: Mindicador.cl (Datos del Banco Central de Chile)",
                nombreProducto != null ? nombreProducto : "Producto",
                fechaActual,
                nombreProducto != null ? nombreProducto : "No especificado",
                formatoDolares.format(montoEnDolares),
                formatoPesos.format(montoEnPesos),
                formatoPesos.format(valorDolar)
            );
        } catch (Exception e) {
            return "❌ Error al obtener el tipo de cambio. Por favor, intente nuevamente.";
        }
    }

    public String obtenerTipoCambioActual() {
        try {
            Double valorDolar = obtenerValorDolar();
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            return String.format(
                "📊 Tipo de Cambio Actual\n" +
                "📅 Fecha: %s\n" +
                "💲 1 USD = $%s CLP\n" +
                "ℹ️  Fuente: Mindicador.cl (Datos del Banco Central de Chile)",
                fechaActual,
                formatoPesos.format(valorDolar)
            );
        } catch (Exception e) {
            return "❌ Error al obtener el tipo de cambio. Por favor, intente nuevamente.";
        }
    }
}