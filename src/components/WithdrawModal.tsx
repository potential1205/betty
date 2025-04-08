import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useStore } from '../stores/useStore';

interface WithdrawModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const WithdrawModal: React.FC<WithdrawModalProps> = ({ isOpen, onClose }) => {
  const { walletInfo } = useStore();
  const [amount, setAmount] = useState('');
  const [customAmount, setCustomAmount] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  // 출금 가능한 금액 옵션들 (현재 보유 BTC의 25%, 50%, 75%, 100%)
  const getWithdrawOptions = () => {
    const totalBTC = walletInfo.totalBTC || 0;
    return [
      { percent: 25, amount: totalBTC * 0.25 },
      { percent: 50, amount: totalBTC * 0.5 },
      { percent: 75, amount: totalBTC * 0.75 },
      { percent: 100, amount: totalBTC }
    ];
  };

  const handleWithdraw = async () => {
    if (!amount) return;
    
    try {
      setIsLoading(true);
      // TODO: Implement withdrawal API call
      console.log('Withdrawing', amount, 'BTC');
      setShowSuccess(true);
      setTimeout(() => {
        setShowSuccess(false);
        setAmount('');
        setCustomAmount('');
        onClose();
      }, 1500);
    } catch (error) {
      console.error('Withdrawal failed:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCustomAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    // 소수점 두 자리까지만 허용하는 정규식
    if (/^\d*\.?\d{0,2}$/.test(value) && Number(value) <= (walletInfo.totalBTC || 0)) {
      setCustomAmount(value);
      setAmount(value);
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
        <h1 className="text-lg font-['Giants-Bold'] text-gray-800">BTC 출금하기</h1>
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
          {/* 현재 보유 BTC */}
          <div className="bg-gradient-to-br from-black to-gray-800 rounded-xl p-3 shadow-lg mb-4">
            <p className="text-xs text-gray-400 mb-1">현재 보유 BTC</p>
            <div className="flex items-baseline">
              <p className="text-xl font-['Giants-Bold'] text-white">{walletInfo.totalBTC?.toFixed(2) || '0.00'}</p>
              <p className="text-sm text-gray-400 ml-1">BTC</p>
            </div>
          </div>

          {/* 금액 선택 버튼들 */}
          <div className="grid grid-cols-2 gap-3 mb-6">
            {getWithdrawOptions().map(({ percent, amount: btcAmount }) => (
              <button
                key={percent}
                onClick={() => {
                  setAmount(btcAmount.toFixed(2));
                  setCustomAmount('');
                }}
                className={`p-4 rounded-xl text-center transition-colors
                  ${Number(amount) === Number(btcAmount.toFixed(2)) && !customAmount
                    ? 'bg-black text-white' 
                    : 'bg-gray-100 text-black hover:bg-gray-200'}`}
              >
                <p className="text-sm mb-1">출금금액</p>
                <p className="text-lg font-['Giants-Bold']">
                  {percent}%
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  {btcAmount.toFixed(2)} BTC
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
                placeholder="0.00"
                className="text-xl font-['Giants-Bold'] text-gray-800 w-full outline-none bg-transparent"
              />
              <span className="text-gray-500 ml-2">BTC</span>
            </div>
          </div>

          {/* 버튼 */}
          <button
            onClick={handleWithdraw}
            disabled={isLoading || !amount}
            className={`w-full py-4 rounded-xl text-white text-base font-['Giants-Bold'] transition-colors ${
              amount
                ? 'bg-black hover:bg-gray-800'
                : 'bg-gray-300 cursor-not-allowed'
            }`}
          >
            {isLoading ? '처리중...' : '출금하기'}
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
                출금이 완료되었습니다
              </motion.p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};

export default WithdrawModal; 