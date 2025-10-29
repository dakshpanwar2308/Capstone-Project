import type { Seat } from '../types'

interface SeatSelectorProps {
  seats: Seat[]
  selectedSeats: string[]
  onToggle: (seatNumber: string) => void
}

const statusClass: Record<Seat['status'], string> = {
  AVAILABLE: 'seat-available',
  HELD: 'seat-held',
  RESERVED: 'seat-reserved',
  BOOKED: 'seat-booked',
}

function groupSeatsByRow(seats: Seat[]) {
  return seats.reduce<Record<string, Seat[]>>((acc, seat) => {
    const row = seat.seatNumber.charAt(0)
    acc[row] = acc[row] ? [...acc[row], seat] : [seat]
    return acc
  }, {})
}

export function SeatSelector({ seats, selectedSeats, onToggle }: SeatSelectorProps) {
  const grouped = groupSeatsByRow(seats)

  return (
    <div className="seat-selector">
      {Object.entries(grouped)
        .sort(([rowA], [rowB]) => rowA.localeCompare(rowB))
        .map(([row, rowSeats]) => {
          const sortedSeats = [...rowSeats].sort((a, b) =>
            a.seatNumber.localeCompare(b.seatNumber, undefined, { numeric: true }),
          )
          return (
            <div key={row} className="seat-row">
              <span className="row-label">{row}</span>
              <div className="seat-row-grid">
                {sortedSeats.map((seat) => {
                  const isSelected = selectedSeats.includes(seat.seatNumber)
                  const isDisabled = seat.status !== 'AVAILABLE'
                  return (
                    <button
                      key={seat.id}
                      type="button"
                      className={`seat ${statusClass[seat.status]} ${isSelected ? 'active' : ''}`}
                      disabled={isDisabled}
                      onClick={() => onToggle(seat.seatNumber)}
                      title={`Seat ${seat.seatNumber} • $${seat.price.toFixed(2)}`}
                      aria-label={`Seat ${seat.seatNumber}, $${seat.price.toFixed(2)}, ${seat.status.toLowerCase()}`}
                    >
                      {seat.seatNumber}
                    </button>
                  )
                })}
              </div>
            </div>
          )
        })}
      <div className="seat-legend">
        <span><span className="legend available" />Available</span>
        <span><span className="legend selected" />Selected</span>
        <span><span className="legend reserved" />Reserved / Booked</span>
      </div>
    </div>
  )
}
