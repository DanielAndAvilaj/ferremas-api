package cl.ferremas.dto;

import java.time.LocalDateTime;

public class PrecioRequest {
    
    private LocalDateTime fecha;
    private Double valor;
    private Long productoId;

    // Constructores
    public PrecioRequest() {}

    public PrecioRequest(LocalDateTime fecha, Double valor, Long productoId) {
        this.fecha = fecha;
        this.valor = valor;
        this.productoId = productoId;
    }

    // Getters y setters
    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
}