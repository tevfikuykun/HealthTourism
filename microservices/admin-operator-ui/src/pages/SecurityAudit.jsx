import React, { useState, useEffect } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
  Typography,
  Chip,
} from '@mui/material';
import { api } from '../services/api';

function SecurityAudit() {
  const [auditLogs, setAuditLogs] = useState([]);

  useEffect(() => {
    fetchAuditLogs();
  }, []);

  const fetchAuditLogs = async () => {
    try {
      const response = await api.securityAuditService.getAuditLogs();
      setAuditLogs(response.data || []);
    } catch (error) {
      console.error('Error fetching audit logs:', error);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Security Audit Logs
      </Typography>
      
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>User ID</TableCell>
              <TableCell>Resource</TableCell>
              <TableCell>Action</TableCell>
              <TableCell>IP Address</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Authorized</TableCell>
              <TableCell>Timestamp</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {auditLogs.map((log) => (
              <TableRow key={log.id}>
                <TableCell>{log.userId}</TableCell>
                <TableCell>{log.resourceType}</TableCell>
                <TableCell>{log.action}</TableCell>
                <TableCell>{log.ipAddress}</TableCell>
                <TableCell>
                  <Chip
                    label={log.status}
                    color={log.status === 'ALLOWED' ? 'success' : 'error'}
                  />
                </TableCell>
                <TableCell>
                  <Chip
                    label={log.authorized ? 'Yes' : 'No'}
                    color={log.authorized ? 'success' : 'error'}
                  />
                </TableCell>
                <TableCell>{new Date(log.timestamp).toLocaleString()}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}

export default SecurityAudit;
