document.addEventListener('DOMContentLoaded', function () {
    var rolSelect = document.getElementById('rolSelect');
    var fechaIngreso = document.getElementById('fechaIngresoEmpleado');

    if (!rolSelect || !fechaIngreso) {
        return;
    }

    function actualizarEstado() {
        var esEmpleado = rolSelect.value === 'E';
        fechaIngreso.disabled = !esEmpleado;
        if (!esEmpleado) {
            fechaIngreso.value = '';
        }
    }

    rolSelect.addEventListener('change', actualizarEstado);

    actualizarEstado();
});