package cl.ferremas.service;

import cl.ferremas.model.Producto;
import cl.ferremas.model.Sucursal;
import cl.ferremas.external.TipoCambioService;
import cl.ferremas.repository.ProductoRepository;
import cl.ferremas.repository.SucursalRepository;
import cl.ferremas.model.ProductoFavorito;
import cl.ferremas.repository.ProductoFavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@Service
public class ProductoCatalogoService {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private TipoCambioService tipoCambioService;
    @Autowired
    private SucursalRepository sucursalRepository;
    @Autowired
    private ProductoFavoritoRepository productoFavoritoRepository;

    public List<Producto> obtenerCatalogo(
            Optional<String> categoria,
            Optional<Double> precioMin,
            Optional<Double> precioMax,
            Optional<String> marca,
            Optional<Long> sucursalId,
            Optional<Boolean> soloStock,
            Optional<String> busqueda
    ) {
        List<Producto> productos = productoRepository.findAll();
        
        System.out.println("🔍 DEBUG: Total productos en BD: " + productos.size());
        System.out.println("🔍 DEBUG: Filtros - Categoria: " + categoria.orElse("null") + 
                          ", Marca: " + marca.orElse("null") + 
                          ", PrecioMin: " + precioMin.orElse(null) + 
                          ", PrecioMax: " + precioMax.orElse(null) + 
                          ", Sucursal: " + sucursalId.orElse(null) + 
                          ", SoloStock: " + soloStock.orElse(null) + 
                          ", Busqueda: " + busqueda.orElse("null"));

        List<Producto> filtrados = productos.stream()
                .filter(p -> {
                    boolean pasa = categoria.map(c -> {
                        // Si la categoría está vacía, no filtrar
                        if (c == null || c.trim().isEmpty()) {
                            return true;
                        }
                        
                        // Filtro simple y directo
                        boolean match = false;
                        if (p.getCategoria() != null) {
                            String categoriaProducto = p.getCategoria().toLowerCase();
                            String categoriaFiltro = c.toLowerCase().trim();
                            
                            // Coincidencia exacta
                            if (categoriaProducto.equals(categoriaFiltro)) {
                                match = true;
                            }
                            // Coincidencia parcial para "Herramientas" (encuentra tanto Eléctricas como Manuales)
                            else if (categoriaFiltro.equals("herramientas") && 
                                    (categoriaProducto.contains("herramientas"))) {
                                match = true;
                            }
                        }
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - Categoria filtro: " + c + " vs " + p.getCategoria() + " = " + match);
                        return match;
                    }).orElse(true);
                    return pasa;
                })
                .filter(p -> {
                    boolean pasa = marca.map(m -> {
                        // Si la marca está vacía, no filtrar
                        if (m == null || m.trim().isEmpty()) {
                            return true;
                        }
                        
                        boolean match = p.getMarca() != null && p.getMarca().equalsIgnoreCase(m.trim());
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - Marca filtro: " + m + " vs " + p.getMarca() + " = " + match);
                        return match;
                    }).orElse(true);
                    return pasa;
                })
                .filter(p -> {
                    boolean pasa = precioMin.map(min -> {
                        double precio = p.getPrecio() != null ? p.getPrecio() : 0.0;
                        boolean match = precio >= min;
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - PrecioMin filtro: " + min + " vs " + precio + " = " + match);
                        return match;
                    }).orElse(true);
                    return pasa;
                })
                .filter(p -> {
                    boolean pasa = precioMax.map(max -> {
                        double precio = p.getPrecio() != null ? p.getPrecio() : 0.0;
                        boolean match = precio <= max;
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - PrecioMax filtro: " + max + " vs " + precio + " = " + match);
                        return match;
                    }).orElse(true);
                    return pasa;
                })
                .filter(p -> {
                    boolean pasa = soloStock.orElse(false) ? p.getStock() != null && p.getStock() > 0 : true;
                    if (soloStock.orElse(false)) {
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - SoloStock filtro: " + p.getStock() + " = " + pasa);
                    }
                    return pasa;
                })
                .filter(p -> {
                    boolean pasa = busqueda.map(q -> {
                        boolean match = (p.getNombre() != null && p.getNombre().toLowerCase().contains(q.toLowerCase())) ||
                                       (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(q.toLowerCase()));
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - Busqueda filtro: " + q + " = " + match);
                        return match;
                    }).orElse(true);
                    return pasa;
                })
                .filter(p -> {
                    if (sucursalId.isPresent()) {
                        boolean pasa = p.getStocksSucursal().stream().anyMatch(ss ->
                                ss.getSucursal() != null &&
                                ss.getSucursal().getId().equals(sucursalId.get()) &&
                                ss.getStock() != null && ss.getStock() > 0
                        );
                        System.out.println("🔍 DEBUG: Producto " + p.getNombre() + " - Sucursal filtro: " + sucursalId.get() + " = " + pasa);
                        return pasa;
                    }
                    return true;
                })
                .collect(Collectors.toList());
                
        System.out.println("🔍 DEBUG: Productos filtrados: " + filtrados.size());
        return filtrados;
    }

    public Producto obtenerDetalle(Long id) {
        System.out.println("🔍 DEBUG: ProductoCatalogoService.obtenerDetalle - Buscando producto ID: " + id);
        
        try {
            // Usar el método básico de JPA primero
            Producto producto = productoRepository.findById(id).orElse(null);
            if (producto == null) {
                System.out.println("🔍 DEBUG: Producto no encontrado con ID: " + id);
                return null;
            }
            
            System.out.println("🔍 DEBUG: Producto encontrado: " + producto.getNombre());
            System.out.println("🔍 DEBUG: Precio actual: " + producto.getPrecio());
            
            return producto;
        } catch (Exception e) {
            System.err.println("❌ ERROR en obtenerDetalle: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<String> obtenerCategorias() {
        return productoRepository.findAll().stream()
                .map(Producto::getCategoria)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> obtenerMarcas() {
        return productoRepository.findAll().stream()
                .map(Producto::getMarca)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Sucursal> obtenerSucursales() {
        return sucursalRepository.findAll();
    }

    public Double obtenerValorDolar() {
        return tipoCambioService.obtenerValorDolar();
    }

    public List<Map<String, Object>> obtenerProductosCatalogo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            cl.ferremas.model.Usuario usuario = (cl.ferremas.model.Usuario) auth.getPrincipal();
            userId = usuario.getId();
        }
        List<ProductoFavorito> favoritos = userId != null ? productoFavoritoRepository.findByUsuarioId(userId) : List.of();
        Set<Long> favoritosIds = favoritos.stream().map(f -> f.getProducto().getId()).collect(Collectors.toSet());
        return productoRepository.findAll().stream().map(producto -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", producto.getId());
            map.put("codigo", producto.getCodigo());
            map.put("nombre", producto.getNombre());
            map.put("marca", producto.getMarca());
            map.put("categoria", producto.getCategoria());
            map.put("descripcion", producto.getDescripcion());
            map.put("precio", producto.getPrecio());
            map.put("stock", producto.getStock());
            map.put("favorito", favoritosIds.contains(producto.getId()));
            return map;
        }).collect(Collectors.toList());
    }
} 