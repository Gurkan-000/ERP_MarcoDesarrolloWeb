const API_BASE_URL = 'http://localhost:8080/api';
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


const mostrarToast = (mensaje, tipo = 'info') => {
  const toast = document.createElement('div');
  toast.className = `toast toast-${tipo}`;
  toast.style.cssText = `
    padding: 12px 16px;
    border-radius: 12px;
    background: ${tipo === 'error' ? 'var(--red-500)' : tipo === 'success' ? 'var(--green-500)' : 'var(--zinc-800)'};
    color: white;
    font-size: 14px;
    font-weight: 500;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    animation: slideIn 0.3s ease;
  `;
  toast.textContent = mensaje;
  toastContainer.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
};


if (!document.getElementById('toast-animations')) {
  const style = document.createElement('style');
  style.id = 'toast-animations';
  style.textContent = `
    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `;
  document.head.appendChild(style);
}

const cargarProductos = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/catalogo/listarProductos`);
    
    if (!response.ok) {
      throw new Error(`Error HTTP: ${response.status}`);
    }
    
    const data = await response.json();
    productosGlobales = data;
    
    renderizarStockActual(data);
    renderizarActualizarStock(data);
    
    
    if (window.lucide?.createIcons) {
      window.lucide.createIcons();
    }
    
  } catch (error) {
    console.error('Error al cargar productos:', error);
    mostrarToast('No se pudieron cargar los productos del inventario', 'error');
  }
};

const renderizarStockActual = (productos) => {
  tbodyStockActual.innerHTML = '';
  
  if (productos.length === 0) {
    stockActualEmpty.classList.remove('hidden');
    if (window.lucide?.createIcons) window.lucide.createIcons();
    return;
  }
  
  stockActualEmpty.classList.add('hidden');
  
  productos.forEach(producto => {
    const row = document.createElement('tr');
    const stockClass = producto.stock > 10 ? 'badge-green' : producto.stock > 5 ? 'badge-orange' : 'badge-purple';
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
    if (window.lucide?.createIcons) window.lucide.createIcons();
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
  

  if (window.lucide?.createIcons) {
    window.lucide.createIcons();
  }
};

const actualizarStockProducto = async (idProducto, nuevoStock, nombreProducto) => {
  try {
    const requestBody = { stock: nuevoStock };
    
    const response = await fetch(`${API_BASE_URL}/catalogo/cantidadProducto/${idProducto}`, {
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

const guardarCambiosStock = async () => {
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
  

  const confirmacion = confirm(`¿Estás seguro de actualizar ${cambios.length} producto(s)?\n\n${
    cambios.map(c => `• ${c.nombreProducto}: ${c.stockAnterior} → ${c.stockNuevo}`).join('\n')
  }`);
  
  if (!confirmacion) return;
  

  btnGuardarStock.disabled = true;
  const originalText = btnGuardarStock.innerHTML;
  btnGuardarStock.innerHTML = '<span class="btn-spinner" style="display:inline-block;width:14px;height:14px;border:2px solid rgba(255,255,255,0.4);border-top-color:white;border-radius:50%;animation:spin 0.8s linear infinite;"></span> Guardando...';
  
  try {

    const resultados = await Promise.all(
      cambios.map(cambio => 
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
  }
};

const cancelarActualizacion = () => {
  cargarProductos();
  mostrarToast('Cambios cancelados', 'info');
};


if (formActualizarStock) {
  formActualizarStock.addEventListener('submit', (e) => {
    e.preventDefault();
    guardarCambiosStock();
  });
}

if (btnCancelarActualizacion) {
  btnCancelarActualizacion.addEventListener('click', cancelarActualizacion);
}


document.addEventListener('DOMContentLoaded', () => {
  cargarProductos();
  

  if (window.lucide?.createIcons) {
    window.lucide.createIcons();
  }
});


document.addEventListener('click', (e) => {
  if (e.target.classList.contains('tab') && e.target.dataset.tab === 'inv-stock') {
    cargarProductos();
  }
});