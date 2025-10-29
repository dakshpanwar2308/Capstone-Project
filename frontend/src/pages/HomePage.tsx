import { useEffect, useMemo, useState } from 'react'
import { fetchHighlights, fetchMovies, searchMovies } from '../api/movies'
import type { Movie } from '../types'
import { MovieGrid } from '../components/MovieGrid'
import { MovieFilters } from '../components/MovieFilters'
import type { MovieFilterState } from '../components/MovieFilters'

const defaultFilters: MovieFilterState = {
  query: '',
  genre: 'all',
  language: 'all',
  rating: 0,
}

const HomePage = () => {
  const [movies, setMovies] = useState<Movie[]>([])
  const [displayedMovies, setDisplayedMovies] = useState<Movie[]>([])
  const [highlights, setHighlights] = useState<Movie[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const loadMovies = async () => {
      try {
        setLoading(true)
        const [movieList, topPicks] = await Promise.all([fetchMovies(), fetchHighlights()])
        setMovies(movieList)
        setDisplayedMovies(movieList)
        setHighlights(topPicks)
      } catch (err) {
        setError('Unable to load movies right now. Please try again later.')
      } finally {
        setLoading(false)
      }
    }
    loadMovies()
  }, [])

  const genres = useMemo(() => Array.from(new Set(movies.map((movie) => movie.genre))).sort(), [movies])
  const languages = useMemo(
    () => Array.from(new Set(movies.map((movie) => movie.language))).sort(),
    [movies],
  )

  const handleFilters = async (filters: MovieFilterState) => {
    try {
      setLoading(true)
      setError(null)
      const params = {
        genre: filters.genre === 'all' ? undefined : filters.genre,
        language: filters.language === 'all' ? undefined : filters.language,
        rating: filters.rating > 0 ? filters.rating : undefined,
      }

      if (filters.query.trim()) {
        const results = await searchMovies({
          query: filters.query,
          genre: params.genre,
          language: params.language,
          rating: params.rating,
        })
        setDisplayedMovies(results)
      } else {
        const filtered = await fetchMovies(params)
        setDisplayedMovies(filtered)
      }
    } catch (err) {
      setError('Something went wrong while filtering movies.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="home-page">
      <section>
        <h2 className="section-title">Find your next movie</h2>
        <MovieFilters
          availableGenres={genres}
          availableLanguages={languages}
          initialState={defaultFilters}
          onFilterChange={handleFilters}
        />
      </section>

      {error && <div className="error-card content-card">{error}</div>}

      {loading ? (
        <div className="content-card">Loading movies...</div>
      ) : (
        <section>
          <MovieGrid movies={displayedMovies} />
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
