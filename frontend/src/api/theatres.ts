import api from './client';
import type { ApiResponse, Show, ShowDetailsResponse, Theatre } from '../types';

export async function fetchTheatres(city?: string) {
  const response = await api.get<ApiResponse<Theatre[]>>('/api/theatres', { params: { city } });
  return response.data.data;
}

export async function fetchShowsByTheatre(theatreId: number) {
  const response = await api.get<ApiResponse<Show[]>>(`/api/theatres/${theatreId}/shows`);
  return response.data.data;
}

export async function fetchShowsByMovie(movieId: number) {
  const response = await api.get<ApiResponse<Show[]>>(`/api/theatres/movies/${movieId}/shows`);
  return response.data.data;
}

export async function fetchShowDetails(showId: number) {
  const response = await api.get<ApiResponse<ShowDetailsResponse>>(`/api/theatres/shows/${showId}`);
  return response.data.data;
}
