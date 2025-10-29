import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { fetchMovie } from '../api/movies'
import { fetchShowDetails, fetchShowsByMovie } from '../api/theatres'
import type { Movie, Show, Theatre } from '../types'

interface EnrichedShow extends Show {
  theatre: Theatre
}

const MovieDetailsPage = () => {
  const { movieId } = useParams<{ movieId: string }>()
  const navigate = useNavigate()
  const [movie, setMovie] = useState<Movie | null>(null)
  const [shows, setShows] = useState<EnrichedShow[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!movieId) return

    const loadDetails = async () => {
      try {
        setLoading(true)
        setError(null)
        const movieResponse = await fetchMovie(Number(movieId))
        setMovie(movieResponse)

        const showList = await fetchShowsByMovie(Number(movieId))
        const details = await Promise.all(
          showList.map(async (show) => {
            const detail = await fetchShowDetails(show.id)
            return {
              ...show,
              screen: detail.show.screen,
              theatre: detail.theatre,
            }
          }),
        )
        setShows(details)
      } catch (err) {
        setError('Unable to load movie details right now.')
      } finally {
        setLoading(false)
      }
    }

    loadDetails()
  }, [movieId])

  const groupedShows = useMemo(() => {
    return shows.reduce<Record<number, EnrichedShow[]>>((acc, show) => {
      acc[show.theatre.id] = acc[show.theatre.id] ? [...acc[show.theatre.id], show] : [show]
      return acc
    }, {})
  }, [shows])

  if (loading) {
    return <div className="content-card">Loading movie details...</div>
  }

  if (error || !movie) {
    return <div className="error-card content-card">{error ?? 'Movie not found.'}</div>
  }

  return (
    <div className="movie-details-page">
      <section className="movie-hero content-card">
        <div className="hero-layout">
          <div className="hero-poster">
            <img
              src={
                movie.posterUrl ??
                `https://via.placeholder.com/420x630.png?text=${encodeURIComponent(movie.title)}`
              }
              alt={movie.title}
            />
          </div>
          <div className="hero-info">
            <span className="badge primary">{movie.genre}</span>
            <h1>{movie.title}</h1>
            <p className="synopsis">{movie.synopsis}</p>
            <div className="hero-meta">
              <span>{movie.language}</span>
              <span>{movie.durationMinutes} min</span>
              <span>Rating: {movie.rating.toFixed(1)}★</span>
              <span>Release: {new Date(movie.releaseDate).toLocaleDateString()}</span>
            </div>
          </div>
        </div>
      </section>

      <section className="content-card">
        <h2 className="section-title">Available showtimes</h2>
        {shows.length === 0 ? (
          <p className="empty-state">No showtimes available right now.</p>
        ) : (
          Object.values(groupedShows).map((theatreShows) => (
            <div key={theatreShows[0].theatre.id} className="theatre-block">
              <div className="theatre-meta">
                <h3>{theatreShows[0].theatre.name}</h3>
                <p>{theatreShows[0].theatre.address}</p>
              </div>
              <div className="showtime-grid">
                {theatreShows.map((show) => (
                  <button
                    key={show.id}
                    className="showtime-card"
                    onClick={() => navigate(`/bookings/${show.id}?movieId=${movie.id}`)}
                  >
                    <span className="showtime">{new Date(show.startTime).toLocaleString()}</span>
                    <span className="screen">{show.screen}</span>
                    <span className="price">${show.basePrice.toFixed(2)}</span>
                  </button>
                ))}
              </div>
            </div>
          ))
        )}
      </section>
    </div>
  )
}

export default MovieDetailsPage
