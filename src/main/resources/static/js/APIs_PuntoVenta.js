
const BASE_URL = "http://localhost:8083/api";

const detallesPedidoLlevar = new Map();
const detallesPedidoDelivery = new Map();
const detallesPedidoLocal = new Map();
const productosMap = new Map();

let tipoPedidoActivo = "LOCAL";
let MesaActiva = null;
let rolUsuarioActivo = null; //variable para saber el rol de usuario sin volver a consultar la api
// Datos de la última boleta generada (para el PDF)
let ultimaBoleta = null;
// ──────────────────────────────────────────────
// TOAST
// ──────────────────────────────────────────────
const toastContainer = document.getElementById('toast-container');
const mostrarToast = (mensaje, tipo = 'info') => {
    const toast = document.createElement('div');
    toast.className = `toast toast-${tipo}`;
    toast.textContent = mensaje;
    toastContainer.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('toast-hide');
        setTimeout(() => toast.remove(), 300);
    }, 4000);
};
// ──────────────────────────────────────────────
// PDF BOLETA con jsPDF
// ──────────────────────────────────────────────
const generarPDFBoleta = (boleta) => {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF({ unit: 'mm', format: [80, 200], orientation: 'portrait' });

    const ancho   = 80;
    const margen  = 5;
    const centro  = ancho / 2;
    let y         = 8;

    const linea = () => {
        doc.setDrawColor(200);
        doc.setLineWidth(0.2);
        doc.line(margen, y, ancho - margen, y);
        y += 4;
    };

    // ── Encabezado ──
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(10);
    doc.text('Lo Esencial Broastería', centro, y, { align: 'center' });
    y += 5;

    doc.setFontSize(8);
    doc.setFont('helvetica', 'normal');
    doc.text('RUC: -', centro, y, { align: 'center' });
    y += 4;
    doc.text('Av. Principal 123, Lima - Perú', centro, y, { align: 'center' });
    y += 5;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.text('BOLETA DE VENTA', centro, y, { align: 'center' });
    y += 4;
    linea();

    // ── Datos pedido ──
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);

    const idCorto = boleta.idPedido
        ? boleta.idPedido.toString().substring(0, 8).toUpperCase()
        : 'N/A';

    const now     = new Date();
    const fecha   = boleta.fecha || now.toLocaleDateString('es-PE');
    const hora    = boleta.hora
        ? boleta.hora.substring(0, 5)
        : now.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });

    const tipoPedidoMap = { LOCAL: 'Salón/Mesa', LLEVAR: 'Para Llevar', DELIVERY: 'Delivery' };
    const tipoPedidoLabel = tipoPedidoMap[boleta.tipoPedido] || boleta.tipoPedido || '-';
    const metodoPago = boleta.metodoPago || '-';

    doc.text(`N° Pedido : ${idCorto}`, margen, y); y += 4;
    doc.text(`Fecha     : ${fecha}`, margen, y); y += 4;
    doc.text(`Hora      : ${hora}`, margen, y); y += 4;
    doc.text(`Cliente   : Cliente Genérico`, margen, y); y += 4;
    doc.text(`Modalidad : ${tipoPedidoLabel}`, margen, y); y += 4;
    linea();

    // ── Encabezado tabla ──
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text('Producto', margen, y);
    doc.text('Cant', 48, y, { align: 'center' });
    doc.text('P.Unit', 60, y, { align: 'right' });
    doc.text('Total', ancho - margen, y, { align: 'right' });
    y += 3;
    linea();

    // ── Ítems ──
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);

    const detalles = boleta.detalles || [];
    detalles.forEach(d => {
        const nombreProducto = d.nombreProducto || '-';
        // Recortar nombre si es muy largo
        const nombreCorto = nombreProducto.length > 20
            ? nombreProducto.substring(0, 19) + '.'
            : nombreProducto;
        doc.text(nombreCorto, margen, y);
        doc.text(String(d.cantidad), 48, y, { align: 'center' });
        doc.text(formatoMoneda(d.precioUnitario), 60, y, { align: 'right' });
        doc.text(formatoMoneda(d.total), ancho - margen, y, { align: 'right' });
        y += 5;
    });

    linea();

    // ── Total ──
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.text('TOTAL:', margen, y);
    doc.text(formatoMoneda(boleta.total), ancho - margen, y, { align: 'right' });
    y += 5;

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);
    doc.text(`Método de pago: ${metodoPago}`, margen, y);
    y += 6;
    if (boleta.efectivoRecibido != null) {
        doc.text(`Efectivo recibido: ${formatoMoneda(boleta.efectivoRecibido)}`, margen, y);
        y += 4;
        doc.text(`Vuelto: ${formatoMoneda(boleta.vuelto != null ? boleta.vuelto : 0)}`, margen, y);
        y += 6;
    }
    linea();

    // ── Pie ──
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.text('¡Gracias por su visita!', centro, y, { align: 'center' });
    y += 4;
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7);
    doc.text('VUELVA PRONTO :) !', centro, y, { align: 'center' });

    doc.save(`boleta_${idCorto}.pdf`);
};

