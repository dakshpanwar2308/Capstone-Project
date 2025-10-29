import { Link } from 'react-router-dom'
import type { Movie } from '../types'

interface MovieCardProps {
  movie: Movie
}

export function MovieCard({ movie }: MovieCardProps) {
  const poster =
    movie.posterUrl ??
    `https://via.placeholder.com/320x450.png?text=${encodeURIComponent(movie.title)}`

  return (
    <div className="movie-card content-card">
      <div className="poster-wrapper">
        <img src={poster} alt={movie.title} />
      </div>
      <div className="movie-info">
        <div className="movie-meta">
          <span className="badge">{movie.genre}</span>
          <span className="badge">{movie.language}</span>
          <span className="badge">{movie.durationMinutes} min</span>
        </div>
        <h3>{movie.title}</h3>
        <p className="synopsis">{movie.synopsis}</p>
        <div className="movie-footer">
          <span className="rating">★ {movie.rating.toFixed(1)}</span>
          <Link to={`/movies/${movie.id}`} className="primary">
            View Details
          </Link>
        </div>
      </div>
    </div>
  )
}
