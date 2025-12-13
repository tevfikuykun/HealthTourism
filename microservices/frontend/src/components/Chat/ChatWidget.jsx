import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  Paper,
  TextField,
  IconButton,
  Typography,
  Avatar,
  Fab,
  Slide,
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import CloseIcon from '@mui/icons-material/Close';
import ChatIcon from '@mui/icons-material/Chat';
import { useAuth } from '../../hooks/useAuth';

export default function ChatWidget() {
  const { isAuthenticated } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async () => {
    if (!inputMessage.trim()) return;

    const newMessage = {
      id: Date.now(),
      text: inputMessage,
      sender: 'user',
      timestamp: new Date(),
    };

    setMessages([...messages, newMessage]);
    setInputMessage('');

    // TODO: Chat API entegrasyonu
    // Simüle edilmiş bot yanıtı
    setTimeout(() => {
      const botMessage = {
        id: Date.now() + 1,
        text: 'Mesajınız alındı. En kısa sürede size dönüş yapacağız.',
        sender: 'bot',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, botMessage]);
    }, 1000);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <>
      <Slide direction="up" in={isOpen} mountOnEnter unmountOnExit>
        <Paper
          sx={{
            position: 'fixed',
            bottom: 80,
            right: 20,
            width: 350,
            height: 500,
            display: 'flex',
            flexDirection: 'column',
            zIndex: 1300,
            boxShadow: 4,
          }}
        >
          <Box
            sx={{
              bgcolor: 'primary.main',
              color: 'white',
              p: 2,
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <Typography variant="h6">Destek</Typography>
            <IconButton size="small" onClick={() => setIsOpen(false)} sx={{ color: 'white' }}>
              <CloseIcon />
            </IconButton>
          </Box>

          <Box
            sx={{
              flex: 1,
              overflowY: 'auto',
              p: 2,
              bgcolor: 'grey.50',
            }}
          >
            {messages.length === 0 ? (
              <Box sx={{ textAlign: 'center', py: 4 }}>
                <Typography variant="body2" color="text.secondary">
                  Merhaba! Size nasıl yardımcı olabiliriz?
                </Typography>
              </Box>
            ) : (
              messages.map((message) => (
                <Box
                  key={message.id}
                  sx={{
                    display: 'flex',
                    justifyContent: message.sender === 'user' ? 'flex-end' : 'flex-start',
                    mb: 2,
                  }}
                >
                  <Box
                    sx={{
                      maxWidth: '70%',
                      p: 1.5,
                      borderRadius: 2,
                      bgcolor: message.sender === 'user' ? 'primary.main' : 'white',
                      color: message.sender === 'user' ? 'white' : 'text.primary',
                      boxShadow: 1,
                    }}
                  >
                    <Typography variant="body2">{message.text}</Typography>
                    <Typography variant="caption" sx={{ opacity: 0.7, display: 'block', mt: 0.5 }}>
                      {message.timestamp.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })}
                    </Typography>
                  </Box>
                </Box>
              ))
            )}
            <div ref={messagesEndRef} />
          </Box>

          <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
            <Box sx={{ display: 'flex', gap: 1 }}>
              <TextField
                fullWidth
                size="small"
                placeholder="Mesajınızı yazın..."
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                disabled={!isAuthenticated}
              />
              <IconButton color="primary" onClick={handleSend} disabled={!inputMessage.trim() || !isAuthenticated}>
                <SendIcon />
              </IconButton>
            </Box>
            {!isAuthenticated && (
              <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                Mesaj göndermek için giriş yapın
              </Typography>
            )}
          </Box>
        </Paper>
      </Slide>

      <Fab
        color="primary"
        aria-label="chat"
        sx={{
          position: 'fixed',
          bottom: 20,
          right: 20,
          zIndex: 1300,
        }}
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? <CloseIcon /> : <ChatIcon />}
      </Fab>
    </>
  );
}

