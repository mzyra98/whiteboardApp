import { NavLink, Outlet } from 'react-router-dom'

export default function App() {
    return (
        <div className="kontener">
            <header className="naglowek">
                <h1>AplikacjaTablica</h1>
                <p>Udostępnianie tablic i statystyki</p>
            </header>
            <div className="uklad">
                <aside className="menu">
                    <NavLink
                        to="/dolacz"
                        className={({ isActive }: { isActive: boolean }) => (isActive ? 'aktywny' : undefined)}
                    >
                        Dołącz
                    </NavLink>
                    <NavLink
                        to="/statystyki"
                        className={({ isActive }: { isActive: boolean }) => (isActive ? 'aktywny' : undefined)}
                    >
                        Statystyki
                    </NavLink>
                    <NavLink
                        to="/udostepnij"
                        className={({ isActive }: { isActive: boolean }) => (isActive ? 'aktywny' : undefined)}
                    >
                        Udostępnij
                    </NavLink>
                </aside>
                <main>
                    <Outlet />
                </main>
            </div>
        </div>
    )
}
