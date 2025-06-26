package cl.ferremas.dto;

import java.time.LocalDate;

public class PrecioRequest {
    
    private LocalDate fecha;
    private Double valor;
    private Long productoId;

    // Constructores
    public PrecioRequest() {}

    public PrecioRequest(LocalDate fecha, Double valor, Long productoId) {
        this.fecha = fecha;
        this.valor = valor;
        this.productoId = productoId;
    }

    // Getters y setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
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