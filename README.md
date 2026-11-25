# Simulacro: Biblioteca UFPS

**Integrantes:** Camilo Andrés Sánchez Martínez (1152329) · Omar Fernando Suárez Oviedo (1152216)

**Materia:** Desarrollo de aplicaciones basadas en microservicios, UFPS

Dos microservicios en Spring Boot, cada uno con su propia base de datos H2: **Libros** (puerto 8091) y **Préstamos** (puerto 8092), comunicados por HTTP.

## Evidencias

### Pruebas de Libros
![Pruebas de Libros](imagenes/pruebas-libros.png)

### Pruebas de Préstamos
![Pruebas de Préstamos](imagenes/pruebas-prestamos.png)

### Libros apagado: Préstamos responde 503
![Prueba 503](imagenes/prueba-503.png)

## Preguntas de la defensa

### 1. ¿Por qué Préstamos tiene su propio record del libro en lugar de usar la entidad Libro?

Porque en microservicios cada servicio es independiente. Si Préstamos usara directamente la entidad Libro del otro servicio, estarían acoplados: cualquier cambio en Libros rompería Préstamos. Con un LibroDTO propio, Préstamos solo modela los datos que a él le interesan y si Libros cambia internamente, a Préstamos no le afecta.

### 2. ¿Por qué el préstamo guarda una copia del título si Libros ya lo tiene?

Porque si no lo guardamos, cada vez que queramos mostrar un préstamo tendríamos que llamar al servicio de Libros para traer el título, y si Libros está caído no podríamos ni listar los préstamos. Guardando una copia del título al momento de crear el préstamo, Préstamos puede funcionar de forma autónoma sin depender de que Libros esté disponible para consultas posteriores.

### 3. ¿Por qué el libro inexistente responde 400 en Préstamos y no 404?

Porque desde el punto de vista de Préstamos, el recurso que el cliente está intentando crear es un **préstamo**, no un libro. El cliente mandó un libroId inválido en su solicitud, o sea que los datos de entrada están mal, y eso es un error del cliente: 400 Bad Request. El 404 lo devuelve Libros internamente cuando Préstamos le consulta, pero Préstamos lo intercepta y lo traduce a un 400 con el mensaje "El libro 99 no existe" para que tenga sentido en su contexto.

### 4. ¿Qué cambia en la dirección de Libros cuando todo corre en Docker? ¿Por qué no hubo que tocar Java?

En local, libros.url apunta a http://localhost:8091. En Docker, cambia a http://libros:8091 porque los contenedores se encuentran entre sí por el nombre del servicio definido en el compose.yaml, gracias al DNS interno de Docker. Spring Boot automáticamente mapea la variable de entorno LIBROS_URL a la propiedad libros.url gracias al Relaxed Binding, así que sobreescribe el valor sin modificar ni una línea de código.

### 5. Préstamos no descuenta ejemplares. ¿Qué servicio debería hacerlo y qué pasa si falla justo después de guardar el préstamo?

El descuento lo debería hacer Libros, porque los ejemplares son datos que le pertenecen a él. Préstamos tendría que hacer un PUT o PATCH a Libros para pedirle que descuente un ejemplar después de guardar el préstamo. Si la llamada falla justo después de guardar el préstamo, quedaría una inconsistencia: el préstamo existe en la base de datos pero los ejemplares no se descontaron. Para manejar eso se podría implementar un patrón de compensación (como Saga), donde si falla el descuento se cancela o marca como fallido el préstamo, o se reintenta la operación.