// ──────────────────────────────────────────────
// MODAL BOLETA
// ──────────────────────────────────────────────
const modalBoleta = document.getElementById('modal-boleta');
const btnDescargarBoleta = document.getElementById('btnDescargarBoleta');
const btnCerrarBoleta    = document.getElementById('btnCerrarBoleta');

const mostrarModalBoleta = (boleta) => {
    ultimaBoleta = boleta;
    if (modalBoleta) modalBoleta.classList.remove('hidden');
};

if (btnDescargarBoleta) {
    btnDescargarBoleta.addEventListener('click', () => {
        if (ultimaBoleta) generarPDFBoleta(ultimaBoleta);
    });
}

if (btnCerrarBoleta) {
    btnCerrarBoleta.addEventListener('click', () => {
        if (modalBoleta) modalBoleta.classList.add('hidden');
    });
}


const formatoMoneda = (valor) => `S/ ${Number(valor).toFixed(2)}`;
// ──────────────────────────────────────────────
// API CALLS
// ──────────────────────────────────────────────
const obtenerCajaAbierta = async () => {

    try {

        const response = await fetch(`${BASE_URL}/caja/obtenerCajaAbierta`);
        const data = await response.json();

        if (!response.ok) {
            return null;
        }

        return data;


    } catch (error) {
        console.log(error);
    }

}


const cargarMesas = async () => {
    try {
        const response = await fetch(`${BASE_URL}/venta/listarMesas`);

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            const data = await response.json();

            const mesaGridVenta = document.getElementById("mesa-grid");
            if (!mesaGridVenta) return;
            mesaGridVenta.innerHTML = "";

            data.forEach((mesa) => {
                const botonMesa = document.createElement("button");
                const estadoMesa = mesa.estado === "OCUPADO" ? "OCUPADO" : "LIBRE";

                botonMesa.id = "btnMesa";
                botonMesa.type = "button";
                botonMesa.classList.add(`table-card`);
                botonMesa.classList.add(`${mesa.estado === "OCUPADO" ? "status-occupied" : "status-free"}`);
                botonMesa.dataset.mesa = mesa.numero;
                botonMesa.dataset.estado = estadoMesa;
                botonMesa.dataset.idMesa = mesa.idMesa;

                botonMesa.innerHTML = `
                <span class="table-number">${mesa.numero}</span>
                <span>${estadoMesa}</span>
                `;

                detallesPedidoLocal.set(mesa.idMesa, new Map());
                mesaGridVenta.appendChild(botonMesa);

            });
        }
    } catch (error) {
        console.log(error);
    }
};


const cargarProductos = async () => {
    try {
        const response = await fetch(`${BASE_URL}/catalogo/listarProductos`);

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            const data = await response.json();

            const comboProductoPedido = document.getElementById("comboProductoPedido");
            const comboProductoMesa = document.getElementById("comboProductoMesa"); // Capturamos el nuevo select

            const optionDefault = `<option value="" disabled selected>Seleccione un producto</option>`;
            comboProductoPedido.innerHTML = optionDefault;
            if (comboProductoMesa) comboProductoMesa.innerHTML = optionDefault;

            data.forEach((producto) => {
                const option = document.createElement("option");
                option.value = producto.idProducto;
                option.textContent = producto.nombreProducto;

                productosMap.set(producto.idProducto, producto);

                comboProductoPedido.appendChild(option.cloneNode(true));
                if (comboProductoMesa) comboProductoMesa.appendChild(option);
            });
        }
    } catch (error) {
        console.log(error);
    }
};


