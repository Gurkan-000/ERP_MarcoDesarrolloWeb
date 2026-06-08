
const BASE_URL = 'http://localhost:8080/api';
let productosGlobales = [];


const tbodyStockActual = document.getElementById('tbody-stock-actual');
const stockActualEmpty = document.getElementById('stock-actual-empty');
const tbodyActualizarStock = document.getElementById('tbody-actualizar-stock');
const actualizarStockEmpty = document.getElementById('actualizar-stock-empty');
const actualizarStockActions = document.getElementById('actualizar-stock-actions');
const formActualizarStock = document.getElementById('form-actualizar-stock');
const btnGuardarStock = document.getElementById('btn-guardar-stock');
const btnCancelarActualizacion = document.getElementById('btn-cancelar-actualizacion');
const toastContainer = document.getElementById('toast-container');

const modalConfirmarStock = document.getElementById('modal-confirmar-stock');
const listaCambiosStock = document.getElementById('lista-cambios-stock');
const btnConfirmarCambiosStock = document.getElementById('btnConfirmarCambiosStock');
const btnCancelarCambiosStock = document.getElementById('btnCancelarCambiosStock');

let cambiosPendientesStock = [];

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


const cargarProductos = async () => {
  try {
    const response = await fetch(`${BASE_URL}/catalogo/listarProductos`);

    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }

    const data = await response.json();
    productosGlobales = data;

    renderizarStockActual(data);
    renderizarActualizarStock(data);

  } catch (error) {
    console.error('Error al cargar productos:', error);
    mostrarToast('No se pudieron cargar los productos del inventario', 'error');
  }
};


const renderizarStockActual = (productos) => {
  tbodyStockActual.innerHTML = '';

  if (productos.length === 0) {
    stockActualEmpty.classList.remove('hidden');
    return;
  }

  stockActualEmpty.classList.add('hidden');

  productos.forEach(producto => {
    const row = document.createElement('tr');
    const stockClass = producto.stock > 10 ? 'badge-green' : producto.stock > 5 ? 'badge-orange' : 'badge-red';
    row.innerHTML = `
      <td><strong>${producto.nombreProducto}</strong></td>
      <td>
        <span class="badge ${stockClass}">
          ${producto.stock} unidades
        </span>
      </td>
    `;
    tbodyStockActual.appendChild(row);
  });
};


const renderizarActualizarStock = (productos) => {
  tbodyActualizarStock.innerHTML = '';

  if (productos.length === 0) {
    actualizarStockEmpty.classList.remove('hidden');
    actualizarStockActions.classList.add('hidden');
    return;
  }

  actualizarStockEmpty.classList.add('hidden');
  actualizarStockActions.classList.remove('hidden');

  productos.forEach(producto => {
    const row = document.createElement('tr');

    row.innerHTML = `
      <td><strong>${producto.nombreProducto}</strong></td>
      <td>
        <span class="badge badge-blue">${producto.stock}</span>
      </td>
      <td>
        <input 
          type="number" 
          min="0" 
          value="${producto.stock}" 
          data-producto-id="${producto.idProducto}"
          data-producto-nombre="${producto.nombreProducto}"
          class="stock-input"
          oninput="this.value = Math.max(0, parseInt(this.value) || 0)"
        />
      </td>
    `;
    tbodyActualizarStock.appendChild(row);
  });

};


