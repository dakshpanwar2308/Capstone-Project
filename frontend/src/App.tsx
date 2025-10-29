import './App.css'
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
import HomePage from './pages/HomePage'
import MovieDetailsPage from './pages/MovieDetailsPage'
import BookingPage from './pages/BookingPage'

function App() {
  return (
    <BrowserRouter>
      <div className="app-shell">
        <header className="app-header">
          <Link to="/" className="brand">
            I-Cinema
          </Link>
          <nav>
            <a href="https://github.com/" target="_blank" rel="noreferrer">
              GitHub
            </a>
          </nav>
        </header>
        <main className="app-main">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/movies/:movieId" element={<MovieDetailsPage />} />
            <Route path="/bookings/:showId" element={<BookingPage />} />
          </Routes>
        </main>
        <footer className="app-footer">
          <span>© {new Date().getFullYear()} I-Cinema. All rights reserved.</span>
        </footer>
      </div>
    </BrowserRouter>
  )
}

export default App
