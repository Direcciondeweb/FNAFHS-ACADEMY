document.addEventListener('DOMContentLoaded', () => {
    const logged = document.body.getAttribute('data-logged') === 'true';

    cargarSlider();
    cargarInfoSitio();
    if (logged) cargarNotificaciones();

    // ---- Menú hamburguesa ----
    document.getElementById('nav-toggle')?.addEventListener('click', () => {
        document.getElementById('nav-list').classList.toggle('open');
    });

    // ---- Carrusel automático ----
    let slideIndex = 0;
    setInterval(() => {
        const slider = document.getElementById('hero-slider');
        if (!slider) return;
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
        if (!slider || !imagenes || imagenes.length === 0) return;

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

async function cargarInfoSitio() {
    try {
        const res = await fetch('/api/info-sitio');
        const info = await res.json();
        if (info.vision && info.vision.trim() !== '') {
            document.getElementById('info-vision').textContent = info.vision;
        }
        if (info.mision && info.mision.trim() !== '') {
            document.getElementById('info-mision').textContent = info.mision;
        }
    } catch (e) {
        console.error('No se pudo cargar la info del sitio', e);
    }
}

async function cargarNotificaciones() {
    try {
        const res = await fetch('/api/notificaciones/contar');
        const data = await res.json();
        const badge = document.getElementById('notif-badge');
        if (badge && data.total > 0) {
            badge.textContent = data.total;
            badge.style.display = 'inline-block';
        }
    } catch (e) {
        console.error('No se pudo cargar el contador de notificaciones', e);
    }
}