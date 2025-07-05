package cl.ferremas.controller.web;

import cl.ferremas.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {
    @Autowired
    private ReporteService reporteService;

    @GetMapping("/ventas")
    public String reporteVentas(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta,
                                Model model) {
        Map<String, Object> data = reporteService.ventasPorPeriodo(desde, hasta);
        model.addAttribute("data", data);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "admin/reportes/ventas";
    }

    @GetMapping("/productos")
    public String reporteProductos(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta,
                                   Model model) {
        Map<String, Object> data = reporteService.productosMasVendidos(desde, hasta);
        model.addAttribute("data", data);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "admin/reportes/productos";
    }

    @GetMapping("/usuarios")
    public String reporteUsuarios(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta,
                                  Model model) {
        Map<String, Object> data = reporteService.actividadUsuarios(desde, hasta);
        model.addAttribute("data", data);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "admin/reportes/usuarios";
    }

    @GetMapping("/sucursales")
    public String reporteSucursales(Model model) {
        Map<String, Object> data = reporteService.stockPorSucursal();
        model.addAttribute("data", data);
        return "admin/reportes/sucursales";
    }

    // API para exportar reportes (Excel/PDF)
    @GetMapping("/exportar/{tipo}")
    @ResponseBody
    public ResponseEntity<?> exportarReporte(@PathVariable String tipo,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date desde,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date hasta) {
        // TODO: Implementar exportación real
        return ResponseEntity.ok(Map.of("ok", true, "tipo", tipo));
    }
} 