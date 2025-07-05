// checkout.js

document.addEventListener('DOMContentLoaded', function () {
    cargarResumen();
    const form = document.querySelector('.checkout-form');
    if (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            const nombre = document.getElementById('nombre').value.trim();
            const direccion = document.getElementById('direccion').value.trim();
            if (!nombre || !direccion) return mostrarMensaje('Completa tus datos', 'warning');
            fetch(`/api/carrito/checkout?sessionId=${getSessionId()}&nombre=${encodeURIComponent(nombre)}&direccion=${encodeURIComponent(direccion)}`, { method: 'POST' })
                .then(r => r.json())
                .then(res => {
                    if (res.url) {
                        window.location.href = res.full_url || res.url;
                    } else if (res.error) {
                        mostrarMensaje(res.mensaje || 'Error en el pago', 'error');
                    }
                })
                .catch(() => mostrarMensaje('Error de red', 'error'));
        });
    }
});

function cargarResumen() {
    fetch(`/api/carrito/items?sessionId=${getSessionId()}`)
        .then(r => r.json())
        .then(items => {
            // Aquí deberías renderizar el resumen del carrito si tienes un bloque para ello
            // Por ejemplo:
            // const lista = document.getElementById('resumenLista');
            // const total = items.reduce((acc, i) => acc + i.producto.precio.clp * i.cantidad, 0);
            // lista.innerHTML = items.map(i => `<li>${i.producto.nombre} x${i.cantidad} - CLP $${i.producto.precio.clp}</li>`).join('');
            // document.getElementById('resumenTotal').textContent = 'Total: CLP $' + total;
        });
}

function getSessionId() {
    let sid = localStorage.getItem('carritoSessionId');
    if (!sid) {
        sid = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        localStorage.setItem('carritoSessionId', sid);
    }
    return sid;
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

(function(){
    const style = document.createElement('style');
    style.textContent = `@keyframes slideIn {from{transform:translateX(100%);opacity:0;}to{transform:translateX(0);opacity:1;}}
    @keyframes slideOut {from{transform:translateX(0);opacity:1;}to{transform:translateX(100%);opacity:0;}}`;
    document.head.appendChild(style);
})(); 