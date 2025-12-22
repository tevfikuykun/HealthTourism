/**
 * Wagmi Configuration for RainbowKit
 * Polygon Network (Mainnet & Mumbai Testnet) support
 */
import { getDefaultConfig } from '@rainbow-me/rainbowkit';
import { polygon, polygonMumbai } from 'wagmi/chains';
import { createConfig, http } from 'wagmi';
import { injected, metaMask } from 'wagmi/connectors';

// WalletConnect Project ID - Get from https://cloud.walletconnect.com
// If not set, use a minimal config (WalletConnect features will be limited)
const WALLETCONNECT_PROJECT_ID = import.meta.env.VITE_WALLETCONNECT_PROJECT_ID;

// Always create a wagmi config - use minimal config if no project ID
export const wagmiConfig = WALLETCONNECT_PROJECT_ID 
  ? getDefaultConfig({
      appName: 'Health Tourism Platform',
      projectId: WALLETCONNECT_PROJECT_ID,
      chains: [
        polygon, // Polygon Mainnet (Chain ID: 137)
        polygonMumbai, // Polygon Mumbai Testnet (Chain ID: 80001) - for development
      ],
      ssr: false, // Disable SSR for better compatibility
    })
  : createConfig({
      chains: [polygon, polygonMumbai],
      connectors: [
        injected(),
        metaMask(),
      ],
      transports: {
        [polygon.id]: http(),
        [polygonMumbai.id]: http(),
      },
      ssr: false,
    }); // Minimal config without WalletConnect if no project ID

// Polygon Mainnet Chain Info
export const polygonChain = {
  id: 137,
  name: 'Polygon',
  network: 'polygon',
  nativeCurrency: {
    decimals: 18,
    name: 'MATIC',
    symbol: 'MATIC',
  },
  rpcUrls: {
    default: {
      http: ['https://polygon-rpc.com'],
    },
    public: {
      http: ['https://polygon-rpc.com'],
    },
  },
  blockExplorers: {
    default: {
      name: 'PolygonScan',
      url: 'https://polygonscan.com',
    },
  },
};

// Health Token Contract Address (Polygon)
export const HEALTH_TOKEN_CONTRACT = import.meta.env.VITE_HEALTH_TOKEN_CONTRACT || '0x...';

