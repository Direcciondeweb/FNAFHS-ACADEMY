document.addEventListener('DOMContentLoaded', () => {
    // ---- Navegación entre pestañas ----
    document.querySelectorAll('.admin-tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.admin-tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.admin-tab-content').forEach(c => c.classList.remove('active'));
            btn.classList.add('active');
            document.getElementById(btn.dataset.tab).classList.add('active');
            cargarPestaña(btn.dataset.tab);
        });
    });

    cargarDashboard();

    function cargarPestaña(tab) {
        switch (tab) {
            case 'tab-dashboard': cargarDashboard(); break;
            case 'tab-personajes': cargarPersonajes(); break;
            case 'tab-arte': cargarArte('arte-oficial'); break;
            case 'tab-videos': cargarVideos(); break;
            case 'tab-comics': cargarComics(); break;
            case 'tab-descartados': cargarDescartados(); break;
            case 'tab-reportes': cargarReportes('PENDIENTE'); break;
            case 'tab-comentarios': cargarComentarios(); break;
            case 'tab-usuarios': cargarUsuarios(); break;
            case 'tab-carrusel': cargarSlider(); break;
            case 'tab-info': cargarInfo(); break;
            case 'tab-historial': cargarHistorial(); break;
        }
    }

    // ================= DASHBOARD =================
    async function cargarDashboard() {
        const res = await fetch('/api/analytics/dashboard');
        const data = await res.json();

        document.getElementById('stats-grid').innerHTML = `
            ${statCard(data.totalUsuarios, 'Usuarios')}
            ${statCard(data.totalArte, 'Arte')}
            ${statCard(data.totalVideos, 'Videos')}
            ${statCard(data.totalPersonajes, 'Personajes')}
            ${statCard(data.comics, 'Comics')}
            ${statCard(data.totalComentarios, 'Comentarios')}
            ${statCard(data.totalReportesPendientes, 'Reportes pendientes')}
        `;

        document.getElementById('ultimas-acciones-list').innerHTML = data.ultimasAcciones.map(a => `
            <div class="accion-item"><b>${a.usuario}</b> ${a.accion} — ${a.entidad} <span style="opacity:.6">(${new Date(a.fecha).toLocaleString('es-PE')})</span></div>
        `).join('') || '<p style="opacity:.5">Sin actividad aún.</p>';

        dibujarGraficoBarras('chart-contenido', data.contenidoPorTipo);
        dibujarGraficoDona('chart-usuarios', data.usuariosPorRol);
    }

    function statCard(valor, label) {
        return `<div class="stat-card"><div class="stat-value">${valor}</div><div class="stat-label">${label}</div></div>`;
    }

    let chartContenido, chartUsuarios;
    function dibujarGraficoBarras(canvasId, dataObj) {
        const ctx = document.getElementById(canvasId);
        if (chartContenido) chartContenido.destroy();
        chartContenido = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(dataObj),
                datasets: [{ data: Object.values(dataObj), backgroundColor: '#d0fc0b' }]
            },
            options: { plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
        });
    }

    function dibujarGraficoDona(canvasId, dataObj) {
        const ctx = document.getElementById(canvasId);
        if (chartUsuarios) chartUsuarios.destroy();
        chartUsuarios = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(dataObj),
                datasets: [{ data: Object.values(dataObj), backgroundColor: ['#fd0000', '#a725bb', '#d0fc0b'] }]
            }
        });
    }

    // ================= PERSONAJES =================
    document.getElementById('form-personaje').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData();
        fd.append('nombre', document.getElementById('p-nombre').value);
        fd.append('categoria', document.getElementById('p-categoria').value);
        fd.append('descripcion', document.getElementById('p-descripcion').value);
        const img = document.getElementById('p-imagen').files[0];
        const imgOrig = document.getElementById('p-imagen-original').files[0];
        if (img) fd.append('imagenFile', img);
        if (imgOrig) fd.append('imagenOriginalFile', imgOrig);

        await fetch('/api/personajes', { method: 'POST', body: fd });
        e.target.reset();
        cargarPersonajes();
    });

    async function cargarPersonajes() {
        const res = await fetch('/api/personajes');
        const lista = await res.json();
        document.getElementById('personajes-admin-grid').innerHTML = lista.map(p => `
            <div class="admin-card">
                <img src="${p.imagenUrl || ''}" alt="${p.nombre}">
                <div class="card-title">${p.nombre}</div>
                <div class="card-actions">
                    <button class="btn-delete" onclick="eliminarItem('/api/personajes/${p.id}', cargarPersonajes)">Eliminar</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">No hay personajes aún.</p>';
    }
    window.cargarPersonajes = cargarPersonajes;

    // ================= ARTE / FANARTS =================
    document.getElementById('form-arte').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData();
        fd.append('titulo', document.getElementById('a-titulo').value);
        fd.append('tipo', document.getElementById('a-tipo').value);
        fd.append('imagenFile', document.getElementById('a-imagen').files[0]);

        await fetch('/api/arte', { method: 'POST', body: fd });
        e.target.reset();
        cargarArte(document.querySelector('#tab-arte .filter-chip.active').dataset.filtro);
    });

    document.querySelectorAll('#tab-arte .filter-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('#tab-arte .filter-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            cargarArte(chip.dataset.filtro);
        });
    });

    async function cargarArte(tipo) {
        const res = await fetch(`/api/arte/tipo/${tipo}`);
        const lista = await res.json();
        document.getElementById('arte-admin-grid').innerHTML = lista.map(a => `
            <div class="admin-card">
                <img src="${a.imagenUrl}" alt="${a.titulo}">
                <div class="card-title">${a.titulo}</div>
                <div class="card-actions">
                    <button class="btn-delete" onclick="eliminarItem('/api/arte/${a.id}', () => cargarArte('${tipo}'))">Eliminar</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">Sin contenido en esta categoría.</p>';
    }
    window.cargarArte = cargarArte;

    // ================= VIDEOS =================
    document.getElementById('form-video').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData();
        fd.append('titulo', document.getElementById('v-titulo').value);
        fd.append('videoFile', document.getElementById('v-archivo').files[0]);

        await fetch('/api/videos', { method: 'POST', body: fd });
        e.target.reset();
        cargarVideos();
    });

    async function cargarVideos() {
        const res = await fetch('/api/videos');
        const lista = await res.json();
        document.getElementById('videos-admin-grid').innerHTML = lista.map(v => `
            <div class="admin-card">
                <video src="${v.videoUrl}" muted></video>
                <div class="card-title">${v.titulo}</div>
                <div class="card-actions">
                    <button class="btn-delete" onclick="eliminarItem('/api/videos/${v.id}', cargarVideos)">Eliminar</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">No hay videos aún.</p>';
    }
    window.cargarVideos = cargarVideos;

    // ================= COMICS (tipo=comic, agrupado por comicId) =================
    document.getElementById('form-comic').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData();
        fd.append('titulo', document.getElementById('c-titulo').value);
        fd.append('tipo', 'comic');
        fd.append('comicId', document.getElementById('c-comicid').value);
        fd.append('imagenFile', document.getElementById('c-imagen').files[0]);

        await fetch('/api/arte', { method: 'POST', body: fd });
        e.target.reset();
        cargarComics();
    });

    async function cargarComics() {
        const res = await fetch('/api/arte/tipo/comic');
        const lista = await res.json();
        const grupos = {};
        lista.forEach(pagina => {
            const key = pagina.comicId || 'sin-id';
            if (!grupos[key]) grupos[key] = [];
            grupos[key].push(pagina);
        });

        document.getElementById('comics-admin-list').innerHTML = Object.entries(grupos).map(([comicId, paginas]) => `
            <div class="admin-list-item">
                <div class="item-meta">Comic: <b>${comicId}</b> — ${paginas.length} página(s)</div>
                <div class="admin-grid" style="grid-template-columns: repeat(auto-fill, minmax(100px,1fr));">
                    ${paginas.map(p => `
                        <div class="admin-card">
                            <img src="${p.imagenUrl}" alt="${p.titulo}">
                            <div class="card-title">${p.titulo}</div>
                            <div class="card-actions">
                                <button class="btn-delete" onclick="eliminarItem('/api/arte/${p.id}', cargarComics)">Eliminar</button>
                            </div>
                        </div>
                    `).join('')}
                </div>
                <div class="item-actions" style="margin-top:10px;">
                    <button class="btn-reject" onclick="eliminarComicCompleto('${comicId}')">Eliminar comic completo</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">No hay comics aún.</p>';
    }
    window.cargarComics = cargarComics;

    window.eliminarComicCompleto = async (comicId) => {
        if (!confirm('¿Eliminar todas las páginas de este comic?')) return;
        await fetch(`/api/arte/comic/${comicId}`, { method: 'DELETE' });
        cargarComics();
    };

    // ================= DESCARTADOS =================
    document.getElementById('form-descartado').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData();
        fd.append('titulo', document.getElementById('d-titulo').value);
        fd.append('tipo', 'descartado');
        fd.append('imagenFile', document.getElementById('d-imagen').files[0]);

        await fetch('/api/arte', { method: 'POST', body: fd });
        e.target.reset();
        cargarDescartados();
    });

    async function cargarDescartados() {
        const res = await fetch('/api/arte/tipo/descartado');
        const lista = await res.json();
        document.getElementById('descartados-admin-grid').innerHTML = lista.map(d => `
            <div class="admin-card">
                <img src="${d.imagenUrl}" alt="${d.titulo}">
                <div class="card-title">${d.titulo}</div>
                <div class="card-actions">
                    <button class="btn-delete" onclick="eliminarItem('/api/arte/${d.id}', cargarDescartados)">Eliminar</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">No hay material descartado.</p>';
    }
    window.cargarDescartados = cargarDescartados;

    // ================= REPORTES =================
    document.querySelectorAll('#tab-reportes .filter-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('#tab-reportes .filter-chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            cargarReportes(chip.dataset.filtroReporte);
        });
    });

    async function cargarReportes(filtro) {
        const url = filtro === 'PENDIENTE' ? '/api/reportes/admin/pendientes?size=50' : '/api/reportes/admin?size=50';
        const res = await fetch(url);
        const data = await res.json();
        const lista = data.content || [];

        document.getElementById('reportes-list').innerHTML = lista.map(r => `
            <div class="admin-list-item">
                <div class="item-meta">
                    <span class="badge-${r.estado.toLowerCase()}">${r.estado}</span> —
                    ${r.contenidoTipo} #${r.contenidoId} — reportado por <b>${r.reportadoPor}</b>
                </div>
                <div class="item-text"><b>Motivo:</b> ${r.motivo}${r.descripcion ? ' — ' + r.descripcion : ''}</div>
                ${r.estado === 'PENDIENTE' ? `
                    <div class="item-actions">
                        <button class="btn-resolve" onclick="resolverReporte(${r.id})">Resolver</button>
                        <button class="btn-reject" onclick="rechazarReporte(${r.id})">Rechazar</button>
                    </div>
                ` : ''}
            </div>
        `).join('') || '<p style="opacity:.5">No hay reportes en esta vista.</p>';
    }

    window.resolverReporte = async (id) => {
        await fetch(`/api/reportes/admin/${id}/resolver`, { method: 'PUT' });
        cargarReportes(document.querySelector('#tab-reportes .filter-chip.active').dataset.filtroReporte);
    };
    window.rechazarReporte = async (id) => {
        await fetch(`/api/reportes/admin/${id}/rechazar`, { method: 'PUT' });
        cargarReportes(document.querySelector('#tab-reportes .filter-chip.active').dataset.filtroReporte);
    };

    // ================= COMENTARIOS =================
    async function cargarComentarios() {
        const res = await fetch('/api/comentarios/admin/todos?size=50');
        const data = await res.json();
        const lista = data.content || [];

        document.getElementById('comentarios-admin-list').innerHTML = lista.map(c => `
            <div class="admin-list-item">
                <div class="item-meta">
                    <b>${c.usuario}</b> en ${c.contenidoTipo} #${c.contenidoId}
                    ${c.fijado ? ' 📌 Fijado' : ''}${c.censurado ? ' 🚫 Censurado' : ''}
                </div>
                <div class="item-text">${c.texto}</div>
                <div class="item-actions">
                    <button class="btn-pin" onclick="toggleComentario(${c.id}, 'fijar', ${!c.fijado})">${c.fijado ? 'Desfijar' : 'Fijar'}</button>
                    <button class="btn-censor" onclick="toggleComentario(${c.id}, 'censurar', ${!c.censurado})">${c.censurado ? 'Descensurar' : 'Censurar'}</button>
                    <button class="btn-reject" onclick="eliminarComentario(${c.id})">Eliminar</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">No hay comentarios aún.</p>';
    }
    window.cargarComentarios = cargarComentarios;

    window.toggleComentario = async (id, accion, valor) => {
        await fetch(`/api/comentarios/admin/${id}/${accion}?valor=${valor}`, { method: 'PUT' });
        cargarComentarios();
    };
    window.eliminarComentario = async (id) => {
        if (!confirm('¿Eliminar este comentario?')) return;
        await fetch(`/api/comentarios/admin/${id}`, { method: 'DELETE' });
        cargarComentarios();
    };

    // ================= USUARIOS =================
    async function cargarUsuarios() {
        const res = await fetch('/api/usuarios?size=100');
        const data = await res.json();
        const lista = data.content || [];

        document.getElementById('usuarios-tbody').innerHTML = lista.map(u => `
            <tr>
                <td>${u.username}</td>
                <td>${u.nombreCompleto || '-'}</td>
                <td>${u.email || '-'}</td>
                <td>
                    <select onchange="cambiarRol(${u.id}, this.value)">
                        <option value="USER" ${u.rol === 'USER' ? 'selected' : ''}>USER</option>
                        <option value="SUBADMIN" ${u.rol === 'SUBADMIN' ? 'selected' : ''}>SUBADMIN</option>
                        <option value="ADMIN" ${u.rol === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                    </select>
                </td>
                <td>
                    <select onchange="cambiarEstado(${u.id}, this.value)">
                        <option value="1" ${u.estado === 1 ? 'selected' : ''}>Activo</option>
                        <option value="0" ${u.estado === 0 ? 'selected' : ''}>Inactivo</option>
                    </select>
                </td>
                <td>${new Date(u.fechaRegistro).toLocaleDateString('es-PE')}</td>
                <td><button class="btn-delete" onclick="eliminarUsuario(${u.id})">Eliminar</button></td>
            </tr>
        `).join('');
    }
    window.cargarUsuarios = cargarUsuarios;

    window.cambiarRol = async (id, rol) => {
        await fetch(`/api/usuarios/${id}/rol?rol=${rol}`, { method: 'PUT' });
        cargarUsuarios();
    };
    window.cambiarEstado = async (id, estado) => {
        await fetch(`/api/usuarios/${id}/estado?estado=${estado}`, { method: 'PUT' });
    };
    window.eliminarUsuario = async (id) => {
        if (!confirm('¿Eliminar este usuario permanentemente?')) return;
        await fetch(`/api/usuarios/${id}`, { method: 'DELETE' });
        cargarUsuarios();
    };

    // ================= CARRUSEL =================
    document.getElementById('form-slider').addEventListener('submit', async (e) => {
        e.preventDefault();
        const fd = new FormData();
        fd.append('imagen', document.getElementById('s-imagen').files[0]);

        await fetch('/api/slider', { method: 'POST', body: fd });
        e.target.reset();
        cargarSlider();
    });

    async function cargarSlider() {
        const res = await fetch('/api/slider');
        const lista = await res.json();
        document.getElementById('slider-admin-grid').innerHTML = lista.map(s => `
            <div class="admin-card">
                <img src="${s.imagenUrl}" alt="Slider">
                <div class="card-title">${s.activo ? '✅ Activa' : 'Inactiva'}</div>
                <div class="card-actions">
                    <button class="btn-toggle" onclick="toggleSlider(${s.id}, ${!s.activo})">${s.activo ? 'Desactivar' : 'Activar'}</button>
                    <button class="btn-delete" onclick="eliminarItem('/api/slider/${s.id}', cargarSlider)">Eliminar</button>
                </div>
            </div>
        `).join('') || '<p style="opacity:.5">No hay imágenes en el carrusel.</p>';
    }
    window.cargarSlider = cargarSlider;

    window.toggleSlider = async (id, activo) => {
        await fetch(`/api/slider/${id}/toggle?activo=${activo}`, { method: 'PUT' });
        cargarSlider();
    };

    // ================= VISION / MISION =================
    document.getElementById('form-info').addEventListener('submit', async (e) => {
        e.preventDefault();
        await fetch('/api/info-sitio', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                vision: document.getElementById('info-vision-input').value,
                mision: document.getElementById('info-mision-input').value
            })
        });
        alert('Guardado correctamente');
    });

    async function cargarInfo() {
        const res = await fetch('/api/info-sitio');
        const data = await res.json();
        document.getElementById('info-vision-input').value = data.vision || '';
        document.getElementById('info-mision-input').value = data.mision || '';
    }
    window.cargarInfo = cargarInfo;

    // ================= HISTORIAL =================
    async function cargarHistorial() {
        const res = await fetch('/api/historial?size=50');
        const data = await res.json();
        const lista = data.content || [];

        document.getElementById('historial-tbody').innerHTML = lista.map(h => `
            <tr>
                <td>${new Date(h.fecha).toLocaleString('es-PE')}</td>
                <td>${h.usuario}</td>
                <td>${h.rol}</td>
                <td>${h.accion}</td>
                <td>${h.entidad}</td>
                <td>${h.detalle || '-'}</td>
            </tr>
        `).join('');
    }
    window.cargarHistorial = cargarHistorial;

    // ================= UTILIDAD GENERAL =================
    window.eliminarItem = async (url, callback) => {
        if (!confirm('¿Eliminar este elemento?')) return;
        await fetch(url, { method: 'DELETE' });
        callback();
    };
});