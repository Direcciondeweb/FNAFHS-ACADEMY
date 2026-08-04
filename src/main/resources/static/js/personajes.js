document.addEventListener('DOMContentLoaded', () => {
    let personajesCache = [];
    let personajeActual = null;

    cargarPersonajes();

    // ---- Menú hamburguesa ----
    document.getElementById('nav-toggle')?.addEventListener('click', () => {
        document.getElementById('nav-list').classList.toggle('open');
    });

    // ---- Panel de filtro ----
    const filterPanel = document.getElementById('filter-panel');
    document.getElementById('filter-toggle').addEventListener('click', () => {
        filterPanel.classList.toggle('active');
    });
    document.getElementById('close-filter').addEventListener('click', () => {
        filterPanel.classList.remove('active');
    });
    document.addEventListener('click', (e) => {
        if (!filterPanel.contains(e.target) && !document.getElementById('filter-toggle').contains(e.target)) {
            filterPanel.classList.remove('active');
        }
    });

    document.getElementById('apply-filter').addEventListener('click', () => {
        aplicarFiltros();
        filterPanel.classList.remove('active');
    });

    document.getElementById('reset-filter').addEventListener('click', () => {
        document.getElementById('filtro-categoria').value = '';
        document.getElementById('filtro-nombre').value = '';
        renderPersonajes(personajesCache);
        filterPanel.classList.remove('active');
    });

    document.getElementById('filtro-nombre').addEventListener('keyup', (e) => {
        if (e.key === 'Enter') { aplicarFiltros(); filterPanel.classList.remove('active'); }
    });

    async function cargarPersonajes() {
        try {
            const res = await fetch('/api/personajes/activos');
            personajesCache = await res.json();

            // Llenar el select de categorías dinámicamente según lo que exista
            const categorias = [...new Set(personajesCache.map(p => p.categoria).filter(Boolean))].sort();
            const select = document.getElementById('filtro-categoria');
            select.innerHTML = '<option value="">Todas las categorías</option>' +
                categorias.map(c => `<option value="${c}">${c}</option>`).join('');

            renderPersonajes(personajesCache);
        } catch (e) {
            console.error('No se pudieron cargar los personajes', e);
            document.getElementById('personajes-grid').innerHTML =
                '<p class="empty-state">Ocurrió un error al cargar los personajes.</p>';
        }
    }

    function renderPersonajes(lista) {
        const grid = document.getElementById('personajes-grid');

        if (!lista || lista.length === 0) {
            grid.innerHTML = '<p class="empty-state">No hay personajes que coincidan con tu búsqueda.</p>';
            return;
        }

        grid.innerHTML = lista.map(p => `
            <div class="personaje-card" data-id="${p.id}">
                <div class="img-wrap">
                    <img src="${p.imagenUrl || ''}" alt="${p.nombre}" loading="lazy">
                </div>
                <div class="info">
                    <h3>${p.nombre}</h3>
                    ${p.categoria ? `<span class="categoria-tag">${p.categoria}</span>` : ''}
                    <p>${p.descripcion || 'Sin descripción disponible.'}</p>
                </div>
            </div>
        `).join('');

        document.querySelectorAll('.personaje-card').forEach(card => {
            card.addEventListener('click', () => {
                const id = card.dataset.id;
                const personaje = lista.find(p => String(p.id) === id);
                abrirModal(personaje);
            });
        });
    }

    function aplicarFiltros() {
        const categoria = document.getElementById('filtro-categoria').value;
        const nombre = document.getElementById('filtro-nombre').value.trim().toLowerCase();

        const filtrado = personajesCache.filter(p => {
            const coincideCategoria = !categoria || p.categoria === categoria;
            const coincideNombre = !nombre || p.nombre.toLowerCase().includes(nombre);
            return coincideCategoria && coincideNombre;
        });

        renderPersonajes(filtrado);
    }

    // ---- Modal Academy / Original ----
    const modal = document.getElementById('personaje-modal');
    const originalWrapper = document.getElementById('original-wrapper');
    const btnVerOriginal = document.getElementById('btn-ver-original');

    function abrirModal(personaje) {
        personajeActual = personaje;

        document.getElementById('modal-nombre').textContent = personaje.nombre;
        document.getElementById('modal-categoria').textContent = personaje.categoria || '';
        document.getElementById('modal-imagen-academy').src = personaje.imagenUrl || '';
        document.getElementById('modal-descripcion').textContent = personaje.descripcion || 'Sin descripción disponible.';

        // Reinicia el estado: solo se ve la versión Academy hasta que se pida la original
        originalWrapper.classList.add('hidden');

        if (personaje.imagenOriginalUrl) {
            btnVerOriginal.classList.remove('hidden-btn');
            btnVerOriginal.innerHTML = '<i class="fa-solid fa-eye"></i> Ver versión original';
        } else {
            btnVerOriginal.classList.add('hidden-btn');
        }

        modal.style.display = 'block';
    }

    btnVerOriginal.addEventListener('click', () => {
        if (!personajeActual || !personajeActual.imagenOriginalUrl) return;

        const yaVisible = !originalWrapper.classList.contains('hidden');

        if (yaVisible) {
            originalWrapper.classList.add('hidden');
            btnVerOriginal.innerHTML = '<i class="fa-solid fa-eye"></i> Ver versión original';
        } else {
            document.getElementById('modal-imagen-original').src = personajeActual.imagenOriginalUrl;
            originalWrapper.classList.remove('hidden');
            btnVerOriginal.innerHTML = '<i class="fa-solid fa-eye-slash"></i> Ocultar versión original';
        }
    });

    document.getElementById('modal-close').addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.addEventListener('click', (e) => {
        if (e.target === modal) modal.style.display = 'none';
    });
});