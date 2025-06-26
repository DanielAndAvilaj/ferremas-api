package cl.ferremas.dto;

import java.util.List;

public class PagoRequest {
    
    private Double monto;
    private String descripcion;
    private String emailCliente;
    private String nombreCliente;
    private List<ItemPago> items;

    // Constructores
    public PagoRequest() {}

    public PagoRequest(Double monto, String descripcion, String emailCliente) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.emailCliente = emailCliente;
    }

    // Getters y setters
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public List<ItemPago> getItems() { return items; }
    public void setItems(List<ItemPago> items) { this.items = items; }

    // Clase interna para items del carrito
    public static class ItemPago {
        private String producto;
        private Integer cantidad;
        private Double precio;

        public ItemPago() {}

        public ItemPago(String producto, Integer cantidad, Double precio) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precio = precio;
        }

        // Getters y setters
        public String getProducto() { return producto; }
        public void setProducto(String producto) { this.producto = producto; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

        public Double getPrecio() { return precio; }
        public void setPrecio(Double precio) { this.precio = precio; }
    }
}