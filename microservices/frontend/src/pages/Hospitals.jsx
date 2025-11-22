import React, { useState, useEffect } from 'react'
import { Container, Typography, Grid, Card, CardContent, CardActions, Button } from '@mui/material'
import { hospitalService } from '../services/api'

function Hospitals() {
  const [hospitals, setHospitals] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadHospitals()
  }, [])

  const loadHospitals = async () => {
    try {
      const response = await hospitalService.getAll()
      setHospitals(response.data)
    } catch (error) {
      console.error('Error loading hospitals:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <Container><Typography>Yükleniyor...</Typography></Container>
  }

  return (
    <Container sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Hastaneler
      </Typography>
      <Grid container spacing={3}>
        {hospitals.map((hospital) => (
          <Grid item xs={12} sm={6} md={4} key={hospital.id}>
            <Card>
              <CardContent>
                <Typography variant="h6">{hospital.name}</Typography>
                <Typography color="text.secondary">
                  {hospital.district}, {hospital.city}
                </Typography>
                <Typography variant="body2" sx={{ mt: 1 }}>
                  {hospital.address}
                </Typography>
              </CardContent>
              <CardActions>
                <Button size="small">Detaylar</Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  )
}

export default Hospitals

