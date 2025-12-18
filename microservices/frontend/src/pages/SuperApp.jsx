import React, { useState, useEffect } from 'react';
import { Box, Container, Grid, Paper, Typography, Fab, Chip, CircularProgress } from '@mui/material';
import { Chat as ChatIcon, Wallet as WalletIcon, Assessment as AssessmentIcon, Translate as TranslateIcon } from '@mui/icons-material';
import { aiHealthCompanionService, healthWalletService, patientRiskScoringService, liveTranslationService } from '../services/api';

/**
 * Super-App: AI Health Companion Merkezli Minimalist Mobil Uygulama
 * Tüm mikroservisleri tek bir arayüzden erişilebilir hale getirir
 */
const SuperApp = () => {
  const [activeView, setActiveView] = useState('companion'); // companion, wallet, risk, translation
  const [userId] = useState(1); // In production, get from auth context
  const [loading, setLoading] = useState(false);
  
  // AI Companion State
  const [conversations, setConversations] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState('');
  
  // Health Wallet State
  const [walletData, setWalletData] = useState(null);
  const [qrCodeImage, setQrCodeImage] = useState(null);
  
  // Risk Score State
  const [riskScore, setRiskScore] = useState(null);
  
  // Translation State
  const [translationSession, setTranslationSession] = useState(null);

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setLoading(true);
    try {
      // Load wallet data
      const wallet = await healthWalletService.getCompleteData(userId);
      setWalletData(wallet.data);
      setQrCodeImage(wallet.data?.qrCodeImage);
      
      // Load latest risk score
      try {
        const score = await patientRiskScoringService.getLatestScore(userId, 1);
        setRiskScore(score.data);
      } catch (e) {
        console.log('No risk score available');
      }
      
      // Load AI companion conversations
      const convs = await aiHealthCompanionService.getByUser(userId);
      setConversations(convs.data || []);
    } catch (error) {
      console.error('Failed to load initial data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAskAI = async () => {
    if (!currentQuestion.trim()) return;
    
    setLoading(true);
    try {
      const response = await aiHealthCompanionService.askQuestion({
        userId,
        reservationId: 1,
        question: currentQuestion
      });
      
      setConversations(prev => [response.data, ...prev]);
      setCurrentQuestion('');
    } catch (error) {
      console.error('Failed to ask AI:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderCompanionView = () => (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        AI Health Companion
      </Typography>
      <Typography variant="body2" color="text.secondary" gutterBottom>
        7/24 Digital Nurse - Ask anything about your health
      </Typography>
      
      {/* Conversation History */}
      <Box sx={{ mt: 2, mb: 2, maxHeight: '400px', overflowY: 'auto' }}>
        {conversations.map((conv, idx) => (
          <Paper key={idx} sx={{ p: 2, mb: 1 }}>
            <Typography variant="body2" fontWeight="bold">You:</Typography>
            <Typography variant="body2" sx={{ mb: 1 }}>{conv.userQuestion}</Typography>
            <Typography variant="body2" fontWeight="bold" color="primary">AI:</Typography>
            <Typography variant="body2">{conv.aiResponse}</Typography>
            {conv.urgencyLevel === 'HIGH' && (
              <Chip label="⚠️ Urgent" color="error" size="small" sx={{ mt: 1 }} />
            )}
          </Paper>
        ))}
      </Box>
      
      {/* Question Input */}
      <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
        <input
          type="text"
          value={currentQuestion}
          onChange={(e) => setCurrentQuestion(e.target.value)}
          placeholder="Ask your health question..."
          style={{ flex: 1, padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
          onKeyPress={(e) => e.key === 'Enter' && handleAskAI()}
        />
        <Fab color="primary" size="small" onClick={handleAskAI} disabled={loading}>
          <ChatIcon />
        </Fab>
      </Box>
    </Box>
  );

  const renderWalletView = () => (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Health Wallet
      </Typography>
      
      {walletData && (
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="subtitle2">QR Code</Typography>
              {qrCodeImage && (
                <img src={qrCodeImage} alt="Health Wallet QR Code" style={{ maxWidth: '200px', marginTop: '10px' }} />
              )}
            </Paper>
          </Grid>
          
          <Grid item xs={6}>
            <Paper sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="h4">{walletData.documentCount || 0}</Typography>
              <Typography variant="caption">Documents</Typography>
            </Paper>
          </Grid>
          
          <Grid item xs={6}>
            <Paper sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="h4">{walletData.iotDataPointCount || 0}</Typography>
              <Typography variant="caption">IoT Data Points</Typography>
            </Paper>
          </Grid>
          
          <Grid item xs={12}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="subtitle2">Recovery Score</Typography>
              <Typography variant="h3" color="primary">
                {walletData.currentRecoveryScore || 'N/A'}
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      )}
    </Box>
  );

  const renderRiskScoreView = () => (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Recovery Score
      </Typography>
      
      {riskScore ? (
        <Box sx={{ mt: 2 }}>
          <Paper sx={{ p: 3, textAlign: 'center', mb: 2 }}>
            <Typography variant="h2" color="primary">
              {riskScore.recoveryScore}/100
            </Typography>
            <Chip 
              label={riskScore.scoreCategory} 
              color={riskScore.scoreCategory === 'EXCELLENT' ? 'success' : 
                     riskScore.scoreCategory === 'GOOD' ? 'info' : 
                     riskScore.scoreCategory === 'FAIR' ? 'warning' : 'error'}
              sx={{ mt: 1 }}
            />
          </Paper>
          
          {riskScore.scoreExplanation && (
            <Paper sx={{ p: 2, mt: 2 }}>
              <Typography variant="subtitle2" fontWeight="bold">Explanation:</Typography>
              <Typography variant="body2" sx={{ mt: 1 }}>
                {riskScore.scoreExplanation}
              </Typography>
            </Paper>
          )}
          
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={6}>
              <Paper sx={{ p: 2 }}>
                <Typography variant="caption">IoT Score</Typography>
                <Typography variant="h6">{riskScore.iotDataScore}</Typography>
              </Paper>
            </Grid>
            <Grid item xs={6}>
              <Paper sx={{ p: 2 }}>
                <Typography variant="caption">Compliance</Typography>
                <Typography variant="h6">{riskScore.complianceScore}</Typography>
              </Paper>
            </Grid>
          </Grid>
        </Box>
      ) : (
        <Typography variant="body2" color="text.secondary">
          No risk score available. Complete a reservation to get your recovery score.
        </Typography>
      )}
    </Box>
  );

  const renderTranslationView = () => (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" gutterBottom>
        Live Translation
      </Typography>
      <Typography variant="body2" color="text.secondary">
        Real-time translation during consultations
      </Typography>
      
      <Paper sx={{ p: 2, mt: 2 }}>
        <Typography variant="body2">
          Start a translation session from your consultation page.
        </Typography>
      </Paper>
    </Box>
  );

  return (
    <Container maxWidth="sm" sx={{ pb: 10 }}>
      {loading && (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
          <CircularProgress />
        </Box>
      )}
      
      {/* Main Content Area */}
      <Paper sx={{ mt: 2, minHeight: '500px' }}>
        {activeView === 'companion' && renderCompanionView()}
        {activeView === 'wallet' && renderWalletView()}
        {activeView === 'risk' && renderRiskScoreView()}
        {activeView === 'translation' && renderTranslationView()}
      </Paper>
      
      {/* Bottom Navigation (Super-App Style) */}
      <Paper 
        sx={{ 
          position: 'fixed', 
          bottom: 0, 
          left: 0, 
          right: 0, 
          p: 1,
          borderTop: '1px solid #e0e0e0',
          zIndex: 1000
        }}
      >
        <Grid container spacing={1}>
          <Grid item xs={3}>
            <Box 
              sx={{ 
                textAlign: 'center', 
                p: 1, 
                cursor: 'pointer',
                bgcolor: activeView === 'companion' ? 'primary.light' : 'transparent',
                borderRadius: 1
              }}
              onClick={() => setActiveView('companion')}
            >
              <ChatIcon color={activeView === 'companion' ? 'primary' : 'action'} />
              <Typography variant="caption" display="block">AI</Typography>
            </Box>
          </Grid>
          <Grid item xs={3}>
            <Box 
              sx={{ 
                textAlign: 'center', 
                p: 1, 
                cursor: 'pointer',
                bgcolor: activeView === 'wallet' ? 'primary.light' : 'transparent',
                borderRadius: 1
              }}
              onClick={() => setActiveView('wallet')}
            >
              <WalletIcon color={activeView === 'wallet' ? 'primary' : 'action'} />
              <Typography variant="caption" display="block">Wallet</Typography>
            </Box>
          </Grid>
          <Grid item xs={3}>
            <Box 
              sx={{ 
                textAlign: 'center', 
                p: 1, 
                cursor: 'pointer',
                bgcolor: activeView === 'risk' ? 'primary.light' : 'transparent',
                borderRadius: 1
              }}
              onClick={() => setActiveView('risk')}
            >
              <AssessmentIcon color={activeView === 'risk' ? 'primary' : 'action'} />
              <Typography variant="caption" display="block">Score</Typography>
            </Box>
          </Grid>
          <Grid item xs={3}>
            <Box 
              sx={{ 
                textAlign: 'center', 
                p: 1, 
                cursor: 'pointer',
                bgcolor: activeView === 'translation' ? 'primary.light' : 'transparent',
                borderRadius: 1
              }}
              onClick={() => setActiveView('translation')}
            >
              <TranslateIcon color={activeView === 'translation' ? 'primary' : 'action'} />
              <Typography variant="caption" display="block">Translate</Typography>
            </Box>
          </Grid>
        </Grid>
      </Paper>
    </Container>
  );
};

export default SuperApp;
