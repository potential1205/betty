import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { useStore } from '../stores/useStore';
import { teamColors, formatTeamCode, formatTeamName } from '../constants/dummy';
import { teamToTokenIdMap } from '../constants/tokenMap';
import backImg from '../assets/back.png';
import hamburgerImg from '../assets/hamburger.png';
import bettyImg from '../assets/bettycoin.png';
import Sidebar from '../components/Sidebar';
import BuyFanToken from '../components/BuyFanToken';
import { getAllTokenPrices, TokenPrice } from '../apis/exchangeApi';

type TeamColor = {
  bg: string;
  text: string;
};

type TeamColors = {
  [key: string]: TeamColor;
};

// TokenId를 팀 이름으로 매핑하는 객체 생성
const tokenIdToTeam: { [key: number]: string } = Object.entries(teamToTokenIdMap).reduce(
  (acc, [team, id]) => ({ ...acc, [id]: team }),
  {}
);

const ExchangePage: React.FC = () => {
  const navigate = useNavigate();
  const { toggleSidebar, bettyPrice } = useStore();
  const [selectedTeam, setSelectedTeam] = useState<string | null>(null);
  const [showBuyModal, setShowBuyModal] = useState(false);
  const [tokenPrices, setTokenPrices] = useState<TokenPrice[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());
  const [timeUntilRefresh, setTimeUntilRefresh] = useState<number>(60);
  
  // 토큰 가격 정보 로드
  useEffect(() => {
    const loadTokenPrices = async () => {
      try {
        setIsLoading(true);
        const prices = await getAllTokenPrices();
        setTokenPrices(prices);
        setError(null);
        setLastUpdateTime(new Date());
        setTimeUntilRefresh(60);
      } catch (err) {
        console.error('토큰 가격 정보 로드 실패:', err);
        setError('토큰 가격 정보를 불러오는데 실패했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    loadTokenPrices();

    // 1분마다 가격 정보 업데이트
    const interval = setInterval(loadTokenPrices, 60000);
    
    // 타이머 업데이트 인터벌
    const timerInterval = setInterval(() => {
      setTimeUntilRefresh(prev => {
        // 토큰 가격이 없거나 로딩 중이면 타이머 업데이트하지 않음
        if (tokenPrices.length === 0 || isLoading) {
          return prev;
        }
        
        if (prev <= 1) return 60;
        return prev - 1;
      });
    }, 1000);
    
    return () => {
      clearInterval(interval);
      clearInterval(timerInterval);
    };
  }, []);

  const handleTeamClick = (tokenId: number, teamName: string) => {
    console.log('클릭한 팀:', teamName);
    console.log('토큰 ID:', tokenId);
    const tokenPrice = tokenPrices.find(t => t.token.id === tokenId);
    console.log('찾은 토큰 가격:', tokenPrice);
    setSelectedTeam(teamName);
    setShowBuyModal(true);
  };

  // tokenId로 직접 가격을 찾도록 수정
  const selectedTokenPrice = selectedTeam ? 
    tokenPrices.find(t => t.token.id === teamToTokenIdMap[selectedTeam]) : null;

  console.log('최종 선택된 토큰 가격:', selectedTokenPrice);

  const getTeamNameFromTokenId = (tokenId: number): string => {
    return tokenIdToTeam[tokenId] || '';
  };

  // 마지막 업데이트 시간 포맷팅
  const formatLastUpdateTime = (date: Date): string => {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
  };

  const handleManualRefresh = async () => {
    try {
      setIsLoading(true);
      const prices = await getAllTokenPrices();
      setTokenPrices(prices);
      setError(null);
      setLastUpdateTime(new Date());
      setTimeUntilRefresh(60);
    } catch (err) {
      console.error('토큰 가격 정보 로드 실패:', err);
      setError('토큰 가격 정보를 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="relative h-full bg-black text-white flex flex-col">
      {showBuyModal ? (
        <BuyFanToken
          isOpen={showBuyModal}
          onClose={() => setShowBuyModal(false)}
          team={selectedTeam || ''}
          price={tokenPrices.find(t => t.token.id === teamToTokenIdMap[selectedTeam || ''])?.price || 0}
          tokenId={teamToTokenIdMap[selectedTeam || '']}
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

          {/* 상태 표시 영역 */}
          <div className="pt-16 px-4">
            <AnimatePresence>
              {isLoading ? (
                <motion.div 
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="bg-blue-500/20 rounded-lg p-3 mb-3 flex justify-between items-center"
                >
                  <div className="flex items-center">
                    <div className="w-4 h-4 border-2 border-blue-500 border-t-transparent rounded-full animate-spin mr-2"></div>
                    <span className="text-sm text-blue-400 font-['Giants-Bold']">토큰 정보 업데이트 중...</span>
                  </div>
                </motion.div>
              ) : error ? (
                <motion.div 
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="bg-red-500/20 rounded-lg p-3 mb-3 flex justify-between items-center"
                >
                  <span className="text-sm text-red-400 font-['Giants-Bold']">{error}</span>
                  <button 
                    onClick={handleManualRefresh}
                    className="text-xs bg-red-500/30 hover:bg-red-500/50 text-white px-2 py-1 rounded"
                  >
                    다시시도
                  </button>
                </motion.div>
              ) : (
                <motion.div 
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="bg-gray-700/30 rounded-lg p-3 mb-3 flex justify-between items-center"
                >
                  <div className="text-xs text-gray-400">
                    마지막 업데이트: {formatLastUpdateTime(lastUpdateTime)}
                  </div>
                  <div className="flex items-center">
                    <div className="text-xs text-gray-400 mr-2">
                      {timeUntilRefresh}초 후 새로고침
                    </div>
                    <button 
                      onClick={handleManualRefresh}
                      className="text-xs bg-gray-600/50 hover:bg-gray-500/50 text-white px-2 py-1 rounded"
                      disabled={isLoading}
                    >
                      <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                      </svg>
                    </button>
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* 메인 컨텐츠 */}
          <div className="flex-1 overflow-y-auto">
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
                <div className="flex-1 pl-2">구단</div>
                <div className="w-[100px] text-center">설명</div>
                <div className="w-[100px] text-right pr-2">현재가</div>
              </div>
              
              {/* 팬토큰 목록 */}
              <div className="grid grid-cols-1 gap-[6px] pb-4">
                {tokenPrices.length === 0 && !isLoading ? (
                  <div className="text-center py-10 text-gray-400">
                    표시할 토큰이 없습니다.
                  </div>
                ) : (
                  tokenPrices.map((token) => {
                    const teamName = getTeamNameFromTokenId(token.token.id);
                    return (
                      <motion.div
                        key={token.token.id}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        className="bg-gray-800/50 rounded-lg p-2.5 cursor-pointer transition-transform hover:scale-[1.01]"
                        onClick={() => handleTeamClick(token.token.id, teamName)}
                        style={{
                          borderTop: '1px solid rgba(255, 255, 255, 0.1)',
                          borderBottom: '1px solid rgba(255, 255, 255, 0.1)'
                        }}
                      >
                        <div className="flex items-center">
                          <div className="flex-1 flex items-center gap-2">
                            <div 
                              className="w-8 h-8 rounded-full flex items-center justify-center text-sm font-['Giants-Bold']"
                              style={{ 
                                backgroundColor: (teamColors as TeamColors)[teamName].bg,
                                color: (teamColors as TeamColors)[teamName].text 
                              }}
                            >
                              {formatTeamCode(teamName)}
                            </div>
                            <div>
                              <h3 className="text-base text-white font-['Giants-Bold']">
                                {formatTeamName(teamName)}
                              </h3>
                            </div>
                          </div>
                          <div className="w-[100px] flex flex-col items-center">
                            <p className="text-sm text-gray-400 leading-tight">
                              {token.token.description ? (
                                <>
                                  <span className="block text-center">{token.token.description.split(' ')[0]}</span>
                                  <span className="block text-center">{token.token.description.split(' ').slice(1).join(' ')}</span>
                                </>
                              ) : (
                                <span>-</span>
                              )}
                            </p>
                          </div>
                          <div className="w-[100px] flex items-center gap-1 justify-end pr-2">
                            <p className="text-base font-['Giants-Bold'] tabular-nums">
                              {token.price}
                            </p>
                            <img src={bettyImg} alt="BETTY" className="w-4 h-4" />
                          </div>
                        </div>
                      </motion.div>
                    );
                  })
                )}
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
