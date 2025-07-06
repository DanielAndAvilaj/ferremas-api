# 📋 Guía de Instalación Paso a Paso - Ferremas

> **Guía completa para ejecutar la aplicación Ferremas en cualquier computadora**

## 🎯 Objetivo
Esta guía te permitirá ejecutar la aplicación Ferremas sin problemas en tu computadora, siguiendo pasos simples y verificables.

## ⏱️ Tiempo Estimado
- **Instalación**: 15-20 minutos
- **Configuración**: 5-10 minutos
- **Verificación**: 5 minutos

---

## 📋 Checklist de Requisitos

Antes de comenzar, verifica que tengas:

- [ ] **Java JDK 21** instalado
- [ ] **Maven 3.6+** instalado
- [ ] **XAMPP** instalado (o MySQL standalone)
- [ ] **Git** instalado
- [ ] **Conexión a internet** (para descargar dependencias)

### Verificar Java
```bash
java -version
```
**Debe mostrar**: `openjdk version "21.x.x"` o similar

### Verificar Maven
```bash
mvn -version
```
**Debe mostrar**: `Apache Maven 3.6.x` o superior

---

## 🚀 Instalación Paso a Paso

### Paso 1: Descargar el Proyecto

```bash
# Clonar el repositorio
git clone https://github.com/DanielAndAvilaj/ferremas-api.git

# Entrar al directorio
cd ferremas-api

# Verificar que estás en el directorio correcto
ls
```
**Debes ver**: `pom.xml`, `src/`, `README.md`, etc.

### Paso 2: Configurar Base de Datos

#### 2.1 Iniciar XAMPP
1. **Abrir XAMPP Control Panel**
2. **Hacer clic en "Start"** para MySQL
3. **Hacer clic en "Start"** para Apache (opcional, para phpMyAdmin)
4. **Verificar que ambos estén en verde**

#### 2.2 Crear Base de Datos
1. **Abrir navegador**: http://localhost/phpmyadmin
2. **Hacer clic en "Nueva"** (izquierda)
3. **Nombre de la base de datos**: `ferremas_db`
4. **Cotejamiento**: `utf8mb4_unicode_ci`
5. **Hacer clic en "Crear"**

#### 2.3 Importar Datos
1. **Seleccionar la base de datos**: `ferremas_db`
2. **Ir a pestaña "Importar"**
3. **Hacer clic en "Seleccionar archivo"**
4. **Buscar y seleccionar**: `src/main/resources/importar_datos.sql`
5. **Hacer clic en "Continuar"**

**✅ Verificación**: Debes ver mensaje "La consulta SQL se ha ejecutado correctamente"

### Paso 3: Verificar Configuración

#### 3.1 Revisar application.properties
El archivo `src/main/resources/application.properties` debe contener:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ferremas_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
```

**Si usas MySQL standalone**, cambia las credenciales según tu configuración.

### Paso 4: Compilar y Ejecutar

#### 4.1 Compilar el Proyecto
```bash
# Limpiar y compilar
mvn clean compile
```

**✅ Verificación**: Debe mostrar "BUILD SUCCESS"

#### 4.2 Ejecutar la Aplicación
```bash
# Ejecutar Spring Boot
mvn spring-boot:run
```

**✅ Verificación**: Debes ver algo como:
```
Started FerremasApiApplication in X.XXX seconds
```

---

## 🔍 Verificación de la Instalación

### 1. Verificar que la Aplicación Funciona
1. **Abrir navegador**: http://localhost:8080
2. **Debes ver**: Página principal de Ferremas

### 2. Verificar Base de Datos
1. **Ir a**: http://localhost/phpmyadmin
2. **Seleccionar**: `ferremas_db`
3. **Verificar tablas**: `usuario`, `producto`, `sucursal`, etc.

### 3. Probar Login
1. **Ir a**: http://localhost:8080/login
2. **Credenciales admin**:
   - Email: `admin@ferremas.cl`
   - Password: `123456`
3. **Debes acceder** al dashboard

---

## 🔑 Credenciales de Prueba

### Usuario Administrador
- **Email**: `admin@ferremas.cl`
- **Password**: `123456`
- **Funciones**: Acceso completo a todas las funcionalidades

### Usuarios Normales
- **Email**: `juan@ejemplo.com`
- **Password**: `123456`
- **Email**: `maria@ejemplo.com`
- **Password**: `123456`

---

## 🎮 Funcionalidades para Probar

### Como Usuario Normal
1. **Navegar catálogo**: http://localhost:8080/catalogo
2. **Agregar productos al carrito**
3. **Ver favoritos**: http://localhost:8080/mis-favoritos
4. **Procesar compra**: Ir al carrito y hacer checkout

### Como Administrador
1. **Panel admin**: http://localhost:8080/admin/dashboard
2. **Gestionar productos**: http://localhost:8080/admin/productos
3. **Gestionar usuarios**: http://localhost:8080/admin/usuarios
4. **Ver reportes**: http://localhost:8080/admin/reportes

---

## 🚨 Solución de Problemas

### Error: "Port 8080 already in use"
```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error: "Communications link failure"
1. **Verificar que MySQL esté ejecutándose en XAMPP**
2. **Verificar credenciales en application.properties**
3. **Verificar que la base de datos exista**

### Error: "Java version not supported"
```bash
# Instalar Java 21
# En macOS: brew install openjdk@21
# En Windows: Descargar desde Oracle
# En Linux: sudo apt install openjdk-21-jdk
```

### Error: "Maven not found"
```bash
# Instalar Maven
# En macOS: brew install maven
# En Windows: Descargar desde Apache Maven
# En Linux: sudo apt install maven
```

### Error: "Duplicate entry for key 'PRIMARY'"
1. **Vaciar la base de datos** en phpMyAdmin
2. **Volver a importar** `importar_datos.sql`

---

## 📞 Contacto

Si encuentras algún problema:

- **Autor**: Daniel Ávila
- **GitHub**: [@DanielAndAvilaj](https://github.com/DanielAndAvilaj)
- **Proyecto**: Sistema de Gestión Ferretera

---

## ✅ Checklist Final

- [ ] La aplicación inicia sin errores
- [ ] Puedo acceder a http://localhost:8080
- [ ] Puedo hacer login con admin@ferremas.cl
- [ ] Veo productos en el catálogo
- [ ] Puedo agregar productos al carrito
- [ ] El panel de administración funciona
- [ ] La base de datos tiene datos

**🎉 ¡Felicitaciones! La aplicación está funcionando correctamente.**

---

*Esta guía fue creada para facilitar la evaluación del proyecto. Si tienes alguna pregunta, no dudes en contactarme.* 