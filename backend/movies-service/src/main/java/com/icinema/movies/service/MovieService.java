package com.icinema.movies.service;

import com.icinema.common.dto.MovieDto;
import com.icinema.movies.domain.Movie;
import com.icinema.movies.repository.MovieRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
    }

    @Transactional
    public MovieDto save(MovieDto movieDto) {
        Movie movie = movieMapper.toEntity(movieDto);
        Movie saved = movieRepository.save(movie);
        return movieMapper.toDto(saved);
    }

    public List<MovieDto> fetchMovies(String genre, String language, Double rating) {
        Specification<Movie> spec = null;
        String normalizedGenre = normalizeEnumFilter(genre);
        String normalizedLanguage = normalizeEnumFilter(language);
        Double normalizedRating = normalizeRatingFilter(rating);

        spec = and(spec, MovieSpecifications.genreEquals(normalizedGenre));
        spec = and(spec, MovieSpecifications.languageEquals(normalizedLanguage));
        if (normalizedRating != null) {
            spec = and(spec, MovieSpecifications.minRating(normalizedRating));
        }

        return movieRepository.findAll(spec).stream()
            .map(movieMapper::toDto)
            .collect(Collectors.toList());
    }

    public MovieDto getById(Long id) {
        return movieRepository.findById(id)
            .map(movieMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + id));
    }

    public List<MovieDto> search(String query, String genre, String language, Double rating) {
        Specification<Movie> spec = null;
        String normalizedGenre = normalizeEnumFilter(genre);
        String normalizedLanguage = normalizeEnumFilter(language);
        Double normalizedRating = normalizeRatingFilter(rating);

        if (query != null && !query.isBlank()) {
            spec = and(spec, MovieSpecifications.titleContains(query));
        }
        spec = and(spec, MovieSpecifications.genreEquals(normalizedGenre));
        spec = and(spec, MovieSpecifications.languageEquals(normalizedLanguage));
        if (normalizedRating != null) {
            spec = and(spec, MovieSpecifications.minRating(normalizedRating));
        }

        return movieRepository.findAll(spec).stream()
            .map(movieMapper::toDto)
            .collect(Collectors.toList());
    }

    public List<MovieDto> highlights() {
        return movieRepository.findTop10ByOrderByRatingDesc().stream()
            .map(movieMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public MovieDto update(Long id, MovieDto dto) {
        Movie existing = movieRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + id));
        existing.setTitle(dto.title());
        existing.setGenre(dto.genre());
        existing.setLanguage(dto.language());
        existing.setDurationMinutes(dto.durationMinutes());
        existing.setRating(dto.rating());
        existing.setCensorRating(dto.censorRating());
        existing.setRatingCount(dto.ratingCount());
        existing.setPosterUrl(dto.posterUrl());
        existing.setSynopsis(dto.synopsis());
        existing.setReleaseDate(dto.releaseDate());
        return movieMapper.toDto(existing);
    }

    @Transactional
    public MovieDto addRating(Long id, double rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 stars.");
        }
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Movie not found: " + id));

        long currentCount = movie.getRatingCount();
        double totalScore = movie.getRating() * currentCount;
        long updatedCount = currentCount + 1;
        double newAverage = (totalScore + rating) / updatedCount;

        movie.setRating(Math.round(newAverage * 10.0) / 10.0);
        movie.setRatingCount(updatedCount);
        return movieMapper.toDto(movie);
    }

    @Transactional
    public void delete(Long id) {
        movieRepository.deleteById(id);
    }

    private Specification<Movie> and(Specification<Movie> base, Specification<Movie> addition) {
        if (addition == null) {
            return base;
        }
        return base == null ? addition : base.and(addition);
    }

    private String normalizeEnumFilter(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "all".equalsIgnoreCase(trimmed)) {
            return null;
        }
        return trimmed;
    }

    private Double normalizeRatingFilter(Double value) {
        if (value == null || value <= 0) {
            return null;
        }
        return value;
    }
}
