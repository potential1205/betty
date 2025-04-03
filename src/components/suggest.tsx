import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { formatTeamCode } from '../constants/dummy';
import { useStore } from '../stores/useStore';

interface SuggestModalProps {
  isOpen: boolean;
  onClose: () => void;
  team: string;
}

const SuggestModal: React.FC<SuggestModalProps> = ({ isOpen, onClose, team }) => {
  const addProposal = useStore(state => state.addProposal);
  const [title, setTitle] = React.useState('');
  const [description, setDescription] = React.useState('');

  // 고정 비용
  const SUGGEST_COST = 3;
  const TARGET_VOTES = 5;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // 7일 후 마감
    const deadline = new Date();
    deadline.setDate(deadline.getDate() + 7);

    addProposal({
      team,
      title,
      description,
      requiredTokens: SUGGEST_COST, // 고정 비용 3 SSAFY
      currentVotes: 0,
      targetVotes: TARGET_VOTES, // 고정 목표 5표
      deadline: deadline.toLocaleDateString(),
      status: 'pending'
    });

    // 폼 초기화
    setTitle('');
    setDescription('');
    onClose();
  };

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
        className="bg-gray-800 rounded-xl w-[calc(100%-32px)] max-h-[80%] overflow-y-auto no-scrollbar"
        onClick={e => e.stopPropagation()}
      >
        <div className="p-5">
          <h2 className="text-xl font-['Giants-Bold'] text-white mb-3">새로운 제안하기</h2>
          <p className="text-sm text-gray-400 mb-4">
            이 제안을 등록하기 위해 {SUGGEST_COST} SSAFY 코인이 필요합니다
          </p>

          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <input
                type="text"
                placeholder="제안 제목"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="w-full bg-gray-700 rounded-lg px-4 py-2.5 text-white placeholder-gray-400 text-sm"
                required
              />
            </div>
            <div>
              <textarea
                placeholder="제안 내용을 자세히 설명해주세요"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={4}
                className="w-full bg-gray-700 rounded-lg px-4 py-2.5 text-white placeholder-gray-400 resize-none text-sm"
                required
              />
            </div>
            <div className="flex justify-end space-x-2">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-2 rounded-full bg-gray-700 text-white text-sm"
              >
                취소
              </button>
              <button
                type="submit"
                className="px-4 py-2 rounded-full bg-white text-black text-sm"
              >
                제안하기
              </button>
            </div>
          </form>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default SuggestModal;