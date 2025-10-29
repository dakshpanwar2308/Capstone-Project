export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

export interface Movie {
  id: number;
  title: string;
  genre: string;
  language: string;
  durationMinutes: number;
  rating: number;
  posterUrl?: string;
  synopsis?: string;
  releaseDate: string;
}

export interface Theatre {
  id: number;
  name: string;
  city: string;
  address?: string;
  contactNumber?: string;
}

export interface Show {
  id: number;
  movieId: number;
  theatreId: number;
  startTime: string;
  basePrice: number;
  screen?: string;
}

export type SeatStatus = 'AVAILABLE' | 'RESERVED' | 'BOOKED' | 'HELD';

export interface Seat {
  id: number;
  showId: number;
  seatNumber: string;
  status: SeatStatus;
  price: number;
}

export interface ShowDetailsResponse {
  show: Show;
  movie: Movie | null;
  theatre: Theatre;
}

export interface BookingPayload {
  showId: number;
  seatNumbers: string[];
  customerName: string;
  customerEmail: string;
  currency: string;
  cardToken: string;
}

export interface Booking {
  id: number;
  showId: number;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  status: string;
  seatNumbers: string[];
  createdAt: string;
}

export interface Payment {
  status: string;
  providerReference?: string;
  amount: number;
}

export interface BookingResult {
  booking: Booking;
  payment?: Payment;
}

export interface SeatHoldResponse {
  holdToken: string;
  expiresAt: string;
  seats: Seat[];
}
