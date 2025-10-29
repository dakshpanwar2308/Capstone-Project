import api from './client';
import type { ApiResponse, Movie } from '../types';

export async function fetchMovies(params?: { genre?: string; language?: string; rating?: number }) {
  const response = await api.get<ApiResponse<Movie[]>>('/api/movies', { params });
  return response.data.data;
}

export async function fetchMovie(id: number) {
  const response = await api.get<ApiResponse<Movie>>(`/api/movies/${id}`);
  return response.data.data;
}

export async function searchMovies(params: {
  query?: string;
  genre?: string;
  language?: string;
  rating?: number;
}) {
  const response = await api.get<ApiResponse<Movie[]>>('/api/movies/search', {
    params: {
      q: params.query,
      genre: params.genre,
      language: params.language,
      rating: params.rating,
    },
  });
  return response.data.data;
}

export async function fetchHighlights() {
  const response = await api.get<ApiResponse<Movie[]>>('/api/movies/highlights');
  return response.data.data;
}

export async function submitMovieRating(movieId: number, rating: number) {
  const response = await api.post<ApiResponse<Movie>>(`/api/movies/${movieId}/ratings`, { rating });
  return response.data.data;
}
