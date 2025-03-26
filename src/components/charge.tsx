import React from 'react';
import { motion } from 'framer-motion';
import { useStore } from '../stores/useStore';

interface ChargeModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const ChargeModal: React.FC<ChargeModalProps> = ({ isOpen, onClose }) => {
  const [amount, setAmount] = React.useState<number>(0);
  const [customAmount, setCustomAmount] = React.useState<string>('');
  const { addTransaction, updateWalletBalance } = useStore();
  
  // 원화를 BTC로 변환하는 함수 (1 BTC = 100원 기준)
  const convertToBTC = (won: number) => won / 100;
  
  // BTC 금액 포맷팅 함수
  const formatBTC = (btc: number) => btc.toFixed(2);
  
  const amounts = [10000, 50000, 100000, 500000];

  const handleCustomAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/[^0-9]/g, '');
    setCustomAmount(value);
    setAmount(Number(value));
  };

  const handleCharge = () => {
    if (amount <= 0) return;
    
    const btcAmount = convertToBTC(amount);
    updateWalletBalance(btcAmount);
    
    addTransaction({
      type: 'CHARGE',
      amount: amount,
      date: new Date().toLocaleString()
    });
    
    onClose();
  };

  if (!isOpen) return null;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.95 }}
        animate={{ scale: 1 }}
        exit={{ scale: 0.95 }}
        className="bg-white rounded-xl p-6 w-[90%] max-w-[324px]"
        onClick={e => e.stopPropagation()}
      >
        <h2 className="text-xl font-['Giants-Bold'] mb-6">BTC 충전하기</h2>
        
        {/* 금액 선택 버튼들 */}
        <div className="grid grid-cols-2 gap-3 mb-4">
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
                {formatBTC(convertToBTC(value))} BTC
              </p>
            </button>
          ))}
        </div>

        {/* 직접 입력 필드 */}
        <div className="mb-6">
          <div className="relative">
            <input
              type="text"
              value={customAmount}
              onChange={handleCustomAmountChange}
              placeholder="직접 입력하기"
              className="w-full p-4 rounded-xl bg-gray-100 text-black placeholder-gray-400
                focus:outline-none focus:ring-2 focus:ring-black"
            />
            <span className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-500">
              원
            </span>
          </div>
          {customAmount && (
            <p className="text-sm text-gray-500 mt-2 pl-2">
              ≈ {formatBTC(convertToBTC(Number(customAmount)))} BTC
            </p>
          )}
        </div>

        {/* 선택된 금액 표시 */}
        <div className="bg-gray-100 rounded-xl p-4 mb-6">
          <p className="text-sm text-gray-500 mb-1">충전 후 BTC</p>
          <p className="text-xl font-['Giants-Bold']">
            {formatBTC(convertToBTC(amount))} BTC
          </p>
          <p className="text-sm text-gray-500 mt-1">
            {amount.toLocaleString()}원
          </p>
        </div>

        {/* 버튼 영역 */}
        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 py-3 rounded-full bg-gray-100 text-black hover:bg-gray-200 transition-colors"
          >
            취소
          </button>
          <button
            onClick={handleCharge}
            disabled={amount === 0}
            className={`flex-1 py-3 rounded-full transition-colors
              ${amount > 0 
                ? 'bg-black text-white hover:bg-gray-800' 
                : 'bg-gray-200 text-gray-400 cursor-not-allowed'}`}
          >
            충전하기
          </button>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default ChargeModal;
