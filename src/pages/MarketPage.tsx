import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import { useStore } from '../stores/useStore';
import type { NFT } from '../constants/dummy';
import Sidebar from '../components/Sidebar';

const Market: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar, marketNFTs, saveNFTToMyPage } = useStore();
  const [searchTerm, setSearchTerm] = useState('');

  const filteredNFTs = marketNFTs.filter(nft =>
    nft.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    nft.matchTeams.some(team => team.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const handleNFTClick = (nft: NFT) => {
    navigate(`/store/${nft.id}`, { state: { nft } });
  };

  const handleSaveNFT = (nft: NFT, e: React.MouseEvent) => {
    e.stopPropagation();
    saveNFTToMyPage(nft);
    alert('NFT가 마이페이지에 저장되었습니다!');
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
        <h1 className="text-xl font-['Giants-Bold']">NFT 아카이브</h1>
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
          placeholder="NFT 또는 팀 검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full px-4 py-2 text-sm rounded-full bg-gray-800 text-white placeholder-gray-400 focus:outline-none focus:ring-1 focus:ring-red-500"
        />
      </div>

      {/* NFT 리스트 */}
      <div className="h-[calc(100%-120px)] overflow-y-auto scrollbar-hide">
        <div className="px-6 py-3">
          <div className="grid grid-cols-2 gap-3">
            {filteredNFTs.map((nft) => (
              <div 
                key={nft.id}
                onClick={() => handleNFTClick(nft)}
                className="bg-gray-900 rounded-lg overflow-hidden border border-gray-800 cursor-pointer hover:border-red-500 transition-colors"
              >
                <div className="aspect-square relative">
                  <img
                    src={nft.image}
                    alt={nft.name}
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute bottom-1.5 left-1.5 px-1.5 py-0.5 rounded-full text-[10px] text-gray-300 bg-black/70">
                    {nft.creator}
                  </div>
                </div>
                <div className="p-2">
                  <h3 className="font-['Giants-Bold'] text-xs mb-0.5 truncate">{nft.name}</h3>
                  <p className="text-gray-400 text-[10px] mb-1.5">
                    {nft.matchTeams.join(' vs ')}
                  </p>
                  <button
                    onClick={(e) => handleSaveNFT(nft, e)}
                    className="w-full py-1.5 text-xs bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors"
                  >
                    마이페이지에 저장
                  </button>
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

export default Market;
