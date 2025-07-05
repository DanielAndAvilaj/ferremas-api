// catalogo.js - Catálogo público Ferremas

document.addEventListener('DOMContentLoaded', function () {
    const filtrosForm = document.getElementById('filtrosForm');
    const busquedaInput = document.getElementById('busquedaInput');
    const grid = document.getElementById('catalogoGrid');
    let page = 1;

    function getFiltros() {
        return {
            categoria: document.getElementById('categoriaFiltro').value,
            marca: document.getElementById('marcaFiltro').value,
            sucursal: document.getElementById('sucursalFiltro').value,
            precioMin: document.getElementById('precioMinFiltro').value,
            precioMax: document.getElementById('precioMaxFiltro').value,
            stock: document.getElementById('stockFiltro').checked,
            q: busquedaInput.value,
            page: page
        };
    }

    function renderProductos(productos, valorDolar) {
        if (!productos.length) {
            grid.innerHTML = `<div style="grid-column:1/-1;text-align:center;padding:3rem 1rem;"><h3>😕 No se encontraron productos con los filtros seleccionados.</h3></div>`;
            return;
        }
        grid.innerHTML = productos.map(producto => `
            <div class="product-card">
                <button class="btn-fav${producto.favorito ? ' fav-active' : ''}" data-id="${producto.id}" title="Agregar/Quitar Favorito">♥</button>
                <a href="/catalogo/producto/${producto.id}" class="product-link">
                    <img src="/img/productos/${producto.codigo}.jpg" class="product-img" alt="Imagen de producto" loading="lazy" onerror="this.src='/img/productos/default.jpg'">
                    <div class="product-title">${producto.nombre}</div>
                    <div class="product-brand">${producto.marca}</div>
                    <div class="product-category">${producto.categoria}</div>
                    <div class="product-desc">${producto.descripcion || ''}</div>
                    <div class="product-prices">
                        <span class="price-clp">CLP $${Number(producto.precio).toLocaleString('es-CL')}</span>
                        <span class="price-usd">USD $${(Number(producto.precio) / valorDolar).toLocaleString('en-US', {minimumFractionDigits:2, maximumFractionDigits:2})}</span>
                    </div>
                    <div class="product-stock">
                        ${producto.stock > 0 ? `<span class="stock-badge">Stock: ${producto.stock}</span>` : `<span class="stock-badge stock-zero">Sin stock</span>`}
                    </div>
                </a>
                <button class="btn-carrito-rapido" data-id="${producto.id}" ${producto.stock == 0 ? 'disabled' : ''} title="Agregar al carrito">🛒</button>
            </div>
        `).join('');
        // Re-activar listeners tras renderizado
        setupFavoritos();
        setupCarrito();
        actualizarContadorCarrito();
    }

    async function fetchProductos() {
        const filtros = getFiltros();
        const params = new URLSearchParams(filtros);
        const res = await fetch(`/api/catalogo/buscar?${params.toString()}`);
        const productos = await res.json();
        // Obtener valorDolar del DOM (inyectado en template)
        const valorDolar = Number(document.body.getAttribute('data-valor-dolar')) || 900;
        renderProductos(productos, valorDolar);
    }

    filtrosForm.addEventListener('submit', function (e) {
        e.preventDefault();
        page = 1;
        fetchProductos();
    });

    busquedaInput.addEventListener('input', function () {
        page = 1;
        fetchProductos();
    });

    // Lazy loading de imágenes (nativo con loading="lazy")
    // Paginación dinámica (si se implementa en backend, aquí se puede agregar)

    // Inicializar valorDolar en body para JS
    if (window.valorDolarThymeleaf) {
        document.body.setAttribute('data-valor-dolar', window.valorDolarThymeleaf);
    }

    setTimeout(() => {
        setupFavoritos();
        setupCarrito();
        actualizarContadorCarrito();
    }, 200); // Espera a que el DOM y los productos estén renderizados
});

// Mostrar mensajes amigables
function mostrarMensaje(mensaje, tipo = 'info') {
    const mensajeDiv = document.createElement('div');
    mensajeDiv.style.cssText = `
        position: fixed; top: 20px; right: 20px; padding: 15px 20px; border-radius: 8px;
        color: white; font-weight: 600; z-index: 10000; max-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15); animation: slideIn 0.3s ease-out;`;
    if (tipo === 'error') mensajeDiv.style.background = '#e74c3c';
    else if (tipo === 'success') mensajeDiv.style.background = '#27ae60';
    else if (tipo === 'warning') mensajeDiv.style.background = '#f39c12';
    else mensajeDiv.style.background = '#3498db';
    mensajeDiv.textContent = mensaje;
    document.body.appendChild(mensajeDiv);
    setTimeout(() => {
        mensajeDiv.style.animation = 'slideOut 0.3s ease-in';
        setTimeout(() => { mensajeDiv.remove(); }, 300);
    }, 4000);
}

