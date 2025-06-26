package cl.ferremas.service;
import cl.ferremas.model.Producto;
import cl.ferremas.model.Precio;
import cl.ferremas.repository.ProductoRepository;
import cl.ferremas.repository.PrecioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProductoService {
   private final ProductoRepository productoRepository;
   private final PrecioRepository precioRepository;
   
   public ProductoService(ProductoRepository productoRepository, PrecioRepository precioRepository) {
       this.productoRepository = productoRepository;
       this.precioRepository = precioRepository;
   }
   
   public List<Producto> obtenerTodos() {
       return productoRepository.findAll();
   }
   
   public Producto buscarPorCodigo(String codigo) {
       return productoRepository.findByCodigo(codigo);
   }
   
   public List<Producto> buscarPorNombre(String nombre) {
       return productoRepository.findByNombreContainingIgnoreCase(nombre);
   }
   
   public List<Producto> buscarPorCategoria(String categoria) {
       return productoRepository.findByCategoria(categoria);
   }
   
   public List<Producto> buscarPorStockMenorA(Integer stock) {
       return productoRepository.findByStockLessThan(stock);
   }
   
   public Producto guardarProducto(Producto producto) {
       return productoRepository.save(producto);
   }
   
   public void eliminarProducto(Long id) {
       productoRepository.deleteById(id);
   }
   
   public Producto obtenerPorId(Long id) {
       return productoRepository.findById(id)
           .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
   }
   
   // MÉTODOS PARA EL ADMIN PANEL CON MANEJO DE PRECIOS
   public Producto crear(Producto producto) {
       // Obtener precio ANTES de guardar
       Double precio = producto.getPrecio();
       
       // Guardar el producto primero
       Producto productoGuardado = productoRepository.save(producto);
       
       // Crear registro de precio si se proporcionó un precio
       if (precio != null && precio > 0) {
           crearRegistroPrecio(productoGuardado, precio);
       }
       
       return productoGuardado;
   }
   
   public Producto actualizar(Long id, Producto producto) {
       if (!productoRepository.existsById(id)) {
           throw new RuntimeException("Producto no encontrado");
       }
       
       // Obtener precio ANTES de guardar
       Double nuevoPrecio = producto.getPrecio();
       
       // Obtener precio actual para comparar
       Producto productoExistente = obtenerPorId(id);
       Double precioActual = productoExistente.getPrecio();
       
       // Guardar producto
       producto.setId(id);
       Producto productoActualizado = productoRepository.save(producto);
       
       // SIEMPRE crear nuevo registro de precio si hay precio
       if (nuevoPrecio != null && nuevoPrecio > 0) {
           // Si el precio cambió o no hay precio actual, crear nuevo registro
           if (precioActual == null || !precioActual.equals(nuevoPrecio)) {
               crearRegistroPrecio(productoActualizado, nuevoPrecio);
           }
       }
       
       return productoActualizado;
   }
   
   public void eliminar(Long id) {
       if (!productoRepository.existsById(id)) {
           throw new RuntimeException("Producto no encontrado");
       }
       productoRepository.deleteById(id);
   }
   
   // MÉTODOS AUXILIARES PARA PRECIOS
   private void crearRegistroPrecio(Producto producto, Double valor) {
       Precio precio = new Precio();
       precio.setProducto(producto);
       precio.setValor(valor);
       precio.setFecha(LocalDate.now());
       precioRepository.save(precio);
   }
   
   private Double obtenerPrecioActual(Long productoId) {
       Producto producto = obtenerPorId(productoId);
       return producto.getPrecio();
   }
}