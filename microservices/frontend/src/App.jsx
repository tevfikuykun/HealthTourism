import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import Hospitals from './pages/Hospitals'
import Doctors from './pages/Doctors'
import Accommodations from './pages/Accommodations'
import Flights from './pages/Flights'
import CarRentals from './pages/CarRentals'
import Transfers from './pages/Transfers'
import Packages from './pages/Packages'
import Reservations from './pages/Reservations'
import Payments from './pages/Payments'
import './App.css'

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/hospitals" element={<Hospitals />} />
          <Route path="/doctors" element={<Doctors />} />
          <Route path="/accommodations" element={<Accommodations />} />
          <Route path="/flights" element={<Flights />} />
          <Route path="/car-rentals" element={<CarRentals />} />
          <Route path="/transfers" element={<Transfers />} />
          <Route path="/packages" element={<Packages />} />
          <Route path="/reservations" element={<Reservations />} />
          <Route path="/payments" element={<Payments />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App

