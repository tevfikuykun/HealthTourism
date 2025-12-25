import React, { useState, useEffect, Suspense } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Heart, Activity, Thermometer, Brain, Cpu, ShieldCheck, AlertTriangle } from 'lucide-react';
import { Grid, Alert, AlertTitle, Collapse } from '@mui/material';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Environment, useGLTF, Float, ContactShadows, Center } from '@react-three/drei';
import * as THREE from 'three';
import { validateGLTF, performanceRecommendations } from '../utils/gltfValidator';

// GLTF modelini preload et (performans için)
useGLTF.preload('/models/human.glb');

/**
 * 3D ANATOMİ MODELİ - Etkileşimli Parçalar
 */
function AnatomyModel({ data, onHover, onLoad, onMeshNamesFound }) {
  // human.glb dosyasını kullan
  const { scene } = useGLTF('/models/human.glb'); 
  const [hoveredPart, setHoveredPart] = useState(null);

  // Model yüklendiğinde mesh isimlerini topla ve console'a yazdır
  useEffect(() => {
    if (scene) {
      const names = [];
      scene.traverse((child) => {
        if (child.isMesh || child.isGroup) {
          if (child.name) {
            names.push(child.name);
          }
        }
      });
      
      // Console'a yazdır
      console.log('=== GLTF MODEL MESH İSİMLERİ ===');
      console.log('Toplam mesh/group sayısı:', names.length);
      console.log('Mesh isimleri:', names);
      names.forEach((name, index) => {
        console.log(`${index + 1}. ${name}`);
      });
      console.log('================================');
      
      // Parent component'e gönder
      if (onMeshNamesFound) {
        onMeshNamesFound(names);
      }
      
      // Callback çağır
      if (onLoad) {
        onLoad();
      }
    }
  }, [scene, onLoad, onMeshNamesFound]);

  useEffect(() => {
    scene.traverse((child) => {
      if (child.isMesh) {
        const name = child.name.toLowerCase();
        const originalName = child.name; // Orijinal ismi sakla
        
        // --- TEMEL HOLOGRAM MATERYALİ ---
        child.material = new THREE.MeshPhysicalMaterial({
          color: '#4facfe',
          transparent: true,
          opacity: 0.25, // İskeleti şeffaf yaparak içini görmeyi sağlarız
          metalness: 0.9,
          roughness: 0.1,
          transmission: 0.6,
        });

        // --- ORGAN VE BÖLGE ÖZELLEŞTİRMELERİ ---
        // Gerçek mesh isimlerine göre eşleştirme yapılacak
        
        // KALP (Heart) - Çeşitli isim formatları
        if (name.includes('heart') || name.includes('kalp') || name.includes('cardiac') || 
            name.includes('cor') || name === 'heart' || originalName.includes('Heart')) {
          child.material.color.set('#ff0000');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#ff0000');
            child.material.emissiveIntensity = 4;
            child.material.opacity = 1;
          }
        } 
        // CİĞERLER (Lungs) - Çeşitli isim formatları
        else if (name.includes('lung') || name.includes('ciger') || name.includes('pulmonary') ||
                 name.includes('akciger') || name === 'lung' || originalName.includes('Lung')) {
          child.material.color.set('#bae6fd');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#7dd3fc');
            child.material.emissiveIntensity = 3;
            child.material.opacity = 1;
          }
        }
        // BÖBREK (Kidney) - Çeşitli isim formatları
        else if (name.includes('kidney') || name.includes('bobrek') || name.includes('renal') ||
                 name === 'kidney' || originalName.includes('Kidney')) {
          child.material.color.set('#a855f7');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#d8b4fe');
            child.material.emissiveIntensity = 3;
            child.material.opacity = 1;
          }
        }
        // DALAK (Spleen) - Çeşitli isim formatları
        else if (name.includes('spleen') || name.includes('dalak') || 
                 name === 'spleen' || originalName.includes('Spleen')) {
          child.material.color.set('#991b1b');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#ef4444');
            child.material.emissiveIntensity = 3;
          }
        }
        // KARACİĞER (Liver) - Çeşitli isim formatları
        else if (name.includes('liver') || name.includes('karaciger') || name.includes('hepatic') ||
                 name === 'liver' || originalName.includes('Liver')) {
          child.material.color.set('#22c55e');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#4ade80');
            child.material.emissiveIntensity = 3;
            child.material.opacity = 1;
          }
        }
        // BEYİN (Brain) - Çeşitli isim formatları
        else if (name.includes('brain') || name.includes('beyin') || name.includes('cerebral') ||
                 name === 'brain' || originalName.includes('Brain') || originalName.includes('Head')) {
          child.material.color.set('#8b5cf6');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#a78bfa');
            child.material.emissiveIntensity = 3;
            child.material.opacity = 1;
          }
        }
        // MİDE (Stomach) - Çeşitli isim formatları
        else if (name.includes('stomach') || name.includes('mide') || name.includes('gastric') ||
                 name === 'stomach' || originalName.includes('Stomach')) {
          child.material.color.set('#f59e0b');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#fbbf24');
            child.material.emissiveIntensity = 3;
            child.material.opacity = 1;
          }
        }
        // BAĞIRSAK (Intestine) - Çeşitli isim formatları
        else if (name.includes('intestine') || name.includes('bagirsak') || name.includes('bowel') ||
                 name === 'intestine' || originalName.includes('Intestine')) {
          child.material.color.set('#ec4899');
          if (hoveredPart === child.uuid) {
            child.material.emissive.set('#f472b6');
            child.material.emissiveIntensity = 3;
            child.material.opacity = 1;
          }
        }
        // SAÇ VE KEMİKLER
        else if (name.includes('hair') || name.includes('sac') || name.includes('bone') || 
                 name.includes('kemik') || name.includes('skeleton') || name.includes('skull') ||
                 originalName.includes('Bone') || originalName.includes('Hair')) {
          child.material.color.set('#ffffff');
          child.material.opacity = 0.4;
        }
        // DERİ VE KAS (Skin, Muscle)
        else if (name.includes('skin') || name.includes('deri') || name.includes('muscle') ||
                 name.includes('kas') || name.includes('flesh') || originalName.includes('Skin') ||
                 originalName.includes('Muscle')) {
          child.material.color.set('#fbbf24');
          child.material.opacity = 0.3;
        }
      }
    });
  }, [scene, hoveredPart]);

  return (
    <Float speed={2} rotationIntensity={0.3} floatIntensity={0.5}>
      <primitive 
        object={scene} 
        scale={0.007} 
        position={[0, -2.5, 0]}
        onPointerOver={(e) => {
          e.stopPropagation();
          setHoveredPart(e.object.uuid);
          onHover(e.object.name);
        }}
        onPointerOut={() => {
          setHoveredPart(null);
          onHover(null);
        }}
      />
    </Float>
  );
}

