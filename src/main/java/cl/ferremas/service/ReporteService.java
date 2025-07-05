package cl.ferremas.service;

import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Servicio para la lógica de negocio de reportes (simulado).
 */
@Service
public class ReporteService {
    // TODO: Inyectar servicios reales de ventas, productos, stock, usuarios, divisas

    /**
     * Retorna datos simulados de ventas por periodo.
     */
    public Map<String, Object> ventasPorPeriodo(Date desde, Date hasta) {
        // Simulación: lista de fechas y montos
        Map<String, Object> data = new HashMap<>();
        data.put("labels", List.of("2024-06-01","2024-06-02","2024-06-03"));
        data.put("montos", List.of(100000, 150000, 120000));
        return data;
    }

    /**
     * Retorna productos más vendidos (simulado).
     */
    public Map<String, Object> productosMasVendidos(Date desde, Date hasta) {
        Map<String, Object> data = new HashMap<>();
        data.put("labels", List.of("Martillo","Taladro","Serrucho"));
        data.put("cantidades", List.of(30, 22, 15));
        return data;
    }

    /**
     * Retorna stock por sucursal (simulado).
     */
    public Map<String, Object> stockPorSucursal() {
        Map<String, Object> data = new HashMap<>();
        data.put("labels", List.of("Sucursal 1","Sucursal 2","Sucursal 3"));
        data.put("stock", List.of(120, 80, 60));
        return data;
    }

    /**
     * Retorna actividad de usuarios (simulado).
     */
    public Map<String, Object> actividadUsuarios(Date desde, Date hasta) {
        Map<String, Object> data = new HashMap<>();
        data.put("labels", List.of("Juan","Ana","Pedro"));
        data.put("acciones", List.of(12, 8, 5));
        return data;
    }

    /**
     * Retorna conversiones de divisa más consultadas (simulado).
     */
    public Map<String, Object> conversionesDivisaMasConsultadas(Date desde, Date hasta) {
        Map<String, Object> data = new HashMap<>();
        data.put("labels", List.of("CLP→USD","USD→CLP"));
        data.put("consultas", List.of(40, 25));
        return data;
    }
} 