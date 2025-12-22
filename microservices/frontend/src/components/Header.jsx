import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Search, Bell, Wallet, User, Menu, X, Command, Cpu, Globe, Zap } from 'lucide-react';

const Header = () => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [role, setRole] = useState('Patient'); 
  const [isSearchFocused, setIsSearchFocused] = useState(false);

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 10);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav className={`fixed top-0 w-full z-[100] transition-all duration-500 ${
      isScrolled 
        ? 'bg-white/60 backdrop-blur-2xl border-b border-white/20 py-3 shadow-[0_8px_32px_rgba(0,0,0,0.05)]' 
        : 'bg-transparent py-6'
    }`}>
      <div className="max-w-[1440px] mx-auto px-8 flex items-center justify-between">
        
        {/* Sol Kısım: Logo & Global Status */}
        <div className="flex items-center gap-6">
          <div className="flex items-center gap-3 group cursor-pointer">
            <motion.div 
              whileHover={{ rotate: 180, scale: 1.1 }}
              className="p-2.5 bg-gradient-to-br from-indigo-600 to-violet-600 rounded-2xl shadow-indigo-200 shadow-xl text-white"
            >
              <Cpu size={24} />
            </motion.div>
            <div className="flex flex-col">
              <span className="text-2xl font-black tracking-tighter text-slate-900 leading-none">
                HEALTH<span className="text-indigo-600">CHAIN</span>
              </span>
              <div className="flex items-center gap-2 mt-1">
                <div className="flex items-center gap-1 bg-emerald-50 px-1.5 py-0.5 rounded-full border border-emerald-100">
                  <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                  <span className="text-[9px] font-black text-emerald-700 uppercase tracking-wider">Node Active</span>
                </div>
                <span className="text-[9px] font-bold text-slate-400 uppercase tracking-widest">v2.5 Hybrid</span>
              </div>
            </div>
          </div>

          {/* Patentli Sistem Durum Göstergesi (Yeni eklendi) */}
          <div className="hidden lg:flex items-center gap-4 pl-6 border-l border-slate-200/60">
             <div className="flex flex-col">
                <div className="flex items-center gap-1.5 text-slate-500">
                   <Globe size={12} className="animate-spin-slow" />
                   <span className="text-[10px] font-bold uppercase tracking-tight">Global Sync</span>
                </div>
                <span className="text-[11px] font-black text-slate-700">99.9% Resilience</span>
             </div>
          </div>
        </div>

        {/* Orta Kısım: AI Search (Daha Akıllı Görünüm) */}
        <div className="hidden md:flex flex-1 max-w-md mx-12">
          <div className={`relative w-full group transition-all duration-300 ${isSearchFocused ? 'scale-105' : ''}`}>
            <div className={`absolute inset-y-0 left-4 flex items-center transition-colors ${isSearchFocused ? 'text-indigo-600' : 'text-slate-400'}`}>
              <Search size={18} strokeWidth={2.5} />
            </div>
            <input 
              type="text" 
              onFocus={() => setIsSearchFocused(true)}
              onBlur={() => setIsSearchFocused(false)}
              placeholder="GraphRAG asistanına danışın..." 
              className="w-full pl-12 pr-16 py-3 bg-slate-100/50 border-none focus:bg-white focus:ring-4 focus:ring-indigo-50 rounded-2xl text-sm transition-all outline-none font-medium text-slate-700"
            />
            <div className="absolute right-3 inset-y-0 flex items-center gap-1">
              <kbd className="hidden sm:flex items-center px-2 py-1 text-[10px] font-bold text-slate-400 bg-white border border-slate-200 rounded-lg shadow-sm">
                <Command size={10} className="mr-1" /> K
              </kbd>
            </div>
          </div>
        </div>

        {/* Sağ Kısım: Actions & Profile */}
        <div className="flex items-center gap-3">
          {/* Hızlı Erişim: Health Token/Wallet */}
          <motion.button 
            whileHover={{ y: -2 }}
            whileTap={{ scale: 0.95 }}
            className="hidden sm:flex items-center gap-2.5 px-5 py-2.5 bg-slate-900 text-white rounded-2xl font-bold text-xs shadow-lg shadow-slate-200 hover:bg-indigo-600 transition-all"
          >
            <Zap size={14} className="text-yellow-400 fill-yellow-400" />
            <span className="border-r border-white/20 pr-2">1,250 HTK</span>
            <span className="font-mono opacity-80">0x71...4F2</span>
          </motion.button>

          {/* Bildirim */}
          <button className="relative p-3 text-slate-500 hover:bg-white hover:shadow-md rounded-2xl transition-all border border-transparent hover:border-slate-100">
            <Bell size={20} />
            <span className="absolute top-2.5 right-2.5 h-2.5 w-2.5 bg-indigo-600 border-2 border-white rounded-full"></span>
          </button>

          {/* Profil */}
          <div className="flex items-center gap-3 ml-2 group cursor-pointer">
            <div className="text-right hidden xl:block">
              <p className="text-sm font-black text-slate-800 leading-none group-hover:text-indigo-600 transition-colors">Semanur Uykun</p>
              <span className="text-[10px] font-black text-indigo-500 uppercase tracking-tighter bg-indigo-50 px-1.5 rounded mt-1 inline-block">Pro {role}</span>
            </div>
            <div className="relative">
              <div className="h-11 w-11 rounded-2xl bg-indigo-100 border-2 border-white shadow-md overflow-hidden group-hover:ring-4 ring-indigo-50 transition-all">
                <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Semanur" alt="Avatar" className="w-full h-full object-cover" />
              </div>
              <div className="absolute -bottom-1 -right-1 h-4 w-4 bg-green-500 border-2 border-white rounded-full"></div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Header;