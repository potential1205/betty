import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamCode, formatTeamName, teamColors, teamTokenPrices } from '../constants/dummy';
import bettyImg from '../assets/bettycoin.png';
import backImg from '../assets/back_black.png';
import { useStore } from '../stores/useStore';
import CandleChart from './CandleChart';

const dummyDailyData = [
  { time: '2024-04-01', open: 100, high: 108, low: 98, close: 106 },
  { time: '2024-04-02', open: 106, high: 110, low: 104, close: 108 },
  { time: '2024-04-03', open: 108, high: 109, low: 102, close: 103 },
  { time: '2024-04-04', open: 103, high: 112, low: 102, close: 111 },
];

const dummyHourlyData = [
  { time: '2024-04-04T00:00:00', open: 103, high: 104, low: 102.5, close: 103.5 },
  { time: '2024-04-04T01:00:00', open: 103.5, high: 104.2, low: 102.8, close: 103 },
  { time: '2024-04-04T02:00:00', open: 103, high: 103.5, low: 101.5, close: 102 },
  { time: '2024-04-04T03:00:00', open: 102, high: 102.2, low: 100.8, close: 101 },
  { time: '2024-04-04T04:00:00', open: 101, high: 102, low: 100.5, close: 101.2 },
  { time: '2024-04-04T05:00:00', open: 101.2, high: 103.5, low: 101.1, close: 103 },
  { time: '2024-04-04T06:00:00', open: 103, high: 106, low: 102.9, close: 105 },
  { time: '2024-04-04T07:00:00', open: 105, high: 106.5, low: 104.5, close: 106 },
  { time: '2024-04-04T08:00:00', open: 106, high: 108, low: 105.5, close: 107.8 },
  { time: '2024-04-04T09:00:00', open: 107.8, high: 110, low: 107.6, close: 109 },
  { time: '2024-04-04T10:00:00', open: 109, high: 111, low: 108.5, close: 110.5 },
  { time: '2024-04-04T11:00:00', open: 110.5, high: 112, low: 109.5, close: 111.8 },
];

type TeamColor = {
  bg: string;
  text: string;
};

type TeamColors = {
  [key: string]: TeamColor;
};

interface BuyFanTokenProps {
  isOpen: boolean;
  onClose: () => void;
  team: string;
  price: number;
}

type Mode = 'buy' | 'swap' | 'sell';

