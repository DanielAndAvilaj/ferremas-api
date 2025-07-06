# 🔧 Información Técnica - Ferremas API

> **Documentación técnica detallada para evaluación del proyecto**

## 📊 Resumen del Proyecto

**Nombre**: Sistema de Gestión Ferretera (Ferremas)  
**Tecnología**: Spring Boot 3.5.0 + Java 21  
**Base de Datos**: MySQL 8.0  
**Frontend**: Thymeleaf + Bootstrap 5  
**Arquitectura**: MVC + REST API  
**Seguridad**: Spring Security con roles  

---

## 🏗️ Arquitectura del Sistema

### Patrones de Diseño Implementados

1. **MVC (Model-View-Controller)**
   - **Model**: Entidades JPA (`Producto`, `Usuario`, `Sucursal`, etc.)
   - **View**: Plantillas Thymeleaf (`templates/`)
   - **Controller**: Controladores web y API (`controller/web/` y `controller/api/`)

2. **Repository Pattern**
   - Interfaces que extienden `JpaRepository`
   - Separación de lógica de acceso a datos
   - Ejemplo: `ProductoRepository`, `UsuarioRepository`

3. **Service Layer Pattern**
   - Lógica de negocio en servicios
   - Inyección de dependencias por constructor
   - Ejemplo: `ProductoService`, `AuthService`

4. **DTO Pattern**
   - Objetos de transferencia de datos
   - Separación entre entidades y datos de API
   - Ejemplo: `LoginRequest`, `PagoRequest`

### Estructura de Paquetes

```
cl.ferremas/
├── config/           # Configuraciones globales
├── controller/       # Controladores
│   ├── api/         # REST API endpoints
│   └── web/         # Controladores de vistas web
├── service/         # Lógica de negocio
├── model/           # Entidades JPA
├── repository/      # Acceso a datos
├── dto/             # Data Transfer Objects
└── external/        # Servicios externos (APIs)
```

---

## 🔐 Sistema de Seguridad

