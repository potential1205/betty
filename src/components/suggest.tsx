import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { formatTeamName, formatTeamCode } from '../constants/dummy';

interface SuggestModalProps {
  isOpen: boolean;
  onClose: () => void;
  team: string;
  onSubmit: (title: string, content: string, targetCount: number) => Promise<void>;
}

const SuggestModal: React.FC<SuggestModalProps> = ({ 
  isOpen, 
  onClose, 
  team, 
  onSubmit 
}) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async () => {
    // 유효성 검사
    if (!title.trim()) {
      setError('제안 제목을 입력해주세요.');
      return;
    }
    
    if (!content.trim()) {
      setError('제안 내용을 입력해주세요.');
      return;
    }
    
    try {
      setIsSubmitting(true);
      setError('');
      // 목표 투표수를 5로 고정
      await onSubmit(title, content, 5);
      
      // 성공적으로 제출 후 폼 초기화 및 모달 닫기
      setTitle('');
      setContent('');
      onClose();
    } catch (error) {
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError('제안 등록 중 오류가 발생했습니다.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  // 모달이 닫힐 때 폼 초기화
  const handleClose = () => {
    setTitle('');
    setContent('');
    setError('');
    onClose();
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
            onClick={handleClose}
          />
          
          {/* 모달 컨텐츠 */}
          <motion.div
            initial={{ scale: 0.9, y: 20 }}
            animate={{ scale: 1, y: 0 }}
            exit={{ scale: 0.9, y: 20 }}
            className="relative mx-4 bg-gray-900 rounded-xl p-6 w-full max-w-[328px] z-10"
          >
            <h2 className="text-xl font-['Giants-Bold'] text-white mb-6">
              {formatTeamName(team)} 팀에 제안하기
            </h2>
            
            {/* 에러 메시지 */}
            {error && (
              <div className="mb-4 p-3 bg-red-900/40 border border-red-500 rounded-md text-red-400 text-sm">
                {error}
              </div>
            )}
            
            {/* 폼 필드 */}
            <div className="mb-4">
              <label className="block text-gray-400 text-sm mb-2">제안 제목</label>
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="w-full bg-gray-800 border border-gray-700 rounded-md p-3 text-white focus:outline-none focus:ring-1 focus:ring-blue-500"
                placeholder="제안 제목을 입력하세요"
                maxLength={50}
              />
            </div>
            
            <div className="mb-4">
              <label className="block text-gray-400 text-sm mb-2">제안 내용</label>
              <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                className="w-full bg-gray-800 border border-gray-700 rounded-md p-3 text-white focus:outline-none focus:ring-1 focus:ring-blue-500 min-h-[120px]"
                placeholder="구체적인 제안 내용을 작성해주세요"
                maxLength={500}
              />
            </div>
            
            <div className="mb-6">
              <label className="block text-gray-400 text-sm mb-2">목표 투표 수</label>
              <div className="w-full bg-gray-800 py-2 px-3 rounded-md border border-gray-700 text-white">
                5
              </div>
            </div>
            
            <div className="text-gray-400 text-sm mb-6">
              <p>- 제안 등록에는 3이 사용됩니다.</p>
              <p>- 목표 투표수에 도달하면 구단에 전달됩니다.</p>
              <p>- 14일 이내에 목표에 도달하지 못하면 자동 소멸됩니다.</p>
            </div>
            
            <div className="flex space-x-3">
              <button
                onClick={handleClose}
                className="flex-1 py-3 rounded-full border border-gray-700 text-white hover:bg-gray-800 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleSubmit}
                disabled={isSubmitting}
                className={`flex-1 py-3 rounded-full font-['Pretendard-Regular'] transition-colors
                  ${isSubmitting
                    ? 'bg-gray-600 text-gray-400 cursor-not-allowed'
                    : 'bg-white text-black hover:bg-gray-200'}`}
              >
                {isSubmitting ? '제출 중...' : '제안하기'}
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default SuggestModal;