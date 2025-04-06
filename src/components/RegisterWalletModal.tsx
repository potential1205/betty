import React, { useState } from 'react';
import { registerWallet } from '../apis/axios';
import { useStore } from '../stores/useStore';

interface RegisterWalletModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const RegisterWalletModal: React.FC<RegisterWalletModalProps> = ({ isOpen, onClose }) => {
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { updateWalletInfo } = useStore();

  if (!isOpen) return null;

  const handleRegister = async () => {
    setError('');
    setIsLoading(true);

    try {
      const response = await registerWallet();
      updateWalletInfo({
        address: response.address,
        totalBTC: 0,
        transactions: [],
        tokens: []
      });
      onClose();
    } catch (error: any) {
      setError('지갑 등록 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl p-6 w-[90%] max-w-md">
        <h2 className="text-xl font-['Giants-Bold'] mb-4">지갑 등록</h2>
        <div className="mb-6">
          <p className="text-gray-600">
            새로운 지갑을 등록하시겠습니까?
          </p>
          {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
        </div>
        <div className="flex gap-2">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 py-3 rounded-xl text-gray-500 bg-gray-100 hover:bg-gray-200 transition-colors font-['Giants-Bold']"
            disabled={isLoading}
          >
            취소
          </button>
          <button
            onClick={handleRegister}
            className="flex-1 py-3 rounded-xl text-white bg-black hover:bg-gray-800 transition-colors font-['Giants-Bold']"
            disabled={isLoading}
          >
            {isLoading ? '등록 중...' : '등록하기'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default RegisterWalletModal; 