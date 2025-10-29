package com.icinema.booking.service;

import com.icinema.booking.client.PaymentClient;
import com.icinema.booking.client.SeatingClient;
import com.icinema.booking.client.dto.ConfirmHoldPayload;
import com.icinema.booking.client.dto.SeatHoldPayload;
import com.icinema.booking.client.dto.SeatHoldResult;
import com.icinema.booking.domain.Booking;
import com.icinema.booking.repository.BookingRepository;
import com.icinema.booking.web.dto.BookingRequest;
import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.BookingDto;
import com.icinema.common.dto.PaymentDto;
import com.icinema.common.dto.PaymentRequest;
import com.icinema.common.model.BookingStatus;
import com.icinema.common.model.PaymentStatus;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final SeatingClient seatingClient;
    private final PaymentClient paymentClient;

    public BookingService(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        SeatingClient seatingClient,
        PaymentClient paymentClient
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.seatingClient = seatingClient;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public BookingDto createBooking(BookingRequest request) {
        ApiResponse<SeatHoldResult> holdResponse = seatingClient.holdSeats(
            new SeatHoldPayload(request.showId(), request.seatNumbers(), 300)
        );
        if (holdResponse == null || !holdResponse.isSuccess() || holdResponse.getData() == null) {
            throw new IllegalStateException("Failed to reserve seats");
        }

        SeatHoldResult holdResult = holdResponse.getData();
        double totalAmount = holdResult.seats().stream()
            .mapToDouble(seat -> seat.price())
            .sum();

        Booking booking = new Booking();
        booking.setShowId(request.showId());
        booking.setCustomerName(request.customerName());
        booking.setCustomerEmail(request.customerEmail());
        booking.setSeatNumbers(request.seatNumbers());
        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus(PaymentStatus.INITIATED);
        booking.setTotalAmount(totalAmount);
        booking.setHoldToken(holdResult.holdToken());
        booking = bookingRepository.save(booking);

        PaymentDto paymentDto = processPayment(request, booking, totalAmount);

        if (paymentDto != null && paymentDto.status() == PaymentStatus.SUCCEEDED) {
            seatingClient.confirmHold(new ConfirmHoldPayload(request.showId(), holdResult.holdToken()));
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            log.warn("Payment failed for booking {}. Releasing seats.", booking.getId());
            seatingClient.releaseHold(new ConfirmHoldPayload(request.showId(), holdResult.holdToken()));
            booking.setStatus(BookingStatus.FAILED);
        }

        if (paymentDto != null) {
            booking.setPaymentStatus(paymentDto.status());
            booking.setPaymentReference(paymentDto.providerReference());
        } else {
            booking.setPaymentStatus(PaymentStatus.FAILED);
        }

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toDto(saved, paymentDto);
    }

    public BookingDto getBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        return bookingMapper.toDto(booking, null);
    }

    public List<BookingDto> bookingsForCustomer(String email) {
        return bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(email).stream()
            .map(booking -> bookingMapper.toDto(booking, null))
            .collect(Collectors.toList());
    }

    private PaymentDto processPayment(BookingRequest request, Booking booking, double totalAmount) {
        try {
            PaymentRequest paymentRequest = new PaymentRequest(
                booking.getId(),
                totalAmount,
                request.currency(),
                request.customerEmail(),
                request.cardToken()
            );
            ApiResponse<PaymentDto> paymentResponse = paymentClient.charge(paymentRequest);
            if (paymentResponse != null && paymentResponse.isSuccess()) {
                return paymentResponse.getData();
            }
        } catch (Exception ex) {
            log.error("Error while processing payment for booking {}: {}", booking.getId(), ex.getMessage());
        }
        return null;
    }
}
