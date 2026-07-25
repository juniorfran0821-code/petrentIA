WITH arr AS (
    SELECT
        ARRAY['Ana','Carlos','María','Jean','Nereyda','Lady','Elian','Sofía','Diego',
              'Valentina','Andrés','Camila','Mateo','Isabella','Sebastián','Daniela',
              'Nicolás','Gabriela','Emilio','Paula'] AS nombres,
        ARRAY['Pérez','Gómez','Cedeño','Vásquez','Zambrano','Pita','Rodríguez','Torres',
              'Vera','Suárez','Álvarez','Chávez','Ortiz','Salazar','Moreno'] AS apellidos
)
INSERT INTO usuarios (nombre, correo, password, rol, activo, fecha_creacion)
SELECT
    (arr.nombres)[(floor(random()*array_length(arr.nombres,1))+1)::int] || ' ' ||
    (arr.apellidos)[(floor(random()*array_length(arr.apellidos,1))+1)::int],
    'usuario' || g.i || '@petrent.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    CASE WHEN g.i <= 2 THEN 'admin' ELSE 'usuario' END,
    TRUE,
    NOW() - (floor(random()*365) || ' days')::interval
FROM generate_series(1,100) AS g(i), arr;

WITH arr AS (
    SELECT
        ARRAY['Max','Luna','Coco','Bella','Rocky','Mia','Toby','Nala','Zeus','Lola',
              'Simba','Kira','Thor','Nina','Bruno','Sasha','Duke','Maya','Rex','Chloe'] AS nombres,
        ARRAY['Golden Retriever','Labrador','Beagle','Poodle Toy','Bulldog Francés',
              'Shih Tzu','Pastor Alemán','Chihuahua','Husky Siberiano','Schnauzer',
              'Border Collie','Cocker Spaniel'] AS razas,
        ARRAY['Pequeño','Mediano','Grande'] AS tamanos,
        ARRAY['Energético,Juguetón,Amigable','Tranquilo,Obediente,Cariñoso',
              'Curioso,Explorador,Alegre','Inteligente,Elegante,Dócil',
              'Sociable,Divertido,Relajado','Tierno,Calmado,Compañero'] AS etiquetas
)
INSERT INTO perros (id_propietario, nombre, raza, tamano, precio, descripcion, etiquetas, imagen, disponible, activo, calificacion, resenas_totales, fecha_creacion)
SELECT
    (floor(random()*100)+1)::bigint,
    (arr.nombres)[(floor(random()*array_length(arr.nombres,1))+1)::int],
    (arr.razas)[(floor(random()*array_length(arr.razas,1))+1)::int],
    (arr.tamanos)[(floor(random()*array_length(arr.tamanos,1))+1)::int],
    ROUND((random()*8 + 4)::numeric, 2),
    'Perro cariñoso y bien cuidado, ideal para paseos y compañía.',
    (arr.etiquetas)[(floor(random()*array_length(arr.etiquetas,1))+1)::int],
    (ARRAY['/max.jpg','/luna.jpg','/rocky.jpg','/bella.jpg','/coco.jpg','/mia.jpg'])[(g.i % 6) + 1],
    random() < 0.85,
    TRUE,
    0.0,
    0,
    NOW() - (floor(random()*500) || ' days')::interval
FROM generate_series(1,1000) AS g(i), arr;

WITH datos AS (
    SELECT
        g AS i,
        (floor(random()*100)+1)::bigint  AS id_usuario,
        (floor(random()*1000)+1)::bigint AS id_perro,
        (floor(random()*8)+1)::int       AS horas,
        NOW() - (floor(random()*730) || ' days')::interval AS fecha_inicio
    FROM generate_series(1,10000) AS g
)
INSERT INTO reservas (id_usuario, id_perro, fecha_inicio, fecha_fin, horas, precio_total, estado, fecha_creacion)
SELECT
    d.id_usuario, d.id_perro, d.fecha_inicio,
    d.fecha_inicio + (d.horas || ' hours')::interval,
    d.horas,
    ROUND(d.horas * p.precio, 2),
    (ARRAY['CONFIRMADA','COMPLETADA','COMPLETADA','COMPLETADA','CANCELADA'])[floor(random()*5)+1],
    d.fecha_inicio
FROM datos d
JOIN perros p ON p.id = d.id_perro;

INSERT INTO pagos (id_reserva, monto, metodo_pago, estado_pago, fecha_pago)
SELECT
    r.id, r.precio_total,
    (ARRAY['TARJETA','EFECTIVO','TRANSFERENCIA','PAYPAL'])[floor(random()*4)+1],
    CASE WHEN r.estado = 'COMPLETADA' THEN 'PAGADO' ELSE 'PENDIENTE' END,
    r.fecha_creacion + INTERVAL '10 minutes'
FROM reservas r
WHERE r.estado <> 'CANCELADA';

INSERT INTO resenas (id_reserva, calificacion, comentario, activo, fecha_creacion)
SELECT
    r.id,
    (floor(random()*5)+1)::int,
    (ARRAY['Excelente experiencia, el perro fue muy dócil.',
           'Muy buen servicio, seguro repito.',
           'El perro estaba feliz y bien cuidado.',
           'Todo tal como se describía en el anuncio.',
           'Buena atención, aunque podría mejorar la puntualidad.'])[floor(random()*5)+1],
    TRUE,
    r.fecha_creacion + INTERVAL '2 hours'
FROM reservas r
WHERE r.estado = 'COMPLETADA' AND random() < 0.7;

UPDATE perros
SET calificacion = sub.promedio, resenas_totales = sub.total
FROM (
    SELECT re.id_reserva, r.id_perro, ROUND(AVG(re.calificacion) OVER (PARTITION BY r.id_perro), 1) AS promedio,
           COUNT(*) OVER (PARTITION BY r.id_perro) AS total
    FROM resenas re
    JOIN reservas r ON r.id = re.id_reserva
    WHERE re.activo = TRUE
) sub
WHERE perros.id = sub.id_perro;

UPDATE perros SET activo = FALSE, disponible = FALSE
WHERE id IN (SELECT id FROM perros ORDER BY id LIMIT 5);

UPDATE usuarios SET activo = FALSE
WHERE id IN (SELECT id FROM usuarios WHERE rol = 'usuario' ORDER BY id DESC LIMIT 3);
