// src/config/routes.js
// Note: This file is kept for backward compatibility
// Main routing is handled in App.jsx with lazy loading
// This file can be used for route metadata or navigation menus

const appRoutes = [
  { path: '/', label: 'Ana Sayfa', icon: 'Home' },
  { path: '/hospitals', label: 'Hastaneler', icon: 'LocalHospital' },
  { path: '/doctors', label: 'Doktorlar', icon: 'Person' },
  { path: '/accommodations', label: 'Konaklama', icon: 'Hotel' },
  { path: '/flights', label: 'Uçak Biletleri', icon: 'Flight' },
  { path: '/car-rentals', label: 'Araç Kiralama', icon: 'DirectionsCar' },
  { path: '/transfers', label: 'Transfer', icon: 'TransferWithinAStation' },
  { path: '/packages', label: 'Paketler', icon: 'CardTravel' },
  { path: '/reservations', label: 'Rezervasyonlar', icon: 'Event' },
  { path: '/payments', label: 'Ödemeler', icon: 'Payment' },
];

export default appRoutes;