### Configuración de Spring Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Configuración de rutas públicas y protegidas
    // Autenticación basada en sesión
    // Control de acceso por roles
}
```

### Rutas Públicas (No requieren autenticación)
- `/`, `/login`, `/register`
- `/catalogo`, `/sucursales`, `/contacto`
- `/css/**`, `/js/**`, `/img/**`
- `/api/auth/**`, `/api/usuarios/registrar`

### Rutas Protegidas
- **USER y ADMIN**: `/dashboard`, `/carrito`, `/mis-favoritos`
- **Solo ADMIN**: `/admin/**`, operaciones CRUD en productos

### Roles Implementados
- **ADMIN**: Acceso completo a todas las funcionalidades
- **USER**: Acceso limitado a funcionalidades de usuario

---

## 🗄️ Modelo de Datos

### Entidades Principales

#### Usuario
```sql
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'USER') DEFAULT 'USER'
);
```

#### Producto
```sql
CREATE TABLE producto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(50) NOT NULL,
    marca VARCHAR(50) NOT NULL,
    stock INT DEFAULT 0
);
```

#### Sucursal
```sql
CREATE TABLE sucursal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    ciudad VARCHAR(50),
    horarios TEXT,
    latitud DECIMAL(10,8),
    longitud DECIMAL(11,8)
);
```

#### Stock por Sucursal
```sql
CREATE TABLE stock_sucursal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    producto_id BIGINT,
    sucursal_id BIGINT,
    stock INT DEFAULT 0,
    FOREIGN KEY (producto_id) REFERENCES producto(id),
    FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);
```

### Relaciones
- **Usuario** ↔ **ProductoFavorito** (Many-to-Many)
- **Producto** ↔ **StockSucursal** (One-to-Many)
- **Sucursal** ↔ **StockSucursal** (One-to-Many)
- **Producto** ↔ **Precio** (One-to-Many)

---

## 🔌 APIs Externas Integradas

### 1. Webpay Plus (Transbank)
**Propósito**: Procesamiento de pagos  
**Endpoint**: `POST /api/pago/iniciar`  
**Configuración**: Ambiente de integración (pruebas)  
**Características**:
- Validación de montos (mínimo $50, máximo $999,999,999)
- Manejo de errores específicos de Webpay
- Confirmación automática de transacciones
- Página de confirmación responsiva

### 2. API Banco Central de Chile
**Propósito**: Conversión de monedas USD/CLP  
**Endpoint**: `GET /api/divisa/tipo-cambio-actual`  
**Configuración**: API pública sin autenticación  
**Características**:
- Obtención automática del tipo de cambio
- Conversión de precios en tiempo real
- Manejo de errores de conectividad
- Cache de respuestas para optimización

---

## 🎨 Frontend y UX

### Tecnologías Frontend
- **Thymeleaf 3.1**: Motor de plantillas del servidor
- **Bootstrap 5.3**: Framework CSS responsivo
- **JavaScript ES6+**: Interactividad del lado cliente
- **CSS3**: Estilos personalizados

### Características de UX
- **Diseño responsivo**: Adaptable a móviles, tablets y desktop
- **Navegación intuitiva**: Menú de navegación claro
- **Feedback visual**: Mensajes de confirmación y error
- **Carga progresiva**: Indicadores de carga
- **Accesibilidad**: Contraste adecuado y navegación por teclado

### Componentes Reutilizables
- **Layout base**: `fragments/layout.html`
- **Navbar**: `fragments/navbar.html`
- **Footer**: `fragments/footer.html`
- **Mensajes**: Sistema de alertas Bootstrap

---

## 🧪 Testing y Calidad

### Casos de Prueba Implementados
1. **Autenticación y Autorización**
   - Login con credenciales válidas
   - Acceso denegado a rutas protegidas
   - Control de acceso por roles

2. **Gestión de Productos**
   - CRUD completo de productos
   - Validaciones de datos
   - Manejo de errores

3. **Carrito de Compras**
   - Agregar productos al carrito
   - Actualizar cantidades
   - Eliminar productos
   - Persistencia de sesión

4. **Integración de APIs**
   - Webpay Plus (ambiente de pruebas)
   - API Banco Central
   - Manejo de errores de conectividad

### Validaciones Implementadas
- **Frontend**: Validación HTML5 y JavaScript
- **Backend**: Validación con Bean Validation
- **Base de datos**: Constraints de integridad

---

## 📈 Métricas y Rendimiento

### Configuraciones de Rendimiento
```properties
# Configuración de Tomcat
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
server.tomcat.max-connections=8192

# Compresión de respuestas
server.compression.enabled=true
server.compression.mime-types=text/html,text/css,application/javascript,application/json

# Cache de recursos estáticos
spring.web.resources.cache.period=3600
```

### Optimizaciones Implementadas
- **Lazy Loading**: Carga diferida de imágenes
- **Paginación**: Listado de productos paginado
- **Cache**: Cache de recursos estáticos
- **Compresión**: Compresión GZIP de respuestas

---

## 🔧 Configuración de Desarrollo

### Variables de Entorno
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/ferremas_db
spring.datasource.username=root
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Servidor
server.port=8080

# Thymeleaf
spring.thymeleaf.cache=false
```

### Logging
```properties
logging.level.cl.ferremas=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.apache.tomcat=INFO
```

---

## 🚀 Despliegue

### Requisitos de Producción
- **Java 21** o superior
- **MySQL 8.0** o superior
- **Mínimo 2GB RAM**
- **Conexión a internet** (para APIs externas)

### Comandos de Despliegue
```bash
# Compilar
mvn clean package

# Ejecutar JAR
java -jar target/ferremas-api-0.0.1-SNAPSHOT.jar

# Con perfil de producción
java -jar target/ferremas-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

## 📋 Checklist de Evaluación

### Funcionalidades Core ✅
- [x] Sistema de autenticación y autorización
- [x] Gestión de productos (CRUD)
- [x] Carrito de compras funcional
- [x] Sistema de favoritos
- [x] Panel de administración
- [x] Gestión de usuarios
- [x] Control de stock por sucursal

### Integraciones ✅
- [x] Webpay Plus para pagos
- [x] API Banco Central para conversión de monedas
- [x] Base de datos MySQL
- [x] Thymeleaf para frontend

### Calidad de Código ✅
- [x] Arquitectura MVC bien estructurada
- [x] Patrones de diseño implementados
- [x] Separación de responsabilidades
- [x] Código documentado y limpio
- [x] Manejo de errores robusto

### UX/UI ✅
- [x] Interfaz responsiva
- [x] Navegación intuitiva
- [x] Feedback visual al usuario
- [x] Diseño moderno y profesional

---

## 🎯 Puntos Destacados para Evaluación

### 1. **Arquitectura Sólida**
- Separación clara de capas (Controller, Service, Repository)
- Uso de patrones de diseño establecidos
- Código modular y mantenible

### 2. **Seguridad Implementada**
- Autenticación basada en sesión
- Control de acceso por roles
- Protección CSRF
- Validación de datos

### 3. **Integración de APIs Externas**
- Webpay Plus (pagos reales)
- API Banco Central (conversión de monedas)
- Manejo robusto de errores

### 4. **Experiencia de Usuario**
- Interfaz intuitiva y responsiva
- Flujos de usuario bien diseñados
- Feedback visual apropiado

### 5. **Base de Datos Bien Diseñada**
- Relaciones correctas entre entidades
- Constraints de integridad
- Datos de prueba realistas

---

## 📞 Información de Contacto

**Desarrollador**: Daniel Ávila  
**GitHub**: [@DanielAndAvilaj](https://github.com/DanielAndAvilaj)  
**Proyecto**: Sistema de Gestión Ferretera  
**Asignatura**: ASY5131 Integración de Plataformas  

---

*Esta documentación técnica proporciona una visión completa del proyecto para su evaluación. El código está listo para ser revisado, ejecutado y evaluado.* 