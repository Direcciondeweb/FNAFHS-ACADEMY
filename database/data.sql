-- =============================================
-- FNAFHS ACADEMY - DATOS INICIALES
-- =============================================

USE fnafhs_db;

-- =============================================
-- 1. USUARIOS POR DEFECTO
-- =============================================

-- Usuario ADMIN (contraseña: admin123)
INSERT INTO usuarios (username, password, rol, nombre_completo, email, estado) 
VALUES ('admin', 'admin123', 'ADMIN', 'Administrador del Sistema', 'admin@fnafhs.com', 1)
ON DUPLICATE KEY UPDATE id=id;

-- Usuario SUBADMIN (contraseña: subadmin123)
INSERT INTO usuarios (username, password, rol, nombre_completo, email, estado) 
VALUES ('subadmin', 'subadmin123', 'SUBADMIN', 'Sub Administrador', 'subadmin@fnafhs.com', 1)
ON DUPLICATE KEY UPDATE id=id;

-- Usuario USER (contraseña: user123)
INSERT INTO usuarios (username, password, rol, nombre_completo, email, estado) 
VALUES ('user', 'user123', 'USER', 'Usuario Normal', 'user@fnafhs.com', 1)
ON DUPLICATE KEY UPDATE id=id;

-- =============================================
-- 2. PERSONAJES DE EJEMPLO
-- =============================================

INSERT INTO personajes (nombre, categoria, descripcion, estado) VALUES
('Freddy Fazbear', 'animatronics', 'El líder del grupo, un oso animatrónico carismático y protector.', 1),
('Bonnie the Bunny', 'animatronics', 'El guitarrista del grupo, un conejo morado muy talentoso.', 1),
('Chica the Chicken', 'animatronics', 'La vocalista, una gallina amarilla amante de la pizza.', 1),
('Foxy the Pirate', 'animatronics', 'El pirata del grupo, un zorro con parche en el ojo.', 1),
('Golden Freddy', 'antagonists', 'Un misterioso oso dorado con poderes sobrenaturales.', 1),
('Springtrap', 'antagonists', 'El villano principal, atrapado dentro de un traje de springlock.', 1),
('Mangle', 'toys', 'Un animatrónico desarmado pero querido por los niños.', 1),
('Puppet', 'marionetas', 'El vigilante espiritual de la pizzería.', 1)
ON DUPLICATE KEY UPDATE id=id;

-- =============================================
-- 3. ARTE DE EJEMPLO
-- =============================================

INSERT INTO arte (titulo, tipo, imagen_url, estado) VALUES
('Freddy Fazbear - Arte Oficial', 'arte-oficial', 'https://via.placeholder.com/300x300?text=Freddy', 1),
('Bonnie - Arte Oficial', 'arte-oficial', 'https://via.placeholder.com/300x300?text=Bonnie', 1),
('Chica - Arte Oficial', 'arte-oficial', 'https://via.placeholder.com/300x300?text=Chica', 1),
('Fanart de Foxy', 'fanart', 'https://via.placeholder.com/300x300?text=Foxy+Fanart', 1),
('Fanart de Mangle', 'fanart', 'https://via.placeholder.com/300x300?text=Mangle+Fanart', 1)
ON DUPLICATE KEY UPDATE id=id;

-- =============================================
-- 4. SLIDER IMAGES DE EJEMPLO
-- =============================================

INSERT INTO slider_images (imagen_url, activo, orden) VALUES
('https://via.placeholder.com/1920x1080?text=FNAFHS+1', 1, 0),
('https://via.placeholder.com/1920x1080?text=FNAFHS+2', 1, 1),
('https://via.placeholder.com/1920x1080?text=FNAFHS+3', 1, 2)
ON DUPLICATE KEY UPDATE id=id;

-- =============================================
-- 5. LOGO DE EJEMPLO
-- =============================================

INSERT INTO logos (imagen_url, titulo, activo) VALUES
('https://via.placeholder.com/200x200?text=FNAFHS+Logo', 'Logo FNAFHS Academy', 1)
ON DUPLICATE KEY UPDATE id=id;