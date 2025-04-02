import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import { useStore } from '../stores/useStore';
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

const ArchivePage: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar } = useStore();
  const [searchTerm, setSearchTerm] = useState('');

  // 임시 데이터 - 실제로는 서버에서 가져올 예정
  const pixelArts: PixelArt[] = [
    {
      id: 1,
      title: "두산 vs 롯데 승리의 순간",
      image: "/pixels/doosan-lotte-1.jpg",
      description: "두산 vs 롯데 경기, 7회 말 역전의 순간을 담은 픽셀 아트",
      creator: "BETTY #1234",
      createdAt: "2024-01-15 20:45",
      matchTeams: ["두산", "롯데"]
    },
    {
      id: 2,
      title: "롯데 응원 픽셀 아트",
      image: "/pixels/lotte-cheer.jpg",
      description: "2024.01.15 롯데 자이언츠 응원단 모습을 담은 픽셀 아트",
      creator: "BETTY #5678",
      createdAt: "2024-01-15 19:30",
      matchTeams: ["두산", "롯데"]
    },
    {
      id: 3,
      title: "양의지 홈런 순간",
      image: "/pixels/doosan-homerun.jpg",
      description: "두산 베어스 양의지 선수의 역전 홈런 순간을 담은 픽셀 아트",
      creator: "BETTY #9012",
      createdAt: "2024-01-15 21:15",
      matchTeams: ["두산", "삼성"]
    },
    {
      id: 4,
      title: "삼성 라이온즈 끝내기 승리",
      image: "/pixels/samsung-victory.jpg",
      description: "삼성 라이온즈의 극적인 끝내기 승리 순간을 담은 픽셀 아트",
      creator: "BETTY #3456",
      createdAt: "2024-01-15 22:00",
      matchTeams: ["삼성", "SSG"]
    }
  ];

  const filteredArts = pixelArts.filter(art =>
    art.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
    art.matchTeams.some(team => team.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const handleArtClick = (art: PixelArt) => {
    navigate(`/archive/${art.id}`, { state: { art } });
  };

  return (
    <div className="relative h-full bg-black text-white overflow-hidden">
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <button 
          onClick={() => navigate(-1)} 
          className="w-[12px] h-[12px]"
        >
          <img src={backImg} alt="Back" className="w-full h-full" />
        </button>
        <h1 className="text-xl font-['Giants-Bold']">픽셀 아트 아카이브</h1>
        <div className="w-5 h-5 flex items-center justify-center">
          <button 
            onClick={() => toggleSidebar(true)}
            className="w-5 h-5"
          >
            <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
          </button>
        </div>
      </div>

      {/* 검색바 */}
      <div className="pt-20 px-6 pb-3">
        <input
          type="text"
          placeholder="픽셀 아트 또는 팀 검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-2 text-sm rounded-full bg-gray-800 text-white placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-red-500"
        />
      </div>

      {/* 픽셀 아트 리스트 */}
      <div className="h-[calc(100%-120px)] overflow-y-auto scrollbar-hide">
        <div className="px-6 py-3">
          <div className="grid grid-cols-2 gap-3">
            {filteredArts.map((art) => (
              <div 
                key={art.id}
                onClick={() => handleArtClick(art)}
                className="bg-gray-900 rounded-lg overflow-hidden border border-gray-800 cursor-pointer hover:border-red-500 transition-colors"
              >
                <div className="aspect-square relative">
                  <img
                    src={art.image}
                    alt={art.title}
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute bottom-1.5 left-1.5 px-1.5 py-0.5 rounded-full text-[10px] text-gray-300 bg-black/70">
                    {art.creator}
                  </div>
                </div>
                <div className="p-2">
                  <h3 className="font-['Giants-Bold'] text-xs mb-0.5 truncate">{art.title}</h3>
                  <p className="text-gray-400 text-[10px] mb-1.5">
                    {art.matchTeams.join(' vs ')}
                  </p>
                  <p className="text-gray-500 text-[10px]">{art.createdAt}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <Sidebar />
    </div>
  );
};

export default ArchivePage; 