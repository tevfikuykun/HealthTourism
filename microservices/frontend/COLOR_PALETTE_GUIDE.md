# Deep-Trust Color Palette Guide

## 🎨 Ana Renk Paleti (The "Deep-Trust" Palette)

### Primary (Ana) - #4F46E5 (Indigo Blue)
**Psikolojik Etki:** Modern teknoloji, Blockchain ve profesyonellik hissi verir.

**Kullanım Alanları:**
- Ana butonlar
- Blockchain işlemleri
- Primary action'lar
- Logo ve branding

**Örnek Kullanım:**
```jsx
// Material-UI
<Button variant="contained" color="primary">Primary Button</Button>

// Tailwind CSS
<button className="bg-primary-600 text-white">Primary Button</button>

// Custom
<Box sx={{ bgcolor: 'primary.main' }}>Content</Box>
```

### Secondary (Vurgu) - #0EA5E9 (Sky Blue)
**Psikolojik Etki:** Sağlık ve temizlik algısını pekiştirir.

**Kullanım Alanları:**
- Secondary butonlar
- Sağlık göstergeleri
- Info mesajları
- Vurgu elementleri

**Örnek Kullanım:**
```jsx
<Button variant="contained" color="secondary">Secondary Button</Button>
<Chip label="Health Status" color="info" />
```

### Success (Güven) - #10B981 (Emerald)
**Psikolojik Etki:** Sağlıklı veriler, onaylanmış işlemler ve kazanç.

**Kullanım Alanları:**
- Başarılı işlemler
- Onay mesajları
- Pozitif göstergeler
- Completed states

**Örnek Kullanım:**
```jsx
<Chip label="Completed" color="success" />
<Alert severity="success">Transaction successful</Alert>
```

### Warning (Kilit) - #F59E0B (Amber)
**Psikolojik Etki:** Escrow'da kilitli tutarlar ve bekleyen randevular.

**Kullanım Alanları:**
- Escrow durumları
- Bekleyen işlemler
- Uyarı mesajları
- Pending states

**Örnek Kullanım:**
```jsx
<Chip label="Locked in Escrow" color="warning" />
<Alert severity="warning">Amount locked</Alert>
```

### Error (IoT Vital Signs) - #F43F5E (Rose)
**Psikolojik Etki:** Yumuşak kırmızı - hastayı paniğe sevk etmeden dikkat çeker.

**Kullanım Alanları:**
- IoT hayati bulgular (nabız, ateş)
- Kritik uyarılar
- Error states
- Vital signs indicators

**Örnek Kullanım:**
```jsx
// Material-UI
<Chip label="High Heart Rate" color="error" />

// Custom IoT Color
import { IOT_COLOR } from '../utils/theme-colors';
<Box sx={{ bgcolor: IOT_COLOR.main }}>IoT Data</Box>

// Tailwind
<div className="bg-iot-main text-white">IoT Indicator</div>
```

### Info (AI Diagnostics) - #8B5CF6 (Purple)
**Psikolojik Etki:** "Burada akıllı bir işlem yapılıyor" mesajını verir.

**Kullanım Alanları:**
- AI teşhisleri
- Machine learning sonuçları
- Smart recommendations
- AI-powered features

**Örnek Kullanım:**
```jsx
// Material-UI
<Chip label="AI Analysis" color="info" />

// Custom AI Color
import { AI_COLOR } from '../utils/theme-colors';
<Box sx={{ bgcolor: AI_COLOR.main }}>AI Result</Box>

// Tailwind
<div className="bg-ai-main text-white">AI Indicator</div>
```

### Surface (Zemin) - #F8FAFC (Slate 50)
**Psikolojik Etki:** Gözü yormayan, steril ve ferah bir çalışma alanı.

**Kullanım Alanları:**
- Arka planlar
- Card backgrounds
- Container backgrounds
- Workspace areas

**Örnek Kullanım:**
```jsx
<Box sx={{ bgcolor: 'background.default' }}>Content</Box>
<div className="bg-slate-50">Content</div>
```

---

## 🌙 Dark Mode - "Neon & Glass" Theme

### Arka Plan: #0F172A (Slate 900)
Derin uzay mavisi - Digital Twin ve Dashboard için.

### Neon Vurgu: #818CF8 (Indigo 400)
3D modellerin (vücut) parlaması için.

### Cam Efekti: rgba(255, 255, 255, 0.05) + backdrop-filter: blur(10px)
Glassmorphism efektleri için.

**Örnek Kullanım:**
```jsx
// Dark mode glass effect
<Box
  sx={{
    bgcolor: 'rgba(255, 255, 255, 0.05)',
    backdropFilter: 'blur(10px)',
    WebkitBackdropFilter: 'blur(10px)',
  }}
>
  Glass Content
</Box>

// Neon glow effect
<Box
  sx={{
    boxShadow: '0 0 20px rgba(129, 140, 248, 0.5)',
    border: '1px solid rgba(129, 140, 248, 0.3)',
  }}
>
  Neon Glow
</Box>
```

---

## 🎯 Projeye Özel "Visual Key" (Fonksiyonel Renkler)

### Blockchain Verileri - Vibrant Indigo
**Renk:** #4F46E5 (Indigo 600) ve tonları
**Amaç:** Dijital varlık temsili - "eski moda altın" yerine modern dijital varlık

