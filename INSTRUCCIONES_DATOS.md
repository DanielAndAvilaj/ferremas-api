# 🚀 Instrucciones para Importar Datos de Prueba - Ferremas API

## ✅ Estado Actual
- ✅ La aplicación está funcionando en http://localhost:8080
- ✅ Las tablas se han creado automáticamente en MySQL
- ✅ Los usuarios de prueba ya están creados en la base de datos
- ⏳ **Pendiente**: Importar datos de productos y otros datos de prueba

## 📋 Pasos para Importar Datos de Prueba

### 1. Abrir phpMyAdmin
- Ve a: http://localhost/phpmyadmin
- Selecciona la base de datos: `ferremas_db`

### 2. Elegir opción de importación

#### Opción A: Limpiar y importar datos frescos (Recomendado)
- Haz clic en la pestaña **"Importar"** o **"SQL"**
- Selecciona el archivo: `src/main/resources/importar_datos.sql`
- Haz clic en **"Continuar"**
- **Este script limpiará todos los datos existentes antes de insertar los nuevos**

#### Opción B: Importar sin limpiar datos existentes
- Haz clic en la pestaña **"Importar"** o **"SQL"**
- Selecciona el archivo: `src/main/resources/importar_datos_sin_limpiar.sql`
- Haz clic en **"Continuar"**
- **Este script usará INSERT IGNORE para evitar errores de claves duplicadas**

### 3. Verificar Importación
- Ve a la pestaña **"Explorar"** para ver las tablas con datos
- Deberías ver:
  - 3 usuarios de prueba (ya creados automáticamente)
  - 3 sucursales
  - 6 productos
  - Stock por sucursal
  - Precios
  - Favoritos
  - Mensajes

## 🔑 Credenciales de Prueba

### Usuario Administrador
- **Email**: admin@ferremas.cl
- **Password**: 123456

### Usuarios Normales
- **Email**: juan@ejemplo.com
- **Password**: 123456
- **Email**: maria@ejemplo.com
- **Password**: 123456

## 🌐 URLs de la Aplicación

- **Página Principal**: http://localhost:8080
- **Catálogo**: http://localhost:8080/catalogo
- **Login**: http://localhost:8080/login
- **Registro**: http://localhost:8080/register
- **Dashboard**: http://localhost:8080/dashboard (requiere login)

## 🛠️ Si Hay Problemas

### Error de Claves Duplicadas (#1062)
Si ves el error "Entrada duplicada para la clave 'PRIMARY'":
1. **Usa el archivo `importar_datos.sql`** (que limpia automáticamente)
2. **O manualmente**: Ve a la pestaña **"Operaciones"** → **"Vaciar la base de datos"** → Vuelve a importar

### La Aplicación No Inicia
1. Verifica que MySQL esté corriendo en XAMPP
2. Verifica que la base de datos `ferremas_db` exista
3. Revisa los logs de la aplicación

---
**Nota**: Los roles no son una tabla, son un campo ENUM en la tabla usuario. No intentes insertar en una tabla rol ni usar rol_id.

## 🎉 ¡Listo!
Una vez importados los datos, podrás:
- Hacer login con las credenciales de prueba
- Ver productos en el catálogo
- Probar el carrito de compras
- Ver el dashboard con datos
- Probar todas las funcionalidades

---
**Nota**: El archivo `data_mysql.sql` está optimizado para MySQL y usa `ON DUPLICATE KEY UPDATE` para evitar errores si se ejecuta múltiples veces. 