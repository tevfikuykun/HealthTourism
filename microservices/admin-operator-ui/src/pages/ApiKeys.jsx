import React, { useState, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Chip,
  Box,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import { api } from '../services/api';

function ApiKeys() {
  const [apiKeys, setApiKeys] = useState([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [newKey, setNewKey] = useState({ name: '', organization: '', plan: 'BASIC' });

  useEffect(() => {
    fetchApiKeys();
  }, []);

  const fetchApiKeys = async () => {
    try {
      const response = await api.apiKeyService.getAllApiKeys();
      setApiKeys(response.data || []);
    } catch (error) {
      console.error('Error fetching API keys:', error);
    }
  };

  const handleGenerateKey = async () => {
    try {
      await api.apiKeyService.generateApiKey(newKey);
      setOpenDialog(false);
      setNewKey({ name: '', organization: '', plan: 'BASIC' });
      fetchApiKeys();
    } catch (error) {
      console.error('Error generating API key:', error);
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4">API Key Management</Typography>
        <Button variant="contained" onClick={() => setOpenDialog(true)}>
          Generate New Key
        </Button>
      </Box>
      
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Organization</TableCell>
              <TableCell>Plan</TableCell>
              <TableCell>Rate Limit</TableCell>
              <TableCell>Total Requests</TableCell>
              <TableCell>Monthly Requests</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {apiKeys.map((key) => (
              <TableRow key={key.id}>
                <TableCell>{key.name}</TableCell>
                <TableCell>{key.organization}</TableCell>
                <TableCell>{key.plan}</TableCell>
                <TableCell>{key.rateLimit}/min</TableCell>
                <TableCell>{key.totalRequests}</TableCell>
                <TableCell>{key.monthlyRequests}</TableCell>
                <TableCell>
                  <Chip
                    label={key.active ? 'Active' : 'Inactive'}
                    color={key.active ? 'success' : 'default'}
                  />
                </TableCell>
                <TableCell>
                  <Button
                    size="small"
                    onClick={() => api.apiKeyService.updateStatus(key.id, !key.active)}
                  >
                    {key.active ? 'Deactivate' : 'Activate'}
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
        <DialogTitle>Generate New API Key</DialogTitle>
        <DialogContent>
          <TextField
            label="Name"
            fullWidth
            margin="normal"
            value={newKey.name}
            onChange={(e) => setNewKey({ ...newKey, name: e.target.value })}
          />
          <TextField
            label="Organization"
            fullWidth
            margin="normal"
            value={newKey.organization}
            onChange={(e) => setNewKey({ ...newKey, organization: e.target.value })}
          />
          <TextField
            label="Plan"
            select
            fullWidth
            margin="normal"
            value={newKey.plan}
            onChange={(e) => setNewKey({ ...newKey, plan: e.target.value })}
          >
            <option value="FREE">FREE</option>
            <option value="BASIC">BASIC</option>
            <option value="PREMIUM">PREMIUM</option>
            <option value="ENTERPRISE">ENTERPRISE</option>
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={handleGenerateKey} variant="contained">Generate</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default ApiKeys;
