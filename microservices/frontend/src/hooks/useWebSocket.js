// src/hooks/useWebSocket.js
import { useEffect, useState } from 'react';
import { getWebSocketClient } from '../utils/websocket';

export const useWebSocket = (url) => {
  const [isConnected, setIsConnected] = useState(false);
  const [messages, setMessages] = useState([]);
  const [wsClient, setWsClient] = useState(null);

  useEffect(() => {
    const client = getWebSocketClient(url);
    setWsClient(client);

    client.on('open', () => {
      setIsConnected(true);
    });

    client.on('close', () => {
      setIsConnected(false);
    });

    client.on('message', (data) => {
      setMessages(prev => [...prev, data]);
    });

    return () => {
      client.disconnect();
    };
  }, [url]);

  const sendMessage = (message) => {
    if (wsClient) {
      wsClient.send(message);
    }
  };

  return {
    isConnected,
    messages,
    sendMessage,
  };
};

