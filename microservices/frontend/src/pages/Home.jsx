import React from 'react'
import { Container, Typography, Box, Button } from '@mui/material'
import { useNavigate } from 'react-router-dom'

function Home() {
  const navigate = useNavigate()

  return (
    <Container>
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h2" component="h1" gutterBottom>
          İstanbul'da Sağlık Turizmi
        </Typography>
        <Typography variant="h5" color="text.secondary" paragraph>
          Dünya standartlarında sağlık hizmetleri, deneyimli doktorlar ve konforlu konaklama imkanları
        </Typography>
        <Button
          variant="contained"
          size="large"
          onClick={() => navigate('/hospitals')}
          sx={{ mt: 4 }}
        >
          Hastaneleri Keşfet
        </Button>
      </Box>
    </Container>
  )
}

export default Home

