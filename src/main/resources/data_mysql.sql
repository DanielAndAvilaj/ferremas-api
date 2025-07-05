-- Datos de prueba para Ferremas API - MySQL
-- Este archivo debe importarse manualmente en phpMyAdmin

-- Limpiar datos existentes (opcional, descomenta si necesitas limpiar)
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE producto_favorito;
-- TRUNCATE TABLE stock_sucursal;
-- TRUNCATE TABLE precio;
-- TRUNCATE TABLE mensaje;
-- TRUNCATE TABLE carrito_item;
-- TRUNCATE TABLE usuario;
-- TRUNCATE TABLE producto;
-- TRUNCATE TABLE sucursal;
-- SET FOREIGN_KEY_CHECKS = 1;

-- Insertar usuarios de prueba (password: 123456)
INSERT INTO usuario (id, username, nombre, email, password, rol, fecha_registro, enabled) VALUES 
(1, 'admin@ferremas.cl', 'Administrador', 'admin@ferremas.cl', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN', NOW(), 1),
(2, 'juan@ejemplo.com', 'Juan Pérez', 'juan@ejemplo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER', NOW(), 1),
(3, 'maria@ejemplo.com', 'María González', 'maria@ejemplo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER', NOW(), 1);

-- Insertar sucursales
INSERT INTO sucursal (id, nombre, direccion, telefono, email, ciudad, horarios, latitud, longitud) VALUES 
(1, 'Sucursal Centro', 'Av. Providencia 1234', '+56 2 2345 6789', 'centro@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00', -33.4489, -70.6693),
(2, 'Sucursal Las Condes', 'Av. Apoquindo 5678', '+56 2 2456 7890', 'lascondes@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00', -33.4167, -70.5833),
(3, 'Sucursal Valparaíso', 'Av. Argentina 9012', '+56 32 2345 6789', 'valparaiso@ferremas.cl', 'Valparaíso', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00', -33.0472, -71.6127);

-- Insertar productos
INSERT INTO producto (id, nombre, descripcion, categoria, marca) VALUES 
(1, 'Taladro DeWalt D25133K', 'Taladro demoledor profesional de 800W', 'Herramientas Eléctricas', 'DeWalt'),
(2, 'Martillo Stanley 16oz', 'Martillo de carpintero con mango de fibra de vidrio', 'Herramientas Manuales', 'Stanley'),
(3, 'Destornillador Phillips #2', 'Destornillador Phillips de 6 pulgadas', 'Herramientas Manuales', 'Stanley'),
(4, 'Sierra Circular Makita 5007MG', 'Sierra circular de 7-1/4 pulgadas con motor de 15A', 'Herramientas Eléctricas', 'Makita'),
(5, 'Nivel de Burbuja 24"', 'Nivel de burbuja profesional de 24 pulgadas', 'Herramientas Manuales', 'Empire'),
(6, 'Cinta Métrica 25m', 'Cinta métrica de 25 metros con carcasa resistente', 'Herramientas Manuales', 'Stanley');

-- Insertar stock por sucursal
INSERT INTO stock_sucursal (id, producto_id, sucursal_id, stock) VALUES 
(1, 1, 1, 15), (2, 1, 2, 8), (3, 1, 3, 12),
(4, 2, 1, 25), (5, 2, 2, 18), (6, 2, 3, 22),
(7, 3, 1, 50), (8, 3, 2, 35), (9, 3, 3, 40),
(10, 4, 1, 5), (11, 4, 2, 3), (12, 4, 3, 7),
(13, 5, 1, 20), (14, 5, 2, 15), (15, 5, 3, 18),
(16, 6, 1, 30), (17, 6, 2, 25), (18, 6, 3, 28);

-- Insertar precios
INSERT INTO precio (id, producto_id, valor, fecha) VALUES 
(1, 1, 89990, '2024-01-01'),
(2, 2, 15990, '2024-01-01'),
(3, 3, 2990, '2024-01-01'),
(4, 4, 129990, '2024-01-01'),
(5, 5, 8990, '2024-01-01'),
(6, 6, 12990, '2024-01-01');

-- Insertar favoritos
INSERT INTO producto_favorito (id, usuario_id, producto_id, fecha_creacion) VALUES 
(1, 2, 1, '2024-01-15'),
(2, 2, 4, '2024-01-16'),
(3, 3, 2, '2024-01-14'),
(4, 3, 5, '2024-01-15');

-- Insertar mensajes
INSERT INTO mensaje (id, nombre_cliente, email, telefono, mensaje, estado, fecha_creacion, respuesta_vendedor, admin_usuario_id) VALUES 
(1, 'Carlos López', 'carlos@ejemplo.com', '+56 9 1234 5678', '¿Tienen taladros inalámbricos en stock?', 'PENDIENTE', '2024-01-15 10:30:00', NULL, NULL),
(2, 'Ana Silva', 'ana@ejemplo.com', '+56 9 8765 4321', 'Necesito cotización para 10 martillos', 'EN_PROCESO', '2024-01-14 15:45:00', 'Te envío la cotización en las próximas horas', 1),
(3, 'Roberto Díaz', 'roberto@ejemplo.com', '+56 9 5555 1234', '¿Hacen envíos a regiones?', 'RESUELTO', '2024-01-13 09:15:00', 'Sí, hacemos envíos a todo Chile', 1); 