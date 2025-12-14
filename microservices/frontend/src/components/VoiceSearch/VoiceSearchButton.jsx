// src/components/VoiceSearch/VoiceSearchButton.jsx
import React, { useState, useEffect } from 'react';
import { IconButton, Tooltip } from '@mui/material';
import MicIcon from '@mui/icons-material/Mic';
import MicOffIcon from '@mui/icons-material/MicOff';
import { useTranslation } from 'react-i18next';

export default function VoiceSearchButton({ onTranscript }) {
  const { t } = useTranslation();
    const [isListening, setIsListening] = useState(false);
    const [recognition, setRecognition] = useState(null);

    useEffect(() => {
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            const recognitionInstance = new SpeechRecognition();
            
            recognitionInstance.continuous = false;
            recognitionInstance.interimResults = false;
            recognitionInstance.lang = 'tr-TR';
            
            recognitionInstance.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                if (onTranscript) {
                    onTranscript(transcript);
                }
                setIsListening(false);
            };
            
            recognitionInstance.onerror = (event) => {
                console.error('Speech recognition error:', event.error);
                setIsListening(false);
            };
            
            recognitionInstance.onend = () => {
                setIsListening(false);
            };
            
            setRecognition(recognitionInstance);
        }
    }, [onTranscript]);

    const toggleListening = () => {
        if (!recognition) {
            alert(t('voiceSearchNotSupported', 'Tarayıcınız sesli aramayı desteklemiyor. Chrome veya Edge kullanın.'));
            return;
        }

        if (isListening) {
            recognition.stop();
            setIsListening(false);
        } else {
            recognition.start();
            setIsListening(true);
        }
    };

    return (
        <Tooltip title={isListening ? t('stopListening', 'Dinlemeyi Durdur') : t('voiceSearch', 'Sesli Arama')}>
            <IconButton
                onClick={toggleListening}
                color={isListening ? "error" : "default"}
                sx={{
                    bgcolor: isListening ? 'error.light' : 'transparent',
                    '&:hover': {
                        bgcolor: isListening ? 'error.main' : 'action.hover'
                    }
                }}
            >
                {isListening ? <MicOffIcon /> : <MicIcon />}
            </IconButton>
        </Tooltip>
    );
}

