import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamCode, formatTeamName, teamColors, teamTokenPrices } from '../constants/dummy';
import bettyImg from '../assets/bettycoin.png';
import backImg from '../assets/back_black.png';

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

const BuyFanToken: React.FC<BuyFanTokenProps> = ({ isOpen, onClose, team, price }) => {
  const [amount, setAmount] = useState<string>('');
  const [showSuccess, setShowSuccess] = useState(false);
  const [isSwapMode, setIsSwapMode] = useState(false);
  const [selectedToken, setSelectedToken] = useState<string | null>(null);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const handleNumberClick = (num: string) => {
    if (amount.length < 10) {
      setAmount(prev => prev + num);
    }
  };

  const handleDelete = () => {
    setAmount(prev => prev.slice(0, -1));
  };

  const handleBuy = () => {
    if (!amount) return;
    
    setShowSuccess(true);
    setTimeout(() => {
      setShowSuccess(false);
      setAmount('');
      onClose();
    }, 1500);
  };

  // 총 가격 계산
  const totalPrice = amount ? Number(amount) * price : 0;

  // 모드 변경 시 초기화
  const handleModeChange = (isSwap: boolean) => {
    setIsSwapMode(isSwap);
    setAmount('');
    setSelectedToken(null);
    setIsDropdownOpen(false);
  };

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
      <div className="flex justify-between items-center p-3 px-4 border-b">
        <button 
          onClick={onClose} 
          className="w-[12px] h-[12px]"
        >
          <img src={backImg} alt="Back" className="w-full h-full" />
        </button>
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
      <div className="h-[calc(100%-56px)] overflow-y-auto">
        <div className="p-4">
          {/* 구매/스왑 모드 선택 */}
          <div className="flex justify-center gap-8 mb-4">
            <button
              onClick={() => handleModeChange(false)}
              className="relative py-2 text-sm font-['Giants-Bold'] transition-colors"
            >
              <span className={!isSwapMode ? 'text-blue-500' : 'text-gray-400'}>
                BETTY로 구매
              </span>
              {!isSwapMode && (
                <motion.div
                  layoutId="underline"
                  className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-500"
                />
              )}
            </button>
            <button
              onClick={() => handleModeChange(true)}
              className="relative py-2 text-sm font-['Giants-Bold'] transition-colors"
            >
              <span className={isSwapMode ? 'text-blue-500' : 'text-gray-400'}>
                토큰으로 스왑
              </span>
              {isSwapMode && (
                <motion.div
                  layoutId="underline"
                  className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-500"
                />
              )}
            </button>
          </div>

          {/* 차트 영역 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-4">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <div 
                  className="w-6 h-6 rounded-full flex items-center justify-center text-xs font-['Giants-Bold']"
                  style={{ 
                    backgroundColor: (teamColors as TeamColors)[team].bg,
                    color: (teamColors as TeamColors)[team].text 
                  }}
                >
                  {formatTeamCode(team)}
                </div>
                <span className="text-sm text-gray-800 font-['Giants-Bold']">
                  {formatTeamName(team)} 가격 차트
                </span>
              </div>
              <div className="flex items-center gap-1">
                <span className="text-sm text-gray-800 font-['Giants-Bold']">{price}</span>
                <img src={bettyImg} alt="BETTY" className="w-4 h-4" />
              </div>
            </div>
            <div className="h-32 bg-white rounded-lg flex items-center justify-center">
              <p className="text-gray-400">차트 구현 예정</p>
            </div>
          </div>

          {/* 토큰 정보 및 스왑 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-4">
            <div className="relative">
              <button
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="w-full bg-white rounded-lg p-3 flex items-center justify-between"
              >
                <div className="flex items-center gap-2">
                  <div 
                    className="w-7 h-7 rounded-full flex items-center justify-center text-sm font-['Giants-Bold']"
                    style={{ 
                      backgroundColor: (teamColors as TeamColors)[isSwapMode ? (selectedToken || team) : team].bg,
                      color: (teamColors as TeamColors)[isSwapMode ? (selectedToken || team) : team].text 
                    }}
                  >
                    {formatTeamCode(isSwapMode ? (selectedToken || team) : team)}
                  </div>
                  <span className="text-base text-gray-800 font-['Giants-Bold']">
                    {formatTeamName(isSwapMode ? (selectedToken || team) : team)}
                  </span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1">
                    <span className="text-base text-gray-800 font-['Giants-Bold']">
                      {isSwapMode ? (selectedToken ? teamTokenPrices.find(t => t.team === selectedToken)?.price : '0') : price}
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
                <div className="absolute top-full left-0 right-0 mt-1 bg-white rounded-lg shadow-lg z-10 max-h-60 overflow-y-auto">
                  {teamTokenPrices
                    .filter(token => token.team !== (isSwapMode ? selectedToken : team))
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
                        <span className="text-sm text-gray-500">{token.price}</span>
                      </button>
                    ))}
                </div>
              )}
            </div>
          </div>

          {/* 금액 입력 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-4">
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm text-gray-500">
                {isSwapMode ? '스왑할 수량' : '구매할 수량'}
              </span>
              <span className="text-sm text-gray-500">보유: 1,000 BETTY</span>
            </div>
            <div className="flex items-center justify-between mb-2">
              <input
                type="text"
                value={amount}
                readOnly
                placeholder="0"
                className="text-2xl font-['Giants-Bold'] text-gray-800 w-full text-right outline-none bg-transparent"
              />
              <span className="text-gray-500 ml-2">BETTY</span>
            </div>
            {amount && (
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-500">총 가격</span>
                <div className="flex items-center gap-1">
                  <span className="text-gray-800 font-['Giants-Bold']">{totalPrice.toLocaleString()}</span>
                  <span className="text-gray-500">BETTY</span>
                </div>
              </div>
            )}
          </div>

          {/* 계산기 */}
          <div className="grid grid-cols-3 gap-1.5">
            {[1, 2, 3, 4, 5, 6, 7, 8, 9].map(num => (
              <button
                key={num}
                onClick={() => handleNumberClick(num.toString())}
                className="h-11 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 text-base font-['Giants-Bold'] transition-colors"
              >
                {num}
              </button>
            ))}
            <button
              onClick={handleDelete}
              className="h-11 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 text-base font-['Giants-Bold'] transition-colors"
            >
              ←
            </button>
            <button
              onClick={() => handleNumberClick('0')}
              className="h-11 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 text-base font-['Giants-Bold'] transition-colors"
            >
              0
            </button>
            <button
              onClick={handleBuy}
              disabled={!amount || totalPrice > 1000}
              className={`h-11 rounded-lg text-white text-base font-['Giants-Bold'] transition-colors ${
                !amount || totalPrice > 1000
                  ? 'bg-gray-300 cursor-not-allowed'
                  : 'bg-blue-500 hover:bg-blue-600'
              }`}
            >
              {isSwapMode ? '스왑하기' : '구매하기'}
            </button>
          </div>
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
                {isSwapMode ? '스왑되었습니다' : '구매되었습니다'}
              </motion.p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default BuyFanToken;
