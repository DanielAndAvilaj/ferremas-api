package cl.ferremas.service;
import cl.ferremas.model.Producto;
import cl.ferremas.model.Precio;
import cl.ferremas.repository.ProductoRepository;
import cl.ferremas.repository.PrecioRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para la lógica de negocio de productos y manejo de precios.
 */
@Service
public class ProductoService {
   private final ProductoRepository productoRepository;
   private final PrecioRepository precioRepository;
   
   public ProductoService(ProductoRepository productoRepository, PrecioRepository precioRepository) {
       this.productoRepository = productoRepository;
       this.precioRepository = precioRepository;
   }
   
   /**
    * Obtiene todos los productos.
    */
   public List<Producto> obtenerTodos() {
       return productoRepository.findAll();
   }
   
   /**
    * Busca un producto por su código.
    */
   public Producto buscarPorCodigo(String codigo) {
       return productoRepository.findByCodigo(codigo);
   }
   
   /**
    * Busca productos por nombre (contiene, ignore case).
    */
   public List<Producto> buscarPorNombre(String nombre) {
       return productoRepository.findByNombreContainingIgnoreCase(nombre);
   }
   
   /**
    * Busca productos por categoría.
    */
   public List<Producto> buscarPorCategoria(String categoria) {
       return productoRepository.findByCategoria(categoria);
   }
   
   /**
    * Busca productos con stock menor a un valor dado.
    */
   public List<Producto> buscarPorStockMenorA(Integer stock) {
       return productoRepository.findByStockLessThan(stock);
   }
   
   /**
    * Guarda un producto (crear o actualizar).
    */
   public Producto guardarProducto(Producto producto) {
       return productoRepository.save(producto);
   }
   
   /**
    * Elimina un producto por ID.
    */
   public void eliminarProducto(Long id) {
       productoRepository.deleteById(id);
   }
   
   /**
    * Obtiene un producto por ID.
    */
   public Producto obtenerPorId(Long id) {
       return productoRepository.findById(id)
           .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
   }
   
   /**
    * Crea un producto y registra su precio si corresponde.
    */
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
   
   /**
    * Actualiza un producto y registra el cambio de precio si corresponde.
    */
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
   
   /**
    * Elimina un producto por ID (con validación).
    */
   public void eliminar(Long id) {
       if (!productoRepository.existsById(id)) {
           throw new RuntimeException("Producto no encontrado");
       }
       productoRepository.deleteById(id);
   }
   
   /**
    * Crea un registro de precio para un producto.
    */
   private void crearRegistroPrecio(Producto producto, Double valor) {
       Precio precio = new Precio();
       precio.setProducto(producto);
       precio.setValor(valor);
       precio.setFecha(java.time.LocalDateTime.now());
       precioRepository.save(precio);
   }
}