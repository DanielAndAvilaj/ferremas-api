// sucursales.js - Funcionalidad para la página de sucursales

// Variables globales
let mapa;
let marcadores = [];
let sucursales = [];
let ciudades = [];

// Inicializar la página de sucursales
function inicializarSucursales() {
    console.log('🔍 Inicializando página de sucursales...');
    
    // Obtener datos desde Thymeleaf (se inyectan en el HTML)
    sucursales = window.sucursalesData || [];
    ciudades = window.ciudadesData || [];
    
    console.log('📊 Sucursales cargadas:', sucursales.length);
    console.log('🏙️ Ciudades disponibles:', ciudades);
    
    if (sucursales.length > 0) {
        inicializarMapa();
        configurarEventListeners();
        actualizarEstadisticas();
    } else {
        mostrarMensajeNoSucursales();
    }
}

// Inicializar mapa con Leaflet
function inicializarMapa() {
    if (sucursales.length === 0) return;
    
    console.log('🗺️ Inicializando mapa...');
    
    // Centro del mapa (Chile)
    const centroLat = -33.4489;
    const centroLng = -70.6693;
    
    // Crear mapa
    mapa = L.map('mapa').setView([centroLat, centroLng], 6);
    
    // Agregar capa de tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 18
    }).addTo(mapa);
    
    // Agregar marcadores para cada sucursal
    sucursales.forEach(sucursal => {
        if (sucursal.latitud && sucursal.longitud) {
            const marcador = L.marker([sucursal.latitud, sucursal.longitud])
                .addTo(mapa)
                .bindPopup(crearPopupSucursal(sucursal));
            
            marcadores.push({
                id: sucursal.id,
                marcador: marcador,
                sucursal: sucursal
            });
        }
    });
    
    console.log('✅ Mapa inicializado con', marcadores.length, 'marcadores');
}

// Crear contenido del popup del mapa
function crearPopupSucursal(sucursal) {
    return `
        <div class="mapa-popup">
            <div class="mapa-popup-titulo">${sucursal.nombre}</div>
            <div class="mapa-popup-info">${sucursal.ciudad}</div>
            <div class="mapa-popup-info">${sucursal.direccion || ''}</div>
            ${sucursal.telefono ? `<div class="mapa-popup-info">📞 ${sucursal.telefono}</div>` : ''}
            ${sucursal.email ? `<div class="mapa-popup-info">✉️ ${sucursal.email}</div>` : ''}
            ${sucursal.horarios ? `<div class="mapa-popup-info">🕒 ${sucursal.horarios}</div>` : ''}
        </div>
    `;
}

// Configurar event listeners
function configurarEventListeners() {
    console.log('🎯 Configurando event listeners...');
    
    // Filtros
    const filtroCiudad = document.getElementById('filtroCiudad');
    const filtroOrden = document.getElementById('filtroOrden');
    
    if (filtroCiudad) {
        filtroCiudad.addEventListener('change', filtrarSucursales);
    }
    
    if (filtroOrden) {
        filtroOrden.addEventListener('change', filtrarSucursales);
    }
    
    // Botones "Ver en mapa"
    document.querySelectorAll('.btn-direccion').forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            const lat = parseFloat(this.getAttribute('data-lat'));
            const lng = parseFloat(this.getAttribute('data-lng'));
            
            if (lat && lng) {
                centrarEnSucursal(lat, lng);
                resaltarCard(this.closest('.sucursal-card'));
            }
        });
    });
    
    // Click en cards de sucursales
    document.querySelectorAll('.sucursal-card').forEach(card => {
        card.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const sucursal = sucursales.find(s => s.id == id);
            
            if (sucursal && sucursal.latitud && sucursal.longitud) {
                centrarEnSucursal(sucursal.latitud, sucursal.longitud);
                resaltarCard(this);
            }
        });
    });
    
    console.log('✅ Event listeners configurados');
}

