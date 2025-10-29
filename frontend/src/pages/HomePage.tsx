import { useEffect, useMemo, useState } from 'react'
import { fetchHighlights, fetchMovies } from '../api/movies'
import type { Movie } from '../types'
import { MovieGrid } from '../components/MovieGrid'
import { MovieFilters } from '../components/MovieFilters'
import type { FilterCounts, MovieFilterState } from '../components/MovieFilters'

const initialFilters: MovieFilterState = {
  query: '',
  genre: 'all',
  language: 'all',
  rating: null,
}

const ratingThresholds = [4, 3, 2, 1]

const applyFilters = (list: Movie[], filters: MovieFilterState): Movie[] => {
  const loweredQuery = filters.query.trim().toLowerCase()
  return list.filter((movie) => {
    const matchesQuery =
      !loweredQuery ||
      movie.title.toLowerCase().includes(loweredQuery) ||
      movie.synopsis?.toLowerCase().includes(loweredQuery)

    const matchesGenre = filters.genre === 'all' || movie.genre === filters.genre
    const matchesLanguage = filters.language === 'all' || movie.language === filters.language
    const matchesRating = !filters.rating || movie.rating >= filters.rating

    return matchesQuery && matchesGenre && matchesLanguage && matchesRating
  })
}

const HomePage = () => {
  const [movies, setMovies] = useState<Movie[]>([])
  const [highlights, setHighlights] = useState<Movie[]>([])
  const [filters, setFilters] = useState<MovieFilterState>(initialFilters)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true)
        setError(null)
        const [allMovies, topPicks] = await Promise.all([fetchMovies(), fetchHighlights()])
        setMovies(allMovies)
        setHighlights(topPicks)
      } catch (err) {
        setError('Unable to load movies right now. Please try again later.')
      } finally {
        setLoading(false)
      }
    }

    load()
  }, [])

  const genres = useMemo(() => Array.from(new Set(movies.map((movie) => movie.genre))).sort(), [movies])
  const languages = useMemo(
    () => Array.from(new Set(movies.map((movie) => movie.language))).sort(),
    [movies],
  )

  const filteredMovies = useMemo(() => applyFilters(movies, filters), [movies, filters])

  const filterCounts: FilterCounts = useMemo(() => {
    const genreCounts = genres.reduce<Record<string, number>>((acc, genre) => {
      acc[genre] = applyFilters(movies, { ...filters, genre }).length
      return acc
    }, {})

    const languageCounts = languages.reduce<Record<string, number>>((acc, language) => {
      acc[language] = applyFilters(movies, { ...filters, language }).length
      return acc
    }, {})

    const ratingCounts = ratingThresholds.reduce<Record<number, number>>((acc, threshold) => {
      acc[threshold] = applyFilters(movies, { ...filters, rating: threshold }).length
      return acc
    }, {})

    return {
      genres: genreCounts,
      languages: languageCounts,
      ratings: ratingCounts,
      genresAll: applyFilters(movies, { ...filters, genre: 'all' }).length,
      languagesAll: applyFilters(movies, { ...filters, language: 'all' }).length,
      ratingsAll: applyFilters(movies, { ...filters, rating: null }).length,
    }
  }, [movies, filters, genres, languages])

  const handleFilterChange = (next: MovieFilterState) => {
    setFilters(next)
  }

  return (
    <div className="home-page">
      <section className="filters-wrapper">
        <h2 className="section-title">Find your next movie</h2>
        <MovieFilters
          genres={genres}
          languages={languages}
          counts={filterCounts}
          onChange={handleFilterChange}
          value={filters}
        />
      </section>

      {error && <div className="error-card content-card">{error}</div>}

      {loading ? (
        <div className="content-card">Loading movies...</div>
      ) : (
        <section aria-live="polite">
          {filteredMovies.length === 0 ? (
            <div className="content-card empty-state">No movies match your selection right now.</div>
          ) : (
            <MovieGrid movies={filteredMovies} />
          )}
        </section>
      )}

      {!loading && highlights.length > 0 && (
        <section className="highlights">
          <h2 className="section-title">Popular right now</h2>
          <MovieGrid movies={highlights} />
        </section>
      )}
    </div>
  )
}

export default HomePage
