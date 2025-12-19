import React, { useState } from 'react';
import {
  Box,
  Container,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
  LinearProgress,
  Alert,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Tabs,
  Tab,
} from '@mui/material';
import {
  Description,
  Download,
  Visibility,
  Verified,
  Lock,
  CloudDownload,
  PictureAsPdf,
  Image,
  Assignment,
  Security,
  CheckCircle,
  Warning,
} from '@mui/icons-material';
import { useTranslation } from 'react-i18next';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';

/**
 * Medical Passport & Documents
 * IPFS üzerindeki raporları, pasaport kopyasını ve blockchain mühürlü
 * sigorta poliçesini görüntüleyip indirebileceği güvenli alan
 */
const MedicalPassport = () => {
  const { t } = useTranslation();
  const [selectedTab, setSelectedTab] = useState(0);
  const [selectedDocument, setSelectedDocument] = useState(null);
  const [openViewer, setOpenViewer] = useState(false);

  // Documents fetch
  const { data: documents, isLoading } = useQuery({
    queryKey: ['medical-documents'],
    queryFn: async () => {
      const response = await api.get('/api/medical-documents/user/me');
      return response.data;
    },
  });

  // Blockchain verification
  const { data: blockchainRecords } = useQuery({
    queryKey: ['blockchain-records'],
    queryFn: async () => {
      const response = await api.get('/api/blockchain/user/me');
      return response.data;
    },
  });

  const documentCategories = {
    medicalReports: documents?.filter((doc) => doc.type === 'MEDICAL_REPORT') || [],
    passport: documents?.filter((doc) => doc.type === 'PASSPORT') || [],
    insurance: documents?.filter((doc) => doc.type === 'INSURANCE') || [],
    prescriptions: documents?.filter((doc) => doc.type === 'PRESCRIPTION') || [],
    labResults: documents?.filter((doc) => doc.type === 'LAB_RESULT') || [],
  };

  const handleDownload = async (document) => {
    try {
      const response = await api.get(`/api/medical-documents/${document.id}/download`, {
        responseType: 'blob',
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `${document.name}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Download failed:', error);
    }
  };

  const handleView = async (document) => {
    setSelectedDocument(document);
    setOpenViewer(true);
  };

  const verifyBlockchain = (documentId) => {
    const record = blockchainRecords?.find((r) => r.recordId === documentId);
    return record?.isValid || false;
  };

  const getDocumentIcon = (type) => {
    const icons = {
      MEDICAL_REPORT: <Assignment />,
      PASSPORT: <Description />,
      INSURANCE: <Security />,
      PRESCRIPTION: <Description />,
      LAB_RESULT: <Image />,
    };
    return icons[type] || <Description />;
  };

  const renderDocumentCard = (document) => {
    const isVerified = verifyBlockchain(document.id);
    const ipfsHash = document.ipfsHash || document.dataReference?.replace('ipfs://', '');

    return (
      <Card key={document.id} sx={{ mb: 2 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
            <Box sx={{ display: 'flex', gap: 2, flex: 1 }}>
              <Box
                sx={{
                  width: 48,
                  height: 48,
                  borderRadius: 2,
                  bgcolor: 'primary.light',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: 'primary.contrastText',
                }}
              >
                {getDocumentIcon(document.type)}
              </Box>
              <Box sx={{ flex: 1 }}>
                <Typography variant="h6" gutterBottom>
                  {document.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  {new Date(document.createdAt).toLocaleDateString()}
                </Typography>
                {ipfsHash && (
                  <Chip
                    icon={<CloudDownload />}
                    label={`IPFS: ${ipfsHash.substring(0, 12)}...`}
                    size="small"
                    sx={{ mt: 1, mr: 1 }}
                  />
                )}
                {isVerified && (
                  <Chip
                    icon={<Verified />}
                    label={t('documents.blockchainVerified')}
                    color="success"
                    size="small"
                    sx={{ mt: 1 }}
                  />
                )}
              </Box>
            </Box>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <IconButton onClick={() => handleView(document)} color="primary">
                <Visibility />
              </IconButton>
              <IconButton onClick={() => handleDownload(document)} color="primary">
                <Download />
              </IconButton>
            </Box>
          </Box>
        </CardContent>
      </Card>
    );
  };

  if (isLoading) {
    return (
      <Box sx={{ p: 3 }}>
        <LinearProgress />
      </Box>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {t('documents.title')}
      </Typography>
      <Typography variant="body1" color="text.secondary" paragraph>
        {t('documents.subtitle')}
      </Typography>

      {/* Security Notice */}
      <Alert severity="info" sx={{ mb: 3 }} icon={<Lock />}>
        <Typography variant="body2">
          {t('documents.securityNotice')}
        </Typography>
      </Alert>

      {/* Tabs */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={selectedTab} onChange={(e, v) => setSelectedTab(v)}>
          <Tab
            label={`${t('documents.medicalReports')} (${documentCategories.medicalReports.length})`}
          />
          <Tab label={`${t('documents.passport')} (${documentCategories.passport.length})`} />
          <Tab
            label={`${t('documents.insurance')} (${documentCategories.insurance.length})`}
          />
          <Tab
            label={`${t('documents.prescriptions')} (${documentCategories.prescriptions.length})`}
          />
          <Tab
            label={`${t('documents.labResults')} (${documentCategories.labResults.length})`}
          />
        </Tabs>
      </Box>

      {/* Documents List */}
      <Box>
        {selectedTab === 0 &&
          documentCategories.medicalReports.map((doc) => renderDocumentCard(doc))}
        {selectedTab === 1 &&
          documentCategories.passport.map((doc) => renderDocumentCard(doc))}
        {selectedTab === 2 &&
          documentCategories.insurance.map((doc) => renderDocumentCard(doc))}
        {selectedTab === 3 &&
          documentCategories.prescriptions.map((doc) => renderDocumentCard(doc))}
        {selectedTab === 4 &&
          documentCategories.labResults.map((doc) => renderDocumentCard(doc))}
      </Box>

      {/* Blockchain Verification Summary */}
      <Card sx={{ mt: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            {t('documents.blockchainVerification')}
          </Typography>
          <List>
            {blockchainRecords?.map((record) => (
              <ListItem key={record.id}>
                <ListItemIcon>
                  {record.isValid ? (
                    <CheckCircle color="success" />
                  ) : (
                    <Warning color="error" />
                  )}
                </ListItemIcon>
                <ListItemText
                  primary={record.recordType}
                  secondary={`Hash: ${record.blockHash.substring(0, 16)}...`}
                />
                <Chip
                  label={record.isValid ? t('documents.verified') : t('documents.unverified')}
                  color={record.isValid ? 'success' : 'error'}
                  size="small"
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>

      {/* Document Viewer Dialog */}
      <Dialog
        open={openViewer}
        onClose={() => setOpenViewer(false)}
        maxWidth="lg"
        fullWidth
      >
        {selectedDocument && (
          <>
            <DialogTitle>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6">{selectedDocument.name}</Typography>
                <Button
                  startIcon={<Download />}
                  onClick={() => handleDownload(selectedDocument)}
                >
                  {t('common.download')}
                </Button>
              </Box>
            </DialogTitle>
            <DialogContent>
              <Box
                sx={{
                  width: '100%',
                  height: '70vh',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  bgcolor: 'grey.100',
                  borderRadius: 2,
                }}
              >
                {selectedDocument.ipfsHash ? (
                  <iframe
                    src={`https://ipfs.io/ipfs/${selectedDocument.ipfsHash}`}
                    style={{ width: '100%', height: '100%', border: 'none' }}
                    title={selectedDocument.name}
                  />
                ) : (
                  <Typography>{t('documents.viewerNotAvailable')}</Typography>
                )}
              </Box>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setOpenViewer(false)}>{t('common.close')}</Button>
            </DialogActions>
          </>
        )}
      </Dialog>
    </Container>
  );
};

export default MedicalPassport;

