document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('form-registro');
    const alertBox = document.getElementById('registro-alert');
    const btn = document.getElementById('btn-registrar');

    // ---- Mostrar/ocultar contraseña ----
    document.querySelectorAll('.toggle-password').forEach(toggleBtn => {
        toggleBtn.addEventListener('click', () => {
            const target = document.getElementById(toggleBtn.dataset.target);
            const icon = toggleBtn.querySelector('i');
            if (target.type === 'password') {
                target.type = 'text';
                icon.classList.replace('fa-eye', 'fa-eye-slash');
            } else {
                target.type = 'password';
                icon.classList.replace('fa-eye-slash', 'fa-eye');
            }
        });
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        limpiarErrores();
        ocultarAlerta();

        const username = document.getElementById('reg-username').value.trim();
        const nombreCompleto = document.getElementById('reg-nombre').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const password = document.getElementById('reg-password').value;
        const confirmarPassword = document.getElementById('reg-confirmar').value;

        // Validación básica en el cliente antes de llamar al backend
        let valido = true;
        if (username.length < 3) {
            marcarError('username', 'El usuario debe tener al menos 3 caracteres');
            valido = false;
        }
        if (!nombreCompleto) {
            marcarError('nombre', 'El nombre completo es obligatorio');
            valido = false;
        }
        if (!email.includes('@')) {
            marcarError('email', 'Ingresa un correo válido');
            valido = false;
        }
        if (password.length < 6) {
            marcarError('password', 'La contraseña debe tener al menos 6 caracteres');
            valido = false;
        }
        if (password !== confirmarPassword) {
            marcarError('confirmar', 'Las contraseñas no coinciden');
            valido = false;
        }
        if (!valido) return;

        btn.disabled = true;
        btn.textContent = 'Creando cuenta...';

        try {
            const res = await fetch('/api/auth/registro', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, nombreCompleto, email, password, confirmarPassword })
            });
            const data = await res.json();

            if (!res.ok) {
                if (data.detalles) {
                    Object.entries(data.detalles).forEach(([campo, mensaje]) => {
                        const mapaCampos = { username: 'username', email: 'email', confirmarPassword: 'confirmar' };
                        marcarError(mapaCampos[campo] || campo, mensaje);
                    });
                } else {
                    mostrarAlerta(data.error || 'Ocurrió un error al registrarte.');
                }
                return;
            }

            document.getElementById('registro-card').classList.add('hidden');
            document.getElementById('registro-exito').classList.remove('hidden');
        } catch (err) {
            mostrarAlerta('Error de conexión. Intenta de nuevo.');
        } finally {
            btn.disabled = false;
            btn.textContent = 'Crear cuenta';
        }
    });

    function marcarError(campo, mensaje) {
        const span = document.getElementById('err-' + campo);
        const input = document.getElementById('reg-' + campo);
        if (span) span.textContent = mensaje;
        if (input) input.classList.add('input-error');
    }

    function limpiarErrores() {
        document.querySelectorAll('.field-error').forEach(s => s.textContent = '');
        document.querySelectorAll('.form-group input').forEach(i => i.classList.remove('input-error'));
    }

    function mostrarAlerta(mensaje) {
        alertBox.textContent = mensaje;
        alertBox.className = 'auth-alert auth-alert-error';
        alertBox.classList.remove('hidden');
    }

    function ocultarAlerta() {
        alertBox.classList.add('hidden');
    }
});