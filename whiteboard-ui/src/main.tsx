import { createRoot } from 'react-dom/client'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import App from './App'
import './index.css'
import Dolacz from './strony/Dolacz'
import Statystyki from './strony/Statystyki'
import Udostepnij from './strony/Udostepnij'

const el = document.getElementById('root') as HTMLElement

createRoot(el).render(
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<App />}>
        <Route index element={<Navigate to="/dolacz" replace />} />
        <Route path="dolacz" element={<Dolacz />} />
        <Route path="statystyki" element={<Statystyki />} />
        <Route path="udostepnij" element={<Udostepnij />} />
        <Route path="*" element={<Navigate to="/dolacz" replace />} />
      </Route>
    </Routes>
  </BrowserRouter>
)
