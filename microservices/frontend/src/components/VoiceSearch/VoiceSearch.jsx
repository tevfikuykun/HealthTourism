// src/components/VoiceSearch/VoiceSearch.jsx
import React, { useState, useEffect } from 'react';
import { IconButton, Tooltip, Box } from '@mui/material';
import MicIcon from '@mui/icons-material/Mic';
import MicOffIcon from '@mui/icons-material/MicOff';

export default function VoiceSearch({ onResult }) {
    const [isListening, setIsListening] = useState(false);
    const [recognition, setRecognition] = useState(null);

    useEffect(() => {
        // Web Speech API kontrolü
        if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
            const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
            const recognitionInstance = new SpeechRecognition();
            
            recognitionInstance.continuous = false;
            recognitionInstance.interimResults = false;
            recognitionInstance.lang = 'tr-TR'; // Türkçe
            
            recognitionInstance.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                if (onResult) {
                    onResult(transcript);
                }
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
    }, [onResult]);

    const toggleListening = () => {
        if (!recognition) {
            alert('Tarayıcınız sesli aramayı desteklemiyor');
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
        <Tooltip title={isListening ? "Dinlemeyi Durdur" : "Sesli Arama"}>
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

