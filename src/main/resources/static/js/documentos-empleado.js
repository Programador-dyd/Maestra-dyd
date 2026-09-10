document.addEventListener('DOMContentLoaded', function () {
    var categoriaSelect = document.getElementById('selectCategoriaDocEmpleado');
    var tipoSelect = document.getElementById('tipoSelect');

    if (categoriaSelect && tipoSelect) {
        categoriaSelect.addEventListener('input', function () {
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
                var yaSubido = tiposYaSubidos.indexOf(tipo.id) !== -1;

                var opcion = document.createElement('option');
                opcion.value = tipo.id;
                opcion.textContent = tipo.nombre + (yaSubido ? ' (ya subido)' : '');
                opcion.disabled = yaSubido;
                tipoSelect.appendChild(opcion);
            });
        });
    }

    // Manejo del modal de "Editar / Reemplazar documento"
    var modal = document.getElementById('modalEditar');
    var formEditar = document.getElementById('formEditar');
    var inputTipoEditar = document.getElementById('inputTipoEditar');
    var modalTitulo = document.getElementById('modalEditarTitulo');
    var btnCancelar = document.getElementById('btnCancelarEditar');

    if (modal && formEditar) {
        document.querySelectorAll('.btn-editar-doc').forEach(function (boton) {
            boton.addEventListener('click', function () {
                var idDoc = boton.getAttribute('data-id');
                var tipoId = boton.getAttribute('data-tipo');
                var tipoNombre = boton.getAttribute('data-tiponombre');

                formEditar.action = urlBaseEditar + idDoc + '/editar';
                inputTipoEditar.value = tipoId;
                modalTitulo.textContent = 'Reemplazar: ' + tipoNombre;
                modal.style.display = 'flex';
            });
        });

        btnCancelar.addEventListener('click', function () {
            modal.style.display = 'none';
        });

        modal.addEventListener('click', function (evento) {
            if (evento.target === modal) {
                modal.style.display = 'none';
            }
        });
    }
});