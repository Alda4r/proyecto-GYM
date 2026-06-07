document.addEventListener("DOMContentLoaded", function() {
    const inputPeso = document.getElementById('pesoActual');
    const inputAltura = document.getElementById('altura');
    const displayIMC = document.getElementById('resultadoIMC'); 

    const pass1 = document.getElementById('password');
    const pass2 = document.getElementById('confirmarContrasena'); 
    const errorDiv = document.getElementById('errorPassword'); 
    const btnEnviar = document.querySelector('button[type="submit"]');
    const fechaNacInput = document.getElementById('fechaNac');
    const edadCampoInput = document.getElementById('edadCampo');
    const inputsNum = document.querySelectorAll('input[inputmode="decimal"], input[type="number"]');
    inputsNum.forEach(input => {
        input.addEventListener('keydown', (e) => {
            if (['-', 'e', '+', 'E'].includes(e.key)) e.preventDefault();
        });
    });

    function actualizarIMC() {
        if (!inputPeso || !inputAltura || !displayIMC) return;

        const peso = parseFloat(inputPeso.value);
        let altura = parseFloat(inputAltura.value);

        if (altura > 3) {
            altura = altura / 100;
        }

        if (peso > 0 && altura > 0) {
            const imc = (peso / (Math.pow(altura, 2))).toFixed(1);
            let clase = "bg-success";
            let estado = "Normal";

            if (imc < 18.5) { clase = "bg-warning"; estado = "Bajo peso"; }
            else if (imc >= 25 && imc < 30) { clase = "bg-warning"; estado = "Sobrepeso"; }
            else if (imc >= 30) { clase = "bg-danger"; estado = "Obesidad"; }

            displayIMC.innerHTML = `<span class="badge ${clase} p-2">IMC: ${imc} (${estado})</span>`;
        } else {
            displayIMC.innerHTML = "";
        }
    }


    function validarPass() {
        if (!pass1 || !pass2 || !errorDiv || !btnEnviar) return;

        const valor1 = pass1.value;
        const valor2 = pass2.value;

        if (valor2.length === 0) {
            errorDiv.innerText = "";
            pass2.style.borderColor = "#dee2e6";
            return;
        }

        if (valor1 === valor2) {
            errorDiv.innerText = "✓ Las contraseñas coinciden";
            errorDiv.style.color = "#2ecc71";
            pass2.style.borderColor = "#2ecc71";
            btnEnviar.disabled = false;
        } else {
            errorDiv.innerText = "✗ Las contraseñas no coinciden";
            errorDiv.style.color = "#ff4d4d";
            pass2.style.borderColor = "#ff4d4d";
            btnEnviar.disabled = true;
        }
    }

    function calcularEdad() {
        if (!fechaNacInput || !edadCampoInput) return;

        const fechaSeleccionada = new Date(fechaNacInput.value);
        const fechaActual = new Date();
        
        if (isNaN(fechaSeleccionada.getTime())) return;

        let edad = fechaActual.getFullYear() - fechaSeleccionada.getFullYear();
        const mes = fechaActual.getMonth() - fechaSeleccionada.getMonth();
        
        if (mes < 0 || (mes === 0 && fechaActual.getDate() < fechaSeleccionada.getDate())) {
            edad--;
        }
        
        edadCampoInput.value = edad >= 0 ? edad : 0;
    }

    // 6. ESCUCHADORES DE EVENTOS (Listeners protegidos contra valores nulos)
    if (inputPeso) inputPeso.addEventListener('input', actualizarIMC);
    if (inputAltura) inputAltura.addEventListener('input', actualizarIMC);
    if (pass1) pass1.addEventListener('input', validarPass);
    if (pass2) pass2.addEventListener('input', validarPass);
    if (fechaNacInput) fechaNacInput.addEventListener('change', calcularEdad);
});