import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { fetchShowDetails } from '../api/theatres'
import { fetchSeats } from '../api/seating'
import { createBooking } from '../api/bookings'
import type { Booking, Seat, ShowDetailsResponse } from '../types'
import { SeatSelector } from '../components/SeatSelector'

const roundCurrency = (value: number) => Math.round(value * 100) / 100

const BookingPage = () => {
  const { showId } = useParams<{ showId: string }>()
  const navigate = useNavigate()

  const [details, setDetails] = useState<ShowDetailsResponse | null>(null)
  const [seats, setSeats] = useState<Seat[]>([])
  const [selectedSeats, setSelectedSeats] = useState<string[]>([])
  const [customerName, setCustomerName] = useState('')
  const [customerEmail, setCustomerEmail] = useState('')
  const [cardType, setCardType] = useState<'CREDIT' | 'DEBIT'>('CREDIT')
  const [cardNumber, setCardNumber] = useState('')
  const [cardExpiry, setCardExpiry] = useState('')
  const [cardCvv, setCardCvv] = useState('')
  const [seatRequest, setSeatRequest] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [paymentError, setPaymentError] = useState<string | null>(null)
  const [booking, setBooking] = useState<Booking | null>(null)

  useEffect(() => {
    if (!showId) {
      return
    }

    const load = async () => {
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

    load()
  }, [showId])

  const availableSeats = useMemo(
    () => seats.filter((seat) => seat.status === 'AVAILABLE').length,
    [seats],
  )

  const seatRequestCount = useMemo(() => Number(seatRequest) || 0, [seatRequest])
  const seatRequestError = useMemo(() => {
    if (seatRequestCount === 0) {
      return null
    }
    if (seatRequestCount > availableSeats) {
      return 'Required number of seats are not available'
    }
    return null
  }, [seatRequestCount, availableSeats])

  useEffect(() => {
    setError((current) => {
      const limitMessage = `You requested ${seatRequestCount} seats. Deselect seats to continue.`
      if (seatRequestCount > 0 && selectedSeats.length > seatRequestCount) {
        if (current && !current.startsWith('You requested')) {
          return current
        }
        return limitMessage
      }
      if (current && current.startsWith('You requested')) {
        return null
      }
      return current
    })
  }, [seatRequestCount, selectedSeats])

  const seatTotal = useMemo(() => {
    return roundCurrency(
      seats
        .filter((seat) => selectedSeats.includes(seat.seatNumber))
        .reduce((total, seat) => total + seat.price, 0),
    )
  }, [seats, selectedSeats])

  const convenienceFee = useMemo(() => roundCurrency(seatTotal * 0.05), [seatTotal])
  const gstAmount = useMemo(() => roundCurrency((seatTotal + convenienceFee) * 0.18), [seatTotal, convenienceFee])
  const discountAmount = useMemo(() => {
    const rate = cardType === 'CREDIT' ? 0.1 : 0.05
    return roundCurrency((seatTotal + convenienceFee + gstAmount) * rate)
  }, [seatTotal, convenienceFee, gstAmount, cardType])
  const totalAmount = useMemo(
    () => Math.max(0, roundCurrency(seatTotal + convenienceFee + gstAmount - discountAmount)),
    [seatTotal, convenienceFee, gstAmount, discountAmount],
  )

  const handleSeatToggle = (seatNumber: string) => {
    setPaymentError(null)
    setError(null)
    setSelectedSeats((prev) => {
      const alreadySelected = prev.includes(seatNumber)
      if (alreadySelected) {
        return prev.filter((seat) => seat !== seatNumber)
      }
      if (seatRequestCount > 0 && prev.length >= seatRequestCount) {
        setError(`You requested ${seatRequestCount} seats. Deselect a seat to choose another.`)
        return prev
      }
      return [...prev, seatNumber]
    })
  }

  const handleBookAnotherShow = () => {
    if (details?.movie?.id) {
      navigate(`/movies/${details.movie.id}`)
    } else {
      navigate('/')
    }
  }

  const validatePaymentFields = () => {
    if (!/^\d{16}$/.test(cardNumber.replace(/\s+/g, ''))) {
      setPaymentError('Card number must contain exactly 16 digits.')
      return false
    }
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(cardExpiry)) {
      setPaymentError('Enter expiry in MM/YY format.')
      return false
    }
    const [month, year] = cardExpiry.split('/').map((value) => Number(value))
    const expiryDate = new Date(2000 + year, month)
    const now = new Date()
    if (expiryDate <= now) {
      setPaymentError('Card has expired.')
      return false
    }
    if (!/^\d{3}$/.test(cardCvv)) {
      setPaymentError('CVV must contain 3 digits.')
      return false
    }
    setPaymentError(null)
    return true
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()
    if (!showId) {
      return
    }
    if (selectedSeats.length === 0) {
      setError('Please select at least one seat to continue.')
      return
    }
    if (seatRequestError) {
      setError(seatRequestError)
      return
    }
    if (seatRequestCount && selectedSeats.length !== seatRequestCount) {
      setError(`You selected ${selectedSeats.length} seats but requested ${seatRequestCount}.`)
      return
    }
    if (!validatePaymentFields()) {
      return
    }

    try {
      setSubmitting(true)
      setError(null)
      setPaymentError(null)
      const bookingResponse = await createBooking({
        showId: Number(showId),
        seatNumbers: selectedSeats,
        customerName,
        customerEmail,
        currency: 'USD',
        cardType,
        cardNumber: cardNumber.replace(/\s+/g, ''),
        expiry: cardExpiry,
        cvv: cardCvv,
      })
      setBooking(bookingResponse)
      setSelectedSeats([])
      setCardNumber('')
      setCardCvv('')
      setCardExpiry('')
      const updatedSeats = await fetchSeats(Number(showId))
      setSeats(updatedSeats)
    } catch (err) {
      setError('We could not complete your booking. Please verify your details and try again.')
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

  const basePrice = roundCurrency(details.show.basePrice)
  const estimatedTotal = seatRequestCount ? roundCurrency(basePrice * seatRequestCount) : 0

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
            <h4>Seat selection</h4>
            <p>Price per seat: ${basePrice.toFixed(2)}</p>
            <label className="seat-request">
              Seats requested
              <input
                type="number"
                min={0}
                value={seatRequest}
                onChange={(event) => setSeatRequest(event.target.value)}
                placeholder="How many seats do you need?"
              />
            </label>
            {seatRequestCount > 0 && (
              <p className="estimate">Estimated total: ${estimatedTotal.toFixed(2)}</p>
            )}
            {seatRequestError && (
              <div className="error-card content-card">
                <p>{seatRequestError}</p>
                <button type="button" onClick={handleBookAnotherShow}>
                  Book some other show
                </button>
              </div>
            )}
            {!seatRequestError && availableSeats === 0 && (
              <div className="error-card content-card">
                <p>All seats are booked for this show.</p>
                <button type="button" onClick={handleBookAnotherShow}>
                  Book some other show
                </button>
              </div>
            )}
          </div>

          <div className="summary-pricing">
            <h4>Pricing details</h4>
            <ul>
              <li>
                Seat total <span>${seatTotal.toFixed(2)}</span>
              </li>
              <li>
                Convenience fee <span>${convenienceFee.toFixed(2)}</span>
              </li>
              <li>
                GST (18%) <span>${gstAmount.toFixed(2)}</span>
              </li>
              <li>
                Discount ({cardType === 'CREDIT' ? '10%' : '5%'}) <span>- ${discountAmount.toFixed(2)}</span>
              </li>
            </ul>
            <div className="total">
              <span>Total payable</span>
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
            <h3>Passenger & payment details</h3>
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
              <div className="card-type-group">
                <span>Card type</span>
                <label>
                  <input
                    type="radio"
                    name="cardType"
                    value="CREDIT"
                    checked={cardType === 'CREDIT'}
                    onChange={() => setCardType('CREDIT')}
                  />
                  Credit card (10% discount)
                </label>
                <label>
                  <input
                    type="radio"
                    name="cardType"
                    value="DEBIT"
                    checked={cardType === 'DEBIT'}
                    onChange={() => setCardType('DEBIT')}
                  />
                  Debit card (5% discount)
                </label>
              </div>
              <label>
                Card number
                <input
                  type="text"
                  inputMode="numeric"
                  placeholder="0000 0000 0000 0000"
                  value={cardNumber}
                  onChange={(e) => setCardNumber(e.target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim())}
                  required
                />
              </label>
              <label>
                Expiry (MM/YY)
                <input
                  type="text"
                  inputMode="numeric"
                  placeholder="MM/YY"
                  value={cardExpiry}
                  onChange={(e) => setCardExpiry(e.target.value)}
                  required
                />
              </label>
              <label>
                CVV
                <input
                  type="password"
                  inputMode="numeric"
                  value={cardCvv}
                  onChange={(e) => setCardCvv(e.target.value)}
                  required
                />
              </label>
            </div>
            {error && <p className="error-text">{error}</p>}
            {paymentError && <p className="error-text">{paymentError}</p>}
            <button type="submit" className="primary" disabled={submitting}>
              {submitting ? 'Processing...' : `Pay $${totalAmount.toFixed(2)}`}
            </button>
            <Link to={`/movies/${details.movie?.id ?? ''}`} className="link-button">
              ← Back to movie details
            </Link>
          </form>
        </section>
      </div>
    </div>
  )
}

export default BookingPage
