import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { useStore } from '../stores/useStore';
import { teamTokenPrices, teamChartData, teamColors, formatTeamCode, formatTeamName } from '../constants/dummy';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import bettyImg from '../assets/bettycoin.png';
import Sidebar from '../components/Sidebar';
import BuyFanToken from '../components/BuyFanToken';

type TeamColor = {
  bg: string;
  text: string;
};

type TeamColors = {
  [key: string]: TeamColor;
};

const ExchangePage: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar, bettyPrice } = useStore();
  const [selectedTeam, setSelectedTeam] = useState<string | null>(null);
  const [showBuyModal, setShowBuyModal] = useState(false);

  const handleTeamClick = (team: string) => {
    setSelectedTeam(team);
    setShowBuyModal(true);
  };

  const selectedToken = selectedTeam ? teamTokenPrices.find(t => t.team === selectedTeam) : null;

  return (
    <div className="relative h-full bg-black text-white flex flex-col">
      {showBuyModal && selectedToken ? (
        <BuyFanToken
          isOpen={showBuyModal}
          onClose={() => setShowBuyModal(false)}
          team={selectedToken.team}
          price={selectedToken.price}
        />
      ) : (
        <>
          {/* 헤더 */}
          <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
            <button 
              onClick={() => navigate(-1)} 
              className="w-[12px] h-[12px]"
            >
              <img src={backImg} alt="Back" className="w-full h-full" />
            </button>
            <div className="flex flex-col items-center">
              <h1 className="text-lg font-['Giants-Bold']">팬토큰 거래소</h1>
              <p className="text-xs text-gray-400 mt-1">BETTY {bettyPrice.toFixed(4)} BTC</p>
            </div>
            <div className="w-5 h-5 flex items-center justify-center">
              <button 
                onClick={() => toggleSidebar(true)}
                className="w-5 h-5"
              >
                <img src={hamburgerImg} alt="Menu" className="w-full h-full" />
              </button>
            </div>
          </div>

          {/* 메인 컨텐츠 */}
          <div className="flex-1 overflow-y-auto pt-16">
            <style>{`
              ::-webkit-scrollbar {
                display: none;
              }
              * {
                -ms-overflow-style: none;
                scrollbar-width: none;
              }
            `}</style>

            <div className="p-3 min-h-full">
              {/* 컬럼 헤더 */}
              <div className="flex items-center px-3 mb-2 text-gray-400 text-xs sticky top-0 bg-black z-10">
                <div className="w-[140px] pl-2">구단</div>
                <div className="w-[80px] text-right">등락률</div>
                <div className="w-[100px] text-right">현재가</div>
              </div>
              
              {/* 팬토큰 목록 */}
              <div className="grid grid-cols-1 gap-[6px] pb-4">
                {teamTokenPrices.map((token) => (
                  <motion.div
                    key={token.team}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className={`bg-gray-800/50 rounded-lg p-2.5 cursor-pointer transition-transform hover:scale-[1.01]`}
                    onClick={() => handleTeamClick(token.team)}
                    style={{
                      borderTop: '1px solid rgba(255, 255, 255, 0.1)',
                      borderBottom: '1px solid rgba(255, 255, 255, 0.1)'
                    }}
                  >
                    <div className="flex items-center">
                      <div className="w-[140px] flex items-center gap-2">
                        <div 
                          className="w-8 h-8 rounded-full flex items-center justify-center text-sm font-['Giants-Bold']"
                          style={{ 
                            backgroundColor: (teamColors as TeamColors)[token.team].bg,
                            color: (teamColors as TeamColors)[token.team].text 
                          }}
                        >
                          {formatTeamCode(token.team)}
                        </div>
                        <div>
                          <h3 className="text-base text-white font-['Giants-Bold']">
                            {formatTeamName(token.team)}
                          </h3>
                        </div>
                      </div>
                      <div className="w-[80px] text-right">
                        <p className={`text-sm ${token.change24h >= 0 ? 'text-red-400' : 'text-blue-400'}`}>
                          {token.change24h >= 0 ? '+' : ''}{token.change24h}%
                        </p>
                      </div>
                      <div className="w-[100px] flex items-center gap-1 justify-end">
                        <p className="text-base font-['Giants-Bold']">{token.price}</p>
                        <img src={bettyImg} alt="BETTY" className="w-5 h-5" />
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          </div>

          <Sidebar />
        </>
      )}
    </div>
  );
};

export default ExchangePage;
