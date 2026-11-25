# Simulacro: Biblioteca UFPS

**Integrantes:** Camilo Andrés Sánchez Martínez (1152329) · Omar Fernando Suárez Oviedo (1152216)

**Materia:** Desarrollo de aplicaciones basadas en microservicios, UFPS

Dos microservicios en Spring Boot, cada uno con su propia base de datos H2, comunicados por HTTP:

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| Libros | 8091 | `jdbc:h2:mem:libros` | CRUD de libros con validaciones |
| Préstamos | 8092 | `jdbc:h2:mem:prestamos` | Registra préstamos consultando a Libros con `RestClient` |

## Hasta dónde llegamos

- **Parte 1 (Libros):** pasos 1 a 3 completos.
- **Parte 2 (Préstamos):** pasos 4 a 6 completos.
- **Parte 3:** paso 9 (las 8 pruebas) completo. Pasos 7 y 8 (Dockerfile y `compose.yaml`): ver los archivos del repositorio.

## Cómo ejecutarlo

```bash
cd libros && ./mvnw spring-boot:run
cd prestamos && ./mvnw spring-boot:run
```

## Batería de pruebas (paso 9)

| # | Petición | Esperado | Obtenido |
|---|---|---|---|
| 1 | `GET /libros` | 200 con 3 libros | 200 |
| 2 | `GET /libros/99` | 404 | 404 |
| 3 | `POST /libros` sin título | 400 | 400 |
| 4 | `POST /prestamos` libro 1 | 201 | 201 |
| 5 | `POST /prestamos` libro 99 | 400 | 400 |
| 6 | `POST /prestamos` libro 3 (0 ejemplares) | 409 | 409 |
| 7 | `POST /prestamos` sin código de estudiante | 400 | 400 |
| 8 | `POST /prestamos` con Libros apagado | 503 | 503 |

### Pruebas 1 a 3: Libros
![Pruebas de Libros](imagenes/pruebas-libros.png)

### Pruebas 4 a 7: Préstamos
![Pruebas de Préstamos](imagenes/pruebas-prestamos.png)

### Prueba 8: Libros apagado
![Prueba 503](imagenes/prueba-503.png)
