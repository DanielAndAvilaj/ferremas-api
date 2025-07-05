# ⚡ Ferremás API

> **API REST para gestión completa de productos ferreteros con integración de pagos Webpay y conversión de monedas en tiempo real**

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-green)
![Maven](https://img.shields.io/badge/Maven-4.0-red)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)

Este proyecto es una API REST desarrollada con **Spring Boot** para la gestión de productos, precios, sucursales y stock por sucursal, implementando control de acceso por roles (`ADMIN` y `USER`) y seguridad básica con **HTTP Basic Auth**.

## 🚀 **Nuevas Características Agregadas**

- 💳 **Integración completa con Webpay Plus** para procesamiento de pagos
- 💱 **Conversión de monedas en tiempo real** usando API del Banco Central de Chile
- 🎨 **Páginas de confirmación de pago** responsivas y profesionales
- ✅ **Validaciones robustas** y manejo avanzado de errores
- 🔐 **Mejoras en seguridad** y autenticación

## 📋 **Requisitos**

- Java 21
- Maven
- MySQL (recomendado vía XAMPP)
- Postman (para pruebas)

## 🛠️ **Instalación**

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/DanielAndAvilaj/ferremas-api.git
   ```

2. **Iniciar XAMPP y MySQL**

3. **Crear la base de datos ejecutando el script en phpMyAdmin:**
   ```sql
   CREATE DATABASE IF NOT EXISTS ferremas_db 
   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

4. **Configurar la base de datos en `src/main/resources/application.properties`:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ferremas_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
   ```

5. **Compilar y ejecutar:**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

La aplicación se ejecutará en `http://localhost:8080`

## 🔌 **Endpoints Principales**

### 🔐 **Autenticación**
- **Registro de usuario (abierto):**
  ```http
  POST /api/usuarios/registrar
  Body JSON: { 
    "username": "nuevo_usuario", 
    "password": "password123", 
    "rol": "ADMIN" | "USER" 
  }
  ```

**Usuarios por defecto:**
- **Admin:** `admin` / `admin123`
- **Usuario:** `user` / `user123`

### 🏷️ **Productos**
- `GET /api/productos`: listar productos (ADMIN, USER)
- `GET /api/productos/{id}`: obtener producto por ID (ADMIN, USER)
- `GET /api/productos/categoria?categoria=HERRAMIENTAS`: buscar por categoría (ADMIN, USER)
- `POST /api/productos`: crear producto (ADMIN)
- `PUT /api/productos/{id}`: actualizar producto (ADMIN)
- `DELETE /api/productos/{id}`: eliminar producto (ADMIN)

**Ejemplo de creación:**
```json
POST /api/productos
Authorization: Basic Auth (admin:admin123)
{
    "codigo": "MART001",
    "marca": "Stanley",
    "nombre": "Martillo de Acero 500g",
    "categoria": "HERRAMIENTAS",
    "stock": 50
}
```

### 💰 **Precios**
- `GET /api/precios`: listar precios de productos (ADMIN, USER)
- `GET /api/precios/{codigo}`: consultar precios por código de producto (ADMIN, USER)
- `POST /api/precios`: crear precio (ADMIN)

**Ejemplo de agregar precio:**
```json
POST /api/precios
Authorization: Basic Auth (admin:admin123)
{
    "fecha": "2025-06-25",
    "valor": 15990,
    "productoId": 1
}
```

### 🏢 **Sucursales**
- `GET /api/sucursales`: listar sucursales
- `POST /api/sucursales`: crear sucursal

### 📦 **Stock por Sucursal**
- `GET /api/stock-sucursal`: listar todos los stocks por sucursal
- `POST /api/stock-sucursal`: asignar stock a un producto en una sucursal

### 💱 **Conversión de Monedas (NUEVO)**
- `GET /api/divisa/tipo-cambio-actual`: obtener tipo de cambio USD/CLP actual
- `GET /api/divisa/convertir-precio?precio=15990&nombreProducto=Martillo`: convertir precio personalizado
- `GET /api/divisa/producto/{id}/precio-dolares`: ver precio de producto en dólares

**Ejemplo de respuesta:**
```json
{
    "precio_clp": 25990,
    "precio_usd": 32.45,
    "tipo_cambio": 801.23,
    "fecha_cambio": "2025-06-25",
    "producto": "Martillo Stanley 500g"
}
```

### 💳 **Pagos Webpay (NUEVO)**
- `POST /api/pago/iniciar`: iniciar pago simple
- `POST /api/pago/iniciar-completo`: iniciar pago con datos completos
- `GET /api/pago/confirmar?token_ws={token}`: confirmación automática (redirect desde Webpay)

**Ejemplo de pago simple:**
```json
POST /api/pago/iniciar
{
    "monto": 25990
}
```

**Ejemplo de pago completo:**
```json
POST /api/pago/iniciar-completo
{
    "monto": 45990,
    "descripcion": "Compra de herramientas",
    "emailCliente": "cliente@email.com",
    "nombreCliente": "Juan Pérez"
}
```

## 🔒 **Seguridad**

- La autenticación es vía **HTTP Basic Auth**
- `ADMIN` puede crear, editar y eliminar productos y precios
- `USER` puede consultar información de productos, precios y realizar conversiones
- **Validaciones implementadas:**
  - Montos de pago (mínimo $50, máximo $999,999,999)
  - Datos obligatorios en productos
  - Manejo de errores específicos de Webpay

## 💳 **Integración Webpay**

### Flujo de Pago
1. **Iniciar transacción** → API devuelve URL de pago de Webpay
2. **Redirección a Webpay** → Usuario completa el pago en el portal de Transbank
3. **Confirmación automática** → Webpay redirige a página de confirmación con detalles

### Características
- ✅ Ambiente de integración (pruebas)
- ✅ Validación completa de montos
- ✅ Manejo específico de errores de Webpay
- ✅ Página de confirmación responsive con detalles del pago
- ✅ Soporte para pagos con tarjetas de prueba

## 💱 **Conversión de Monedas**

### Integración con Banco Central de Chile
- 🔄 Obtención automática del tipo de cambio USD/CLP en tiempo real
- 💰 Conversión automática de precios de productos a dólares
- 📊 Cálculo de precios personalizados
- ⚡ Manejo de errores de conectividad con la API externa

## 🏗️ **Estructura del Proyecto**

```
src/main/java/cl/ferremas/
├── controller/          # Controladores REST
│   ├── ProductoController.java
│   ├── PrecioController.java
│   ├── DivisaController.java
│   └── PagoController.java
├── service/            # Lógica de negocio
│   ├── ProductoService.java
│   ├── PrecioService.java
│   ├── DivisaService.java
│   └── PagoService.java
├── model/              # Entidades JPA
│   ├── Producto.java
│   ├── Precio.java
│   ├── Sucursal.java
│   ├── StockSucursal.java
│   └── Usuario.java
├── repository/         # Acceso a datos
├── dto/                # Data Transfer Objects
│   ├── PrecioRequest.java
│   └── PagoRequest.java
├── config/             # Configuraciones
│   └── SecurityConfig.java
└── FerremasApplication.java
```

### Patrones Implementados
- **Repository Pattern** para acceso a datos
- **DTO Pattern** para transferencia de datos
- **Service Layer** para lógica de negocio
- **Dependency Injection** para desacoplamiento

## 🔧 **Tecnologías**

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.5.0 | Framework principal |
| Spring Security | 6.0+ | Autenticación y autorización |
| Spring Data JPA | 3.0+ | Persistencia de datos |
| MySQL | 8.0+ | Base de datos |
| WebClient | 6.0+ | Cliente HTTP reactivo para APIs externas |
| Swagger/OpenAPI | 3.0+ | Documentación de API |
| Maven | 3.6+ | Gestión de dependencias |

## 🌟 **Componentes Reutilizables**

- 🔐 **Sistema de autenticación** completo con Spring Security
- 💳 **Servicio de pagos Webpay** (adaptable a cualquier e-commerce chileno)
- 💱 **Servicio de conversión de monedas** (configurable para otras APIs)
- 🎨 **Páginas de confirmación** responsivas y modernas
- ✅ **Sistema de validaciones** con mensajes amigables al usuario
- 🏗️ **Arquitectura REST** escalable y mantenible

## 🧪 **Testing**

### Casos de Prueba Implementados
- ✅ Autenticación y autorización por roles
- ✅ CRUD completo de productos con validaciones
- ✅ Gestión de precios históricos
- ✅ Integración con APIs externas (Banco Central, Webpay)
- ✅ Manejo de errores y casos límite
- ✅ Validaciones de datos de entrada

### Colección Postman
El proyecto incluye casos de prueba para:
- Flujos de autenticación
- Operaciones CRUD de productos
- Conversiones de moneda
- Procesamiento de pagos
- Manejo de errores

## 🚀 **Próximas Mejoras**

- [ ] Tests automatizados con JUnit 5
- [ ] Documentación completa con Swagger UI
- [ ] Docker containerization
- [ ] CI/CD con GitHub Actions
- [ ] Métricas y monitoreo
- [ ] Logging estructurado

## 👨‍💻 **Autor**

**Daniel Ávila**
- GitHub: [@DanielAndAvilaj](https://github.com/DanielAndAvilaj)

Desarrollado para la Evaluación Parcial 2 de la asignatura **ASY5131 Integración de Plataformas**.

---

⭐ **¡No olvides dar una estrella si este proyecto te fue útil!** ⭐

## Arquitectura y Organización del Proyecto

### Estructura de Carpetas

- `src/main/java/cl/ferremas/controller/web/` — Controladores de vistas (Thymeleaf)
- `src/main/java/cl/ferremas/controller/api/` — Controladores API REST
- `src/main/java/cl/ferremas/service/` — Servicios de negocio (SOLID, inyección por constructor)
- `src/main/java/cl/ferremas/model/` — Entidades JPA
- `src/main/java/cl/ferremas/repository/` — Repositorios Spring Data
- `src/main/resources/templates/` — Vistas Thymeleaf (todas usan layout y fragmentos)
- `src/main/resources/static/` — Archivos estáticos (CSS, JS, imágenes)

### Principios y Buenas Prácticas
- Controladores delgados, lógica en servicios.
- Inyección de dependencias por constructor.
- Código limpio, documentado y profesional.
- Modularización de JS y CSS.
- Layout y fragmentos centralizados en frontend.

## Seguridad y Roles

### Rutas públicas (no requieren login)
- `/`, `/login`, `/register`, `/catalogo`, `/catalogo/**`, `/sucursales`, `/contacto`, `/css/**`, `/js/**`, `/img/**`, `/static/**`
- `/test/**`, `/test-simple` (solo para pruebas)
- `/api/auth/**`, `/api/usuarios/registrar` (registro y login API)

### Rutas protegidas (requieren login)
- `/dashboard`, `/carrito`, `/mis-favoritos`, `/checkout` — **USER y ADMIN**
- `/admin/**` — **Solo ADMIN**
- `/api/productos/**`, `/api/precios/**` (GET) — **USER y ADMIN**
- `/api/productos/**`, `/api/precios/**` (POST/PUT/DELETE) — **Solo ADMIN**

### Buenas prácticas de seguridad
- CSRF desactivado solo para `/api/**` (REST).
- Gestión de sesión segura, logout y expiración.
- Manejo de errores y redirecciones amigable para usuarios y APIs.
- No exponer información sensible en mensajes de error.

## Onboarding rápido para desarrolladores

1. Clona el repositorio y revisa la estructura de carpetas.
2. Todas las vistas usan el layout base y fragmentos (`fragments/layout.html`).
3. El JS y CSS está modularizado en `/static/js/` y `/static/css/`.
4. Los controladores web y API están separados y siguen buenas prácticas.
5. Los servicios aplican SOLID y están documentados.
6. Consulta la sección de seguridad para saber qué rutas requieren autenticación y/o roles.

---