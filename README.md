# ★ FNAFHS ACADEMY ★

[![GitHub Pages](https://img.shields.io/badge/GitHub%20Pages-Deployed-blue)](https://tu-usuario.github.io/FNAFHS-ACADEMY)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 🌟 Descripción

Portal oficial del fandom **FNAFHS Academy** - Una plataforma completa para gestionar personajes, arte oficial, fanarts, cómics y contenido exclusivo de la comunidad.

## ✨ Características

- 👥 **Gestión de Usuarios** con roles (ADMIN, SUBADMIN, USER)
- 🎨 **CRUD completo** de personajes, arte oficial y fanarts
- 📚 **Sistema de cómics** con múltiples páginas
- 🎬 **Gestión de videos**
- 🖼️ **Slider dinámico** para página principal
- 📊 **Exportación a Excel** de todos los usuarios
- 🔐 **Recuperación de contraseña** vía email
- ☁️ **Almacenamiento en la nube** (Supabase)

## 🚀 Tecnologías

- **Backend:** Spring Boot 3.4.0, Java 21
- **Frontend:** HTML5, CSS3, JavaScript Vanilla
- **Base de Datos:** PostgreSQL (Cloud)
- **Almacenamiento:** Supabase Storage
- **Email:** Spring Mail (Gmail SMTP)
- **Build:** Maven

## 🌐 Enlaces del Proyecto

| Componente | URL |
|------------|-----|
| **Frontend (GitHub Pages)** | https://tu-usuario.github.io/FNAFHS-ACADEMY |
| **Backend API** | https://fnafhs-academy-backend.onrender.com |

## 🏗️ Estructura del Proyecto
FNAFHS-ACADEMY/
├── index.html # Página principal (RAÍZ)
├── frontend/ # Frontend estático (GitHub Pages)
│ ├── css/ # Estilos
│ ├── js/ # JavaScript
│ └── pages/ # HTML pages
├── backend/ # Spring Boot backend
└── docs/ # Documentación

## 📦 Instalación Local

```bash
# Clonar repositorio
git clone https://github.com/tu-usuario/FNAFHS-ACADEMY.git
cd FNAFHS-ACADEMY

# Iniciar backend
cd backend
./mvnw spring-boot:run

# El frontend se sirve desde GitHub Pages o localmente