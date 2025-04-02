import React from 'react';
import { motion } from 'framer-motion';
import { formatTeamCode } from '../constants/dummy';

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
  if (!isOpen) return null;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="absolute inset-0 bg-black/80 z-50 flex items-center justify-center"
      onClick={onClose}
    >
      <motion.div
        initial={{ scale: 0.95 }}
        animate={{ scale: 1 }}
        exit={{ scale: 0.95 }}
        className="bg-gray-800 rounded-xl w-[calc(100%-32px)]"
        onClick={e => e.stopPropagation()}
      >
        <div className="p-5">
          <h2 className="text-xl font-['Giants-Bold'] text-white mb-3">투표하기</h2>
          <p className="text-sm text-gray-400 mb-4">
            {requiredTokens} {formatTeamCode(team)}를 사용하여 투표하시겠습니까?
          </p>

          <div className="flex justify-end space-x-2">
            <button
              onClick={onClose}
              className="px-4 py-2 rounded-full bg-gray-700 text-white text-sm"
            >
              취소
            </button>
            <button
              onClick={() => {
                onConfirm();
                onClose();
              }}
              className="px-4 py-2 rounded-full bg-white text-black text-sm"
            >
              확인
            </button>
          </div>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default ConfirmModal;
