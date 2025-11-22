const API_BASE_URL = 'http://localhost:8080/api';

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadHospitals();
    loadFlights();
    loadCarRentals();
    loadTransfers();
    loadPackages();
    setupEventListeners();
});

function setupEventListeners() {
    // Reservation form submit
    document.getElementById('reservationForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await createReservation();
    });
}

// Scroll to section
function scrollToSection(sectionId) {
    document.getElementById(sectionId).scrollIntoView({ behavior: 'smooth' });
}

// Load Hospitals
async function loadHospitals() {
    try {
        const response = await fetch(`${API_BASE_URL}/hospitals`);
        const hospitals = await response.json();
        displayHospitals(hospitals);
    } catch (error) {
        console.error('Error loading hospitals:', error);
        document.getElementById('hospitalsList').innerHTML = '<div class="loading">Hastaneler yüklenirken bir hata oluştu.</div>';
    }
}

function displayHospitals(hospitals) {
    const container = document.getElementById('hospitalsList');
    if (hospitals.length === 0) {
        container.innerHTML = '<div class="loading">Henüz hastane bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = hospitals.map(hospital => `
        <div class="card">
            <div class="card-header">
                <div>
                    <div class="card-title">${hospital.name}</div>
                    <div class="card-subtitle">${hospital.district}, ${hospital.city}</div>
                </div>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${hospital.rating.toFixed(1)}</span>
                </div>
            </div>
            <div class="card-body">
                <div class="card-info">
                    <i class="fas fa-map-marker-alt"></i>
                    <span>${hospital.address}</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-plane"></i>
                    <span>Havalimanı: ${hospital.airportDistance} km (${hospital.airportDistanceMinutes} dk)</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-phone"></i>
                    <span>${hospital.phone}</span>
                </div>
                ${hospital.description ? `<p style="margin-top: 10px; color: var(--text-light); font-size: 0.9rem;">${hospital.description.substring(0, 100)}...</p>` : ''}
            </div>
            <div class="card-footer">
                <div>
                    <div style="font-size: 0.85rem; color: var(--text-light);">${hospital.totalReviews} değerlendirme</div>
                </div>
                <button class="btn-secondary" onclick="viewHospitalDetails(${hospital.id})">Detaylar</button>
            </div>
        </div>
    `).join('');
}

// View Hospital Details
async function viewHospitalDetails(hospitalId) {
    try {
        const response = await fetch(`${API_BASE_URL}/hospitals/${hospitalId}`);
        const hospital = await response.json();
        
        // Load doctors for this hospital
        await loadDoctorsByHospital(hospitalId);
        
        // Load accommodations for this hospital
        await loadAccommodationsByHospital(hospitalId);
        
        // Scroll to doctors section
        scrollToSection('doctors');
    } catch (error) {
        console.error('Error loading hospital details:', error);
        alert('Hastane detayları yüklenirken bir hata oluştu.');
    }
}

// Load Doctors by Hospital
async function loadDoctorsByHospital(hospitalId) {
    try {
        const response = await fetch(`${API_BASE_URL}/doctors/hospital/${hospitalId}`);
        const doctors = await response.json();
        displayDoctors(doctors);
    } catch (error) {
        console.error('Error loading doctors:', error);
    }
}

function displayDoctors(doctors) {
    const container = document.getElementById('doctorsList');
    if (doctors.length === 0) {
        container.innerHTML = '<div class="loading">Bu hastanede doktor bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = doctors.map(doctor => `
        <div class="card">
            <div class="card-header">
                <div>
                    <div class="card-title">${doctor.fullName}</div>
                    <div class="card-subtitle">${doctor.specialization}</div>
                </div>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${doctor.rating.toFixed(1)}</span>
                </div>
            </div>
            <div class="card-body">
                <div class="card-info">
                    <i class="fas fa-hospital"></i>
                    <span>${doctor.hospitalName}</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-briefcase"></i>
                    <span>${doctor.experienceYears} yıl deneyim</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-language"></i>
                    <span>${doctor.languages}</span>
                </div>
                ${doctor.bio ? `<p style="margin-top: 10px; color: var(--text-light); font-size: 0.9rem;">${doctor.bio.substring(0, 100)}...</p>` : ''}
            </div>
            <div class="card-footer">
                <div class="price">${doctor.consultationFee.toFixed(2)} ₺</div>
                <button class="btn-secondary" onclick="openReservationModal(${doctor.hospitalId}, ${doctor.id})">Rezervasyon Yap</button>
            </div>
        </div>
    `).join('');
}

// Load Accommodations by Hospital
async function loadAccommodationsByHospital(hospitalId) {
    try {
        const response = await fetch(`${API_BASE_URL}/accommodations/hospital/${hospitalId}`);
        const accommodations = await response.json();
        displayAccommodations(accommodations);
    } catch (error) {
        console.error('Error loading accommodations:', error);
    }
}

function displayAccommodations(accommodations) {
    const container = document.getElementById('accommodationsList');
    if (accommodations.length === 0) {
        container.innerHTML = '<div class="loading">Bu hastane için konaklama bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = accommodations.map(acc => `
        <div class="card">
            <div class="card-header">
                <div>
                    <div class="card-title">${acc.name}</div>
                    <div class="card-subtitle">${acc.type} - ${acc.starRating} Yıldız</div>
                </div>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${acc.rating.toFixed(1)}</span>
                </div>
            </div>
            <div class="card-body">
                <div class="card-info">
                    <i class="fas fa-map-marker-alt"></i>
                    <span>${acc.address}</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-walking"></i>
                    <span>Hastaneye: ${acc.distanceToHospital} km (${acc.distanceToHospitalMinutes} dk)</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-plane"></i>
                    <span>Havalimanı: ${acc.airportDistance} km (${acc.airportDistanceMinutes} dk)</span>
                </div>
                <div style="margin-top: 10px; display: flex; gap: 15px;">
                    ${acc.hasWifi ? '<span style="color: var(--secondary-color);"><i class="fas fa-wifi"></i> WiFi</span>' : ''}
                    ${acc.hasParking ? '<span style="color: var(--secondary-color);"><i class="fas fa-parking"></i> Otopark</span>' : ''}
                    ${acc.hasBreakfast ? '<span style="color: var(--secondary-color);"><i class="fas fa-coffee"></i> Kahvaltı</span>' : ''}
                </div>
            </div>
            <div class="card-footer">
                <div class="price">${acc.pricePerNight.toFixed(2)} ₺/gece</div>
                <div style="font-size: 0.85rem; color: var(--text-light);">${acc.totalReviews} değerlendirme</div>
            </div>
        </div>
    `).join('');
}

// Search Hospitals
async function searchHospitals() {
    const city = document.getElementById('citySearch').value;
    const specialization = document.getElementById('specializationSearch').value;
    
    try {
        let url = `${API_BASE_URL}/hospitals`;
        if (city) {
            url = `${API_BASE_URL}/hospitals/city/${city}`;
        }
        
        const response = await fetch(url);
        const hospitals = await response.json();
        displayHospitals(hospitals);
        
        // If specialization is provided, filter doctors
        if (specialization) {
            const doctorsResponse = await fetch(`${API_BASE_URL}/doctors/specialization/${specialization}`);
            const doctors = await doctorsResponse.json();
            displayDoctors(doctors);
        }
    } catch (error) {
        console.error('Error searching:', error);
        alert('Arama yapılırken bir hata oluştu.');
    }
}

// Reservation Modal
function openReservationModal(hospitalId, doctorId) {
    document.getElementById('reservationModal').style.display = 'block';
    document.getElementById('hospitalSelect').value = hospitalId;
    document.getElementById('doctorSelect').value = doctorId;
    loadDoctors();
    loadAccommodations();
}

function closeModal() {
    document.getElementById('reservationModal').style.display = 'none';
}

// Load Doctors for Select
async function loadDoctors() {
    const hospitalId = document.getElementById('hospitalSelect').value;
    if (!hospitalId) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/doctors/hospital/${hospitalId}`);
        const doctors = await response.json();
        
        const select = document.getElementById('doctorSelect');
        select.innerHTML = doctors.map(d => 
            `<option value="${d.id}">${d.fullName} - ${d.specialization}</option>`
        ).join('');
    } catch (error) {
        console.error('Error loading doctors:', error);
    }
}

