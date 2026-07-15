function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
    var isDark = document.body.classList.contains('dark-mode');
    localStorage.setItem('darkMode', isDark ? 'true' : 'false');

    var icons = ['themeIcon', 'menuThemeIcon'];
    var labels = ['themeLabel', 'menuThemeLabel'];
    icons.forEach(function(id) {
        var el = document.getElementById(id);
        if (el) el.className = isDark ? 'fas fa-sun' : 'fas fa-moon';
    });
    labels.forEach(function(id) {
        var el = document.getElementById(id);
        if (el) el.textContent = isDark ? 'Modo claro' : 'Modo oscuro';
    });
}
(function() {
    if (localStorage.getItem('darkMode') === 'true') {
        document.body.classList.add('dark-mode');
        var icons = ['themeIcon', 'menuThemeIcon'];
        var labels = ['themeLabel', 'menuThemeLabel'];
        icons.forEach(function(id) {
            var el = document.getElementById(id);
            if (el) el.className = 'fas fa-sun';
        });
        labels.forEach(function(id) {
            var el = document.getElementById(id);
            if (el) el.textContent = 'Modo claro';
        });
    }
})();
