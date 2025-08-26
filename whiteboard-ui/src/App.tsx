import { NavLink, Routes, Route, Navigate, useLocation } from 'react-router-dom'
import Dolacz from './strony/Dolacz'
import Statystyki from './strony/Statystyki'
import Udostepnij from './strony/Udostepnij'

function aktywna({ isActive }: { isActive: boolean }) {
  return isActive ? 'aktywny' : ''
}

export default function App() {
  useLocation()

  return (
      <div className="kontener">
        <header className="naglowek">
          <h1>AplikacjaTablica</h1>
          <p>Udostępnianie tablic i statystyki</p>
        </header>

        <div className="uklad">
          <nav className="menu">
            <NavLink to="/dolacz" className={aktywna}>Dołącz</NavLink>
            <NavLink to="/statystyki" className={aktywna}>Statystyki</NavLink>
            <NavLink to="/udostepnij" className={aktywna}>Udostępnij</NavLink>
          </nav>

          <main>
            <Routes>
              <Route path="/" element={<Navigate to="/udostepnij" replace />} />
              <Route path="/dolacz" element={<Dolacz />} />
              <Route path="/statystyki" element={<Statystyki />} />
              <Route path="/udostepnij" element={<Udostepnij />} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </div>
  )
}