// Load Accommodations for Select
async function loadAccommodations() {
    const hospitalId = document.getElementById('hospitalSelect').value;
    if (!hospitalId) return;
    
    try {
        const response = await fetch(`${API_BASE_URL}/accommodations/hospital/${hospitalId}`);
        const accommodations = await response.json();
        
        const select = document.getElementById('accommodationSelect');
        select.innerHTML = '<option value="">Konaklama seçmeyin</option>' +
            accommodations.map(a => 
                `<option value="${a.id}">${a.name} - ${a.pricePerNight.toFixed(2)} ₺/gece</option>`
            ).join('');
    } catch (error) {
        console.error('Error loading accommodations:', error);
    }
}

// Create Reservation
async function createReservation() {
    const reservationData = {
        userId: parseInt(document.getElementById('userId').value),
        hospitalId: parseInt(document.getElementById('hospitalSelect').value),
        doctorId: parseInt(document.getElementById('doctorSelect').value),
        accommodationId: document.getElementById('accommodationSelect').value ? 
            parseInt(document.getElementById('accommodationSelect').value) : null,
        appointmentDate: document.getElementById('appointmentDate').value,
        checkInDate: document.getElementById('checkInDate').value,
        checkOutDate: document.getElementById('checkOutDate').value,
        notes: document.getElementById('notes').value
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reservationData)
        });
        
        if (response.ok) {
            const reservation = await response.json();
            alert(`Rezervasyon başarıyla oluşturuldu! Rezervasyon No: ${reservation.reservationNumber}`);
            closeModal();
            document.getElementById('reservationForm').reset();
        } else {
            const error = await response.text();
            alert('Rezervasyon oluşturulamadı: ' + error);
        }
    } catch (error) {
        console.error('Error creating reservation:', error);
        alert('Rezervasyon oluşturulurken bir hata oluştu.');
    }
}

