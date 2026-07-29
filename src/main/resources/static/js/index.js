document.addEventListener('DOMContentLoaded', () => {
    const logged = document.body.getAttribute('data-logged') === 'true';

    cargarSlider();
    cargarPersonajesPreview();
    cargarArtePreview('fanart', 'fanarts-preview');
    cargarArtePreview('arte-oficial', 'arte-preview');
    if (logged) cargarNotificaciones();

    // ---- Modal ----
    const modal = document.getElementById('content-modal');
    document.getElementById('modal-close').addEventListener('click', () => modal.style.display = 'none');
    window.addEventListener('click', (e) => { if (e.target === modal) modal.style.display = 'none'; });

    let contenidoActual = { tipo: null, id: null };

    document.getElementById('like-btn').addEventListener('click', () => toggleLike(contenidoActual.tipo, contenidoActual.id));
    document.getElementById('comment-form').addEventListener('submit', (e) => {
        e.preventDefault();
        enviarComentario(contenidoActual.tipo, contenidoActual.id);
    });

    window.abrirModalContenido = function (tipo, id, titulo, imagenUrl) {
        contenidoActual = { tipo, id };
        document.getElementById('modal-title').textContent = titulo;
        document.getElementById('modal-image').src = imagenUrl;
        document.getElementById('comment-text').value = '';

        const loginMsg = document.getElementById('login-required-msg');
        const commentForm = document.getElementById('comment-form');
        loginMsg.style.display = logged ? 'none' : 'block';
        commentForm.style.display = logged ? 'flex' : 'none';

        cargarEstadoLike(tipo, id);
        cargarComentarios(tipo, id);

        modal.style.display = 'block';
    };

    // ---- Carrusel automático ----
    let slideIndex = 0;
    setInterval(() => {
        const slider = document.getElementById('hero-slider');
        const total = slider.children.length;
        if (total <= 1) return;
        slideIndex = (slideIndex + 1) % total;
        slider.style.transform = `translateX(-${slideIndex * 100}%)`;
    }, 5000);
});

async function cargarSlider() {
    try {
        const res = await fetch('/api/slider/activas');
        const imagenes = await res.json();
        const slider = document.getElementById('hero-slider');

        if (!imagenes || imagenes.length === 0) return; // se queda el slide de respaldo

        slider.innerHTML = '';
        imagenes.forEach(img => {
            const div = document.createElement('div');
            div.className = 'slide';
            div.style.backgroundImage = `url('${img.imagenUrl}')`;
            slider.appendChild(div);
        });
    } catch (e) {
        console.error('No se pudo cargar el slider', e);
    }
}

async function cargarPersonajesPreview() {
    try {
        const res = await fetch('/api/personajes/activos');
        const personajes = await res.json();
        const cont = document.getElementById('personajes-preview');
        cont.innerHTML = '';

        personajes.slice(0, 10).forEach(p => {
            const div = document.createElement('div');
            div.className = 'item';
            div.textContent = p.nombre;
            div.onclick = () => abrirModalContenido('PERSONAJE', p.id, p.nombre, p.imagenUrl);
            cont.appendChild(div);
        });
    } catch (e) {
        console.error('No se pudieron cargar los personajes', e);
    }
}

async function cargarArtePreview(tipo, contenedorId) {
    try {
        const res = await fetch(`/api/arte/tipo/${tipo}`);
        const items = await res.json();
        const cont = document.getElementById(contenedorId);
        cont.innerHTML = '';

        for (const item of items.slice(0, 8)) {
            const div = document.createElement('div');
            div.className = 'arte-item';
            div.onclick = () => abrirModalContenido('ARTE', item.id, item.titulo, item.imagenUrl);

            const img = document.createElement('img');
            img.src = item.imagenUrl;
            img.alt = item.titulo;
            div.appendChild(img);

            const stats = document.createElement('div');
            stats.className = 'mini-stats';
            stats.innerHTML = `<span><i class="fa-regular fa-heart"></i> <span class="mini-likes">0</span></span>
                                <span><i class="fa-regular fa-comment"></i> <span class="mini-comments">0</span></span>`;
            div.appendChild(stats);
            cont.appendChild(div);

            fetch(`/api/likes/ARTE/${item.id}/count`)
                .then(r => r.json())
                .then(d => stats.querySelector('.mini-likes').textContent = d.totalLikes ?? 0);
            fetch(`/api/comentarios/ARTE/${item.id}/count`)
                .then(r => r.json())
                .then(d => stats.querySelector('.mini-comments').textContent = d.total ?? 0);
        }
    } catch (e) {
        console.error('No se pudo cargar el contenido de ' + tipo, e);
    }
}

