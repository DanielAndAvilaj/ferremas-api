-- Script corregido para importar datos de prueba en Ferremas API
-- Ejecutar en phpMyAdmin después de que las tablas estén creadas

-- OPCIÓN 1: Limpiar datos con DELETE (respeta foreign keys)
DELETE FROM producto_favorito;
DELETE FROM stock_sucursal;
DELETE FROM precio;
DELETE FROM carrito_item;
DELETE FROM mensaje;
DELETE FROM usuario;
DELETE FROM producto;
DELETE FROM sucursal;

-- Resetear auto_increment
ALTER TABLE producto_favorito AUTO_INCREMENT = 1;
ALTER TABLE stock_sucursal AUTO_INCREMENT = 1;
ALTER TABLE precio AUTO_INCREMENT = 1;
ALTER TABLE carrito_item AUTO_INCREMENT = 1;
ALTER TABLE mensaje AUTO_INCREMENT = 1;
ALTER TABLE usuario AUTO_INCREMENT = 1;
ALTER TABLE producto AUTO_INCREMENT = 1;
ALTER TABLE sucursal AUTO_INCREMENT = 1;

-- ===== INSERTAR DATOS DE PRUEBA =====

-- 1. Insertar usuarios de prueba (password: 123456)
INSERT INTO usuario (id, username, nombre, email, password, rol) VALUES 
(1, 'admin@ferremas.cl', 'Administrador Ferremas', 'admin@ferremas.cl', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN'),
(2, 'juan@ejemplo.com', 'Juan Pérez', 'juan@ejemplo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER'),
(3, 'maria@ejemplo.com', 'María González', 'maria@ejemplo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER'),
(4, 'vendedor@ferremas.cl', 'Carlos Vendedor', 'vendedor@ferremas.cl', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'USER');

-- 2. Insertar sucursales
INSERT INTO sucursal (id, nombre, direccion, telefono, email, ciudad, horarios, latitud, longitud) VALUES 
(1, 'Ferremas Centro', 'Av. Providencia 1234, Providencia', '+56 2 2345 6789', 'centro@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00, Dom 10:00-16:00', -33.4489, -70.6693),
(2, 'Ferremas Las Condes', 'Av. Apoquindo 5678, Las Condes', '+56 2 2456 7890', 'lascondes@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00, Dom 10:00-16:00', -33.4167, -70.5833),
(3, 'Ferremas Valparaíso', 'Av. Argentina 9012, Valparaíso', '+56 32 2345 6789', 'valparaiso@ferremas.cl', 'Valparaíso', 'Lun-Vie 8:00-19:00, Sáb 9:00-17:00', -33.0472, -71.6127),
(4, 'Ferremas Maipú', 'Av. Américo Vespucio 1500, Maipú', '+56 2 2567 8901', 'maipu@ferremas.cl', 'Santiago', 'Lun-Vie 8:00-20:00, Sáb 9:00-18:00, Dom 10:00-16:00', -33.5073, -70.7644);

-- 3. Insertar productos con códigos únicos
INSERT INTO producto (id, codigo, nombre, descripcion, categoria, marca, stock) VALUES 
-- Herramientas Eléctricas
(1, 'TAL001', 'Taladro DeWalt D25133K', 'Taladro demoledor profesional de 800W con sistema SDS-Plus. Ideal para concreto, mampostería y metal. Incluye maletín y accesorios.', 'Herramientas Eléctricas', 'DeWalt', 35),
(2, 'SIE001', 'Sierra Circular Makita 5007MG', 'Sierra circular de 7-1/4 pulgadas con motor de 15A. Corte preciso en madera y materiales de construcción. Base de magnesio liviana.', 'Herramientas Eléctricas', 'Makita', 15),
(3, 'TAL002', 'Taladro Inalámbrico Bosch GSR120', 'Taladro inalámbrico de 12V con batería de litio. Perfecto para trabajos ligeros y de precisión.', 'Herramientas Eléctricas', 'Bosch', 28),
(4, 'ESM001', 'Esmeril Angular DeWalt DWE402', 'Esmeril angular de 4.5 pulgadas, motor de 11A. Ideal para corte y desbaste de metal.', 'Herramientas Eléctricas', 'DeWalt', 22),

-- Herramientas Manuales
(5, 'MAR001', 'Martillo Stanley 16oz', 'Martillo de carpintero con mango de fibra de vidrio antivibración. Cabeza forjada de una pieza.', 'Herramientas Manuales', 'Stanley', 65),
(6, 'DES001', 'Set Destornilladores Phillips/Plano', 'Set de 6 destornilladores profesionales Phillips y plano. Mangos ergonómicos antideslizantes.', 'Herramientas Manuales', 'Stanley', 125),
(7, 'NIV001', 'Nivel de Burbuja 24" Empire', 'Nivel profesional de 24 pulgadas con precisión de 0.0005". Carcasa de aluminio reforzada.', 'Herramientas Manuales', 'Empire', 38),
(8, 'CIN001', 'Cinta Métrica Stanley 25m', 'Cinta métrica de 25 metros con carcasa resistente. Cinta de acero templado con graduación clara.', 'Herramientas Manuales', 'Stanley', 83),
(9, 'LLA001', 'Set Llaves Combinadas 8-22mm', 'Set de 12 llaves combinadas cromadas desde 8mm hasta 22mm. Acero al cromo-vanadio.', 'Herramientas Manuales', 'Bahco', 42),

-- Materiales
(10, 'TOR001', 'Tornillos Autoperforantes 3/4"', 'Caja de 100 tornillos autoperforantes de 3/4 pulgada para metal. Cabeza Phillips.', 'Materiales', 'Hilti', 200),
(11, 'CLA001', 'Clavos 2.5" x 100 unidades', 'Clavos de acero galvanizado de 2.5 pulgadas. Ideal para construcción y carpintería.', 'Materiales', 'Sodimac', 150),
(12, 'SIL001', 'Silicona Transparente 280ml', 'Silicona acética transparente de uso general. Resistente a la humedad y cambios de temperatura.', 'Materiales', '3M', 75),

-- Electricidad
(13, 'CAB001', 'Cable Eléctrico 12 AWG x 100m', 'Cable eléctrico sólido de cobre 12 AWG. Aislamiento THHN para uso residencial e industrial.', 'Electricidad', 'Indeco', 45),
(14, 'ENC001', 'Enchufe Schuko 16A', 'Enchufe schuko de 16 amperios con toma a tierra. Carcasa de policarbonato resistente.', 'Electricidad', 'Schneider', 95),
(15, 'INT001', 'Interruptor Simple 10A', 'Interruptor simple de 10 amperios. Diseño moderno color blanco. Fácil instalación.', 'Electricidad', 'Legrand', 110);

-- 4. Insertar stock por sucursal (distribución realista)
INSERT INTO stock_sucursal (producto_id, sucursal_id, stock) VALUES 
-- Taladro DeWalt (35 total)
(1, 1, 15), (1, 2, 8), (1, 3, 7), (1, 4, 5),
-- Sierra Makita (15 total)
(2, 1, 5), (2, 2, 3), (2, 3, 4), (2, 4, 3),
-- Taladro Bosch (28 total)
(3, 1, 12), (3, 2, 8), (3, 3, 5), (3, 4, 3),
-- Esmeril DeWalt (22 total)
(4, 1, 8), (4, 2, 6), (4, 3, 4), (4, 4, 4),
-- Martillo Stanley (65 total)
(5, 1, 25), (5, 2, 18), (5, 3, 12), (5, 4, 10),
-- Set Destornilladores (125 total)
(6, 1, 50), (6, 2, 35), (6, 3, 25), (6, 4, 15),
-- Nivel (38 total)
(7, 1, 15), (7, 2, 10), (7, 3, 8), (7, 4, 5),
-- Cinta Métrica (83 total)
(8, 1, 30), (8, 2, 25), (8, 3, 18), (8, 4, 10),
-- Set Llaves (42 total)
(9, 1, 18), (9, 2, 12), (9, 3, 8), (9, 4, 4),
-- Tornillos (200 total)
(10, 1, 80), (10, 2, 60), (10, 3, 40), (10, 4, 20),
-- Clavos (150 total)
(11, 1, 60), (11, 2, 45), (11, 3, 30), (11, 4, 15),
-- Silicona (75 total)
(12, 1, 30), (12, 2, 25), (12, 3, 15), (12, 4, 5),
-- Cable (45 total)
(13, 1, 20), (13, 2, 15), (13, 3, 7), (13, 4, 3),
-- Enchufe (95 total)
(14, 1, 40), (14, 2, 30), (14, 3, 15), (14, 4, 10),
-- Interruptor (110 total)
(15, 1, 45), (15, 2, 35), (15, 3, 20), (15, 4, 10);

-- 5. Insertar precios (con histórico)
INSERT INTO precio (producto_id, valor, fecha) VALUES 
-- Precios actuales (2024)
(1, 89990, '2024-06-01'),   -- Taladro DeWalt
(2, 129990, '2024-06-01'),  -- Sierra Makita
(3, 45990, '2024-06-01'),   -- Taladro Bosch
(4, 67990, '2024-06-01'),   -- Esmeril DeWalt
(5, 15990, '2024-06-01'),   -- Martillo Stanley
(6, 24990, '2024-06-01'),   -- Set Destornilladores
(7, 18990, '2024-06-01'),   -- Nivel Empire
(8, 12990, '2024-06-01'),   -- Cinta Métrica
(9, 89990, '2024-06-01'),   -- Set Llaves Bahco
(10, 5990, '2024-06-01'),   -- Tornillos
(11, 3990, '2024-06-01'),   -- Clavos
(12, 7990, '2024-06-01'),   -- Silicona
(13, 89990, '2024-06-01'),  -- Cable
(14, 2990, '2024-06-01'),   -- Enchufe
(15, 3990, '2024-06-01'),   -- Interruptor

-- Precios históricos (para mostrar cambios)
(1, 85990, '2024-01-01'),   -- Taladro más barato antes
(2, 124990, '2024-01-01'),  -- Sierra más barata antes
(5, 14990, '2024-01-01'),   -- Martillo más barato antes
(8, 11990, '2024-01-01');   -- Cinta más barata antes

-- 6. Insertar favoritos de usuarios
INSERT INTO producto_favorito (usuario_id, producto_id, fecha_creacion) VALUES 
(2, 1, '2024-06-15 10:30:00'),  -- Juan: Taladro DeWalt
(2, 2, '2024-06-16 14:20:00'),  -- Juan: Sierra Makita
(2, 5, '2024-06-17 09:15:00'),  -- Juan: Martillo
(3, 3, '2024-06-14 16:45:00'),  -- María: Taladro Bosch
(3, 7, '2024-06-15 11:30:00'),  -- María: Nivel
(3, 9, '2024-06-16 13:20:00'),  -- María: Set Llaves
(4, 1, '2024-06-18 08:30:00'),  -- Carlos: Taladro DeWalt
(4, 4, '2024-06-18 15:45:00');  -- Carlos: Esmeril

-- 7. Insertar mensajes de contacto
INSERT INTO mensaje (nombre_cliente, email, telefono, asunto, mensaje, estado, fecha_creacion, respuesta_vendedor, fecha_respuesta, admin_usuario_id, usuario_id) VALUES 
('Carlos López', 'carlos@ejemplo.com', '+56 9 1234 5678', 'Consulta Stock', '¿Tienen taladros inalámbricos Bosch en stock en la sucursal de Las Condes?', 'PENDIENTE', '2024-06-20 10:30:00', NULL, NULL, NULL, NULL),

('Ana Silva', 'ana@ejemplo.com', '+56 9 8765 4321', 'Cotización Empresarial', 'Necesito cotización para 10 martillos Stanley y 5 sets de destornilladores para mi empresa constructora.', 'EN_PROCESO', '2024-06-19 15:45:00', 'Hola Ana, te preparo la cotización empresarial con descuentos por volumen. Te contacto en 2 horas.', '2024-06-19 16:30:00', 1, NULL),

('Roberto Díaz', 'roberto@ejemplo.com', '+56 9 5555 1234', 'Envíos a Regiones', '¿Hacen envíos a Temuco? ¿Cuál es el costo y tiempo de entrega?', 'RESUELTO', '2024-06-18 09:15:00', 'Hola Roberto, sí hacemos envíos a todo Chile. A Temuco el costo es $8.990 y llega en 3-5 días hábiles via Chilexpress.', '2024-06-18 11:20:00', 1, NULL),

('Juan Pérez', 'juan@ejemplo.com', '+56 9 9999 8888', 'Garantía Producto', 'Compré un taladro DeWalt hace 2 meses y tiene un problema con el mandril. ¿Está cubierto por garantía?', 'RESUELTO', '2024-06-17 14:20:00', 'Hola Juan, todos los productos DeWalt tienen 2 años de garantía. Trae tu boleta y el producto a cualquier sucursal para el cambio inmediato.', '2024-06-17 15:45:00', 1, 2),

('Patricia Morales', 'pmorales@construcciones.cl', '+56 2 2234 5567', 'Descuentos Mayoristas', 'Somos una empresa constructora, ¿manejan descuentos para compras superiores a $500.000?', 'EN_PROCESO', '2024-06-21 08:45:00', 'Hola Patricia, sí tenemos programa mayorista con descuentos del 15-25% según volumen. Te envío la información completa por email.', '2024-06-21 10:15:00', 1, NULL);

-- ===== VERIFICACIÓN DE DATOS =====

-- Mostrar resumen de datos insertados
SELECT 'USUARIOS' as Tabla, COUNT(*) as Total FROM usuario
UNION ALL
SELECT 'SUCURSALES', COUNT(*) FROM sucursal
UNION ALL  
SELECT 'PRODUCTOS', COUNT(*) FROM producto
UNION ALL
SELECT 'STOCK_SUCURSAL', COUNT(*) FROM stock_sucursal
UNION ALL
SELECT 'PRECIOS', COUNT(*) FROM precio
UNION ALL
SELECT 'FAVORITOS', COUNT(*) FROM producto_favorito
UNION ALL
SELECT 'MENSAJES', COUNT(*) FROM mensaje;