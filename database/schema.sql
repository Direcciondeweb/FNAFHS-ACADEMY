-- =============================================
-- FNAFHS ACADEMY - SCHEMA DE BASE DE DATOS
-- =============================================

-- Crear base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS fnafhs_db;
USE fnafhs_db;

-- =============================================
-- 1. TABLA: usuarios
-- =============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'USER',
    nombre_completo VARCHAR(100),
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    estado INT DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 2. TABLA: usuario_permisos
-- =============================================
CREATE TABLE IF NOT EXISTS usuario_permisos (
    usuario_id BIGINT NOT NULL,
    permiso VARCHAR(50) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, permiso)
);

-- =============================================
-- 3. TABLA: usuario_permisos_subadmin
-- =============================================
CREATE TABLE IF NOT EXISTS usuario_permisos_subadmin (
    usuario_id BIGINT NOT NULL,
    permiso_key VARCHAR(50) NOT NULL,
    permiso_value BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, permiso_key)
);

-- =============================================
-- 4. TABLA: personajes
-- =============================================
CREATE TABLE IF NOT EXISTS personajes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50),
    descripcion TEXT,
    imagen_url VARCHAR(500),
    imagen_original_url VARCHAR(500),
    estado INT DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 5. TABLA: personaje_galeria
-- =============================================
CREATE TABLE IF NOT EXISTS personaje_galeria (
    personaje_id BIGINT NOT NULL,
    galeria_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (personaje_id) REFERENCES personajes(id) ON DELETE CASCADE
);

-- =============================================
-- 6. TABLA: arte
-- =============================================
CREATE TABLE IF NOT EXISTS arte (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200),
    tipo VARCHAR(50) NOT NULL,
    imagen_url VARCHAR(500),
    comic_id VARCHAR(100),
    total_paginas INT DEFAULT 1,
    estado INT DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 7. TABLA: videos
-- =============================================
CREATE TABLE IF NOT EXISTS videos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    video_url VARCHAR(500) NOT NULL,
    estado INT DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 8. TABLA: slider_images
-- =============================================
CREATE TABLE IF NOT EXISTS slider_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    imagen_url VARCHAR(500) NOT NULL,
    activo BOOLEAN DEFAULT FALSE,
    orden INT DEFAULT 0
);

-- =============================================
-- 9. TABLA: logos
-- =============================================
CREATE TABLE IF NOT EXISTS logos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    imagen_url VARCHAR(500) NOT NULL,
    titulo VARCHAR(100),
    activo BOOLEAN DEFAULT FALSE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 10. TABLA: password_reset_tokens
-- =============================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- =============================================
-- ÍNDICES PARA MEJORAR RENDIMIENTO
-- =============================================
CREATE INDEX idx_arte_tipo ON arte(tipo);
CREATE INDEX idx_arte_comic_id ON arte(comic_id);
CREATE INDEX idx_personajes_categoria ON personajes(categoria);
CREATE INDEX idx_personajes_estado ON personajes(estado);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_slider_activo ON slider_images(activo);
CREATE INDEX idx_logos_activo ON logos(activo);
CREATE INDEX idx_password_token ON password_reset_tokens(token);