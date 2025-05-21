package cl.ferremas.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

@Service
public class TipoCambioService {

    private final String url = "https://mindicador.cl/api/dolar";

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
}
