import { useEffect, useMemo, useState } from 'react'

export interface MovieFilterState {
  query: string
  genre: string
  language: string
  rating: number | null
}

export interface FilterCounts {
  genres: Record<string, number>
  languages: Record<string, number>
  ratings: Record<number, number>
  genresAll: number
  languagesAll: number
  ratingsAll: number
}

interface MovieFiltersProps {
  genres: string[]
  languages: string[]
  counts: FilterCounts
  onChange: (filters: MovieFilterState) => void
  value?: MovieFilterState
}

const defaultState: MovieFilterState = {
  query: '',
  genre: 'all',
  language: 'all',
  rating: null,
}

export function MovieFilters({
  genres,
  languages,
  counts,
  onChange,
  value,
}: MovieFiltersProps) {
  const [filters, setFilters] = useState<MovieFilterState>(value ?? defaultState)
  const [expanded, setExpanded] = useState<{ rating: boolean; genre: boolean; language: boolean }>({
    rating: true,
    genre: true,
    language: true,
  })

  const ratingOptions = useMemo(
    () => [
      { label: '4+ stars', value: 4 },
      { label: '3+ stars', value: 3 },
      { label: '2+ stars', value: 2 },
      { label: '1+ stars', value: 1 },
    ],
    [],
  )

  const updateFilters = (patch: Partial<MovieFilterState>) => {
    const next = { ...filters, ...patch }
    setFilters(next)
    onChange(next)
  }

  const handleReset = () => {
    setFilters(defaultState)
    onChange(defaultState)
  }

  useEffect(() => {
    if (value) {
      setFilters(value)
    }
  }, [value])

  return (
    <aside className="filters-card content-card">
      <div className="filter-group search">
        <label htmlFor="query">Search</label>
        <input
          id="query"
          type="text"
          value={filters.query}
          onChange={(e) => updateFilters({ query: e.target.value })}
          placeholder="Search by title"
        />
      </div>

      <div className="filter-accordion">
        <details open={expanded.rating} onToggle={(event) => setExpanded((prev) => ({ ...prev, rating: (event.target as HTMLDetailsElement).open }))}>
          <summary>Rating</summary>
          <ul>
            <li>
              <button
                type="button"
                className={!filters.rating ? 'active' : ''}
                onClick={() => updateFilters({ rating: null })}
              >
                All <span className="count">({counts.ratingsAll})</span>
              </button>
            </li>
            {ratingOptions.map((option) => (
              <li key={option.value}>
                <button
                  type="button"
                  className={filters.rating === option.value ? 'active' : ''}
                  onClick={() => updateFilters({ rating: option.value })}
                >
                  {option.label} <span className="count">({counts.ratings[option.value] ?? 0})</span>
                </button>
              </li>
            ))}
          </ul>
        </details>

        <details open={expanded.genre} onToggle={(event) => setExpanded((prev) => ({ ...prev, genre: (event.target as HTMLDetailsElement).open }))}>
          <summary>Genre</summary>
          <ul>
            <li>
              <button
                type="button"
                className={filters.genre === 'all' ? 'active' : ''}
                onClick={() => updateFilters({ genre: 'all' })}
              >
                All <span className="count">({counts.genresAll})</span>
              </button>
            </li>
            {genres.map((genre) => (
              <li key={genre}>
                <button
                  type="button"
                  className={filters.genre === genre ? 'active' : ''}
                  onClick={() => updateFilters({ genre })}
                >
                  {genre} <span className="count">({counts.genres[genre] ?? 0})</span>
                </button>
              </li>
            ))}
          </ul>
        </details>

        <details open={expanded.language} onToggle={(event) => setExpanded((prev) => ({ ...prev, language: (event.target as HTMLDetailsElement).open }))}>
          <summary>Language</summary>
          <ul>
            <li>
              <button
                type="button"
                className={filters.language === 'all' ? 'active' : ''}
                onClick={() => updateFilters({ language: 'all' })}
              >
                All <span className="count">({counts.languagesAll})</span>
              </button>
            </li>
            {languages.map((language) => (
              <li key={language}>
                <button
                  type="button"
                  className={filters.language === language ? 'active' : ''}
                  onClick={() => updateFilters({ language })}
                >
                  {language} <span className="count">({counts.languages[language] ?? 0})</span>
                </button>
              </li>
            ))}
          </ul>
        </details>
      </div>

      <div className="filter-actions">
        <button type="button" onClick={handleReset}>
          Reset filters
        </button>
      </div>
    </aside>
  )
}
