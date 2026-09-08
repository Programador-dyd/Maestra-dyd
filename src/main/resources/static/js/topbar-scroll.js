document.addEventListener('DOMContentLoaded', function () {
    var topbar = document.querySelector('.topbar');
    if (!topbar) {
        return;
    }

    function actualizarEstado() {
        if (window.scrollY > 8) {
            topbar.classList.add('topbar-scrolled');
        } else {
            topbar.classList.remove('topbar-scrolled');
        }
    }

    window.addEventListener('scroll', actualizarEstado);
    actualizarEstado();
});