// Load User Reservations
async function loadUserReservations() {
    const userId = document.getElementById('userReservationId').value;
    if (!userId) {
        alert('Lütfen kullanıcı ID girin.');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/reservations/user/${userId}`);
        const reservations = await response.json();
        displayReservations(reservations);
    } catch (error) {
        console.error('Error loading reservations:', error);
        alert('Rezervasyonlar yüklenirken bir hata oluştu.');
    }
}

function displayReservations(reservations) {
    const container = document.getElementById('reservationsList');
    if (reservations.length === 0) {
        container.innerHTML = '<div class="loading">Rezervasyon bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = reservations.map(res => {
        const statusClass = `status-${res.status.toLowerCase()}`;
        const appointmentDate = new Date(res.appointmentDate).toLocaleString('tr-TR');
        const checkInDate = new Date(res.checkInDate).toLocaleDateString('tr-TR');
        const checkOutDate = new Date(res.checkOutDate).toLocaleDateString('tr-TR');
        
        return `
            <div class="card">
                <div class="card-header">
                    <div>
                        <div class="card-title">Rezervasyon #${res.reservationNumber}</div>
                        <div class="card-subtitle">${res.hospitalName}</div>
                    </div>
                    <span class="status-badge ${statusClass}">${getStatusText(res.status)}</span>
                </div>
                <div class="card-body">
                    <div class="card-info">
                        <i class="fas fa-user-md"></i>
                        <span>Doktor: ${res.doctorName}</span>
                    </div>
                    <div class="card-info">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Randevu: ${appointmentDate}</span>
                    </div>
                    ${res.accommodationName ? `
                        <div class="card-info">
                            <i class="fas fa-hotel"></i>
                            <span>Konaklama: ${res.accommodationName}</span>
                        </div>
                        <div class="card-info">
                            <i class="fas fa-calendar"></i>
                            <span>Giriş: ${checkInDate} - Çıkış: ${checkOutDate} (${res.numberOfNights} gece)</span>
                        </div>
                    ` : ''}
                    ${res.notes ? `<p style="margin-top: 10px; color: var(--text-light);">${res.notes}</p>` : ''}
                </div>
                <div class="card-footer">
                    <div class="price">${res.totalPrice.toFixed(2)} ₺</div>
                    <div style="font-size: 0.85rem; color: var(--text-light);">
                        ${new Date(res.createdAt).toLocaleDateString('tr-TR')}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': 'Beklemede',
        'CONFIRMED': 'Onaylandı',
        'CANCELLED': 'İptal Edildi',
        'COMPLETED': 'Tamamlandı'
    };
    return statusMap[status] || status;
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('reservationModal');
    if (event.target == modal) {
        closeModal();
    }
}