const BuyFanToken: React.FC<BuyFanTokenProps> = ({ isOpen, onClose, team, price }) => {
  const [amount, setAmount] = useState<string>('');
  const [showSuccess, setShowSuccess] = useState(false);
  const [mode, setMode] = useState<Mode>('buy');
  const [selectedToken, setSelectedToken] = useState<string | null>(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const { userTokens, bettyBalance, setBettyBalance, bettyPrice } = useStore();

  const handleNumberClick = (num: string) => {
    if (amount.length < 10) {
      setAmount(prev => prev + num);
    }
  };

  const handleDelete = () => {
    setAmount(prev => prev.slice(0, -1));
  };

  const handleAction = () => {
    if (!amount) return;
    
    const { addTransaction, updateWalletBalance, updateTokenBalance, useTeamToken } = useStore.getState();
    const numAmount = Number(amount);
    
    switch (mode) {
      case 'buy':
        // BETTY 잔액 감소 및 토큰 증가
        if (bettyBalance < totalPrice) return;
        setBettyBalance(bettyBalance - totalPrice);
        // BET 가치 계산 (1 BETTY = 1 BET)
        const betValue = totalPrice;
        updateTokenBalance(team, numAmount, betValue);
        break;
        
      case 'sell':
        // 토큰 감소 및 BETTY 잔액 증가
        const sellPrice = numAmount * (teamTokenPrices.find(t => t.team === selectedToken)?.price || 0);
        if (useTeamToken(selectedToken!, numAmount)) {
          setBettyBalance(bettyBalance + sellPrice);
          // BET 가치 계산 (1 BETTY = 1 BET)
          const sellBtcValue = sellPrice;
          updateTokenBalance(selectedToken!, -numAmount, -sellBtcValue);
        }
        break;
        
      case 'swap':
        // 현재 토큰 감소 및 새로운 토큰 증가
        const swapPrice = numAmount * price;
        if (bettyBalance < swapPrice) return;
        if (useTeamToken(selectedToken!, numAmount)) {
          setBettyBalance(bettyBalance - swapPrice);
          // BET 가치 계산 (1 BETTY = 1 BET)
          const swapBtcValue = swapPrice;
          updateTokenBalance(selectedToken!, -numAmount, -swapBtcValue);
          updateTokenBalance(team, numAmount, swapBtcValue);
        }
        break;
    }
    
    setShowSuccess(true);
    setTimeout(() => {
      setShowSuccess(false);
      setAmount('');
      onClose();
    }, 1500);
  };

  // 총 가격 계산
  const totalPrice = amount ? Number(amount) * price : 0;

  // 모드 변경 시 초기화 및 초기 선택 토큰 설정
  const handleModeChange = (newMode: Mode) => {
    setMode(newMode);
    setAmount('');
    setIsDropdownOpen(false);
    
    if (newMode === 'swap' || newMode === 'sell') {
      // 현재 팀이 아닌 다른 보유 토큰 중 첫 번째를 선택
      const availableToken = userTokens.find(token => token.team !== team);
      setSelectedToken(availableToken?.team || null);
    } else {
      setSelectedToken(null);
    }
  };

  // 컴포넌트 마운트 시 초기 선택 토큰 설정
  useEffect(() => {
    if (isOpen && (mode === 'swap' || mode === 'sell')) {
      const availableToken = userTokens.find(token => token.team !== team);
      setSelectedToken(availableToken?.team || null);
    }
  }, [isOpen, mode, team, userTokens]);

  if (!isOpen) return null;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 bg-white z-50"
      style={{
        width: '360px',
        height: '743px',
        position: 'absolute',
        top: '50%',
        left: '50%',
        transform: 'translate(-50%, -50%)',
        overflow: 'hidden'
      }}
    >
      {/* 헤더 */}
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <div className="w-[12px] h-[12px]" />
        <h1 className="text-lg font-['Giants-Bold'] text-gray-800">팬토큰 거래</h1>
        <button 
          onClick={onClose}
          className="w-5 h-5 flex items-center justify-center text-gray-400 hover:text-gray-600"
        >
          <svg
            className="w-full h-full"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M6 18L18 6M6 6l12 12"
            />
          </svg>
        </button>
      </div>

      {/* 메인 컨텐츠 */}
      <div className="h-[calc(100%-56px)] flex flex-col pt-12">
        <div className="flex-1 p-4">
          {/* 모드 선택 */}
          <div className="flex justify-between mb-4">
            {(['buy', 'swap', 'sell'] as Mode[]).map((tabMode) => (
              <button
                key={tabMode}
                onClick={() => handleModeChange(tabMode)}
                className="relative py-2 text-sm font-['Giants-Bold'] transition-colors flex-1"
              >
                <span className={mode === tabMode ? 'text-blue-500' : 'text-gray-400'}>
                  {tabMode === 'buy' ? '구매하기' : tabMode === 'swap' ? '스왑하기' : '판매하기'}
                </span>
                {mode === tabMode && (
                  <motion.div
                    layoutId="underline"
                    className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-500"
                  />
                )}
              </button>
            ))}
          </div>

          {/* 차트 영역 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-4">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <div 
                  className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-['Giants-Bold']"
                  style={{ 
                    backgroundColor: (teamColors as TeamColors)[mode === 'sell' ? (selectedToken || team) : team].bg,
                    color: (teamColors as TeamColors)[mode === 'sell' ? (selectedToken || team) : team].text 
                  }}
                >
                  {formatTeamCode(mode === 'sell' ? (selectedToken || team) : team)}
                </div>
                <span className="text-sm text-gray-800 font-['Giants-Bold']">
                  {formatTeamName(mode === 'sell' ? (selectedToken || team) : team)} 가격 차트
                </span>
              </div>
              <div className="flex items-center gap-1">
                <span className="text-sm text-gray-800 font-['Giants-Bold']">
                  {mode === 'sell' 
                    ? teamTokenPrices.find(t => t.team === selectedToken)?.price || price
                    : price}
                </span>
                <img src={bettyImg} alt="BETTY" className="w-4 h-4" />
              </div>
            </div>
            <div className="h-56 bg-white rounded-lg [-1]">
              <CandleChart dailyData={dummyDailyData} hourlyData={dummyHourlyData} />
            </div>
          </div>

          {/* 토큰 선택 (스왑/판매 모드) */}
          {(mode === 'swap' || mode === 'sell') && (
            <div className="relative mb-4">
              <button
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="w-full bg-white rounded-lg p-3 flex items-center justify-between border border-gray-200"
              >
                <div className="flex items-center gap-2">
                  <div 
                    className="w-7 h-7 rounded-full flex items-center justify-center text-sm font-['Giants-Bold']"
                    style={{ 
                      backgroundColor: (teamColors as TeamColors)[selectedToken || team].bg,
                      color: (teamColors as TeamColors)[selectedToken || team].text 
                    }}
                  >
                    {formatTeamCode(selectedToken || team)}
                  </div>
                  <span className="text-base text-gray-800 font-['Giants-Bold']">
                    내가 가진 토큰
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1">
                    <span className="text-base text-gray-800 font-['Giants-Bold']">
                      {selectedToken ? teamTokenPrices.find(t => t.team === selectedToken)?.price : '0'}
                    </span>
                    <img src={bettyImg} alt="BETTY" className="w-5 h-5" />
                  </div>
                  <svg
                    className={`w-5 h-5 text-gray-500 transition-transform ${isDropdownOpen ? 'rotate-180' : ''}`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </div>
              </button>
              {isDropdownOpen && (
                <div className="absolute top-full left-0 right-0 mt-1 bg-white rounded-lg shadow-lg z-10 max-h-60 overflow-y-auto border border-gray-200">
                  {userTokens
                    .filter(token => token.team !== team && token.amount > 0)
                    .map(token => (
                      <button
                        key={token.team}
                        onClick={() => {
                          setSelectedToken(token.team);
                          setIsDropdownOpen(false);
                        }}
                        className={`w-full p-3 flex items-center justify-between hover:bg-gray-50 ${
                          selectedToken === token.team ? 'bg-blue-50' : ''
                        }`}
                      >
                        <div className="flex items-center gap-2">
                          <div 
                            className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-['Giants-Bold']"
                            style={{ 
                              backgroundColor: (teamColors as TeamColors)[token.team].bg,
                              color: (teamColors as TeamColors)[token.team].text 
                            }}
                          >
                            {formatTeamCode(token.team)}
                          </div>
                          <span className={`text-sm font-['Giants-Bold'] ${
                            selectedToken === token.team ? 'text-blue-500' : 'text-gray-800'
                          }`}>
                            {formatTeamName(token.team)}
                          </span>
                        </div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm text-gray-500">{token.amount}개</span>
                          <span className="text-sm text-gray-500">{teamTokenPrices.find(t => t.team === token.team)?.price}</span>
                        </div>
                      </button>
                    ))}
                </div>
              )}
            </div>
          )}

          {/* 금액 입력 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-4">
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm text-gray-500">
                {mode === 'buy' ? '구매할 수량' : mode === 'swap' ? '스왑할 수량' : '판매할 수량'}
              </span>
              <span className="text-sm text-gray-500">
                {mode === 'sell' 
                  ? `보유: ${selectedToken ? userTokens.find(t => t.team === selectedToken)?.amount || 0 : 0}개`
                  : `보유: ${bettyBalance.toLocaleString()} BETTY`}
              </span>
            </div>
            <div className="flex items-center justify-between mb-2">
              <input
                type="number"
                value={amount}
                onChange={(e) => {
                  const value = e.target.value;
                  if (value.length <= 10) {
                    setAmount(value);
                  }
                }}
                placeholder="0"
                className="text-xl font-['Giants-Bold'] text-gray-800 w-full text-right outline-none bg-transparent"
              />
              <span className="text-gray-500 ml-2">
                {mode === 'sell' ? '개' : 'BETTY'}
              </span>
            </div>
            {amount && (
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-500">총 {mode === 'sell' ? '판매' : '구매'}가</span>
                <div className="flex items-center gap-1">
                  <span className="text-gray-800 font-['Giants-Bold']">{totalPrice.toLocaleString()}</span>
                  <span className="text-gray-500">BETTY</span>
                </div>
              </div>
            )}
          </div>

          {/* 구매/스왑/판매 버튼 */}
          <button
            onClick={handleAction}
            disabled={!amount || (mode === 'sell' ? Number(amount) > (selectedToken ? userTokens.find(t => t.team === selectedToken)?.amount || 0 : 0) : totalPrice > bettyBalance)}
            className={`w-full h-11 rounded-lg text-white text-base font-['Giants-Bold'] transition-colors ${
              !amount || (mode === 'sell' ? Number(amount) > (selectedToken ? userTokens.find(t => t.team === selectedToken)?.amount || 0 : 0) : totalPrice > bettyBalance)
                ? 'bg-gray-300 cursor-not-allowed'
                : 'bg-blue-500 hover:bg-blue-600'
            }`}
          >
            {mode === 'buy' ? '구매하기' : mode === 'swap' ? '스왑하기' : '판매하기'}
          </button>
        </div>
      </div>

      <AnimatePresence>
        {showSuccess && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-white flex items-center justify-center pointer-events-none z-[60]"
          >
            <div className="flex flex-col items-center">
              <svg
                className="w-16 h-16 sm:w-24 sm:h-24 text-green-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <motion.path
                  initial={{ pathLength: 0 }}
                  animate={{ pathLength: 1 }}
                  transition={{ duration: 0.5 }}
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 13l4 4L21 7"
                />
              </svg>
              <motion.p
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.5 }}
                className="text-sm sm:text-base font-['Giants-Bold'] text-gray-800 mt-2 sm:mt-3"
              >
                {mode === 'buy' ? '구매되었습니다' : mode === 'swap' ? '스왑되었습니다' : '판매되었습니다'}
              </motion.p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default BuyFanToken;
