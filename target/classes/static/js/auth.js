document.addEventListener('DOMContentLoaded', () => {
    const cards = {
        login: document.getElementById('login-card'),
        loginEmail: document.getElementById('step-login-email'),
        loginCodigo: document.getElementById('step-login-codigo'),
        email: document.getElementById('step-email'),
        codigo: document.getElementById('step-codigo'),
        password: document.getElementById('step-nueva-password'),
        exito: document.getElementById('step-exito')
    };

    let emailActual = '';
    let usernameActual = '';
    let cooldownInterval = null;

    function mostrar(paso) {
        Object.values(cards).forEach(c => c.classList.add('hidden'));
        cards[paso].classList.remove('hidden');
    }

    function alertaError(mensaje) {
        Swal.fire({
            icon: 'error', title: 'Ups...', text: mensaje,
            background: '#1a1a1a', color: '#F6EFEB', confirmButtonColor: '#fd0000'
        });
    }

    function alertaExito(mensaje) {
        Swal.fire({
            icon: 'success', title: '¡Listo!', text: mensaje,
            background: '#1a1a1a', color: '#F6EFEB', confirmButtonColor: '#d0fc0b',
            timer: 2000, showConfirmButton: false
        });
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

    // ================= LOGIN PASO 1: usuario + contraseña =================
    document.getElementById('form-login').addEventListener('submit', async (e) => {
        e.preventDefault();
        usernameActual = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const btn = document.getElementById('btn-login-paso1');

        btn.disabled = true;
        btn.textContent = 'Verificando...';

        try {
            const res = await fetch('/api/auth/login-paso1', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: usernameActual, password })
            });
            const data = await res.json();

            if (!res.ok) {
                alertaError(data.error || 'No se pudo iniciar sesión.');
                return;
            }

            mostrar('loginEmail');
        } catch (err) {
            alertaError('Error de conexión. Intenta de nuevo.');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Continuar';
        }
    });

    // ================= LOGIN PASO 2: confirmar correo (ahora acepta cualquier correo) =================
    document.getElementById('form-login-email').addEventListener('submit', async (e) => {
        e.preventDefault();
        emailActual = document.getElementById('login-email-input').value.trim();
        const btn = document.getElementById('btn-login-email');

        btn.disabled = true;
        btn.textContent = 'Enviando...';

        try {
            const res = await fetch('/api/auth/login-confirmar-email', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: usernameActual, email: emailActual })
            });
            const data = await res.json();

            if (res.status === 429) {
                alertaError(data.error);
                return;
            }
            if (!res.ok) {
                alertaError(data.error);
                return;
            }

            mostrar('loginCodigo');
            alertaExito('Código enviado. Revisa tu correo (puede tardar unos segundos).');
        } catch (err) {
            alertaError('Error de conexión. Intenta de nuevo.');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Enviar código';
        }
    });

    document.getElementById('link-cancelar-login-email').addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });

    // ================= LOGIN PASO 3: código =================
    document.getElementById('form-login-codigo').addEventListener('submit', async (e) => {
        e.preventDefault();
        const codigo = document.getElementById('login-codigo-input').value.trim();
        const btn = document.getElementById('btn-login-paso2');

        btn.disabled = true;
        btn.textContent = 'Ingresando...';

        try {
            const res = await fetch('/api/auth/login-paso2', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: usernameActual, codigo, email: emailActual })
            });
            const data = await res.json();

            if (res.status === 429) {
                alertaError(data.error);
                iniciarCooldown(data.segundosRestantes, 'login');
                return;
            }
            if (!res.ok) {
                alertaError(data.error);
                return;
            }

            window.location.href = data.redirect;
        } catch (err) {
            alertaError('Error de conexión. Intenta de nuevo.');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Ingresar';
        }
    });

    document.getElementById('link-reenviar-login').addEventListener('click', async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('/api/auth/login-reenviar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: usernameActual, email: emailActual })
            });
            const data = await res.json();
            if (res.status === 429) {
                alertaError(data.error);
                iniciarCooldown(data.segundosRestantes, 'login');
                return;
            }
            alertaExito('Se envió un nuevo código a tu correo.');
        } catch (err) {
            alertaError('Error de conexión.');
        }
    });

    document.getElementById('link-cancelar-login').addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });

    // ================= NAVEGACIÓN RECUPERACIÓN =================
    document.getElementById('link-olvide-password').addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('email');
    });
    document.getElementById('link-volver-login-1').addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });
    document.getElementById('link-volver-login-2').addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });
    document.getElementById('link-volver-login-3')?.addEventListener('click', (e) => {
        e.preventDefault();
        mostrar('login');
    });

    // ================= RECUPERACIÓN PASO 1 =================
    document.getElementById('form-email').addEventListener('submit', async (e) => {
        e.preventDefault();
        const emailRecuperacion = document.getElementById('recovery-email').value.trim();
        const btn = document.getElementById('btn-enviar-codigo');

        btn.disabled = true;
        btn.textContent = 'Enviando...';

        try {
            const res = await fetch('/api/auth/solicitar-codigo', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailRecuperacion })
            });
            const data = await res.json();

            if (res.status === 429) {
                alertaError(data.error);
                return;
            }

            document.getElementById('email-mostrado').textContent = emailRecuperacion;
            emailActual = emailRecuperacion;
            mostrar('codigo');
        } catch (err) {
            alertaError('Error de conexión. Intenta de nuevo.');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Enviar código';
        }
    });

    // ================= RECUPERACIÓN PASO 2 =================
    document.getElementById('form-codigo').addEventListener('submit', async (e) => {
        e.preventDefault();
        const codigo = document.getElementById('codigo-input').value.trim();
        const btn = document.getElementById('btn-verificar-codigo');

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
                alertaError(data.error);
                iniciarCooldown(data.segundosRestantes, 'recovery');
                return;
            }
            if (!res.ok) {
                alertaError(data.error);
                return;
            }

            mostrar('password');
        } catch (err) {
            alertaError('Error de conexión. Intenta de nuevo.');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Verificar código';
        }
    });

    document.getElementById('link-reenviar-codigo').addEventListener('click', async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('/api/auth/solicitar-codigo', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: emailActual })
            });
            const data = await res.json();
            if (res.status === 429) {
                alertaError(data.error);
                iniciarCooldown(data.segundosRestantes, 'recovery');
                return;
            }
            alertaExito('Se envió un nuevo código a tu correo.');
        } catch (err) {
            alertaError('Error de conexión.');
        }
    });

    // ================= RECUPERACIÓN PASO 3 =================
    document.getElementById('form-nueva-password').addEventListener('submit', async (e) => {
        e.preventDefault();
        const nueva = document.getElementById('nueva-password').value;
        const confirmar = document.getElementById('confirmar-password').value;
        const codigo = document.getElementById('codigo-input').value.trim();

        if (nueva !== confirmar) {
            alertaError('Las contraseñas no coinciden.');
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
                alertaError(data.error);
                return;
            }

            mostrar('exito');
        } catch (err) {
            alertaError('Error de conexión. Intenta de nuevo.');
        }
    });

    // ================= COOLDOWN DE BLOQUEO =================
    function iniciarCooldown(segundos, contexto) {
        const msg = contexto === 'login' ? document.getElementById('login-cooldown-msg') : document.getElementById('cooldown-msg');
        const btn = contexto === 'login' ? document.getElementById('btn-login-paso2') : document.getElementById('btn-verificar-codigo');

        btn.disabled = true;
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
                btn.disabled = false;
            }
        }, 1000);
    }
});