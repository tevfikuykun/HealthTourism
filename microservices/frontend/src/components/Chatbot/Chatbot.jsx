// src/components/Chatbot/Chatbot.jsx
import React, { useState, useRef, useEffect } from 'react';
import {
  Box, Paper, TextField, IconButton, Typography, Avatar,
  List, ListItem, ListItemText
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import SmartToyIcon from '@mui/icons-material/SmartToy';
import PersonIcon from '@mui/icons-material/Person';
import CloseIcon from '@mui/icons-material/Close';
import { useTranslation } from 'react-i18next';

const Chatbot = () => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const messagesEndRef = useRef(null);

  // Initialize welcome message when component mounts
  useEffect(() => {
    if (messages.length === 0) {
      setMessages([
        {
          id: 1,
          text: t('chatbotWelcome', 'Merhaba! Size nasıl yardımcı olabilirim?'),
          sender: 'bot',
          timestamp: new Date(),
        },
      ]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [t]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = () => {
    if (!input.trim()) return;

    const userMessage = {
      id: messages.length + 1,
      text: input,
      sender: 'user',
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    setInput('');

    // Simüle edilmiş bot yanıtı
    setTimeout(() => {
      const botResponse = {
        id: messages.length + 2,
        text: getBotResponse(input),
        sender: 'bot',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, botResponse]);
    }, 1000);
  };

  const getBotResponse = (userInput) => {
    const lowerInput = userInput.toLowerCase();
    
    if (lowerInput.includes('rezervasyon') || lowerInput.includes('randevu') || lowerInput.includes('reservation') || lowerInput.includes('appointment')) {
      return t('chatbotReservationResponse', 'Rezervasyon yapmak için lütfen rezervasyon sayfasını ziyaret edin veya bize telefon numaranızı bırakın.');
    } else if (lowerInput.includes('fiyat') || lowerInput.includes('ücret') || lowerInput.includes('price') || lowerInput.includes('cost')) {
      return t('chatbotPriceResponse', 'Fiyatlar hizmet tipine ve pakete göre değişmektedir. Detaylı bilgi için paketler sayfasını inceleyebilirsiniz.');
    } else if (lowerInput.includes('hastane') || lowerInput.includes('doktor') || lowerInput.includes('hospital') || lowerInput.includes('doctor')) {
      return t('chatbotHospitalResponse', 'Hastane ve doktor listemizi görmek için ilgili sayfaları ziyaret edebilirsiniz.');
    } else if (lowerInput.includes('merhaba') || lowerInput.includes('selam') || lowerInput.includes('hello') || lowerInput.includes('hi')) {
      return t('chatbotWelcome', 'Merhaba! Size nasıl yardımcı olabilirim?');
    } else {
      return t('chatbotDefaultResponse', 'Anladım. Daha detaylı bilgi için lütfen iletişim sayfamızı ziyaret edin veya canlı destek ekibimizle iletişime geçin.');
    }
  };

  if (!open) {
    return (
      <Box
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
          zIndex: 1000,
        }}
      >
        <IconButton
          onClick={() => setOpen(true)}
          sx={{
            bgcolor: 'primary.main',
            color: 'white',
            width: 56,
            height: 56,
            '&:hover': {
              bgcolor: 'primary.dark',
            },
          }}
        >
          <SmartToyIcon />
        </IconButton>
      </Box>
    );
  }

  return (
    <Paper
      sx={{
        position: 'fixed',
        bottom: 24,
        right: 24,
        width: 350,
        height: 500,
        display: 'flex',
        flexDirection: 'column',
        zIndex: 1000,
        boxShadow: 3,
      }}
    >
      <Box
        sx={{
          p: 2,
          bgcolor: 'primary.main',
          color: 'white',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <SmartToyIcon />
          <Typography variant="h6">{t('chatbotAssistant', 'Yardımcı Asistan')}</Typography>
        </Box>
        <IconButton
          size="small"
          onClick={() => setOpen(false)}
          sx={{ color: 'white' }}
        >
          <CloseIcon />
        </IconButton>
      </Box>

      <Box sx={{ flexGrow: 1, overflow: 'auto', p: 2 }}>
        <List>
          {messages.map((message) => (
            <ListItem
              key={message.id}
              sx={{
                flexDirection: message.sender === 'user' ? 'row-reverse' : 'row',
                alignItems: 'flex-start',
              }}
            >
              <Avatar
                sx={{
                  bgcolor: message.sender === 'user' ? 'primary.main' : 'grey.500',
                  width: 32,
                  height: 32,
                }}
              >
                {message.sender === 'user' ? <PersonIcon /> : <SmartToyIcon />}
              </Avatar>
              <ListItemText
                primary={message.text}
                secondary={new Date(message.timestamp).toLocaleTimeString()}
                sx={{
                  ml: message.sender === 'user' ? 0 : 1,
                  mr: message.sender === 'user' ? 1 : 0,
                  textAlign: message.sender === 'user' ? 'right' : 'left',
                }}
              />
            </ListItem>
          ))}
          <div ref={messagesEndRef} />
        </List>
      </Box>

      <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <TextField
            fullWidth
            size="small"
            placeholder={t('typeMessage', 'Mesaj yazın...')}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleSend();
              }
            }}
          />
          <IconButton color="primary" onClick={handleSend}>
            <SendIcon />
          </IconButton>
        </Box>
      </Box>
    </Paper>
  );
};

export default Chatbot;

