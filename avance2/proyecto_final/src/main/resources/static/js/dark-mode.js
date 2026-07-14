function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
    var isDark = document.body.classList.contains('dark-mode');
    localStorage.setItem('darkMode', isDark ? 'true' : 'false');
    var icon = document.getElementById('themeIcon');
    var label = document.getElementById('themeLabel');
    if (isDark) {
        icon.className = 'fas fa-sun';
        label.textContent = 'Modo Claro';
    } else {
        icon.className = 'fas fa-moon';
        label.textContent = 'Modo Oscuro';
    }
}
(function() {
    if (localStorage.getItem('darkMode') === 'true') {
        document.body.classList.add('dark-mode');
        var icon = document.getElementById('themeIcon');
        var label = document.getElementById('themeLabel');
        if (icon) { icon.className = 'fas fa-sun'; }
        if (label) { label.textContent = 'Modo Claro'; }
    }
})();
