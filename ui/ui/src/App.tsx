import React from 'react';
import {Route, Routes} from "react-router-dom";
import WelcomePage from "./pages/WelcomePage/WelcomePage";
import LoginPage from "./pages/LoginPage/LoginPage";
import AuctionsPage from "./pages/AuctionsPage/AuctionsPage";
import AuctionPage from "./pages/AuctionPage/AuctionPage";

function App() {
    return (
            <Routes>
                <Route path={'/'} element={<WelcomePage/>} />
                <Route path={'/login'} element={<LoginPage/>} />
                <Route path={'/auctions'} element={<AuctionsPage/>} />
                <Route path={'/auctions/:auctionId'} element={<AuctionPage/>} />
            </Routes>
    );
}

export default App;