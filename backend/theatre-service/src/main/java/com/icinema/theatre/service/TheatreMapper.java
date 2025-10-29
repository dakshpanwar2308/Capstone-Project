package com.icinema.theatre.service;

import com.icinema.common.dto.ShowDto;
import com.icinema.common.dto.TheatreDto;
import com.icinema.theatre.domain.Show;
import com.icinema.theatre.domain.Theatre;
import org.springframework.stereotype.Component;

@Component
public class TheatreMapper {

    public TheatreDto toDto(Theatre theatre) {
        if (theatre == null) {
            return null;
        }
        return new TheatreDto(
            theatre.getId(),
            theatre.getName(),
            theatre.getCity(),
            theatre.getAddress(),
            theatre.getContactNumber()
        );
    }

    public ShowDto toDto(Show show) {
        if (show == null) {
            return null;
        }
        return new ShowDto(
            show.getId(),
            show.getMovieId(),
            show.getTheatre().getId(),
            show.getStartTime(),
            show.getBasePrice(),
            show.getScreen()
        );
    }
}
