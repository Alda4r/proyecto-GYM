/**
 * FitGym - Lógica de Seguimiento Nutricional
 * Este script maneja el cálculo de calorías y la actualización dinámica de la interfaz.
 */

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formComida');
    
    // 1. Configuración inicial de datos (Valores base de tu imagen)
    let metaDiaria = 2500;
    
    // Calculamos lo que ya tienes quemado en el HTML inicial:
    // Desayuno (420) + Almuerzo (650) + Snack (210) = 1280 kcal consumidas
    let caloriasConsumidas = 1280; 

    // Escuchamos el evento de envío del formulario
    form.addEventListener('submit', function(e) {
        // Evitamos que la página se recargue
        e.preventDefault();

        // 2. Captura de datos del formulario
        const nombre = document.getElementById('nombreComida').value;
        const kcalInput = document.getElementById('valorCalorias').value;
        const protInput = document.getElementById('valorProteina').value;

        // Convertimos a números enteros
        const kcal = parseInt(kcalInput);
        const prot = parseInt(protInput) || 0; // Si está vacío, ponemos 0

        // 3. VALIDACIÓN ANTINEGATIVOS Y VALORES VACÍOS
        if (kcal < 0 || prot < 0) {
            alert("⚠️ Error: No puedes ingresar valores negativos.");
            return; // Detiene la ejecución
        }

        if (isNaN(kcal)) {
            alert("⚠️ Por favor, ingresa un número válido de calorías.");
            return;
        }

        // 4. LÓGICA MATEMÁTICA
        caloriasConsumidas += kcal;
        let restantes = metaDiaria - caloriasConsumidas;

        // 5. ACTUALIZACIÓN DE LA INTERFAZ (Círculo de calorías)
        const displayRestantes = document.getElementById('caloriasRestantes');
        if (displayRestantes) {
            // .toLocaleString() añade la coma de miles automáticamente (ej: 1,840)
            displayRestantes.innerText = restantes.toLocaleString();
        }

        // 6. CREACIÓN DEL NUEVO ELEMENTO EN LA LISTA
        // Obtenemos la hora actual para el registro
        const ahora = new Date();
        const horaFormateada = ahora.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        const nuevaComidaHTML = `
            <div class="food-item">
                <div>
                    <h6 class="fw-bold mb-0">${nombre}</h6>
                    <small class="text-muted">${horaFormateada}</small>
                </div>
                <span class="badge bg-light text-dark align-self-center">${kcal} kcal</span>
            </div>
        `;

        // 7. INSERCIÓN EN EL HTML
        const contenedor = document.getElementById('contenedorComidas');
        if (contenedor) {
            // 'afterbegin' lo pone al principio de la lista
            contenedor.insertAdjacentHTML('afterbegin', nuevaComidaHTML);
        }

        // 8. CIERRE DEL MODAL Y LIMPIEZA
        // Obtenemos la instancia del modal de Bootstrap para cerrarlo
        const modalElement = document.getElementById('modalComida');
        const modalInstance = bootstrap.Modal.getInstance(modalElement);
        
        if (modalInstance) {
            modalInstance.hide();
        }

        // Limpiamos los campos para la siguiente entrada
        form.reset();

        console.log(`✅ Comida guardada: ${nombre} (+${kcal} kcal). Restan: ${restantes}`);
    });
});