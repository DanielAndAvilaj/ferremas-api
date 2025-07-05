-- Script para importar datos de prueba en Ferremas API
-- Este script NO elimina datos existentes, solo inserta nuevos datos

-- Deshabilitar verificación de claves foráneas temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- Insertar usuarios de prueba (contraseña: 123456)
INSERT IGNORE INTO usuario (id, username, nombre, email, password, rol, fecha_registro, enabled) VALUES 
(1, 'admin@ferremas.cl', 'Administrador', 'admin@ferremas.cl', '$2a$10$qyWn7WFjIb2./2tyP1x5WOOi8bJwkVMMTPVmSQrYA3JC2.1m8z05m', 'ADMIN', '2024-01-01 10:00:00', 1),
(2, 'juan@ejemplo.com', 'Juan Pérez', 'juan@ejemplo.com', '$2a$10$qyWn7WFjIb2./2tyP1x5WOOi8bJwkVMMTPVmSQrYA3JC2.1m8z05m', 'USER', '2024-01-01 10:00:00', 1),
(3, 'maria@ejemplo.com', 'María González', 'maria@ejemplo.com', '$2a$10$qyWn7WFjIb2./2tyP1x5WOOi8bJwkVMMTPVmSQrYA3JC2.1m8z05m', 'USER', '2024-01-01 10:00:00', 1);

-- Insertar sucursales
INSERT IGNORE INTO sucursal (id, nombre, direccion, telefono, email, ciudad, horarios, latitud, longitud) VALUES 
(1, 'Sucursal Centro', 'Av. Providencia 1234', '+56 2 2345 6789', 'centro@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00', -33.4489, -70.6693),
(2, 'Sucursal Las Condes', 'Av. Apoquindo 5678', '+56 2 2456 7890', 'lascondes@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00', -33.4167, -70.5833),
(3, 'Sucursal Valparaíso', 'Av. Argentina 9012', '+56 32 2345 6789', 'valparaiso@ferremas.cl', 'Valparaíso', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00', -33.0472, -71.6127);

-- Insertar productos
INSERT IGNORE INTO producto (id, codigo, nombre, descripcion, categoria, marca, stock) VALUES 
(1, 'TAL001', 'Taladro DeWalt D25133K', 'Taladro demoledor profesional de 800W', 'Herramientas Eléctricas', 'DeWalt', 50),
(2, 'MAR001', 'Martillo Stanley 16oz', 'Martillo de carpintero con mango de fibra de vidrio', 'Herramientas Manuales', 'Stanley', 100),
(3, 'DES001', 'Destornillador Phillips #2', 'Destornillador Phillips de 6 pulgadas', 'Herramientas Manuales', 'Stanley', 200),
(4, 'SIE001', 'Sierra Circular Makita 5007MG', 'Sierra circular de 7-1/4 pulgadas con motor de 15A', 'Herramientas Eléctricas', 'Makita', 25),
(5, 'NIV001', 'Nivel de Burbuja 24"', 'Nivel de burbuja profesional de 24 pulgadas', 'Herramientas Manuales', 'Empire', 75),
(6, 'CIN001', 'Cinta Métrica 25m', 'Cinta métrica de 25 metros con carcasa resistente', 'Herramientas Manuales', 'Stanley', 150);

-- Insertar stock por sucursal
INSERT IGNORE INTO stock_sucursal (id, producto_id, sucursal_id, stock) VALUES 
(1, 1, 1, 15), (2, 1, 2, 8), (3, 1, 3, 12),
(4, 2, 1, 25), (5, 2, 2, 18), (6, 2, 3, 22),
(7, 3, 1, 50), (8, 3, 2, 35), (9, 3, 3, 40),
(10, 4, 1, 5), (11, 4, 2, 3), (12, 4, 3, 7),
(13, 5, 1, 20), (14, 5, 2, 15), (15, 5, 3, 18),
(16, 6, 1, 30), (17, 6, 2, 25), (18, 6, 3, 28);

-- Insertar precios
INSERT IGNORE INTO precio (id, producto_id, valor, fecha) VALUES 
(1, 1, 89990, '2024-01-01'),
(2, 2, 15990, '2024-01-01'),
(3, 3, 2990, '2024-01-01'),
(4, 4, 129990, '2024-01-01'),
(5, 5, 8990, '2024-01-01'),
(6, 6, 12990, '2024-01-01');

-- Insertar favoritos
INSERT IGNORE INTO producto_favorito (id, usuario_id, producto_id, fecha_creacion) VALUES 
(1, 2, 1, '2024-01-15 10:00:00'),
(2, 2, 4, '2024-01-16 10:00:00'),
(3, 3, 2, '2024-01-14 10:00:00'),
(4, 3, 5, '2024-01-15 10:00:00');

-- Insertar mensajes
INSERT IGNORE INTO mensaje (id, nombre_cliente, email, telefono, mensaje, estado, fecha_creacion, respuesta_vendedor, admin_usuario_id) VALUES 
(1, 'Carlos López', 'carlos@ejemplo.com', '+56 9 1234 5678', '¿Tienen taladros inalámbricos en stock?', 'PENDIENTE', '2024-01-15 10:30:00', NULL, NULL),
(2, 'Ana Silva', 'ana@ejemplo.com', '+56 9 8765 4321', 'Necesito cotización para 10 martillos', 'EN_PROCESO', '2024-01-14 15:45:00', 'Te envío la cotización en las próximas horas', 1),
(3, 'Roberto Díaz', 'roberto@ejemplo.com', '+56 9 5555 1234', '¿Hacen envíos a regiones?', 'RESUELTO', '2024-01-13 09:15:00', 'Sí, hacemos envíos a todo Chile', 1);

-- Habilitar verificación de claves foráneas
SET FOREIGN_KEY_CHECKS = 1; 