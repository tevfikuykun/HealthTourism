import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  Button,
  Divider,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import {
  ExpandMore,
  Security,
  Verified,
  Cloud,
  Lock,
  Visibility,
  Download,
  CheckCircle,
  Warning,
  AccountTree,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * Legal & Transparency Center
 * GDPR/HIPAA uyumunu hastaya kanıtlayan,
 * verisinin nerede saklandığını (IPFS/Blockchain) ve
 * kimlerin eriştiğini (Audit Logs) şeffaf bir şekilde gösteren sayfa
 */
const LegalTransparency = () => {
  const { t } = useTranslation();
  const [expanded, setExpanded] = useState('data-storage');

  // Fetch data storage info
  const { data: dataStorage } = useQuery({
    queryKey: ['data-storage'],
    queryFn: async () => {
      const response = await api.get('/legal/data-storage');
      return response.data;
    },
  });

  // Fetch access logs
  const { data: accessLogs } = useQuery({
    queryKey: ['access-logs'],
    queryFn: async () => {
      const response = await api.get('/legal/access-logs');
      return response.data;
    },
  });

  // Fetch compliance status
  const { data: compliance } = useQuery({
    queryKey: ['compliance'],
    queryFn: async () => {
      const response = await api.get('/legal/compliance');
      return response.data;
    },
  });

  const handleAccordionChange = (panel) => (event, isExpanded) => {
    setExpanded(isExpanded ? panel : false);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('legal.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('legal.subtitle')}
      </Typography>

      {/* Compliance Status */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Verified color="success" sx={{ fontSize: 40 }} />
                <Box>
                  <Typography variant="h6">GDPR</Typography>
                  <Chip label={t('legal.compliant')} color="success" size="small" />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Verified color="success" sx={{ fontSize: 40 }} />
                <Box>
                  <Typography variant="h6">HIPAA</Typography>
                  <Chip label={t('legal.compliant')} color="success" size="small" />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Security color="primary" sx={{ fontSize: 40 }} />
                <Box>
                  <Typography variant="h6">{t('legal.encryption')}</Typography>
                  <Chip label="AES-256-GCM" color="primary" size="small" />
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Accordion Sections */}
      <Box sx={{ mb: 3 }}>
        <Accordion
          expanded={expanded === 'data-storage'}
          onChange={handleAccordionChange('data-storage')}
        >
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Cloud />
              <Typography variant="h6">{t('legal.dataStorage')}</Typography>
            </Box>
          </AccordionSummary>
          <AccordionDetails>
            <List>
              <ListItem>
                <ListItemIcon>
                  <AccountTree />
                </ListItemIcon>
                <ListItemText
                  primary={t('legal.blockchain')}
                  secondary={dataStorage?.blockchain?.network || 'Polygon Mainnet'}
                />
                <Chip label={t('legal.verified')} color="success" size="small" />
              </ListItem>
              <ListItem>
                <ListItemIcon>
                  <Cloud />
                </ListItemIcon>
                <ListItemText
                  primary={t('legal.ipfs')}
                  secondary={dataStorage?.ipfs?.hash || 'N/A'}
                />
                <Chip label={t('legal.decentralized')} color="info" size="small" />
              </ListItem>
              <ListItem>
                <ListItemIcon>
                  <Lock />
                </ListItemIcon>
                <ListItemText
                  primary={t('legal.encryption')}
                  secondary={t('legal.encryptionDesc')}
                />
                <Chip label="AES-256-GCM" color="primary" size="small" />
              </ListItem>
            </List>
          </AccordionDetails>
        </Accordion>

        <Accordion
          expanded={expanded === 'access-logs'}
          onChange={handleAccordionChange('access-logs')}
        >
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Visibility />
              <Typography variant="h6">{t('legal.accessLogs')}</Typography>
            </Box>
          </AccordionSummary>
          <AccordionDetails>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>{t('legal.date')}</TableCell>
                    <TableCell>{t('legal.user')}</TableCell>
                    <TableCell>{t('legal.action')}</TableCell>
                    <TableCell>{t('legal.resource')}</TableCell>
                    <TableCell>{t('legal.status')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {accessLogs?.slice(0, 10).map((log) => (
                    <TableRow key={log.id}>
                      <TableCell>{new Date(log.timestamp).toLocaleString()}</TableCell>
                      <TableCell>{log.userName}</TableCell>
                      <TableCell>{log.action}</TableCell>
                      <TableCell>{log.resource}</TableCell>
                      <TableCell>
                        <Chip
                          label={log.status}
                          color={log.status === 'SUCCESS' ? 'success' : 'error'}
                          size="small"
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <Button
              variant="outlined"
              startIcon={<Download />}
              sx={{ mt: 2 }}
              onClick={() => {
                // Export audit logs
                window.location.href = '/api/legal/access-logs/export';
              }}
            >
              {t('legal.exportLogs')}
            </Button>
          </AccordionDetails>
        </Accordion>

        <Accordion
          expanded={expanded === 'rights'}
          onChange={handleAccordionChange('rights')}
        >
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Security />
              <Typography variant="h6">{t('legal.yourRights')}</Typography>
            </Box>
          </AccordionSummary>
          <AccordionDetails>
            <List>
              <ListItem>
                <ListItemIcon>
                  <Download />
                </ListItemIcon>
                <ListItemText
                  primary={t('legal.rightToExport')}
                  secondary={t('legal.rightToExportDesc')}
                />
                <Button variant="outlined" size="small">
                  {t('legal.export')}
                </Button>
              </ListItem>
              <ListItem>
                <ListItemIcon>
                  <Warning />
                </ListItemIcon>
                <ListItemText
                  primary={t('legal.rightToDelete')}
                  secondary={t('legal.rightToDeleteDesc')}
                />
                <Button variant="outlined" color="error" size="small">
                  {t('legal.delete')}
                </Button>
              </ListItem>
              <ListItem>
                <ListItemIcon>
                  <Visibility />
                </ListItemIcon>
                <ListItemText
                  primary={t('legal.rightToAccess')}
                  secondary={t('legal.rightToAccessDesc')}
                />
                <Button variant="outlined" size="small">
                  {t('legal.view')}
                </Button>
              </ListItem>
            </List>
          </AccordionDetails>
        </Accordion>
      </Box>

      {/* Privacy Notice */}
      <Alert severity="info" sx={{ mb: 3 }}>
        <Typography variant="body2">
          {t('legal.privacyNotice')}
        </Typography>
      </Alert>
    </Container>
  );
};

export default LegalTransparency;



