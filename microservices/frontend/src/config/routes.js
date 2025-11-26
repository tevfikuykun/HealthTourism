// src/config/routes.js
import Home from '../pages/Home';
import Hospitals from '../pages/Hospitals';
import Doctors from '../pages/Doctors';
import Accommodations from '../pages/Accommodations';
import Flights from '../pages/Flights';
import CarRentals from '../pages/CarRentals';
import Transfers from '../pages/Transfers';
import Packages from '../pages/Packages';
import Reservations from '../pages/Reservations';
import Payments from '../pages/Payments';

const appRoutes = [
  { path: '/', element: Home },
  { path: '/hospitals', element: Hospitals },
  { path: '/doctors', element: Doctors },
  { path: '/accommodations', element: Accommodations },
  { path: '/flights', element: Flights },
  { path: '/car-rentals', element: CarRentals },
  { path: '/transfers', element: Transfers },
  { path: '/packages', element: Packages },
  { path: '/reservations', element: Reservations },
  { path: '/payments', element: Payments },
];

export default appRoutes;