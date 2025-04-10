import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useStore } from '../stores/useStore';
import { removeBettyCoin } from '../apis/exchangeApi';
import { useWalletStore } from '../stores/walletStore';
import { ethers } from 'ethers';
import TokenABI from '../../abi/Token.json'
import { web3auth } from '../utils/web3auth';

interface WithdrawModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const WithdrawModal: React.FC<WithdrawModalProps> = ({ isOpen, onClose }) => {
  const { walletInfo } = useStore();
  const [amount, setAmount] = useState(''); // BET 단위
  const [customAmount, setCustomAmount] = useState(''); // 원 단위
  const [isLoading, setIsLoading] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  const options = [10000, 50000, 100000, 500000];
  const formatBET = (bet: number) => bet.toFixed(2);

  const handleWithdraw = async () => {
    if (!amount || isNaN(Number(amount))) return;
    
    try {
      setIsLoading(true);

      // 1. 개인키
      if (!web3auth.provider) throw new Error("지갑 연결이 필요합니다.");
      const provider = new ethers.BrowserProvider(web3auth.provider);
      const signer = await provider.getSigner();

      const betAddress = import.meta.env.VITE_BET_ADDRESS!;
      const exchangeAddress = import.meta.env.VITE_EXCHANGE_ADDRESS!;
      const betContract = new ethers.Contract(betAddress, TokenABI.abi, signer);
      const amountWei = ethers.parseEther(amount);

      // 2. approve
      const userAddress = await signer.getAddress();
      const allowance = await betContract.allowance(userAddress, exchangeAddress);
      if (allowance < amountWei) {
        const approveTx = await betContract.approve(exchangeAddress, amountWei);
        await approveTx.wait();
        console.log('approve 완료');
      } else {
        console.log('이미 승인됨');
      }

      // 3. 백엔드 출금 요청
      const res = await removeBettyCoin(Number(amount));

      if (res.success) {
        console.log('출금 성공', res.transactionId);
        setShowSuccess(true);
        setTimeout(() => {
          setShowSuccess(false);
          setAmount('');
          setCustomAmount('');
          onClose();
        }, 1500);
      } else {
        alert(`출금 실패: ${res.message}`);
      }
    } catch (error) {
      console.error('Withdrawal failed:', error);
      alert('서버 오류로 출금에 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCustomAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/[^0-9]/g, '');
    setCustomAmount(value);
    setAmount((Number(value) / 100).toFixed(2)); // BET 단위 환산
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
        <h1 className="text-lg font-['Giants-Bold'] text-gray-800">BET 출금하기</h1>
        <button
          onClick={onClose}
          className="w-5 h-5 flex items-center justify-center text-gray-400 hover:text-gray-600"
        >
          <svg className="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      {/* 메인 */}
      <div className="h-[calc(100%-56px)] flex flex-col justify-center">
        <div className="p-4 flex flex-col">

          {/* 현재 보유 BET */}
          <div className="bg-gradient-to-br from-black to-gray-800 rounded-xl p-3 shadow-lg mb-4">
            <p className="text-xs text-gray-400 mb-1">현재 보유 BET</p>
            <div className="flex items-baseline">
              <p className="text-xl font-['Giants-Bold'] text-white">
                {formatBET(walletInfo.totalBET || 0)}
              </p>
              <p className="text-sm text-gray-400 ml-1">BET</p>
            </div>
          </div>

          {/* 금액 선택 */}
          <div className="grid grid-cols-2 gap-3 mb-6">
            {options.map((won) => {
              const bet = won / 100;
              return (
                <button
                  key={won}
                  onClick={() => {
                    setCustomAmount(String(won));
                    setAmount(bet.toFixed(2));
                  }}
                  className={`p-4 rounded-xl text-center transition-colors ${
                    Number(customAmount) === won
                      ? 'bg-black text-white'
                      : 'bg-gray-100 text-black hover:bg-gray-200'
                  }`}
                >
                  <p className="text-sm mb-1">출금금액</p>
                  <p className="text-lg font-['Giants-Bold']">{won.toLocaleString()}원</p>
                  <p className="text-xs text-gray-500 mt-1">{bet.toFixed(2)} BET</p>
                </button>
              );
            })}
          </div>

          {/* 직접 입력 */}
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
                  <span className="text-gray-800 font-['Giants-Bold']">
                    {(Number(customAmount) / 100).toFixed(2)}
                  </span>
                  <span className="text-gray-500">BET</span>
                </div>
              </div>
            )}
          </div>

          {/* 버튼 */}
          <button
            onClick={handleWithdraw}
            disabled={
              isLoading ||
              !amount ||
              isNaN(Number(amount)) ||
              Number(amount) <= 0 ||
              Number(amount) > (walletInfo.totalBET || 0)
            }
            className={`w-full py-4 rounded-xl text-white text-base font-['Giants-Bold'] transition-colors ${
              amount && Number(amount) > 0 && Number(amount) <= (walletInfo.totalBET || 0)
                ? 'bg-black hover:bg-gray-800'
                : 'bg-gray-300 cursor-not-allowed'
            }`}
          >
            {isLoading ? '처리중...' : '출금하기'}
          </button>
        </div>
      </div>

      {/* 성공 애니메이션 */}
      <AnimatePresence>
        {showSuccess && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-white flex items-center justify-center pointer-events-none z-[60]"
          >
            <div className="flex flex-col items-center">
              <svg className="w-16 h-16 sm:w-24 sm:h-24 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
