import React, { useState } from 'react';
import { Quiz, QuizHistory } from '../constants/dummy';
import { Problem as WebSocketProblem } from '../services/LiveVotingWebsocketService';

interface QuizModalProps {
  problem: WebSocketProblem;
  isActive: boolean;
  onAnswer: (answer: string) => void;
  currentTime: number;
  currentAnswer: string | null;
}

const QuizModal: React.FC<QuizModalProps> = ({
  problem,
  isActive,
  onAnswer,
  currentTime,
  currentAnswer,
}) => {
  // 옵션 개수에 따라 레이아웃 결정
  const isTwoOptions = problem.options.length === 2;

  return (
    <div className="backdrop-blur-md bg-black/30 rounded-xl p-3 border border-white/10 mb-4">
      {/* 문제 정보 */}
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-2">
          <span className="text-xs text-gray-300 bg-white/10 px-2 py-0.5 rounded-full">
            {problem.inning}
          </span>
          <span className="text-xs text-gray-300">
            {problem.attackTeam}
          </span>
        </div>
      </div>
      
      <div className="text-sm font-['Pretendard-Regular'] text-gray-300 mb-1">
        {problem.batterName} 타석
      </div>
      
      <div className="text-center mb-3">
        <div className="text-base font-['Pretendard-Regular'] text-white">
          {problem.description}
        </div>
      </div>

      {/* 타이머 - 15초 제한시간 */}
      <div className="text-center mb-3">
        <div className="text-lg font-['Pretendard-Regular'] text-red-400">
          {currentTime}초
        </div>
      </div>

      {/* 선택지 */}
      <div className={`${isTwoOptions ? 'grid grid-cols-2' : 'space-y-2'} gap-2`}>
        {problem.options.map((option) => (
          <button
            key={option}
            onClick={() => onAnswer(option)}
            disabled={!isActive || currentAnswer !== null}
            className={`w-full py-2 rounded-lg text-sm font-['Pretendard-Regular'] transition-all duration-200
              ${currentAnswer === option 
                ? 'bg-green-500/20 text-green-300' 
                : isActive && currentAnswer === null
                  ? 'bg-white/10 text-white hover:bg-white/20'
                  : 'bg-white/5 text-gray-400'
              }`}
          >
            {option}
          </button>
        ))}
      </div>
    </div>
  );
};

// 퀴즈 히스토리 아이템 컴포넌트
export const QuizHistoryItem: React.FC<{
  quiz: Quiz;
  history: QuizHistory;
}> = ({ quiz, history }) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const isCorrect = history.userAnswer === quiz.answer;
  
  return (
    <div className="bg-gray-800/50 rounded-lg p-4 mb-4">
      {/* 문제 내용 */}
      <div className="mb-3">
        <h3 className="text-sm text-gray-400">{quiz.question}</h3>
      </div>

      {/* 결과 헤더 */}
      <div className={`flex items-center justify-between ${isExpanded ? 'mb-4' : ''}`}>
        <div className="flex items-center gap-3">
          {history.userAnswer === null ? (
            <div className="text-yellow-500 text-2xl font-bold">시간 초과</div>
          ) : isCorrect ? (
            <div className="text-green-500 text-2xl font-bold">정답</div>
          ) : (
            <div className="text-red-500 text-2xl font-bold">오답</div>
          )}
          {!isExpanded && history.userAnswer !== null && (
            <div className="text-gray-400">
              {/* 선택한 답변 표시 */}
              <span>선택: {quiz.options[history.userAnswer]}</span>
              {!isCorrect && (
                <span className="ml-2">
                  (정답: {quiz.options[quiz.answer]})
                </span>
              )}
            </div>
          )}
        </div>
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="text-gray-400 hover:text-white text-sm flex items-center gap-1"
        >
          {isExpanded ? '접기' : '상세보기'}
          <svg 
            className={`w-4 h-4 transition-transform ${isExpanded ? 'rotate-180' : ''}`}
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </button>
      </div>

      {/* 상세 내용 - 펼쳤을 때만 표시 */}
      {isExpanded && (
        <div className="space-y-2">
          {quiz.options.map((option, index) => {
            const isSelected = history.userAnswer === index;
            const isCorrect = quiz.answer === index;
            
            return (
              <div key={index} className="relative">
                <div
                  className={`w-full h-10 rounded-lg overflow-hidden ${
                    isSelected 
                      ? isCorrect 
                        ? 'bg-green-600/20'
                        : 'bg-red-600/20'
                      : isCorrect
                        ? 'bg-green-600/20'
                        : 'bg-gray-700/20'
                  }`}
                >
                  <div
                    className={`h-full ${
                      isSelected 
                        ? isCorrect
                          ? 'bg-green-600/40'
                          : 'bg-red-600/40'
                        : isCorrect
                          ? 'bg-green-600/40'
                          : 'bg-gray-700/40'
                    }`}
                    style={{ width: isSelected || isCorrect ? '100%' : '0%' }}
                  />
                </div>
                <div className="absolute top-0 left-0 right-0 h-10 px-3 flex items-center justify-between">
                  <span className="text-sm">{option}</span>
                  {(isSelected || isCorrect) && (
                    <span className={`text-sm ${
                      isCorrect ? 'text-green-400' : 'text-red-400'
                    }`}>
                      {isCorrect ? '정답' : '오답'}
                    </span>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default QuizModal;