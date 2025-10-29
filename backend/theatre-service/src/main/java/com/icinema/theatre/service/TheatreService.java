package com.icinema.theatre.service;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.MovieDto;
import com.icinema.common.dto.ShowDto;
import com.icinema.common.dto.TheatreDto;
import com.icinema.theatre.client.MovieCatalogClient;
import com.icinema.theatre.domain.Show;
import com.icinema.theatre.domain.Theatre;
import com.icinema.theatre.repository.ShowRepository;
import com.icinema.theatre.repository.TheatreRepository;
import com.icinema.theatre.web.dto.ShowDetailsResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TheatreService {

    private static final Logger log = LoggerFactory.getLogger(TheatreService.class);

    private final TheatreRepository theatreRepository;
    private final ShowRepository showRepository;
    private final TheatreMapper theatreMapper;
    private final MovieCatalogClient movieCatalogClient;

    public TheatreService(
        TheatreRepository theatreRepository,
        ShowRepository showRepository,
        TheatreMapper theatreMapper,
        MovieCatalogClient movieCatalogClient
    ) {
        this.theatreRepository = theatreRepository;
        this.showRepository = showRepository;
        this.theatreMapper = theatreMapper;
        this.movieCatalogClient = movieCatalogClient;
    }

    public List<TheatreDto> fetchTheatres(String city) {
        List<Theatre> theatres = city == null || city.isBlank()
            ? theatreRepository.findAll()
            : theatreRepository.findByCityIgnoreCase(city);
        return theatres.stream()
            .map(theatreMapper::toDto)
            .collect(Collectors.toList());
    }

    public TheatreDto getTheatre(Long id) {
        return theatreRepository.findById(id)
            .map(theatreMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Theatre not found: " + id));
    }

    @Transactional
    public TheatreDto createTheatre(TheatreDto dto) {
        Theatre theatre = new Theatre();
        theatre.setName(dto.name());
        theatre.setCity(dto.city());
        theatre.setAddress(dto.address());
        theatre.setContactNumber(dto.contactNumber());
        Theatre saved = theatreRepository.save(theatre);
        return theatreMapper.toDto(saved);
    }

    @Transactional
    public ShowDto createShow(Long theatreId, ShowDto dto) {
        Theatre theatre = theatreRepository.findById(theatreId)
            .orElseThrow(() -> new IllegalArgumentException("Theatre not found: " + theatreId));
        Show show = new Show();
        show.setTheatre(theatre);
        show.setMovieId(dto.movieId());
        show.setStartTime(dto.startTime());
        show.setScreen(dto.screen() == null ? "Screen 1" : dto.screen());
        double price = dto.basePrice() <= 0 ? 250.0 : dto.basePrice();
        show.setBasePrice(price);
        Show saved = showRepository.save(show);
        return theatreMapper.toDto(saved);
    }

    public List<ShowDto> showsByTheatre(Long theatreId) {
        return showRepository.findByTheatreId(theatreId).stream()
            .map(theatreMapper::toDto)
            .collect(Collectors.toList());
    }

    public List<ShowDto> showsByMovie(Long movieId) {
        return showRepository.findByMovieId(movieId).stream()
            .map(theatreMapper::toDto)
            .collect(Collectors.toList());
    }

    public ShowDetailsResponse showDetails(Long showId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        MovieDto movieDto = fetchMovie(show.getMovieId());
        TheatreDto theatreDto = theatreMapper.toDto(show.getTheatre());
        return new ShowDetailsResponse(theatreMapper.toDto(show), movieDto, theatreDto);
    }

    private MovieDto fetchMovie(Long movieId) {
        try {
            ApiResponse<MovieDto> response = movieCatalogClient.getMovie(movieId);
            if (response != null && response.isSuccess()) {
                return response.getData();
            }
        } catch (Exception ex) {
            log.warn("Failed to fetch movie {} details: {}", movieId, ex.getMessage());
        }
        return null;
    }
}
