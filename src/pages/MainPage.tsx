import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useStore } from '../stores/useStore';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import Sidebar from '../components/Sidebar';
import { ColorPicker } from '../components/ColorPicker';
import { CollaborativeCanvas } from '../components/CollaborativeCanvas';

type Tab = 'LIVE-PICK' | 'WINNER' | 'PIXEL';

const MainPage: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<Tab>('LIVE-PICK');
  const { currentGame, toggleSidebar } = useStore();

  // 게임 정보가 없으면 홈으로 리다이렉트
  if (!currentGame) {
    navigate('/');
    return null;
  }

  return (
    <div className="relative h-full bg-black text-white overflow-hidden">
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate(-1)} 
          className="w-[12px] h-[12px]"
        >
          <img src={backImg} alt="Back" className="w-full h-full" />
        </button>
        <div className="w-5 h-5 flex items-center justify-center">
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 상단 매치업 타이틀 */}
      <div className="pt-16 pb-4 text-center">
        <div className="text-red-600 text-sm font-['Giants-Bold']">
          {currentGame.status === 'LIVE' ? 'LIVE' : 
           currentGame.status === 'ENDED' ? '경기종료' :
           currentGame.status === 'CANCELED' ? '경기취소' : '경기예정'}
        </div>
        <h1 className="text-4xl font-['Giants-Bold'] mt-1">
          {`${currentGame.homeTeam} vs ${currentGame.awayTeam}`}
        </h1>
        <div className="mt-2">
          <div className="text-xl font-['Giants-Bold']">
            {currentGame.homeScore} : {currentGame.awayScore}
          </div>
          <div className="text-xs text-gray-400 mt-0.5">
            {currentGame.schedule.stadium} | {currentGame.schedule.startTime}
          </div>
        </div>
      </div>

      {/* 토글 탭 */}
      <div className="px-6">
        <div className="flex rounded-full bg-gray-800/50 p-1">
          {(['LIVE-PICK', 'WINNER', 'PIXEL'] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`flex-1 py-3 rounded-full text-sm font-['Pretendard-Regular'] transition-colors
                ${activeTab === tab ? 'bg-white text-black' : 'text-white'}`}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>

      {/* 컨텐츠 영역 */}
      <div className="mt-6 px-6 overflow-y-auto h-[calc(100vh-280px)] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:'none'] [scrollbar-width:'none']">
        {activeTab === 'PIXEL' && (
          <div className="flex flex-col h-full">
            <div className="w-full flex items-center mb-4">
              <div className="flex items-center gap-2">
                <span className="text-sm text-gray-400">선택된 색상:</span>
                <ColorPicker />
              </div>
            </div>
            <div className="w-full">
              <CollaborativeCanvas />
            </div>
          </div>
        )}
      </div>

      {/* Sidebar */}
      <Sidebar />
    </div>
  );
};

export default MainPage;
