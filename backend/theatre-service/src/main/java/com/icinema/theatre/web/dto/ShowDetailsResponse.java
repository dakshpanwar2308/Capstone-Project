package com.icinema.theatre.web.dto;

import com.icinema.common.dto.MovieDto;
import com.icinema.common.dto.ShowDto;
import com.icinema.common.dto.TheatreDto;

public record ShowDetailsResponse(
    ShowDto show,
    MovieDto movie,
    TheatreDto theatre
) {
}
