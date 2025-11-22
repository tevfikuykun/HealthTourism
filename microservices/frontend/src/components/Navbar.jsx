import React from 'react'
import { Link } from 'react-router-dom'
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material'
import LocalHospitalIcon from '@mui/icons-material/LocalHospital'

function Navbar() {
  return (
    <AppBar position="static">
      <Toolbar>
        <LocalHospitalIcon sx={{ mr: 2 }} />
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          İstanbul Sağlık Turizmi
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button color="inherit" component={Link} to="/">Ana Sayfa</Button>
          <Button color="inherit" component={Link} to="/hospitals">Hastaneler</Button>
          <Button color="inherit" component={Link} to="/doctors">Doktorlar</Button>
          <Button color="inherit" component={Link} to="/accommodations">Konaklama</Button>
          <Button color="inherit" component={Link} to="/flights">Uçak Bileti</Button>
          <Button color="inherit" component={Link} to="/car-rentals">Araç Kiralama</Button>
          <Button color="inherit" component={Link} to="/transfers">Transfer</Button>
          <Button color="inherit" component={Link} to="/packages">Paketler</Button>
          <Button color="inherit" component={Link} to="/reservations">Rezervasyonlar</Button>
        </Box>
      </Toolbar>
    </AppBar>
  )
}

export default Navbar

