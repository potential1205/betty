import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamCode, formatTeamName, teamColors } from '../constants/dummy';
import bettyImg from '../assets/bettycoin.png';

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

  if (!isOpen) return null;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 bg-black/60 flex items-center justify-center z-50"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        exit={{ scale: 0.9, opacity: 0 }}
        className="bg-white rounded-2xl w-[90vw] sm:w-[320px] max-w-[420px] overflow-hidden"
        onClick={e => e.stopPropagation()}
      >
        <div className="p-3 sm:p-4 bg-gray-100">
          <h2 className="text-base sm:text-lg font-['Giants-Bold'] text-center text-gray-800">
            몇 BETTY를 채울까요?
          </h2>
          <p className="text-[10px] sm:text-xs text-center text-gray-500 mt-0.5 sm:mt-1">
            다른 계정에서 입력한 금액 확인하기
          </p>
        </div>

        <div className="p-4 sm:p-6">
          <div className="flex items-center justify-between mb-4 sm:mb-6">
            <div className="flex items-center gap-1.5 sm:gap-2">
              <div 
                className="w-6 h-6 sm:w-8 sm:h-8 rounded-full flex items-center justify-center text-xs sm:text-sm font-['Giants-Bold']"
                style={{ 
                  backgroundColor: (teamColors as TeamColors)[team].bg,
                  color: (teamColors as TeamColors)[team].text 
                }}
              >
                {formatTeamCode(team)}
              </div>
              <span className="text-sm sm:text-base text-gray-800 font-['Giants-Bold']">
                {formatTeamName(team)}
              </span>
            </div>
            <div className="flex items-center gap-1">
              <span className="text-sm sm:text-base text-gray-800 font-['Giants-Bold']">{price}</span>
              <img src={bettyImg} alt="BETTY" className="w-4 h-4 sm:w-5 sm:h-5" />
            </div>
          </div>

          <div className="flex items-center justify-between mb-6 sm:mb-8">
            <input
              type="text"
              value={amount}
              readOnly
              placeholder="0"
              className="text-xl sm:text-2xl font-['Giants-Bold'] text-gray-800 w-full text-right outline-none"
            />
            <span className="text-sm sm:text-base text-gray-400 ml-2">BETTY</span>
          </div>

          <div className="grid grid-cols-3 gap-2 sm:gap-4">
            {[1, 2, 3, 4, 5, 6, 7, 8, 9].map(num => (
              <button
                key={num}
                onClick={() => handleNumberClick(num.toString())}
                className="h-10 sm:h-12 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 text-sm sm:text-base font-['Giants-Bold'] transition-colors"
              >
                {num}
              </button>
            ))}
            <button
              onClick={handleDelete}
              className="h-10 sm:h-12 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 text-sm sm:text-base font-['Giants-Bold'] transition-colors"
            >
              ←
            </button>
            <button
              onClick={() => handleNumberClick('0')}
              className="h-10 sm:h-12 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-800 text-sm sm:text-base font-['Giants-Bold'] transition-colors"
            >
              0
            </button>
            <button
              onClick={handleBuy}
              className="h-10 sm:h-12 rounded-lg bg-blue-500 hover:bg-blue-600 text-white text-sm sm:text-base font-['Giants-Bold'] transition-colors"
            >
              확인
            </button>
          </div>
        </div>
      </motion.div>

      <AnimatePresence>
        {showSuccess && (
          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.8, opacity: 0 }}
            className="fixed inset-0 flex items-center justify-center pointer-events-none"
          >
            <div className="bg-white rounded-full p-8 sm:p-12 flex flex-col items-center">
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
                구매되었습니다
              </motion.p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default BuyFanToken;
