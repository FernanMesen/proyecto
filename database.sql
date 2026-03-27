
CREATE DATABASE bolsa_empleo;


CREATE TABLE usuario (
                         id BIGSERIAL PRIMARY KEY,
                         correo VARCHAR(150) NOT NULL UNIQUE,
                         clave VARCHAR(255) NOT NULL,
                         rol VARCHAR(20) NOT NULL CHECK (rol IN ('EMPRESA','OFERENTE','ADMIN')),
                         activo BOOLEAN NOT NULL DEFAULT FALSE,
                         fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE empresa (
                         id BIGSERIAL PRIMARY KEY,
                         usuario_id BIGINT NOT NULL UNIQUE,
                         nombre VARCHAR(200) NOT NULL,
                         localizacion VARCHAR(200),
                         telefono VARCHAR(20),
                         descripcion TEXT,
                         CONSTRAINT fk_empresa_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE oferente (
                          id BIGSERIAL PRIMARY KEY,
                          usuario_id BIGINT NOT NULL UNIQUE,
                          identificacion VARCHAR(30) NOT NULL,
                          nombre VARCHAR(100) NOT NULL,
                          primer_apellido VARCHAR(100),
                          nacionalidad VARCHAR(80),
                          telefono VARCHAR(20),
                          residencia VARCHAR(200),
                          cv_path VARCHAR(300),
                          CONSTRAINT fk_oferente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE caracteristica (
                                id BIGSERIAL PRIMARY KEY,
                                nombre VARCHAR(150) NOT NULL,
                                padre_id BIGINT,
                                CONSTRAINT fk_caracteristica_padre FOREIGN KEY (padre_id) REFERENCES caracteristica(id)
);

CREATE TABLE habilidad (
                           id BIGSERIAL PRIMARY KEY,
                           oferente_id BIGINT NOT NULL,
                           caracteristica_id BIGINT NOT NULL,
                           nivel INT NOT NULL CHECK (nivel BETWEEN 1 AND 5),
                           CONSTRAINT uk_habilidad UNIQUE (oferente_id, caracteristica_id),
                           CONSTRAINT fk_hab_oferente FOREIGN KEY (oferente_id) REFERENCES oferente(id),
                           CONSTRAINT fk_hab_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
);

CREATE TABLE puesto (
                        id BIGSERIAL PRIMARY KEY,
                        empresa_id BIGINT NOT NULL,
                        descripcion VARCHAR(300) NOT NULL,
                        salario DECIMAL(10,2),
                        tipo VARCHAR(10) NOT NULL DEFAULT 'PUBLICO' CHECK (tipo IN ('PUBLICO','PRIVADO')),
                        activo BOOLEAN NOT NULL DEFAULT TRUE,
                        fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_puesto_empresa FOREIGN KEY (empresa_id) REFERENCES empresa(id)
);

CREATE TABLE puesto_caracteristica (
                                       id BIGSERIAL PRIMARY KEY,
                                       puesto_id BIGINT NOT NULL,
                                       caracteristica_id BIGINT NOT NULL,
                                       nivel_minimo INT NOT NULL CHECK (nivel_minimo BETWEEN 1 AND 5),
                                       CONSTRAINT uk_puesto_car UNIQUE (puesto_id, caracteristica_id),
                                       CONSTRAINT fk_pc_puesto FOREIGN KEY (puesto_id) REFERENCES puesto(id),
                                       CONSTRAINT fk_pc_caracteristica FOREIGN KEY (caracteristica_id) REFERENCES caracteristica(id)
);



INSERT INTO usuario (correo, clave, rol, activo)
VALUES (
           'admin@bolsaempleo.local',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LPZdIFiRnUu',
           'ADMIN',
           TRUE
       );

INSERT INTO caracteristica (nombre, padre_id) VALUES ('Bases de Datos', NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Ciberseguridad', NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Lenguajes de programacion', NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Tecnologias Web', NULL);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Testing', NULL);

INSERT INTO caracteristica (nombre, padre_id) VALUES ('MySql', 1);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Oracle', 1);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('C#', 3);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Java', 3);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Kotlin', 3);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('HTML', 4);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('CSS', 4);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('JavaScript', 4);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('JUnit', 5);

INSERT INTO caracteristica (nombre, padre_id) VALUES ('Assertions', 14);
INSERT INTO caracteristica (nombre, padre_id) VALUES ('Test cases', 14);