(function(){
    const style = document.createElement('style');
    style.textContent = `@keyframes slideIn {from{transform:translateX(100%);opacity:0;}to{transform:translateX(0);opacity:1;}}
    @keyframes slideOut {from{transform:translateX(0);opacity:1;}to{transform:translateX(100%);opacity:0;}}`;
    document.head.appendChild(style);
})();

function actualizarContadorCarrito() {
    let sessionId = localStorage.getItem('carritoSessionId');
    if (!sessionId) return;
    fetch(`/api/carrito/items?sessionId=${sessionId}`)
        .then(response => response.json())
        .then(items => {
            const contador = document.getElementById('carritoContador');
            if (contador) contador.textContent = items.length;
        })
        .catch(() => {});
}

function setupFavoritos() {
    document.querySelectorAll('.btn-fav').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const id = this.getAttribute('data-id');
            const isActive = this.classList.contains('fav-active');
            
            // Feedback visual inmediato
            this.style.pointerEvents = 'none';
            const originalText = this.innerHTML;
            
            // Animación de carga
            this.innerHTML = '💫';
            this.style.transform = 'scale(0.8) rotate(180deg)';
            
            fetch(`/api/favoritos/${id}`, {
                method: isActive ? 'DELETE' : 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    // Restaurar estado original
                    this.innerHTML = originalText;
                    this.style.transform = '';
                    this.style.pointerEvents = '';
                    mostrarMensaje('Debes iniciar sesión para usar favoritos', 'warning');
                    setTimeout(() => { window.location.href = '/login'; }, 2000);
                    return;
                }
                
                if (response.ok) {
                    // Animación de éxito
                    this.innerHTML = isActive ? '💔' : '💖';
                    this.style.transform = 'scale(1.2)';
                    
                    setTimeout(() => {
                        this.classList.toggle('fav-active');
                        this.innerHTML = '♥';
                        this.style.transform = '';
                        this.style.pointerEvents = '';
                        
                        // Mensaje de confirmación
                        const mensaje = isActive ? 'Producto quitado de favoritos' : 'Producto agregado a favoritos';
                        mostrarMensaje(mensaje, 'success');
                        
                        // Si se quitó de favoritos, agregar efecto de "desaparición"
                        if (isActive) {
                            this.style.animation = 'fadeOut 0.3s ease-out';
                            setTimeout(() => {
                                this.style.animation = '';
                            }, 300);
                        }
                    }, 300);
                } else {
                    // Restaurar estado original en caso de error
                    this.innerHTML = originalText;
                    this.style.transform = '';
                    this.style.pointerEvents = '';
                    return response.text().then(text => mostrarMensaje('Error al actualizar favoritos: ' + text, 'error'));
                }
            })
            .catch(() => {
                // Restaurar estado original en caso de error de conexión
                this.innerHTML = originalText;
                this.style.transform = '';
                this.style.pointerEvents = '';
                mostrarMensaje('Error de conexión al actualizar favoritos', 'error');
            });
        });
    });
}

function setupCarrito() {
    document.querySelectorAll('.btn-carrito-rapido').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const productoId = this.getAttribute('data-id');
            if (!productoId) return mostrarMensaje('Error: No se pudo obtener el ID del producto', 'error');
            const cantidad = 1;
            let sessionId = localStorage.getItem('carritoSessionId');
            if (!sessionId) {
                sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
                localStorage.setItem('carritoSessionId', sessionId);
            }
            const btnOriginal = this.textContent;
            this.textContent = '🔄';
            this.disabled = true;
            fetch('/api/carrito/agregar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `sessionId=${sessionId}&productoId=${productoId}&cantidad=${cantidad}`
            })
            .then(response => response.json())
            .then(data => {
                this.textContent = btnOriginal;
                this.disabled = false;
                if (data.error) mostrarMensaje('Error: ' + data.error, 'error');
                else {
                    mostrarMensaje('¡Producto agregado al carrito!', 'success');
                    actualizarContadorCarrito();
                }
            })
            .catch(() => {
                this.textContent = btnOriginal;
                this.disabled = false;
                mostrarMensaje('Error de conexión al agregar al carrito', 'error');
            });
        });
    });
} 