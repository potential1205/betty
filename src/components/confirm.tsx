import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamName, formatTeamCode } from '../constants/dummy';

interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  team: string;
  requiredTokens: number;
}

const ConfirmModal: React.FC<ConfirmModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  team,
  requiredTokens
}) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleConfirm = async () => {
    try {
      setIsSubmitting(true);
      setError('');
      await onConfirm();
      onClose();
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError('투표 중 오류가 발생했습니다.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-50 flex items-center justify-center"
          style={{
            maxWidth: '360px',
            maxHeight: '743px',
            margin: 'auto',
            left: '0',
            right: '0'
          }}
        >
          {/* 배경 오버레이 */}
          <div 
            className="fixed inset-0 bg-black/60"
            onClick={onClose}
          />
          
          {/* 모달 컨텐츠 */}
          <motion.div
            initial={{ scale: 0.9, y: 20 }}
            animate={{ scale: 1, y: 0 }}
            exit={{ scale: 0.9, y: 20 }}
            className="relative mx-4 bg-gray-900 rounded-xl p-6 w-full max-w-[328px] z-10"
          >
            <h2 className="text-xl font-['Giants-Bold'] text-white mb-4 text-center">
              제안에 투표하기
            </h2>
            
            {/* 에러 메시지 */}
            {error && (
              <div className="mb-4 p-3 bg-red-900/40 border border-red-500 rounded-md text-red-400 text-sm">
                {error}
              </div>
            )}
            
            <div className="text-center mb-6">
              <p className="text-white mb-4">
                이 제안에 {requiredTokens} {formatTeamCode(team)}을 사용하여 투표하시겠습니까?
              </p>
              <p className="text-gray-400 text-sm">
                - 투표한 토큰은 구단에서 거절시 반환됩니다.<br />
              </p>
            </div>
            
            <div className="flex space-x-3">
              <button
                onClick={onClose}
                className="flex-1 py-3 rounded-full border border-gray-700 text-white hover:bg-gray-800 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleConfirm}
                disabled={isSubmitting}
                className={`flex-1 py-3 rounded-full font-['Pretendard-Regular'] transition-colors
                  ${isSubmitting
                    ? 'bg-gray-600 text-gray-400 cursor-not-allowed'
                    : 'bg-white text-black hover:bg-gray-200'}`}
              >
                {isSubmitting ? '처리 중...' : '투표하기'}
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default ConfirmModal;