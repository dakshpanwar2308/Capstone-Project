package com.icinema.theatre.client;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "movies-service",
    url = "${clients.movies.url:http://localhost:8081}"
)
public interface MovieCatalogClient {

    @GetMapping("/api/movies/{id}")
    ApiResponse<MovieDto> getMovie(@PathVariable("id") Long id);
}