// Load Flights
async function loadFlights() {
    try {
        const response = await fetch(`${API_BASE_URL}/flights`);
        const flights = await response.json();
        displayFlights(flights);
    } catch (error) {
        console.error('Error loading flights:', error);
    }
}

function displayFlights(flights) {
    const container = document.getElementById('flightsList');
    if (flights.length === 0) {
        container.innerHTML = '<div class="loading">Uçuş bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = flights.map(flight => {
        const departureDate = new Date(flight.departureDateTime).toLocaleString('tr-TR');
        const arrivalDate = new Date(flight.arrivalDateTime).toLocaleString('tr-TR');
        const durationHours = Math.floor(flight.durationMinutes / 60);
        const durationMins = flight.durationMinutes % 60;
        
        return `
            <div class="card">
                <div class="card-header">
                    <div>
                        <div class="card-title">${flight.airlineName}</div>
                        <div class="card-subtitle">${flight.flightNumber}</div>
                    </div>
                    <div class="rating">
                        <i class="fas fa-star"></i>
                        <span>${flight.rating.toFixed(1)}</span>
                    </div>
                </div>
                <div class="card-body">
                    <div class="card-info">
                        <i class="fas fa-plane-departure"></i>
                        <span>${flight.departureCity} → ${flight.arrivalCity}</span>
                    </div>
                    <div class="card-info">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Kalkış: ${departureDate}</span>
                    </div>
                    <div class="card-info">
                        <i class="fas fa-calendar-check"></i>
                        <span>Varış: ${arrivalDate}</span>
                    </div>
                    <div class="card-info">
                        <i class="fas fa-clock"></i>
                        <span>Süre: ${durationHours}s ${durationMins}d</span>
                    </div>
                    <div class="card-info">
                        <i class="fas fa-chair"></i>
                        <span>Sınıf: ${flight.flightClass} | Koltuk: ${flight.availableSeats}</span>
                    </div>
                    <div class="card-info">
                        <i class="fas fa-suitcase"></i>
                        <span>Bagaj: ${flight.baggageAllowance} kg</span>
                    </div>
                    <div style="margin-top: 10px; display: flex; gap: 15px;">
                        ${flight.hasMeal ? '<span style="color: var(--secondary-color);"><i class="fas fa-utensils"></i> Yemek</span>' : ''}
                        ${flight.hasEntertainment ? '<span style="color: var(--secondary-color);"><i class="fas fa-tv"></i> Eğlence</span>' : ''}
                        ${flight.isDirectFlight ? '<span style="color: var(--secondary-color);"><i class="fas fa-check-circle"></i> Direkt</span>' : ''}
                    </div>
                </div>
                <div class="card-footer">
                    <div class="price">${flight.price.toFixed(2)} ₺</div>
                    <button class="btn-secondary" onclick="bookFlight(${flight.id})">Rezervasyon Yap</button>
                </div>
            </div>
        `;
    }).join('');
}

