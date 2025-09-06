import { Navigate, Outlet, useLocation } from "react-router-dom";
import { czyZalogowany } from "./lib/autoryzacja";

export default function Guard() {
    const loc = useLocation();
    return czyZalogowany() ? <Outlet /> : <Navigate to="/Logowanie" replace state={{ from: loc }} />;
}

