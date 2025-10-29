package com.icinema.theatre.web;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.ShowDto;
import com.icinema.common.dto.TheatreDto;
import com.icinema.theatre.service.TheatreService;
import com.icinema.theatre.web.dto.ShowDetailsResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheatreDto>>> list(@RequestParam(required = false) String city) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.fetchTheatres(city)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheatreDto>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.getTheatre(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TheatreDto>> create(@Valid @RequestBody TheatreDto dto) {
        return ResponseEntity.ok(ApiResponse.created(theatreService.createTheatre(dto)));
    }

    @GetMapping("/{id}/shows")
    public ResponseEntity<ApiResponse<List<ShowDto>>> shows(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.showsByTheatre(id)));
    }

    @GetMapping("/movies/{movieId}/shows")
    public ResponseEntity<ApiResponse<List<ShowDto>>> showsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.showsByMovie(movieId)));
    }

    @PostMapping("/{id}/shows")
    public ResponseEntity<ApiResponse<ShowDto>> createShow(
        @PathVariable Long id,
        @Valid @RequestBody ShowDto dto
    ) {
        return ResponseEntity.ok(ApiResponse.created(theatreService.createShow(id, dto)));
    }

    @GetMapping("/shows/{showId}")
    public ResponseEntity<ApiResponse<ShowDetailsResponse>> showDetails(@PathVariable Long showId) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.showDetails(showId)));
    }
}
