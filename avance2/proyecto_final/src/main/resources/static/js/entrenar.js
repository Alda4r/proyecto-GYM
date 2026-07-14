var state = {
    exercises: [],
    currentIdx: 0,
    currentSet: 1,
    totalSets: 0,
    completedSets: 0,
    startTime: null,
    isResting: false,
    restTimer: null,
    restSeconds: 60,
    restRemaining: 0,
    configRest: 60,
    completed: false
};

function initWorkout(exercises, restSeconds) {
    console.log('initWorkout called with', JSON.stringify(exercises));
    state.exercises = exercises;
    state.configRest = restSeconds || 60;
    state.restSeconds = state.configRest;
    state.totalSets = exercises.reduce(function(sum, e) { return sum + e.series; }, 0);

    if (!exercises || exercises.length === 0) {
        document.getElementById('exerciseDisplay').innerHTML =
            '<p class="text-muted">Esta rutina no tiene ejercicios.</p>';
        return;
    }

    state.completedSets = 0;
    state.currentIdx = 0;
    state.currentSet = 1;
    state.startTime = Date.now();
    state.completed = false;
    renderExercise();
    updateProgress();
}

function getCurrentExercise() {
    return state.exercises[state.currentIdx];
}

function renderExercise() {
    const ex = getCurrentExercise();
    if (!ex) {
        showCompletion();
        return;
    }

    document.getElementById('exerciseName').textContent = ex.nombre;
    document.getElementById('setCounter').innerHTML =
        'Set <strong>' + state.currentSet + '</strong> de ' + ex.series;
    document.getElementById('repsValue').textContent = ex.repeticiones + ' reps';
    document.getElementById('weightValue').textContent = ex.pesoSugerido;

    document.getElementById('exerciseDisplay').style.display = 'block';
    document.getElementById('restOverlay').classList.remove('active');
    document.getElementById('completionScreen').style.display = 'none';
    document.getElementById('nextBtn').style.display = 'none';

    document.getElementById('completeBtn').disabled = false;
    document.getElementById('completeBtn').innerHTML =
        '<i class="fas fa-check-circle"></i> Completar Serie';
}

function completeSet() {
    console.log('completeSet called');
    var ex = getCurrentExercise();
    if (!ex || state.completed) return;

    document.getElementById('completeBtn').disabled = true;
    state.completedSets++;

    if (state.currentSet < ex.series) {
        state.currentSet++;
        updateProgress();
        startRest();
    } else {
        if (state.currentIdx < state.exercises.length - 1) {
            updateProgress();
            showNextButton();
        } else {
            state.completed = true;
            updateProgress();
            showCompletion();
        }
    }
}

function startRest() {
    state.isResting = true;
    state.restRemaining = state.configRest;
    state.restSeconds = state.configRest;

    document.getElementById('restOverlay').classList.add('active');
    updateRestTimer();

    if (state.restTimer) clearInterval(state.restTimer);
    state.restTimer = setInterval(function() {
        state.restRemaining--;
        updateRestTimer();

        if (state.restRemaining <= 0) {
            clearInterval(state.restTimer);
            state.restTimer = null;
            playBeep();
            endRest();
        }
    }, 1000);
}

function updateRestTimer() {
    var mins = Math.floor(state.restRemaining / 60);
    var secs = state.restRemaining % 60;
    document.getElementById('restValue').textContent =
        String(mins).padStart(2, '0') + ':' + String(secs).padStart(2, '0');

    var total = state.configRest;
    var elapsed = total - state.restRemaining;
    var degrees = (elapsed / total) * 360;
    document.getElementById('restRing').style.background =
        'conic-gradient(var(--primary, #f97316) ' + degrees + 'deg, var(--border-color, #eee) ' + degrees + 'deg)';
}

function skipRest() {
    if (state.restTimer) {
        clearInterval(state.restTimer);
        state.restTimer = null;
    }
    endRest();
}

function endRest() {
    state.isResting = false;
    document.getElementById('restOverlay').classList.remove('active');
    document.getElementById('completeBtn').disabled = false;
    document.getElementById('completeBtn').innerHTML =
        '<i class="fas fa-dumbbell"></i> Siguiente Serie';
    renderExercise();
}

function showNextButton() {
    document.getElementById('exerciseDisplay').style.display = 'none';
    document.getElementById('nextBtn').style.display = 'block';
    document.getElementById('nextExerciseName').textContent =
        state.exercises[state.currentIdx + 1].nombre;
}

function goToNextExercise() {
    state.currentIdx++;
    state.currentSet = 1;
    document.getElementById('nextBtn').style.display = 'none';
    renderExercise();
    updateProgress();
}

function showCompletion() {
    document.getElementById('exerciseDisplay').style.display = 'none';
    document.getElementById('restOverlay').classList.remove('active');
    document.getElementById('nextBtn').style.display = 'none';

    var elapsed = Math.floor((Date.now() - state.startTime) / 60000);
    var calories = elapsed * 8;

    document.getElementById('compSets').textContent = state.totalSets;
    document.getElementById('compTime').textContent = elapsed + ' min';
    document.getElementById('compCalories').textContent = calories;
    document.getElementById('compRoutineName').textContent =
        document.getElementById('routineName').textContent;

    document.getElementById('completionScreen').style.display = 'block';

    document.getElementById('finishForm').querySelector('input[name="duracion"]').value = elapsed;
    document.getElementById('finishForm').querySelector('input[name="calorias"]').value = calories;
}

function updateProgress() {
    const pct = state.totalSets > 0
        ? Math.round((state.completedSets / state.totalSets) * 100)
        : 0;
    document.getElementById('progressFill').style.width = pct + '%';
    document.getElementById('progressText').textContent =
        state.completedSets + ' de ' + state.totalSets + ' series';
    document.getElementById('progressPct').textContent = pct + '%';
}

function playBeep() {
    try {
        var ctx = new (window.AudioContext || window.webkitAudioContext)();
        var osc = ctx.createOscillator();
        var gain = ctx.createGain();
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.frequency.value = 880;
        osc.type = 'sine';
        gain.gain.setValueAtTime(0.3, ctx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
        osc.start(ctx.currentTime);
        osc.stop(ctx.currentTime + 0.5);

        setTimeout(function() {
            var osc2 = ctx.createOscillator();
            var gain2 = ctx.createGain();
            osc2.connect(gain2);
            gain2.connect(ctx.destination);
            osc2.frequency.value = 1100;
            osc2.type = 'sine';
            gain2.gain.setValueAtTime(0.3, ctx.currentTime);
            gain2.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);
            osc2.start(ctx.currentTime);
            osc2.stop(ctx.currentTime + 0.5);
        }, 200);
    } catch(e) {
        console.log('Beep failed:', e);
    }
}
