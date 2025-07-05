package cl.ferremas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String nombreCliente;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    @Pattern(regexp = "^[+0-9\\-() ]*$", message = "Teléfono inválido")
    private String telefono;

    @NotBlank
    @Size(max = 100)
    private String asunto;

    @NotBlank
    @Size(max = 1000)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMensaje estado;

    @Size(max = 2000)
    private String respuestaVendedor;

    private LocalDateTime fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_usuario_id")
    private Usuario adminUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public enum EstadoMensaje { PENDIENTE, EN_PROCESO, RESUELTO }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public EstadoMensaje getEstado() { return estado; }
    public void setEstado(EstadoMensaje estado) { this.estado = estado; }
    public String getRespuestaVendedor() { return respuestaVendedor; }
    public void setRespuestaVendedor(String respuestaVendedor) { this.respuestaVendedor = respuestaVendedor; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
    public Usuario getAdminUsuario() { return adminUsuario; }
    public void setAdminUsuario(Usuario adminUsuario) { this.adminUsuario = adminUsuario; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
} 