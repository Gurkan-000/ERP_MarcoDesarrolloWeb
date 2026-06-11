const BASE_URL = 'http://localhost:8083/api';

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
            if (el) el.classList.add("hidden");
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


const cargarEstadisticas = async () => {
    try {

        const response = await fetch(`${BASE_URL}/dashboard-api/estadisticas`);
        if (response.ok) {
            const data = await response.json();

            document.querySelector('.text-success').textContent = `S/ ${Number(data.ventasHoy).toFixed(2)}`;
            document.querySelectorAll('.stat-card h4')[1].textContent = data.pedidosTotales;
            document.querySelector('.text-warning').textContent = `${data.mesasOcupadas}/6`;
            document.querySelectorAll('.stat-card h4')[3].textContent = data.productosTotales;
            
            renderizarGraficas(data);
        } else {
            console.error("Error al obtener estadísticas del servidor");
        }
    } catch (error) {
        console.error("Error al cargar estadísticas:", error);
    }
}

document.getElementById("btnCerrarSesion").addEventListener("click", async (e) => {
    e.preventDefault();

    try {

        const response = await fetch(`http://localhost:8083/api/usuario/cerrarSesion`, {
            method: 'PUT'
        });

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            window.location.href = 'login.html';
        }

    } catch (error) {
        console.log(error);
    }
});

document.addEventListener("DOMContentLoaded", async (e) => {
    e.preventDefault();

    const sesionActiva = await verificarSesionActiva();

    if (sesionActiva == null) {
        window.location.href = 'login.html';
        return;
    }
    if (typeof window.poblarTopbarUsuario === 'function') {
        window.poblarTopbarUsuario(sesionActiva);
    }

    const userInfoP = document.querySelector(".user-meta p");
    const userInfoSpan = document.querySelector(".user-meta span");
    const userAvatar = document.querySelector(".user-avatar");

    if (userInfoP) userInfoP.textContent = sesionActiva.nombre;
    if (userInfoSpan) userInfoSpan.textContent = sesionActiva.rol;
    if (userAvatar) userAvatar.textContent = sesionActiva.rol.charAt(0).toUpperCase();

    cargarSecciones(sesionActiva);

    cargarEstadisticas();
    // btnHISTORIAL
    function cargarAnios() {

        const select = document.getElementById("selectAnio");

        const anioActual = new Date().getFullYear();

        for(let i = anioActual; i >= 2020; i--) {

            const option = document.createElement("option");

            option.value = i;
            option.textContent = i;

            select.appendChild(option);
        }
    }
    document
        .getElementById("btnHistorialMensual")
        .addEventListener("click", () => {

            document
                .getElementById("modalHistorial")
                .classList.toggle("hidden");
        });

    document
        .getElementById("btnDescargarExcel")
        .addEventListener("click", descargarExcelMensual);

    // generacion del excel
    async function descargarExcelMensual() {

        const mes = document.getElementById("selectMes").value;
        const anio = document.getElementById("selectAnio").value;

        const response = await fetch(
            `${BASE_URL}/dashboard-api/pedidosMensuales?mes=${mes}&anio=${anio}`
        );

        if (!response.ok) {
            throw new Error("Error al obtener los pedidos");
        }

        const pedidos = await response.json();

        generarExcel(pedidos, mes, anio);
    }

    function generarExcel(pedidos, mes, anio) {

    const datos = pedidos.map(p => ({
        "Fecha": p.fecha_pedido,
        "Tipo Pedido": p.tipo_pedido,
        "Método Pago": p.metodo_pago,
        "Total (S/)": Number(p.total)
    }));

    const totalGeneral = pedidos.reduce(
        (acum, pedido) => acum + Number(pedido.total),
        0
    );

    const worksheet = XLSX.utils.aoa_to_sheet([
        [`Reporte de Pedidos - ${mes}/${anio}`],
        [],
        ["Fecha", "Tipo Pedido", "Método Pago", "Total (S/)"]
    ]);

    XLSX.utils.sheet_add_json(
        worksheet,
        datos,
        {
            origin: "A4",
            skipHeader: true
        }
    );

    const filaTotal = datos.length + 5;

    XLSX.utils.sheet_add_aoa(
        worksheet,
        [
            ["", "", "TOTAL GENERAL", totalGeneral]
        ],
        {
            origin: `A${filaTotal}`
        }
    );

    worksheet["!cols"] = [
        { wch: 15 },
        { wch: 15 },
        { wch: 20 },
        { wch: 15 }
    ];

    const workbook = XLSX.utils.book_new();

    XLSX.utils.book_append_sheet(
        workbook,
        worksheet,
        "Pedidos"
    );

    XLSX.writeFile(
        workbook,
        `Pedidos_${mes}_${anio}.xlsx`
    );
}

    cargarSecciones(sesionActiva);

    cargarEstadisticas();
    cargarAnios();
});