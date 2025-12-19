// components/Header.jsx
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Search, Bell, Wallet, User, Menu, X, Command, Cpu } from 'lucide-react';

const Header = () => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [role, setRole] = useState('Patient'); // Admin, Doctor, Patient

  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  return (
    <nav className={`fixed top-0 w-full z-[100] transition-all duration-300 ${
      isScrolled ? 'bg-white/70 backdrop-blur-xl border-b border-slate-200/50 py-3' : 'bg-transparent py-5'
    }`}>
      <div className="max-w-7xl mx-auto px-6 flex items-center justify-between">
        
        {/* Logo & Platform Tag */}
        <div className="flex items-center gap-3">
          <div className="p-2 bg-indigo-600 rounded-xl shadow-indigo-200 shadow-lg text-white">
            <Cpu size={24} />
          </div>
          <div>
            <span className="text-xl font-black tracking-tighter text-slate-900">HEALTH<span className="text-indigo-600">CHAIN</span></span>
            <div className="flex items-center gap-1.5">
              <span className="block h-1.5 w-1.5 rounded-full bg-green-500 animate-pulse"></span>
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Mainnet v2.5</span>
            </div>
          </div>
        </div>

        {/* Desktop Navigation - AI Driven Search */}
        <div className="hidden md:flex items-center gap-6">
          <div className="relative group">
            <div className="absolute inset-y-0 left-3 flex items-center text-slate-400 group-focus-within:text-indigo-600 transition-colors">
              <Search size={18} />
            </div>
            <input 
              type="text" 
              placeholder="AI Asistanına sor... (Ctrl+K)"
              className="pl-10 pr-12 py-2.5 bg-slate-100 border-transparent focus:bg-white focus:ring-2 focus:ring-indigo-100 focus:border-indigo-200 rounded-2xl text-sm w-80 transition-all outline-none"
            />
            <div className="absolute right-3 inset-y-0 flex items-center">
              <kbd className="hidden sm:inline-block px-1.5 py-0.5 text-[10px] font-medium text-slate-400 bg-white border border-slate-200 rounded-md shadow-sm">
                <Command size={10} className="inline mr-1" />K
              </kbd>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex items-center gap-4">
          {/* Notification */}
          <button className="relative p-2.5 text-slate-600 hover:bg-slate-100 rounded-xl transition-colors">
            <Bell size={22} />
            <span className="absolute top-2 right-2 h-2 w-2 bg-red-500 border-2 border-white rounded-full"></span>
          </button>

          {/* Wallet Status */}
          <button className="hidden sm:flex items-center gap-2 px-4 py-2.5 bg-indigo-50 text-indigo-700 rounded-xl font-bold text-sm border border-indigo-100 hover:bg-indigo-100 transition-all">
            <Wallet size={18} />
            0x71...4F2
          </button>

          {/* User Profile & Role Badge */}
          <div className="flex items-center gap-3 pl-4 border-l border-slate-200">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-bold text-slate-800 leading-none">Semanur Uygun</p>
              <span className="text-[10px] font-black text-indigo-500 uppercase">{role}</span>
            </div>
            <div className="h-10 w-10 rounded-xl bg-slate-200 border-2 border-white shadow-sm overflow-hidden cursor-pointer hover:ring-2 ring-indigo-400 transition-all">
              <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Semanur" alt="Avatar" />
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Header;