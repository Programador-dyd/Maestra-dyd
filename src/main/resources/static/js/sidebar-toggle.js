document.addEventListener('DOMContentLoaded', function () {
  var toggle = document.getElementById('sidebarToggle');
  var overlay = document.getElementById('sidebarOverlay');
  var sidebar = document.getElementById('sidebar');

  if (!toggle || !overlay || !sidebar) {
    return;
  }

  if (toggle.dataset.sidebarInitialized === 'true') {
    return;
  }
  toggle.dataset.sidebarInitialized = 'true';

  document.body.classList.remove('sidebar-open');

  function closeSidebar() {
    document.body.classList.remove('sidebar-open');
    sidebar.querySelectorAll('details[open]').forEach(function (item) {
      item.open = false;
    });
  }

  function toggleSidebar() {
    if (document.body.classList.contains('sidebar-open')) {
      closeSidebar();
      return;
    }
    document.body.classList.add('sidebar-open');
  }

  toggle.addEventListener('click', toggleSidebar);
  overlay.addEventListener('click', closeSidebar);

  document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
      closeSidebar();
    }
  });
});
