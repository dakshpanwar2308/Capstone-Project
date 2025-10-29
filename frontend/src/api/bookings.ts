import api from './client';
import type { ApiResponse, Booking, BookingPayload, SeatHoldResponse } from '../types';
import { holdSeats, releaseHold } from './seating';

export interface BookingRequestInput {
  showId: number;
  customerName: string;
  customerEmail: string;
  seatNumbers: string[];
  currency?: string;
  cardToken?: string;
}

export async function createBooking(input: BookingRequestInput) {
  const hold: SeatHoldResponse = await holdSeats({
    showId: input.showId,
    seatNumbers: input.seatNumbers,
  });

  const payload: BookingPayload = {
    showId: input.showId,
    seatNumbers: input.seatNumbers,
    customerName: input.customerName,
    customerEmail: input.customerEmail,
    currency: input.currency ?? 'USD',
    cardToken: input.cardToken ?? `tok_${Date.now()}`,
  };

  try {
    const response = await api.post<ApiResponse<Booking>>('/api/bookings', payload);
    return response.data.data;
  } catch (error) {
    await releaseHold(input.showId, hold.holdToken);
    throw error;
  }
}

export async function fetchBooking(id: number) {
  const response = await api.get<ApiResponse<Booking>>(`/api/bookings/${id}`);
  return response.data.data;
}
