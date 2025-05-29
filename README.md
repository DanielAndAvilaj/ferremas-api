# Ferremás API

Este proyecto es una API REST desarrollada con **Spring Boot** para la gestión de productos, precios, sucursales y stock por sucursal, implementando control de acceso por roles (`ADMIN` y `USER`) y seguridad básica con **HTTP Basic Auth**.

## Requisitos

- Java 21
- Maven
- MySQL (recomendado vía XAMPP)
- Postman (para pruebas)

## Instalación

1. Clonar el repositorio:
   ```
   git clone https://github.com/tu-usuario/ferremas-api.git
   ```

2. Configurar la base de datos en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ferremas_db
   spring.datasource.username=root
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Crear una base de datos llamada `ferremas_db` en MySQL.

4. Compilar y ejecutar:
   ```
   mvn spring-boot:run
   ```

## Endpoints Principales

### Autenticación

- **Registro de usuario (abierto)**
  ```
  POST /api/usuarios/registrar
  Body JSON: { "username": "...", "password": "...", "rol": "ADMIN" | "USER" }
  ```

### Productos

- `GET /api/productos`: listar productos (ADMIN, USER)
- `POST /api/productos`: crear producto (ADMIN)
- `PUT /api/productos/{id}`: actualizar producto (ADMIN)
- `DELETE /api/productos/{id}`: eliminar producto (ADMIN)

### Precios

- `GET /api/precios`: listar precios de productos (ADMIN, USER)
- `POST /api/precios`: crear precio (ADMIN)

### Sucursales

- `GET /api/sucursales`: listar sucursales
- `POST /api/sucursales`: crear sucursal

### Stock por Sucursal

- `GET /api/stock-sucursal`: listar todos los stocks por sucursal
- `POST /api/stock-sucursal`: asignar stock a un producto en una sucursal

## Seguridad

- La autenticación es vía **HTTP Basic Auth**
- `ADMIN` puede crear, editar y eliminar
- `USER` puede consultar información

## Estructura del Proyecto

- `model`: entidades JPA (Producto, Precio, Sucursal, StockSucursal, Usuario)
- `repository`: interfaces JPA para acceso a datos
- `service`: lógica de negocio
- `controller`: manejo de endpoints
- `config`: configuración de seguridad

## Tecnologías

- Spring Boot 3.5.0
- Spring Security
- Spring Data JPA
- MySQL
- Swagger/OpenAPI
- Maven

## Autor

Desarrollado por Daniel Avila para la Evaluación Parcial 2 de la asignatura **ASY5131 Integración de Plataformas**.