import api from './client';
import type { ApiResponse, Booking, BookingPayload } from '../types';

export interface BookingRequestInput {
  showId: number;
  customerName: string;
  customerEmail: string;
  seatNumbers: string[];
  currency?: string;
  cardType: 'CREDIT' | 'DEBIT';
  cardNumber: string;
  expiry: string;
  cvv: string;
}

export async function createBooking(input: BookingRequestInput) {
  const payload: BookingPayload = {
    showId: input.showId,
    seatNumbers: input.seatNumbers,
    customerName: input.customerName,
    customerEmail: input.customerEmail,
    currency: input.currency ?? 'USD',
    cardType: input.cardType,
    cardNumber: input.cardNumber,
    expiry: input.expiry,
    cvv: input.cvv,
  };

  const response = await api.post<ApiResponse<Booking>>('/api/bookings', payload);
  return response.data.data;
}

export async function fetchBooking(id: number) {
  const response = await api.get<ApiResponse<Booking>>(`/api/bookings/${id}`);
  return response.data.data;
}
