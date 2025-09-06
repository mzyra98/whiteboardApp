import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Logowanie from "./strony/Logowanie";
import Rejestracja from "./strony/Rejestracja";
import Konto from "./strony/Konto";
import Dolacz from "./strony/Dolacz";
import Statystyki from "./strony/Statystyki";
import Udostepnij from "./strony/Udostepnij";
import Czat from "./strony/Czat";
import Guard from "./Guard";

export default function App(): React.ReactElement {
    return (
        <BrowserRouter>
            <Routes>
                {}
                <Route path="/" element={<Navigate to="/udostepnij" replace />} />
                {}
                <Route path="/logowanie" element={<Logowanie />} />
                <Route path="/rejestracja" element={<Rejestracja />} />
                {}
                <Route path="" element={<Guard />}>
                    <Route path="/konto" element={<Konto />} />
                    <Route path="/dolacz" element={<Dolacz />} />
                    <Route path="/statystyki" element={<Statystyki />} />
                    <Route path="/udostepnij" element={<Udostepnij />} />
                    <Route path="/czat/:id" element={<Czat />} />
                </Route>
                <Route path="*" element={<Navigate to="/udostepnij" replace />} />
            </Routes>
        </BrowserRouter>
    );
}