async function cargarEstadoLike(tipo, id) {
    try {
        const res = await fetch(`/api/likes/${tipo}/${id}/check`);
        const data = await res.json();
        const btn = document.getElementById('like-btn');
        document.getElementById('like-count').textContent = data.totalLikes ?? 0;
        btn.classList.toggle('liked', !!data.leGusta);
        btn.querySelector('i').className = data.leGusta ? 'fa-solid fa-heart' : 'fa-regular fa-heart';
    } catch (e) {
        console.error('No se pudo verificar el like', e);
    }
}

async function toggleLike(tipo, id) {
    const logged = document.body.getAttribute('data-logged') === 'true';
    if (!logged) {
        window.location.href = '/login';
        return;
    }
    try {
        const res = await fetch(`/api/likes/${tipo}/${id}/toggle`, { method: 'POST' });
        if (res.status === 401) { window.location.href = '/login'; return; }
        const data = await res.json();
        const btn = document.getElementById('like-btn');
        document.getElementById('like-count').textContent = data.totalLikes ?? 0;
        btn.classList.toggle('liked', !!data.leGusta);
        btn.querySelector('i').className = data.leGusta ? 'fa-solid fa-heart' : 'fa-regular fa-heart';
    } catch (e) {
        console.error('No se pudo procesar el like', e);
    }
}

async function cargarComentarios(tipo, id) {
    try {
        const res = await fetch(`/api/comentarios/${tipo}/${id}`);
        const comentarios = await res.json();
        const lista = document.getElementById('comments-list');
        lista.innerHTML = '';

        document.getElementById('comment-count').textContent = comentarios.length;

        if (comentarios.length === 0) {
            lista.innerHTML = '<p style="color:rgba(246,239,235,0.5); font-size:13px;">Sé el primero en comentar.</p>';
            return;
        }

        comentarios.forEach(c => {
            const div = document.createElement('div');
            div.className = 'comment-item';
            const fecha = new Date(c.fecha).toLocaleString('es-PE');
            div.innerHTML = `
                <div class="comment-user">${c.usuario}</div>
                <div class="comment-text">${escaparHtml(c.texto)}</div>
                <div class="comment-date">${fecha}</div>
            `;
            lista.appendChild(div);
        });
    } catch (e) {
        console.error('No se pudieron cargar los comentarios', e);
    }
}

async function enviarComentario(tipo, id) {
    const logged = document.body.getAttribute('data-logged') === 'true';
    if (!logged) {
        window.location.href = '/login';
        return;
    }
    const textarea = document.getElementById('comment-text');
    const texto = textarea.value.trim();
    if (!texto) return;

    try {
        const res = await fetch(`/api/comentarios/${tipo}/${id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ texto })
        });
        if (res.status === 401) { window.location.href = '/login'; return; }
        if (!res.ok) {
            const err = await res.json();
            alert(err.error || 'No se pudo publicar el comentario');
            return;
        }
        textarea.value = '';
        cargarComentarios(tipo, id);
    } catch (e) {
        console.error('No se pudo enviar el comentario', e);
    }
}

async function cargarNotificaciones() {
    try {
        const res = await fetch('/api/notificaciones/contar');
        const data = await res.json();
        const badge = document.getElementById('notif-badge');
        if (data.total > 0) {
            badge.textContent = data.total;
            badge.style.display = 'inline-block';
        }
    } catch (e) {
        console.error('No se pudo cargar el contador de notificaciones', e);
    }
}

function escaparHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
}