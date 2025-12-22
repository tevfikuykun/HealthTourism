/**
 * GLTF Dosya Validasyon ve Test Utility
 * human.glb dosyasının projede çalışıp çalışmayacağını kontrol eder
 */

/**
 * GLTF dosyasını test et
 * @param {string} modelPath - Model dosyasının yolu
 * @returns {Promise<{success: boolean, error?: string, info?: object}>}
 */
export async function validateGLTF(modelPath) {
  try {
    // Dosya yolunu kontrol et
    const response = await fetch(modelPath, { method: 'HEAD' });
    
    if (!response.ok) {
      return {
        success: false,
        error: `Dosya bulunamadı veya erişilemiyor: ${response.status} ${response.statusText}`
      };
    }

    // Dosya boyutunu kontrol et
    const contentLength = response.headers.get('content-length');
    const fileSizeMB = contentLength ? (parseInt(contentLength) / (1024 * 1024)).toFixed(2) : 'bilinmiyor';

    // GLTF dosyası olup olmadığını kontrol et (Content-Type)
    const contentType = response.headers.get('content-type');
    const isGLTF = contentType?.includes('model/gltf') || 
                   contentType?.includes('application/octet-stream') ||
                   modelPath.endsWith('.glb') || 
                   modelPath.endsWith('.gltf');

    return {
      success: true,
      info: {
        exists: true,
        fileSize: `${fileSizeMB} MB`,
        contentType: contentType || 'application/octet-stream',
        isGLTF: isGLTF,
        url: modelPath,
        warnings: fileSizeMB > 50 ? [
          '⚠️ Dosya boyutu çok büyük (>50MB). Performans sorunlarına neden olabilir.',
          '💡 Öneri: Modeli optimize edin veya daha küçük bir versiyon kullanın.',
          '💡 Öneri: Lazy loading ve progressive loading kullanın.'
        ] : []
      }
    };
  } catch (error) {
    return {
      success: false,
      error: `Dosya kontrolü sırasında hata: ${error.message}`
    };
  }
}

/**
 * React Three Fiber ile GLTF yükleme testi
 * @param {Function} useGLTF - @react-three/drei'den useGLTF hook'u
 * @param {string} modelPath - Model dosyasının yolu
 * @returns {Promise<{success: boolean, error?: string, scene?: object}>}
 */
export async function testGLTFLoad(useGLTF, modelPath) {
  try {
    // useGLTF hook'unu test et (bu bir hook olduğu için component içinde kullanılmalı)
    // Bu fonksiyon sadece bilgi amaçlıdır
    return {
      success: true,
      message: 'useGLTF hook component içinde kullanılmalıdır. Test için DigitalTwin sayfasını açın.'
    };
  } catch (error) {
    return {
      success: false,
      error: `GLTF yükleme testi başarısız: ${error.message}`
    };
  }
}

/**
 * Performans önerileri
 */
export const performanceRecommendations = {
  largeFile: {
    title: 'Büyük Dosya Boyutu (>50MB)',
    recommendations: [
      'Modeli optimize edin: Draco compression kullanın',
      'LOD (Level of Detail) ekleyin: Farklı detay seviyelerinde modeller',
      'Progressive loading: Modeli parçalara bölerek yükleyin',
      'CDN kullanın: Statik dosyaları CDN\'den serve edin',
      'Cache stratejisi: Browser cache ve service worker kullanın'
    ]
  },
  loading: {
    title: 'Yükleme Optimizasyonu',
    recommendations: [
      'useGLTF.preload() kullanın: Modeli önceden yükleyin',
      'Suspense kullanın: Yükleme sırasında fallback gösterin',
      'Progress indicator: Yükleme ilerlemesini gösterin',
      'Error boundary: Hata durumlarını yakalayın'
    ]
  },
  rendering: {
    title: 'Render Optimizasyonu',
    recommendations: [
      'Instancing kullanın: Tekrarlanan objeler için',
      'Frustum culling: Görünmeyen objeleri render etmeyin',
      'Shadow optimization: Shadow map boyutunu optimize edin',
      'Material optimization: Gereksiz material özelliklerini kaldırın'
    ]
  }
};

