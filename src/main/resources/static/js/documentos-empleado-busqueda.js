document.addEventListener('DOMContentLoaded', function () {
    var categoriaSelect = document.getElementById('selectCategoriaBusqueda');
    var tipoSelect = document.getElementById('tipoSelectBusqueda');

    if (!categoriaSelect || !tipoSelect) {
        return;
    }

    categoriaSelect.addEventListener('change', function () {
        var categoria = categoriaSelect.value;
        tipoSelect.innerHTML = '';

        var opcionTodos = document.createElement('option');
        opcionTodos.value = '';
        opcionTodos.textContent = 'Todos';
        tipoSelect.appendChild(opcionTodos);

        if (!categoria || !tiposPorCategoriaBusqueda[categoria]) {
            return;
        }

        tiposPorCategoriaBusqueda[categoria].forEach(function (tipo) {
            var opcion = document.createElement('option');
            opcion.value = tipo.id;
            opcion.textContent = tipo.nombre;
            tipoSelect.appendChild(opcion);
        });
    });
});