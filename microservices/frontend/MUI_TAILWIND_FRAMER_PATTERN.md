# Material-UI + Tailwind CSS + Framer Motion Integration Pattern

Bu projede üç teknolojiyi birlikte kullanmak için standart pattern'ler ve best practice'ler.

## 📦 Kurulum

Tüm paketler zaten yüklü:
- ✅ Material-UI (`@mui/material`, `@mui/icons-material`)
- ✅ Tailwind CSS (`tailwindcss`, `autoprefixer`, `postcss`)
- ✅ Framer Motion (`framer-motion`)

## 🎯 Kullanım Pattern'i

### 1. Import Yapısı

```jsx
import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Box, Container, Typography, Card, Button } from '@mui/material';
import { SomeIcon } from '@mui/icons-material';
import { fadeInUp, staggerContainer, staggerItem, hoverLift } from '../utils/ui-helpers';
```

### 2. Component Yapısı

```jsx
const MyComponent = () => {
  return (
    <Container maxWidth="lg" className="py-8" sx={{ py: 4 }}>
      {/* Framer Motion Container */}
      <motion.div
        variants={fadeInUp}
        initial="initial"
        animate="animate"
      >
        {/* Material-UI Component + Tailwind Classes */}
        <Card 
          className="shadow-lg hover:shadow-xl transition-shadow"
          sx={{ borderRadius: 2, p: 3 }}
        >
          <Typography variant="h4" className="font-bold mb-4">
            Başlık
          </Typography>
        </Card>
      </motion.div>
    </Container>
  );
};
```

### 3. Stagger Animasyonları (Liste/Grid)

```jsx
<motion.div
  variants={staggerContainer}
  initial="hidden"
  whileInView="show"
  viewport={{ once: true }}
>
  <Grid container spacing={3}>
    {items.map((item, index) => (
      <Grid item xs={12} md={4} key={item.id}>
        <motion.div variants={staggerItem}>
          <Card className="shadow-lg hover:shadow-xl">
            {/* Content */}
          </Card>
        </motion.div>
      </Grid>
    ))}
  </Grid>
</motion.div>
```

### 4. Hover Efektleri

```jsx
<motion.div
  {...hoverLift} // veya {...hoverScale}
  className="cursor-pointer"
>
  <Card className="shadow-md hover:shadow-xl transition-shadow">
    {/* Content */}
  </Card>
</motion.div>
```

## 🎨 Stil Yaklaşımı

### Material-UI (sx prop)
- **Kullanım:** Tema değişkenleri, responsive breakpoints, Material-UI özel özellikler
- **Örnek:** `sx={{ p: 3, borderRadius: 2, bgcolor: 'primary.main' }}`

### Tailwind CSS (className)
- **Kullanım:** Utility class'ları, hover states, transitions, shadows
- **Örnek:** `className="shadow-lg hover:shadow-xl transition-shadow rounded-lg"`

### Framer Motion (motion props)
- **Kullanım:** Animasyonlar, geçişler, hover/tap efektleri
- **Örnek:** `variants={fadeInUp} initial="initial" animate="animate"`

## 📚 Yardımcı Fonksiyonlar

`src/utils/ui-helpers.js` dosyasında:

- `fadeInUp` - Yukarıdan fade-in
- `fadeIn` - Basit fade-in
- `scaleIn` - Scale ile fade-in
- `slideInRight` - Sağdan slide-in
- `staggerContainer` - Stagger container variant
- `staggerItem` - Stagger item variant
- `hoverLift` - Hover'da yukarı kalkma
- `hoverScale` - Hover'da büyüme

## ✅ Best Practices

1. **Material-UI Component'leri koruyun** - Tema sistemi ve accessibility için
2. **Tailwind'i utility olarak kullanın** - Hızlı styling için
3. **Framer Motion'ı animasyon için kullanın** - Smooth geçişler için
4. **className ve sx'i birlikte kullanın** - Her ikisinin güçlü yönlerinden faydalanın
5. **viewport={{ once: true }} ekleyin** - Scroll animasyonlarında performans için

## 🚀 Örnek Sayfalar

- ✅ `Home.jsx` - Tam entegrasyon örneği
- ✅ `HealthWallet.jsx` - Modern UI örneği

## 📝 Notlar

- Tailwind'in `preflight: false` ayarı MUI ile çakışmayı önler
- `className` ve `sx` prop'ları birlikte kullanılabilir
- Framer Motion `motion.div` ile Material-UI component'lerini sarabilirsiniz



