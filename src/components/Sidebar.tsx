import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useStore } from '../stores/useStore';

const Sidebar: React.FC = () => {
  const { isSidebarOpen, toggleSidebar } = useStore();
  
  const sidebarVariants = {
    open: { x: '0%' },
    closed: { x: '120%' },
  };

  const handleLinkClick = () => {
    toggleSidebar(false);
  };

  return (
    <motion.div
      initial="closed"
      animate={isSidebarOpen ? "open" : "closed"}
      variants={sidebarVariants}
      transition={{ 
        type: "tween",
        duration: 0.8,
        ease: "easeInOut"
      }}
      className="absolute top-0 right-0 w-full h-full bg-black z-50 font-['Giants-Bold']"
      style={{
        aspectRatio: '360/743',
        maxHeight: 'min(743px, 100vh)',
      }}
    >
      <div className="py-6 pl-4">
        <button 
          onClick={() => toggleSidebar(false)} 
          className="text-white text-3xl mb-8 hover:text-gray-300 transition-colors"
        >×</button>
        <nav className="space-y-0 pl-2">
          <Link 
            to="/home" 
            className="block text-white text-3xl py-3 hover:text-gray-300 transition-colors"
            onClick={handleLinkClick}
          >홈</Link>
          <div className="h-[1px] bg-gray-700"></div>
          <Link 
            to="/my" 
            className="block text-white text-3xl py-3 hover:text-gray-300 transition-colors"
            onClick={handleLinkClick}
          >마이월렛</Link>
          <div className="h-[1px] bg-gray-700"></div>
          <Link 
            to="/exchange" 
            className="block text-white text-3xl py-3 hover:text-gray-300 transition-colors"
            onClick={handleLinkClick}
          >거래소</Link>
          <div className="h-[1px] bg-gray-700"></div>
          <Link 
            to="/team" 
            className="block text-white text-3xl py-3 hover:text-gray-300 transition-colors"
            onClick={handleLinkClick}
          >팀채널</Link>
          <div className="h-[1px] bg-gray-700"></div>
          <Link 
            to="/archive" 
            className="block text-white text-3xl py-3 hover:text-gray-300 transition-colors"
            onClick={handleLinkClick}
          >아카이브</Link>
        </nav>
      </div>
    </motion.div>
  );
};

export default Sidebar;
