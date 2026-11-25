package co.edu.ufps.prestamos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class LibrosClient {

    private final RestClient http;

    public LibrosClient(@Value("${libros.url}") String librosUrl) {
        this.http = RestClient.create(librosUrl);
    }

    public LibroDTO obtenerLibro(Long id) {
        return http.get()
                .uri("/libros/{id}", id)
                .retrieve()
                .body(LibroDTO.class);
    }
}