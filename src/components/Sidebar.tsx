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

  // 링크 클릭 시 사이드바를 닫는 함수
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
          className="text-white text-2xl mb-8"
        >×</button>
        <nav className="space-y-0 pl-2">
          <Link 
            to="/" 
            className="block text-white text-3xl py-3"
            onClick={handleLinkClick}
          >홈</Link>
          <div className="h-[1px] bg-gray-500/50"></div>
          <Link 
            to="/distance" 
            className="block text-white text-3xl py-3"
            onClick={handleLinkClick}
          >토큰 거래소</Link>
          <div className="h-[1px] bg-gray-500/50"></div>
          <Link 
            to="/my-page" 
            className="block text-white text-3xl py-3"
            onClick={handleLinkClick}
          >마이페이지</Link>
          <div className="h-[1px] bg-gray-500/50"></div>
          <Link 
            to="/team" 
            className="block text-white text-3xl py-3"
            onClick={handleLinkClick}
          >팀채널</Link>
          <div className="h-[1px] bg-gray-500/50"></div>
          <Link 
            to="/store" 
            className="block text-white text-3xl py-3"
            onClick={handleLinkClick}
          >스토어</Link>
        </nav>
      </div>
    </motion.div>
  );
};

export default Sidebar;
