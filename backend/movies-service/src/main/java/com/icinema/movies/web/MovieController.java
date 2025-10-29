package com.icinema.movies.web;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.MovieDto;
import com.icinema.movies.service.MovieService;
import com.icinema.movies.web.dto.RatingRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieDto>>> listMovies(
        @RequestParam(required = false) String genre,
        @RequestParam(required = false) String language,
        @RequestParam(required = false) Double rating
    ) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.fetchMovies(genre, language, rating)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDto>> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MovieDto>>> searchMovies(
        @RequestParam(required = false, name = "q") String query,
        @RequestParam(required = false) String genre,
        @RequestParam(required = false) String language,
        @RequestParam(required = false) Double rating
    ) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.search(query, genre, language, rating)));
    }

    @GetMapping("/highlights")
    public ResponseEntity<ApiResponse<List<MovieDto>>> highlights() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.highlights()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieDto>> create(@Valid @RequestBody MovieDto movieDto) {
        var saved = movieService.save(movieDto);
        return ResponseEntity.ok(ApiResponse.created(saved));
    }

    @PostMapping("/{id}/ratings")
    public ResponseEntity<ApiResponse<MovieDto>> rateMovie(
        @PathVariable Long id,
        @Valid @RequestBody RatingRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.addRating(id, request.rating())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDto>> update(
        @PathVariable Long id,
        @Valid @RequestBody MovieDto movieDto
    ) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.update(id, movieDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
