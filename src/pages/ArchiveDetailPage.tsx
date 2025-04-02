import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useStore } from '../stores/useStore';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import Sidebar from '../components/Sidebar';

interface PixelArt {
  id: number;
  title: string;
  image: string;
  description: string;
  creator: string;
  createdAt: string;
  matchTeams: string[];
}

const ArchiveDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { art } = location.state as { art: PixelArt };
  const { toggleSidebar } = useStore();

  return (
    <div className="relative h-full bg-black text-white overflow-hidden">
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-4 px-6">
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

      {/* 스크롤 영역 */}
      <div className="h-full pt-16 overflow-y-auto scrollbar-hide">
        <div className="px-6">
          {/* 픽셀 아트 이미지 */}
          <div className="aspect-square rounded-xl overflow-hidden mb-4">
            <img
              src={art.image}
              alt={art.title}
              className="w-full h-full object-cover"
            />
          </div>

          {/* 픽셀 아트 정보 */}
          <div className="space-y-3">
            <div>
              <h3 className="text-xl font-['Giants-Bold'] text-white mb-1">{art.title}</h3>
              <p className="text-gray-400 text-sm">{art.matchTeams.join(' vs ')}</p>
            </div>

            <div>
              <p className="text-xs text-gray-400 mb-0.5">크리에이터</p>
              <p className="text-sm text-white">{art.creator}</p>
            </div>

            <div>
              <p className="text-xs text-gray-400 mb-0.5">설명</p>
              <p className="text-sm text-white whitespace-pre-wrap">{art.description}</p>
            </div>

            <div>
              <p className="text-xs text-gray-400 mb-0.5">생성일</p>
              <p className="text-sm text-white">{art.createdAt}</p>
            </div>
          </div>
        </div>
      </div>

      <Sidebar />
    </div>
  );
};

export default ArchiveDetailPage; 