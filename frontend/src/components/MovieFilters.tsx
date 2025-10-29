import { useState } from 'react'
import type { FormEvent } from 'react'

export interface MovieFilterState {
  query: string
  genre: string
  language: string
  rating: number
}

interface MovieFiltersProps {
  availableGenres: string[]
  availableLanguages: string[]
  onFilterChange: (filters: MovieFilterState) => void
  initialState?: MovieFilterState
}

const defaultState: MovieFilterState = {
  query: '',
  genre: 'all',
  language: 'all',
  rating: 0,
}

export function MovieFilters({
  availableGenres,
  availableLanguages,
  onFilterChange,
  initialState,
}: MovieFiltersProps) {
  const [filters, setFilters] = useState<MovieFilterState>(initialState ?? defaultState)

  const handleSubmit = (event: FormEvent) => {
    event.preventDefault()
    onFilterChange(filters)
  }

  const handleReset = () => {
    setFilters(defaultState)
    onFilterChange(defaultState)
  }

  return (
    <form className="filters-card content-card" onSubmit={handleSubmit}>
      <div className="filters-grid">
        <div className="filter-group">
          <label htmlFor="query">Search</label>
          <input
            id="query"
            type="text"
            value={filters.query}
            onChange={(e) => setFilters({ ...filters, query: e.target.value })}
            placeholder="Search by movie title"
          />
        </div>
        <div className="filter-group">
          <label htmlFor="genre">Genre</label>
          <select
            id="genre"
            value={filters.genre}
            onChange={(e) => setFilters({ ...filters, genre: e.target.value })}
          >
            <option value="all">All</option>
            {availableGenres.map((genre) => (
              <option key={genre} value={genre}>
                {genre}
              </option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label htmlFor="language">Language</label>
          <select
            id="language"
            value={filters.language}
            onChange={(e) => setFilters({ ...filters, language: e.target.value })}
          >
            <option value="all">All</option>
            {availableLanguages.map((language) => (
              <option key={language} value={language}>
                {language}
              </option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label htmlFor="rating">Minimum Rating</label>
          <input
            id="rating"
            type="range"
            min={0}
            max={5}
            step={0.5}
            value={filters.rating}
            onChange={(e) => setFilters({ ...filters, rating: Number(e.target.value) })}
          />
          <span className="slider-value">{filters.rating.toFixed(1)}★</span>
        </div>
      </div>
      <div className="filter-actions">
        <button type="submit" className="primary">
          Apply
        </button>
        <button type="button" onClick={handleReset}>
          Reset
        </button>
      </div>
    </form>
  )
}
