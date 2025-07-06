# 🏪 Ferremas - Sistema de Gestión Ferretera

> **Aplicación web completa para gestión de productos ferreteros con carrito de compras, sistema de pagos Webpay y conversión de monedas en tiempo real**

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-purple)
![Webpay](https://img.shields.io/badge/Webpay-Plus-red)

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Características Principales](#-características-principales)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos del Sistema](#-requisitos-del-sistema)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Base de Datos](#-base-de-datos)
- [Ejecución de la Aplicación](#-ejecución-de-la-aplicación)
- [Credenciales de Prueba](#-credenciales-de-prueba)
- [Funcionalidades](#-funcionalidades)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [API REST](#-api-rest)
- [Troubleshooting](#-troubleshooting)
- [Autor](#-autor)

## 🎯 Descripción del Proyecto

Ferremas es una aplicación web completa desarrollada con **Spring Boot** que simula un sistema de gestión para una ferretería. La aplicación incluye:

- **Catálogo de productos** con imágenes y descripciones
- **Sistema de usuarios** con roles (ADMIN/USER)
- **Carrito de compras** funcional
- **Sistema de favoritos** para productos
- **Integración con Webpay Plus** para procesamiento de pagos
- **Conversión de monedas** en tiempo real usando API del Banco Central de Chile
- **Panel de administración** para gestión de productos y usuarios
- **Sistema de sucursales** con gestión de stock
- **Interfaz web responsiva** con Thymeleaf

## ✨ Características Principales

### 🔐 Autenticación y Autorización
- Sistema de login/registro de usuarios
- Control de acceso basado en roles (ADMIN/USER)
- Gestión de sesiones segura
- Panel de administración protegido

### 🛍️ E-commerce
- Catálogo de productos con filtros
- Carrito de compras persistente
- Sistema de favoritos
- Proceso de checkout completo
- Integración con Webpay Plus para pagos

### 💱 Conversión de Monedas
- Obtención automática del tipo de cambio USD/CLP
- Conversión de precios en tiempo real
- Integración con API del Banco Central de Chile

### 🏢 Gestión de Negocio
- Panel de administración completo
- Gestión de productos (CRUD)
- Gestión de usuarios
- Control de stock por sucursal
- Reportes de ventas

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 3.5.0 | Framework web |
| **Spring Security** | 6.0+ | Autenticación y autorización |
| **Spring Data JPA** | 3.0+ | Persistencia de datos |
| **MySQL** | 8.0+ | Base de datos |
| **Thymeleaf** | 3.1+ | Motor de plantillas |
| **Bootstrap** | 5.3+ | Framework CSS |
| **Webpay Plus** | - | Procesamiento de pagos |
| **Maven** | 3.6+ | Gestión de dependencias |

## 💻 Requisitos del Sistema

### Software Requerido
- **Java JDK 21** o superior
- **Maven 3.6** o superior
- **MySQL 8.0** o superior (recomendado XAMPP)
- **Git** (para clonar el repositorio)

### Hardware Mínimo
- **RAM**: 4GB
- **Espacio en disco**: 2GB libres
- **Procesador**: Dual Core 2.0 GHz

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/DanielAndAvilaj/ferremas-api.git
cd ferremas-api
```

### 2. Configurar Base de Datos

#### Opción A: Usando XAMPP (Recomendado)

1. **Descargar e instalar XAMPP** desde [https://www.apachefriends.org/](https://www.apachefriends.org/)
2. **Iniciar XAMPP** y activar los servicios:
   - Apache (opcional, para phpMyAdmin)
   - MySQL
3. **Abrir phpMyAdmin**: http://localhost/phpmyadmin
4. **Crear la base de datos**:
   ```sql
   CREATE DATABASE ferremas_db 
   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

#### Opción B: MySQL Standalone

1. **Instalar MySQL Server 8.0**
2. **Crear la base de datos**:
   ```sql
   CREATE DATABASE ferremas_db 
   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

### 3. Configurar Conexión a Base de Datos

El archivo `src/main/resources/application.properties` ya está configurado para XAMPP con las siguientes configuraciones:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ferremas_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

**Si usas MySQL standalone**, modifica el archivo con tus credenciales:

```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

## 🗄️ Base de Datos

### Importar Datos de Prueba

1. **Abrir phpMyAdmin**: http://localhost/phpmyadmin
2. **Seleccionar la base de datos**: `ferremas_db`
3. **Ir a la pestaña "Importar"**
4. **Seleccionar el archivo**: `src/main/resources/importar_datos.sql`
5. **Hacer clic en "Continuar"**

Este script incluye:
- ✅ 3 usuarios de prueba (admin y usuarios normales)
- ✅ 6 productos con imágenes
- ✅ 3 sucursales
- ✅ Stock por sucursal
- ✅ Precios de productos
- ✅ Datos de favoritos y carrito

### Estructura de la Base de Datos

```
ferremas_db/
├── usuario          # Usuarios del sistema
├── producto         # Catálogo de productos
├── precio           # Historial de precios
├── sucursal         # Sucursales de la empresa
├── stock_sucursal   # Stock por sucursal
├── carrito_item     # Items del carrito de compras
├── producto_favorito # Productos favoritos de usuarios
└── mensaje          # Mensajes del sistema
```

## ▶️ Ejecución de la Aplicación

### 1. Compilar el Proyecto

```bash
mvn clean compile
```

### 2. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

### 3. Acceder a la Aplicación

La aplicación estará disponible en: **http://localhost:8080**

## 🔑 Credenciales de Prueba

### Usuario Administrador
- **Email**: `admin@ferremas.cl`
- **Password**: `123456`
- **Rol**: ADMIN (acceso completo)

### Usuarios Normales
- **Email**: `juan@ejemplo.com`
- **Password**: `123456`
- **Rol**: USER

- **Email**: `maria@ejemplo.com`
- **Password**: `123456`
- **Rol**: USER

## 🎮 Funcionalidades

### Para Usuarios (USER)
- ✅ **Navegar por el catálogo** de productos
- ✅ **Agregar productos al carrito** de compras
- ✅ **Gestionar favoritos** (agregar/quitar)
- ✅ **Procesar compras** con Webpay
- ✅ **Ver sucursales** y stock disponible
- ✅ **Acceder al dashboard** personal

### Para Administradores (ADMIN)
- ✅ **Todas las funcionalidades de usuario**
- ✅ **Panel de administración** completo
- ✅ **Gestión de productos** (crear, editar, eliminar)
- ✅ **Gestión de usuarios** del sistema
- ✅ **Reportes de ventas** y estadísticas
- ✅ **Control de stock** por sucursal

### Funcionalidades Especiales
- 💳 **Integración Webpay Plus** para pagos reales
- 💱 **Conversión de monedas** USD/CLP en tiempo real
- 📱 **Interfaz responsiva** para móviles y tablets
- 🔍 **Búsqueda y filtros** en el catálogo
- 📊 **Dashboard con estadísticas** en tiempo real

## 📁 Estructura del Proyecto

```
ferremas-api/
├── src/main/java/cl/ferremas/
│   ├── config/                 # Configuraciones
│   │   ├── SecurityConfig.java
│   │   └── GlobalExceptionHandler.java
│   ├── controller/
│   │   ├── api/               # Controladores REST API
│   │   │   ├── ProductoController.java
│   │   │   ├── CarritoController.java
│   │   │   ├── PagoController.java
│   │   │   └── DivisaController.java
│   │   └── web/               # Controladores de vistas web
│   │       ├── WebController.java
│   │       ├── AuthController.java
│   │       └── AdminController.java
│   ├── service/               # Lógica de negocio
│   │   ├── ProductoService.java
│   │   ├── AuthService.java
│   │   ├── CarritoService.java
│   │   └── PagoService.java
│   ├── model/                 # Entidades JPA
│   │   ├── Producto.java
│   │   ├── Usuario.java
│   │   ├── CarritoItem.java
│   │   └── Sucursal.java
│   ├── repository/            # Repositorios de datos
│   ├── dto/                   # Data Transfer Objects
│   └── FerremasApiApplication.java
├── src/main/resources/
│   ├── templates/             # Vistas Thymeleaf
│   │   ├── admin/            # Panel de administración
│   │   ├── fragments/        # Fragmentos reutilizables
│   │   └── *.html            # Páginas principales
│   ├── static/               # Archivos estáticos
│   │   ├── css/             # Estilos CSS
│   │   ├── js/              # JavaScript
│   │   └── img/             # Imágenes
│   ├── application.properties # Configuración principal
│   └── importar_datos.sql    # Datos de prueba
└── pom.xml                   # Dependencias Maven
```

## 🔌 API REST

La aplicación expone endpoints REST para integración con otros sistemas:

### Autenticación
```http
POST /api/auth/login
POST /api/usuarios/registrar
```

### Productos
```http
GET /api/productos                    # Listar productos
GET /api/productos/{id}              # Obtener producto
POST /api/productos                  # Crear producto (ADMIN)
PUT /api/productos/{id}              # Actualizar producto (ADMIN)
DELETE /api/productos/{id}           # Eliminar producto (ADMIN)
```

### Carrito de Compras
```http
GET /api/carrito                     # Obtener carrito
POST /api/carrito/agregar            # Agregar producto
PUT /api/carrito/actualizar          # Actualizar cantidad
DELETE /api/carrito/eliminar/{id}    # Eliminar producto
```

### Pagos
```http
POST /api/pago/iniciar               # Iniciar pago Webpay
GET /api/pago/confirmar              # Confirmar pago
```

### Conversión de Monedas
```http
GET /api/divisa/tipo-cambio-actual   # Tipo de cambio actual
GET /api/divisa/convertir-precio     # Convertir precio
```

## 🔧 Troubleshooting

### Problemas Comunes

#### 1. Error de Conexión a Base de Datos
```
Error: Communications link failure
```
**Solución:**
- Verificar que MySQL esté ejecutándose
- Verificar credenciales en `application.properties`
- Verificar que la base de datos `ferremas_db` exista

#### 2. Puerto 8080 Ocupado
```
Error: Web server failed to start. Port 8080 was already in use.
```
**Solución:**
```bash
# Cambiar puerto en application.properties
server.port=8081
```

#### 3. Error de Compilación Java
```
Error: invalid target release: 21
```
**Solución:**
- Instalar Java JDK 21
- Verificar variable JAVA_HOME
- Verificar versión: `java -version`

#### 4. Error de Importación de Datos
```
Error: Duplicate entry for key 'PRIMARY'
```
**Solución:**
- Usar el archivo `importar_datos.sql` (limpia automáticamente)
- O vaciar la base de datos manualmente en phpMyAdmin

#### 5. Error de Permisos en Windows
```
Error: Access denied for user 'root'@'localhost'
```
**Solución:**
- Ejecutar XAMPP como administrador
- O crear un usuario específico en MySQL

### Logs de la Aplicación

Los logs se muestran en la consola durante la ejecución. Para más detalles:

```properties
# En application.properties
logging.level.cl.ferremas=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Verificar Estado de la Aplicación

1. **Aplicación funcionando**: http://localhost:8080
2. **Base de datos conectada**: Ver logs de inicio
3. **Datos cargados**: Verificar en phpMyAdmin

## 👨‍💻 Autor

**Daniel Ávila**
- **GitHub**: [@DanielAndAvilaj](https://github.com/DanielAndAvilaj)
- **Proyecto**: Sistema de Gestión Ferretera
- **Asignatura**: ASY5131 Integración de Plataformas

---

## 📝 Notas Importantes

### Para el Profesor

1. **La aplicación está completamente funcional** y lista para evaluar
2. **Todos los datos de prueba están incluidos** en el archivo SQL
3. **La documentación es completa** y actualizada
4. **El código sigue buenas prácticas** de desarrollo
5. **La interfaz es intuitiva** y fácil de usar

### Características Destacadas

- ✅ **Arquitectura MVC** bien estructurada
- ✅ **Patrones de diseño** implementados (Repository, Service, DTO)
- ✅ **Seguridad robusta** con Spring Security
- ✅ **Integración con APIs externas** (Webpay, Banco Central)
- ✅ **Interfaz responsiva** y moderna
- ✅ **Base de datos optimizada** con relaciones correctas
- ✅ **Código limpio** y documentado
- ✅ **Manejo de errores** completo

---

⭐ **¡Gracias por revisar este proyecto!** ⭐

Si encuentras algún problema o tienes preguntas, no dudes en contactarme.