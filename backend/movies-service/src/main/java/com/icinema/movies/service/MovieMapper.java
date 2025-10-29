package com.icinema.movies.service;

import com.icinema.common.dto.MovieDto;
import com.icinema.movies.domain.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {
    public MovieDto toDto(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieDto(
            movie.getId(),
            movie.getTitle(),
            movie.getGenre(),
            movie.getLanguage(),
            movie.getDurationMinutes(),
            movie.getRating(),
            movie.getCensorRating(),
            movie.getRatingCount(),
            movie.getPosterUrl(),
            movie.getSynopsis(),
            movie.getReleaseDate()
        );
    }

    public Movie toEntity(MovieDto dto) {
        if (dto == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setId(dto.id());
        movie.setTitle(dto.title());
        movie.setGenre(dto.genre());
        movie.setLanguage(dto.language());
        movie.setDurationMinutes(dto.durationMinutes());
        movie.setRating(dto.rating());
        movie.setCensorRating(dto.censorRating());
        movie.setRatingCount(dto.ratingCount());
        movie.setPosterUrl(dto.posterUrl());
        movie.setSynopsis(dto.synopsis());
        movie.setReleaseDate(dto.releaseDate());
        return movie;
    }
}
