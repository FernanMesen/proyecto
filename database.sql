-- =============================================
-- BOLSA DE EMPLEO — Script SQL Server
-- EIF209 Programación 4 — 2026-01
-- Ejecutar conectado como: sa / StrongPass123!
-- =============================================

-- Crear base de datos
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'bolsa_empleo')
    CREATE DATABASE bolsa_empleo;
GO

USE bolsa_empleo;
GO

-- =============================================
-- TABLAS
-- =============================================

-- USUARIO (tabla base para login)
CREATE TABLE usuario (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    correo         NVARCHAR(150) NOT NULL UNIQUE,
    clave          NVARCHAR(255) NOT NULL,
    rol            NVARCHAR(20)  NOT NULL
                   CONSTRAINT chk_rol CHECK (rol IN ('EMPRESA','OFERENTE','ADMIN')),
    activo         BIT           NOT NULL DEFAULT 0,
    fecha_registro DATETIME2     NOT NULL DEFAULT SYSDATETIME()
);
GO

-- EMPRESA
CREATE TABLE empresa (
    id           BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_id   BIGINT        NOT NULL UNIQUE,
    nombre       NVARCHAR(200) NOT NULL,
    localizacion NVARCHAR(200),
    telefono     NVARCHAR(20),
    descripcion  NVARCHAR(MAX),
    CONSTRAINT fk_empresa_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
GO

-- OFERENTE
CREATE TABLE oferente (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    usuario_id      BIGINT        NOT NULL UNIQUE,
    identificacion  NVARCHAR(30)  NOT NULL,
    nombre          NVARCHAR(100) NOT NULL,
    primer_apellido NVARCHAR(100),
    nacionalidad    NVARCHAR(80),
    telefono        NVARCHAR(20),
    residencia      NVARCHAR(200),
    cv_path         NVARCHAR(300),
    CONSTRAINT fk_oferente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
GO

-- CARACTERÍSTICA (árbol jerárquico)
CREATE TABLE caracteristica (
    id       BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre   NVARCHAR(150) NOT NULL,
    padre_id BIGINT NULL,
    CONSTRAINT fk_caracteristica_padre FOREIGN KEY (padre_id) REFERENCES caracteristica(id)
);
GO

-- HABILIDAD del oferente
CREATE TABLE habilidad (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    oferente_id       BIGINT NOT NULL,
    caracteristica_id BIGINT NOT NULL,
    nivel             INT    NOT NULL CONSTRAINT chk_nivel_hab CHECK (nivel BETWEEN 1 AND 5),
    CONSTRAINT uk_habilidad UNIQUE (oferente_id, caracteristica_id),
    CONSTRAINT fk_hab_oferente       FOREIGN KEY (oferente_id)       REFERENCES oferente(id),
    CONSTRAINT fk_hab_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
);
GO

-- PUESTO DE TRABAJO
CREATE TABLE puesto (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    empresa_id     BIGINT          NOT NULL,
    descripcion    NVARCHAR(300)   NOT NULL,
    salario        DECIMAL(10,2),
    tipo           NVARCHAR(10)    NOT NULL DEFAULT 'PUBLICO'
                   CONSTRAINT chk_tipo CHECK (tipo IN ('PUBLICO','PRIVADO')),
    activo         BIT             NOT NULL DEFAULT 1,
    fecha_registro DATETIME2       NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_puesto_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);
GO

-- CARACTERÍSTICA REQUERIDA POR EL PUESTO
CREATE TABLE puesto_caracteristica (
    id                BIGINT IDENTITY(1,1) PRIMARY KEY,
    puesto_id         BIGINT NOT NULL,
    caracteristica_id BIGINT NOT NULL,
    nivel_minimo      INT    NOT NULL CONSTRAINT chk_nivel_min CHECK (nivel_minimo BETWEEN 1 AND 5),
    CONSTRAINT uk_puesto_car UNIQUE (puesto_id, caracteristica_id),
    CONSTRAINT fk_pc_puesto         FOREIGN KEY (puesto_id)         REFERENCES puesto(id),
    CONSTRAINT fk_pc_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
);
GO

-- =============================================
-- DATOS INICIALES
-- =============================================

-- Admin por defecto  (clave: admin123  —  hash BCrypt)
INSERT INTO usuario (correo, clave, rol, activo)
VALUES ('admin@bolsaempleo.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZdIFiRnUu',
        'ADMIN', 1);
GO

-- Categorías raíz
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Bases de Datos',           NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Ciberseguridad',           NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Lenguajes de programación',NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Tecnologías Web',          NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Testing',                  NULL);
GO

-- Sub-categorías (padre_id corresponde al IDENTITY generado arriba)
INSERT INTO caracteristica (nombre, padre_id) VALUES ('MySql',      1);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Oracle',     1);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('C#',         3);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Java',       3);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Kotlin',     3);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('HTML',       4);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('CSS',        4);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('JavaScript', 4);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('JUnit',      5);
GO

-- Sub-subcategorías de JUnit (id = 14 si se insertaron en orden)
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Assertions', 14);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Test cases',  14);
GO
