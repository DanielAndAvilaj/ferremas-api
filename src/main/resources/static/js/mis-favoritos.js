document.addEventListener('DOMContentLoaded', function () {
    setupFavoritos();
    setupCarrito();
});

function setupFavoritos() {
    document.querySelectorAll('.btn-quitar-fav').forEach(btn => {
        btn.addEventListener('click', function () {
            const id = this.getAttribute('data-id');
            const card = this.closest('.favorito-card');
            
            // Feedback visual inmediato
            this.style.pointerEvents = 'none';
            this.innerHTML = '💫';
            this.style.transform = 'scale(0.8) rotate(180deg)';
            
            fetch(`/api/favoritos/${id}`, { method: 'DELETE' })
                .then(r => {
                    if (r.ok) {
                        // Animación de eliminación
                        card.style.animation = 'fadeOutSlide 0.5s ease-out';
                        setTimeout(() => {
                            card.remove();
                            actualizarContadorFavoritos();
                            mostrarMensaje('Producto quitado de favoritos', 'success');
                        }, 500);
                    } else {
                        // Restaurar estado en caso de error
                        this.innerHTML = '❌';
                        this.style.transform = '';
                        this.style.pointerEvents = '';
                        mostrarMensaje('Error al quitar favorito', 'error');
                    }
                })
                .catch(() => {
                    // Restaurar estado en caso de error de red
                    this.innerHTML = '❌';
                    this.style.transform = '';
                    this.style.pointerEvents = '';
                    mostrarMensaje('Error de conexión', 'error');
                });
        });
    });
}

function setupCarrito() {
    document.querySelectorAll('.btn-agregar-carrito').forEach(btn => {
        btn.addEventListener('click', function () {
            const productoId = this.getAttribute('data-id');
            if (!productoId) return mostrarMensaje('Error: No se pudo obtener el ID del producto', 'error');
            
            const cantidad = 1;
            let sessionId = localStorage.getItem('carritoSessionId');
            if (!sessionId) {
                sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
                localStorage.setItem('carritoSessionId', sessionId);
            }
            
            const btnOriginal = this.innerHTML;
            this.innerHTML = '🔄';
            this.disabled = true;
            
            fetch('/api/carrito/agregar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `sessionId=${sessionId}&productoId=${productoId}&cantidad=${cantidad}`
            })
            .then(response => response.json())
            .then(data => {
                this.innerHTML = btnOriginal;
                this.disabled = false;
                if (data.error) {
                    mostrarMensaje('Error: ' + data.error, 'error');
                } else {
                    mostrarMensaje('¡Producto agregado al carrito!', 'success');
                    actualizarContadorCarrito();
                }
            })
            .catch(() => {
                this.innerHTML = btnOriginal;
                this.disabled = false;
                mostrarMensaje('Error de conexión al agregar al carrito', 'error');
            });
        });
    });
}

function actualizarContadorFavoritos() {
    const favoritosCards = document.querySelectorAll('.favorito-card');
    const contador = document.querySelector('.favoritos-count');
    if (contador) {
        const count = favoritosCards.length;
        contador.textContent = count + ' producto' + (count !== 1 ? 's' : '') + ' favorito' + (count !== 1 ? 's' : '');
        
        // Si no hay favoritos, mostrar mensaje de vacío
        if (count === 0) {
            const container = document.querySelector('.favoritos-content');
            if (container) {
                container.innerHTML = `
                    <div class="favoritos-vacio">
                        <div class="favoritos-vacio-icon">💔</div>
                        <h3>No tienes productos favoritos</h3>
                        <p>Aún no has agregado productos a tus favoritos. ¡Explora el catálogo y encuentra productos increíbles!</p>
                        <a href="/catalogo" class="btn btn-primary">Ir al catálogo</a>
                    </div>
                `;
            }
        }
    }
}

function actualizarContadorCarrito() {
    let sessionId = localStorage.getItem('carritoSessionId');
    if (!sessionId) return;
    
    fetch(`/api/carrito/items?sessionId=${sessionId}`)
        .then(response => response.json())
        .then(items => {
            const contador = document.getElementById('carritoContador');
            if (contador) {
                const count = items.reduce((acc, item) => acc + item.cantidad, 0);
                contador.textContent = count;
            }
        })
        .catch(() => {});
}

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

// Agregar estilos CSS dinámicamente
(function(){
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
        @keyframes fadeOutSlide {
            0% { transform: translateX(0); opacity: 1; }
            100% { transform: translateX(-100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);
})(); 