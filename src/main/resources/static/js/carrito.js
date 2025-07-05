// carrito.js

let emailAutenticado = null;

// Obtener email del usuario autenticado al cargar la página
fetch('/api/auth/me')
    .then(r => r.json())
    .then(data => {
        if (data && data.authenticated && data.user && data.user.email) {
            emailAutenticado = data.user.email;
        }
    })
    .catch(error => {
        console.log('No se pudo obtener el email del usuario autenticado');
    });

document.addEventListener('DOMContentLoaded', function () {
    renderCarrito();
    // Contador en navbar
    actualizarContadorCarrito();
});

function getSessionId() {
    let sid = localStorage.getItem('carritoSessionId');
    if (!sid) {
        sid = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        localStorage.setItem('carritoSessionId', sid);
    }
    return sid;
}

function actualizarContadorCarrito() {
    fetch(`/api/carrito/items?sessionId=${getSessionId()}`)
        .then(r => r.json())
        .then(items => {
            const count = items.reduce((acc, i) => acc + i.cantidad, 0);
            const badge = document.getElementById('carritoContador');
            if (badge) badge.textContent = count;
        });
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

function renderCarrito() {
    fetch(`/api/carrito/items?sessionId=${getSessionId()}`)
        .then(r => r.json())
        .then(items => {
            console.log('🔍 DEBUG: Items del carrito recibidos:', items);
            items.forEach(item => {
                console.log('🔍 DEBUG: Item ID:', item.id, 'Tipo:', typeof item.id, 'Producto:', item.producto?.nombre);
            });
            
            const tbody = document.getElementById('carritoItems');
            const totalSpan = document.getElementById('carritoTotal');
            const vacioDiv = document.getElementById('carritoVacio');
            const contenidoDiv = document.getElementById('carritoContenido');
            const checkoutDiv = document.getElementById('carritoCheckout');
            const btnIrAPagar = document.getElementById('btnIrAPagar');
            if (!tbody || !totalSpan || !vacioDiv || !contenidoDiv || !checkoutDiv) return;
            // Oculta ambos por defecto para evitar parpadeos o doble render
            vacioDiv.style.display = 'none';
            contenidoDiv.style.display = 'none';
            checkoutDiv.style.display = 'none';
            tbody.innerHTML = '';
            let total = 0;
            if (items.length === 0) {
                vacioDiv.style.display = '';
                totalSpan.textContent = '0';
                checkoutDiv.style.display = 'none';
                if (btnIrAPagar) btnIrAPagar.style.display = 'none';
                actualizarContadorCarrito();
                return;
            }
            contenidoDiv.style.display = '';
            checkoutDiv.style.display = 'flex';
            if (btnIrAPagar) {
                btnIrAPagar.style.display = '';
                btnIrAPagar.disabled = false;
            }
            items.forEach(item => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${item.producto ? item.producto.nombre : 'Producto eliminado'}</td>
                    <td>$${item.precioUnitario ? item.precioUnitario.toLocaleString('es-CL') : '0'}</td>
                    <td>
                        <input type="number" min="1" value="${item.cantidad}" data-id="${item.id}" class="carrito-cantidad" style="width:60px;">
                    </td>
                    <td>$${(item.precioUnitario * item.cantidad).toLocaleString('es-CL')}</td>
                    <td><button class="carrito-eliminar" data-id="${item.id}">Eliminar</button></td>
                `;
                tbody.appendChild(tr);
                total += item.precioUnitario * item.cantidad;
            });
            totalSpan.textContent = total.toLocaleString('es-CL');
            // Reasignar listeners después de renderizar
            tbody.querySelectorAll('.carrito-cantidad').forEach(input => {
                input.addEventListener('change', function () {
                    const id = this.getAttribute('data-id');
                    const cantidad = parseInt(this.value);
                    console.log('🔍 DEBUG: Actualizando cantidad - ItemId:', id, 'Cantidad:', cantidad);
                    
                    if (cantidad < 1) {
                        mostrarMensaje('Cantidad inválida', 'error');
                        return;
                    }
                    
                    fetch(`/api/carrito/item/${id}?sessionId=${getSessionId()}&cantidad=${cantidad}`, { method: 'PUT' })
                        .then(r => {
                            console.log('🔍 DEBUG: Respuesta del servidor - Status:', r.status);
                            if (!r.ok) {
                                return r.json().then(errorData => {
                                    throw new Error(errorData.error || 'Error al actualizar cantidad');
                                });
                            }
                            return r.json();
                        })
                        .then(data => {
                            console.log('🔍 DEBUG: Cantidad actualizada exitosamente:', data);
                            mostrarMensaje('Cantidad actualizada correctamente', 'success');
                            renderCarrito();
                        })
                        .catch(error => {
                            console.error('🔍 DEBUG: Error al actualizar cantidad:', error);
                            mostrarMensaje(error.message || 'Error al actualizar cantidad', 'error');
                        });
                });
            });
            tbody.querySelectorAll('.carrito-eliminar').forEach(btn => {
                btn.addEventListener(
                    'click',
                    function () {
                        const id = this.getAttribute('data-id');
                        fetch(`/api/carrito/item/${id}?sessionId=${getSessionId()}`, { method: 'DELETE' })
                            .then(r => r.json())
                            .then(() => {
                                mostrarMensaje('Producto eliminado del carrito', 'success');
                                renderCarrito();
                            })
                            .catch(() => mostrarMensaje('Error al eliminar producto', 'error'));
                    }
                );
            });
            // Asignar evento al botón Ir a pagar cada vez que se renderiza
            const btnPagar = document.getElementById('btnIrAPagar');
            if (btnPagar) {
                btnPagar.disabled = false;
                btnPagar.style.display = '';
                
                // Remover eventos previos usando removeEventListener
                const oldClickHandler = btnPagar._clickHandler;
                if (oldClickHandler) {
                    btnPagar.removeEventListener('click', oldClickHandler);
                }
                
                // Crear nuevo handler
                const clickHandler = function () {
                    let email = emailAutenticado;
                    if (!email) {
                        email = prompt('Ingresa tu correo electrónico para el pago:');
                        if (!email || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
                            mostrarMensaje('Debes ingresar un correo válido para continuar', 'error');
                            return;
                        }
                    }
                    btnPagar.disabled = true;
                    btnPagar.textContent = 'Redirigiendo a Webpay...';
                    fetch(`/api/carrito/checkout`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: `sessionId=${encodeURIComponent(getSessionId())}&emailCliente=${encodeURIComponent(email)}`
                    })
                    .then(r => {
                        return r.json();
                    })
                    .then(res => {
                        if (res && res.full_url) {
                            window.location.href = res.full_url;
                        } else if (res && res.url && res.token) {
                            window.location.href = res.url + '?token_ws=' + res.token;
                        } else {
                            mostrarMensaje(res.mensaje || 'Error inesperado al iniciar el pago', 'error');
                            btnPagar.disabled = false;
                            btnPagar.textContent = 'Ir a pagar';
                        }
                    })
                    .catch(error => {
                        mostrarMensaje('Error al conectar con Webpay', 'error');
                        btnPagar.disabled = false;
                        btnPagar.textContent = 'Ir a pagar';
                    });
                };
                
                // Guardar referencia al handler para poder removerlo después
                btnPagar._clickHandler = clickHandler;
                btnPagar.addEventListener('click', clickHandler);
            }
            actualizarContadorCarrito();
        })
        .catch(() => {
            document.getElementById('carritoVacio').style.display = '';
            document.getElementById('carritoContenido').style.display = 'none';
            document.getElementById('carritoTotal').textContent = '0';
            const checkoutDiv = document.getElementById('carritoCheckout');
            if (checkoutDiv) checkoutDiv.style.display = 'none';
            actualizarContadorCarrito();
        });
}

(function(){
    const style = document.createElement('style');
    style.textContent = `@keyframes slideIn {from{transform:translateX(100%);opacity:0;}to{transform:translateX(0);opacity:1;}}
    @keyframes slideOut {from{transform:translateX(0);opacity:1;}to{transform:translateX(100%);opacity:0;}}`;
    document.head.appendChild(style);
})(); 