const validarDetallePedido = async (requestDetalle) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/validarDetallePedido`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestDetalle)
        });


        if (!response.ok) {
            const data = await response.json();
            data.mensajes.forEach(m => {
                mostrarToast(m, "error");
            });
            return false;
        }

        return true;
    } catch (error) {
        console.log(error);
        return false;
    }
};

// cobrarPedido ahora devuelve ResponseBoleta
const cobrarPedido = async (requestPedido) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/cobrarPedido`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestPedido)
        });
        const data = await response.json();
        if (!response.ok) {
            (data.mensajes || []).forEach(m => mostrarToast(m, "error"));
            return null;
        }
        mostrarToast(data.mensaje || "Cobrado exitosamente", "success");
        return data;        // ResponseBoleta
    } catch (error) {
        mostrarToast("Ocurrió un error inesperado", "error");
        return null;
    }
};


const ocuparMesa = async (requestPedido, idMesa) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/ocuparMesa/${idMesa}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestPedido)
        });

        if (!response.ok) {
            const data = await response.json().catch(() => null);
            if (data && data.mensajes) {
                data.mensajes.forEach(m => mostrarToast(m, "error"));
            } else {
                mostrarToast("Error al ocupar la mesa", "error");
            }
            return false;
        }

        return true;

    } catch (error) {
        mostrarToast("Ocurrió un error inesperado", "error");
        return false;
    }
};

// cobrarMesa ahora devuelve ResponseBoleta
const cobrarMesa = async (idMesa, metodoPago) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/cobrarMesa/${idMesa}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ metodoPago })
        });
        const data = await response.json();
        if (!response.ok) {
            (data.mensajes || []).forEach(m => mostrarToast(m, "error"));
            return null;
        }
        mostrarToast(data.mensaje || "Cobrado exitosamente", "success");
        return data;        // ResponseBoleta
    } catch (error) {
        mostrarToast("Ocurrió un error inesperado", "error");
        return null;
    }
};


const agregarDetalleAlaMesa = async (idMesa, requestDetallePedido) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/agregarDetallePedidoAlaMesa/${idMesa}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestDetallePedido)
        });
        if (!response.ok) {
            data.mensajes.forEach(m => {
                mostrarToast(m, "error");
            });
            return false;
        }
        return true;
    } catch (error) {
        mostrarToast("Ocurrio un error inesperado", "error");
        return false;
    }
};

const obtenerDetallesPedidoPorMesa = async (idMesa) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/detallePedidosPorMesa/${idMesa}`);

        if (!response.ok) {
            mostrarToast("Error al obtener los detalles de la mesa", "error");
            return [];
        }

        return await response.json();
    } catch (error) {
        console.log(error);
        mostrarToast("Error de conexión", "error");
        return [];
    }
};
// ──────────────────────────────────────────────
// RENDER DETALLES
// ──────────────────────────────────────────────
const renderDetallesPedido = () => {
    const detalles = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    const tbodyPedidosAgregados = document.getElementById("tbodyPedidosAgregados");
    const txtTotalPedido = document.getElementById("txtTotalPedido");
    const tfootTotalPedido = document.getElementById("tfootTotalPedido");

    tbodyPedidosAgregados.innerHTML = "";
    let total = 0;

    detalles.forEach((detalle) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${detalle.nombreProducto}</td>
            <td>${detalle.cantidad}</td>
            <td>${formatoMoneda(detalle.precioUnitario)}</td>
            <td>${formatoMoneda(detalle.total)}</td>
        `;

        total += detalle.total;
        tbodyPedidosAgregados.appendChild(fila);
    });

    txtTotalPedido.textContent = formatoMoneda(total);

    if (detalles.size > 0) {
        tfootTotalPedido.classList.remove("hidden");
        btnRegistrarPagoPedido.classList.remove("hidden");
    } else {
        tfootTotalPedido.classList.add("hidden");
        btnRegistrarPagoPedido.classList.add("hidden");
    }
};