**Örnek Kullanım:**
```jsx
import { BLOCKCHAIN_COLOR } from '../utils/theme-colors';

<Box sx={{ bgcolor: BLOCKCHAIN_COLOR.main }}>
  Blockchain Transaction
</Box>

// Tailwind
<div className="bg-blockchain-main">Blockchain Data</div>
```

### AI Teşhisleri - Purple (#8B5CF6)
**Renk:** #8B5CF6 (Purple 500) ve tonları
**Amaç:** Akıllı işlem göstergesi

**Örnek Kullanım:**
```jsx
import { AI_COLOR } from '../utils/theme-colors';

<Box sx={{ bgcolor: AI_COLOR.main }}>
  AI Diagnosis
</Box>

// Tailwind
<div className="bg-ai-main">AI Result</div>
```

### Hayati Bulgular (IoT) - Rose (#F43F5E)
**Renk:** #F43F5E (Rose 500) - Yumuşak kırmızı
**Amaç:** Paniğe sevk etmeden dikkat çekme

**Örnek Kullanım:**
```jsx
import { IOT_COLOR } from '../utils/theme-colors';

<Box sx={{ bgcolor: IOT_COLOR.main }}>
  Heart Rate: 85 bpm
</Box>

// Tailwind
<div className="bg-iot-main">Vital Sign</div>
```

---

## 📐 Modern Dokunuşlar

### Border Radius
- **Small:** 8px (rounded-lg)
- **Medium:** 12px (rounded-xl)
- **Large:** 16px (rounded-2xl)
- **Extra Large:** 24px (rounded-3xl)

### Shadows
- **Soft:** `0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)`
- **Medium:** `0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)`
- **Large:** `0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)`
- **Indigo Glow:** `0 10px 25px -5px rgba(79, 70, 229, 0.3)`

---

## 🔧 Kullanım Örnekleri

### Material-UI ile
```jsx
import { useTheme } from '@mui/material/styles';
import { Box, Button, Chip } from '@mui/material';

const MyComponent = () => {
  const theme = useTheme();
  
  return (
    <Box sx={{ bgcolor: 'primary.main' }}>
      <Button variant="contained" color="primary">
        Primary Action
      </Button>
      <Chip label="AI Analysis" sx={{ bgcolor: theme.custom?.colors?.ai?.main }} />
    </Box>
  );
};
```

### Tailwind CSS ile
```jsx
<div className="bg-primary-600 text-white rounded-xl shadow-indigo">
  Primary Content
</div>

<div className="bg-ai-main text-white rounded-2xl">
  AI Content
</div>

<div className="bg-iot-main text-white rounded-xl">
  IoT Vital Sign
</div>
```

### Custom Colors ile
```jsx
import { AI_COLOR, IOT_COLOR, BLOCKCHAIN_COLOR } from '../utils/theme-colors';

<Box sx={{ bgcolor: AI_COLOR.main }}>AI Content</Box>
<Box sx={{ bgcolor: IOT_COLOR.main }}>IoT Content</Box>
<Box sx={{ bgcolor: BLOCKCHAIN_COLOR.main }}>Blockchain Content</Box>
```

---

## 🎨 Renk Paleti Özeti

| Rol | Renk Kodu | Kullanım |
|-----|-----------|----------|
| Primary | #4F46E5 | Blockchain, Ana butonlar |
| Secondary | #0EA5E9 | Sağlık göstergeleri |
| Success | #10B981 | Başarılı işlemler |
| Warning | #F59E0B | Escrow, Bekleyen |
| Error/IoT | #F43F5E | Hayati bulgular |
| Info/AI | #8B5CF6 | AI teşhisleri |
| Surface | #F8FAFC | Arka planlar |
| Dark BG | #0F172A | Dark mode arka plan |
| Neon | #818CF8 | 3D model glow |

---

## ✅ Best Practices

1. **Blockchain işlemleri için:** Primary (Indigo) kullanın
2. **AI sonuçları için:** Info/Purple (#8B5CF6) kullanın
3. **IoT verileri için:** Error/Rose (#F43F5E) kullanın - yumuşak kırmızı
4. **Başarılı işlemler için:** Success (Emerald) kullanın
5. **Escrow durumları için:** Warning (Amber) kullanın
6. **Dark mode'da:** Neon efektler için #818CF8 kullanın
7. **Glass effect için:** rgba(255, 255, 255, 0.05) + blur(10px)

---

## 🚀 Hızlı Başlangıç

```jsx
// 1. Theme'i import et
import { useTheme } from '@mui/material/styles';
import { AI_COLOR, IOT_COLOR, BLOCKCHAIN_COLOR } from '../utils/theme-colors';

// 2. Component'te kullan
const MyComponent = () => {
  const theme = useTheme();
  
  return (
    <Box>
      {/* Primary color */}
      <Button color="primary">Primary</Button>
      
      {/* AI color */}
      <Chip sx={{ bgcolor: AI_COLOR.main }}>AI</Chip>
      
      {/* IoT color */}
      <Chip sx={{ bgcolor: IOT_COLOR.main }}>IoT</Chip>
      
      {/* Blockchain color */}
      <Chip sx={{ bgcolor: BLOCKCHAIN_COLOR.main }}>Blockchain</Chip>
    </Box>
  );
};
```


