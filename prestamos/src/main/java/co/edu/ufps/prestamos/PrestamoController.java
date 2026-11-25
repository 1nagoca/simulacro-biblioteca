package co.edu.ufps.prestamos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    private final PrestamoRepository repo;
    private final LibrosClient libros;

    public PrestamoController(PrestamoRepository repo, LibrosClient libros) {
        this.repo = repo;
        this.libros = libros;
    }

    @GetMapping
    public List<Prestamo> listar() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prestamo crear(@Valid @RequestBody NuevoPrestamo pedido) {
        LibroDTO libro;
        try {
            libro = libros.obtenerLibro(pedido.libroId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El libro " + pedido.libroId() + " no existe");
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "El servicio de libros no responde");
        }

        if (libro.ejemplaresDisponibles() == null || libro.ejemplaresDisponibles() < 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay ejemplares disponibles");
        }

        Prestamo p = new Prestamo();
        p.setLibroId(libro.id());
        p.setTituloLibro(libro.titulo());
        p.setCodigoEstudiante(pedido.codigoEstudiante());
        p.setFechaPrestamo(LocalDate.now());
        p.setFechaDevolucion(LocalDate.now().plusDays(15));
        p.setEstado("ACTIVO");
        return repo.save(p);
    }
}