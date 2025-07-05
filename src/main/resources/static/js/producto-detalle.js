// producto-detalle.js

// Galería de imágenes
function cambiarImagen(el) {
    const imgPrincipal = document.getElementById('imgPrincipal');
    if (imgPrincipal) {
        imgPrincipal.src = el.src;
    }
    document.querySelectorAll('.galeria-thumb').forEach(t => t.classList.remove('selected'));
    el.classList.add('selected');
}

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

// Animaciones CSS para mensajes
(function(){
    const style = document.createElement('style');
    style.textContent = `@keyframes slideIn {from{transform:translateX(100%);opacity:0;}to{transform:translateX(0);opacity:1;}}
    @keyframes slideOut {from{transform:translateX(0);opacity:1;}to{transform:translateX(100%);opacity:0;}}`;
    document.head.appendChild(style);
})();

// Favoritos
function setupFavoritos() {
    document.querySelectorAll('.btn-fav').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            const id = this.getAttribute('data-id');
            const isActive = this.classList.contains('fav-active');
            fetch(`/api/favoritos/${id}`, {
                method: isActive ? 'DELETE' : 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    mostrarMensaje('Debes iniciar sesión para usar favoritos', 'warning');
                    setTimeout(() => { window.location.href = '/login'; }, 2000);
                    return;
                }
                if (response.ok) {
                    this.classList.toggle('fav-active');
                    mostrarMensaje(isActive ? 'Producto quitado de favoritos' : 'Producto agregado a favoritos', 'success');
                } else {
                    return response.text().then(text => mostrarMensaje('Error al actualizar favoritos: ' + text, 'error'));
                }
            })
            .catch(() => mostrarMensaje('Error de conexión al actualizar favoritos', 'error'));
        });
    });
}

// Carrito
function setupCarrito() {
    document.querySelectorAll('.btn-carrito').forEach(btn => {
        btn.addEventListener('click', function() {
            const productoId = this.getAttribute('data-id');
            if (!productoId) return mostrarMensaje('Error: No se pudo obtener el ID del producto', 'error');
            const cantidad = 1;
            let sessionId = localStorage.getItem('carritoSessionId');
            if (!sessionId) {
                sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
                localStorage.setItem('carritoSessionId', sessionId);
            }
            const btnOriginal = this.textContent;
            this.textContent = '🔄 Agregando...';
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

// Actualizar contador del carrito
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

document.addEventListener('DOMContentLoaded', function() {
    setupFavoritos();
    setupCarrito();
    actualizarContadorCarrito();
}); 