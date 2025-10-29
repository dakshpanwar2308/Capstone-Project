package com.icinema.movies.service;

import com.icinema.movies.domain.Movie;
import org.springframework.data.jpa.domain.Specification;

public final class MovieSpecifications {

    private MovieSpecifications() {
    }

    public static Specification<Movie> genreEquals(String genre) {
        return (root, query, cb) ->
            genre == null || genre.isBlank() ? null : cb.equal(cb.lower(root.get("genre")), genre.toLowerCase());
    }

    public static Specification<Movie> languageEquals(String language) {
        return (root, query, cb) ->
            language == null || language.isBlank() ? null : cb.equal(cb.lower(root.get("language")), language.toLowerCase());
    }

    public static Specification<Movie> minRating(double minRating) {
        return (root, query, cb) -> minRating <= 0 ? null : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Movie> titleContains(String queryText) {
        return (root, query, cb) ->
            queryText == null || queryText.isBlank()
                ? null
                : cb.like(cb.lower(root.get("title")), "%" + queryText.toLowerCase() + "%");
    }
}
