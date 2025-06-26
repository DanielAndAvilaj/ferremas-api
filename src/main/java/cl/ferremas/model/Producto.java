package cl.ferremas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "producto")  // ✅ CORREGIDO: tabla "producto" no "productos"
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo", nullable = false, unique = true, length = 20)
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 20, message = "El código no puede exceder 20 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El código solo puede contener letras mayúsculas y números")
    private String codigo;
    
    @Column(name = "marca", nullable = false, length = 50)
    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede exceder 50 caracteres")
    private String marca;
    
    @Column(name = "nombre", nullable = false, length = 100)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @Column(name = "categoria", nullable = false, length = 50)
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    
    @Column(name = "stock", nullable = false)
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 9999, message = "El stock no puede exceder 9999")
    private Integer stock;
    
    @Column(name = "descripcion", length = 500)
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Precio> precios = new ArrayList<>();
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<StockSucursal> stocksSucursal = new ArrayList<>();
    
    // CONSTRUCTORES
    public Producto() {}
    
    public Producto(String codigo, String marca, String nombre, String categoria, Integer stock) {
        this.codigo = codigo;
        this.marca = marca;
        this.nombre = nombre;
        this.categoria = categoria;
        this.stock = stock;
    }
    
    // GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { 
        this.codigo = codigo != null ? codigo.toUpperCase().trim() : null; 
    }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { 
        this.marca = marca != null ? marca.trim() : null; 
    }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { 
        this.nombre = nombre != null ? nombre.trim() : null; 
    }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { 
        this.categoria = categoria != null ? categoria.trim() : null; 
    }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion != null ? descripcion.trim() : null; 
    }
    
    public List<Precio> getPrecios() { return precios; }
    public void setPrecios(List<Precio> precios) { 
        this.precios = precios != null ? precios : new ArrayList<>(); 
    }
    
    public List<StockSucursal> getStocksSucursal() { return stocksSucursal; }
    public void setStocksSucursal(List<StockSucursal> stocksSucursal) { 
        this.stocksSucursal = stocksSucursal != null ? stocksSucursal : new ArrayList<>(); 
    }
    
    // MÉTODOS HELPER PARA PRECIO
    
    /**
     * Obtiene el precio más reciente del producto
     * @return el precio actual o 0.0 si no hay precios
     */
    public Double getPrecio() {
        if (precios == null || precios.isEmpty()) {
            return 0.0;
        }
        
        // Obtener el precio más reciente (último en la lista)
        return precios.get(precios.size() - 1).getValor();
    }
    
    /**
     * Obtiene el precio formateado como String sin decimales
     * @return precio formateado como "1000" o "0"
     */
    public String getPrecioFormateado() {
        Double precio = getPrecio();
        return precio != null ? String.format("%.0f", precio) : "0";
    }
    
    /**
     * Método helper para establecer precio (usado en formularios)
     * Este método no modifica directamente los precios, 
     * debe ser manejado en el controlador
     */
    public void setPrecio(Double precio) {
        // Este método se usa para binding en formularios
        // El precio real se maneja en el controlador creando un nuevo objeto Precio
    }
    
    // MÉTODOS HELPER ADICIONALES
    
    /**
     * Verifica si el producto tiene stock disponible
     * @return true si hay stock, false si no
     */
    public boolean tieneStock() {
        return stock != null && stock > 0;
    }
    
    /**
     * Verifica si el stock está bajo (menos de 10 unidades)
     * @return true si el stock está bajo
     */
    public boolean isStockBajo() {
        return stock != null && stock < 10;
    }
    
    /**
     * Obtiene el nivel de stock como String para mostrar en UI
     * @return "Alto", "Medio" o "Bajo"
     */
    public String getNivelStock() {
        if (stock == null) return "Sin datos";
        if (stock > 50) return "Alto";
        if (stock >= 10) return "Medio";
        return "Bajo";
    }
    
    /**
     * Agrega un precio al producto
     * @param precio el objeto Precio a agregar
     */
    public void agregarPrecio(Precio precio) {
        if (precios == null) {
            precios = new ArrayList<>();
        }
        precios.add(precio);
        precio.setProducto(this);
    }
    
    /**
     * Agrega stock por sucursal
     * @param stockSucursal el objeto StockSucursal a agregar
     */
    public void agregarStockSucursal(StockSucursal stockSucursal) {
        if (stocksSucursal == null) {
            stocksSucursal = new ArrayList<>();
        }
        stocksSucursal.add(stockSucursal);
        stockSucursal.setProducto(this);
    }
    
    // MÉTODOS EQUALS, HASHCODE Y TOSTRING
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Producto producto = (Producto) obj;
        return codigo != null && codigo.equals(producto.codigo);
    }
    
    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("Producto{id=%d, codigo='%s', marca='%s', nombre='%s', categoria='%s', stock=%d}", 
                           id, codigo, marca, nombre, categoria, stock);
    }
}