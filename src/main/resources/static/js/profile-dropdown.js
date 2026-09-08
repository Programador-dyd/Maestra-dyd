document.addEventListener('DOMContentLoaded', function () {
    var seccion = document.getElementById('profileSection');
    var trigger = document.getElementById('profileTrigger');

    if (!seccion || !trigger) {
        return;
    }

    trigger.addEventListener('click', function (evento) {
        evento.stopPropagation();
        seccion.classList.toggle('open');
    });

    document.addEventListener('click', function () {
        seccion.classList.remove('open');
    });

    document.addEventListener('keydown', function (evento) {
        if (evento.key === 'Escape') {
            seccion.classList.remove('open');
        }
    });
});