const actualizarStockProducto = async (idProducto, nuevoStock, nombreProducto) => {
  try {
    const requestBody = { stock: nuevoStock };

    const response = await fetch(`${BASE_URL}/catalogo/cantidadProducto/${idProducto}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.mensajes?.[0] || `Error al actualizar stock de ${nombreProducto}`);
    }

    console.log(`✓ Stock actualizado para ${nombreProducto}: ${nuevoStock}`);
    return true;

  } catch (error) {
    console.error(`✗ Error al actualizar ${nombreProducto}:`, error);
    throw error;
  }
};


const guardarCambiosStock = () => {
  const inputs = document.querySelectorAll('.stock-input');
  const cambios = [];

  inputs.forEach(input => {
    const idProducto = input.dataset.productoId;
    const nombreProducto = input.dataset.productoNombre;
    const nuevoStock = parseInt(input.value, 10);
    const productoOriginal = productosGlobales.find(p => p.idProducto === idProducto);

    if (productoOriginal && productoOriginal.stock !== nuevoStock) {
      cambios.push({
        idProducto,
        nombreProducto,
        stockAnterior: productoOriginal.stock,
        stockNuevo: nuevoStock
      });
    }
  });

  if (cambios.length === 0) {
    mostrarToast('No hay cambios para guardar', 'info');
    return;
  }

  cambiosPendientesStock = cambios;

  listaCambiosStock.innerHTML = '';
  cambiosPendientesStock.forEach(c => {
    const item = document.createElement("div");
    item.style.marginBottom = "8px";
    item.innerHTML = `<strong>${c.nombreProducto}:</strong> ${c.stockAnterior} &rarr; <span style="color: green; font-weight: bold;">${c.stockNuevo}</span>`;
    listaCambiosStock.appendChild(item);
  });

  modalConfirmarStock.classList.remove('hidden');
};


btnCancelarCambiosStock.addEventListener('click', () => {
  modalConfirmarStock.classList.add('hidden');
  cambiosPendientesStock = [];
});


btnConfirmarCambiosStock.addEventListener('click', async () => {

  modalConfirmarStock.classList.add('hidden');

  btnGuardarStock.disabled = true;
  const originalText = btnGuardarStock.innerHTML;
  btnGuardarStock.innerHTML = '<span class="btn-spinner"></span> Guardando...';

  try {
    const resultados = await Promise.all(
      cambiosPendientesStock.map(cambio =>
        actualizarStockProducto(cambio.idProducto, cambio.stockNuevo, cambio.nombreProducto)
          .then(() => ({ ...cambio, exito: true }))
          .catch(error => ({ ...cambio, exito: false, error: error.message }))
      )
    );

    const exitosos = resultados.filter(r => r.exito);
    const fallidos = resultados.filter(r => !r.exito);

    if (exitosos.length > 0) {
      mostrarToast(` ${exitosos.length} producto(s) actualizado(s)`, 'success');
    }

    if (fallidos.length > 0) {
      mostrarToast(` ${fallidos.length} fallaron: ${fallidos.map(f => f.nombreProducto).join(', ')}`, 'error');
    }

    await cargarProductos();

  } catch (error) {
    console.error('Error crítico al guardar cambios:', error);
    mostrarToast('Error al guardar los cambios', 'error');
  } finally {
    btnGuardarStock.disabled = false;
    btnGuardarStock.innerHTML = originalText;
    cambiosPendientesStock = [];
  }
});


const cancelarActualizacion = () => {
  cargarProductos();
  mostrarToast('Cambios cancelados', 'info');
};


formActualizarStock.addEventListener('submit', (e) => {
  e.preventDefault();
  guardarCambiosStock();
});


btnCancelarActualizacion.addEventListener('click', cancelarActualizacion);


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

  if (sesionActiva.rol === "Administrador") {
    return;
  }

  if (sesionActiva.rol === "Mesero") {
    document.getElementById("navSeccionCaja").classList.add("hidden");
    document.getElementById("navSeccionInventario").classList.add("hidden");
    document.getElementById("navSeccionCatalogo").classList.add("hidden");
    document.getElementById("navSeccionUsuario").classList.add("hidden");
    document.getElementById("navSeccionConfiguracion").classList.add("hidden");
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

    const response = await fetch(`http://localhost:8080/api/usuario/cerrarSesion`, {
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

  document.getElementById("infoUsuario").textContent = sesionActiva.nombre;
  document.getElementById("infoRol").textContent = sesionActiva.rol;
  document.getElementById("infoAvatar").textContent = sesionActiva.rol.charAt(0).toUpperCase();

  cargarSecciones(sesionActiva);

  cargarProductos();
});

document.addEventListener('click', (e) => {
  if (e.target.classList.contains('tab') && e.target.dataset.tab === 'inv-stock') {
    cargarProductos();
  }
});