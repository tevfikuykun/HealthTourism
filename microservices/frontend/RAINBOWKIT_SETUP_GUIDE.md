# 🌈 RainbowKit Entegrasyon Rehberi

## ✅ Tamamlanan Entegrasyonlar

### 1. Package Dependencies
- ✅ `@rainbow-me/rainbowkit` - Wallet connection UI
- ✅ `wagmi` - React hooks for Ethereum
- ✅ `viem` - TypeScript Ethereum library

### 2. Configuration
- ✅ `src/config/wagmi.js` - Wagmi & RainbowKit config
- ✅ Polygon Mainnet & Mumbai Testnet support
- ✅ WalletConnect Cloud integration

### 3. App.jsx Integration
- ✅ `WagmiProvider` - Web3 provider
- ✅ `RainbowKitProvider` - Wallet UI provider
- ✅ Theme integration

### 4. HealthWallet.jsx Integration
- ✅ `ConnectButton` - Wallet connection button
- ✅ Wallet info display (address, balance)
- ✅ Copy address functionality
- ✅ Glassmorphism styling

---

## 🚀 Kurulum Adımları

### 1. Paketleri Yükle

```bash
cd microservices/frontend
npm install @rainbow-me/rainbowkit wagmi viem
```

### 2. WalletConnect Project ID Al

1. https://cloud.walletconnect.com adresine git
2. Yeni proje oluştur
3. Project ID'yi kopyala
4. `.env` dosyasına ekle:

```env
VITE_WALLETCONNECT_PROJECT_ID=your-project-id-here
```

### 3. Environment Variables

`.env` dosyasına ekle:

```env
# WalletConnect
VITE_WALLETCONNECT_PROJECT_ID=your-project-id

# Health Token Contract (Polygon)
VITE_HEALTH_TOKEN_CONTRACT=0x...
```

---

## 📱 Kullanım

### HealthWallet Sayfasında

RainbowKit Connect Button otomatik olarak:
- ✅ Tüm popüler cüzdanları gösterir (MetaMask, WalletConnect, Coinbase, etc.)
- ✅ Polygon network'ü otomatik algılar
- ✅ Cüzdan bakiyesini gösterir
- ✅ Network değiştirme desteği

### Wallet Bağlantısı

1. Kullanıcı "Connect Wallet" butonuna tıklar
2. RainbowKit modal açılır
3. Kullanıcı cüzdanını seçer
4. Cüzdan onayı beklenir
5. Bağlantı başarılı olunca wallet bilgileri gösterilir

---

## 🎨 Özelleştirme

### Connect Button Styling

`HealthWallet.jsx` içinde:

```jsx
<Box
  sx={{
    '& button': {
      borderRadius: '12px !important',
      fontWeight: 700,
      // Custom styles
    },
  }}
>
  <ConnectButton />
</Box>
```

### Wallet Info Card

Bağlı cüzdan bilgileri:
- Wallet name (MetaMask, WalletConnect, etc.)
- Truncated address (0x1234...5678)
- Copy button
- Native balance (MATIC)

---

## 🔧 Gelişmiş Özellikler

### 1. Network Switching

Kullanıcı yanlış network'teyse otomatik uyarı:
- Polygon Mainnet'e geçiş önerisi
- Network değiştirme butonu

### 2. Balance Display

Gerçek zamanlı bakiye gösterimi:
- Native token (MATIC)
- Health Token (HT) - contract entegrasyonu gerekli

### 3. Transaction Signing

Blockchain işlemleri için:
```javascript
import { useWriteContract, useWaitForTransactionReceipt } from 'wagmi';

const { writeContract, data: hash } = useWriteContract();
const { isLoading, isSuccess } = useWaitForTransactionReceipt({ hash });

// Health Token transfer
writeContract({
  address: HEALTH_TOKEN_CONTRACT,
  abi: healthTokenABI,
  functionName: 'transfer',
  args: [toAddress, amount],
});
```

---

## 📋 Desteklenen Cüzdanlar

RainbowKit otomatik olarak şu cüzdanları destekler:

### Browser Wallets
- ✅ MetaMask
- ✅ Coinbase Wallet
- ✅ Brave Wallet
- ✅ Trust Wallet

### Mobile Wallets (WalletConnect)
- ✅ MetaMask Mobile
- ✅ Trust Wallet
- ✅ Rainbow Wallet
- ✅ Coinbase Wallet Mobile
- ✅ 100+ diğer WalletConnect uyumlu cüzdan

### Hardware Wallets
- ✅ Ledger
- ✅ Trezor

---

## 🐛 Troubleshooting

### "Project ID is required" Hatası

**Çözüm:** `.env` dosyasına `VITE_WALLETCONNECT_PROJECT_ID` ekle

### Cüzdan Bağlanmıyor

**Kontrol:**
1. Browser extension yüklü mü? (MetaMask, etc.)
2. Network doğru mu? (Polygon Mainnet)
3. WalletConnect Project ID geçerli mi?

### Network Mismatch

**Çözüm:** Kullanıcıya Polygon'a geçmesi için uyarı göster:
```javascript
import { useSwitchChain } from 'wagmi';

const { switchChain } = useSwitchChain();

if (chainId !== 137) {
  switchChain({ chainId: 137 }); // Polygon Mainnet
}
```

---

## 🎯 Sonraki Adımlar

1. ✅ Health Token contract entegrasyonu
2. ✅ Token transfer functionality
3. ✅ Transaction history
4. ✅ ENS (Ethereum Name Service) support
5. ✅ Multi-chain support (Ethereum, BSC)

---

## 📚 Kaynaklar

- [RainbowKit Docs](https://www.rainbowkit.com/docs/introduction)
- [Wagmi Docs](https://wagmi.sh/)
- [Viem Docs](https://viem.sh/)
- [WalletConnect Cloud](https://cloud.walletconnect.com/)