const renderDetallesMesa = () => {
    const tbodyPedidosMesaAgregados = document.getElementById("tbodyPedidosMesaAgregados");
    const txtTotalPedidoMesa = document.getElementById("txtTotalPedidoMesa");
    const tfootTotalPedidoMesa = document.getElementById("tfootTotalPedidoMesa");

    tbodyPedidosMesaAgregados.innerHTML = "";

    if (!MesaActiva) {
        txtTotalPedidoMesa.textContent = formatoMoneda(0);
        tfootTotalPedidoMesa.classList.add("hidden");
        return;
    }

    const mapDetallesMesa = detallesPedidoLocal.get(MesaActiva.dataset.idMesa) || new Map();
    let total = 0;

    mapDetallesMesa.forEach((detalle) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${detalle.nombreProducto}</td>
            <td>${detalle.cantidad}</td>
            <td>${formatoMoneda(detalle.precioUnitario)}</td>
            <td>${formatoMoneda(detalle.total)}</td>
        `;
        total += detalle.total;
        tbodyPedidosMesaAgregados.appendChild(fila);
    });

    txtTotalPedidoMesa.textContent = formatoMoneda(total);

    if (mapDetallesMesa.size > 0) {
        tfootTotalPedidoMesa.classList.remove("hidden");
    } else {
        tfootTotalPedidoMesa.classList.add("hidden");
    }
};


// ──────────────────────────────────────────────
// BOTONES — PEDIDO (LLEVAR / DELIVERY)
// ──────────────────────────────────────────────
const btnAgregarPedido = document.getElementById("btnAgregarPedido");
btnAgregarPedido.addEventListener("click", async (e) => {
    e.preventDefault();

    const comboProductoPedido = document.getElementById("comboProductoPedido");
    const txtCantidadPedido = document.getElementById("txtCantidadPedido");

    const requestDetalle = {
        idProducto: comboProductoPedido.value,
        cantidad: Number(txtCantidadPedido.value)
    };

    const detalleValido = await validarDetallePedido(requestDetalle);

    if (!detalleValido) {
        return;
    }

    const producto = productosMap.get(requestDetalle.idProducto);
    const precioUnitario = Number(producto.precio);
    const total = precioUnitario * requestDetalle.cantidad;

    const detalle = {
        idProducto: comboProductoPedido.value,
        nombreProducto: producto.nombreProducto,
        cantidad: Number(txtCantidadPedido.value),
        precioUnitario,
        total
    };

    const mapActivo = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    if (mapActivo.has(detalle.idProducto)) {
        const productoExistente = mapActivo.get(detalle.idProducto);
        productoExistente.cantidad += detalle.cantidad;
        productoExistente.total = productoExistente.cantidad * productoExistente.precioUnitario;
    } else {
        mapActivo.set(detalle.idProducto, detalle);
    }

    txtCantidadPedido.value = "";
    comboProductoPedido.selectedIndex = 0;
    renderDetallesPedido(tipoPedidoActivo);
});

const modalCobroPedido = document.getElementById("modal-cobro-pedido");
const tipoPedidoLabel = document.getElementById("tipo-pedido-label");
const modalPedidoTotalMonto = document.getElementById("modal-pedido-total-monto");
const comboMetodoPagoModal = document.getElementById("comboMetodoPagoModal");

// Elementos para manejo de efectivo en modal pedido
const efectivoBlockModal = document.getElementById('efectivo-block-modal');
const inputEfectivoModal = document.getElementById('inputEfectivoModal');
const vueltoModal = document.getElementById('vuelto-modal');

if (comboMetodoPagoModal) {
    comboMetodoPagoModal.addEventListener('change', () => {
        if (comboMetodoPagoModal.value === 'EFECTIVO') {
            if (efectivoBlockModal) efectivoBlockModal.classList.remove('hidden');
            if (inputEfectivoModal) inputEfectivoModal.value = '';
            if (vueltoModal) vueltoModal.textContent = formatoMoneda(0);
        } else {
            if (efectivoBlockModal) efectivoBlockModal.classList.add('hidden');
        }
    });
}

if (inputEfectivoModal) {
    inputEfectivoModal.addEventListener('input', () => {
        const text = modalPedidoTotalMonto ? modalPedidoTotalMonto.textContent : 'S/ 0.00';
        const total = Number(text.replace('S/', '').replace(/\s/g, '').replace(',', '.')) || 0;
        const efectivo = Number(inputEfectivoModal.value) || 0;
        const vuelto = efectivo - total;
        if (vueltoModal) vueltoModal.textContent = formatoMoneda(vuelto >= 0 ? vuelto : 0);
    });
}

const btnCancelarCompraPedido = document.getElementById("btnCancelarCompraPedido");
btnCancelarCompraPedido.addEventListener("click", () => {
    modalCobroPedido.classList.add("hidden");
});

const btnConfirmarCompraPedido = document.getElementById("btnConfirmarCompraPedido");
btnConfirmarCompraPedido.addEventListener("click", async (e) => {
    e.preventDefault();

    const mapActivo = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    const requestPedido = {
        metodoPago: comboMetodoPagoModal.value,
        tipoPedido: tipoPedidoActivo,
        detalles: Array.from(mapActivo.values()).map((detalle) => ({
            idProducto: detalle.idProducto,
            cantidad: detalle.cantidad
        }))
    };

    btnConfirmarCompraPedido.disabled = true;
    btnConfirmarCompraPedido.textContent = "Procesando...";

    // Si es EFECTIVO, validar monto recibido y calcular vuelto
    let totalPedido = 0;
    mapActivo.forEach(d => totalPedido += d.total);

    if (requestPedido.metodoPago === 'EFECTIVO') {
        const efectivoRec = Number(inputEfectivoModal ? inputEfectivoModal.value : 0) || 0;
        if (isNaN(efectivoRec) || efectivoRec < totalPedido) {
            mostrarToast('El monto recibido es insuficiente para cubrir el total.', 'error');
            btnConfirmarCompraPedido.disabled = false;
            btnConfirmarCompraPedido.textContent = 'Comprar';
            return;
        }
        requestPedido.efectivoRecibido = Number(efectivoRec.toFixed(2));
        requestPedido.vuelto = Number((efectivoRec - totalPedido).toFixed(2));
    }

    const pagoCorrecto = await cobrarPedido(requestPedido);

    btnConfirmarCompraPedido.disabled = false;
    btnConfirmarCompraPedido.textContent = "Comprar";

    if (pagoCorrecto) {
        // Adjuntar datos de efectivo a la boleta que mostraremos/convertiremos a PDF
        if (requestPedido.metodoPago === 'EFECTIVO') {
            pagoCorrecto.efectivoRecibido = requestPedido.efectivoRecibido;
            pagoCorrecto.vuelto = requestPedido.vuelto;
        }
        mapActivo.clear();
        renderDetallesPedido();
        modalCobroPedido.classList.add("hidden");
        mostrarModalBoleta(pagoCorrecto);
    }
});

const btnRegistrarPagoPedido = document.getElementById("btnRegistrarPagoPedido");
btnRegistrarPagoPedido.addEventListener("click", (e) => {
    e.preventDefault();

    const mapActivo = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    let total = 0;
    mapActivo.forEach(detalle => total += detalle.total);

    tipoPedidoLabel.textContent = `Pedido - ${tipoPedidoActivo}`;
    modalPedidoTotalMonto.textContent = formatoMoneda(total);

    comboMetodoPagoModal.selectedIndex = 0;

    // Asegurar estado inicial del bloque de efectivo según el valor actual del select
    if (comboMetodoPagoModal && comboMetodoPagoModal.value === 'EFECTIVO') {
        if (efectivoBlockModal) efectivoBlockModal.classList.remove('hidden');
    } else {
        if (efectivoBlockModal) efectivoBlockModal.classList.add('hidden');
    }
    if (inputEfectivoModal) inputEfectivoModal.value = '';
    if (vueltoModal) vueltoModal.textContent = formatoMoneda(0);

    modalCobroPedido.classList.remove("hidden");

});
// ──────────────────────────────────────────────
// TABS
// ──────────────────────────────────────────────
const divPedidoRegistro = document.getElementById("divPedidoRegistro");
const divMesaPedidos = document.getElementById("divMesaPedidos");

const btnTabLlevar = document.getElementById("btnTabLlevar");
const btnTabDelivery = document.getElementById("btnTabDelivery");
const btnTabLocal = document.getElementById("btnTabLocal");

const divPedidoLlevar = document.getElementById("divPedidoLlevar");
btnTabLlevar.addEventListener("click", () => {
    divPedidoLlevar.appendChild(divPedidoRegistro);
    divPedidoRegistro.classList.remove("hidden");
    divMesaPedidos.classList.add("hidden");
    tipoPedidoActivo = "LLEVAR";
    renderDetallesPedido();
});


const divPedidoDelivery = document.getElementById("divPedidoDelivery");
btnTabDelivery.addEventListener("click", () => {
    divPedidoDelivery.appendChild(divPedidoRegistro);
    divPedidoRegistro.classList.remove("hidden");
    divMesaPedidos.classList.add("hidden");
    tipoPedidoActivo = "DELIVERY";
    renderDetallesPedido();
});


btnTabLocal.addEventListener("click", () => {
    tipoPedidoActivo = "LOCAL";
    divPedidoRegistro.classList.add("hidden");
    divMesaPedidos.classList.add("hidden");
    renderDetallesMesa();
});


// ──────────────────────────────────────────────
// MESA — AGREGAR PEDIDO
// ──────────────────────────────────────────────
const btnOcuparMesa = document.getElementById("btnOcuparMesa");
btnOcuparMesa.addEventListener("click", async (e) => {
    e.preventDefault();

    const idMesaNum = MesaActiva.dataset.idMesa;
    const mapActivo = detallesPedidoLocal.get(idMesaNum);

    const requestPedido = {
        metodoPago: "PENDIENTE",
        tipoPedido: tipoPedidoActivo,
        detalles: Array.from(mapActivo.values()).map((detalle) => ({
            idProducto: detalle.idProducto,
            cantidad: detalle.cantidad
        }))
    };

    const mesaOcupada = await ocuparMesa(requestPedido, idMesaNum);
    if (mesaOcupada) {
        mapActivo.clear();
        renderDetallesPedido();
    }

    divMesaPedidos.classList.add("hidden");
    btnOcuparMesa.classList.add("hidden");
    await cargarMesas();
});


const btnAgregarPedidoMesa = document.getElementById("btnAgregarPedidoMesa");
btnAgregarPedidoMesa.addEventListener("click", async (e) => {
    e.preventDefault();

    if (MesaActiva == null) {
        mostrarToast("Por favor, selecciona una mesa primero", "info");
        return;
    }

    const comboProductoMesa = document.getElementById("comboProductoMesa");
    const txtCantidadMesa = document.getElementById("txtCantidadMesa");

    const requestDetalle = {
        idProducto: comboProductoMesa.value,
        cantidad: Number(txtCantidadMesa.value)
    };

    const detalleValido = await validarDetallePedido(requestDetalle);

    if (!detalleValido) {
        return;
    }

    if (MesaActiva.dataset.estado === "OCUPADO") {
        const agregadoBackend = await agregarDetalleAlaMesa(MesaActiva.dataset.idMesa, requestDetalle);
        if (!agregadoBackend) return;
        mostrarToast("Producto agregado al pedido existente", "success");
    } else {
        btnOcuparMesa.classList.remove("hidden");
    }

    const producto = productosMap.get(requestDetalle.idProducto);
    const precioUnitario = Number(producto.precio);
    const total = precioUnitario * requestDetalle.cantidad;

    const detalle = {
        idProducto: comboProductoMesa.value,
        nombreProducto: producto.nombreProducto,
        cantidad: Number(txtCantidadMesa.value),
        precioUnitario,
        total
    };

    const mapMesa = detallesPedidoLocal.get(MesaActiva.dataset.idMesa);

    if (mapMesa.has(detalle.idProducto)) {
        const productoExistente = mapMesa.get(detalle.idProducto);
        productoExistente.cantidad += detalle.cantidad;
        productoExistente.total = productoExistente.cantidad * productoExistente.precioUnitario;
    } else {
        mapMesa.set(detalle.idProducto, detalle);
    }

    txtCantidadMesa.value = "";
    comboProductoMesa.selectedIndex = 0;

    renderDetallesMesa();
});

const divModalMesaOcupada = document.getElementById("divModalMesaOcupada");
const mesaGridVenta = document.getElementById("mesa-grid");
mesaGridVenta.addEventListener("click", () => {

    divMesaPedidos.classList.add("hidden");
    const boton = event.target.closest(".table-card");

    if (!boton) return;

    MesaActiva = boton;
    const numeroMesa = boton.dataset.mesa;
    const estadoMesa = boton.dataset.estado;

    if (estadoMesa === "OCUPADO") {

        // Ocultar el boton Cobrar si el usuario es Mesero
        const btnCobrarModal = document.getElementById("btnMesaOcupadaCobrar");
        if (rolUsuarioActivo === "Mesero") {
            btnCobrarModal.classList.add("hidden");
        } else {
            btnCobrarModal.classList.remove("hidden");
        }

        divModalMesaOcupada.classList.remove("hidden");

    } else {

        divMesaPedidos.classList.remove("hidden");

        document.getElementById("mesa-selected").textContent = `Mesa ${numeroMesa}`;
        document.getElementById("mesa-estado").textContent = estadoMesa;
        document.getElementById("mesa-estado").classList.add("badge-green");

        if (detallesPedidoLocal.get(MesaActiva.dataset.idMesa).size > 0) {
            btnOcuparMesa.classList.remove("hidden");
        } else {
            btnOcuparMesa.classList.add("hidden");
        }

        renderDetallesMesa();

    }

});

const modalCobro = document.getElementById("cobro-modal");
const cobroListaDetalles = document.getElementById("cobro-lista-detalles");
const cobroTotalMonto = document.getElementById("cobro-total-monto");
const comboMetodoPagoCobro = document.getElementById("comboMetodoPagoCobro");
const btnConfirmarCobro = document.getElementById("btnConfirmarCobro");
const btnCancelarCobro = document.getElementById("btnCancelarCobro");

// Elementos para manejo de efectivo en modal cobro mesa
const efectivoBlockCobro = document.getElementById('efectivo-block-cobro');
const inputEfectivoCobro = document.getElementById('inputEfectivoCobro');
const vueltoCobro = document.getElementById('vuelto-cobro');

if (comboMetodoPagoCobro) {
    comboMetodoPagoCobro.addEventListener('change', () => {
        if (comboMetodoPagoCobro.value === 'EFECTIVO') {
            if (efectivoBlockCobro) efectivoBlockCobro.classList.remove('hidden');
            if (inputEfectivoCobro) inputEfectivoCobro.value = '';
            if (vueltoCobro) vueltoCobro.textContent = formatoMoneda(0);
        } else {
            if (efectivoBlockCobro) efectivoBlockCobro.classList.add('hidden');
        }
    });
}

if (inputEfectivoCobro) {
    inputEfectivoCobro.addEventListener('input', () => {
        const text = cobroTotalMonto ? cobroTotalMonto.textContent : 'S/ 0.00';
        const total = Number(text.replace('S/', '').replace(/\s/g, '').replace(',', '.')) || 0;
        const efectivo = Number(inputEfectivoCobro.value) || 0;
        const vuelto = efectivo - total;
        if (vueltoCobro) vueltoCobro.textContent = formatoMoneda(vuelto >= 0 ? vuelto : 0);
    });
}

const btnMesaOcupadaAgregar = document.getElementById("btnMesaOcupadaAgregar");
btnMesaOcupadaAgregar.addEventListener("click", async () => {
    divModalMesaOcupada.classList.add("hidden");
    divMesaPedidos.classList.remove("hidden");

    const idMesa = MesaActiva.dataset.idMesa;
    const detallesBackend = await obtenerDetallesPedidoPorMesa(idMesa);

    const mapMesa = detallesPedidoLocal.get(idMesa);
    mapMesa.clear();

    detallesBackend.forEach(detalle => {
        mapMesa.set(detalle.idProducto, {
            idProducto: detalle.idProducto,
            nombreProducto: detalle.nombreProducto,
            cantidad: detalle.cantidad,
            precioUnitario: detalle.precioUnitario,
            total: detalle.total
        });
    });

    renderDetallesMesa();
});

document.getElementById("btnMesaOcupadaCobrar").addEventListener("click", async () => {
    divModalMesaOcupada.classList.add("hidden");
    const idMesa = MesaActiva.dataset.idMesa;
    if (!idMesa) { mostrarToast("Error: No se encontró el identificador de la mesa.", "error"); return; }

    cobroListaDetalles.innerHTML = "<p class='muted'>Cargando detalles...</p>";
    modalCobro.classList.remove("hidden");
    // Estado inicial del bloque de efectivo según el valor actual del select
    if (comboMetodoPagoCobro) comboMetodoPagoCobro.selectedIndex = 0;
    if (comboMetodoPagoCobro && comboMetodoPagoCobro.value === 'EFECTIVO') {
        if (efectivoBlockCobro) efectivoBlockCobro.classList.remove('hidden');
    } else {
        if (efectivoBlockCobro) efectivoBlockCobro.classList.add('hidden');
    }
    if (inputEfectivoCobro) inputEfectivoCobro.value = '';
    if (vueltoCobro) vueltoCobro.textContent = formatoMoneda(0);
    document.getElementById("cobro-mesa-label").textContent = `Mesa ${MesaActiva.dataset.mesa}`;

    const detallesBackend = await obtenerDetallesPedidoPorMesa(idMesa);
    cobroListaDetalles.innerHTML = "";
    let totalCobro = 0;
    detallesBackend.forEach((detalle) => {
        const item = document.createElement("div");
        item.style.cssText = "display:flex;justify-content:space-between;padding:4px 0;";
        item.innerHTML = `<span>${detalle.cantidad}x ${detalle.nombreProducto}</span><span>${formatoMoneda(detalle.total)}</span>`;
        cobroListaDetalles.appendChild(item);
        totalCobro += detalle.total;
    });
    cobroTotalMonto.textContent = formatoMoneda(totalCobro);
});



const btnMesaOcupadaCancel = document.getElementById("btnMesaOcupadaCancel");
btnMesaOcupadaCancel.addEventListener("click", () => {
    divModalMesaOcupada.classList.add("hidden");
    divMesaPedidos.classList.add("hidden");
    MesaActiva = null;
});

btnConfirmarCobro.addEventListener("click", async () => {
    const idMesa     = MesaActiva.dataset.idMesa;
    const metodoPago = comboMetodoPagoCobro.value;

    // Si es EFECTIVO, validar monto recibido
    let totalCobro = Number(cobroTotalMonto ? cobroTotalMonto.textContent.replace('S/', '').replace(/\s/g, '').replace(',', '.') : 0) || 0;
    let efectivoRec = null;
    if (metodoPago === 'EFECTIVO') {
        efectivoRec = Number(inputEfectivoCobro ? inputEfectivoCobro.value : 0) || 0;
        if (isNaN(efectivoRec) || efectivoRec < totalCobro) {
            mostrarToast('El monto recibido es insuficiente para cubrir el total.', 'error');
            return;
        }
    }

    const boleta = await cobrarMesa(idMesa, metodoPago);

    if (boleta) {
        // Adjuntar datos de efectivo si aplica
        if (metodoPago === 'EFECTIVO') {
            boleta.efectivoRecibido = Number(efectivoRec.toFixed(2));
            boleta.vuelto = Number((efectivoRec - totalCobro).toFixed(2));
        }
        detallesPedidoLocal.get(idMesa).clear();
        modalCobro.classList.add("hidden");
        MesaActiva = null;
        await cargarMesas();
        mostrarModalBoleta(boleta);
    }
});


btnCancelarCobro.addEventListener("click", () => {
    modalCobro.classList.add("hidden");
    MesaActiva = null;
});

const verificarSesionActiva = async () => {

    try {

        const response = await fetch(`${BASE_URL}/usuario/sesionActiva`);
        const data = await response.json();

        if (!response.ok) {
            return null;
        } else {
            return data;
        }

    } catch (error) {
        console.log(error);
    }

}


const cargarSecciones = (sesionActiva) => {

    rolUsuarioActivo = sesionActiva.rol;

    if (sesionActiva.rol === "Administrador") {
        return;
    }

    if (sesionActiva.rol === "Mesero") {
        document.getElementById("navSeccionCaja").classList.add("hidden");
        document.getElementById("navSeccionInventario").classList.add("hidden");
        document.getElementById("navSeccionCatalogo").classList.add("hidden");
        document.getElementById("navSeccionUsuario").classList.add("hidden");
        document.getElementById("navSeccionConfiguracion").classList.add("hidden");

        // Ocultar tabs de Para Llevar y Delivery: el mesero solo atiende salon
        document.getElementById("btnTabLlevar").classList.add("hidden");
        document.getElementById("btnTabDelivery").classList.add("hidden");
    }

    if (sesionActiva.rol === "Cajero") {
        document.getElementById("navSeccionInventario").classList.add("hidden");
        document.getElementById("navSeccionCatalogo").classList.add("hidden");
        document.getElementById("navSeccionUsuario").classList.add("hidden");
        document.getElementById("navSeccionConfiguracion").classList.add("hidden");
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

const cerrarSesion = async () =>{
    try {

        const response = await fetch(`http://localhost:8083/api/usuario/cerrarSesion`, {
            method: 'PUT'
        });

        if (!response.ok) {
            return false;
        } 

        return true;

    } catch (error) {
        console.log(error);
    }
}

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

    document.getElementById("infoUsuario").textContent = sesionActiva.nombre;
    document.getElementById("infoRol").textContent = sesionActiva.rol;
    document.getElementById("infoAvatar").textContent = sesionActiva.rol.charAt(0).toUpperCase();

    cargarSecciones(sesionActiva);

    const cajaAbierta = await obtenerCajaAbierta();

    if (cajaAbierta) {
        cargarMesas();
        cargarProductos();
    } else {

        let html = "caja.html";
        let mensaje = "Ir a caja";

        if (sesionActiva.rol === "Mesero" && await cerrarSesion()) {
            html = "login.html";
            mensaje = "Logear como cajero";
        }

        document.getElementById("caja-cerrada-ir").textContent = mensaje;

        const cajaCerradaModal = document.getElementById("caja-cerrada-modal");
        const cajaCerradaIr = document.getElementById("caja-cerrada-ir");
        cajaCerradaModal.classList.remove("hidden");
        cajaCerradaIr.addEventListener("click", () => {
            window.location.href = html;
        });
    }
});


