package cl.ferremas.service;

import cl.ferremas.model.Mensaje;
import cl.ferremas.repository.MensajeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio para la lógica de negocio de mensajes.
 */
@Service
public class MensajeService {
    private final MensajeRepository mensajeRepository;

    public MensajeService(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    /**
     * Cuenta mensajes por estado.
     */
    public long contarPorEstado(Mensaje.EstadoMensaje estado) {
        return mensajeRepository.countByEstado(estado);
    }

    /**
     * Cuenta mensajes por una lista de estados.
     */
    public long contarPorEstados(List<Mensaje.EstadoMensaje> estados) {
        return mensajeRepository.countByEstadoIn(estados);
    }

    /**
     * Obtiene mensajes pendientes.
     */
    public List<Mensaje> obtenerPendientes() {
        return mensajeRepository.findByEstado(Mensaje.EstadoMensaje.PENDIENTE);
    }

    /**
     * Obtiene mensajes por estado.
     */
    public List<Mensaje> obtenerPorEstado(Mensaje.EstadoMensaje estado) {
        return mensajeRepository.findByEstado(estado);
    }

    /**
     * Obtiene mensajes por usuario.
     */
    public List<Mensaje> obtenerPorUsuario(Long usuarioId) {
        return mensajeRepository.findAll().stream().filter(m -> m.getUsuario() != null && m.getUsuario().getId().equals(usuarioId)).toList();
    }

    /**
     * Guarda un mensaje.
     */
    public Mensaje guardar(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    /**
     * Obtiene un mensaje por ID.
     */
    public Mensaje obtenerPorId(Long id) {
        return mensajeRepository.findById(id).orElse(null);
    }
} 