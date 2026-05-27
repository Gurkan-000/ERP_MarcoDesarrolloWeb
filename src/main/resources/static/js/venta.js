document.addEventListener('DOMContentLoaded', () => {
    const mesaGrid = document.getElementById('mesa-grid');
    const mesaDetails = document.getElementById('pos-salon-details');
    const mesaSelectedLabel = document.getElementById('mesa-selected');
    const mesaEstadoBadge = document.getElementById('mesa-estado');
    const pedidoList = document.querySelector('.mesa-pedidos-list .list');
    const mesaTotalLabel = document.getElementById('mesa-total');
    const productSelect = document.querySelector('#pos-salon-details select');
    const cantidadInput = document.querySelector('#pos-salon-details input[type="number"]');
    const addPedidoForm = document.querySelector('#pos-salon-details form');
    const mesaOcuparWrapper = document.getElementById('mesa-ocupar-wrapper');
    const mesaOcuparForm = mesaOcuparWrapper ? mesaOcuparWrapper.querySelector('form') : null;

    let selectedMesaId = null;
    let selectedPedidoId = null;

    const loadMesas = async () => {
        try {
            const response = await fetch('/api/mesa/listar');
            const mesas = await response.json();
            
            mesaGrid.innerHTML = '';
            mesas.forEach(mesa => {
                const btn = document.createElement('button');
                btn.className = `mesa-btn ${mesa.estado === 'OCUPADO' ? 'is-occupied' : ''}`;
                btn.dataset.id = mesa.idMesa;
                btn.dataset.idPedido = mesa.idPedido || '';
                btn.innerHTML = `
                    <span class="table-number">${mesa.numero}</span>
                    <span>${mesa.estado}</span>
                `;
                btn.onclick = () => selectMesa(mesa);
                mesaGrid.appendChild(btn);
            });
        } catch (error) {
            console.error('Error loading mesas:', error);
        }
    };

    const loadProducts = async () => {
        try {
            const response = await fetch('/api/catalogo/listarProductos');
            const products = await response.json();
            
            const selects = document.querySelectorAll('select[data-type="product-select"]');
            selects.forEach(select => {
                select.innerHTML = '<option value="" disabled selected>Seleccione un producto</option>';
                products.forEach(prod => {
                    const option = document.createElement('option');
                    option.value = prod.idProducto;
                    option.textContent = `${prod.nombre} - S/ ${prod.precio.toFixed(2)}`;
                    select.appendChild(option);
                });
            });
        } catch (error) {
            console.error('Error loading products:', error);
        }
    };

    // Generic order handler for Carry Out / Delivery
    const setupQuickSale = (formId, tipoPedido) => {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const idProducto = form.querySelector('select').value;
            const cantidad = parseInt(form.querySelector('input[type="number"]').value) || 1;
            const metodoPago = form.querySelector('select[name="metodoPago"]')?.value || 'EFECTIVO';

            try {
                // Create and checkout immediately for quick sales
                const response = await fetch('/api/venta/realizarPedido', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        tipoPedido: tipoPedido,
                        metodoPago: metodoPago,
                        idMesa: null
                    })
                });
                const pedido = await response.json();

                await fetch(`/api/venta/agregarDetallePedido/${pedido.idPedido}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        idProducto: idProducto,
                        cantidad: cantidad
                    })
                });

                alert('Venta registrada con éxito');
                form.reset();
            } catch (error) {
                console.error(`Error in ${tipoPedido} sale:`, error);
            }
        });
    };

    setupQuickSale('form-llevar', 'LLEVAR');
    setupQuickSale('form-delivery', 'DELIVERY');


    const selectMesa = async (mesa) => {
        selectedMesaId = mesa.idMesa;
        selectedPedidoId = mesa.idPedido;
        
        mesaDetails.classList.remove('hidden');
        mesaSelectedLabel.textContent = `Mesa ${mesa.numero}`;
        mesaEstadoBadge.textContent = mesa.estado;
        mesaEstadoBadge.className = `badge ${mesa.estado === 'OCUPADO' ? 'badge-orange' : 'badge-green'}`;

        if (mesaOcuparWrapper) {
            mesaOcuparWrapper.classList.toggle('hidden', mesa.estado === 'OCUPADO' || !selectedPedidoId);
        }

        updateOrderList();
    };

    const updateOrderList = async () => {
        if (!selectedMesaId) return;

        try {
            const response = await fetch(`/api/venta/detallePedidosMesa/${selectedMesaId}`);
            const detalles = await response.json();

            const listContainer = document.querySelector('.mesa-pedidos-list');
            const emptyState = document.getElementById('mesa-sin-pedidos');

            if (detalles.length > 0) {
                listContainer.classList.remove('hidden');
                emptyState.classList.add('hidden');
                pedidoList.innerHTML = '';
                let total = 0;
                detalles.forEach(det => {
                    total += det.total;
                    const item = document.createElement('div');
                    item.className = 'list-item';
                    item.innerHTML = `
                        <div>
                            <p class="title">${det.productoNombre}</p>
                            <span class="badge">x${det.cantidad}</span>
                        </div>
                        <strong>S/ ${det.total.toFixed(2)}</strong>
                    `;
                    pedidoList.appendChild(item);
                });
                mesaTotalLabel.textContent = `S/ ${total.toFixed(2)}`;
            } else {
                listContainer.classList.add('hidden');
                emptyState.classList.remove('hidden');
                mesaTotalLabel.textContent = 'S/ 0.00';
            }
        } catch (error) {
            console.error('Error loading order details:', error);
        }
    };

    if (addPedidoForm) {
        addPedidoForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            if (!selectedMesaId) return;

            const idProducto = productSelect.value;
            const cantidad = parseInt(cantidadInput.value) || 1;

            try {
                // If no pedido exists, create one
                if (!selectedPedidoId) {
                    const pedidoResponse = await fetch('/api/venta/realizarPedido', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            tipoPedido: 'SALON',
                            metodoPago: 'EFECTIVO', // Default
                            idMesa: selectedMesaId
                        })
                    });
                    const newPedido = await pedidoResponse.json();
                    selectedPedidoId = newPedido.idPedido;
                }

                // Add detail
                const detailResponse = await fetch(`/api/venta/agregarDetallePedido/${selectedPedidoId}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        idProducto: idProducto,
                        cantidad: cantidad
                    })
                });

                if (detailResponse.ok) {
                    updateOrderList();
                    loadMesas(); // Update mesa status if it changed
                }
            } catch (error) {
                console.error('Error adding product to order:', error);
            }
        });
    }

    // Modal handling for Checkout (Cobro)
    const cobroBtn = document.getElementById('mesa-ocupada-cobrar');
    if (cobroBtn) {
        cobroBtn.onclick = () => {
            // Open checkout modal logic
            // ... (I'll implement this if needed, but for now focus on the main flow)
        };
    }

    loadMesas();
    loadProducts();
});
