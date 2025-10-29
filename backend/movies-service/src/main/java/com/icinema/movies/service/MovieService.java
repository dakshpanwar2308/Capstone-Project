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
        Specification<Movie> spec = Specification.where(null);
        if (genre != null) {
            spec = spec.and(MovieSpecifications.genreEquals(genre));
        }
        if (language != null) {
            spec = spec.and(MovieSpecifications.languageEquals(language));
        }
        if (rating != null) {
            spec = spec.and(MovieSpecifications.minRating(rating));
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
        Specification<Movie> spec = MovieSpecifications.titleContains(query);
        if (genre != null) {
            spec = spec.and(MovieSpecifications.genreEquals(genre));
        }
        if (language != null) {
            spec = spec.and(MovieSpecifications.languageEquals(language));
        }
        if (rating != null) {
            spec = spec.and(MovieSpecifications.minRating(rating));
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
        existing.setPosterUrl(dto.posterUrl());
        existing.setSynopsis(dto.synopsis());
        existing.setReleaseDate(dto.releaseDate());
        return movieMapper.toDto(existing);
    }

    @Transactional
    public void delete(Long id) {
        movieRepository.deleteById(id);
    }
}
