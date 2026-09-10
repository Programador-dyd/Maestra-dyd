document.addEventListener('DOMContentLoaded', function () {
    var rolSelect = document.getElementById('rolSelect');
    var fechaIngreso = document.getElementById('fechaIngresoEmpleado');

    if (rolSelect && fechaIngreso) {
        function actualizarEstadoFecha() {
            var esEmpleado = rolSelect.value === 'E';
            fechaIngreso.disabled = !esEmpleado;
            if (!esEmpleado) {
                fechaIngreso.value = '';
            }
        }

        rolSelect.addEventListener('change', actualizarEstadoFecha);
        actualizarEstadoFecha();
    }

    var cambiarClaveCheck = document.getElementById('cambiarClaveCheck');
    var claveInput = document.getElementById('claveInput');

    if (cambiarClaveCheck && claveInput) {
        cambiarClaveCheck.addEventListener('change', function () {
            claveInput.disabled = !cambiarClaveCheck.checked;
            if (!cambiarClaveCheck.checked) {
                claveInput.value = '';
            } else {
                claveInput.focus();
            }
        });
    }
});