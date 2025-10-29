import api from './client';
import type { ApiResponse, Seat, SeatHoldResponse } from '../types';

export async function fetchSeats(showId: number) {
  const response = await api.get<ApiResponse<Seat[]>>(`/api/seats/show/${showId}`);
  return response.data.data;
}

export async function holdSeats(payload: { showId: number; seatNumbers: string[]; holdSeconds?: number }) {
  const response = await api.post<ApiResponse<SeatHoldResponse>>('/api/seats/hold', {
    showId: payload.showId,
    seatNumbers: payload.seatNumbers,
    holdSeconds: payload.holdSeconds ?? 300,
  });
  return response.data.data;
}

export async function releaseHold(showId: number, holdToken: string) {
  await api.post('/api/seats/release', { showId, holdToken });
}