const DigitalTwin = () => {
  const [hoveredOrgan, setHoveredOrgan] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [meshNames, setMeshNames] = useState([]);
  const [showDebug, setShowDebug] = useState(false);
  const [liveData] = useState({
    heartRate: 72,
    bloodPressure: '120/80',
    temperature: 36.6,
  });

  return (
    <div className="min-h-screen bg-[#020617] pt-28 pb-12 px-8 relative overflow-hidden text-slate-200">
      
      {/* Arka Plan Efekti */}
      <div className="absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_50%_50%,#1e1b4b_0%,#020617_100%)] -z-10"></div>

      <div className="max-w-[1500px] mx-auto relative z-10">
        <header className="mb-12 flex justify-between items-end border-b border-white/5 pb-8">
          <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }}>
            <h1 className="text-6xl font-black text-white tracking-tighter italic uppercase">
              DIGITALTWIN
            </h1>
            <p className="text-indigo-400 font-bold tracking-[6px] text-[10px] mt-3 uppercase opacity-70">
              Biometric Scan • Raycaster Engine v2.0
            </p>
          </motion.div>

          {/* Debug Butonu */}
          {meshNames.length > 0 && (
            <motion.button
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              onClick={() => setShowDebug(!showDebug)}
              className="px-4 py-2 bg-indigo-600/20 border border-indigo-500/30 rounded-lg text-xs text-indigo-400 hover:bg-indigo-600/30 transition-all"
            >
              {showDebug ? 'Mesh Listesini Gizle' : 'Mesh Listesini Göster'}
            </motion.button>
          )}

          <AnimatePresence mode="wait">
            {hoveredOrgan && (
              <motion.div 
                key={hoveredOrgan}
                initial={{ opacity: 0, y: 10 }} 
                animate={{ opacity: 1, y: 0 }} 
                exit={{ opacity: 0, y: -10 }}
                className="bg-indigo-600/10 border border-indigo-500/30 px-8 py-4 rounded-3xl backdrop-blur-3xl"
              >
                <p className="text-[10px] font-black text-indigo-400 uppercase tracking-widest text-center">Analiz Edilen Bölge</p>
                <p className="text-2xl font-black text-white uppercase italic tracking-wider">{hoveredOrgan}</p>
              </motion.div>
            )}
          </AnimatePresence>
        </header>

        {/* Debug Panel - Mesh İsimleri */}
        {showDebug && meshNames.length > 0 && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 bg-slate-900/80 border border-indigo-500/30 rounded-2xl p-6 backdrop-blur-xl"
          >
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-sm font-black text-indigo-400 uppercase tracking-widest">
                Model Mesh İsimleri ({meshNames.length} adet)
              </h3>
              <button
                onClick={() => setShowDebug(false)}
                className="text-slate-400 hover:text-white text-xs"
              >
                ✕
              </button>
            </div>
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2 max-h-60 overflow-y-auto">
              {meshNames.map((name, index) => (
                <div
                  key={index}
                  className="px-3 py-2 bg-slate-800/50 rounded-lg text-xs text-slate-300 font-mono border border-slate-700/50"
                >
                  {name || `Unnamed_${index}`}
                </div>
              ))}
            </div>
            <p className="text-xs text-slate-500 mt-4">
              💡 Console'da (F12) detaylı mesh listesini görebilirsiniz.
            </p>
          </motion.div>
        )}

        <Grid container spacing={4}>
          {/* SOL PANEL */}
          <Grid item xs={12} md={3} className="space-y-6">
            <DataCard icon={Heart} label="Kardiyak Nabız" value={liveData.heartRate} unit="BPM" color="rose" />
            <DataCard icon={Activity} label="Kan Basıncı" value={liveData.bloodPressure} color="indigo" />
            <DataCard icon={Thermometer} label="Sıcaklık" value={liveData.temperature} unit="°C" color="amber" />
          </Grid>

          {/* ORTA PANEL - 3D CANVAS */}
          <Grid item xs={12} md={6}>
            <div className="relative h-[780px] bg-slate-950/30 backdrop-blur-3xl rounded-[4rem] border border-white/10 shadow-3xl overflow-hidden group">
              {/* Loading Indicator - Canvas dışında */}
              {isLoading && (
                <div className="absolute inset-0 flex items-center justify-center z-10">
                  <div className="text-center">
                    <div className="w-12 h-12 rounded-full border-4 border-indigo-500/20 border-t-indigo-500 animate-spin mx-auto mb-4" />
                    <p className="text-indigo-400 font-black animate-pulse uppercase tracking-[4px] text-sm">
                      Sistem Yükleniyor...
                    </p>
                  </div>
                </div>
              )}

              <Canvas dpr={[1, 2]} camera={{ position: [0, 0, 15], fov: 35 }}>
                <ambientLight intensity={0.5} />
                <spotLight position={[10, 15, 10]} angle={0.2} penumbra={1} intensity={2} color="#6366f1" />
                
                <Suspense fallback={null}>
                  <Center top>
                    <AnatomyModel 
                      data={liveData} 
                      onHover={(organName) => {
                        setHoveredOrgan(organName);
                      }}
                      onLoad={() => setIsLoading(false)}
                      onMeshNamesFound={(names) => setMeshNames(names)}
                    />
                  </Center>
                  <ContactShadows position={[0, -2.8, 0]} opacity={0.4} scale={12} blur={2.8} far={4} color="#6366f1" />
                </Suspense>

                <Environment preset="night" />
                <OrbitControls enableZoom={true} makeDefault minDistance={5} maxDistance={25} />
              </Canvas>

              {/* HUD FOOTER */}
              <div className="absolute bottom-10 inset-x-10 p-6 bg-slate-950/80 border border-white/5 rounded-[2.5rem] backdrop-blur-3xl flex justify-between items-center">
                <div className="flex items-center gap-4">
                  <div className="p-3 bg-indigo-500/20 rounded-2xl text-indigo-400"><Brain size={24} /></div>
                  <div>
                    <p className="text-[10px] font-black text-slate-500 tracking-widest">AI TEŞHİS MOTORU</p>
                    <p className="text-xs text-slate-300">Patolojik risk saptanmadı. Tüm sistemler stabil.</p>
                  </div>
                </div>
                <div className="w-12 h-12 rounded-full border-4 border-indigo-500/20 border-t-indigo-500 animate-spin" />
              </div>
            </div>
          </Grid>

          {/* SAĞ PANEL */}
          <Grid item xs={12} md={3}>
            <div className="bg-slate-900/40 backdrop-blur-3xl border border-white/10 p-8 rounded-[3.5rem] h-full flex flex-col">
               <h4 className="text-white font-black text-xs mb-12 tracking-[4px] flex items-center gap-3 uppercase">
                 <Cpu size={20} className="text-indigo-500" /> Organ Kapasitesi
               </h4>
               
               <div className="space-y-12 flex-grow">
                 {[
                   { n: 'Akciğer', v: 94, c: 'bg-cyan-400' },
                   { n: 'Karaciğer', v: 98, c: 'bg-emerald-400' },
                   { n: 'Böbrek', v: 91, c: 'bg-rose-500' }
                 ].map((organ) => (
                   <div key={organ.n}>
                     <div className="flex justify-between text-[10px] font-black text-slate-500 mb-3 tracking-widest uppercase">
                       <span>{organ.n}</span>
                       <span className="text-white">%{organ.v}</span>
                     </div>
                     <div className="h-1.5 w-full bg-white/5 rounded-full overflow-hidden">
                        <motion.div 
                          initial={{ width: 0 }} 
                          animate={{ width: `${organ.v}%` }} 
                          className={`h-full ${organ.c} shadow-[0_0_15px_rgba(99,102,241,0.3)]`} 
                        />
                     </div>
                   </div>
                 ))}
               </div>

               <motion.button 
                 whileHover={{ scale: 1.02 }} 
                 className="w-full mt-10 py-6 bg-indigo-600 text-white rounded-[2rem] font-black text-[10px] uppercase tracking-[4px] shadow-2xl"
               >
                 SAĞLIK RAPORU OLUŞTUR
               </motion.button>
            </div>
          </Grid>
        </Grid>
      </div>
    </div>
  );
};

const DataCard = ({ icon: Icon, label, value, color, unit = '' }) => (
  <div className="bg-slate-900/60 backdrop-blur-3xl border border-white/10 p-7 rounded-[2.5rem] group hover:border-white/20 transition-all">
    <div className={`p-4 w-fit rounded-2xl bg-${color}-500/10 text-${color}-400 mb-6 group-hover:scale-110 transition-all`}>
      <Icon size={26} />
    </div>
    <p className="text-slate-500 text-[10px] font-black uppercase tracking-[3px]">{label}</p>
    <h3 className="text-4xl font-black text-white mt-1 italic tracking-tighter">
      {value} <span className="text-sm font-medium text-slate-600 not-italic ml-1">{unit}</span>
    </h3>
  </div>
);

export default DigitalTwin;
