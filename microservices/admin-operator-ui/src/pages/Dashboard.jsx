import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  LinearProgress,
} from '@mui/material';
import { api } from '../services/api';

function Dashboard() {
  const [stats, setStats] = useState({
    totalReservations: 0,
    activeUsers: 0,
    securityAlerts: 0,
    apiRequests: 0,
  });

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      // Fetch statistics from various services
      // Simplified - should fetch from actual APIs
      setStats({
        totalReservations: 1250,
        activeUsers: 342,
        securityAlerts: 5,
        apiRequests: 125000,
      });
    } catch (error) {
      console.error('Error fetching stats:', error);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      
      <Grid container spacing={3}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Total Reservations
              </Typography>
              <Typography variant="h4">
                {stats.totalReservations}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Active Users
              </Typography>
              <Typography variant="h4">
                {stats.activeUsers}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                Security Alerts
              </Typography>
              <Typography variant="h4" color="error">
                {stats.securityAlerts}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Typography color="textSecondary" gutterBottom>
                API Requests (Today)
              </Typography>
              <Typography variant="h4">
                {stats.apiRequests.toLocaleString()}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

export default Dashboard;
