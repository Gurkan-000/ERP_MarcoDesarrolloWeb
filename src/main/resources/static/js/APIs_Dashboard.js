const BASE_URL = 'http://localhost:8080/api';

// 1. Función para verificar si hay un usuario logueado en el backend
const verificarSesionActiva = async () => {
    try {
        const response = await fetch(`${BASE_URL}/usuario/sesionActiva`);
        if (!response.ok) return null;
        return await response.json();
    } catch (error) {
        console.error("Error al verificar la sesión:", error);
        return null;
    }
}

// 2. Función para ocultar secciones del menú según el rol
const cargarSecciones = (sesionActiva) => {
    if (sesionActiva.rol === "Administrador") return;

    const ocultarMenus = (urls) => {
        urls.forEach(url => {
            const el = document.querySelector(`a[href='${url}']`);
            if(el) el.classList.add("hidden");
        });
    }

    if (sesionActiva.rol === "Mesero") {
        ocultarMenus(['caja.html', 'inventario.html', 'catalogo/catalogoProducto.html', 'usuario.html', 'configuracion.html']);
    }

    if (sesionActiva.rol === "Cajero") {
        ocultarMenus(['inventario.html', 'catalogo/catalogoProducto.html', 'usuario.html', 'configuracion.html']);
    }
}

// 3. Función para cerrar sesión
const cerrarSesion = async (e) => {
    e.preventDefault();
    try {
        const response = await fetch(`${BASE_URL}/usuario/cerrarSesion`, { method: 'PUT' });
        if (response.ok) {
            window.location.href = 'login.html'; 
        } else {
            console.error("Ocurrió un error al cerrar sesión");
        }
    } catch (error) {
        console.error(error);
    }
}

// 4. Función para dibujar las gráficas de Chart.js
const renderizarGraficas = (data) => {
    // Gráfico de Barras (Ingresos vs Egresos)
    const ctxCaja = document.getElementById('graficoCaja').getContext('2d');
    new Chart(ctxCaja, {
        type: 'bar',
        data: {
            labels: ['Ingresos', 'Egresos'],
            datasets: [{
                label: 'Soles (S/)',
                data: [data.ingresos, data.egresos], 
                backgroundColor: [
                    'rgba(34, 197, 94, 0.6)', // Verde 
                    'rgba(239, 68, 68, 0.6)'  // Rojo 
                ],
                borderColor: [
                    'rgb(34, 197, 94)',
                    'rgb(239, 68, 68)'
                ],
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: { 
            responsive: true, 
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false } // Oculta la leyenda superior para verse más limpio
            }
        }
    });

    // Gráfico de Dona (Tipos de Pedido)
    const ctxPedidos = document.getElementById('graficoPedidos').getContext('2d');
    new Chart(ctxPedidos, {
        type: 'doughnut',
        data: {
            labels: ['Local (Mesa)', 'Para Llevar', 'Delivery'],
            datasets: [{
                data: [data.pedidosLocal, data.pedidosLlevar, data.pedidosDelivery], 
                backgroundColor: [
                    'rgba(59, 130, 246, 0.7)', // Azul
                    'rgba(249, 115, 22, 0.7)', // Naranja brand
                    'rgba(168, 85, 247, 0.7)'  // Morado
                ],
                borderWidth: 0,
                hoverOffset: 4
            }]
        },
        options: { 
            responsive: true, 
            maintainAspectRatio: false 
        }
    });
}

// 5. Función para cargar las estadísticas y pintar los datos
const cargarEstadisticas = async () => {
    try {
        // CÓDIGO REAL: Llama al backend de Spring Boot
        const response = await fetch(`${BASE_URL}/dashboard-api/estadisticas`);
        if (response.ok) {
            const data = await response.json();
            
            // Llenar tarjetas superiores
            document.querySelector('.text-success').textContent = `S/ ${Number(data.ventasHoy).toFixed(2)}`;
            document.querySelectorAll('.stat-card h4')[1].textContent = data.pedidosTotales;
            document.querySelector('.text-warning').textContent = `${data.mesasOcupadas}/6`;
            document.querySelectorAll('.stat-card h4')[3].textContent = data.productosTotales;

            // Dibujar las gráficas con los datos de la base de datos
            renderizarGraficas(data);
        } else {
            console.error("Error al obtener estadísticas del servidor");
        }
    } catch (error) {
        console.error("Error al cargar estadísticas:", error);
    }
}

// 6. Inicializador cuando la página carga
document.addEventListener("DOMContentLoaded", async (e) => {
    e.preventDefault();

    const sesionActiva = await verificarSesionActiva();

    if (sesionActiva == null) {
        window.location.href = 'login.html'; 
        return;
    }

    // Actualizamos la UI del usuario
    const userInfoP = document.querySelector(".user-meta p");
    const userInfoSpan = document.querySelector(".user-meta span");
    const userAvatar = document.querySelector(".user-avatar");

    if (userInfoP) userInfoP.textContent = sesionActiva.nombre;
    if (userInfoSpan) userInfoSpan.textContent = sesionActiva.rol;
    if (userAvatar) userAvatar.textContent = sesionActiva.rol.charAt(0).toUpperCase();

    // Validar permisos del menú
    cargarSecciones(sesionActiva);

    // Configurar cierre de sesión
    const btnCerrarSesion = document.querySelector(".sidebar-footer .btn-ghost");
    if (btnCerrarSesion) {
        btnCerrarSesion.removeAttribute("onclick");
        btnCerrarSesion.addEventListener("click", cerrarSesion);
    }

    // Finalmente, cargar los números y gráficas del Dashboard
    cargarEstadisticas();
});