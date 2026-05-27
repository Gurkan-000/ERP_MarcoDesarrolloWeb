document.addEventListener('DOMContentLoaded', () => {
    const stockTableBody = document.querySelector('#inv-stock table tbody');
    const editTableBody = document.querySelector('#inv-actualizar table tbody');
    const updateForm = document.querySelector('#inv-actualizar form');

    const loadInventory = async () => {
        try {
            const response = await fetch('/api/catalogo/listarProductos');
            const products = await response.json();

            if (stockTableBody) {
                stockTableBody.innerHTML = '';
                products.forEach(prod => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${prod.nombre}</td>
                        <td>${prod.stock}</td>
                    `;
                    stockTableBody.appendChild(row);
                });
            }

            if (editTableBody) {
                editTableBody.innerHTML = '';
                products.forEach(prod => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${prod.nombre}</td>
                        <td>${prod.stock}</td>
                        <td>
                            <input type="number" name="stock-${prod.idProducto}" value="${prod.stock}" min="0" class="field" style="width: 80px;">
                        </td>
                    `;
                    editTableBody.appendChild(row);
                });
            }
        } catch (error) {
            console.error('Error loading inventory:', error);
        }
    };

    if (updateForm) {
        updateForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const inputs = updateForm.querySelectorAll('input[name^="stock-"]');
            const updates = Array.from(inputs).map(input => {
                const idProducto = input.name.replace('stock-', '');
                const stock = parseInt(input.value);
                return { idProducto, stock };
            });

            try {
                // Individual calls as we don't have a bulk endpoint yet
                const promises = updates.map(update => 
                    fetch(`/api/catalogo/cantidadProducto/${update.idProducto}`, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ stock: update.stock })
                    })
                );

                await Promise.all(promises);
                alert('Stock actualizado correctamente');
                loadInventory();
            } catch (error) {
                console.error('Error updating stock:', error);
                alert('Hubo un error al actualizar el stock');
            }
        });
    }

    loadInventory();
});