// Filtrar sucursales
function filtrarSucursales() {
    const ciudadSeleccionada = document.getElementById('filtroCiudad')?.value || '';
    const ordenSeleccionado = document.getElementById('filtroOrden')?.value || 'nombre';
    
    console.log('🔍 Filtrando sucursales:', { ciudadSeleccionada, ordenSeleccionado });
    
    const cards = document.querySelectorAll('.sucursal-card');
    let sucursalesVisibles = 0;
    
    cards.forEach(card => {
        const nombre = card.querySelector('.sucursal-nombre').textContent;
        const ciudad = card.querySelector('.sucursal-ciudad').textContent;
        
        let mostrar = true;
        
        // Filtro por ciudad
        if (ciudadSeleccionada && ciudad !== ciudadSeleccionada) {
            mostrar = false;
        }
        
        card.style.display = mostrar ? 'block' : 'none';
        if (mostrar) sucursalesVisibles++;
    });
    
    // Ordenar
    ordenarSucursales(ordenSeleccionado);
    
    // Actualizar estadísticas
    actualizarEstadisticas(sucursalesVisibles);
    
    console.log('✅ Filtrado completado:', sucursalesVisibles, 'sucursales visibles');
}

// Ordenar sucursales
function ordenarSucursales(criterio) {
    const lista = document.querySelector('.sucursales-lista');
    const cards = Array.from(document.querySelectorAll('.sucursal-card'));
    
    cards.sort((a, b) => {
        const nombreA = a.querySelector('.sucursal-nombre').textContent;
        const nombreB = b.querySelector('.sucursal-nombre').textContent;
        const ciudadA = a.querySelector('.sucursal-ciudad').textContent;
        const ciudadB = b.querySelector('.sucursal-ciudad').textContent;
        
        switch (criterio) {
            case 'nombre':
                return nombreA.localeCompare(nombreB);
            case 'ciudad':
                return ciudadA.localeCompare(ciudadB);
            default:
                return 0;
        }
    });
    
    cards.forEach(card => lista.appendChild(card));
}

// Centrar mapa en una sucursal específica
function centrarEnSucursal(lat, lng) {
    if (mapa) {
        mapa.setView([lat, lng], 15);
        console.log('📍 Mapa centrado en:', lat, lng);
    }
}

// Resaltar card de sucursal
function resaltarCard(cardSeleccionada) {
    document.querySelectorAll('.sucursal-card').forEach(card => {
        card.classList.remove('activa');
    });
    
    if (cardSeleccionada) {
        cardSeleccionada.classList.add('activa');
        cardSeleccionada.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
}

// Actualizar estadísticas
function actualizarEstadisticas(sucursalesVisibles = null) {
    const estadisticaNumero = document.querySelector('.estadistica-numero');
    if (estadisticaNumero) {
        const numero = sucursalesVisibles !== null ? sucursalesVisibles : sucursales.length;
        estadisticaNumero.textContent = numero;
    }
}

// Mostrar mensaje cuando no hay sucursales
function mostrarMensajeNoSucursales() {
    console.log('⚠️ No hay sucursales disponibles');
}

// Función para obtener ubicación del usuario (futura funcionalidad)
function obtenerUbicacionUsuario() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                console.log('📍 Ubicación del usuario:', lat, lng);
                
                // Aquí se podría implementar la funcionalidad de "sucursal más cercana"
                // centrarEnSucursal(lat, lng);
            },
            function(error) {
                console.log('❌ Error al obtener ubicación:', error.message);
            }
        );
    }
}

// Función para calcular distancia entre dos puntos (futura funcionalidad)
function calcularDistancia(lat1, lng1, lat2, lng2) {
    const R = 6371; // Radio de la Tierra en km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLng = (lng2 - lng1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
              Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
              Math.sin(dLng/2) * Math.sin(dLng/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Página de sucursales cargada');
    inicializarSucursales();
    
    // Event listener específico para el botón de volver
    const btnVolver = document.querySelector('.btn-volver');
    if (btnVolver) {
        console.log('🔍 Botón volver encontrado:', btnVolver);
        btnVolver.addEventListener('click', function(e) {
            console.log('🔄 Botón volver clickeado, redirigiendo a /catalogo');
            // No prevenir el comportamiento por defecto para que funcione la navegación
        });
    } else {
        console.log('❌ Botón volver no encontrado');
    }
});

// Exportar funciones para uso global (si es necesario)
window.SucursalesApp = {
    inicializarSucursales,
    filtrarSucursales,
    centrarEnSucursal,
    obtenerUbicacionUsuario
}; 