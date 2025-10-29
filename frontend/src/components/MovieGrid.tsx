import type { Movie } from '../types'
import { MovieCard } from './MovieCard'

interface MovieGridProps {
  movies: Movie[]
}

export function MovieGrid({ movies }: MovieGridProps) {
  if (!movies.length) {
    return <p className="empty-state">No movies match your search criteria.</p>
  }

  return (
    <div className="movie-grid">
      {movies.map((movie) => (
        <MovieCard key={movie.id} movie={movie} />
      ))}
    </div>
  )
}
