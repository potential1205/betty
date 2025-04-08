import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useStore } from '../stores/useStore';
import bettyImg from '../assets/bettycoin.png';
import { addBettyCoin } from '../apis/exchangeApi';

interface ChargeModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const ChargeModal: React.FC<ChargeModalProps> = ({ isOpen, onClose }) => {
  const [amount, setAmount] = React.useState<number>(0);
  const [customAmount, setCustomAmount] = React.useState<string>('');
  const [showSuccess, setShowSuccess] = React.useState(false);
  const { chargeBetty, walletInfo } = useStore();
  
  // 원화를 BET로 변환하는 함수 (1 BET = 100원 기준)
  const convertToBET = (won: number) => won / 100;
  
  // BET 금액 포맷팅 함수
  const formatBET = (bet: number) => bet.toFixed(2);
  
  const amounts = [10000, 50000, 100000, 500000];

  const handleCustomAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/[^0-9]/g, '');
    setCustomAmount(value);
    setAmount(Number(value));
  };

  const handleNumberClick = (num: string) => {
    if (customAmount.length < 10) {
      const newAmount = customAmount + num;
      setCustomAmount(newAmount);
      setAmount(Number(newAmount));
    }
  };

  const handleDelete = () => {
    const newAmount = customAmount.slice(0, -1);
    setCustomAmount(newAmount);
    setAmount(Number(newAmount));
  };

  const handleCharge = async () => {
    if (amount <= 0) return;

    try {
      await addBettyCoin(amount);
      chargeBetty(amount / 100);
      setShowSuccess(true);
      setTimeout(() => {
        setShowSuccess(false);
        setAmount(0);
        setCustomAmount('');
        onClose();
      }, 1500);
    } catch (err) {
      console.error('충전 실패:', err);
      alert('충전에 실패했습니다. 다시 시도해주세요.');
    }
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
      <div className="absolute top-0 left-0 right-0 z-10 flex justify-between items-center p-6 px-8">
        <div className="w-[12px] h-[12px]" />
        <h1 className="text-lg font-['Giants-Bold'] text-gray-800">BET 충전하기</h1>
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
      <div className="h-[calc(100%-56px)] flex flex-col justify-center">
        <div className="p-4 flex flex-col">
          {/* 현재 보유 BET */}
          <div className="bg-gradient-to-br from-black to-gray-800 rounded-xl p-3 shadow-lg mb-4">
            <p className="text-xs text-gray-400 mb-1">현재 보유 BET</p>
            <div className="flex items-baseline">
              <p className="text-xl font-['Giants-Bold'] text-white">{formatBET(walletInfo.totalBET)}</p>
              <p className="text-sm text-gray-400 ml-1">BET</p>
            </div>
          </div>

          {/* 금액 선택 버튼들 */}
          <div className="grid grid-cols-2 gap-3 mb-6">
            {amounts.map((value) => (
              <button
                key={value}
                onClick={() => {
                  setAmount(value);
                  setCustomAmount('');
                }}
                className={`p-4 rounded-xl text-center transition-colors
                  ${amount === value && !customAmount
                    ? 'bg-black text-white' 
                    : 'bg-gray-100 text-black hover:bg-gray-200'}`}
              >
                <p className="text-sm mb-1">충전금액</p>
                <p className="text-lg font-['Giants-Bold']">
                  {value.toLocaleString()}원
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  {formatBET(convertToBET(value))} BET
                </p>
              </button>
            ))}
          </div>

          {/* 직접 입력 필드 */}
          <div className="bg-gray-100 rounded-lg p-3 mb-6">
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm text-gray-500">직접 입력하기</span>
            </div>
            <div className="flex items-center justify-between">
              <input
                type="text"
                value={customAmount}
                onChange={handleCustomAmountChange}
                placeholder="0"
                className="text-xl font-['Giants-Bold'] text-gray-800 w-full outline-none bg-transparent"
              />
              <span className="text-gray-500 ml-2">원</span>
            </div>
            {customAmount && (
              <div className="flex items-center justify-between text-sm mt-2">
                <span className="text-gray-500">BET 환산</span>
                <div className="flex items-center gap-1">
                  <span className="text-gray-800 font-['Giants-Bold']">{formatBET(convertToBET(Number(customAmount)))}</span>
                  <span className="text-gray-500">BET</span>
                </div>
              </div>
            )}
          </div>

          {/* 버튼 */}
          <button
            onClick={handleCharge}
            disabled={amount === 0}
            className={`w-full py-4 rounded-xl text-white text-base font-['Giants-Bold'] transition-colors ${
              amount > 0
                ? 'bg-black hover:bg-gray-800'
                : 'bg-gray-300 cursor-not-allowed'
            }`}
          >
            충전하기
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
                충전되었습니다
              </motion.p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default ChargeModal;
