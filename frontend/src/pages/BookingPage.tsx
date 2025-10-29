import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { useParams } from 'react-router-dom'
import { fetchShowDetails } from '../api/theatres'
import { fetchSeats } from '../api/seating'
import { createBooking } from '../api/bookings'
import type { Booking, Seat, ShowDetailsResponse } from '../types'
import { SeatSelector } from '../components/SeatSelector'

const BookingPage = () => {
  const { showId } = useParams<{ showId: string }>()
  const [details, setDetails] = useState<ShowDetailsResponse | null>(null)
  const [seats, setSeats] = useState<Seat[]>([])
  const [selectedSeats, setSelectedSeats] = useState<string[]>([])
  const [customerName, setCustomerName] = useState('')
  const [customerEmail, setCustomerEmail] = useState('')
  const [cardNumber, setCardNumber] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [booking, setBooking] = useState<Booking | null>(null)

  useEffect(() => {
    if (!showId) return

    const loadData = async () => {
      try {
        setLoading(true)
        setError(null)
        const [showDetails, seatList] = await Promise.all([
          fetchShowDetails(Number(showId)),
          fetchSeats(Number(showId)),
        ])
        setDetails(showDetails)
        setSeats(seatList)
      } catch (err) {
        setError('Unable to load seat map right now.')
      } finally {
        setLoading(false)
      }
    }

    loadData()
  }, [showId])

  const totalAmount = useMemo(() => {
    const selected = seats.filter((seat) => selectedSeats.includes(seat.seatNumber))
    return selected.reduce((acc, seat) => acc + seat.price, 0)
  }, [seats, selectedSeats])

  const handleSeatToggle = (seatNumber: string) => {
    setSelectedSeats((prev) =>
      prev.includes(seatNumber) ? prev.filter((seat) => seat !== seatNumber) : [...prev, seatNumber],
    )
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    if (!showId) return
    if (!selectedSeats.length) {
      setError('Please select at least one seat.')
      return
    }
    if (!customerName || !customerEmail) {
      setError('Please provide your name and email.')
      return
    }

    try {
      setSubmitting(true)
      setError(null)
      const bookingResponse = await createBooking({
        showId: Number(showId),
        seatNumbers: selectedSeats,
        customerName,
        customerEmail,
        currency: 'USD',
        cardToken: cardNumber ? `tok_${cardNumber}` : undefined,
      })
      setBooking(bookingResponse)
      setSelectedSeats([])
      setCardNumber('')
      const updatedSeats = await fetchSeats(Number(showId))
      setSeats(updatedSeats)
    } catch (err) {
      setError('We could not complete your booking. Please try again or use a different card.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <div className="content-card">Loading booking experience...</div>
  }

  if (error && !details) {
    return <div className="error-card content-card">{error}</div>
  }

  if (!details || !showId) {
    return <div className="error-card content-card">Show not found.</div>
  }

  return (
    <div className="booking-page">
      <div className="booking-layout">
        <section className="content-card booking-summary">
          <h2 className="section-title">Booking summary</h2>
          <div className="summary-block">
            <h3>{details.movie?.title}</h3>
            <p className="synopsis">{details.movie?.synopsis}</p>
            <div className="summary-meta">
              <span>
                Theatre: <strong>{details.theatre.name}</strong>
              </span>
              <span>{details.theatre.address}</span>
              <span>{new Date(details.show.startTime).toLocaleString()}</span>
              <span>Screen: {details.show.screen}</span>
            </div>
          </div>

          <div className="summary-pricing">
            <h4>Selected seats</h4>
            {selectedSeats.length ? (
              <ul>
                {selectedSeats.map((seat) => (
                  <li key={seat}>{seat}</li>
                ))}
              </ul>
            ) : (
              <p className="empty-state">No seats selected yet.</p>
            )}
            <div className="total">
              <span>Total</span>
              <strong>${totalAmount.toFixed(2)}</strong>
            </div>
          </div>

          {booking && (
            <div className="success-card">
              <h4>Booking confirmed!</h4>
              <p>Confirmation #: {booking.id}</p>
              <p>Seats: {booking.seatNumbers.join(', ')}</p>
            </div>
          )}
        </section>

        <section className="content-card booking-actions">
          <h2 className="section-title">Select your seats</h2>
          <SeatSelector seats={seats} selectedSeats={selectedSeats} onToggle={handleSeatToggle} />

          <form className="booking-form" onSubmit={handleSubmit}>
            <h3>Passenger details</h3>
            <div className="form-grid">
              <label>
                Full name
                <input
                  value={customerName}
                  onChange={(e) => setCustomerName(e.target.value)}
                  required
                />
              </label>
              <label>
                Email address
                <input
                  type="email"
                  value={customerEmail}
                  onChange={(e) => setCustomerEmail(e.target.value)}
                  required
                />
              </label>
              <label>
                Card number
                <input
                  type="text"
                  placeholder="4242 4242 4242 4242"
                  value={cardNumber}
                  onChange={(e) => setCardNumber(e.target.value)}
                />
              </label>
            </div>
            {error && <p className="error-text">{error}</p>}
            <button type="submit" className="primary" disabled={submitting}>
              {submitting ? 'Processing...' : `Confirm booking ($${totalAmount.toFixed(2)})`}
            </button>
          </form>
        </section>
      </div>
    </div>
  )
}

export default BookingPage
