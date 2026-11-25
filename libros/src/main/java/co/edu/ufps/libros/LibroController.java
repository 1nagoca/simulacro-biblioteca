package co.edu.ufps.libros;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/libros")
public class LibroController {

    private final LibroRepository repo;

    public LibroController(LibroRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Libro> listar() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Libro obtener(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro " + id + " no encontrado"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Libro crear(@Valid @RequestBody Libro libro) {
        libro.setId(null);
        return repo.save(libro);
    }

    @PutMapping("/{id}")
    public Libro actualizar(@PathVariable Long id, @Valid @RequestBody Libro datos) {
        Libro libro = obtener(id);
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setEjemplaresDisponibles(datos.getEjemplaresDisponibles());
        return repo.save(libro);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro " + id + " no encontrado");
        }
        repo.deleteById(id);
    }
}