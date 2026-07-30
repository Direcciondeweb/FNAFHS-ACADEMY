document.addEventListener('DOMContentLoaded', () => {
    const cards = {
        login: document.getElementById('login-card'),
        email: document.getElementById('step-email'),
        codigo: document.getElementById('step-codigo'),
        password: document.getElementById('step-nueva-password'),
        exito: document.getElementById('step-exito')
    };

    let emailActual = '';
    let cooldownInterval = null;

    function mostrar(paso) {
        Object.values(cards).forEach(c => c.classList.add('hidden'));
        cards[paso].classList.remove('hidden');
    }

    // ---- Mostrar/ocultar contraseña ----
    document.querySelectorAll('.toggle-password').forEach(btn => {
        btn.addEventListener('click', () => {
            const target = document.getElementById(btn.dataset.target);
            const icon = btn.querySelector('i');
            if (target.type === 'password') {
                target.type = 'text';
                icon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                target.type = 'password';
                icon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        });
    });

    // ---- Navegación entre pasos ----
    document.getElementById('link-olvide-password')?.addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('email');
    });
    document.getElementById('link-volver-login-1')?.addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });
    document.getElementById('link-volver-login-2')?.addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });

    // ---- Paso 1: solicitar código ----
    document.getElementById('form-email')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        emailActual = document.getElementById('recovery-email').value.trim();
        const btn = document.getElementById('btn-enviar-codigo');
        const alertBox = document.getElementById('email-alert');

        btn.disabled = true;
        btn.textContent = 'Enviando...';

        try {
            const res = await fetch('/api/auth/solicitar-codigo', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailActual })
            });
            const data = await res.json();

            if (res.status === 429) {
                mostrarAlerta(alertBox, data.error, 'error');
                btn.disabled = false;
                btn.textContent = 'Enviar código';
                return;
            }

            document.getElementById('email-mostrado').textContent = emailActual;
            mostrar('codigo');
        } catch (err) {
            mostrarAlerta(alertBox, 'Error de conexión. Intenta de nuevo.', 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Enviar código';
        }
    });

    // ---- Paso 2: verificar código ----
    document.getElementById('form-codigo')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const codigo = document.getElementById('codigo-input').value.trim();
        const btn = document.getElementById('btn-verificar-codigo');
        const alertBox = document.getElementById('codigo-alert');

        btn.disabled = true;
        btn.textContent = 'Verificando...';

        try {
            const res = await fetch('/api/auth/verificar-codigo', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailActual, codigo })
            });
            const data = await res.json();

            if (res.status === 429) {
                mostrarAlerta(alertBox, data.error, 'error');
                iniciarCooldown(data.segundosRestantes);
                return;
            }
            if (!res.ok) {
                mostrarAlerta(alertBox, data.error, 'error');
                return;
            }

            mostrar('password');
        } catch (err) {
            mostrarAlerta(alertBox, 'Error de conexión. Intenta de nuevo.', 'error');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Verificar código';
        }
    });

    // ---- Reenviar código (sin límite, disponible siempre que no esté bloqueado) ----
    document.getElementById('link-reenviar-codigo')?.addEventListener('click', async (e) => {
        e.preventDefault();
        const alertBox = document.getElementById('codigo-alert');

        try {
            const res = await fetch('/api/auth/solicitar-codigo', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailActual })
            });
            const data = await res.json();

            if (res.status === 429) {
                mostrarAlerta(alertBox, data.error, 'error');
                iniciarCooldown(data.segundosRestantes);
                return;
            }

            mostrarAlerta(alertBox, 'Se envió un nuevo código a tu correo.', 'success');
        } catch (err) {
            mostrarAlerta(alertBox, 'Error de conexión. Intenta de nuevo.', 'error');
        }
    });

    // ---- Paso 3: cambiar contraseña ----
    document.getElementById('form-nueva-password')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const nueva = document.getElementById('nueva-password').value;
        const confirmar = document.getElementById('confirmar-password').value;
        const alertBox = document.getElementById('password-alert');
        const codigo = document.getElementById('codigo-input').value.trim();

        if (nueva !== confirmar) {
            mostrarAlerta(alertBox, 'Las contraseñas no coinciden.', 'error');
            return;
        }

        try {
            const res = await fetch('/api/auth/cambiar-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailActual, codigo, nuevaPassword: nueva })
            });
            const data = await res.json();

            if (!res.ok) {
                mostrarAlerta(alertBox, data.error, 'error');
                return;
            }

            mostrar('exito');
        } catch (err) {
            mostrarAlerta(alertBox, 'Error de conexión. Intenta de nuevo.', 'error');
        }
    });

    function mostrarAlerta(elemento, mensaje, tipo) {
        elemento.textContent = mensaje;
        elemento.className = 'auth-alert auth-alert-' + tipo;
        elemento.classList.remove('hidden');
    }

    function iniciarCooldown(segundos) {
        const msg = document.getElementById('cooldown-msg');
        const btnVerificar = document.getElementById('btn-verificar-codigo');
        const linkReenviar = document.getElementById('link-reenviar-codigo');

        btnVerificar.disabled = true;
        linkReenviar.style.pointerEvents = 'none';
        linkReenviar.style.opacity = '0.5';
        msg.classList.remove('hidden');

        let restante = segundos;
        clearInterval(cooldownInterval);
        cooldownInterval = setInterval(() => {
            const min = Math.floor(restante / 60);
            const seg = restante % 60;
            msg.textContent = `Podrás intentar de nuevo en ${min}:${seg.toString().padStart(2, '0')}`;
            restante--;

            if (restante < 0) {
                clearInterval(cooldownInterval);
                msg.classList.add('hidden');
                btnVerificar.disabled = false;
                linkReenviar.style.pointerEvents = 'auto';
                linkReenviar.style.opacity = '1';
            }
        }, 1000);
    }
});