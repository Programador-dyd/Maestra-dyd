document.addEventListener('DOMContentLoaded', function () {
    var categoriaSelect = document.getElementById('categoriaSelect');
    var tipoSelect = document.getElementById('tipoSelect');

    if (!categoriaSelect || !tipoSelect) {
        return;
    }

    categoriaSelect.addEventListener('change', function () {
        var categoria = categoriaSelect.value;
        tipoSelect.innerHTML = '';

        if (!categoria || !tiposPorCategoria[categoria]) {
            tipoSelect.innerHTML = '<option value="">Primero elige una categoría</option>';
            tipoSelect.disabled = true;
            return;
        }

        tipoSelect.disabled = false;
        var opcionVacia = document.createElement('option');
        opcionVacia.value = '';
        opcionVacia.textContent = 'Selecciona un tipo de documento';
        tipoSelect.appendChild(opcionVacia);

        tiposPorCategoria[categoria].forEach(function (tipo) {
            var opcion = document.createElement('option');
            opcion.value = tipo.id;
            opcion.textContent = tipo.nombre;
            tipoSelect.appendChild(opcion);
        });
    });
});