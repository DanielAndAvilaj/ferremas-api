package cl.ferremas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "precio")
public class Precio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "valor", nullable = false)
    private Double valor;
    
    // ✅ RELACIÓN CORREGIDA: ManyToOne con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false) // ✅ IMPORTANTE: nombre de columna correcto
    @JsonBackReference
    private Producto producto;
    
    // ✅ CONSTRUCTORES
    public Precio() {}
    
    public Precio(LocalDate fecha, Double valor, Producto producto) {
        this.fecha = fecha;
        this.valor = valor;
        this.producto = producto;
    }
    
    // ✅ GETTERS Y SETTERS
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    @Override
    public String toString() {
        return "Precio{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", valor=" + valor +
                ", producto_id=" + (producto != null ? producto.getId() : "NULL") +
                '}';
    }
}