// Search Flights
async function searchFlights() {
    const departure = document.getElementById('departureCity').value;
    const arrival = document.getElementById('arrivalCity').value;
    
    if (!departure || !arrival) {
        alert('Lütfen kalkış ve varış şehirlerini girin.');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/flights/search?departureCity=${departure}&arrivalCity=${arrival}`);
        const flights = await response.json();
        displayFlights(flights);
    } catch (error) {
        console.error('Error searching flights:', error);
        alert('Uçuş arama yapılırken bir hata oluştu.');
    }
}

function bookFlight(flightId) {
    alert('Uçuş rezervasyonu için lütfen iletişime geçin. Flight ID: ' + flightId);
}

// Load Car Rentals
async function loadCarRentals() {
    try {
        const response = await fetch(`${API_BASE_URL}/car-rentals`);
        const carRentals = await response.json();
        displayCarRentals(carRentals);
    } catch (error) {
        console.error('Error loading car rentals:', error);
    }
}

function displayCarRentals(carRentals) {
    const container = document.getElementById('carRentalsList');
    if (carRentals.length === 0) {
        container.innerHTML = '<div class="loading">Araç kiralama bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = carRentals.map(car => `
        <div class="card">
            <div class="card-header">
                <div>
                    <div class="card-title">${car.carModel}</div>
                    <div class="card-subtitle">${car.companyName} - ${car.carType}</div>
                </div>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${car.rating.toFixed(1)}</span>
                </div>
            </div>
            <div class="card-body">
                <div class="card-info">
                    <i class="fas fa-users"></i>
                    <span>${car.passengerCapacity} kişi</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-suitcase"></i>
                    <span>${car.luggageCapacity} bagaj</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-cog"></i>
                    <span>${car.transmission}</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-map-marker-alt"></i>
                    <span>${car.pickupLocation} → ${car.dropoffLocation}</span>
                </div>
                <div style="margin-top: 10px; display: flex; gap: 15px; flex-wrap: wrap;">
                    ${car.hasAirConditioning ? '<span style="color: var(--secondary-color);"><i class="fas fa-snowflake"></i> Klima</span>' : ''}
                    ${car.hasGPS ? '<span style="color: var(--secondary-color);"><i class="fas fa-map"></i> GPS</span>' : ''}
                    ${car.includesInsurance ? '<span style="color: var(--secondary-color);"><i class="fas fa-shield-alt"></i> Sigorta</span>' : ''}
                    ${car.includesDriver ? '<span style="color: var(--secondary-color);"><i class="fas fa-user-tie"></i> Şoförlü</span>' : ''}
                </div>
            </div>
            <div class="card-footer">
                <div>
                    <div class="price">${car.pricePerDay.toFixed(2)} ₺/gün</div>
                    <div style="font-size: 0.85rem; color: var(--text-light);">
                        Haftalık: ${car.pricePerWeek.toFixed(2)} ₺
                    </div>
                </div>
                <button class="btn-secondary" onclick="bookCarRental(${car.id})">Kirala</button>
            </div>
        </div>
    `).join('');
}

async function filterCarRentals() {
    const carType = document.getElementById('carTypeFilter').value;
    try {
        let url = `${API_BASE_URL}/car-rentals`;
        if (carType) {
            url = `${API_BASE_URL}/car-rentals/type/${carType}`;
        }
        const response = await fetch(url);
        const carRentals = await response.json();
        displayCarRentals(carRentals);
    } catch (error) {
        console.error('Error filtering car rentals:', error);
    }
}

function bookCarRental(carRentalId) {
    alert('Araç kiralama için lütfen iletişime geçin. Car Rental ID: ' + carRentalId);
}

// Load Transfers
async function loadTransfers() {
    try {
        const response = await fetch(`${API_BASE_URL}/transfers`);
        const transfers = await response.json();
        displayTransfers(transfers);
    } catch (error) {
        console.error('Error loading transfers:', error);
    }
}

function displayTransfers(transfers) {
    const container = document.getElementById('transfersList');
    if (transfers.length === 0) {
        container.innerHTML = '<div class="loading">Transfer hizmeti bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = transfers.map(transfer => `
        <div class="card">
            <div class="card-header">
                <div>
                    <div class="card-title">${transfer.companyName}</div>
                    <div class="card-subtitle">${transfer.serviceType} - ${transfer.vehicleType}</div>
                </div>
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${transfer.rating.toFixed(1)}</span>
                </div>
            </div>
            <div class="card-body">
                <div class="card-info">
                    <i class="fas fa-route"></i>
                    <span>${transfer.pickupLocation} → ${transfer.dropoffLocation}</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-users"></i>
                    <span>${transfer.passengerCapacity} kişi</span>
                </div>
                <div class="card-info">
                    <i class="fas fa-road"></i>
                    <span>${transfer.distanceKm} km (${transfer.durationMinutes} dk)</span>
                </div>
                <div style="margin-top: 10px; display: flex; gap: 15px; flex-wrap: wrap;">
                    ${transfer.hasMeetAndGreet ? '<span style="color: var(--secondary-color);"><i class="fas fa-handshake"></i> Karşılama</span>' : ''}
                    ${transfer.hasLuggageAssistance ? '<span style="color: var(--secondary-color);"><i class="fas fa-suitcase"></i> Bagaj Yardımı</span>' : ''}
                    ${transfer.hasWifi ? '<span style="color: var(--secondary-color);"><i class="fas fa-wifi"></i> WiFi</span>' : ''}
                    ${transfer.hasChildSeat ? '<span style="color: var(--secondary-color);"><i class="fas fa-baby"></i> Çocuk Koltuğu</span>' : ''}
                </div>
            </div>
            <div class="card-footer">
                <div class="price">${transfer.price.toFixed(2)} ₺</div>
                <button class="btn-secondary" onclick="bookTransfer(${transfer.id})">Rezervasyon Yap</button>
            </div>
        </div>
    `).join('');
}

function bookTransfer(transferId) {
    alert('Transfer rezervasyonu için lütfen iletişime geçin. Transfer ID: ' + transferId);
}

// Load Packages
async function loadPackages() {
    try {
        const response = await fetch(`${API_BASE_URL}/packages`);
        const packages = await response.json();
        displayPackages(packages);
    } catch (error) {
        console.error('Error loading packages:', error);
    }
}

function displayPackages(packages) {
    const container = document.getElementById('packagesList');
    if (packages.length === 0) {
        container.innerHTML = '<div class="loading">Paket bulunmamaktadır.</div>';
        return;
    }
    
    container.innerHTML = packages.map(pkg => {
        const discount = pkg.discountPercentage > 0 ? 
            `<div style="color: var(--danger-color); font-weight: 600;">%${pkg.discountPercentage.toFixed(0)} İndirim!</div>` : '';
        
        return `
            <div class="card">
                <div class="card-header">
                    <div>
                        <div class="card-title">${pkg.packageName}</div>
                        <div class="card-subtitle">${pkg.packageType} Paket - ${pkg.durationDays} Gün</div>
                    </div>
                    <div class="rating">
                        <i class="fas fa-star"></i>
                        <span>${pkg.rating.toFixed(1)}</span>
                    </div>
                </div>
                <div class="card-body">
                    <div class="card-info">
                        <i class="fas fa-hospital"></i>
                        <span>${pkg.hospitalName}</span>
                    </div>
                    ${pkg.doctorName ? `
                        <div class="card-info">
                            <i class="fas fa-user-md"></i>
                            <span>${pkg.doctorName}</span>
                        </div>
                    ` : ''}
                    <div style="margin-top: 15px; padding: 15px; background: var(--bg-light); border-radius: 8px;">
                        <div style="font-weight: 600; margin-bottom: 10px;">Paket İçeriği:</div>
                        <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px; font-size: 0.9rem;">
                            ${pkg.includesFlight ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Uçak Bileti</span>' : ''}
                            ${pkg.includesAccommodation ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Konaklama</span>' : ''}
                            ${pkg.includesTransfer ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Transfer</span>' : ''}
                            ${pkg.includesCarRental ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Araç Kiralama</span>' : ''}
                            ${pkg.includesVisaService ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Vize Hizmeti</span>' : ''}
                            ${pkg.includesTranslation ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Tercüman</span>' : ''}
                            ${pkg.includesInsurance ? '<span><i class="fas fa-check" style="color: var(--secondary-color);"></i> Sigorta</span>' : ''}
                        </div>
                    </div>
                    ${pkg.description ? `<p style="margin-top: 15px; color: var(--text-light); font-size: 0.9rem;">${pkg.description.substring(0, 150)}...</p>` : ''}
                </div>
                <div class="card-footer">
                    <div>
                        ${discount}
                        <div style="text-decoration: line-through; color: var(--text-light); font-size: 0.9rem;">
                            ${pkg.totalPrice.toFixed(2)} ₺
                        </div>
                        <div class="price">${pkg.finalPrice.toFixed(2)} ₺</div>
                    </div>
                    <button class="btn-secondary" onclick="bookPackage(${pkg.id})">Paketi Seç</button>
                </div>
            </div>
        `;
    }).join('');
}

function bookPackage(packageId) {
    alert('Paket rezervasyonu için lütfen iletişime geçin. Package ID: ' + packageId);
}

